import { Routes, Route } from "react-router-dom";
import Navbar from "./components/layout/Navbar";
import HeroCarousel from "./components/carousel/HeroCarousel";
import ProductGrid from "./components/product/ProductGrid";
import ProductDetails from "./components/product/ProductDetails";
import CategorySection from "./components/category/CategorySection";
import Footer from "./components/layout/Footer";
import { useTheme } from "./context/ThemeContext";
import ScrollToTop from "./components/layout/ScrollToTop";
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import Auth from "./pages/Auth";
import CategoryProducts from "./pages/CategoryProducts";
import SearchResults from "./pages/SearchResults";
import ProtectedRoute from "./components/ProtectedRoute";
import AuthSuccess from "./pages/AuthSuccess";
import AuthFailure from "./pages/AuthFailure";

function App() {
  const { theme } = useTheme();

  return (
    <div
      className={`min-h-screen w-full ${
        theme === "dark" ? "bg-black text-white" : "bg-white text-black"
      }`}
    >
      <Navbar />
      <ScrollToTop />

      <div className="pt-16">
        <Routes>
          <Route
            path="/"
            element={
              <>
                <HeroCarousel />
                <CategorySection />
                <ProductGrid />
              </>
            }
          />

          <Route path="/product/:id" element={<ProductDetails />} />
          <Route path="/categories" element={<CategorySection />} />
          <Route path="/category/:categoryId" element={<CategoryProducts />} />
          <Route path="/search/:keyword" element={<SearchResults />} />

          <Route
            path="/cart"
            element={
              <ProtectedRoute>
                <Cart />
              </ProtectedRoute>
            }
          />

          <Route
            path="/checkout"
            element={
              <ProtectedRoute>
                <Checkout />
              </ProtectedRoute>
            }
          />

          <Route path="/auth" element={<Auth />} />
          <Route path="/auth/success" element={<AuthSuccess />} />
          <Route path="/auth/failure" element={<AuthFailure />} />
        </Routes>
      </div>

      <Footer />
    </div>
  );
}

export default App;
