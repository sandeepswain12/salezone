import { useNavigate } from "react-router-dom";
import { useTheme } from "../context/ThemeContext";
import { useWishlist } from "../context/WishlistContext";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { Heart, ShoppingCart } from "lucide-react";
import { useState, useEffect } from "react";
import wishlistService from "../services/wishlistService";

const Wishlist = () => {
  const { theme } = useTheme();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { toggleWishlist, wishlistIds } = useWishlist();
  const { addToCart } = useCart();

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isAuthenticated || !user?.userId) {
      setLoading(false);
      return;
    }
    const fetch = async () => {
      try {
        const data = await wishlistService.getWishlist(user.userId);
        setProducts(data.products);
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [user?.userId, isAuthenticated]);

  // Keep local list in sync when items are removed via toggle
  useEffect(() => {
    setProducts((prev) => prev.filter((p) => wishlistIds.has(p.productId)));
  }, [wishlistIds]);

  const formatCurrency = (amount) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(amount);

  const card = `rounded-2xl p-5 ${
    theme === "dark"
      ? "bg-[#111] border border-gray-800"
      : "bg-white shadow-sm border border-gray-100"
  }`;

  if (!isAuthenticated) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-24 text-center">
        <Heart size={48} className="mx-auto mb-4 opacity-30" />
        <h2 className="text-2xl font-bold mb-2">
          Sign in to view your wishlist
        </h2>
        <button
          onClick={() => navigate("/auth")}
          className="mt-4 px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition"
        >
          Sign In
        </button>
      </div>
    );
  }

  if (loading) {
    return (
      <section className="max-w-7xl mx-auto px-4 py-12 animate-pulse">
        <div
          className={`h-8 w-40 rounded mb-10 ${
            theme === "dark" ? "bg-gray-800" : "bg-gray-200"
          }`}
        />
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
          {[...Array(4)].map((_, i) => (
            <div
              key={i}
              className={`h-72 rounded-2xl ${
                theme === "dark" ? "bg-gray-800" : "bg-gray-200"
              }`}
            />
          ))}
        </div>
      </section>
    );
  }

  if (products.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-24 text-center">
        <Heart size={48} className="mx-auto mb-4 opacity-30" />
        <h2 className="text-2xl font-bold mb-2">Your wishlist is empty</h2>
        <p className="opacity-60 mb-6">
          Save items you love and come back to them anytime.
        </p>
        <button
          onClick={() => navigate("/")}
          className="px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition"
        >
          Explore Products
        </button>
      </div>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      <h1 className="text-3xl font-bold mb-10">
        My Wishlist ({products.length})
      </h1>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 sm:gap-6">
        {products.map((product) => {
          const discount = Math.round(
            ((product.price - product.discountedPrice) / product.price) * 100
          );
          return (
            <div key={product.productId} className={card}>
              {/* Image */}
              <div
                onClick={() => navigate(`/product/${product.productId}`)}
                className={`relative aspect-square mb-4 overflow-hidden rounded-xl cursor-pointer
                  ${theme === "dark" ? "bg-[#1a1a1a]" : "bg-gray-100"}`}
              >
                <img
                  src={product.productImageUrl || "/no-image.png"}
                  alt={product.title}
                  className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
                />
                {discount > 0 && (
                  <span className="absolute top-2 left-2 bg-red-500 text-white text-[10px] px-2 py-1 rounded-md font-semibold">
                    {discount}% OFF
                  </span>
                )}

                {/* Remove from wishlist */}
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    toggleWishlist(product.productId);
                  }}
                  className="absolute top-2 right-2 p-1.5 rounded-full bg-white/80 dark:bg-black/60 hover:scale-110 transition"
                >
                  <Heart size={16} className="text-red-500 fill-red-500" />
                </button>
              </div>

              {/* Title */}
              <h3 className="font-semibold text-sm mb-2 line-clamp-2 min-h-[40px]">
                {product.title}
              </h3>

              {/* Price */}
              <div className="flex items-center gap-2 mb-4">
                <span className="font-bold text-sm">
                  {formatCurrency(product.discountedPrice)}
                </span>
                {product.price > product.discountedPrice && (
                  <span className="line-through text-xs opacity-50">
                    {formatCurrency(product.price)}
                  </span>
                )}
              </div>

              {/* Add to Cart */}
              <button
                onClick={() => addToCart(product.productId)}
                disabled={product.quantity <= 0}
                className="w-full flex items-center justify-center gap-2 py-2.5 rounded-lg text-xs font-medium
                  bg-blue-600 text-white hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <ShoppingCart size={14} />
                {product.quantity <= 0 ? "Out of Stock" : "Add to Cart"}
              </button>
            </div>
          );
        })}
      </div>
    </section>
  );
};

export default Wishlist;
