import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { updateUser } from "../services/userService";
import { useToast } from "../context/ToastContext";
import { useTheme } from "../context/ThemeContext";

const Profile = () => {
  const { user, loading, updateUserContext } = useAuth();
  const { showToast } = useToast();
  const { theme } = useTheme();

  const [formData, setFormData] = useState({
    userName: "",
    email: "",
    gender: "",
    about: "",
    phoneNumber: "",
    imageName: "",
  });

  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const getAvatarColor = (name) => {
    const colors = [
      "from-blue-500 to-indigo-600",
      "from-purple-500 to-pink-500",
      "from-emerald-500 to-teal-600",
      "from-orange-500 to-red-500",
      "from-cyan-500 to-blue-500",
    ];

    const index = name ? name.charCodeAt(0) % colors.length : 0;

    return colors[index];
  };

  // Load user data into form
  useEffect(() => {
    if (user) {
      setFormData({
        userName: user.userName || "",
        email: user.email || "",
        gender: user.gender || "",
        about: user.about || "",
        phoneNumber: user.phoneNumber || "",
        imageName: user.imageName || "",
      });
    }
  }, [user]);

  if (loading) return null;
  if (!user) return <div className="p-6 text-center">User not found</div>;

  // Handle input change
  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    // Clear field error while typing
    setErrors((prev) => ({
      ...prev,
      [name]: "",
    }));
  };

  // Handle update
  const handleUpdate = async (e) => {
    e.preventDefault();
    setSaving(true);
    setErrors({});

    try {
      const updatedUser = await updateUser(user.userId, formData);

      // Merge to prevent losing userId or roles
      updateUserContext({
        ...user,
        ...updatedUser,
      });

      showToast("Profile updated successfully 🚀", "success");
    } catch (err) {
      const data = err?.response?.data;

      if (data && typeof data === "object") {
        setErrors(data); // backend validation errors
      } else {
        showToast("Update failed ❌", "error");
      }
    } finally {
      setSaving(false);
    }
  };

  const isGoogleUser = user.provider === "GOOGLE";

  const inputBaseStyle = `
    w-full px-4 py-2 rounded-lg border outline-none transition-all
    ${
      theme === "dark"
        ? "bg-zinc-800 border-zinc-700 text-white focus:ring-2 focus:ring-blue-500"
        : "bg-gray-50 border-gray-300 text-black focus:ring-2 focus:ring-blue-400"
    }
  `;

  return (
    <div className="min-h-screen flex justify-center px-4 py-10">
      <div
        className={`w-full max-w-4xl rounded-2xl shadow-xl p-8 transition-all
          ${
            theme === "dark"
              ? "bg-zinc-900 border border-zinc-800"
              : "bg-white border border-gray-200"
          }
        `}
      >
        {/* HEADER */}
        <div className="flex flex-col md:flex-row items-center gap-6 mb-10">
          {/* {formData.imageName ? (
            <img
              src={formData.imageName}
              alt="Profile"
              className="w-28 h-28 rounded-full object-cover border-4 border-blue-500 shadow-md"
            />
          ) : (
            <div className="w-28 h-28 rounded-full bg-gray-300 dark:bg-zinc-700 flex items-center justify-center text-xl font-bold">
              {user.userName?.charAt(0).toUpperCase()}
            </div>
          )} */}
          <div
            className={`w-28 h-28 rounded-full bg-gradient-to-br ${getAvatarColor(
              user.userName
            )} flex items-center justify-center text-4xl font-semibold text-white shadow-xl ring-4 ${
              theme === "dark" ? "ring-zinc-800" : "ring-white"
            } transition-transform duration-300 hover:scale-105`}
          >
            {user.userName?.charAt(0).toUpperCase()}
          </div>
          <div className="text-center md:text-left">
            <h2 className="text-3xl font-bold">{user.userName}</h2>
            <p className="opacity-70 mt-1">{user.email}</p>
          </div>
        </div>

        {/* FORM */}
        <form onSubmit={handleUpdate} className="grid md:grid-cols-2 gap-6">
          {/* Username */}
          <div>
            <label className="block mb-2 font-medium">Username</label>
            <input
              type="text"
              name="userName"
              value={formData.userName}
              onChange={handleChange}
              className={`${inputBaseStyle} ${
                errors.userName ? "border-red-500" : ""
              }`}
            />
            {errors.userName && (
              <p className="text-red-500 text-sm mt-1">{errors.userName}</p>
            )}
          </div>

          {/* Email */}
          <div>
            <label className="block mb-2 font-medium">Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              disabled={isGoogleUser}
              className={`${inputBaseStyle} ${
                errors.email ? "border-red-500" : ""
              } ${isGoogleUser ? "opacity-60 cursor-not-allowed" : ""}`}
            />
            {errors.email && (
              <p className="text-red-500 text-sm mt-1">{errors.email}</p>
            )}
          </div>

          {/* Phone */}
          <div>
            <label className="block mb-2 font-medium">Phone</label>
            <input
              type="text"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              className={`${inputBaseStyle} ${
                errors.phoneNumber ? "border-red-500" : ""
              }`}
            />
            {errors.phoneNumber && (
              <p className="text-red-500 text-sm mt-1">{errors.phoneNumber}</p>
            )}
          </div>

          {/* Gender */}
          <div>
            <label className="block mb-2 font-medium">Gender</label>
            <select
              name="gender"
              value={formData.gender}
              onChange={handleChange}
              className={`${inputBaseStyle} ${
                errors.gender ? "border-red-500" : ""
              }`}
            >
              <option value="">Select</option>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
            </select>
            {errors.gender && (
              <p className="text-red-500 text-sm mt-1">{errors.gender}</p>
            )}
          </div>

          {/* About */}
          <div className="md:col-span-2">
            <label className="block mb-2 font-medium">About</label>
            <textarea
              name="about"
              value={formData.about}
              onChange={handleChange}
              rows="4"
              className={`${inputBaseStyle} ${
                errors.about ? "border-red-500" : ""
              }`}
            />
            {errors.about && (
              <p className="text-red-500 text-sm mt-1">{errors.about}</p>
            )}
          </div>

          {/* Submit Button */}
          <div className="md:col-span-2">
            <button
              type="submit"
              disabled={saving}
              className={`w-full py-3 rounded-xl font-semibold transition-all
                ${
                  saving
                    ? "bg-gray-400 cursor-not-allowed"
                    : "bg-blue-600 hover:bg-blue-700 text-white shadow-lg hover:shadow-xl"
                }
              `}
            >
              {saving ? "Updating..." : "Update Profile"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Profile;
