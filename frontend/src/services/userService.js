import api from "./api";

// Get user by ID
export const getUserById = async (userId) => {
  const response = await api.get(`/users/${userId}`);
  return response.data;
};

// Update user (basic profile info)
export const updateUser = async (userId, userData) => {
  const response = await api.put(`/users/update/${userId}`, userData);
  return response.data;
};

// Change password
export const changePassword = async (userId, passwordData) => {
  const response = await api.put(
    `/users/change-password/${userId}`,
    passwordData
  );
  return response.data;
};

// Get all users (ADMIN only)
export const getAllUsers = async () => {
  const response = await api.get(`/users`);
  return response.data;
};

// Delete user (ADMIN only)
export const deleteUser = async (userId) => {
  const response = await api.delete(`/users/${userId}`);
  return response.data;
};

// upload user image (USER/ADMIN)
export const uploadUserImage = async (userId, file) => {
  const formData = new FormData();
  formData.append("userImage", file);

  const res = await api.post(`/users/image/${userId}`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return res.data;
};
