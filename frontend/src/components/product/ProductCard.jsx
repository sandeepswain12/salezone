import { ShoppingCart } from "lucide-react";
import { useTheme } from "../../context/ThemeContext";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../context/CartContext";

const ProductCard = ({ product }) => {
  const { theme } = useTheme();
  const navigate = useNavigate();
  const { addToCart } = useCart();

  const imageUrl = product.productId
    ? `http://localhost:8089/salezone/ecom/products/image/${product.productId}`
    : "/no-image.png";
  return (
    <div
      onClick={() => navigate(`/product/${product.productId}`)}
      className={`rounded-xl p-4 cursor-pointer transition
        ${
          theme === "dark"
            ? "bg-[#0f0f0f] hover:bg-[#151515]"
            : "bg-white shadow-sm hover:shadow-lg"
        }
      `}
    >
      {/* IMAGE */}
      <div className="aspect-square mb-4 overflow-hidden rounded-lg bg-gray-100 dark:bg-[#1a1a1a]">
        <img
          src={imageUrl}
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
        <span className="font-bold text-sm">₹{product.discountedPrice}</span>
        <span className="line-through text-xs opacity-60">
          ₹{product.price}
        </span>
      </div>

      {/* BUTTON */}
      <button
        onClick={(e) => {
          e.stopPropagation();
          addToCart(product);
        }}
        className="w-full flex items-center justify-center gap-2
          py-2.5 rounded-lg text-xs font-medium
          bg-blue-600 text-white hover:bg-blue-700 transition"
      >
        <ShoppingCart size={15} />
        Add to Cart
      </button>
    </div>
  );
};

export default ProductCard;
