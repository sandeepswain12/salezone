import { CheckCircle2, XCircle, Info, X } from "lucide-react";
import { useTheme } from "../../context/ThemeContext";

const Toast = ({ id, message, type = "success", onClose }) => {
  const { theme } = useTheme();

  const config = {
    success: {
      icon: CheckCircle2,
      color: "text-emerald-500",
      progress: "bg-emerald-500",
    },
    error: {
      icon: XCircle,
      color: "text-red-500",
      progress: "bg-red-500",
    },
    info: {
      icon: Info,
      color: "text-blue-500",
      progress: "bg-blue-500",
    },
  };

  const { icon: Icon, color, progress } = config[type];

  const isDark = theme === "dark";

  return (
    <div
      className={`relative w-[360px] overflow-hidden rounded-2xl border shadow-xl animate-toast-in
        ${
          isDark
            ? "bg-neutral-900 border-neutral-700 text-white"
            : "bg-white border-neutral-200 text-neutral-800"
        }
      `}
    >
      <div className="flex items-start gap-3 p-4">
        {/* Icon */}
        <div className={color}>
          <Icon size={20} />
        </div>

        {/* Message */}
        <div className="flex-1 text-sm font-medium">{message}</div>

        {/* Close */}
        <button
          onClick={() => onClose(id)}
          className={`transition ${
            isDark
              ? "text-neutral-400 hover:text-white"
              : "text-neutral-400 hover:text-black"
          }`}
        >
          <X size={16} />
        </button>
      </div>

      {/* Progress Bar */}
      <div
        className={`absolute bottom-0 left-0 h-1 w-full ${
          isDark ? "bg-neutral-800" : "bg-neutral-200"
        }`}
      >
        <div className={`h-full ${progress} animate-progress`} />
      </div>
    </div>
  );
};

export default Toast;
