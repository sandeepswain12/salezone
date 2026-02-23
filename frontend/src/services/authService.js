import api from "./api";

const authService = {
  signup: async (data) => {
    const res = await api.post("/auth/signup", data);
    return res.data;
  },

  login: async (email, password) => {
    const res = await api.post("/auth/login", { email, password });
    return res.data;
  },

  refresh: async () => {
    const res = await api.post("/auth/refresh");
    return res.data;
  },

  logout: async () => {
    await api.post("/auth/logout");
  },
};

export default authService;
