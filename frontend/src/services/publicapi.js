import axios from "axios";

const publicApi = axios.create({
  baseURL: "http://localhost:8089/salezone/ecom",
});

export default publicApi;
