import { useState, useEffect, useRef, useCallback } from "react";
import { useTheme } from "../context/ThemeContext";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { FcGoogle } from "react-icons/fc";
import { useToast } from "../context/ToastContext";
import authService from "../services/authService";

// ─ OTP Timer Hook
const useOtpTimer = (seconds = 300) => {
  const [timeLeft, setTimeLeft] = useState(seconds);
  const [isExpired, setIsExpired] = useState(false);
  const intervalRef = useRef(null);

  const start = useCallback(() => {
    setTimeLeft(seconds);
    setIsExpired(false);
    clearInterval(intervalRef.current);
    intervalRef.current = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(intervalRef.current);
          setIsExpired(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  }, [seconds]);

  const reset = useCallback(() => {
    clearInterval(intervalRef.current);
    setTimeLeft(seconds);
    setIsExpired(false);
  }, [seconds]);

  useEffect(() => () => clearInterval(intervalRef.current), []);

  const minutes = String(Math.floor(timeLeft / 60)).padStart(2, "0");
  const secs = String(timeLeft % 60).padStart(2, "0");

  return { timeLeft, isExpired, start, reset, display: `${minutes}:${secs}` };
};

// ─ 6-Box OTP Input ─
const OtpInput = ({ value, onChange, disabled }) => {
  const inputsRef = useRef([]);
  const digits = value.split("");

  const handleKey = (e, idx) => {
    if (e.key === "Backspace") {
      e.preventDefault();
      const next = [...digits];
      if (next[idx]) {
        next[idx] = "";
        onChange(next.join(""));
      } else if (idx > 0) {
        next[idx - 1] = "";
        onChange(next.join(""));
        inputsRef.current[idx - 1]?.focus();
      }
    }
    // Enter bubbles up to parent <form> naturally — no extra handling needed
  };

  const handleChange = (e, idx) => {
    const val = e.target.value.replace(/\D/g, "").slice(-1);
    if (!val) return;
    const next = [...digits];
    next[idx] = val;
    onChange(next.join(""));
    if (idx < 5) inputsRef.current[idx + 1]?.focus();
  };

  const handlePaste = (e) => {
    e.preventDefault();
    const pasted = e.clipboardData
      .getData("text")
      .replace(/\D/g, "")
      .slice(0, 6);
    const padded = pasted.padEnd(6, "").slice(0, 6);
    onChange(padded);
    const focusIdx = Math.min(pasted.length, 5);
    inputsRef.current[focusIdx]?.focus();
  };

  return (
    <div className="flex gap-2 justify-center my-4">
      {Array.from({ length: 6 }).map((_, idx) => (
        <input
          key={idx}
          ref={(el) => (inputsRef.current[idx] = el)}
          type="text"
          inputMode="numeric"
          maxLength={1}
          value={digits[idx] || ""}
          disabled={disabled}
          onChange={(e) => handleChange(e, idx)}
          onKeyDown={(e) => handleKey(e, idx)}
          onPaste={handlePaste}
          onFocus={(e) => e.target.select()}
          className="w-11 h-12 text-center text-lg font-bold rounded-lg border-2 outline-none transition-all duration-150 otp-box"
        />
      ))}
    </div>
  );
};

// ─ Timer Ring
const TimerRing = ({ timeLeft, total, display, isExpired }) => {
  const r = 22;
  const circ = 2 * Math.PI * r;
  const progress = isExpired ? 0 : (timeLeft / total) * circ;
  const color = isExpired ? "#ef4444" : timeLeft < 60 ? "#f97316" : "#3b82f6";

  return (
    <div className="flex flex-col items-center gap-1 my-3">
      <div className="relative w-14 h-14">
        <svg className="w-full h-full -rotate-90" viewBox="0 0 56 56">
          <circle
            cx="28"
            cy="28"
            r={r}
            fill="none"
            strokeWidth="3"
            className="timer-track"
          />
          <circle
            cx="28"
            cy="28"
            r={r}
            fill="none"
            strokeWidth="3"
            stroke={color}
            strokeDasharray={circ}
            strokeDashoffset={circ - progress}
            strokeLinecap="round"
            style={{ transition: "stroke-dashoffset 1s linear, stroke 0.5s" }}
          />
        </svg>
        <span
          className="absolute inset-0 flex items-center justify-center text-xs font-bold"
          style={{ color }}
        >
          {display}
        </span>
      </div>
      <p
        className={`text-xs font-medium ${
          isExpired ? "text-red-500" : "text-gray-400"
        }`}
      >
        {isExpired ? "OTP expired" : "OTP expires in"}
      </p>
    </div>
  );
};

// ─ Reusable Input Field
const Field = ({
  type = "text",
  name,
  placeholder,
  value,
  onChange,
  disabled,
  error,
  autoComplete,
}) => (
  <div className="mb-4">
    <input
      type={type}
      name={name}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      disabled={disabled}
      autoComplete={autoComplete}
      className={`
        auth-input w-full px-4 py-3 rounded-xl text-sm outline-none
        transition-all duration-200 border-2
        ${error ? "border-red-500" : ""}
        ${disabled ? "opacity-50 cursor-not-allowed" : ""}
      `}
    />
    {error && <p className="text-red-500 text-xs mt-1 ml-1">{error}</p>}
  </div>
);

// ─ Spinner ─
const Spinner = () => <span className="spinner" />;

// ─ Main Auth Component ─
const Auth = () => {
  const { theme } = useTheme();
  const { login, signup, verifyOtp } = useAuth();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const isDark = theme === "dark";

  const [step, setStep] = useState("LOGIN"); // LOGIN | SIGNUP | OTP_LOGIN | OTP_SIGNUP
  const [resetStep, setResetStep] = useState("NONE"); // NONE | REQUEST | VERIFY
  const [preAuthToken, setPreAuthToken] = useState("");
  const [signupEmail, setSignupEmail] = useState("");
  const [resetEmail, setResetEmail] = useState("");
  const [resetOtp, setResetOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [otp, setOtp] = useState("");
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState("");
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const timer = useOtpTimer(300);

  const handleChange = (e) => {
    setFormData((p) => ({ ...p, [e.target.name]: e.target.value }));
    setErrors((p) => ({ ...p, [e.target.name]: "" }));
    setFormError("");
  };

  // Guard: if OTP_LOGIN but no preAuthToken somehow, go back
  useEffect(() => {
    if (step === "OTP_LOGIN" && !preAuthToken) setStep("LOGIN");
  }, [step, preAuthToken]);

  // Start timer when entering OTP steps
  useEffect(() => {
    if (step === "OTP_LOGIN" || step === "OTP_SIGNUP") {
      setOtp("");
      timer.start();
    }
  }, [step]);

  const switchStep = (to) => {
    setStep(to);
    setErrors({});
    setFormError("");
    setOtp("");
  };

  //  Login ─
  const handleLogin = async (e) => {
    e?.preventDefault();
    setFormError("");
    setLoading(true);
    try {
      const res = await login(formData.email, formData.password);
      setPreAuthToken(res.preAuthToken);
      setStep("OTP_LOGIN");
      showToast("OTP sent to your email 📩", "success");
    } catch (err) {
      setFormError(err?.response?.data?.message || "Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyLoginOtp = async (e) => {
    e?.preventDefault();
    if (timer.isExpired) {
      setFormError("OTP expired. Please resend.");
      return;
    }
    setLoading(true);
    try {
      await verifyOtp({
        email: formData.email,
        code: otp,
        type: "LOGIN",
        preAuthToken,
      });
      showToast("Login successful 🎉", "success");
      navigate("/");
    } catch (err) {
      setFormError(err?.response?.data?.message || "Invalid OTP");
    } finally {
      setLoading(false);
    }
  };

  const handleResendLoginOtp = async () => {
    setFormError("");
    setOtp("");
    setLoading(true);
    try {
      const res = await login(formData.email, formData.password);
      setPreAuthToken(res.preAuthToken);
      timer.start();
      showToast("OTP resent 📩", "success");
    } catch {
      setFormError("Failed to resend OTP");
    } finally {
      setLoading(false);
    }
  };

  //  Signup
  const handleSignup = async (e) => {
    e?.preventDefault();
    setErrors({});
    setFormError("");
    if (formData.password !== formData.confirmPassword) {
      setErrors({ confirmPassword: "Passwords do not match" });
      return;
    }
    setLoading(true);
    try {
      await signup({
        userName: formData.name,
        email: formData.email,
        password: formData.password,
      });
      setSignupEmail(formData.email);
      setStep("OTP_SIGNUP");
      showToast("OTP sent to your email 📩", "success");
    } catch (err) {
      const data = err?.response?.data;
      if (data && typeof data === "object") setErrors(data);
      else setFormError("Signup failed");
    } finally {
      setLoading(false);
    }
  };

  const handleVerifySignupOtp = async (e) => {
    e?.preventDefault();
    if (timer.isExpired) {
      setFormError("OTP expired. Please resend.");
      return;
    }
    setLoading(true);
    try {
      await authService.verifyOtp({
        email: signupEmail,
        code: otp,
        type: "REGISTRATION",
      });
      showToast("Account verified ✅ Please login", "success");
      setStep("LOGIN");
      setOtp("");
      setFormData({ name: "", email: "", password: "", confirmPassword: "" });
    } catch (err) {
      setFormError(err?.response?.data?.message || "Invalid OTP");
    } finally {
      setLoading(false);
    }
  };

  const handleResendSignupOtp = async () => {
    setFormError("");
    setOtp("");
    setLoading(true);
    try {
      await signup({
        userName: formData.name,
        email: signupEmail,
        password: formData.password,
      });
      timer.start();
      showToast("OTP resent 📩", "success");
    } catch {
      setFormError("Failed to resend OTP");
    } finally {
      setLoading(false);
    }
  };

  //  Password Reset
  const handleRequestResetOtp = async (e) => {
    e?.preventDefault();
    setFormError("");
    setLoading(true);
    try {
      await authService.requestPasswordOtp(formData.email);
      setResetEmail(formData.email);
      setResetStep("VERIFY");
      showToast("Reset OTP sent 📩", "success");
    } catch (err) {
      setFormError(err?.response?.data?.message || "Failed to send OTP");
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyResetOtp = async (e) => {
    e?.preventDefault();
    setFormError("");
    setLoading(true);
    try {
      await authService.verifyPasswordOtp({
        email: resetEmail,
        otp: resetOtp,
        newPassword,
      });
      showToast("Password reset successful ✅", "success");
      setResetStep("NONE");
      setStep("LOGIN");
      setResetOtp("");
      setNewPassword("");
    } catch (err) {
      setFormError(err?.response?.data?.message || "Invalid OTP");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = `${import.meta.env.VITE_API_BASE_URL.replace(
      "/salezone/ecom",
      ""
    )}/oauth2/authorization/google`;
  };

  //  Derived title / subtitle
  const title = () => {
    if (resetStep === "REQUEST") return "Reset Password";
    if (resetStep === "VERIFY") return "New Password";
    if (step === "OTP_LOGIN" || step === "OTP_SIGNUP") return "Verify Email";
    if (step === "SIGNUP") return "Create Account";
    return "Welcome Back";
  };

  const subtitle = () => {
    if (step === "OTP_LOGIN")
      return `Enter the 6-digit code sent to ${formData.email}`;
    if (step === "OTP_SIGNUP")
      return `Enter the 6-digit code sent to ${signupEmail}`;
    if (step === "SIGNUP") return "Join us today";
    if (resetStep === "REQUEST") return "We'll send an OTP to your email";
    if (resetStep === "VERIFY") return `OTP sent to ${resetEmail}`;
    return "Sign in to your account";
  };

  return (
    <>
      <style>{`
        :root {
          --auth-bg-light:       #f8fafc;
          --auth-card-light:     #ffffff;
          --auth-border-light:   #e2e8f0;
          --auth-text-light:     #0f172a;
          --auth-sub-light:      #64748b;
          --auth-input-bg-light: #f8fafc;

          --auth-bg-dark:        #09090b;
          --auth-card-dark:      #0f0f0f;
          --auth-border-dark:    #27272a;
          --auth-text-dark:      #fafafa;
          --auth-sub-dark:       #71717a;
          --auth-input-bg-dark:  #18181b;
        }

        .auth-wrap {
          min-height: 100vh;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 1.5rem;
          background: ${
            isDark ? "var(--auth-bg-dark)" : "var(--auth-bg-light)"
          };
          font-family: 'DM Sans', sans-serif;
        }

        .auth-card {
          width: 100%;
          max-width: 420px;
          background: ${
            isDark ? "var(--auth-card-dark)" : "var(--auth-card-light)"
          };
          border: 1.5px solid ${
            isDark ? "var(--auth-border-dark)" : "var(--auth-border-light)"
          };
          border-radius: 20px;
          padding: 2.5rem 2rem;
          box-shadow: ${
            isDark
              ? "0 0 0 1px #27272a, 0 20px 60px rgba(0,0,0,0.5)"
              : "0 4px 6px -1px rgba(0,0,0,0.07), 0 20px 60px rgba(0,0,0,0.06)"
          };
          animation: cardIn 0.35s cubic-bezier(0.16,1,0.3,1);
        }

        @keyframes cardIn {
          from { opacity: 0; transform: translateY(16px) scale(0.98); }
          to   { opacity: 1; transform: translateY(0)    scale(1);    }
        }

        .auth-title {
          font-size: 1.6rem;
          font-weight: 700;
          letter-spacing: -0.03em;
          color: ${isDark ? "var(--auth-text-dark)" : "var(--auth-text-light)"};
          margin-bottom: 0.25rem;
        }

        .auth-subtitle {
          font-size: 0.82rem;
          color: ${isDark ? "var(--auth-sub-dark)" : "var(--auth-sub-light)"};
          margin-bottom: 1.75rem;
          word-break: break-word;
        }

        .auth-input {
          background:   ${
            isDark ? "var(--auth-input-bg-dark)" : "var(--auth-input-bg-light)"
          };
          border-color: ${
            isDark ? "var(--auth-border-dark)" : "var(--auth-border-light)"
          };
          color:        ${
            isDark ? "var(--auth-text-dark)" : "var(--auth-text-light)"
          };
          font-size: 0.875rem;
          font-family: 'DM Sans', sans-serif;
        }

        .auth-input::placeholder { color: ${isDark ? "#52525b" : "#94a3b8"}; }

        .auth-input:focus {
          border-color: #3b82f6;
          background: ${isDark ? "#1c1c1e" : "#ffffff"};
          box-shadow: 0 0 0 3px rgba(59,130,246,0.12);
        }

        .auth-btn-primary {
          width: 100%;
          padding: 0.75rem;
          border-radius: 12px;
          background: #2563eb;
          color: #fff;
          font-size: 0.9rem;
          font-weight: 600;
          letter-spacing: -0.01em;
          border: none;
          cursor: pointer;
          transition: all 0.2s;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 0.5rem;
          margin-top: 0.5rem;
          font-family: 'DM Sans', sans-serif;
        }

        .auth-btn-primary:hover:not(:disabled) {
          background: #1d4ed8;
          transform: translateY(-1px);
          box-shadow: 0 4px 16px rgba(37,99,235,0.35);
        }

        .auth-btn-primary:active:not(:disabled) { transform: translateY(0); }
        .auth-btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

        .auth-btn-ghost {
          background: transparent;
          border: 1.5px solid ${
            isDark ? "var(--auth-border-dark)" : "var(--auth-border-light)"
          };
          color: ${isDark ? "var(--auth-text-dark)" : "var(--auth-text-light)"};
          width: 100%;
          padding: 0.7rem;
          border-radius: 12px;
          font-size: 0.875rem;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.2s;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 0.5rem;
          margin-top: 0.75rem;
          font-family: 'DM Sans', sans-serif;
        }

        .auth-btn-ghost:hover {
          background: ${isDark ? "#18181b" : "#f1f5f9"};
          border-color: ${isDark ? "#3f3f46" : "#cbd5e1"};
        }

        .auth-link {
          background: none;
          border: none;
          color: #3b82f6;
          font-size: 0.82rem;
          font-weight: 500;
          cursor: pointer;
          padding: 0;
          transition: color 0.15s;
          font-family: 'DM Sans', sans-serif;
        }

        .auth-link:hover  { color: #1d4ed8; text-decoration: underline; }
        .auth-link:disabled { opacity: 0.5; cursor: not-allowed; }

        .auth-divider {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          margin: 1.25rem 0;
          color: ${isDark ? "#52525b" : "#94a3b8"};
          font-size: 0.75rem;
          font-weight: 500;
        }

        .auth-divider::before,
        .auth-divider::after {
          content: '';
          flex: 1;
          height: 1px;
          background: ${
            isDark ? "var(--auth-border-dark)" : "var(--auth-border-light)"
          };
        }

        .otp-box {
          background:   ${
            isDark ? "var(--auth-input-bg-dark)" : "var(--auth-input-bg-light)"
          };
          border-color: ${
            isDark ? "var(--auth-border-dark)" : "var(--auth-border-light)"
          };
          color:        ${
            isDark ? "var(--auth-text-dark)" : "var(--auth-text-light)"
          };
          caret-color: #3b82f6;
          font-family: 'DM Sans', sans-serif;
        }

        .otp-box:focus {
          border-color: #3b82f6 !important;
          box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
          background: ${isDark ? "#1c1c1e" : "#ffffff"};
        }

        .otp-box:not(:placeholder-shown):not(:focus) {
          border-color: ${isDark ? "#3b82f6aa" : "#93c5fd"};
          background:   ${isDark ? "#1e3a5f22" : "#eff6ff"};
        }

        .timer-track { stroke: ${isDark ? "#27272a" : "#e2e8f0"}; }

        .form-error {
          background: ${isDark ? "#450a0a" : "#fef2f2"};
          border: 1px solid ${isDark ? "#7f1d1d" : "#fecaca"};
          color: ${isDark ? "#fca5a5" : "#dc2626"};
          border-radius: 10px;
          padding: 0.6rem 0.85rem;
          font-size: 0.8rem;
          margin-top: 0.75rem;
          text-align: center;
        }

        .auth-back-btn {
          display: flex;
          align-items: center;
          gap: 0.35rem;
          background: none;
          border: none;
          color: ${isDark ? "var(--auth-sub-dark)" : "var(--auth-sub-light)"};
          font-size: 0.8rem;
          cursor: pointer;
          padding: 0;
          margin-bottom: 1.25rem;
          transition: color 0.15s;
          font-family: 'DM Sans', sans-serif;
        }

        .auth-back-btn:hover { color: #3b82f6; }

        .spinner {
          width: 16px; height: 16px;
          border: 2px solid rgba(255,255,255,0.3);
          border-top-color: #fff;
          border-radius: 50%;
          animation: spin 0.7s linear infinite;
          display: inline-block;
        }

        @keyframes spin { to { transform: rotate(360deg); } }

        .step-fade { animation: stepFade 0.25s ease; }

        @keyframes stepFade {
          from { opacity: 0; transform: translateX(8px); }
          to   { opacity: 1; transform: translateX(0);   }
        }

        .resend-row {
          text-align: center;
          margin-top: 1rem;
          font-size: 0.8rem;
          color: ${isDark ? "#71717a" : "#64748b"};
        }

        .switch-row {
          text-align: center;
          margin-top: 1.25rem;
          font-size: 0.82rem;
          color: ${isDark ? "#71717a" : "#64748b"};
        }
      `}</style>

      <link
        href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap"
        rel="stylesheet"
      />

      <div className="auth-wrap">
        <div className="auth-card">
          {/*  Header (re-animates on step change)  */}
          <div className="step-fade" key={step + resetStep}>
            <h2 className="auth-title">{title()}</h2>
            <p className="auth-subtitle">{subtitle()}</p>
          </div>

          {/* 
              RESET — REQUEST OTP
              Wrapped in <form> so Enter key works
           */}
          {resetStep === "REQUEST" && (
            <div className="step-fade">
              <button
                className="auth-back-btn"
                onClick={() => {
                  setResetStep("NONE");
                  setFormError("");
                }}
              >
                ← Back to login
              </button>

              <form onSubmit={handleRequestResetOtp}>
                <Field
                  type="email"
                  name="email"
                  placeholder="Your email address"
                  value={formData.email}
                  onChange={handleChange}
                  autoComplete="email"
                />
                <button
                  type="submit"
                  className="auth-btn-primary"
                  disabled={loading}
                >
                  {loading ? <Spinner /> : "Send OTP"}
                </button>
              </form>

              {formError && <div className="form-error">{formError}</div>}
            </div>
          )}

          {/* 
              RESET — VERIFY OTP + NEW PASSWORD
              Wrapped in <form> so Enter key works
           */}
          {resetStep === "VERIFY" && (
            <div className="step-fade">
              <button
                className="auth-back-btn"
                onClick={() => {
                  setResetStep("REQUEST");
                  setFormError("");
                }}
              >
                ← Back
              </button>

              <form onSubmit={handleVerifyResetOtp}>
                <Field
                  type="text"
                  placeholder="Enter OTP"
                  value={resetOtp}
                  onChange={(e) => {
                    setResetOtp(e.target.value);
                    setFormError("");
                  }}
                  autoComplete="one-time-code"
                />
                <Field
                  type="password"
                  placeholder="New password"
                  value={newPassword}
                  onChange={(e) => {
                    setNewPassword(e.target.value);
                    setFormError("");
                  }}
                  autoComplete="new-password"
                />
                <button
                  type="submit"
                  className="auth-btn-primary"
                  disabled={loading}
                >
                  {loading ? <Spinner /> : "Reset Password"}
                </button>
              </form>

              {formError && <div className="form-error">{formError}</div>}
            </div>
          )}

          {/* 
              OTP VERIFICATION — LOGIN + SIGNUP
              Wrapped in <form> so Enter from OTP
              boxes triggers submit automatically
           */}
          {resetStep === "NONE" &&
            (step === "OTP_LOGIN" || step === "OTP_SIGNUP") && (
              <div className="step-fade">
                <button
                  className="auth-back-btn"
                  onClick={() => {
                    switchStep(step === "OTP_LOGIN" ? "LOGIN" : "SIGNUP");
                    timer.reset();
                  }}
                >
                  ← Back
                </button>

                <TimerRing
                  timeLeft={timer.timeLeft}
                  total={300}
                  display={timer.display}
                  isExpired={timer.isExpired}
                />

                <form
                  onSubmit={
                    step === "OTP_LOGIN"
                      ? handleVerifyLoginOtp
                      : handleVerifySignupOtp
                  }
                >
                  <OtpInput
                    value={otp}
                    onChange={setOtp}
                    disabled={timer.isExpired}
                  />

                  {timer.isExpired && (
                    <div className="form-error">
                      OTP has expired. Please request a new one.
                    </div>
                  )}

                  <button
                    type="submit"
                    className="auth-btn-primary"
                    disabled={loading || otp.length < 6 || timer.isExpired}
                  >
                    {loading ? <Spinner /> : "Verify OTP"}
                  </button>
                </form>

                <div className="resend-row">
                  Didn't receive the code?{" "}
                  <button
                    className="auth-link"
                    onClick={
                      step === "OTP_LOGIN"
                        ? handleResendLoginOtp
                        : handleResendSignupOtp
                    }
                    disabled={loading}
                  >
                    Resend OTP
                  </button>
                </div>

                {formError && <div className="form-error">{formError}</div>}
              </div>
            )}

          {/* 
              LOGIN / SIGNUP FORMS
              Already had <form> — Enter worked before
           */}
          {resetStep === "NONE" && (step === "LOGIN" || step === "SIGNUP") && (
            <div className="step-fade">
              <form
                onSubmit={step === "LOGIN" ? handleLogin : handleSignup}
                autoComplete="on"
              >
                {step === "SIGNUP" && (
                  <Field
                    type="text"
                    name="name"
                    placeholder="Full name"
                    value={formData.name}
                    onChange={handleChange}
                    error={errors.userName}
                    autoComplete="name"
                  />
                )}

                <Field
                  type="email"
                  name="email"
                  placeholder="Email address"
                  value={formData.email}
                  onChange={handleChange}
                  error={errors.email}
                  autoComplete="email"
                />

                <Field
                  type="password"
                  name="password"
                  placeholder="Password"
                  value={formData.password}
                  onChange={handleChange}
                  error={errors.password}
                  autoComplete={
                    step === "LOGIN" ? "current-password" : "new-password"
                  }
                />

                {step === "SIGNUP" && (
                  <Field
                    type="password"
                    name="confirmPassword"
                    placeholder="Confirm password"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    error={errors.confirmPassword}
                    autoComplete="new-password"
                  />
                )}

                {step === "LOGIN" && (
                  <div style={{ textAlign: "right", marginBottom: "0.5rem" }}>
                    <button
                      type="button"
                      className="auth-link"
                      onClick={() => setResetStep("REQUEST")}
                    >
                      Forgot password?
                    </button>
                  </div>
                )}

                <button
                  type="submit"
                  className="auth-btn-primary"
                  disabled={loading}
                >
                  {loading ? (
                    <Spinner />
                  ) : step === "LOGIN" ? (
                    "Continue"
                  ) : (
                    "Create Account"
                  )}
                </button>

                {formError && <div className="form-error">{formError}</div>}
              </form>

              <div className="switch-row">
                {step === "LOGIN"
                  ? "Don't have an account? "
                  : "Already have an account? "}
                <button
                  type="button"
                  className="auth-link"
                  onClick={() =>
                    switchStep(step === "LOGIN" ? "SIGNUP" : "LOGIN")
                  }
                >
                  {step === "LOGIN" ? "Sign up" : "Log in"}
                </button>
              </div>

              <div className="auth-divider">or</div>

              <button
                type="button"
                className="auth-btn-ghost"
                onClick={handleGoogleLogin}
              >
                <FcGoogle size={18} />
                Continue with Google
              </button>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Auth;
