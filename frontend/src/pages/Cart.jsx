import { Trash2, Plus, Minus, AlertTriangle } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useState, useMemo, useEffect } from "react";
import CartSkeleton from "../components/skeleton/CartSkeleton";

const Cart = () => {
  const { theme } = useTheme();
  const navigate = useNavigate();

  const { cartItems, removeItem, updateQuantity, loading, clearCart } =
    useCart();

  const [showConfirm, setShowConfirm] = useState(false);
  const [clearing, setClearing] = useState(false);

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

  // ESC key close
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

  if (loading) return <CartSkeleton />;

  if (!cartItems || cartItems.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-24 text-center">
        <h2 className="text-3xl font-bold mb-3">Your cart is empty 🛒</h2>
        <p className="opacity-70 mb-6">Start shopping to add items</p>
        <button
          onClick={() => navigate("/")}
          className="px-8 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition"
        >
          Continue Shopping
        </button>
      </div>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-4 py-12 relative overflow-x-hidden">
      {/* HEADER */}
      <div className="flex justify-between items-center mb-10">
        <h1 className="text-3xl font-bold">Shopping Cart ({totalItems})</h1>

        <button
          onClick={() => setShowConfirm(true)}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg transition
  ${
    theme === "dark"
      ? "bg-red-900/30 text-red-400 hover:bg-red-900/50"
      : "bg-red-100 text-red-700 hover:bg-red-200 border border-red-200"
  }
`}
        >
          <Trash2 size={16} />
          Clear Cart
        </button>
      </div>

      <div className="grid gap-10 md:grid-cols-3">
        {/* CART ITEMS */}
        <div className="md:col-span-2 space-y-6">
          {cartItems.map((item) => {
            const lineTotal = item.quantity * item.product.discountedPrice;

            return (
              <div
                key={item.cartItemId}
                className={`p-5 rounded-2xl
    ${
      theme === "dark"
        ? "bg-[#121212] border border-gray-800"
        : "bg-white shadow-sm border border-gray-100"
    }`}
              >
                {/* DESKTOP LAYOUT */}
                <div className="hidden md:flex gap-6">
                  {/* IMAGE */}
                  <div
                    onClick={() =>
                      navigate(`/product/${item.product.productId}`)
                    }
                    className={`w-28 h-28 flex-shrink-0 flex items-center justify-center rounded-xl cursor-pointer
        ${
          theme === "dark"
            ? "bg-[#1a1a1a] border border-gray-800"
            : "bg-gray-50 border border-gray-200"
        }
      `}
                  >
                    <img
                      src={item.product.productImageUrl || "/no-image.png"}
                      alt={item.product.title}
                      className="max-h-full max-w-full object-contain p-2"
                    />
                  </div>

                  {/* CONTENT */}
                  <div className="flex-1 flex flex-col justify-between">
                    <div>
                      <h3 className="font-semibold text-lg">
                        {item.product.title}
                      </h3>

                      <div className="mt-2 flex items-center gap-3">
                        <span className="text-lg font-bold">
                          {formatCurrency(item.product.discountedPrice)}
                        </span>

                        {item.product.price > item.product.discountedPrice && (
                          <>
                            <span className="text-sm line-through text-gray-500">
                              {formatCurrency(item.product.price)}
                            </span>
                            <span className="text-sm text-green-600 font-semibold">
                              {Math.round(
                                ((item.product.price -
                                  item.product.discountedPrice) /
                                  item.product.price) *
                                  100
                              )}
                              % OFF
                            </span>
                          </>
                        )}
                      </div>
                    </div>

                    <div className="flex items-center justify-between mt-4">
                      <div className="flex items-center border rounded-xl px-2">
                        <button
                          onClick={() =>
                            updateQuantity(item.cartItemId, item.quantity - 1)
                          }
                          disabled={item.quantity <= 1}
                          className="px-2 py-1"
                        >
                          <Minus size={16} />
                        </button>

                        <span className="px-4">{item.quantity}</span>

                        <button
                          onClick={() =>
                            updateQuantity(item.cartItemId, item.quantity + 1)
                          }
                          disabled={item.quantity >= item.product.quantity}
                          className="px-2 py-1"
                        >
                          <Plus size={16} />
                        </button>
                      </div>

                      <span className="font-semibold">
                        {formatCurrency(lineTotal)}
                      </span>
                    </div>
                  </div>

                  {/* DELETE */}
                  <button
                    onClick={() => removeItem(item.cartItemId)}
                    className="text-red-500 hover:text-red-700"
                  >
                    <Trash2 />
                  </button>
                </div>

                {/* MOBILE LAYOUT */}
                <div className="md:hidden">
                  <div className="flex gap-4">
                    {/* IMAGE */}
                    <div
                      onClick={() =>
                        navigate(`/product/${item.product.productId}`)
                      }
                      className="w-24 h-24 flex-shrink-0 flex items-center justify-center rounded-xl bg-gray-50 dark:bg-[#1a1a1a]"
                    >
                      <img
                        src={item.product.productImageUrl || "/no-image.png"}
                        alt={item.product.title}
                        className="max-h-full max-w-full object-contain p-2"
                      />
                    </div>

                    {/* RIGHT SIDE */}
                    <div className="flex-1">
                      <h3 className="font-semibold text-base">
                        {item.product.title}
                      </h3>

                      <div className="mt-1 flex items-center gap-2 flex-wrap">
                        <span className="font-bold">
                          {formatCurrency(item.product.discountedPrice)}
                        </span>

                        {item.product.price > item.product.discountedPrice && (
                          <>
                            <span className="text-xs line-through text-gray-500">
                              {formatCurrency(item.product.price)}
                            </span>
                            <span className="text-xs text-green-600 font-semibold">
                              {Math.round(
                                ((item.product.price -
                                  item.product.discountedPrice) /
                                  item.product.price) *
                                  100
                              )}
                              % OFF
                            </span>
                          </>
                        )}
                      </div>

                      {/* Quantity + Delete Row */}
                      <div className="flex items-center justify-between mt-3">
                        <div className="flex items-center border rounded-xl px-2">
                          <button
                            onClick={() =>
                              updateQuantity(item.cartItemId, item.quantity - 1)
                            }
                            disabled={item.quantity <= 1}
                            className="px-2 py-1"
                          >
                            <Minus size={14} />
                          </button>

                          <span className="px-3 text-sm">{item.quantity}</span>

                          <button
                            onClick={() =>
                              updateQuantity(item.cartItemId, item.quantity + 1)
                            }
                            disabled={item.quantity >= item.product.quantity}
                            className="px-2 py-1"
                          >
                            <Plus size={14} />
                          </button>
                        </div>

                        <span className="font-semibold text-sm">
                          {formatCurrency(lineTotal)}
                        </span>

                        <button
                          onClick={() => removeItem(item.cartItemId)}
                          className="text-red-500"
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* PROFESSIONAL ORDER SUMMARY */}
        <div
          className={`p-8 rounded-2xl sticky top-24
          ${
            theme === "dark"
              ? "bg-[#0f0f0f] border border-gray-800"
              : "bg-white shadow-md border border-gray-100"
          }`}
        >
          <h3 className="text-xl font-semibold mb-6">Order Summary</h3>

          <div className="flex justify-between mb-3">
            <span>Price ({totalItems} items)</span>
            <span>{formatCurrency(originalTotal)}</span>
          </div>

          <div className="flex justify-between mb-3 text-green-600">
            <span>Discount</span>
            <span>- {formatCurrency(totalDiscount)}</span>
          </div>

          <div className="flex justify-between mb-3">
            <span>Delivery</span>
            <span className="text-green-600 font-medium">FREE</span>
          </div>

          <hr className="my-5 opacity-40" />

          <div className="flex justify-between font-bold text-lg mb-2">
            <span>Total Amount</span>
            <span>{formatCurrency(discountedTotal)}</span>
          </div>

          {totalDiscount > 0 && (
            <div className="mt-3 text-sm text-green-600 font-medium">
              🎉 You saved {formatCurrency(totalDiscount)} on this order
            </div>
          )}

          <button
            onClick={() => navigate("/checkout")}
            className="mt-8 w-full py-4 rounded-xl
            bg-gradient-to-r from-blue-600 to-indigo-600
            text-white font-semibold shadow-lg
            hover:scale-[1.02]
            active:scale-[0.98]
            transition-all duration-200"
          >
            Proceed to Checkout
          </button>
        </div>
      </div>

      {/* CLEAR CART MODAL */}
      {showConfirm && (
        <div
          className="fixed inset-0 flex items-center justify-center bg-black/50 backdrop-blur-sm z-50"
          onClick={() => setShowConfirm(false)}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="w-[90%] max-w-md p-8 rounded-2xl bg-white dark:bg-[#1a1a1a] shadow-xl"
          >
            <div className="flex items-center gap-3 mb-4 text-red-600">
              <AlertTriangle />
              <h2 className="text-xl font-semibold">Clear Cart</h2>
            </div>

            <p className="mb-8 opacity-70">
              This will permanently remove all items from your cart.
            </p>

            <div className="flex justify-end gap-4">
              <button
                onClick={() => setShowConfirm(false)}
                disabled={clearing}
                className="px-5 py-2 rounded-lg border hover:bg-gray-100 dark:hover:bg-gray-800 transition"
              >
                Cancel
              </button>

              <button
                onClick={handleClearCart}
                disabled={clearing}
                className="px-5 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 transition disabled:opacity-50"
              >
                {clearing ? "Clearing..." : "Yes, Clear Cart"}
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
};

export default Cart;
