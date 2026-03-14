import { Routes, Route } from "react-router-dom";
import { useEffect } from "react";
import axios from "axios";

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
import ProtectedRoute from "./components/protectedroute/ProtectedRoute";
import AuthSuccess from "./pages/AuthSuccess";
import AuthFailure from "./pages/AuthFailure";
import Orders from "./pages/Orders";
import Profile from "./pages/Profile";

import { setAccessToken } from "./services/api";

function App() {
  const { theme } = useTheme();

  // Restore session on page refresh
  // useEffect(() => {
  //   const restoreSession = async () => {
  //     try {
  //       const res = await axios.post(
  //         import.meta.env.VITE_REFRESH_URL,
  //         {},
  //         { withCredentials: true }
  //       );

  //       setAccessToken(res.data.accessToken);
  //     } catch (err) {
  //       console.log("No active session");
  //     }
  //   };

  //   restoreSession();
  // }, []);

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
          {/* Home */}
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

          {/* Public Routes */}
          <Route path="/product/:id" element={<ProductDetails />} />
          <Route path="/categories" element={<CategorySection />} />
          <Route path="/category/:categoryId" element={<CategoryProducts />} />
          <Route path="/search/:keyword" element={<SearchResults />} />
          <Route path="/auth" element={<Auth />} />
          <Route path="/auth/success" element={<AuthSuccess />} />
          <Route path="/auth/failure" element={<AuthFailure />} />

          {/* Protected Routes */}
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

          <Route
            path="/orders"
            element={
              <ProtectedRoute>
                <Orders />
              </ProtectedRoute>
            }
          />

          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
        </Routes>
      </div>

      <Footer />
    </div>
  );
}

export default App;
