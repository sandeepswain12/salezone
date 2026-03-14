import publicApi from "./publicapi";

// Get Categories (Paginated)
export const getCategories = async ({
  pageNumber = 0,
  pageSize = 10,
  sortBy = "title",
  sortDir = "asc",
} = {}) => {
  const response = await publicApi.get("/categories");

  return response.data;
};

// Get Single Category By ID
export const getCategoryById = async (categoryId) => {
  const response = await publicApi.get(`/categories/${categoryId}`);
  return response.data;
};

// Get Products By Category (Paginated)
export const getProductsByCategory = async ({
  categoryId,
  pageNumber = 0,
  pageSize = 10,
  sortBy = "title",
  sortDir = "asc",
}) => {
  const response = await publicApi.get(`/categories/${categoryId}/products`, {
    params: {
      pageNumber,
      pageSize,
      sortBy,
      sortDir,
    },
  });

  return response.data;
};
