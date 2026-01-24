import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { ShoppingCart } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { getProductById } from "../services/ProductService";
import ProductDetailsSkeleton from "./ProductDetailsSkeleton";
import BackButton from "./BackButton";

const ProductDetails = () => {
  const { id: productId } = useParams();
  const { theme } = useTheme();

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);

    getProductById(productId)
      .then((data) => setProduct(data))
      .catch(() => setError("Product not found"))
      .finally(() => setLoading(false));
  }, [productId]);

  if (loading) return <ProductDetailsSkeleton />;

  if (error) return <p className="text-center py-20 text-red-500">{error}</p>;

  return (
    <section className="max-w-7xl mx-auto px-4 py-6">
      <BackButton label="Back" />
      <section className="max-w-7xl mx-auto px-4 py-8 sm:py-12">
        <div className="grid gap-8 md:grid-cols-2">
          {/* IMAGE */}
          <div
            className={`rounded-xl p-4 sm:p-6
            
            ${theme === "dark" ? "bg-[#0f0f0f]" : "bg-gray-100"}
          `}
          >
            <img
              src={`http://localhost:8089/salezone/ecom/products/image/${product.productId}`}
              alt={product.title}
              loading="lazy"
              className="w-full h-64 sm:h-80 md:h-[400px] object-contain"
            />
          </div>

          {/* INFO */}
          <div className="flex flex-col">
            <h1 className="text-2xl sm:text-3xl font-bold mb-3">
              {product.title}
            </h1>

            <p className="opacity-70 mb-4">
              Category: {product.category?.title}
            </p>

            {/* PRICE */}
            <div className="flex flex-wrap items-center gap-3 mb-6">
              <span className="text-2xl font-bold">
                ₹{product.discountedPrice}
              </span>
              <span className="line-through opacity-60">₹{product.price}</span>
            </div>

            <p className="leading-relaxed mb-6">{product.description}</p>

            <p className="mb-6">
              Stock:{" "}
              <span className="font-semibold">
                {product.quantity > 0 ? "Available" : "Out of stock"}
              </span>
            </p>

            {/* ACTION BUTTONS */}
            <div className="flex flex-col sm:flex-row gap-4">
              <button className="w-full sm:flex-1 py-3 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition">
                Buy Now
              </button>

              <button
                className={`w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-3 rounded-lg transition
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
    </section>
  );
};

export default ProductDetails;
