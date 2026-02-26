import { useTheme } from "../context/ThemeContext";
import { useState } from "react";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import orderService from "../services/orderService";
import { useNavigate } from "react-router-dom";

const Checkout = () => {
  const { theme } = useTheme();
  const { totalAmount, cartItems, cartId, clearCart } = useCart();
  const { user } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const [address, setAddress] = useState({
    name: "",
    phone: "",
    street: "",
    city: "",
    state: "",
    pincode: "",
  });

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setAddress({ ...address, [e.target.name]: e.target.value });
  };

  const subtotal = totalAmount;
  const delivery = 0;
  const total = subtotal + delivery;

  const handlePlaceOrder = async () => {
    if (!cartItems.length) {
      showToast("Cart is empty", "error");
      return;
    }

    if (!cartId) {
      showToast("Cart not found. Please refresh.", "error");
      return;
    }

    if (!address.name || !address.phone || !address.street) {
      showToast("Please fill all required fields", "error");
      return;
    }

    try {
      setLoading(true);

      const orderData = {
        cartId: cartId, // ✅ correct cartId
        userId: user.userId,
        billingName: address.name,
        billingPhone: address.phone,
        billingAddress: `${address.street}, ${address.city}, ${address.state}, ${address.pincode}`,
      };

      await orderService.createOrder(orderData);

      await clearCart();

      showToast("Order placed successfully 🎉", "success");

      navigate("/orders");
    } catch (error) {
      console.error("Order creation failed:", error);
      showToast("Failed to place order", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      <h1 className="text-3xl font-bold mb-8">Checkout</h1>

      <div className="grid gap-10 md:grid-cols-3">
        {/* LEFT */}
        <div className="md:col-span-2 space-y-8">
          {/* SHIPPING */}
          <div
            className={`p-6 rounded-xl ${
              theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"
            }`}
          >
            <h2 className="text-xl font-semibold mb-4">Shipping Address</h2>

            <div className="grid gap-4 sm:grid-cols-2">
              {["name", "phone", "city", "state", "pincode"].map((field) => (
                <input
                  key={field}
                  name={field}
                  placeholder={field.charAt(0).toUpperCase() + field.slice(1)}
                  value={address[field]}
                  onChange={handleChange}
                  className={`p-3 rounded border outline-none ${
                    theme === "dark" ? "bg-black border-gray-700" : "bg-white"
                  }`}
                />
              ))}

              <input
                name="street"
                placeholder="Street Address"
                value={address.street}
                onChange={handleChange}
                className={`p-3 rounded border outline-none sm:col-span-2 ${
                  theme === "dark" ? "bg-black border-gray-700" : "bg-white"
                }`}
              />
            </div>
          </div>

          {/* PAYMENT */}
          <div
            className={`p-6 rounded-xl ${
              theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"
            }`}
          >
            <h2 className="text-xl font-semibold mb-4">Payment Method</h2>

            <label className="flex items-center gap-3">
              <input type="radio" defaultChecked />
              <span>Cash on Delivery</span>
            </label>
          </div>
        </div>

        {/* RIGHT - SUMMARY */}
        <div
          className={`p-6 rounded-xl h-fit ${
            theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"
          }`}
        >
          <h3 className="text-xl font-semibold mb-4">Order Summary</h3>

          <div className="flex justify-between mb-2">
            <span>Subtotal</span>
            <span>₹{subtotal}</span>
          </div>

          <div className="flex justify-between mb-2">
            <span>Delivery</span>
            <span className="text-green-600">FREE</span>
          </div>

          <hr className="my-3 opacity-40" />

          <div className="flex justify-between font-bold text-lg mb-6">
            <span>Total</span>
            <span>₹{total}</span>
          </div>

          <button
            onClick={handlePlaceOrder}
            disabled={loading}
            className="w-full py-3 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition disabled:opacity-50"
          >
            {loading ? "Placing Order..." : "Place Order"}
          </button>
        </div>
      </div>
    </section>
  );
};

export default Checkout;
