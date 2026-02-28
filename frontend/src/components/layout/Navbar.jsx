import {
  ShoppingCart,
  User,
  Search,
  Menu,
  X,
  Home,
  Grid,
  LogOut,
  Package,
  MapPin,
  Heart,
} from "lucide-react";
import { useTheme } from "../../context/ThemeContext";
import { useState, useEffect, useRef } from "react";
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

  const menuRef = useRef();

  /* -------------------- Sync Search with URL -------------------- */
  useEffect(() => {
    const keywordFromUrl = location.pathname.startsWith("/search/")
      ? decodeURIComponent(location.pathname.split("/search/")[1] || "")
      : "";
    setSearchText(keywordFromUrl);
  }, [location.pathname]);

  /* -------------------- Scroll Effect -------------------- */
  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 50);
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  /* -------------------- Close Drawer on Outside Click -------------------- */
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setShowMenu(false);
      }
    };

    if (showMenu) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showMenu]);

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
      {/* NAVBAR */}
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
          {/* HAMBURGER */}
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
                className={`w-4 h-4 bg-white rounded-full transition-transform duration-300
                  ${theme === "dark" ? "translate-x-6" : ""}
                `}
              />
            </button>

            {/* PROFILE (HOVER DROPDOWN) */}
            <div
              className="relative hidden md:block"
              onMouseEnter={() => setShowProfileMenu(true)}
              onMouseLeave={() => setShowProfileMenu(false)}
            >
              {!isAuthenticated ? (
                <button
                  onClick={() => navigate("/auth")}
                  className="font-medium hover:underline"
                >
                  Sign In / Sign Up
                </button>
              ) : (
                <>
                  <button className="flex items-center gap-2">
                    <User size={20} />
                    <span>{user?.userName}</span>
                  </button>

                  {/* DROPDOWN */}
                  <div
                    className={`absolute right-0 mt-2 w-72 rounded-lg shadow-2xl border transition-all duration-200 origin-top-right transform
                      ${
                        showProfileMenu
                          ? "opacity-100 scale-100 visible translate-y-0"
                          : "opacity-0 scale-95 invisible -translate-y-2"
                      }
                      ${
                        theme === "dark"
                          ? "bg-[#0f0f0f] border-gray-800 text-white"
                          : "bg-white border-gray-200 text-gray-800"
                      }
                    `}
                  >
                    {/* HEADER */}
                    <div
                      className={`px-4 py-3 border-b
                        ${
                          theme === "dark"
                            ? "border-gray-800"
                            : "border-gray-200"
                        }
                      `}
                    >
                      <p className="font-semibold">{user?.userName}</p>
                      <p className="text-sm opacity-70">Manage your account</p>
                    </div>

                    {/* MENU ITEMS */}
                    <div className="flex flex-col text-sm">
                      <button
                        onClick={() => navigate("/profile")}
                        className={`flex items-center gap-3 px-4 py-3 transition-colors
                          ${
                            theme === "dark"
                              ? "hover:bg-gray-800"
                              : "hover:bg-gray-100"
                          }
                        `}
                      >
                        <User size={16} /> My Profile
                      </button>

                      <button
                        onClick={() => navigate("/orders")}
                        className={`flex items-center gap-3 px-4 py-3 transition-colors
                          ${
                            theme === "dark"
                              ? "hover:bg-gray-800"
                              : "hover:bg-gray-100"
                          }
                        `}
                      >
                        <Package size={16} /> Orders
                      </button>

                      <button
                        onClick={() => navigate("/addresses")}
                        className={`flex items-center gap-3 px-4 py-3 transition-colors
                          ${
                            theme === "dark"
                              ? "hover:bg-gray-800"
                              : "hover:bg-gray-100"
                          }
                        `}
                      >
                        <MapPin size={16} /> Saved Addresses
                      </button>

                      <button
                        onClick={() => navigate("/wishlist")}
                        className={`flex items-center gap-3 px-4 py-3 transition-colors
                          ${
                            theme === "dark"
                              ? "hover:bg-gray-800"
                              : "hover:bg-gray-100"
                          }
                        `}
                      >
                        <Heart size={16} /> Wishlist
                      </button>

                      <button
                        onClick={handleLogout}
                        className={`flex items-center gap-3 px-4 py-3 text-red-500 transition-colors
                          ${
                            theme === "dark"
                              ? "hover:bg-gray-800"
                              : "hover:bg-gray-100"
                          }
                        `}
                      >
                        <LogOut size={16} /> Logout
                      </button>
                    </div>
                  </div>
                </>
              )}
            </div>

            {/* CART */}
            <div
              onClick={() => navigate("/cart")}
              className="relative cursor-pointer"
            >
              <ShoppingCart />
              {cartCount > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full px-1">
                  {cartCount}
                </span>
              )}
            </div>
          </div>
        </div>
      </nav>

      {/* MOBILE MENU DRAWER */}
      <div
        className={`fixed inset-0 z-50 md:hidden transition-all duration-300 ${
          showMenu ? "visible" : "invisible"
        }`}
      >
        {/* BACKDROP */}
        <div
          className={`absolute inset-0 bg-black transition-opacity duration-300 ${
            showMenu ? "opacity-50" : "opacity-0"
          }`}
          onClick={() => setShowMenu(false)}
        />

        {/* DRAWER */}
        <div
          ref={menuRef}
          className={`absolute top-0 left-0 h-full w-80 transform transition-transform duration-300 ${
            showMenu ? "translate-x-0" : "-translate-x-full"
          } ${theme === "dark" ? "bg-black" : "bg-white"} shadow-xl`}
        >
          {/* DRAWER HEADER */}
          <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-800">
            <span className="text-xl font-bold">Menu</span>
            <button onClick={() => setShowMenu(false)}>
              <X size={24} />
            </button>
          </div>

          {/* DRAWER CONTENT */}
          <div className="p-4">
            {/* USER SECTION */}
            <div className="mb-6">
              {!isAuthenticated ? (
                <button
                  onClick={() => {
                    navigate("/auth");
                    setShowMenu(false);
                  }}
                  className="w-full py-2 px-4 bg-blue-500 text-white rounded-lg font-medium"
                >
                  Sign In / Sign Up
                </button>
              ) : (
                <div className="space-y-1">
                  <p className="font-semibold">{user?.userName}</p>
                  <p className="text-sm opacity-70">{user?.email}</p>
                </div>
              )}
            </div>

            {/* NAVIGATION LINKS */}
            <div className="space-y-2">
              <button
                onClick={() => {
                  navigate("/");
                  setShowMenu(false);
                }}
                className={`flex items-center gap-3 w-full p-3 rounded-lg transition-colors ${
                  theme === "dark" ? "hover:bg-gray-800" : "hover:bg-gray-100"
                }`}
              >
                <Home size={20} /> Home
              </button>

              {isAuthenticated && (
                <>
                  <button
                    onClick={() => {
                      navigate("/profile");
                      setShowMenu(false);
                    }}
                    className={`flex items-center gap-3 w-full p-3 rounded-lg transition-colors ${
                      theme === "dark"
                        ? "hover:bg-gray-800"
                        : "hover:bg-gray-100"
                    }`}
                  >
                    <User size={20} /> Profile
                  </button>

                  <button
                    onClick={() => {
                      navigate("/orders");
                      setShowMenu(false);
                    }}
                    className={`flex items-center gap-3 w-full p-3 rounded-lg transition-colors ${
                      theme === "dark"
                        ? "hover:bg-gray-800"
                        : "hover:bg-gray-100"
                    }`}
                  >
                    <Package size={20} /> Orders
                  </button>

                  <button
                    onClick={() => {
                      navigate("/addresses");
                      setShowMenu(false);
                    }}
                    className={`flex items-center gap-3 w-full p-3 rounded-lg transition-colors ${
                      theme === "dark"
                        ? "hover:bg-gray-800"
                        : "hover:bg-gray-100"
                    }`}
                  >
                    <MapPin size={20} /> Addresses
                  </button>

                  <button
                    onClick={() => {
                      navigate("/wishlist");
                      setShowMenu(false);
                    }}
                    className={`flex items-center gap-3 w-full p-3 rounded-lg transition-colors ${
                      theme === "dark"
                        ? "hover:bg-gray-800"
                        : "hover:bg-gray-100"
                    }`}
                  >
                    <Heart size={20} /> Wishlist
                  </button>

                  <button
                    onClick={() => {
                      handleLogout();
                      setShowMenu(false);
                    }}
                    className="flex items-center gap-3 w-full p-3 rounded-lg text-red-500 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                  >
                    <LogOut size={20} /> Logout
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* MOBILE SEARCH DRAWER */}
      <div
        className={`fixed top-16 left-0 right-0 z-40 md:hidden transition-all duration-300 ${
          showSearch ? "translate-y-0" : "-translate-y-full"
        } ${
          theme === "dark" ? "bg-black" : "bg-white"
        } border-b border-gray-200 dark:border-gray-800 p-4 shadow-lg`}
      >
        <div className="relative">
          <input
            type="search"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && submitSearch()}
            placeholder="Search products..."
            className={`w-full rounded-full px-5 py-3 pr-12 outline-none
              ${
                theme === "dark"
                  ? "bg-[#111] text-white"
                  : "bg-gray-100 text-black"
              }
            `}
            autoFocus
          />
          <button
            onClick={submitSearch}
            className="absolute right-3 top-1/2 -translate-y-1/2"
          >
            <Search size={18} />
          </button>
        </div>
      </div>
    </>
  );
};

export default Navbar;
