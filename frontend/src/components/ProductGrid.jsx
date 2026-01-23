import { ShoppingCart } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useNavigate } from "react-router-dom";

const products = [
  {
    id: 1,
    name: "iPhone 15",
    price: "₹79,999",
    image:
      "https://images.unsplash.com/photo-1695048133142-1a20484d2568?q=80&w=600",
  },
  {
    id: 2,
    name: "Noise Smart Watch",
    price: "₹3,999",
    image:
      "https://images.unsplash.com/photo-1517433456452-f9633a875f6f?q=80&w=600",
  },
  {
    id: 3,
    name: "Nike Sneakers",
    price: "₹6,499",
    image:
      "https://images.unsplash.com/photo-1528701800489-20be3c8f67b4?q=80&w=600",
  },
  {
    id: 4,
    name: "Wireless Headphones",
    price: "₹2,999",
    image:
      "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=600",
  },
];

const ProductGrid = () => {
  const { theme } = useTheme(); // ✅ FIXED
  const navigate = useNavigate(); // ✅ FIXED

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      <h2 className="text-2xl font-bold mb-8">Trending Products</h2>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
        {products.map((product) => (
          <div
            key={product.id}
            onClick={() => navigate(`/product/${product.id}`)} // ✅ FIXED
            className={`rounded-xl p-4 transition-all cursor-pointer
              ${
                theme === "dark"
                  ? "bg-[#0f0f0f] hover:bg-[#151515]"
                  : "bg-white hover:shadow-lg"
              }
            `}
          >
            {/* IMAGE */}
            <div className="aspect-square mb-4 overflow-hidden rounded-lg">
              <img
                src={product.image}
                alt={product.name}
                className="w-full h-full object-cover hover:scale-105 transition"
              />
            </div>

            {/* INFO */}
            <h3 className="font-semibold text-sm mb-1">{product.name}</h3>
            <p className="font-bold mb-3">{product.price}</p>

            {/* ACTION */}
            <button
              onClick={(e) => e.stopPropagation()} // prevent navigation
              className="w-full flex items-center justify-center gap-2
                py-2 rounded-lg text-sm font-medium
                bg-blue-600 text-white hover:bg-blue-700 transition"
            >
              <ShoppingCart size={16} />
              Add to Cart
            </button>
          </div>
        ))}
      </div>
    </section>
  );
};

export default ProductGrid;
