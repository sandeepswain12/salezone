import { useAuth } from "../../context/AuthContext";
import { useTheme } from "../../context/ThemeContext";

const AuthGate = ({ children }) => {
  const { loading } = useAuth();
  const { theme } = useTheme(); // assuming theme = "light" | "dark"

  if (loading) {
    return (
      <div
        className={`min-h-screen flex items-center justify-center transition-colors duration-300
        ${
          theme === "dark"
            ? "bg-black"
            : "bg-gradient-to-br from-gray-100 to-gray-200"
        }`}
      >
        <div
          className={`flex flex-col items-center gap-6 px-10 py-12 rounded-2xl shadow-xl
          ${
            theme === "dark"
              ? "bg-white/5 backdrop-blur-md border border-white/10"
              : "bg-white"
          }`}
        >
          {/* Logo / Brand */}
          <h1
            className={`text-2xl font-bold tracking-wide ${
              theme === "dark" ? "text-white" : "text-gray-800"
            }`}
          >
            SaleZone
          </h1>

          {/* Spinner */}
          <div className="relative w-12 h-12">
            <div
              className={`absolute inset-0 rounded-full border-4 ${
                theme === "dark" ? "border-gray-700" : "border-gray-200"
              }`}
            ></div>
            <div className="absolute inset-0 rounded-full border-4 border-blue-600 border-t-transparent animate-spin"></div>
          </div>

          {/* Text */}
          <p
            className={`text-sm ${
              theme === "dark" ? "text-gray-400" : "text-gray-500"
            }`}
          >
            Securely authenticating your session...
          </p>
        </div>
      </div>
    );
  }

  return children;
};

export default AuthGate;
