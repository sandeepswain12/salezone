import { Trash2, Plus, Minus } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

const initialCart = [
  {
    id: 1,
    name: "iPhone 15",
    price: 79999,
    image:
      "https://images.unsplash.com/photo-1695048133142-1a20484d2568?q=80&w=400",
    qty: 1,
  },
  {
    id: 2,
    name: "Wireless Headphones",
    price: 2999,
    image:
      "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=400",
    qty: 2,
  },
];

const Cart = () => {
  const { theme } = useTheme();
  const [cart, setCart] = useState(initialCart);
  const navigate = useNavigate();

  const increaseQty = (id) => {
    setCart((prev) =>
      prev.map((item) =>
        item.id === id ? { ...item, qty: item.qty + 1 } : item
      )
    );
  };

  const decreaseQty = (id) => {
    setCart((prev) =>
      prev.map((item) =>
        item.id === id && item.qty > 1 ? { ...item, qty: item.qty - 1 } : item
      )
    );
  };

  const removeItem = (id) => {
    setCart((prev) => prev.filter((item) => item.id !== id));
  };

  const subtotal = cart.reduce(
    (total, item) => total + item.price * item.qty,
    0
  );

  if (cart.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <h2 className="text-2xl font-bold mb-3">Your cart is empty 🛒</h2>
        <p className="opacity-70">Start shopping to add items</p>
      </div>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      <h1 className="text-3xl font-bold mb-8">Shopping Cart</h1>

      <div className="grid gap-8 md:grid-cols-3">
        {/* CART ITEMS */}
        <div className="md:col-span-2 space-y-6">
          {cart.map((item) => (
            <div
              key={item.id}
              className={`flex gap-4 p-4 rounded-xl
                ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"}
              `}
            >
              <img
                src={item.image}
                alt={item.name}
                className="w-24 h-24 object-cover rounded-lg"
              />

              <div className="flex-1">
                <h3 className="font-semibold">{item.name}</h3>
                <p className="font-bold mt-1">₹{item.price}</p>

                {/* QTY */}
                <div className="flex items-center gap-3 mt-4">
                  <button
                    onClick={() => decreaseQty(item.id)}
                    className="p-2 rounded border"
                  >
                    <Minus size={16} />
                  </button>

                  <span>{item.qty}</span>

                  <button
                    onClick={() => increaseQty(item.id)}
                    className="p-2 rounded border"
                  >
                    <Plus size={16} />
                  </button>
                </div>
              </div>

              {/* REMOVE */}
              <button
                onClick={() => removeItem(item.id)}
                className="text-red-500"
              >
                <Trash2 />
              </button>
            </div>
          ))}
        </div>

        {/* SUMMARY */}
        <div
          className={`p-6 rounded-xl h-fit
            ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-white shadow-sm"}
          `}
        >
          <h3 className="text-xl font-semibold mb-4">Price Details</h3>

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
            <span>₹{subtotal}</span>
          </div>

          <button
            onClick={() => navigate("/checkout")}
            className="w-full py-3 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition"
          >
            Proceed to Checkout
          </button>
        </div>
      </div>
    </section>
  );
};

export default Cart;
