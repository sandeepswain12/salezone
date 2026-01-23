import { useState } from "react";
import { useTheme } from "../context/ThemeContext";
import { FcGoogle } from "react-icons/fc";

const Auth = () => {
  const { theme } = useTheme();
  const [isLogin, setIsLogin] = useState(true);
  const handleGoogleLogin = () => {
    // later this will hit backend OAuth endpoint
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4">
      <div
        className={`w-full max-w-md p-8 rounded-xl
          ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-lg"}
        `}
      >
        {/* TITLE */}
        <h2 className="text-2xl font-bold mb-6 text-center">
          {isLogin ? "Login to SaleZone" : "Create your SaleZone account"}
        </h2>

        {/* FORM */}
        <form className="space-y-4">
          {!isLogin && (
            <input
              type="text"
              placeholder="Full Name"
              className={`w-full p-3 rounded border outline-none
                ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
              `}
            />
          )}

          <input
            type="email"
            placeholder="Email"
            className={`w-full p-3 rounded border outline-none
              ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
            `}
          />

          <input
            type="password"
            placeholder="Password"
            className={`w-full p-3 rounded border outline-none
              ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
            `}
          />

          {!isLogin && (
            <input
              type="password"
              placeholder="Confirm Password"
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
          >
            {isLogin ? "Sign Up" : "Login"}
          </button>
          <div className="flex items-center gap-3 my-4">
            <div className="flex-1 h-px bg-gray-300 dark:bg-gray-700" />
            <span className="text-sm opacity-60">OR</span>
            <div className="flex-1 h-px bg-gray-300 dark:bg-gray-700" />
          </div>

          <button
            type="button"
            onClick={() => handleGoogleLogin()}
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
        </p>
      </div>
    </div>
  );
};

export default Auth;
