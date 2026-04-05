import { ShoppingCart, Star, Heart } from "lucide-react";
import { useTheme } from "../../context/ThemeContext";
import { useNavigate, useLocation } from "react-router-dom";
import { useCart } from "../../context/CartContext";
import { useWishlist } from "../../context/WishlistContext";
import { useMemo, useState } from "react";

const ProductCard = ({ product }) => {
  const { theme } = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const { addToCart } = useCart();
  const { wishlistIds, toggleWishlist } = useWishlist();
  const [imgError, setImgError] = useState(false);

  if (!product) return null;

  const hasProductId = Boolean(product.productId);
  const isWishlisted = wishlistIds.has(product.productId);

  const imageUrl =
    product.productImageUrl && !imgError
      ? product.productImageUrl
      : "/no-image.png";

  const formattedPrice = useMemo(
    () =>
      new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0,
      }).format(product.discountedPrice || 0),
    [product.discountedPrice]
  );

  const formattedOriginalPrice = useMemo(
    () =>
      new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0,
      }).format(product.price || 0),
    [product.price]
  );

  const discountPercentage = useMemo(() => {
    if (!product.price || !product.discountedPrice) return null;
    return Math.round(
      ((product.price - product.discountedPrice) / product.price) * 100
    );
  }, [product.price, product.discountedPrice]);

  const handleNavigation = () => {
    if (hasProductId)
      navigate(`/product/${product.productId}`, { state: { from: location } });
  };

  const handleAddToCart = (e) => {
    e.stopPropagation();
    if (hasProductId) addToCart(product.productId);
  };

  const rating = product.rating ?? 4;
  const totalReviews = product.reviewCount ?? 124;

  return (
    <div
      onClick={handleNavigation}
      className={`group relative rounded-2xl cursor-pointer transition-all duration-300 overflow-hidden
        ${
          theme === "dark"
            ? "bg-[#111] hover:bg-[#161616] border border-gray-800/60 hover:border-gray-700"
            : "bg-white border border-gray-100 hover:border-gray-200 shadow-sm hover:shadow-lg"
        }`}
    >
      {/* IMAGE AREA */}
      <div
        className={`relative aspect-square overflow-hidden
        ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-50"}`}
      >
        <img
          src={imageUrl}
          alt={product.title}
          loading="lazy"
          onError={() => setImgError(true)}
          className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
        />

        {/* Discount badge */}
        {discountPercentage > 0 && (
          <span className="absolute top-2.5 left-2.5 bg-red-500 text-white text-[10px] px-2 py-0.5 rounded-full font-semibold tracking-wide shadow-sm">
            {discountPercentage}% OFF
          </span>
        )}

        {/* Wishlist button — always visible on mobile, hover on desktop */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            toggleWishlist(product.productId);
          }}
          className={`absolute top-2.5 right-2.5 p-2 rounded-full transition-all duration-200
            shadow-sm md:opacity-0 md:group-hover:opacity-100 md:scale-90 md:group-hover:scale-100
            ${
              isWishlisted
                ? "bg-red-50 dark:bg-red-900/30"
                : theme === "dark"
                ? "bg-black/50 hover:bg-black/70"
                : "bg-white/90 hover:bg-white"
            }`}
        >
          <Heart
            size={14}
            className={
              isWishlisted
                ? "fill-red-500 text-red-500"
                : theme === "dark"
                ? "text-gray-300"
                : "text-gray-500"
            }
          />
        </button>
      </div>

      {/* CONTENT AREA */}
      <div className="p-3.5">
        {/* Title */}
        <h3
          className={`font-medium text-sm mb-2 line-clamp-2 min-h-[40px] leading-snug
          ${theme === "dark" ? "text-gray-100" : "text-gray-800"}`}
        >
          {product.title}
        </h3>

        {/* Rating */}
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-0.5">
            {[1, 2, 3, 4, 5].map((star) => (
              <Star
                key={star}
                size={12}
                className={
                  star <= rating
                    ? "text-amber-400 fill-amber-400"
                    : theme === "dark"
                    ? "text-gray-700"
                    : "text-gray-200"
                }
              />
            ))}
            <span
              className={`text-[11px] ml-1.5 ${
                theme === "dark" ? "text-gray-500" : "text-gray-400"
              }`}
            >
              ({totalReviews})
            </span>
          </div>
        </div>

        {/* Price row */}
        <div className="flex items-baseline gap-2 mb-3.5">
          <span
            className={`font-bold text-base ${
              theme === "dark" ? "text-white" : "text-gray-900"
            }`}
          >
            {formattedPrice}
          </span>
          {product.price > product.discountedPrice && (
            <span
              className={`line-through text-xs ${
                theme === "dark" ? "text-gray-600" : "text-gray-400"
              }`}
            >
              {formattedOriginalPrice}
            </span>
          )}
        </div>

        {/* Add to Cart */}
        <button
          onClick={handleAddToCart}
          disabled={!hasProductId}
          className={`w-full flex items-center justify-center gap-2
            py-2.5 rounded-xl text-xs font-semibold tracking-wide
            transition-all duration-200 active:scale-[0.97]
            ${
              !hasProductId
                ? "opacity-50 cursor-not-allowed bg-gray-300 text-gray-500"
                : "bg-blue-600 hover:bg-blue-700 text-white shadow-sm hover:shadow-md"
            }`}
        >
          <ShoppingCart size={13} />
          Add to Cart
        </button>
      </div>
    </div>
  );
};

export default ProductCard;
