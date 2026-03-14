import { ShoppingCart, Star } from "lucide-react";
import { useTheme } from "../../context/ThemeContext";
import { useNavigate, useLocation } from "react-router-dom";
import { useCart } from "../../context/CartContext";
import { useMemo, useState } from "react";

const ProductCard = ({ product }) => {
  const { theme } = useTheme();
  const navigate = useNavigate();
  const location = useLocation(); // IMPORTANT
  const { addToCart } = useCart();
  const [imgError, setImgError] = useState(false);

  if (!product) return null;

  const hasProductId = Boolean(product.productId);

  const imageUrl =
    product.productImageUrl && !imgError
      ? product.productImageUrl
      : "/no-image.png";

  const formattedPrice = useMemo(() => {
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(product.discountedPrice || 0);
  }, [product.discountedPrice]);

  const formattedOriginalPrice = useMemo(() => {
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(product.price || 0);
  }, [product.price]);

  const discountPercentage = useMemo(() => {
    if (!product.price || !product.discountedPrice) return null;
    const discount =
      ((product.price - product.discountedPrice) / product.price) * 100;
    return Math.round(discount);
  }, [product.price, product.discountedPrice]);

  const handleNavigation = () => {
    if (hasProductId) {
      navigate(`/product/${product.productId}`, {
        state: { from: location }, // CRITICAL
      });
    }
  };

  const handleAddToCart = (e) => {
    e.stopPropagation();
    if (hasProductId) {
      addToCart(product.productId);
    }
  };

  const rating = product.rating ?? 4;
  const totalReviews = product.reviewCount ?? 124;

  return (
    <div
      onClick={handleNavigation}
      className={`group rounded-2xl p-4 cursor-pointer transition-all duration-300
        ${
          theme === "dark"
            ? "bg-[#111] hover:bg-[#1a1a1a]"
            : "bg-white shadow-sm hover:shadow-xl"
        }
        hover:-translate-y-1`}
    >
      <div className="relative aspect-square mb-4 overflow-hidden rounded-xl bg-gray-100 dark:bg-[#1a1a1a]">
        <img
          src={imageUrl}
          alt={product.title}
          loading="lazy"
          onError={() => setImgError(true)}
          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
        />

        {discountPercentage > 0 && (
          <span className="absolute top-2 left-2 bg-red-500 text-white text-[10px] px-2 py-1 rounded-md font-semibold shadow-md">
            {discountPercentage}% OFF
          </span>
        )}
      </div>

      <h3 className="font-semibold text-sm mb-1 line-clamp-2 min-h-[40px]">
        {product.title}
      </h3>

      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-1">
          {[1, 2, 3, 4, 5].map((star) => (
            <Star
              key={star}
              size={14}
              className={
                star <= rating
                  ? "text-yellow-500 fill-yellow-500"
                  : "text-gray-300"
              }
            />
          ))}
        </div>
        <span className="text-[11px] text-gray-500">
          {totalReviews} reviews
        </span>
      </div>

      <div className="flex items-center gap-2 mb-4">
        <span className="font-bold text-sm">{formattedPrice}</span>
        {product.price > product.discountedPrice && (
          <span className="line-through text-xs opacity-60">
            {formattedOriginalPrice}
          </span>
        )}
      </div>

      <button
        onClick={handleAddToCart}
        disabled={!hasProductId}
        className="w-full flex items-center justify-center gap-2
          py-2.5 rounded-lg text-xs font-medium
          bg-blue-600 text-white hover:bg-blue-700
          transition-all duration-200
          disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <ShoppingCart size={15} />
        Add to Cart
      </button>
    </div>
  );
};

export default ProductCard;
