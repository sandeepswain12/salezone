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

  // 🔥 Restore session on app load
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const res = await authService.refresh();

        if (res?.accessToken) {
          setAccessToken(res.accessToken);
          setUser(res.user);
        }
      } catch (err) {
        console.log("No active session");
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
  }, []);

  // 🔐 Login
  const login = async (email, password) => {
    const res = await authService.login(email, password);

    setAccessToken(res.accessToken);
    setUser(res.user);
  };

  // 📝 Signup
  const signup = async (data) => {
    return await authService.signup(data);
  };

  // 🚪 Logout
  const logout = async () => {
    try {
      await authService.logout();
    } catch (err) {
      console.error(err);
    } finally {
      setUser(null);
      setAccessToken(null);
    }
  };

  // 🔥 NEW: Sync Updated User (for profile updates)
  const updateUserContext = useCallback((updatedUser) => {
    setUser(updatedUser);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        loading,
        login,
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
