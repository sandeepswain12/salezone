import { useTheme } from "../../context/ThemeContext";

const ProductGridSkeleton = () => {
  const { theme } = useTheme();

  return (
    <section className="max-w-7xl mx-auto px-4 py-6 sm:py-12">
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 sm:gap-6">
        {Array.from({ length: 4 }).map((_, index) => (
          <div
            key={index}
            className={`rounded-xl p-4 animate-pulse
              ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"}
            `}
          >
            {/* IMAGE */}
            <div
              className={`aspect-square mb-4 rounded-lg
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-200"}
              `}
            />

            {/* TITLE */}
            <div
              className={`h-4 w-3/4 mb-2 rounded
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-200"}
              `}
            />

            {/* PRICE */}
            <div className="flex gap-2 mb-4">
              <div
                className={`h-4 w-12 rounded
                  ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-200"}
                `}
              />
              <div
                className={`h-4 w-10 rounded
                  ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-200"}
                `}
              />
            </div>

            {/* BUTTON */}
            <div
              className={`h-9 rounded-lg
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-300"}
              `}
            />
          </div>
        ))}
      </div>
    </section>
  );
};

export default ProductGridSkeleton;
