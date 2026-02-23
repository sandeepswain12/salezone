import { createContext, useContext, useState, useEffect } from "react";
import authService from "../services/authService";
import { setAccessToken } from "../services/api";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  // 🔥 AUTO REFRESH ON APP LOAD
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const res = await authService.refresh();
        setAccessToken(res.accessToken);
        setUser(res.user);
        setIsAuthenticated(true);
      } catch (err) {
        console.log("No active session");
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
  }, []);

  const login = async (email, password) => {
    const res = await authService.login(email, password);
    setAccessToken(res.accessToken);
    setUser(res.user);
    setIsAuthenticated(true);
  };

  const signup = async (data) => {
    return await authService.signup(data);
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (err) {
      console.error(err);
    } finally {
      setUser(null);
      setIsAuthenticated(false);
      setAccessToken(null);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        loading,
        login,
        signup,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
