import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
const REFRESH_URL = import.meta.env.VITE_REFRESH_URL;

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
});

let accessToken = null;

export const setAccessToken = (token) => {
  accessToken = token;
};

api.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      try {
        const res = await axios.post(
          REFRESH_URL,
          {},
          { withCredentials: true }
        );

        accessToken = res.data.accessToken;
        error.config.headers.Authorization = `Bearer ${accessToken}`;

        return api(error.config);
      } catch (err) {
        console.log("Session expired");
      }
    }

    return Promise.reject(error);
  }
);

export default api;
