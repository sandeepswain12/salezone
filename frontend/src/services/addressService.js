import api from "./api";

const addressService = {
  getAddresses: async (userId) => {
    const res = await api.get(`/address/${userId}`);
    return res.data;
  },

  getDefaultAddress: async (userId) => {
    const res = await api.get(`/address/${userId}/default`);
    return res.data;
  },

  addAddress: async (userId, data) => {
    const res = await api.post(`/address/${userId}`, data);
    return res.data;
  },

  updateAddress: async (userId, addressId, data) => {
    const res = await api.put(`/address/${userId}/${addressId}`, data);
    return res.data;
  },

  deleteAddress: async (userId, addressId) => {
    const res = await api.delete(`/address/${userId}/${addressId}`);
    return res.data;
  },

  setDefault: async (userId, addressId) => {
    const res = await api.put(`/address/${userId}/default/${addressId}`);
    return res.data;
  },
};

export default addressService;
