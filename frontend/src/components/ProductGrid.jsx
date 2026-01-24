import { ShoppingCart } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { getProducts } from "../services/ProductService";
import ProductGridSkeleton from "./ProductGridSkeleton";

const ProductGrid = () => {
  const { theme } = useTheme();
  const navigate = useNavigate();

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    setLoading(true);

    getProducts({ pageNumber: page, pageSize: 4 })
      .then((data) => {
        setProducts(data.content || []);
        setTotalPages(data.totalPages || 0);
      })
      .catch(() => {
        setProducts([]);
        setTotalPages(0);
      })
      .finally(() => setLoading(false));
  }, [page]);

  if (loading) return <ProductGridSkeleton />;

  return (
    <section className="max-w-7xl mx-auto px-4 py-6 sm:py-12">
      <h2 className="text-xl sm:text-2xl font-bold mb-6 text-center sm:text-left">
        Trending Products
      </h2>

      {/* EMPTY STATE */}
      {products.length === 0 && (
        <p className="text-center opacity-60 py-20">
          No trending products found.
        </p>
      )}

      {/* GRID */}
      {products.length > 0 && (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 sm:gap-6">
          {products.map((product) => {
            const imageSrc = product.productImageName
              ? `http://localhost:8089/salezone/ecom/products/image/${product.productId}`
              : "/no-image.png";

            return (
              <div
                key={product.productId}
                onClick={() => navigate(`/product/${product.productId}`)}
                className={`rounded-xl p-3 sm:p-4 cursor-pointer transition
                  ${
                    theme === "dark"
                      ? "bg-[#0f0f0f] hover:bg-[#151515]"
                      : "bg-white shadow-sm hover:shadow-lg"
                  }
                `}
              >
                {/* IMAGE */}
                <div className="aspect-square mb-3 overflow-hidden rounded-lg bg-gray-100 dark:bg-[#1a1a1a]">
                  <img
                    loading="lazy"
                    src={imageSrc}
                    alt={product.title}
                    className="w-full h-full object-cover"
                  />
                </div>

                {/* TITLE */}
                <h3 className="text-sm font-semibold mb-1 line-clamp-2">
                  {product.title}
                </h3>

                {/* PRICE */}
                <div className="flex items-center gap-2 mb-3">
                  <span className="font-bold text-sm">
                    ₹{product.discountedPrice}
                  </span>
                  <span className="line-through text-xs opacity-60">
                    ₹{product.price}
                  </span>
                </div>

                {/* BUTTON */}
                <button
                  onClick={(e) => e.stopPropagation()}
                  className="w-full flex items-center justify-center gap-2
                    py-2.5 rounded-lg text-xs font-medium
                    bg-blue-600 text-white hover:bg-blue-700"
                >
                  <ShoppingCart size={15} />
                  Add to Cart
                </button>
              </div>
            );
          })}
        </div>
      )}

      {/* PAGINATION */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-4 mt-8">
          <button
            disabled={page === 0}
            onClick={() => setPage((p) => p - 1)}
            className="px-4 py-2 border rounded text-sm disabled:opacity-40"
          >
            Prev
          </button>

          <span className="text-xs opacity-70">
            Page {page + 1} of {totalPages}
          </span>

          <button
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((p) => p + 1)}
            className="px-4 py-2 border rounded text-sm disabled:opacity-40"
          >
            Next
          </button>
        </div>
      )}
    </section>
  );
};

export default ProductGrid;
