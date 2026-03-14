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

  // Restore session on page reload
  useEffect(() => {
    const token = sessionStorage.getItem("accessToken");
    const storedUser = localStorage.getItem("user");

    if (token) {
      setAccessToken(token);
    }

    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }

    setLoading(false);
  }, []);

  // Login
  const login = async (email, password) => {
    const res = await authService.login(email, password);

    // save token
    setAccessToken(res.accessToken);
    sessionStorage.setItem("accessToken", res.accessToken);

    // save user
    setUser(res.user);
    localStorage.setItem("user", JSON.stringify(res.user));
  };

  // Signup
  const signup = async (data) => {
    return await authService.signup(data);
  };

  // Logout
  const logout = async () => {
    try {
      await authService.logout();
    } catch (err) {
      console.error(err);
    } finally {
      setUser(null);
      setAccessToken(null);

      sessionStorage.removeItem("accessToken");
      localStorage.removeItem("user");
    }
  };

  // Sync updated user (profile update)
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
