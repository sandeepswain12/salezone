import { ArrowLeft } from "lucide-react";
import { useNavigate, useLocation } from "react-router-dom";
import { useTheme } from "../../context/ThemeContext";

const BackButton = ({ label = "Back" }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { theme } = useTheme();

  const handleBack = () => {
    if (location.state?.from) {
      navigate(location.state.from.pathname, {
        state: location.state.from.state,
      });
    } else {
      navigate(-1);
    }
  };

  return (
    <button
      onClick={handleBack}
      className={`flex items-center gap-2 px-4 py-2 rounded-lg
        text-sm font-medium transition
        ${
          theme === "dark"
            ? "bg-[#0f0f0f] text-white hover:bg-[#1a1a1a]"
            : "bg-gray-100 text-black hover:bg-gray-200"
        }
      `}
    >
      <ArrowLeft size={18} />
      {label}
    </button>
  );
};

export default BackButton;
