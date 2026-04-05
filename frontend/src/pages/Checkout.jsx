import { useTheme } from "../context/ThemeContext";
import { useState, useMemo } from "react";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import orderService from "../services/orderService";
import { useNavigate } from "react-router-dom";
import loadRazorpay from "../utils/loadRazorpay";

const Checkout = () => {
  const { theme } = useTheme();
  const { cartItems, cartId, clearCart } = useCart();
  const { user } = useAuth();
  const { showToast } = useToast();
  const [paymentMethod, setPaymentMethod] = useState("COD");
  const [processingPayment, setProcessingPayment] = useState(false);
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

  const formatCurrency = (amount) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(amount);

  const totalItems = useMemo(
    () => cartItems.reduce((t, i) => t + i.quantity, 0),
    [cartItems]
  );

  const originalTotal = useMemo(
    () => cartItems.reduce((t, i) => t + i.quantity * i.product.price, 0),
    [cartItems]
  );

  const discountedTotal = useMemo(
    () =>
      cartItems.reduce((t, i) => t + i.quantity * i.product.discountedPrice, 0),
    [cartItems]
  );

  const totalDiscount = originalTotal - discountedTotal;

  const handleChange = (e) => {
    setAddress({ ...address, [e.target.name]: e.target.value });
  };

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
        cartId,
        userId: user.userId,
        billingName: address.name,
        billingPhone: address.phone,
        billingAddress: `${address.street}, ${address.city}, ${address.state}, ${address.pincode}`,
        paymentMethod,
      };

      const order = await orderService.createOrder(orderData);

      if (paymentMethod === "COD") {
        await clearCart();
        showToast("Order placed successfully", "success");
        navigate("/orders");
        return;
      }

      const razorpayData = await orderService.initiatePayment(order.orderId);

      const sdkLoaded = await loadRazorpay();

      if (!sdkLoaded) {
        showToast("Failed to load Razorpay SDK", "error");
        return;
      }

      const options = {
        key: import.meta.env.VITE_RAZORPAY_KEY,
        amount: razorpayData.amount * 100,
        currency: "INR",
        order_id: razorpayData.razorpayOrderId,

        handler: async function (response) {
          try {
            setProcessingPayment(true); // show loader
            window.scrollTo({ top: 0, behavior: "smooth" });

            await orderService.capturePayment(order.orderId, {
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpayPaymentSignature: response.razorpay_signature,
            });

            await clearCart();

            showToast("Order placed successfully", "success");

            navigate("/orders");
          } catch (err) {
            console.error(err);
            showToast("Payment verification failed", "error");
          } finally {
            setProcessingPayment(false);
          }
        },

        modal: {
          ondismiss: function () {
            showToast("Payment cancelled", "info");
          },
        },
      };

      const razorpay = new window.Razorpay(options);
      razorpay.open();
    } catch (error) {
      console.error(error);
      showToast("Failed to place order", "error");
    } finally {
      setLoading(false);
    }
  };
  if (processingPayment) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-lg font-semibold">Redirecting...</p>
        </div>
      </div>
    );
  }

  return (
    <section
      className={`max-w-7xl mx-auto px-4 py-12 ${
        theme === "dark" ? "text-white" : "text-gray-900"
      }`}
    >
      <h1 className="text-3xl font-bold mb-10">
        Checkout ({totalItems} items)
      </h1>

      <div className="grid gap-10 md:grid-cols-3">
        {/* LEFT SIDE */}
        <div className="md:col-span-2 space-y-8">
          {/* SHIPPING ADDRESS */}
          <div
            className={`p-8 rounded-2xl border
            ${
              theme === "dark"
                ? "bg-[#0f0f0f] border-gray-800"
                : "bg-white shadow-sm border-gray-100"
            }`}
          >
            <h2 className="text-xl font-semibold mb-6">Shipping Address</h2>

            <div className="grid gap-5 sm:grid-cols-2">
              {["name", "phone", "city", "state", "pincode"].map((field) => (
                <input
                  key={field}
                  name={field}
                  placeholder={field.charAt(0).toUpperCase() + field.slice(1)}
                  value={address[field]}
                  onChange={handleChange}
                  className={`p-3 rounded-lg border outline-none transition
                    ${
                      theme === "dark"
                        ? "bg-black border-gray-700 focus:border-blue-500"
                        : "bg-white border-gray-300 focus:border-blue-500"
                    }`}
                />
              ))}

              <input
                name="street"
                placeholder="Street Address"
                value={address.street}
                onChange={handleChange}
                className={`p-3 rounded-lg border outline-none sm:col-span-2 transition
                ${
                  theme === "dark"
                    ? "bg-black border-gray-700 focus:border-blue-500"
                    : "bg-white border-gray-300 focus:border-blue-500"
                }`}
              />
            </div>
          </div>

          {/* PAYMENT */}
          <div
            className={`p-8 rounded-2xl border
            ${
              theme === "dark"
                ? "bg-[#0f0f0f] border-gray-800"
                : "bg-white shadow-sm border-gray-100"
            }`}
          >
            <h2 className="text-xl font-semibold mb-6">Payment Method</h2>

            <label className="flex items-center gap-3 p-4 rounded-lg border border-gray-200 dark:border-gray-700 mb-3">
              <input
                type="radio"
                value="COD"
                checked={paymentMethod === "COD"}
                onChange={(e) => setPaymentMethod(e.target.value)}
              />
              <span>Cash on Delivery</span>
            </label>

            <label className="flex items-center gap-3 p-4 rounded-lg border border-gray-200 dark:border-gray-700">
              <input
                type="radio"
                value="ONLINE"
                checked={paymentMethod === "ONLINE"}
                onChange={(e) => setPaymentMethod(e.target.value)}
              />
              <span>Pay Online (Razorpay)</span>
            </label>
          </div>
        </div>

        {/* RIGHT SIDE */}
        <div
          className={`p-8 rounded-2xl sticky top-24 border
          ${
            theme === "dark"
              ? "bg-[#0f0f0f] border-gray-800"
              : "bg-white shadow-md border-gray-100"
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
              🎉 You saved {formatCurrency(totalDiscount)}
            </div>
          )}

          <button
            onClick={handlePlaceOrder}
            disabled={loading}
            className="mt-8 w-full py-4 rounded-xl
            bg-gradient-to-r from-blue-600 to-indigo-600
            text-white font-semibold shadow-lg
            hover:scale-[1.02]
            active:scale-[0.98]
            transition-all duration-200
            disabled:opacity-50"
          >
            {loading ? "Placing Order..." : "Place Order"}
          </button>
        </div>
      </div>
    </section>
  );
};

export default Checkout;
