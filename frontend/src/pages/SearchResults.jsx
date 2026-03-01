import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { searchProducts } from "../services/ProductService";
import ProductGridSkeleton from "../components/skeleton/ProductGridSkeleton";
import ProductCard from "../components/product/ProductCard";
import BackButton from "../components/ui/BackButton";
import Pagination from "../components/ui/Pagination";

const SearchResults = () => {
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
      <BackButton label="Back" />

      <h2 className="text-xl sm:text-2xl font-bold mb-6">
        Search results for “{keyword}”
      </h2>

      {products.length === 0 ? (
        <p className="text-center opacity-60 py-20">No products found.</p>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
          {products.map((product) => (
            <ProductCard key={product.productId} product={product} />
          ))}
        </div>
      )}

      <Pagination
        page={page}
        totalPages={totalPages}
        onPrev={() => setPage((p) => p - 1)}
        onNext={() => setPage((p) => p + 1)}
      />
    </section>
  );
};

export default SearchResults;
