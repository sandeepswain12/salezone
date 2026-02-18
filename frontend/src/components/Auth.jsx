import { useState } from "react";
import { useTheme } from "../context/ThemeContext";
import { FcGoogle } from "react-icons/fc";
import authService from "../services/authService";
import { useNavigate } from "react-router-dom";

const Auth = () => {
  const { theme } = useTheme();
  const [isLogin, setIsLogin] = useState(true);
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  // useEffect(() => {
  //   if (authService.isLoggedIn()) {

  //   }
  // }, [navigate]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // 🔐 LOGIN
  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const msg = await authService.login(formData.email, formData.password);

      alert(msg);
      navigate("/");
    } catch (err) {
      alert(err.message);
    }
  };

  // 🔓 SIGNUP
  const handleSignup = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    try {
      await authService.signup({
        userName: formData.name,
        email: formData.email,
        password: formData.password,
      });

      alert("Signup successful, please login");
      setIsLogin(true);
    } catch (err) {
      alert(err.message);
    }
  };

  // 🌐 GOOGLE LOGIN
  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4">
      <div
        className={`w-full max-w-md p-8 rounded-xl
          ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-lg"}
        `}
      >
        <h2 className="text-2xl font-bold mb-6 text-center">
          {isLogin ? "Login to SaleZone" : "Create your SaleZone account"}
        </h2>

        <form
          className="space-y-4"
          onSubmit={isLogin ? handleLogin : handleSignup}
        >
          {!isLogin && (
            <input
              type="text"
              name="name"
              placeholder="Full Name"
              onChange={handleChange}
              className={`w-full p-3 rounded border outline-none
                ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
              `}
            />
          )}

          <input
            type="email"
            name="email"
            placeholder="Email"
            onChange={handleChange}
            className={`w-full p-3 rounded border outline-none
              ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
            `}
          />

          <input
            type="password"
            name="password"
            placeholder="Password"
            onChange={handleChange}
            className={`w-full p-3 rounded border outline-none
              ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
            `}
          />

          {!isLogin && (
            <input
              type="password"
              name="confirmPassword"
              placeholder="Confirm Password"
              onChange={handleChange}
              className={`w-full p-3 rounded border outline-none
                ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
              `}
            />
          )}

          <button
            type="submit"
            className="w-full py-3 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition"
          >
            {isLogin ? "Login" : "Sign Up"}
          </button>
        </form>

        {/* SWITCH */}
        <p className="text-sm text-center mt-6 opacity-80">
          {isLogin ? "Don't have an account?" : "Already have an account?"}
          <button
            onClick={() => setIsLogin(!isLogin)}
            className="ml-2 text-blue-600 font-medium"
            type="button"
          >
            {isLogin ? "Sign Up" : "Login"}
          </button>
        </p>

        <div className="flex items-center gap-3 my-4">
          <div className="flex-1 h-px bg-gray-300 dark:bg-gray-700" />
          <span className="text-sm opacity-60">OR</span>
          <div className="flex-1 h-px bg-gray-300 dark:bg-gray-700" />
        </div>

        <button
          type="button"
          onClick={handleGoogleLogin}
          className={`w-full flex items-center justify-center gap-3 py-3 rounded-lg border
            ${
              theme === "dark"
                ? "border-gray-700 hover:bg-[#1a1a1a]"
                : "border-gray-300 hover:bg-gray-100"
            }
          `}
        >
          <FcGoogle size={22} />
          <span className="font-medium">Continue with Google</span>
        </button>
      </div>
    </div>
  );
};

export default Auth;
