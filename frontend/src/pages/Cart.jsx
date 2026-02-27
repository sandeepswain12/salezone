import { Trash2, Plus, Minus } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useState } from "react";

const Cart = () => {
  const { theme } = useTheme();
  const navigate = useNavigate();
  const {
    cartItems,
    removeItem,
    updateQuantity,
    totalAmount,
    loading,
    clearCart,
  } = useCart();

  const [showConfirm, setShowConfirm] = useState(false);
  const [clearing, setClearing] = useState(false);

  const handleClearCart = async () => {
    try {
      setClearing(true);
      await clearCart();
      setShowConfirm(false);
    } finally {
      setClearing(false);
    }
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <p>Loading cart...</p>
      </div>
    );
  }

  if (!cartItems || cartItems.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <h2 className="text-2xl font-bold mb-3">Your cart is empty 🛒</h2>
        <p className="opacity-70 mb-6">Start shopping to add items</p>
        <button
          onClick={() => navigate("/")}
          className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          Go Shopping
        </button>
      </div>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-4 py-12 relative">
      {/* HEADER */}
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Shopping Cart</h1>

        <button
          onClick={() => setShowConfirm(true)}
          className="text-red-500 hover:text-red-700 font-medium transition"
        >
          Clear Cart
        </button>
      </div>

      <div className="grid gap-8 md:grid-cols-3">
        {/* CART ITEMS */}
        <div className="md:col-span-2 space-y-6">
          {cartItems.map((item) => (
            <div
              key={item.cartItemId}
              className={`flex gap-5 p-5 rounded-2xl transition-all duration-200 
${
  theme === "dark"
    ? "bg-[#121212] border border-gray-800"
    : "bg-white shadow-md border border-gray-100"
}`}
            >
              <img
                src={`http://localhost:8089/salezone/ecom/products/image/${item.product.productId}`}
                alt={item.product.title}
                className="w-24 h-24 object-cover rounded-lg"
              />

              <div className="flex-1">
                <h3 className="font-semibold">{item.product.title}</h3>

                <p className="mt-2">
                  <span className="text-lg font-bold">
                    ₹{item.product.discountedPrice.toLocaleString("en-IN")}
                  </span>
                  <span className="ml-2 text-sm line-through opacity-60">
                    ₹{item.product.price.toLocaleString("en-IN")}
                  </span>
                </p>

                <div className="mt-5">
                  <div
                    className={`inline-flex items-center rounded-lg border transition-all 
    ${
      theme === "dark"
        ? "border-gray-700 bg-[#1a1a1a]"
        : "border-gray-300 bg-gray-50"
    }`}
                  >
                    {/* Minus Button */}
                    <button
                      onClick={() =>
                        updateQuantity(item.cartItemId, item.quantity - 1)
                      }
                      disabled={item.quantity <= 1}
                      className={`px-3 py-2 transition-all 
      ${
        theme === "dark"
          ? "hover:bg-gray-700 active:bg-gray-600"
          : "hover:bg-gray-200 active:bg-gray-300"
      } disabled:opacity-40 disabled:cursor-not-allowed`}
                    >
                      <Minus size={16} />
                    </button>

                    {/* Quantity */}
                    <div
                      className={`px-5 text-sm font-semibold tracking-wide ${
                        theme === "dark" ? "text-white" : "text-gray-800"
                      }`}
                    >
                      {item.quantity}
                    </div>

                    {/* Plus Button */}
                    <button
                      onClick={() =>
                        updateQuantity(item.cartItemId, item.quantity + 1)
                      }
                      disabled={item.quantity >= item.product.quantity}
                      className={`px-3 py-2 transition-all 
      ${
        theme === "dark"
          ? "hover:bg-gray-700 active:bg-gray-600"
          : "hover:bg-gray-200 active:bg-gray-300"
      } disabled:opacity-40 disabled:cursor-not-allowed`}
                    >
                      <Plus size={16} />
                    </button>
                  </div>
                </div>
              </div>

              <button
                onClick={() => removeItem(item.cartItemId)}
                className="text-red-500 hover:text-red-700 transition"
              >
                <Trash2 />
              </button>
            </div>
          ))}
        </div>

        {/* SUMMARY */}
        <div
          className={`p-6 rounded-xl h-fit ${
            theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"
          }`}
        >
          <h3 className="text-xl font-semibold mb-4">Price Details</h3>

          <div className="flex justify-between mb-2">
            <span>Items</span>
            <span>{cartItems.reduce((t, i) => t + i.quantity, 0)}</span>
          </div>

          <div className="flex justify-between mb-2">
            <span>Subtotal</span>
            <span className="font-semibold">
              ₹{totalAmount.toLocaleString("en-IN")}
            </span>
          </div>

          <div className="flex justify-between mb-2">
            <span>Delivery</span>
            <span className="text-green-600">FREE</span>
          </div>

          <hr className="my-3 opacity-40" />

          <div className="flex justify-between font-bold text-lg mb-6">
            <span>Total</span>
            <span>₹{totalAmount}</span>
          </div>

          <button
            onClick={() => navigate("/checkout")}
            className="w-full py-3 rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600 
text-white font-semibold tracking-wide shadow-lg 
hover:scale-[1.02] active:scale-[0.98] transition-all duration-200"
          >
            Proceed to Checkout
          </button>
        </div>
      </div>

      {/* CONFIRM MODAL */}
      {showConfirm && (
        <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
          <div
            className={`w-[90%] max-w-md p-6 rounded-xl ${
              theme === "dark" ? "bg-[#1a1a1a]" : "bg-white"
            }`}
          >
            <h2 className="text-lg font-semibold mb-4">Clear Cart</h2>

            <p className="mb-6 opacity-70">
              Are you sure you want to remove all items from your cart?
            </p>

            <div className="flex justify-end gap-4">
              <button
                onClick={() => setShowConfirm(false)}
                disabled={clearing}
                className="px-4 py-2 rounded-lg border hover:bg-gray-100 dark:hover:bg-gray-800 transition"
              >
                Cancel
              </button>

              <button
                onClick={handleClearCart}
                disabled={clearing}
                className="px-4 py-2 rounded-lg bg-red-500 text-white hover:bg-red-600 transition disabled:opacity-50"
              >
                {clearing ? "Clearing..." : "Clear"}
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
};

export default Cart;
