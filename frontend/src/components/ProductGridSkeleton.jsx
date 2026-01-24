import { useTheme } from "../context/ThemeContext";

const ProductGridSkeleton = () => {
  const { theme } = useTheme();

  return (
    <section className="max-w-7xl mx-auto px-4 py-12 animate-pulse">
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
        {Array.from({ length: 8 }).map((_, i) => (
          <div
            key={i}
            className={`rounded-xl p-4
              ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow"}
            `}
          >
            <div
              className={`aspect-square rounded-lg mb-4
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
              `}
            />

            <div
              className={`h-4 rounded mb-2
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
              `}
            />

            <div
              className={`h-4 w-1/2 rounded mb-4
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
              `}
            />

            <div
              className={`h-10 rounded-lg
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-200"}
              `}
            />
          </div>
        ))}
      </div>
    </section>
  );
};

export default ProductGridSkeleton;
