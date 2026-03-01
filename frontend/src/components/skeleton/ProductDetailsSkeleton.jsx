import { useTheme } from "../../context/ThemeContext";

const ProductDetailsSkeleton = () => {
  const { theme } = useTheme();

  return (
    <section className="max-w-7xl mx-auto px-4 py-12 animate-pulse">
      <div className="grid gap-10 md:grid-cols-2">
        {/* IMAGE */}
        <div
          className={`h-400px rounded-xl
            ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
          `}
        />

        {/* INFO */}
        <div className="space-y-5">
          <div
            className={`h-8 w-3/4 rounded
              ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
            `}
          />

          <div
            className={`h-4 w-1/3 rounded
              ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
            `}
          />

          <div className="flex gap-4">
            <div
              className={`h-6 w-24 rounded
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
              `}
            />
            <div
              className={`h-6 w-20 rounded
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
              `}
            />
          </div>

          <div className="space-y-3">
            <div
              className={`h-4 rounded
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
              `}
            />
            <div
              className={`h-4 rounded
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
              `}
            />
            <div
              className={`h-4 w-2/3 rounded
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}
              `}
            />
          </div>

          <div className="flex gap-4 mt-6">
            <div
              className={`h-12 w-40 rounded-lg
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-200"}
              `}
            />
            <div
              className={`h-12 w-40 rounded-lg
                ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-200"}
              `}
            />
          </div>
        </div>
      </div>
    </section>
  );
};

export default ProductDetailsSkeleton;
