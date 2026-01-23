import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import HeroCarousel from "./components/HeroCarousel";
import ProductGrid from "./components/ProductGrid";
import ProductDetails from "./components/ProductDetails";
import Footer from "./components/Footer";
import { useTheme } from "./context/ThemeContext";
import ScrollToTop from "./components/ScrollToTop";
import Cart from "./components/Cart";
import Checkout from "./components/Checkout";
import Auth from "./components/Auth";

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
      <div className="pt-16">
        <Routes>
          <Route
            path="/"
            element={
              <>
                <HeroCarousel />
                <ProductGrid />
              </>
            }
          />

          <Route path="/product/:id" element={<ProductDetails />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/auth" element={<Auth />} />
        </Routes>

        <Footer />
      </div>
    </div>
  );
}

export default App;
