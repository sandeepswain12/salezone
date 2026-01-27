// import axios from "axios";

// const authAxios = axios.create({
//   baseURL: "http://localhost:8089/salezone/ecom",
//   timeout: 10000,
//   headers: {
//     "Content-Type": "application/json",
//   },
// });

// // Attach BASIC AUTH dynamically
// authAxios.interceptors.request.use(
//   (config) => {
//     if (config.auth) {
//       const { username, password } = config.auth;
//       const token = btoa(`${username}:${password}`);
//       config.headers.Authorization = `Basic ${token}`;
//     }
//     return config;
//   },
//   (error) => Promise.reject(error)
// );

// export default authAxios;
