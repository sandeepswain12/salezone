import { useTheme } from "../context/ThemeContext";
import { useState } from "react";

const Checkout = () => {
  const { theme } = useTheme();

  const [address, setAddress] = useState({
    name: "",
    phone: "",
    street: "",
    city: "",
    state: "",
    pincode: "",
  });

  const handleChange = (e) => {
    setAddress({ ...address, [e.target.name]: e.target.value });
  };

  const subtotal = 85997; // later from cart
  const delivery = 0;
  const total = subtotal + delivery;

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      <h1 className="text-3xl font-bold mb-8">Checkout</h1>

      <div className="grid gap-10 md:grid-cols-3">
        {/* LEFT */}
        <div className="md:col-span-2 space-y-8">
          {/* SHIPPING */}
          <div
            className={`p-6 rounded-xl
              ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"}
            `}
          >
            <h2 className="text-xl font-semibold mb-4">Shipping Address</h2>

            <div className="grid gap-4 sm:grid-cols-2">
              <input
                name="name"
                placeholder="Full Name"
                value={address.name}
                onChange={handleChange}
                className={`p-3 rounded border outline-none
                  ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
                `}
              />
              <input
                name="phone"
                placeholder="Phone Number"
                value={address.phone}
                onChange={handleChange}
                className={`p-3 rounded border outline-none
                  ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
                `}
              />
              <input
                name="street"
                placeholder="Street Address"
                value={address.street}
                onChange={handleChange}
                className={`p-3 rounded border outline-none sm:col-span-2
                  ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
                `}
              />
              <input
                name="city"
                placeholder="City"
                value={address.city}
                onChange={handleChange}
                className={`p-3 rounded border outline-none
                  ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
                `}
              />
              <input
                name="state"
                placeholder="State"
                value={address.state}
                onChange={handleChange}
                className={`p-3 rounded border outline-none
                  ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
                `}
              />
              <input
                name="pincode"
                placeholder="Pincode"
                value={address.pincode}
                onChange={handleChange}
                className={`p-3 rounded border outline-none
                  ${theme === "dark" ? "bg-black border-gray-700" : "bg-white"}
                `}
              />
            </div>
          </div>

          {/* PAYMENT */}
          <div
            className={`p-6 rounded-xl
              ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"}
            `}
          >
            <h2 className="text-xl font-semibold mb-4">Payment Method</h2>

            <div className="space-y-3">
              <label className="flex items-center gap-3 cursor-pointer">
                <input type="radio" name="payment" defaultChecked />
                <span>Cash on Delivery</span>
              </label>
              <label className="flex items-center gap-3 cursor-pointer opacity-60">
                <input type="radio" disabled />
                <span>UPI / Card (coming soon)</span>
              </label>
            </div>
          </div>
        </div>

        {/* RIGHT - SUMMARY */}
        <div
          className={`p-6 rounded-xl h-fit
            ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"}
          `}
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

          <button className="w-full py-3 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition">
            Place Order
          </button>
        </div>
      </div>
    </section>
  );
};

export default Checkout;
