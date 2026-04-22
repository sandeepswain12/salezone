import { MapPin, Phone, Pencil, Trash2, Star } from "lucide-react";
import { useTheme } from "../../context/ThemeContext";

const TYPE_COLORS = {
  HOME: "bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300",
  WORK: "bg-purple-100 text-purple-700 dark:bg-purple-900/40 dark:text-purple-300",
  OTHER: "bg-zinc-100 text-zinc-600 dark:bg-zinc-700 dark:text-zinc-300",
};

const AddressCard = ({
  address,
  onEdit,
  onDelete,
  onSetDefault,
  selectable = false,
  selected = false,
  onSelect,
  deletingId,
  settingDefaultId,
}) => {
  const { theme } = useTheme();
  const isDark = theme === "dark";

  const isDeleting = deletingId === address.id;
  const isSettingDefault = settingDefaultId === address.id;

  const borderClass = selected
    ? "border-blue-500 ring-2 ring-blue-500/30"
    : isDark
    ? "border-zinc-700 hover:border-zinc-500"
    : "border-gray-200 hover:border-gray-300";

  return (
    <div
      onClick={selectable ? onSelect : undefined}
      className={`relative rounded-xl border p-4 transition-all ${
        isDark ? "bg-zinc-900" : "bg-white"
      } ${borderClass} ${selectable ? "cursor-pointer" : ""}`}
    >
      {/* Default badge */}
      {address.isDefault && (
        <span className="absolute top-3 right-3 flex items-center gap-1 text-xs font-semibold bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-400 px-2 py-0.5 rounded-full">
          <Star size={11} fill="currentColor" /> Default
        </span>
      )}

      {/* Selected indicator for checkout */}
      {selectable && selected && (
        <span className="absolute top-3 right-3 flex items-center gap-1 text-xs font-semibold bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300 px-2 py-0.5 rounded-full">
          ✓ Selected
        </span>
      )}

      {/* Type badge */}
      <span
        className={`inline-block text-xs font-semibold px-2 py-0.5 rounded-full mb-2 ${
          TYPE_COLORS[address.addressType] || TYPE_COLORS.OTHER
        }`}
      >
        {address.addressType}
      </span>

      {/* Name */}
      <p className="font-semibold text-sm mb-1">{address.name}</p>

      {/* Address */}
      <p
        className={`text-sm leading-relaxed mb-1 ${
          isDark ? "text-zinc-400" : "text-gray-500"
        }`}
      >
        <MapPin size={13} className="inline mr-1 mb-0.5" />
        {address.fullAddress}, {address.city}, {address.state} —{" "}
        {address.pincode}
      </p>

      {/* Phone */}
      <p className={`text-sm ${isDark ? "text-zinc-400" : "text-gray-500"}`}>
        <Phone size={13} className="inline mr-1 mb-0.5" />
        {address.mobile}
      </p>

      {/* Actions — only shown in non-selectable (manage) mode */}
      {!selectable && (
        <div className="flex items-center gap-2 mt-3 pt-3 border-t border-inherit">
          {!address.isDefault && (
            <button
              onClick={() => onSetDefault(address.id)}
              disabled={isSettingDefault}
              className={`text-xs px-3 py-1.5 rounded-lg font-medium transition border ${
                isDark
                  ? "border-zinc-600 hover:bg-zinc-800 text-zinc-300"
                  : "border-gray-300 hover:bg-gray-50 text-gray-600"
              } disabled:opacity-50`}
            >
              {isSettingDefault ? "Setting..." : "Set as Default"}
            </button>
          )}
          <button
            onClick={() => onEdit(address)}
            className={`text-xs px-3 py-1.5 rounded-lg font-medium transition flex items-center gap-1 border ${
              isDark
                ? "border-zinc-600 hover:bg-zinc-800 text-zinc-300"
                : "border-gray-300 hover:bg-gray-50 text-gray-600"
            }`}
          >
            <Pencil size={12} /> Edit
          </button>
          <button
            onClick={() => onDelete(address.id)}
            disabled={isDeleting}
            className="text-xs px-3 py-1.5 rounded-lg font-medium transition flex items-center gap-1 border border-red-300 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 disabled:opacity-50"
          >
            <Trash2 size={12} /> {isDeleting ? "Deleting..." : "Delete"}
          </button>
        </div>
      )}
    </div>
  );
};

export default AddressCard;
