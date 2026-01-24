import { ShoppingCart, User, Search, Menu, X, Home, Grid } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Navbar = ({ isLoggedIn = false }) => {
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();

  const [showSearch, setShowSearch] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [searchText, setSearchText] = useState("");
  useEffect(() => {
    const keywordFromUrl = location.pathname.startsWith("/search/")
      ? decodeURIComponent(location.pathname.split("/search/")[1] || "")
      : "";

    setSearchText(keywordFromUrl);
  }, [location.pathname]);

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 10);
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  // 🔍 SEARCH SUBMIT (DESKTOP + MOBILE SAFE)
  const submitSearch = () => {
    if (!searchText.trim()) return;

    navigate(`/search/${encodeURIComponent(searchText.trim())}`);
    setSearchText("");
    setShowSearch(false);
    setShowMenu(false);
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
          {/* LEFT - HAMBURGER (MOBILE) */}
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
                className="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer"
              >
                <Search size={18} />
              </button>
            </div>
          </div>

          {/* RIGHT */}
          <div className="flex items-center gap-4">
            {/* MOBILE SEARCH ICON */}
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
              className={`w-11 h-5 rounded-full px-1 flex items-center cursor-pointer
                ${theme === "dark" ? "bg-gray-700" : "bg-gray-300"}
              `}
            >
              <div
                className={`w-4 h-4 bg-white rounded-full transition-transform
                  ${theme === "dark" ? "translate-x-6" : ""}
                `}
              />
            </button>

            {/* PROFILE (DESKTOP) */}
            <div className="hidden md:block">
              {isLoggedIn ? (
                <User
                  className="cursor-pointer"
                  onClick={() => navigate("/profile")}
                />
              ) : (
                <button
                  onClick={() => navigate("/auth")}
                  className="font-medium hover:underline cursor-pointer"
                >
                  Sign In / Sign Up
                </button>
              )}
            </div>

            {/* CART */}
            <div
              onClick={() => navigate("/cart")}
              className="relative cursor-pointer"
            >
              <ShoppingCart />
              <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full px-1">
                0
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
      theme === "dark" ? "bg-black border-gray-800" : "bg-white border-gray-200"
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
              className={`flex-1 rounded-full px-4 py-2 outline-none
                ${
                  theme === "dark"
                    ? "bg-[#111] text-white"
                    : "bg-gray-100 text-black"
                }
              `}
            />
            <button
              type="submit"
              className="px-4 py-2 rounded-full bg-blue-600 text-white "
            >
              Search
            </button>
          </form>
        </div>
      )}

      {/* MOBILE DRAWER MENU */}
      {showMenu && (
        <div
          className="md:hidden fixed inset-0 z-50 bg-black/50"
          onClick={() => setShowMenu(false)}
        >
          <div
            className={`absolute top-0 left-0 w-64 h-full p-4
              ${
                theme === "dark" ? "bg-black text-white" : "bg-white text-black"
              }
            `}
            onClick={(e) => e.stopPropagation()}
          >
            {/* HEADER */}
            <div className="flex items-center justify-between mb-6">
              <span className="text-lg font-bold">Menu</span>
              <button onClick={() => setShowMenu(false)}>
                <X />
              </button>
            </div>

            {/* MENU ITEMS */}
            <div className="space-y-4">
              <button
                onClick={() => {
                  navigate("/");
                  setShowMenu(false);
                }}
                className="flex items-center gap-3"
              >
                <Home size={18} /> Home
              </button>

              <button
                onClick={() => {
                  navigate("/categories");
                  setShowMenu(false);
                }}
                className="flex items-center gap-3"
              >
                <Grid size={18} /> Categories
              </button>

              <hr className="opacity-30" />

              {isLoggedIn ? (
                <button
                  onClick={() => {
                    navigate("/profile");
                    setShowMenu(false);
                  }}
                  className="flex items-center gap-3"
                >
                  <User size={18} /> Profile
                </button>
              ) : (
                <button
                  onClick={() => {
                    navigate("/auth");
                    setShowMenu(false);
                  }}
                  className="flex items-center gap-3"
                >
                  <User size={18} /> Sign In / Sign Up
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default Navbar;
