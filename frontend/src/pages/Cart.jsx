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
              className={`flex gap-4 p-4 rounded-xl ${
                theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"
              }`}
            >
              <img
                src={`http://localhost:8089/salezone/ecom/products/image/${item.product.productId}`}
                alt={item.product.title}
                className="w-24 h-24 object-cover rounded-lg"
              />

              <div className="flex-1">
                <h3 className="font-semibold">{item.product.title}</h3>

                <p className="font-bold mt-1">
                  ₹{item.product.discountedPrice}
                </p>

                <div className="flex items-center gap-3 mt-4">
                  <button
                    onClick={() =>
                      item.quantity > 1 &&
                      updateQuantity(item.cartItemId, item.quantity - 1)
                    }
                    className="p-2 rounded border hover:bg-gray-100 dark:hover:bg-gray-800 transition"
                  >
                    <Minus size={16} />
                  </button>

                  <span>{item.quantity}</span>

                  <button
                    onClick={() =>
                      updateQuantity(item.cartItemId, item.quantity + 1)
                    }
                    className="p-2 rounded border hover:bg-gray-100 dark:hover:bg-gray-800 transition"
                  >
                    <Plus size={16} />
                  </button>
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
            <span>{cartItems.length}</span>
          </div>

          <div className="flex justify-between mb-2">
            <span>Subtotal</span>
            <span>₹{totalAmount}</span>
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
            className="w-full py-3 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition"
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
