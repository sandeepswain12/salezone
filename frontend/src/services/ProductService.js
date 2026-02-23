import api from "./api";

// ✅ Get all products
export const getProducts = async ({
  pageNumber = 0,
  pageSize = 10,
  sortBy = "title",
  sortDir = "asc",
} = {}) => {
  const response = await api.get("/products", {
    params: {
      pageNumber,
      pageSize,
      sortBy,
      sortDir,
    },
  });

  return response.data;
};

// ✅ Get single product by ID
export const getProductById = async (productId) => {
  if (!productId) throw new Error("Invalid product ID");

  const response = await api.get(`/products/${productId}`);
  return response.data;
};

// ✅ Search products (FIXED for your backend)
export const searchProducts = async ({
  keyword,
  pageNumber = 0,
  pageSize = 10,
  sortBy = "title",
  sortDir = "asc",
}) => {
  if (!keyword) throw new Error("Keyword required");

  const response = await api.get(`/products/search/${keyword}`, {
    params: {
      pageNumber,
      pageSize,
      sortBy,
      sortDir,
    },
  });

  return response.data;
};
