import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState, useMemo } from "react";
import { ShoppingCart, Star, ArrowLeft } from "lucide-react";
import { useTheme } from "../../context/ThemeContext";
import { getProductById } from "../../services/ProductService";
import ProductDetailsSkeleton from "../skeleton/ProductDetailsSkeleton";
import { useCart } from "../../context/CartContext";

const ProductDetails = () => {
  const { id: productId } = useParams();
  const navigate = useNavigate();
  const { theme } = useTheme();
  const { addToCart } = useCart();

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [imgError, setImgError] = useState(false);

  // ✅ Fetch Product
  useEffect(() => {
    if (!productId) return;

    window.scrollTo({ top: 0, behavior: "smooth" });
    setLoading(true);
    setError(null);

    const fetchProduct = async () => {
      try {
        const data = await getProductById(productId);
        setProduct(data);
      } catch (err) {
        console.error(err);
        setError("Product not found");
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [productId]);

  // ✅ Image URL
  const imageUrl =
    product && !imgError
      ? `${import.meta.env.VITE_API_BASE_URL}/products/image/${
          product.productId
        }`
      : "/no-image.png";

  // ✅ Price Formatting
  const formattedPrice = useMemo(() => {
    if (!product) return "";
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(product.discountedPrice);
  }, [product]);

  const formattedOriginalPrice = useMemo(() => {
    if (!product) return "";
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(product.price);
  }, [product]);

  const discountPercentage = useMemo(() => {
    if (!product || !product.price) return null;
    const discount =
      ((product.price - product.discountedPrice) / product.price) * 100;
    return Math.round(discount);
  }, [product]);

  // ✅ Back Handler
  const handleBack = () => {
    navigate(-1);
  };

  if (loading) return <ProductDetailsSkeleton />;

  if (error) {
    return (
      <div className="text-center py-20 text-red-500 text-lg">{error}</div>
    );
  }

  if (!product) return null;

  const rating = product.rating ?? 4;
  const reviewCount = product.reviewCount ?? 124;

  return (
    <section className="max-w-7xl mx-auto px-4 py-8">
      {/* ✅ IMPROVED BACK BUTTON */}
      <button
        onClick={handleBack}
        className={`flex items-center gap-2 px-4 py-2 rounded-lg 
        text-sm font-medium transition
        ${
          theme === "dark"
            ? "bg-[#0f0f0f] text-white hover:bg-[#1a1a1a]"
            : "bg-gray-100 text-black hover:bg-gray-200"
        }`}
      >
        <ArrowLeft size={16} />
        Back
      </button>

      <div className="grid gap-12 md:grid-cols-2 mt-6">
        {/* LEFT SIDE - IMAGE */}
        <div
          className={`rounded-2xl p-6 transition-all duration-300
            ${theme === "dark" ? "bg-[#111]" : "bg-gray-100"}`}
        >
          <div className="relative overflow-hidden rounded-xl">
            <img
              src={imageUrl}
              alt={product.title}
              onError={() => setImgError(true)}
              className="w-full h-[420px] object-contain transition-transform duration-500 hover:scale-105"
            />

            {discountPercentage > 0 && (
              <span className="absolute top-4 left-4 bg-red-500 text-white text-xs px-3 py-1 rounded-full font-semibold shadow-md">
                {discountPercentage}% OFF
              </span>
            )}
          </div>
        </div>

        {/* RIGHT SIDE - INFO */}
        <div className="flex flex-col">
          <h1 className="text-3xl font-bold mb-3">{product.title}</h1>

          {/* Rating */}
          <div className="flex items-center gap-1 mb-3">
            {[1, 2, 3, 4, 5].map((star) => (
              <Star
                key={star}
                size={16}
                className={
                  star <= rating
                    ? "text-yellow-500 fill-yellow-500"
                    : "text-gray-300"
                }
              />
            ))}
            <span className="text-sm text-gray-500 ml-2">
              ({reviewCount} reviews)
            </span>
          </div>

          <p className="text-sm opacity-70 mb-4">
            Category: {product.category?.title}
          </p>

          {/* PRICE */}
          <div className="flex items-center gap-4 mb-6">
            <span className="text-3xl font-bold">{formattedPrice}</span>

            {product.price > product.discountedPrice && (
              <span className="line-through text-gray-500 text-lg">
                {formattedOriginalPrice}
              </span>
            )}
          </div>

          {/* DESCRIPTION */}
          <p
            className={`leading-relaxed mb-6 text-[15px]
              ${theme === "dark" ? "text-gray-300" : "text-gray-700"}`}
          >
            {product.description}
          </p>

          {/* STOCK */}
          <div className="mb-6">
            {product.quantity > 0 ? (
              <span className="text-green-600 font-semibold">
                In Stock ({product.quantity} available)
              </span>
            ) : (
              <span className="text-red-500 font-semibold">Out of Stock</span>
            )}
          </div>

          {/* ADD TO CART */}
          <button
            disabled={product.quantity <= 0}
            onClick={() => addToCart(product.productId)}
            className={`mt-auto w-full flex items-center justify-center gap-3
              py-4 rounded-xl font-semibold text-white transition-all duration-300
              ${
                product.quantity <= 0
                  ? "bg-gray-400 cursor-not-allowed"
                  : "bg-blue-600 hover:bg-blue-700 hover:shadow-lg"
              }`}
          >
            <ShoppingCart size={20} />
            {product.quantity <= 0 ? "Out of Stock" : "Add to Cart"}
          </button>
        </div>
      </div>
    </section>
  );
};

export default ProductDetails;
