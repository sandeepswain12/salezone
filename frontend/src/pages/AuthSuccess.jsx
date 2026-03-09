import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { setAccessToken } from "../services/api";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";

const REFRESH_URL = import.meta.env.VITE_REFRESH_URL;

const AuthSuccess = () => {
  const navigate = useNavigate();
  const { updateUserContext } = useAuth();
  const { showToast } = useToast();

  useEffect(() => {
    const restoreSession = async () => {
      try {
        const res = await axios.post(
          REFRESH_URL,
          {},
          { withCredentials: true }
        );

        const { accessToken, user } = res.data;

        setAccessToken(accessToken);
        sessionStorage.setItem("accessToken", accessToken);

        localStorage.setItem("user", JSON.stringify(user));
        updateUserContext(user);

        showToast("Google login successful 🎉", "success");

        setTimeout(() => {
          navigate("/");
        }, 1000);
      } catch (error) {
        console.error("OAuth restore failed", error);
        navigate("/login");
      }
    };

    restoreSession();
  }, [navigate, showToast, updateUserContext]);

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
