import axios from "axios";
import authService from "./authService";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8089/salezone/ecom",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

axiosInstance.interceptors.request.use(
  (config) => {
    const authHeader = authService.getAuthHeader(); // "Basic xxx"

    if (authHeader) {
      config.headers.Authorization = authHeader;
      console.log("Sending Authorization:", authHeader);
    }

    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error.response || error.message);
    return Promise.reject(error);
  }
);

export default axiosInstance;
