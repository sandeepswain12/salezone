import { useTheme } from "../context/ThemeContext";
import { useState, useMemo, useEffect, useCallback } from "react";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import orderService from "../services/orderService";
import addressService from "../services/addressService";
import { useNavigate } from "react-router-dom";
import loadRazorpay from "../utils/loadRazorpay";
import AddressCard from "../components/address/AddressCard";
import AddressFormModal from "../components/address/AddressFormModal";
import { MapPin, Plus, ChevronDown, ChevronUp } from "lucide-react";

const Checkout = () => {
  const { theme } = useTheme();
  const { cartItems, cartId, clearCart } = useCart();
  const { user } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const isDark = theme === "dark";

  const [paymentMethod, setPaymentMethod] = useState("COD");
  const [processingPayment, setProcessingPayment] = useState(false);
  const [loading, setLoading] = useState(false);

  // Address state
  const [addresses, setAddresses] = useState([]);
  const [addressesLoading, setAddressesLoading] = useState(true);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [showAllAddresses, setShowAllAddresses] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);
  const [savingAddress, setSavingAddress] = useState(false);

  const fetchAddresses = useCallback(async () => {
    if (!user?.userId) return;
    try {
      setAddressesLoading(true);
      const data = await addressService.getAddresses(user.userId);
      const sorted = data.sort(
        (a, b) => (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0)
      );
      setAddresses(sorted);
      const def = sorted.find((a) => a.isDefault);
      if (def) setSelectedAddressId(def.id);
      else if (sorted.length > 0) setSelectedAddressId(sorted[0].id);
    } catch {
      showToast("Failed to load addresses", "error");
    } finally {
      setAddressesLoading(false);
    }
  }, [user?.userId]);

  useEffect(() => {
    fetchAddresses();
  }, [fetchAddresses]);

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

  const formatCurrency = (amount) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(amount);

  const handleSaveAddress = async (formData) => {
    setSavingAddress(true);
    try {
      let saved;
      if (editingAddress) {
        saved = await addressService.updateAddress(
          user.userId,
          editingAddress.id,
          formData
        );
        showToast("Address updated", "success");
      } else {
        saved = await addressService.addAddress(user.userId, formData);
        showToast("Address added", "success");
      }
      setModalOpen(false);
      await fetchAddresses();
      if (!editingAddress) setSelectedAddressId(saved.id);
    } catch {
      showToast("Failed to save address", "error");
    } finally {
      setSavingAddress(false);
    }
  };

  const selectedAddress = addresses.find((a) => a.id === selectedAddressId);

  const handlePlaceOrder = async () => {
    if (!cartItems.length) {
      showToast("Cart is empty", "error");
      return;
    }
    if (!cartId) {
      showToast("Cart not found. Please refresh.", "error");
      return;
    }
    if (!selectedAddressId) {
      showToast("Please select a delivery address", "error");
      return;
    }

    try {
      setLoading(true);

      const orderData = {
        cartId,
        userId: user.userId,
        addressId: selectedAddressId,
        paymentMethod,
      };
      const order = await orderService.createOrder(orderData);

      if (paymentMethod === "COD") {
        await clearCart();
        showToast("Order placed successfully!", "success");
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
        handler: async (response) => {
          try {
            setProcessingPayment(true);
            window.scrollTo({ top: 0, behavior: "smooth" });
            await orderService.capturePayment(order.orderId, {
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpayPaymentSignature: response.razorpay_signature,
            });
            await clearCart();
            showToast("Order placed successfully!", "success");
            navigate("/orders");
          } catch {
            showToast("Payment verification failed", "error");
          } finally {
            setProcessingPayment(false);
          }
        },
        modal: { ondismiss: () => showToast("Payment cancelled", "info") },
      };

      new window.Razorpay(options).open();
    } catch {
      showToast("Failed to place order", "error");
    } finally {
      setLoading(false);
    }
  };

  const cardClass = `rounded-2xl border p-6 md:p-8 ${
    isDark
      ? "bg-zinc-900 border-zinc-800"
      : "bg-white shadow-sm border-gray-100"
  }`;

  if (processingPayment) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto mb-4" />
          <p className="text-lg font-semibold">Processing payment...</p>
        </div>
      </div>
    );
  }

  return (
    <section
      className={`max-w-7xl mx-auto px-4 py-12 ${
        isDark ? "text-white" : "text-gray-900"
      }`}
    >
      <h1 className="text-3xl font-bold mb-10">
        Checkout ({totalItems} items)
      </h1>

      <div className="grid gap-8 md:grid-cols-3">
        {/* ── LEFT COLUMN ── */}
        <div className="md:col-span-2 space-y-6">
          {/* DELIVERY ADDRESS */}
          <div className={cardClass}>
            <div className="flex items-center justify-between mb-5">
              <h2 className="text-xl font-semibold">Delivery Address</h2>
              <button
                onClick={() => {
                  setEditingAddress(null);
                  setModalOpen(true);
                }}
                className="flex items-center gap-1.5 text-sm text-blue-500 hover:text-blue-400 font-medium transition"
              >
                <Plus size={15} /> Add New
              </button>
            </div>

            {/* Loading skeleton */}
            {addressesLoading ? (
              <div className="space-y-3">
                {[1, 2].map((i) => (
                  <div
                    key={i}
                    className={`rounded-xl h-24 animate-pulse ${
                      isDark ? "bg-zinc-800" : "bg-gray-100"
                    }`}
                  />
                ))}
              </div>
            ) : addresses.length === 0 ? (
              /* Empty state */
              <div className="flex flex-col items-center py-10 gap-4 text-center">
                <div
                  className={`w-14 h-14 rounded-full flex items-center justify-center ${
                    isDark ? "bg-zinc-800" : "bg-gray-100"
                  }`}
                >
                  <MapPin
                    size={24}
                    className={isDark ? "text-zinc-500" : "text-gray-400"}
                  />
                </div>
                <p
                  className={`text-sm ${
                    isDark ? "text-zinc-400" : "text-gray-500"
                  }`}
                >
                  You don't have any saved addresses yet.
                </p>
                <button
                  onClick={() => {
                    setEditingAddress(null);
                    setModalOpen(true);
                  }}
                  className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold transition"
                >
                  <Plus size={15} /> Add Address
                </button>
              </div>
            ) : (
              /* Address cards — selectable */
              <div className="space-y-3">
                {(showAllAddresses ? addresses : addresses.slice(0, 2)).map(
                  (addr) => (
                    <AddressCard
                      key={addr.id}
                      address={addr}
                      selectable
                      selected={selectedAddressId === addr.id}
                      onSelect={() => setSelectedAddressId(addr.id)}
                      onEdit={(a) => {
                        setEditingAddress(a);
                        setModalOpen(true);
                      }}
                      onDelete={() => {}}
                      onSetDefault={() => {}}
                    />
                  )
                )}

                {addresses.length > 2 && (
                  <button
                    onClick={() => setShowAllAddresses((v) => !v)}
                    className={`w-full py-2.5 rounded-xl text-sm font-medium flex items-center justify-center gap-1.5 transition border ${
                      isDark
                        ? "border-zinc-700 hover:bg-zinc-800 text-zinc-300"
                        : "border-gray-200 hover:bg-gray-50 text-gray-600"
                    }`}
                  >
                    {showAllAddresses ? (
                      <>
                        <ChevronUp size={15} /> Show Less
                      </>
                    ) : (
                      <>
                        <ChevronDown size={15} /> {addresses.length - 2} More
                        Address{addresses.length - 2 > 1 ? "es" : ""}
                      </>
                    )}
                  </button>
                )}
              </div>
            )}
          </div>

          {/* PAYMENT METHOD */}
          <div className={cardClass}>
            <h2 className="text-xl font-semibold mb-5">Payment Method</h2>

            {[
              {
                value: "COD",
                label: "Cash on Delivery",
                sub: "Pay when your order arrives",
              },
              {
                value: "ONLINE",
                label: "Pay Online (Razorpay)",
                sub: "Credit/Debit card, UPI, Net Banking",
              },
            ].map(({ value, label, sub }) => (
              <label
                key={value}
                className={`flex items-center gap-3 p-4 rounded-xl border mb-3 last:mb-0 cursor-pointer transition ${
                  paymentMethod === value
                    ? "border-blue-500 bg-blue-500/5"
                    : isDark
                    ? "border-zinc-700 hover:border-zinc-600"
                    : "border-gray-200 hover:border-gray-300"
                }`}
              >
                <input
                  type="radio"
                  value={value}
                  checked={paymentMethod === value}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="accent-blue-600"
                />
                <div>
                  <p className="text-sm font-medium">{label}</p>
                  <p
                    className={`text-xs mt-0.5 ${
                      isDark ? "text-zinc-500" : "text-gray-400"
                    }`}
                  >
                    {sub}
                  </p>
                </div>
              </label>
            ))}
          </div>
        </div>

        {/* ── RIGHT COLUMN — Order Summary ── */}
        <div className={`${cardClass} sticky top-24 self-start`}>
          <h3 className="text-xl font-semibold mb-5">Order Summary</h3>

          {/* Selected address preview */}
          {selectedAddress && (
            <div
              className={`mb-5 p-3 rounded-xl text-xs leading-relaxed border ${
                isDark
                  ? "bg-zinc-800 border-zinc-700"
                  : "bg-gray-50 border-gray-200"
              }`}
            >
              <p
                className={`font-semibold text-sm mb-0.5 ${
                  isDark ? "text-zinc-100" : "text-gray-900"
                }`}
              >
                {selectedAddress.name}
              </p>
              <p className={isDark ? "text-zinc-400" : "text-gray-500"}>
                {selectedAddress.fullAddress}, {selectedAddress.city},<br />
                {selectedAddress.state} — {selectedAddress.pincode}
              </p>
              <p
                className={`mt-0.5 ${
                  isDark ? "text-zinc-400" : "text-gray-500"
                }`}
              >
                {selectedAddress.mobile}
              </p>
            </div>
          )}

          <div className="flex justify-between mb-3 text-sm">
            <span>Price ({totalItems} items)</span>
            <span>{formatCurrency(originalTotal)}</span>
          </div>
          <div className="flex justify-between mb-3 text-sm text-green-500">
            <span>Discount</span>
            <span>- {formatCurrency(totalDiscount)}</span>
          </div>
          <div className="flex justify-between mb-3 text-sm">
            <span>Delivery</span>
            <span className="text-green-500 font-medium">FREE</span>
          </div>

          <hr className="my-4 opacity-20" />

          <div className="flex justify-between font-bold text-lg mb-2">
            <span>Total Amount</span>
            <span>{formatCurrency(discountedTotal)}</span>
          </div>

          {totalDiscount > 0 && (
            <p className="text-sm text-green-500 font-medium mt-2">
              🎉 You saved {formatCurrency(totalDiscount)}
            </p>
          )}

          <button
            onClick={handlePlaceOrder}
            disabled={loading || addressesLoading || addresses.length === 0}
            className="mt-6 w-full py-4 rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold shadow-lg hover:scale-[1.02] active:scale-[0.98] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
          >
            {loading ? "Placing Order..." : "Place Order"}
          </button>
        </div>
      </div>

      {/* Add / Edit address modal */}
      {modalOpen && (
        <AddressFormModal
          existing={editingAddress}
          onSave={handleSaveAddress}
          onClose={() => setModalOpen(false)}
          saving={savingAddress}
        />
      )}
    </section>
  );
};

export default Checkout;
