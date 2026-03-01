import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
const REFRESH_URL = import.meta.env.VITE_REFRESH_URL;

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // required for refresh token cookie
});

// 🔐 Access token stored in memory
let accessToken = null;

// ✅ Setter for access token (call after login)
export const setAccessToken = (token) => {
  accessToken = token;
};

// ✅ Optional getter (if needed somewhere)
export const getAccessToken = () => accessToken;

// ===============================
// 🔹 Request Interceptor
// ===============================
api.interceptors.request.use(
  (config) => {
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ===============================
// 🔹 Response Interceptor
// ===============================
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If token expired & not already retried
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const res = await axios.post(
          REFRESH_URL,
          {},
          { withCredentials: true }
        );

        const newAccessToken = res.data.accessToken;

        // Update memory token
        setAccessToken(newAccessToken);

        // Attach new token to failed request
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        // Retry original request
        return api(originalRequest);
      } catch (refreshError) {
        console.log("Session expired. Redirecting to login...");

        // Clear everything
        accessToken = null;
        localStorage.removeItem("userId");

        // Redirect to login
        window.location.href = "/login";

        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
