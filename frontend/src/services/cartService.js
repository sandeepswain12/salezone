import api from "./api";

const cartService = {
  getCart: async (userId) => {
    const res = await api.get(`/carts/${userId}`);
    return res.data;
  },

  addToCart: async (userId, productId, quantity = 1) => {
    const res = await api.post(`/carts/${userId}`, {
      productId,
      quantity,
    });
    return res.data;
  },

  removeCartItem: async (userId, cartItemId) => {
    const res = await api.delete(`/carts/${userId}/items/${cartItemId}`);
    return res.data;
  },

  clearCart: async (userId) => {
    const res = await api.delete(`/carts/${userId}`);
    return res.data;
  },

  updateCartItem: async (userId, cartItemId, quantity) => {
    const res = await api.put(`/carts/${userId}/items/${cartItemId}`, {
      quantity,
    });
    return res.data;
  },
};

export default cartService;
