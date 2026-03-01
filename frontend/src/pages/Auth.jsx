import { useState } from "react";
import { useTheme } from "../context/ThemeContext";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { FcGoogle } from "react-icons/fc";
import { useToast } from "../context/ToastContext";

const Auth = () => {
  const { theme } = useTheme();
  const { login, signup } = useAuth();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [isLogin, setIsLogin] = useState(true);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState("");

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });

    // Clear field error when typing
    setErrors((prev) => ({ ...prev, [e.target.name]: "" }));
    setFormError("");
  };

  // ---------------- LOGIN ----------------
  const handleLogin = async (e) => {
    e.preventDefault();
    setFormError("");

    try {
      await login(formData.email, formData.password);
      showToast("Login successful 🎉", "success");
      navigate("/");
    } catch (err) {
      const message =
        err?.response?.data?.message || "Invalid email or password";

      setFormError(message);
    }
  };

  // ---------------- SIGNUP ----------------
  const handleSignup = async (e) => {
    e.preventDefault();
    setErrors({});
    setFormError("");

    if (formData.password !== formData.confirmPassword) {
      setErrors({ confirmPassword: "Passwords do not match" });
      return;
    }

    try {
      await signup({
        userName: formData.name,
        email: formData.email,
        password: formData.password,
      });

      showToast("Signup successful ✅ Please login.", "success");
      setIsLogin(true);
    } catch (err) {
      const data = err?.response?.data;

      if (data && typeof data === "object") {
        setErrors(data); // backend validation errors
      } else {
        setFormError("Signup failed");
      }
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = `${import.meta.env.VITE_API_BASE_URL.replace(
      "/salezone/ecom",
      ""
    )}/oauth2/authorization/google`;
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div
        className={`w-full max-w-md p-8 rounded-xl ${
          theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-lg"
        }`}
      >
        <h2 className="text-2xl font-bold mb-6 text-center">
          {isLogin ? "Login" : "Sign Up"}
        </h2>

        <form
          onSubmit={isLogin ? handleLogin : handleSignup}
          className="space-y-4"
        >
          {/* Full Name */}
          {!isLogin && (
            <div>
              <input
                type="text"
                name="name"
                placeholder="Full Name"
                onChange={handleChange}
                className={`w-full p-3 rounded border ${
                  errors.userName ? "border-red-500" : ""
                }`}
              />
              {errors.userName && (
                <p className="text-red-500 text-sm mt-1">{errors.userName}</p>
              )}
            </div>
          )}

          {/* Email */}
          <div>
            <input
              type="text"
              name="email"
              placeholder="Email"
              onChange={handleChange}
              className={`w-full p-3 rounded border ${
                errors.email ? "border-red-500" : ""
              }`}
            />
            {errors.email && (
              <p className="text-red-500 text-sm mt-1">{errors.email}</p>
            )}
          </div>

          {/* Password */}
          <div>
            <input
              type="password"
              name="password"
              placeholder="Password"
              onChange={handleChange}
              className={`w-full p-3 rounded border ${
                errors.password ? "border-red-500" : ""
              }`}
            />
            {errors.password && (
              <p className="text-red-500 text-sm mt-1">{errors.password}</p>
            )}
          </div>

          {/* Confirm Password */}
          {!isLogin && (
            <div>
              <input
                type="password"
                name="confirmPassword"
                placeholder="Confirm Password"
                onChange={handleChange}
                className={`w-full p-3 rounded border ${
                  errors.confirmPassword ? "border-red-500" : ""
                }`}
              />
              {errors.confirmPassword && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.confirmPassword}
                </p>
              )}
            </div>
          )}

          {/* Submit Button */}
          <button
            type="submit"
            className="w-full py-3 bg-blue-600 text-white rounded-lg"
          >
            {isLogin ? "Login" : "Sign Up"}
          </button>

          {/* Login Error Message */}
          {formError && (
            <p className="text-red-500 text-sm text-center mt-2">{formError}</p>
          )}
        </form>

        <p className="text-center mt-6 text-sm">
          {isLogin ? "Don't have an account?" : "Already have an account?"}
          <button
            onClick={() => {
              setIsLogin(!isLogin);
              setErrors({});
              setFormError("");
            }}
            className="ml-2 text-blue-600"
            type="button"
          >
            {isLogin ? "Sign Up" : "Login"}
          </button>
        </p>

        <div className="flex items-center gap-3 my-6">
          <div className="flex-1 h-px bg-gray-300" />
          <span>OR</span>
          <div className="flex-1 h-px bg-gray-300" />
        </div>

        <button
          type="button"
          onClick={handleGoogleLogin}
          className="w-full flex items-center justify-center gap-3 py-3 border rounded-lg"
        >
          <FcGoogle size={22} />
          Continue with Google
        </button>
      </div>
    </div>
  );
};

export default Auth;
