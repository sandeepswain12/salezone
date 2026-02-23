import { useContext } from "react";
import { ThemeContext } from "../../context/ThemeContext";

const ThemeToggle = () => {
  const { theme, setTheme } = useContext(ThemeContext);

  return (
    <div
      onClick={() => setTheme(theme === "light" ? "dark" : "light")}
      className="
        w-14 h-7
        rounded-full
        bg-gray-300 dark:bg-blue-600
        cursor-pointer
        p-1
        transition-colors
      "
    >
      <div
        className={`
          w-5 h-5 bg-white rounded-full shadow
          transition-transform
          ${theme === "dark" ? "translate-x-7" : ""}
        `}
      />
    </div>
  );
};

export default ThemeToggle;
