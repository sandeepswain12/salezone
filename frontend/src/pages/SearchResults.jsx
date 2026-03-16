import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { searchProducts } from "../services/ProductService";
import { getCategories } from "../services/CategoryService";

import ProductGridSkeleton from "../components/skeleton/ProductGridSkeleton";
import ProductCard from "../components/product/ProductCard";
import BackButton from "../components/ui/BackButton";
import Pagination from "../components/ui/Pagination";

const SearchResults = () => {
  const { keyword } = useParams();

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);

  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [filters, setFilters] = useState({
    sortBy: "title",
    sortDir: "asc",
    minPrice: "",
    maxPrice: "",
    categoryId: "",
  });

  const [appliedFilters, setAppliedFilters] = useState(filters);

  // Load categories
  useEffect(() => {
    getCategories().then(setCategories);
  }, []);

  const fetchProducts = async () => {
    setLoading(true);

    try {
      const data = await searchProducts({
        keyword,
        pageNumber: page,
        sortBy: appliedFilters.sortBy,
        sortDir: appliedFilters.sortDir,
        minPrice: appliedFilters.minPrice
          ? Number(appliedFilters.minPrice)
          : undefined,
        maxPrice: appliedFilters.maxPrice
          ? Number(appliedFilters.maxPrice)
          : undefined,
        categoryId: appliedFilters.categoryId || undefined,
      });

      setProducts(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (error) {
      console.error("Search error:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [keyword, page, appliedFilters]);

  // Reset filters when keyword changes
  useEffect(() => {
    setPage(0);
    setFilters({
      sortBy: "title",
      sortDir: "asc",
      minPrice: "",
      maxPrice: "",
      categoryId: "",
    });
    setAppliedFilters({
      sortBy: "title",
      sortDir: "asc",
      minPrice: "",
      maxPrice: "",
      categoryId: "",
    });
  }, [keyword]);

  const applyFilters = () => {
    setPage(0);
    setAppliedFilters(filters);
  };

  const clearFilters = () => {
    const reset = {
      sortBy: "title",
      sortDir: "asc",
      minPrice: "",
      maxPrice: "",
      categoryId: "",
    };
    setFilters(reset);
    setAppliedFilters(reset);
    setPage(0);
  };

  const handleSortChange = (e) => {
    const [sortBy, sortDir] = e.target.value.split("-");

    setFilters((prev) => ({
      ...prev,
      sortBy,
      sortDir,
    }));
  };

  if (loading) return <ProductGridSkeleton />;

  return (
    <section className="max-w-7xl mx-auto px-4 py-10">
      <div className="mb-4">
        <BackButton label="Back" />
      </div>

      {/* HEADER */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-8">
        <h2 className="text-xl sm:text-2xl font-bold">
          Search results for “{keyword}”
        </h2>

        <p className="text-sm opacity-70 mt-2 md:mt-0">
          {products.length} products found
        </p>
      </div>

      {/* FILTER BAR */}
      <div className="bg-white border rounded-xl p-4 mb-8 shadow-sm">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
          {/* CATEGORY */}
          <select
            className="border rounded-lg px-3 py-2"
            value={filters.categoryId}
            onChange={(e) =>
              setFilters((prev) => ({
                ...prev,
                categoryId: e.target.value,
              }))
            }
          >
            <option value="">All Categories</option>

            {categories.map((cat) => (
              <option key={cat.categoryId} value={cat.categoryId}>
                {cat.title}
              </option>
            ))}
          </select>

          {/* SORT */}
          <select
            className="border rounded-lg px-3 py-2 w-full"
            value={`${filters.sortBy}-${filters.sortDir}`}
            onChange={handleSortChange}
          >
            <option value="title-asc">Sort By</option>
            <option value="price-asc">Price: Low → High</option>
            <option value="price-desc">Price: High → Low</option>
            <option value="createdAt-desc">Newest</option>
          </select>

          {/* MIN PRICE */}
          <input
            type="number"
            placeholder="Min price"
            className="border rounded-lg px-3 py-2"
            value={filters.minPrice}
            onChange={(e) =>
              setFilters((prev) => ({
                ...prev,
                minPrice: e.target.value,
              }))
            }
          />

          {/* MAX PRICE */}
          <input
            type="number"
            placeholder="Max price"
            className="border rounded-lg px-3 py-2"
            value={filters.maxPrice}
            onChange={(e) =>
              setFilters((prev) => ({
                ...prev,
                maxPrice: e.target.value,
              }))
            }
          />

          {/* BUTTONS */}
          <div className="flex gap-2 sm:col-span-2 lg:col-span-1">
            <button
              onClick={applyFilters}
              className="flex-1 bg-blue-600 text-white rounded-lg px-4 py-2 hover:bg-blue-700 transition"
            >
              Apply
            </button>

            <button
              onClick={clearFilters}
              className="flex-1 border rounded-lg px-4 py-2 hover:bg-gray-100 transition"
            >
              Reset
            </button>
          </div>
        </div>
      </div>

      {/* PRODUCTS GRID */}
      {products.length === 0 ? (
        <p className="text-center opacity-60 py-20">No products found.</p>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4 sm:gap-6">
          {products.map((product) => (
            <ProductCard key={product.productId} product={product} />
          ))}
        </div>
      )}

      {/* PAGINATION */}
      {totalPages > 1 && (
        <div className="mt-10 flex justify-center">
          <Pagination
            page={page}
            totalPages={totalPages}
            onPrev={() => setPage((p) => Math.max(p - 1, 0))}
            onNext={() => setPage((p) => (p < totalPages - 1 ? p + 1 : p))}
          />
        </div>
      )}
    </section>
  );
};

export default SearchResults;
