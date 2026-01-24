import axiosInstance from "./axiosInstance";

export const getCategories = async ({
  pageNumber = 0,
  pageSize = 5,
  sortBy = "title",
  sortDir = "asc",
}) => {
  const response = await axiosInstance.get("/categories", {
    params: {
      pageNumber,
      pageSize,
      sortBy,
      sortDir,
    },
  });

  return response.data;
};

export const getProductsByCategory = async (
  categoryId,
  { pageNumber = 0, pageSize = 10, sortBy = "title", sortDir = "asc" } = {}
) => {
  const response = await axiosInstance.get(
    `/categories/${categoryId}/products`,
    {
      params: {
        pageNumber,
        pageSize,
        sortBy,
        sortDir,
      },
    }
  );

  return response.data;
};
