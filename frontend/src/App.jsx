import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import HeroCarousel from "./components/HeroCarousel";
import ProductGrid from "./components/ProductGrid";
import ProductDetails from "./components/ProductDetails";
import CategorySection from "./components/CategorySection";
import Footer from "./components/Footer";
import { useTheme } from "./context/ThemeContext";
import ScrollToTop from "./components/ScrollToTop";
import Cart from "./components/Cart";
import Checkout from "./components/Checkout";
import Auth from "./components/Auth";
import CategoryProducts from "./components/CategoryProducts";
import SearchResults from "./components/SearchResults";

function App() {
  const { theme } = useTheme();

  return (
    <div
      className={`min-h-screen w-full
        ${theme === "dark" ? "bg-black text-white" : "bg-white text-black"}
      `}
    >
      <Navbar />
      <ScrollToTop />

      {/* Offset for fixed navbar */}
      <div className="pt-16">
        <Routes>
          {/* HOME */}
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

          {/* PRODUCT */}
          <Route path="/product/:id" element={<ProductDetails />} />

          {/* CATEGORY (READY FOR NEXT STEP) */}
          <Route path="/categories" element={<CategorySection />} />
          {/* later */}
          {/* <Route path="/category/:categoryId" element={<CategoryProducts />} /> */}

          {/* CART / CHECKOUT / AUTH */}
          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/auth" element={<Auth />} />
          <Route path="/category/:categoryId" element={<CategoryProducts />} />
          <Route path="/search/:keyword" element={<SearchResults />} />
        </Routes>

        <Footer />
      </div>
    </div>
  );
}

export default App;
