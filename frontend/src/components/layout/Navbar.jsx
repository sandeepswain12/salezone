import {
  ShoppingCart,
  User,
  Search,
  Menu,
  X,
  Home,
  Grid,
  LogOut,
} from "lucide-react";
import { useTheme } from "../../context/ThemeContext";
import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useCart } from "../../context/CartContext";
import { useAuth } from "../../context/AuthContext";
import { useToast } from "../../context/ToastContext";

const Navbar = () => {
  const { theme, toggleTheme } = useTheme();
  const { cartCount } = useCart();
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { showToast } = useToast();
  const [showSearch, setShowSearch] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  // Sync search with URL
  useEffect(() => {
    const keywordFromUrl = location.pathname.startsWith("/search/")
      ? decodeURIComponent(location.pathname.split("/search/")[1] || "")
      : "";

    setSearchText(keywordFromUrl);
  }, [location.pathname]);

  // Scroll effect
  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 10);
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  const submitSearch = () => {
    if (!searchText.trim()) return;
    navigate(`/search/${encodeURIComponent(searchText.trim())}`);
    setShowSearch(false);
    setShowMenu(false);
  };

  const handleLogout = async () => {
    await logout();
    showToast("Logged out successfully 👋", "success");
    navigate("/");
  };

  return (
    <>
      <nav
        className={`fixed top-0 w-full z-50 transition-all duration-300
          ${
            scrolled
              ? theme === "dark"
                ? "bg-black/80 backdrop-blur-md border-b border-gray-800 shadow-lg"
                : "bg-white/80 backdrop-blur-md border-b border-gray-200 shadow-sm"
              : theme === "dark"
              ? "bg-black border-b border-gray-800"
              : "bg-white border-b border-gray-200"
          }
        `}
      >
        <div className="max-w-7xl mx-auto h-16 px-4 flex items-center justify-between">
          {/* MOBILE MENU BUTTON */}
          <button
            className="md:hidden"
            onClick={() => {
              setShowMenu(true);
              setShowSearch(false);
            }}
          >
            <Menu />
          </button>

          {/* LOGO */}
          <span
            onClick={() => navigate("/")}
            className="text-2xl font-bold cursor-pointer"
          >
            SaleZone
          </span>

          {/* DESKTOP SEARCH */}
          <div className="hidden md:flex flex-1 justify-center px-6">
            <div className="relative w-full max-w-xl">
              <input
                type="search"
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && submitSearch()}
                placeholder="Search products..."
                className={`w-full rounded-full px-5 py-2 pr-12 outline-none
                  ${
                    theme === "dark"
                      ? "bg-[#111] text-white"
                      : "bg-gray-100 text-black"
                  }
                `}
              />
              <button
                onClick={submitSearch}
                className="absolute right-3 top-1/2 -translate-y-1/2"
              >
                <Search size={18} />
              </button>
            </div>
          </div>

          {/* RIGHT SECTION */}
          <div className="flex items-center gap-4">
            {/* MOBILE SEARCH */}
            <button
              className="md:hidden"
              onClick={() => {
                setShowSearch(!showSearch);
                setShowMenu(false);
              }}
            >
              <Search />
            </button>

            {/* THEME TOGGLE */}
            <button
              onClick={toggleTheme}
              className={`w-11 h-5 rounded-full px-1 flex items-center
                ${theme === "dark" ? "bg-gray-700" : "bg-gray-300"}
              `}
            >
              <div
                className={`w-4 h-4 bg-white rounded-full transition-transform
                  ${theme === "dark" ? "translate-x-6" : ""}
                `}
              />
            </button>

            {/* AUTH SECTION */}
            <div className="relative hidden md:block">
              {!isAuthenticated ? (
                <button
                  onClick={() => navigate("/auth")}
                  className="font-medium hover:underline"
                >
                  Sign In / Sign Up
                </button>
              ) : (
                <>
                  <button
                    onClick={() => setShowProfileMenu(!showProfileMenu)}
                    className="flex items-center gap-2"
                  >
                    <User size={20} />
                    <span>{user?.userName}</span>
                  </button>

                  {showProfileMenu && (
                    <div className="absolute right-0 mt-2 w-40 bg-white shadow-lg rounded-lg overflow-hidden">
                      <button
                        onClick={() => navigate("/profile")}
                        className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                      >
                        Profile
                      </button>
                      <button
                        onClick={handleLogout}
                        className="block w-full text-left px-4 py-2 hover:bg-gray-100 text-red-500 flex items-center gap-2"
                      >
                        <LogOut size={16} /> Logout
                      </button>
                    </div>
                  )}
                </>
              )}
            </div>

            {/* CART */}
            <div
              onClick={() => navigate("/cart")}
              className="relative cursor-pointer"
            >
              <ShoppingCart />
              <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full px-1">
                {cartCount}
              </span>
            </div>
          </div>
        </div>
      </nav>

      {/* MOBILE SEARCH PANEL */}
      {showSearch && (
        <div
          className={`md:hidden fixed top-16 w-full z-40 border-b
            ${
              theme === "dark"
                ? "bg-black border-gray-800"
                : "bg-white border-gray-200"
            }
          `}
        >
          <form
            onSubmit={(e) => {
              e.preventDefault();
              submitSearch();
            }}
            className="px-4 py-4 flex gap-2"
          >
            <input
              type="search"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              autoFocus
              placeholder="Search products..."
              className="flex-1 rounded-full px-4 py-2 outline-none bg-gray-100"
            />
            <button
              type="submit"
              className="px-4 py-2 rounded-full bg-blue-600 text-white"
            >
              Search
            </button>
          </form>
        </div>
      )}
    </>
  );
};

export default Navbar;
