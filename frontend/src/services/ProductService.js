import axiosInstance from "./axiosInstance";

export const getProducts = async ({
  pageNumber = 0, // frontend (0-based)
  pageSize = 4,
  sortBy = "title",
  sortDir = "asc",
}) => {
  const backendPage = pageNumber; // 🔥 backend is 1-based

  const response = await axiosInstance.get("/products", {
    params: {
      pageNumber: backendPage,
      pageSize,
      sortBy,
      sortDir,
    },
  });

  return response.data; // axios auto-parses JSON
};

export const getProductById = async (productId) => {
  const response = await axiosInstance.get(`/products/${productId}`);
  return response.data;
};

export const searchProducts = async ({
  keyword,
  pageNumber = 0,
  pageSize = 8,
}) => {
  const response = await axiosInstance.get(
    `/products/search/${encodeURIComponent(keyword)}`,
    {
      params: {
        pageNumber,
        pageSize,
      },
    }
  );

  return response.data;
};
