import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
const REFRESH_URL = import.meta.env.VITE_REFRESH_URL;

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
});

// Access token in memory only (not read from sessionStorage here)
let accessToken = null;

// Queue for pending requests while refreshing
let isRefreshing = false;
let refreshSubscribers = [];

const onRefreshed = (token) => {
  refreshSubscribers.forEach((cb) => cb(token));
  refreshSubscribers = [];
};

const subscribeTokenRefresh = (callback) => {
  refreshSubscribers.push(callback);
};

// Request Interceptor
api.interceptors.request.use(
  (config) => {
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

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
        const newToken = res.data.accessToken;

        // update memory
        accessToken = newToken;
        // cache for same-tab refreshes
        sessionStorage.setItem("accessToken", newToken);

        onRefreshed(newToken);

        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        accessToken = null;
        sessionStorage.removeItem("accessToken");
        localStorage.removeItem("user");
        window.location.href = "/auth";
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export const setAccessToken = (token) => {
  accessToken = token;
  if (token) {
    sessionStorage.setItem("accessToken", token);
  } else {
    sessionStorage.removeItem("accessToken");
  }
};

export const getAccessToken = () => accessToken;

export default api;
