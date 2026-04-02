import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from "react";
import authService from "../services/authService";
import { setAccessToken } from "../services/api";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const isAuthenticated = !!user;

  useEffect(() => {
    const restoreSession = async () => {
      try {
        const cachedToken = sessionStorage.getItem("accessToken");

        if (cachedToken) {
          // ✅ Same tab refresh → use cached token, no network call
          setAccessToken(cachedToken);
          const storedUser = localStorage.getItem("user");
          if (storedUser) setUser(JSON.parse(storedUser));
        } else {
          // ✅ New tab / window / first load → call refresh
          // browser auto sends httpOnly refresh cookie
          const res = await authService.refresh();
          setAccessToken(res.accessToken); // sets memory + sessionStorage internally
          setUser(res.user);
          localStorage.setItem("user", JSON.stringify(res.user));
        }
      } catch (err) {
        // Refresh token expired or doesn't exist → force login
        console.log("No active session");
        setUser(null);
        sessionStorage.removeItem("accessToken");
        localStorage.removeItem("user");
      } finally {
        setLoading(false);
      }
    };

    restoreSession();
  }, []);

  // LOGIN - Step 1 only, returns preAuthToken for OTP flow
  const login = async (email, password) => {
    return await authService.login(email, password);
  };

  // VERIFY OTP - Real login happens here
  const verifyOtp = async (data) => {
    const res = await authService.verifyOtp(data);

    // setAccessToken handles both memory + sessionStorage
    setAccessToken(res.accessToken);
    setUser(res.user);
    localStorage.setItem("user", JSON.stringify(res.user));

    return res;
  };

  // SIGNUP
  const signup = async (data) => {
    return await authService.signup(data);
  };

  // LOGOUT
  const logout = async () => {
    try {
      await authService.logout(); // backend clears httpOnly cookie
    } catch (err) {
      console.error(err);
    } finally {
      setUser(null);
      setAccessToken(null); // clears memory + sessionStorage internally
      localStorage.removeItem("user");
    }
  };

  // Sync updated user after profile update
  const updateUserContext = useCallback((updatedUser) => {
    setUser(updatedUser);
    localStorage.setItem("user", JSON.stringify(updatedUser));
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        loading,
        login,
        verifyOtp,
        signup,
        logout,
        updateUserContext,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
