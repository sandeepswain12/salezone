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

  updateCartItem(userId, itemId, quantity) {
    return api.put(`/carts/${userId}/items/${itemId}?quantity=${quantity}`);
  },
};

export default cartService;
