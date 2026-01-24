import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8089/salezone/ecom",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 🔥 optional: request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    // later: attach JWT token here
    return config;
  },
  (error) => Promise.reject(error)
);

// 🔥 optional: response interceptor
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error.response || error.message);
    return Promise.reject(error);
  }
);

export default axiosInstance;
