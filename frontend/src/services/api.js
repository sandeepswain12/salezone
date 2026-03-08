import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
const REFRESH_URL = import.meta.env.VITE_REFRESH_URL;

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
});

// 🔐 Access token stored in memory
let accessToken = sessionStorage.getItem("accessToken") || null;

// Queue for pending requests while refreshing
let isRefreshing = false;
let refreshSubscribers = [];

// Notify all queued requests
const onRefreshed = (token) => {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
};

// Add request to queue
const subscribeTokenRefresh = (callback) => {
  refreshSubscribers.push(callback);
};

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

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // If refresh already in progress → wait
      if (isRefreshing) {
        return new Promise((resolve) => {
          subscribeTokenRefresh((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(api(originalRequest));
          });
        });
      }

      isRefreshing = true;

      try {
        const res = await axios.post(
          REFRESH_URL,
          {},
          { withCredentials: true }
        );

        const newAccessToken = res.data.accessToken;

        // Save token
        accessToken = newAccessToken;
        sessionStorage.setItem("accessToken", newAccessToken);

        // Notify queued requests
        onRefreshed(newAccessToken);

        // Retry original request
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        console.log("Session expired. Redirecting to login...");

        accessToken = null;
        sessionStorage.removeItem("accessToken");
        localStorage.removeItem("user");

        window.location.href = "/login";

        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

// Setter for token
export const setAccessToken = (token) => {
  accessToken = token;
};

// Getter if needed
export const getAccessToken = () => accessToken;

export default api;
