import { useEffect, useState, useCallback, useRef } from "react";
import { getProducts } from "../../services/ProductService";
import ProductGridSkeleton from "../skeleton/ProductGridSkeleton";
import ProductCard from "./ProductCard";
import Pagination from "../ui/Pagination";
import { useToast } from "../../context/ToastContext";
import { useSearchParams } from "react-router-dom";

const PAGE_SIZE = 8;

const ProductGrid = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState(null);

  const [searchParams, setSearchParams] = useSearchParams();

  // ✅ Safe Page Parsing
  const pageParam = Number(searchParams.get("page"));
  const page = !isNaN(pageParam) && pageParam >= 0 ? pageParam : 0;

  const sectionRef = useRef(null);
  const isFirstLoad = useRef(true);

  const { showToast } = useToast();

  // ✅ Fetch Products
  const fetchProducts = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await getProducts({
        pageNumber: page,
        pageSize: PAGE_SIZE,
        sortBy: "title",
        sortDir: "asc",
      });

      setProducts(data?.content ?? []);
      setTotalPages(data?.totalPages ?? 0);
    } catch (err) {
      console.error("Product fetch failed:", err);
      setError("Unable to load products.");
      showToast("Failed to load products", "error");
    } finally {
      setLoading(false);
    }
  }, [page, showToast]);

  // ✅ Fetch when page changes
  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  // ✅ Scroll only on page change (not first load)
  useEffect(() => {
    if (isFirstLoad.current) {
      isFirstLoad.current = false;
      return;
    }

    sectionRef.current?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  }, [page]);

  // ✅ Preserve Other Query Params
  const handlePrev = () => {
    if (page > 0) {
      setSearchParams((prev) => {
        const params = new URLSearchParams(prev);
        params.set("page", page - 1);
        return params;
      });
    }
  };

  const handleNext = () => {
    if (page < totalPages - 1) {
      setSearchParams((prev) => {
        const params = new URLSearchParams(prev);
        params.set("page", page + 1);
        return params;
      });
    }
  };

  return (
    <section ref={sectionRef} className="max-w-7xl mx-auto px-4 py-10 sm:py-14">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-8 gap-4">
        <h2 className="text-2xl sm:text-3xl font-bold tracking-tight">
          Trending Products
        </h2>

        {totalPages > 1 && (
          <span className="text-sm opacity-60">
            Page {page + 1} of {totalPages}
          </span>
        )}
      </div>

      {/* LOADING */}
      {loading && <ProductGridSkeleton />}

      {/* ERROR */}
      {!loading && error && (
        <div className="text-center py-20">
          <p className="text-red-500 text-lg mb-4">{error}</p>
          <button
            onClick={fetchProducts}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            Retry
          </button>
        </div>
      )}

      {/* EMPTY */}
      {!loading && !error && products.length === 0 && (
        <div className="text-center py-24 opacity-60">No products found.</div>
      )}

      {/* GRID */}
      {!loading && !error && products.length > 0 && (
        <>
          <div
            className="
              grid
              grid-cols-2
              sm:grid-cols-3
              md:grid-cols-4
              gap-4 sm:gap-6
              transition-all duration-300
            "
          >
            {products.map((product) => (
              <ProductCard key={product.productId} product={product} />
            ))}
          </div>

          {/* PAGINATION */}
          {totalPages > 1 && (
            <div className="mt-12 flex justify-center">
              <Pagination
                page={page}
                totalPages={totalPages}
                onPrev={handlePrev}
                onNext={handleNext}
              />
            </div>
          )}
        </>
      )}
    </section>
  );
};

export default ProductGrid;
