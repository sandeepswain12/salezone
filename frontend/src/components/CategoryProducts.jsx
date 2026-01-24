import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { ShoppingCart } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useTheme } from "../context/ThemeContext";
import { getProductsByCategory } from "../services/CategoryService";
import ProductGridSkeleton from "./ProductGridSkeleton";
import BackButton from "./BackButton";

const CategoryProducts = () => {
  const { categoryId } = useParams();
  const { theme } = useTheme();
  const navigate = useNavigate();

  const [products, setProducts] = useState([]);
  const [categoryTitle, setCategoryTitle] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);

    getProductsByCategory(categoryId)
      .then((data) => {
        setProducts(data.content);
        setCategoryTitle(data.content[0]?.category?.title || "");
      })
      .finally(() => setLoading(false));
  }, [categoryId]);

  if (loading) return <ProductGridSkeleton />;

  return (
    <section className="max-w-7xl mx-auto px-4 py-6">
      <BackButton label="Back" />
      <section className="max-w-7xl mx-auto px-4 py-10">
        {/* HEADER */}
        <h2 className="text-xl sm:text-2xl font-bold mb-8">{categoryTitle}</h2>

        {/* PRODUCTS GRID */}
        {products.length === 0 ? (
          <p className="text-center opacity-60">
            No products found in this category.
          </p>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
            {products.map((product) => (
              <div
                key={product.productId}
                onClick={() => navigate(`/product/${product.productId}`)}
                className={`rounded-xl p-4 cursor-pointer transition
                ${
                  theme === "dark"
                    ? "bg-[#0f0f0f] hover:bg-[#151515]"
                    : "bg-white hover:shadow-lg"
                }
              `}
              >
                {/* IMAGE */}
                <div className="aspect-square mb-4 overflow-hidden rounded-lg bg-gray-100 dark:bg-[#1a1a1a]">
                  <img
                    src={`http://localhost:8089/salezone/ecom/products/image/${product.productId}`}
                    alt={product.title}
                    loading="lazy"
                    className="w-full h-full object-cover"
                  />
                </div>

                {/* TITLE */}
                <h3 className="font-semibold text-sm mb-1 line-clamp-2">
                  {product.title}
                </h3>

                {/* PRICE */}
                <div className="flex items-center gap-2 mb-3">
                  <span className="font-bold">₹{product.discountedPrice}</span>
                  <span className="line-through text-sm opacity-60">
                    ₹{product.price}
                  </span>
                </div>

                {/* BUTTON */}
                <button
                  onClick={(e) => e.stopPropagation()}
                  className="w-full flex items-center justify-center gap-2
                  py-2 rounded-lg text-sm font-medium
                  bg-blue-600 text-white hover:bg-blue-700"
                >
                  <ShoppingCart size={16} />
                  Add to Cart
                </button>
              </div>
            ))}
          </div>
        )}
      </section>
    </section>
  );
};

export default CategoryProducts;
