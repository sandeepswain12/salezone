import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { getProductsByCategory } from "../services/CategoryService";
import ProductGridSkeleton from "../components/product/ProductGridSkeleton";
import ProductCard from "../components/product/ProductCard";
import BackButton from "../components/ui/BackButton";

const CategoryProducts = () => {
  const { categoryId } = useParams();

  const [products, setProducts] = useState([]);
  const [categoryTitle, setCategoryTitle] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (!categoryId) return;

    window.scrollTo(0, 0);
    setLoading(true);
    setError(false);

    const fetchProducts = async () => {
      try {
        const data = await getProductsByCategory({
          categoryId,
          pageNumber: 0,
          pageSize: 10,
          sortBy: "title",
          sortDir: "asc",
        });

        const content = data?.content || [];

        setProducts(content);

        if (content.length > 0) {
          setCategoryTitle(content[0]?.category?.title || "Category");
        } else {
          setCategoryTitle("Category");
        }
      } catch (err) {
        console.error("Failed to load products", err);
        setError(true);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, [categoryId]);

  if (loading) return <ProductGridSkeleton />;

  if (error) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-10 text-center text-red-500">
        Failed to load products.
      </div>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-4 py-10">
      <BackButton label="Back" />

      <h2 className="text-xl sm:text-2xl font-bold mb-8">{categoryTitle}</h2>

      {products.length === 0 ? (
        <div className="text-center py-16">
          <p className="text-lg font-medium opacity-70">No products found</p>
          <p className="text-sm opacity-50">Try another category</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
          {products.map((product) => (
            <ProductCard key={product.productId} product={product} />
          ))}
        </div>
      )}
    </section>
  );
};

export default CategoryProducts;
