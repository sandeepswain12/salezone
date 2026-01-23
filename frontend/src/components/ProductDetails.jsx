import { ShoppingCart, Star } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useState } from "react";

const ProductDetails = () => {
  const { theme } = useTheme();
  const [qty, setQty] = useState(1);

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      <div className="grid gap-10 md:grid-cols-2">
        {/* IMAGE */}
        <div
          className={`rounded-xl p-6
            ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-gray-100"}
          `}
        >
          <img
            src="https://images.unsplash.com/photo-1695048133142-1a20484d2568?q=80&w=800"
            alt="Product"
            className="w-full h-400px object-cover rounded-lg"
          />
        </div>

        {/* INFO */}
        <div>
          <h1 className="text-3xl font-bold mb-3">iPhone 15 Pro Max</h1>

          {/* RATING */}
          <div className="flex items-center gap-1 mb-4">
            {[1, 2, 3, 4, 5].map((i) => (
              <Star key={i} size={18} className="text-yellow-500" />
            ))}
            <span className="ml-2 text-sm opacity-70">(4.9)</span>
          </div>

          {/* PRICE */}
          <p className="text-2xl font-semibold mb-6">₹1,39,999</p>

          {/* DESCRIPTION */}
          <p className="mb-6 leading-relaxed opacity-90">
            Experience the power of the latest A-series chip, stunning camera
            system, and premium design with iPhone 15 Pro Max.
          </p>

          {/* QUANTITY */}
          <div className="flex items-center gap-4 mb-6">
            <span className="font-medium">Quantity:</span>
            <div className="flex items-center border rounded-lg overflow-hidden">
              <button
                onClick={() => setQty(qty > 1 ? qty - 1 : 1)}
                className="px-4 py-2"
              >
                −
              </button>
              <span className="px-4">{qty}</span>
              <button onClick={() => setQty(qty + 1)} className="px-4 py-2">
                +
              </button>
            </div>
          </div>

          {/* ACTIONS */}
          <div className="flex gap-4">
            <button
              className="flex-1 py-3 rounded-lg
              bg-blue-600 text-white font-medium
              hover:bg-blue-700 transition"
            >
              Buy Now
            </button>

            <button
              className={`flex items-center justify-center gap-2 px-6 py-3 rounded-lg
                ${
                  theme === "dark"
                    ? "bg-[#1a1a1a] hover:bg-[#222]"
                    : "bg-gray-200 hover:bg-gray-300"
                }
              `}
            >
              <ShoppingCart size={18} />
              Add to Cart
            </button>
          </div>
        </div>
      </div>
    </section>
  );
};

export default ProductDetails;
