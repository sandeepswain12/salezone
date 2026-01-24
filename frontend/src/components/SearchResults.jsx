import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { searchProducts } from "../services/ProductService";
import ProductGridSkeleton from "./ProductGridSkeleton";
import { useTheme } from "../context/ThemeContext";
import { ArrowLeft } from "lucide-react";

const SearchResults = () => {
  const { theme } = useTheme();
  const navigate = useNavigate();
  const { keyword } = useParams();

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    setLoading(true);
    searchProducts({ keyword, pageNumber: page })
      .then((data) => {
        setProducts(data.content || []);
        setTotalPages(data.totalPages || 0);
      })
      .finally(() => setLoading(false));
  }, [keyword, page]);

  if (loading) return <ProductGridSkeleton />;

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      {/* BACK BUTTON */}
      <button
        onClick={() => navigate(-1)}
        className={`mb-6 inline-flex items-center gap-2 px-4 py-2 rounded-lg border 
          ${
            theme === "dark"
              ? "border-gray-700 hover:bg-[#1a1a1a]"
              : "border-gray-300 hover:bg-gray-100"
          }
        `}
      >
        <ArrowLeft size={16} />
        Back
      </button>

      <h2 className="text-xl sm:text-2xl font-bold mb-6">
        Search results for “{keyword}”
      </h2>

      {products.length === 0 && (
        <p className="text-center opacity-60 py-20">No products found.</p>
      )}

      {products.length > 0 && (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
          {products.map((product) => (
            <div
              key={product.productId}
              onClick={() => navigate(`/product/${product.productId}`)}
              className={`rounded-xl p-4 cursor-pointer
                ${
                  theme === "dark"
                    ? "bg-[#0f0f0f] hover:bg-[#151515]"
                    : "bg-white shadow hover:shadow-lg"
                }
              `}
            >
              <div className="aspect-square mb-3 rounded-lg overflow-hidden">
                <img
                  src={`http://localhost:8089/salezone/ecom/products/image/${product.productId}`}
                  alt={product.title}
                  loading="lazy"
                  className="w-full h-full object-cover"
                />
              </div>

              <h3 className="text-sm font-semibold line-clamp-2">
                {product.title}
              </h3>

              <div className="flex gap-2 mt-1">
                <span className="font-bold text-sm">
                  ₹{product.discountedPrice}
                </span>
                <span className="line-through text-xs opacity-60">
                  ₹{product.price}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex justify-center gap-4 mt-10">
          <button
            disabled={page === 0}
            onClick={() => setPage((p) => p - 1)}
            className="px-4 py-2 border rounded disabled:opacity-40"
          >
            Prev
          </button>
          <button
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((p) => p + 1)}
            className="px-4 py-2 border rounded disabled:opacity-40"
          >
            Next
          </button>
        </div>
      )}
    </section>
  );
};

export default SearchResults;
