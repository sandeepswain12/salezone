import api from "./api";

const orderService = {
  getAllOrders: async () => {
    const res = await api.get("/orders");
    return res.data;
  },

  getOrdersByUser: async (userId) => {
    const res = await api.get(`/orders/users/${userId}`);
    return res.data;
  },

  createOrder: async (orderData) => {
    const res = await api.post("/orders", orderData);
    return res.data;
  },

  updateOrder: async (orderId, updatedData) => {
    const res = await api.put(`/orders/${orderId}`, updatedData);
    return res.data;
  },

  deleteOrder: async (orderId) => {
    const res = await api.delete(`/orders/${orderId}`);
    return res.data;
  },
};

export default orderService;
