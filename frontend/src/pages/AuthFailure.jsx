import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useToast } from "../context/ToastContext";

const AuthFailure = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();

  useEffect(() => {
    sessionStorage.removeItem("accessToken");
    localStorage.removeItem("user");

    showToast("Google authentication failed ❌", "error");

    const timer = setTimeout(() => {
      navigate("/auth");
    }, 2500);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="flex flex-col items-center justify-center h-screen gap-4">
      <div className="text-center">
        <h2 className="text-2xl font-bold text-red-500 mb-2">
          Authentication Failed
        </h2>
        <p className="text-gray-500 text-sm mb-4">
          Redirecting to login page...
        </p>
        <div className="w-8 h-8 border-4 border-red-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
      </div>
    </div>
  );
};

export default AuthFailure;
