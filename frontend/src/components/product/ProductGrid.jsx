import { useEffect, useState } from "react";
import { getProducts } from "../../services/ProductService";
import ProductGridSkeleton from "./ProductGridSkeleton";
import ProductCard from "./ProductCard";
import Pagination from "../ui/Pagination";
import { useToast } from "../../context/ToastContext";

const ProductGrid = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const { showToast } = useToast();

  useEffect(() => {
    fetchProducts();
  }, [page]);

  const fetchProducts = async () => {
    try {
      setLoading(true);

      const data = await getProducts({
        pageNumber: page,
        pageSize: 8,
        sortBy: "title",
        sortDir: "asc",
      });

      setProducts(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (error) {
      showToast("Failed to load products", "error");
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <ProductGridSkeleton />;

  return (
    <section className="max-w-7xl mx-auto px-4 py-6 sm:py-12">
      <h2 className="text-xl sm:text-2xl font-bold mb-6 text-center sm:text-left">
        Trending Products
      </h2>

      {products.length === 0 ? (
        <p className="text-center opacity-60 py-20">No products found.</p>
      ) : (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 sm:gap-6">
          {products.map((product) => (
            <ProductCard key={product.productId} product={product} />
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <Pagination
          page={page}
          totalPages={totalPages}
          onPrev={() => setPage((prev) => Math.max(prev - 1, 0))}
          onNext={() => setPage((prev) => Math.min(prev + 1, totalPages - 1))}
        />
      )}
    </section>
  );
};

export default ProductGrid;
