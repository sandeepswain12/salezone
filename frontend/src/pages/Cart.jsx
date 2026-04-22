import {
  Trash2,
  Plus,
  Minus,
  AlertTriangle,
  ShoppingBag,
  ArrowRight,
  Tag,
} from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useState, useMemo, useEffect } from "react";
import CartSkeleton from "../components/skeleton/CartSkeleton";

const Cart = () => {
  const { theme } = useTheme();
  const navigate = useNavigate();
  const isDark = theme === "dark";

  const { cartItems, removeItem, updateQuantity, loading, clearCart } =
    useCart();

  const [showConfirm, setShowConfirm] = useState(false);
  const [clearing, setClearing] = useState(false);
  const [removingId, setRemovingId] = useState(null);

  const formatCurrency = (amount) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(amount);

  const totalItems = useMemo(
    () => cartItems?.reduce((t, i) => t + i.quantity, 0) || 0,
    [cartItems]
  );

  const originalTotal = useMemo(
    () => cartItems?.reduce((t, i) => t + i.quantity * i.product.price, 0) || 0,
    [cartItems]
  );

  const discountedTotal = useMemo(
    () =>
      cartItems?.reduce(
        (t, i) => t + i.quantity * i.product.discountedPrice,
        0
      ) || 0,
    [cartItems]
  );

  const totalDiscount = originalTotal - discountedTotal;
  const discountPercent =
    originalTotal > 0 ? Math.round((totalDiscount / originalTotal) * 100) : 0;

  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") setShowConfirm(false);
    };
    window.addEventListener("keydown", handleEsc);
    return () => window.removeEventListener("keydown", handleEsc);
  }, []);

  const handleClearCart = async () => {
    try {
      setClearing(true);
      await clearCart();
      setShowConfirm(false);
    } finally {
      setClearing(false);
    }
  };

  const handleRemove = async (cartItemId) => {
    setRemovingId(cartItemId);
    try {
      await removeItem(cartItemId);
    } finally {
      setRemovingId(null);
    }
  };

  if (loading) return <CartSkeleton />;

  // ── Empty State ──
  if (!cartItems || cartItems.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-24 flex flex-col items-center justify-center text-center gap-6">
        <div
          className={`w-24 h-24 rounded-full flex items-center justify-center ${
            isDark ? "bg-zinc-800" : "bg-gray-100"
          }`}
        >
          <ShoppingBag
            size={40}
            className={isDark ? "text-zinc-500" : "text-gray-400"}
          />
        </div>
        <div>
          <h2 className="text-3xl font-bold mb-2">Your cart is empty</h2>
          <p
            className={`text-base ${
              isDark ? "text-zinc-400" : "text-gray-500"
            }`}
          >
            Looks like you haven't added anything yet.
          </p>
        </div>
        <button
          onClick={() => navigate("/")}
          className="flex items-center gap-2 px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-semibold transition-all hover:scale-[1.02] active:scale-[0.98] shadow-md"
        >
          Continue Shopping <ArrowRight size={18} />
        </button>
      </div>
    );
  }

  const cardBase = isDark
    ? "bg-zinc-900 border border-zinc-800"
    : "bg-white border border-gray-100 shadow-sm";

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      {/* ── HEADER ── */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold">Shopping Cart</h1>
          <p
            className={`text-sm mt-1 ${
              isDark ? "text-zinc-400" : "text-gray-500"
            }`}
          >
            {totalItems} {totalItems === 1 ? "item" : "items"} in your cart
          </p>
        </div>
        <button
          onClick={() => setShowConfirm(true)}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-medium transition ${
            isDark
              ? "bg-red-950/40 text-red-400 hover:bg-red-950/70 border border-red-900/40"
              : "bg-red-50 text-red-600 hover:bg-red-100 border border-red-200"
          }`}
        >
          <Trash2 size={15} /> Clear All
        </button>
      </div>

      <div className="grid gap-8 lg:grid-cols-3">
        {/* ── CART ITEMS ── */}
        <div className="lg:col-span-2 space-y-4">
          {cartItems.map((item, index) => {
            const lineTotal = item.quantity * item.product.discountedPrice;
            const discountPct =
              item.product.price > item.product.discountedPrice
                ? Math.round(
                    ((item.product.price - item.product.discountedPrice) /
                      item.product.price) *
                      100
                  )
                : 0;
            const isRemoving = removingId === item.cartItemId;

            return (
              <div
                key={item.cartItemId}
                className={`rounded-2xl p-5 transition-all duration-300 ${cardBase} ${
                  isRemoving ? "opacity-50 scale-[0.99]" : "opacity-100"
                }`}
                style={{ animationDelay: `${index * 60}ms` }}
              >
                <div className="flex gap-4 md:gap-5">
                  {/* ── PRODUCT IMAGE ── */}
                  <div
                    onClick={() =>
                      navigate(`/product/${item.product.productId}`)
                    }
                    className={`w-24 h-24 md:w-28 md:h-28 flex-shrink-0 rounded-xl overflow-hidden cursor-pointer flex items-center justify-center transition hover:scale-105 ${
                      isDark
                        ? "bg-zinc-800 border border-zinc-700"
                        : "bg-gray-50 border border-gray-200"
                    }`}
                  >
                    <img
                      src={item.product.productImageUrl || "/no-image.png"}
                      alt={item.product.title}
                      className="max-h-full max-w-full object-contain p-2"
                    />
                  </div>

                  {/* ── CONTENT ── */}
                  <div className="flex-1 min-w-0">
                    {/* Title + Delete */}
                    <div className="flex items-start justify-between gap-2">
                      <h3
                        onClick={() =>
                          navigate(`/product/${item.product.productId}`)
                        }
                        className="font-semibold text-base md:text-lg leading-snug cursor-pointer hover:text-blue-500 transition line-clamp-2"
                      >
                        {item.product.title}
                      </h3>
                      <button
                        onClick={() => handleRemove(item.cartItemId)}
                        disabled={isRemoving}
                        className={`flex-shrink-0 p-1.5 rounded-lg transition ${
                          isDark
                            ? "text-zinc-500 hover:text-red-400 hover:bg-red-950/30"
                            : "text-gray-400 hover:text-red-500 hover:bg-red-50"
                        } disabled:opacity-40`}
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>

                    {/* Category badge */}
                    {item.product.category?.title && (
                      <span
                        className={`inline-block mt-1 text-xs px-2 py-0.5 rounded-full font-medium ${
                          isDark
                            ? "bg-zinc-700 text-zinc-300"
                            : "bg-gray-100 text-gray-500"
                        }`}
                      >
                        {item.product.category.title}
                      </span>
                    )}

                    {/* Price row */}
                    <div className="flex items-center gap-2 mt-2 flex-wrap">
                      <span className="text-lg font-bold">
                        {formatCurrency(item.product.discountedPrice)}
                      </span>
                      {discountPct > 0 && (
                        <>
                          <span
                            className={`text-sm line-through ${
                              isDark ? "text-zinc-500" : "text-gray-400"
                            }`}
                          >
                            {formatCurrency(item.product.price)}
                          </span>
                          <span className="text-xs font-semibold text-green-500 bg-green-500/10 px-2 py-0.5 rounded-full">
                            {discountPct}% OFF
                          </span>
                        </>
                      )}
                    </div>

                    {/* Quantity + Line total */}
                    <div className="flex items-center justify-between mt-3">
                      {/* Quantity stepper */}
                      <div
                        className={`flex items-center rounded-xl border overflow-hidden ${
                          isDark
                            ? "border-zinc-700 bg-zinc-800"
                            : "border-gray-200 bg-gray-50"
                        }`}
                      >
                        <button
                          onClick={() =>
                            updateQuantity(item.cartItemId, item.quantity - 1)
                          }
                          disabled={item.quantity <= 1}
                          className={`px-3 py-2 transition ${
                            isDark
                              ? "hover:bg-zinc-700 text-zinc-300 disabled:text-zinc-600"
                              : "hover:bg-gray-100 text-gray-600 disabled:text-gray-300"
                          } disabled:cursor-not-allowed`}
                        >
                          <Minus size={14} />
                        </button>
                        <span
                          className={`px-4 py-2 text-sm font-semibold min-w-[2.5rem] text-center border-x ${
                            isDark ? "border-zinc-700" : "border-gray-200"
                          }`}
                        >
                          {item.quantity}
                        </span>
                        <button
                          onClick={() =>
                            updateQuantity(item.cartItemId, item.quantity + 1)
                          }
                          disabled={item.quantity >= item.product.quantity}
                          className={`px-3 py-2 transition ${
                            isDark
                              ? "hover:bg-zinc-700 text-zinc-300 disabled:text-zinc-600"
                              : "hover:bg-gray-100 text-gray-600 disabled:text-gray-300"
                          } disabled:cursor-not-allowed`}
                        >
                          <Plus size={14} />
                        </button>
                      </div>

                      {/* Line total */}
                      <span className="font-bold text-base">
                        {formatCurrency(lineTotal)}
                      </span>
                    </div>

                    {/* Stock warning */}
                    {item.quantity >= item.product.quantity && (
                      <p className="text-xs text-amber-500 mt-2 flex items-center gap-1">
                        <AlertTriangle size={12} /> Max available quantity
                        reached
                      </p>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* ── ORDER SUMMARY ── */}
        <div className={`rounded-2xl p-6 sticky top-24 self-start ${cardBase}`}>
          <h3 className="text-lg font-bold mb-5">Order Summary</h3>

          {/* Savings highlight */}
          {totalDiscount > 0 && (
            <div className="flex items-center gap-2 mb-5 p-3 rounded-xl bg-green-500/10 border border-green-500/20">
              <Tag size={15} className="text-green-500 flex-shrink-0" />
              <p className="text-sm text-green-600 dark:text-green-400 font-medium">
                You're saving {formatCurrency(totalDiscount)} ({discountPercent}
                % off)
              </p>
            </div>
          )}

          {/* Price breakdown */}
          <div className="space-y-3 text-sm">
            <div className="flex justify-between">
              <span className={isDark ? "text-zinc-400" : "text-gray-500"}>
                Price ({totalItems} {totalItems === 1 ? "item" : "items"})
              </span>
              <span className="font-medium">
                {formatCurrency(originalTotal)}
              </span>
            </div>

            {totalDiscount > 0 && (
              <div className="flex justify-between text-green-500">
                <span>Discount</span>
                <span className="font-medium">
                  − {formatCurrency(totalDiscount)}
                </span>
              </div>
            )}

            <div className="flex justify-between">
              <span className={isDark ? "text-zinc-400" : "text-gray-500"}>
                Delivery
              </span>
              <span className="text-green-500 font-semibold">FREE</span>
            </div>
          </div>

          <div
            className={`my-5 border-t ${
              isDark ? "border-zinc-800" : "border-gray-100"
            }`}
          />

          {/* Total */}
          <div className="flex justify-between items-center mb-6">
            <span className="font-bold text-lg">Total</span>
            <div className="text-right">
              <p className="font-bold text-xl">
                {formatCurrency(discountedTotal)}
              </p>
              {totalDiscount > 0 && (
                <p
                  className={`text-xs mt-0.5 line-through ${
                    isDark ? "text-zinc-500" : "text-gray-400"
                  }`}
                >
                  {formatCurrency(originalTotal)}
                </p>
              )}
            </div>
          </div>

          {/* Checkout CTA */}
          <button
            onClick={() => navigate("/checkout")}
            className="w-full py-3.5 rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold shadow-md hover:shadow-blue-500/25 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 flex items-center justify-center gap-2"
          >
            Proceed to Checkout <ArrowRight size={17} />
          </button>

          <button
            onClick={() => navigate("/")}
            className={`w-full mt-3 py-3 rounded-xl text-sm font-medium transition border ${
              isDark
                ? "border-zinc-700 text-zinc-400 hover:bg-zinc-800"
                : "border-gray-200 text-gray-500 hover:bg-gray-50"
            }`}
          >
            Continue Shopping
          </button>

          {/* Trust badges */}
          <div
            className={`mt-5 pt-4 border-t flex items-center justify-center gap-4 text-xs ${
              isDark
                ? "border-zinc-800 text-zinc-500"
                : "border-gray-100 text-gray-400"
            }`}
          >
            <span>🔒 Secure Checkout</span>
            <span>·</span>
            <span>🚚 Free Delivery</span>
          </div>
        </div>
      </div>

      {/* ── CLEAR CART CONFIRMATION MODAL ── */}
      {showConfirm && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center p-4"
          style={{ background: "rgba(0,0,0,0.6)" }}
          onClick={() => setShowConfirm(false)}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className={`w-full max-w-sm rounded-2xl p-7 shadow-2xl border ${
              isDark
                ? "bg-zinc-900 border-zinc-800 text-white"
                : "bg-white border-gray-100 text-gray-900"
            }`}
          >
            <div
              className={`w-12 h-12 rounded-xl flex items-center justify-center mb-4 ${
                isDark ? "bg-red-950/50" : "bg-red-50"
              }`}
            >
              <AlertTriangle size={22} className="text-red-500" />
            </div>

            <h2 className="text-lg font-bold mb-2">Clear entire cart?</h2>
            <p
              className={`text-sm mb-6 ${
                isDark ? "text-zinc-400" : "text-gray-500"
              }`}
            >
              This will permanently remove all {totalItems}{" "}
              {totalItems === 1 ? "item" : "items"} from your cart. This action
              cannot be undone.
            </p>

            <div className="flex gap-3">
              <button
                onClick={() => setShowConfirm(false)}
                disabled={clearing}
                className={`flex-1 py-2.5 rounded-xl text-sm font-medium transition border ${
                  isDark
                    ? "border-zinc-700 hover:bg-zinc-800 text-zinc-300"
                    : "border-gray-200 hover:bg-gray-50 text-gray-700"
                }`}
              >
                Cancel
              </button>
              <button
                onClick={handleClearCart}
                disabled={clearing}
                className="flex-1 py-2.5 rounded-xl text-sm font-semibold bg-red-600 hover:bg-red-700 text-white transition disabled:opacity-50"
              >
                {clearing ? "Clearing..." : "Clear Cart"}
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
};

export default Cart;
