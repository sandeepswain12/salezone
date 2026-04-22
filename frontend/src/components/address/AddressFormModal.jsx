import { useState, useEffect } from "react";
import { useTheme } from "../../context/ThemeContext";
import { X } from "lucide-react";

const EMPTY_FORM = {
  name: "",
  mobile: "",
  pincode: "",
  city: "",
  state: "",
  fullAddress: "",
  addressType: "HOME",
  isDefault: false,
};

const AddressFormModal = ({ existing, onSave, onClose, saving }) => {
  const { theme } = useTheme();
  const isDark = theme === "dark";

  const [form, setForm] = useState(EMPTY_FORM);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (existing) {
      setForm({
        name: existing.name || "",
        mobile: existing.mobile || "",
        pincode: existing.pincode || "",
        city: existing.city || "",
        state: existing.state || "",
        fullAddress: existing.fullAddress || "",
        addressType: existing.addressType || "HOME",
        isDefault: existing.isDefault || false,
      });
    } else {
      setForm(EMPTY_FORM);
    }
    setErrors({});
  }, [existing]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const e = {};
    if (!form.name.trim()) e.name = "Name is required";
    if (!form.mobile.trim()) e.mobile = "Mobile is required";
    else if (!/^\d{10}$/.test(form.mobile))
      e.mobile = "Enter valid 10-digit number";
    if (!form.pincode.trim()) e.pincode = "Pincode is required";
    else if (!/^\d{6}$/.test(form.pincode))
      e.pincode = "Enter valid 6-digit pincode";
    if (!form.city.trim()) e.city = "City is required";
    if (!form.state.trim()) e.state = "State is required";
    if (!form.fullAddress.trim()) e.fullAddress = "Address is required";
    return e;
  };

  const handleSubmit = () => {
    const e = validate();
    if (Object.keys(e).length) {
      setErrors(e);
      return;
    }
    onSave(form);
  };

  const card = isDark
    ? "bg-zinc-900 border-zinc-700 text-white"
    : "bg-white border-gray-200 text-gray-900";

  const input = isDark
    ? "bg-zinc-800 border-zinc-700 text-white focus:border-blue-500"
    : "bg-gray-50 border-gray-300 text-gray-900 focus:border-blue-500";

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4"
      style={{ background: "rgba(0,0,0,0.55)" }}
    >
      <div className={`w-full max-w-lg rounded-2xl border shadow-2xl ${card}`}>
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-inherit">
          <h2 className="text-lg font-semibold">
            {existing ? "Edit Address" : "Add New Address"}
          </h2>
          <button
            onClick={onClose}
            className="p-1 rounded-full hover:bg-zinc-700/30 transition"
          >
            <X size={20} />
          </button>
        </div>

        {/* Body */}
        <div className="px-6 py-5 space-y-4 max-h-[70vh] overflow-y-auto">
          {/* Name + Mobile */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">
                Full Name
              </label>
              <input
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="John Doe"
                className={`w-full px-3 py-2 rounded-lg border outline-none transition text-sm ${input} ${
                  errors.name ? "border-red-500" : ""
                }`}
              />
              {errors.name && (
                <p className="text-red-500 text-xs mt-1">{errors.name}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Mobile</label>
              <input
                name="mobile"
                value={form.mobile}
                onChange={handleChange}
                placeholder="10-digit number"
                maxLength={10}
                className={`w-full px-3 py-2 rounded-lg border outline-none transition text-sm ${input} ${
                  errors.mobile ? "border-red-500" : ""
                }`}
              />
              {errors.mobile && (
                <p className="text-red-500 text-xs mt-1">{errors.mobile}</p>
              )}
            </div>
          </div>

          {/* Pincode + City */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Pincode</label>
              <input
                name="pincode"
                value={form.pincode}
                onChange={handleChange}
                placeholder="6-digit pincode"
                maxLength={6}
                className={`w-full px-3 py-2 rounded-lg border outline-none transition text-sm ${input} ${
                  errors.pincode ? "border-red-500" : ""
                }`}
              />
              {errors.pincode && (
                <p className="text-red-500 text-xs mt-1">{errors.pincode}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">City</label>
              <input
                name="city"
                value={form.city}
                onChange={handleChange}
                placeholder="City"
                className={`w-full px-3 py-2 rounded-lg border outline-none transition text-sm ${input} ${
                  errors.city ? "border-red-500" : ""
                }`}
              />
              {errors.city && (
                <p className="text-red-500 text-xs mt-1">{errors.city}</p>
              )}
            </div>
          </div>

          {/* State + Type */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">State</label>
              <input
                name="state"
                value={form.state}
                onChange={handleChange}
                placeholder="State"
                className={`w-full px-3 py-2 rounded-lg border outline-none transition text-sm ${input} ${
                  errors.state ? "border-red-500" : ""
                }`}
              />
              {errors.state && (
                <p className="text-red-500 text-xs mt-1">{errors.state}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">
                Address Type
              </label>
              <select
                name="addressType"
                value={form.addressType}
                onChange={handleChange}
                className={`w-full px-3 py-2 rounded-lg border outline-none transition text-sm ${input}`}
              >
                <option value="HOME">Home</option>
                <option value="WORK">Work</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
          </div>

          {/* Full Address */}
          <div>
            <label className="block text-sm font-medium mb-1">
              Street / Full Address
            </label>
            <textarea
              name="fullAddress"
              value={form.fullAddress}
              onChange={handleChange}
              rows={2}
              placeholder="House No, Street, Locality"
              className={`w-full px-3 py-2 rounded-lg border outline-none transition text-sm resize-none ${input} ${
                errors.fullAddress ? "border-red-500" : ""
              }`}
            />
            {errors.fullAddress && (
              <p className="text-red-500 text-xs mt-1">{errors.fullAddress}</p>
            )}
          </div>

          {/* Set Default */}
          <label className="flex items-center gap-3 cursor-pointer select-none">
            <input
              type="checkbox"
              name="isDefault"
              checked={form.isDefault}
              onChange={handleChange}
              className="w-4 h-4 accent-blue-600"
            />
            <span className="text-sm">Set as default address</span>
          </label>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-inherit flex gap-3 justify-end">
          <button
            onClick={onClose}
            className={`px-5 py-2 rounded-lg text-sm font-medium transition border ${
              isDark
                ? "border-zinc-600 hover:bg-zinc-800"
                : "border-gray-300 hover:bg-gray-100"
            }`}
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            disabled={saving}
            className="px-5 py-2 rounded-lg text-sm font-semibold bg-blue-600 hover:bg-blue-700 text-white transition disabled:opacity-50"
          >
            {saving
              ? "Saving..."
              : existing
              ? "Update Address"
              : "Save Address"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddressFormModal;
