import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";

const AuthSuccess = () => {
  const navigate = useNavigate();
  const { loading, isAuthenticated } = useAuth();
  const { showToast } = useToast();

  useEffect(() => {
    if (!loading && isAuthenticated) {
      showToast("Google login successful 🎉", "success");

      const timer = setTimeout(() => {
        navigate("/");
      }, 1500);

      return () => clearTimeout(timer);
    }
  }, [loading, isAuthenticated, navigate, showToast]);

  return (
    <div className="flex items-center justify-center h-screen">
      <div className="text-center">
        <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
        <p className="text-lg font-semibold">Signing you in...</p>
      </div>
    </div>
  );
};

export default AuthSuccess;
