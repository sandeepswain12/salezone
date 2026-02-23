import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8089/salezone/ecom",
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

// 🔥 AUTO RETRY ON 401
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      try {
        const res = await axios.post(
          "http://localhost:8089/salezone/ecom/auth/refresh",
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
