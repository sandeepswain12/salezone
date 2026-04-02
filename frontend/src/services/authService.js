import api from "./api";

const authService = {
  // ---------------- AUTH ----------------
  signup: async (data) => {
    const res = await api.post("/auth/signup", data);
    return res.data;
  },

  login: async (email, password) => {
    const res = await api.post("/auth/login", { email, password });
    return res.data;
  },

  verifyOtp: async (data) => {
    const res = await api.post("/auth/verify-otp", data);
    return res.data;
  },

  refresh: async () => {
    const res = await api.post("/auth/refresh");
    return res.data;
  },

  logout: async () => {
    await api.post("/auth/logout");
  },

  // ---------------- PASSWORD RESET (OTP FLOW) ----------------
  requestPasswordOtp: async (email) => {
    const res = await api.post("/auth/password-reset/otp/request", {
      email,
    });
    return res.data;
  },

  verifyPasswordOtp: async ({ email, otp, newPassword }) => {
    const res = await api.post("/auth/password-reset/otp/verify", {
      email,
      otp,
      newPassword,
    });
    return res.data;
  },

  // ---------------- PASSWORD RESET (LINK FLOW) ----------------
  requestPasswordLink: async (email) => {
    const res = await api.post("/auth/password-reset/link/request", {
      email,
    });
    return res.data;
  },

  verifyPasswordLink: async ({ resetToken, newPassword }) => {
    const res = await api.post("/auth/password-reset/link/verify", {
      resetToken,
      newPassword,
    });
    return res.data;
  },
};

export default authService;
