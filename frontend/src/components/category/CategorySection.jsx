import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCategories } from "../../services/CategoryService";
import { useTheme } from "../../context/ThemeContext";
import CategorySkeleton from "../skeleton/CategorySkeleton";
import {
  Book,
  Laptop,
  Shirt,
  Home,
  Dumbbell,
  Car,
  Sparkles,
  ShoppingCart,
  Gamepad2,
  Apple,
} from "lucide-react";

/* ---------------------------------- */
/* 🔥 Dynamic Icon Resolver */
/* ---------------------------------- */
const iconMap = [
  { keywords: ["book"], icon: Book },
  { keywords: ["elect", "laptop", "mobile"], icon: Laptop },
  { keywords: ["fashion", "cloth", "men", "women"], icon: Shirt },
  { keywords: ["home", "decor"], icon: Home },
  { keywords: ["sport", "fit"], icon: Dumbbell },
  { keywords: ["auto", "car", "vehicle"], icon: Car },
  { keywords: ["beauty", "care", "cosmetic"], icon: Sparkles },
  { keywords: ["grocery", "food"], icon: Apple },
  { keywords: ["toy", "game"], icon: Gamepad2 },
];

const resolveIcon = (title = "") => {
  const lower = title.toLowerCase();

  for (let item of iconMap) {
    if (item.keywords.some((word) => lower.includes(word))) {
      return item.icon;
    }
  }

  return ShoppingCart;
};

/* ---------------------------------- */
/* 🎨 Deterministic Gradient Generator */
/* ---------------------------------- */
const gradients = [
  "from-blue-500 to-indigo-600",
  "from-purple-500 to-pink-500",
  "from-emerald-500 to-teal-600",
  "from-orange-500 to-red-500",
  "from-cyan-500 to-blue-500",
  "from-rose-500 to-pink-600",
  "from-amber-500 to-orange-600",
  "from-violet-500 to-indigo-500",
  "from-lime-500 to-emerald-500",
];

const getGradient = (title = "") => {
  let hash = 0;

  for (let i = 0; i < title.length; i++) {
    hash = title.charCodeAt(i) + ((hash << 5) - hash);
  }

  const index = Math.abs(hash) % gradients.length;

  return gradients[index];
};

const CategorySection = () => {
  const { theme } = useTheme();
  const navigate = useNavigate();

  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await getCategories({ pageSize: 5 });
        setCategories(data?.content || []);
      } catch (err) {
        console.error("Failed to load categories", err);
        setError(true);
      } finally {
        setLoading(false);
      }
    };

    fetchCategories();
  }, []);

  return (
    <section className="max-w-7xl mx-auto px-4 py-14">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <h2 className="text-2xl font-bold tracking-tight">Shop by Category</h2>
      </div>

      {/* Scroll Container */}
      <div
        className={`
          flex gap-5 overflow-x-auto overflow-y-visible
          pb-6 pt-2
          scroll-smooth snap-x snap-mandatory
          custom-scrollbar
          ${theme === "dark" ? "scrollbar-dark" : "scrollbar-light"}
        `}
      >
        {/* 🔥 Loading Skeleton */}
        {loading &&
          Array.from({ length: 5 }).map((_, index) => (
            <CategorySkeleton key={index} theme={theme} />
          ))}

        {/* ❌ Error State */}
        {!loading && error && (
          <div className="text-sm text-red-500">Failed to load categories.</div>
        )}

        {/* ✅ Categories */}
        {!loading &&
          !error &&
          categories.map((category) => {
            const Icon = resolveIcon(category.title);
            const gradient = getGradient(category.title);

            return (
              <button
                key={category.categoryId}
                onClick={() => navigate(`/category/${category.categoryId}`)}
                className={`
                  snap-start shrink-0
                  w-[150px] sm:w-[170px] md:w-[180px]
                  rounded-2xl p-6 text-center
                  transition-all duration-300
                  ${
                    theme === "dark"
                      ? "bg-zinc-900 border border-zinc-800 hover:bg-zinc-800"
                      : "bg-white shadow-sm hover:shadow-xl"
                  }
                  hover:scale-[1.03]
                  focus:outline-none focus:ring-2 focus:ring-blue-500
                `}
              >
                {/* Icon */}
                <div
                  className={`
                    w-14 h-14 mx-auto mb-4
                    rounded-xl flex items-center justify-center
                    bg-gradient-to-br ${gradient}
                    text-white shadow-md
                    transition-transform duration-300
                  `}
                >
                  <Icon size={24} />
                </div>

                <h3 className="text-sm font-semibold tracking-wide">
                  {category.title}
                </h3>
              </button>
            );
          })}
      </div>
    </section>
  );
};

export default CategorySection;
