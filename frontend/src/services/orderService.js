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

  initiatePayment: async (orderId) => {
    const res = await api.post(`/payment/initiate-payment/${orderId}`);
    return res.data;
  },

  capturePayment: async (orderId, paymentData) => {
    const res = await api.post(`/payment/capture/${orderId}`, paymentData);
    return res.data;
  },
};

export default orderService;
