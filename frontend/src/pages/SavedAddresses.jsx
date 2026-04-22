import { useState, useEffect, useCallback } from "react";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { useTheme } from "../context/ThemeContext";
import addressService from "../services/addressService";
import AddressCard from "../components/address/AddressCard";
import AddressFormModal from "../components/address/AddressFormModal";
import { Plus, MapPin } from "lucide-react";

const SavedAddresses = () => {
  const { user } = useAuth();
  const { showToast } = useToast();
  const { theme } = useTheme();
  const isDark = theme === "dark";

  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [settingDefaultId, setSettingDefaultId] = useState(null);

  const fetchAddresses = useCallback(async () => {
    if (!user?.userId) return;
    try {
      setLoading(true);
      const data = await addressService.getAddresses(user.userId);
      // sort: default first
      setAddresses(
        data.sort((a, b) => (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0))
      );
    } catch {
      showToast("Failed to load addresses", "error");
    } finally {
      setLoading(false);
    }
  }, [user?.userId]);

  useEffect(() => {
    fetchAddresses();
  }, [fetchAddresses]);

  const handleOpenAdd = () => {
    setEditingAddress(null);
    setModalOpen(true);
  };

  const handleOpenEdit = (address) => {
    setEditingAddress(address);
    setModalOpen(true);
  };

  const handleSave = async (formData) => {
    setSaving(true);
    try {
      if (editingAddress) {
        await addressService.updateAddress(
          user.userId,
          editingAddress.id,
          formData
        );
        showToast("Address updated", "success");
      } else {
        await addressService.addAddress(user.userId, formData);
        showToast("Address added", "success");
      }
      setModalOpen(false);
      fetchAddresses();
    } catch {
      showToast("Failed to save address", "error");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (addressId) => {
    setDeletingId(addressId);
    try {
      await addressService.deleteAddress(user.userId, addressId);
      showToast("Address deleted", "success");
      fetchAddresses();
    } catch {
      showToast("Failed to delete address", "error");
    } finally {
      setDeletingId(null);
    }
  };

  const handleSetDefault = async (addressId) => {
    setSettingDefaultId(addressId);
    try {
      await addressService.setDefault(user.userId, addressId);
      showToast("Default address updated", "success");
      fetchAddresses();
    } catch {
      showToast("Failed to set default", "error");
    } finally {
      setSettingDefaultId(null);
    }
  };

  return (
    <div className="min-h-screen flex justify-center px-4 py-10">
      <div
        className={`w-full max-w-3xl rounded-2xl shadow-xl p-6 md:p-8 transition-all border ${
          isDark ? "bg-zinc-900 border-zinc-800" : "bg-white border-gray-200"
        }`}
      >
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold">Saved Addresses</h1>
            <p
              className={`text-sm mt-1 ${
                isDark ? "text-zinc-400" : "text-gray-500"
              }`}
            >
              Manage your delivery addresses
            </p>
          </div>
          <button
            onClick={handleOpenAdd}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold transition shadow-md"
          >
            <Plus size={16} /> Add Address
          </button>
        </div>

        {/* Content */}
        {loading ? (
          <div className="space-y-4">
            {[1, 2].map((i) => (
              <div
                key={i}
                className={`rounded-xl h-32 animate-pulse ${
                  isDark ? "bg-zinc-800" : "bg-gray-100"
                }`}
              />
            ))}
          </div>
        ) : addresses.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20 gap-4">
            <div
              className={`w-16 h-16 rounded-full flex items-center justify-center ${
                isDark ? "bg-zinc-800" : "bg-gray-100"
              }`}
            >
              <MapPin
                size={28}
                className={isDark ? "text-zinc-500" : "text-gray-400"}
              />
            </div>
            <p
              className={`text-sm ${
                isDark ? "text-zinc-400" : "text-gray-500"
              }`}
            >
              No saved addresses yet
            </p>
            <button
              onClick={handleOpenAdd}
              className="px-5 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold transition"
            >
              Add Your First Address
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {addresses.map((addr) => (
              <AddressCard
                key={addr.id}
                address={addr}
                onEdit={handleOpenEdit}
                onDelete={handleDelete}
                onSetDefault={handleSetDefault}
                deletingId={deletingId}
                settingDefaultId={settingDefaultId}
              />
            ))}
          </div>
        )}
      </div>

      {/* Modal */}
      {modalOpen && (
        <AddressFormModal
          existing={editingAddress}
          onSave={handleSave}
          onClose={() => setModalOpen(false)}
          saving={saving}
        />
      )}
    </div>
  );
};

export default SavedAddresses;
