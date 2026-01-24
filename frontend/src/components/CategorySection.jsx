import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCategories } from "../services/CategoryService";
import { useTheme } from "../context/ThemeContext";
import { Book, Laptop, Shirt, Home, Dumbbell, Grid } from "lucide-react";

const categoryMeta = {
  Books: { icon: Book, color: "from-indigo-500 to-indigo-600" },
  Electronics: { icon: Laptop, color: "from-blue-500 to-blue-600" },
  Fashion: { icon: Shirt, color: "from-pink-500 to-pink-600" },
  "Home Appliances": { icon: Home, color: "from-emerald-500 to-emerald-600" },
  "Sports Fitness": { icon: Dumbbell, color: "from-orange-500 to-orange-600" },
};

const DefaultIcon = Grid;

const CategorySection = () => {
  const { theme } = useTheme();
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getCategories({ pageSize: 5 })
      .then((data) => setCategories(data.content || []))
      .finally(() => setLoading(false));
  }, []);

  return (
    <section className="max-w-7xl mx-auto px-3 sm:px-4 py-10">
      <h2 className="text-xl sm:text-2xl font-bold mb-6 text-center sm:text-left">
        Shop by Category
      </h2>
      {/* SKELETON */}
      {loading && (
        <div className="flex gap-4 overflow-x-auto sm:grid sm:grid-cols-3 md:grid-cols-5 sm:overflow-visible">
          {Array.from({ length: 5 }).map((_, i) => (
            <div
              key={i}
              className={`min-w-[140px] sm:min-w-0 rounded-xl p-4
          ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"}
        `}
            >
              {/* ICON SKELETON */}
              <div
                className={`w-12 h-12 mx-auto rounded-xl mb-3 animate-pulse
            ${theme === "dark" ? "bg-[#1f1f1f]" : "bg-gray-200"}
          `}
              />

              {/* TEXT SKELETON */}
              <div
                className={`h-3 w-3/4 mx-auto rounded animate-pulse
            ${theme === "dark" ? "bg-[#1f1f1f]" : "bg-gray-200"}
          `}
              />
            </div>
          ))}
        </div>
      )}

      {/* CATEGORY LIST */}
      {!loading && (
        <div className="flex gap-4 overflow-x-auto sm:grid sm:grid-cols-3 md:grid-cols-5 sm:overflow-visible">
          {categories.map((category) => {
            const Icon = categoryMeta[category.title]?.icon || DefaultIcon;
            const gradient =
              categoryMeta[category.title]?.color ||
              "from-gray-500 to-gray-600";

            return (
              <div
                key={category.categoryId}
                onClick={() => navigate(`/category/${category.categoryId}`)}
                className={`min-w-[140px] sm:min-w-0 cursor-pointer
                  rounded-xl p-4 flex flex-col items-center justify-center
                  transition
                  ${
                    theme === "dark"
                      ? "bg-[#0f0f0f] hover:bg-[#151515]"
                      : "bg-white shadow-sm hover:shadow-lg"
                  }
                `}
              >
                {/* ICON */}
                <div
                  className={`w-12 h-12 rounded-xl mb-3 flex items-center justify-center
                    bg-gradient-to-br ${gradient} text-white`}
                >
                  <Icon size={22} />
                </div>

                {/* TITLE */}
                <h3 className="text-sm font-semibold text-center">
                  {category.title}
                </h3>
              </div>
            );
          })}
        </div>
      )}
    </section>
  );
};

export default CategorySection;
