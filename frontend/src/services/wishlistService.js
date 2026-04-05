import api from "./api";

const wishlistService = {
  getWishlist: async (userId) => {
    const res = await api.get(`/wishlist/${userId}`);
    return res.data;
  },

  addToWishlist: async (userId, productId) => {
    const res = await api.post(`/wishlist/${userId}`, { productId });
    return res.data;
  },

  removeFromWishlist: async (userId, productId) => {
    const res = await api.delete(`/wishlist/${userId}/${productId}`);
    return res.data;
  },
};

export default wishlistService;
