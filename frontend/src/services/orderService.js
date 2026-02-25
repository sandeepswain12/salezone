// // services/orderService.js
// import api from "./api";

// const orderService = {
//   createOrder: async (orderData) => {
//     const res = await api.post("/orders", orderData);
//     return res.data;
//   },

//   getAllOrders: async (page = 0, size = 10) => {
//     const res = await api.get(`/orders?pageNumber=${page}&pageSize=${size}`);
//     return res.data;
//   },

//   getOrdersByUser: async (userId) => {
//     const res = await api.get(`/orders/users/${userId}`);
//     return res.data;
//   },

//   updateOrder: async (orderId, data) => {
//     const res = await api.put(`/orders/${orderId}`, data);
//     return res.data;
//   },

//   deleteOrder: async (orderId) => {
//     const res = await api.delete(`/orders/${orderId}`);
//     return res.data;
//   },
// };

// export default orderService;
