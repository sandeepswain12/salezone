import { ShoppingCart, User, Search } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Navbar = ({ isLoggedIn = false }) => {
  const { theme, toggleTheme } = useTheme();
  const [showSearch, setShowSearch] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 10);
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

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
          {/* LEFT - LOGO */}
          <span
            onClick={() => navigate("/")}
            className="text-xl font-bold cursor-pointer"
          >
            SaleZone
          </span>

          {/* CENTER - SEARCH (DESKTOP ONLY) */}
          <div className="flex-1 hidden md:flex justify-center px-6">
            <input
              type="text"
              placeholder="Search products..."
              className={`
  w-full max-w-xl rounded-full px-5 py-2
  outline-none
  focus:ring-2 focus:ring-grey-500
  ${theme === "dark" ? "bg-[#111] text-white" : "bg-gray-100 text-black"}
`}
            />
          </div>

          {/* RIGHT */}
          <div className="flex items-center gap-4">
            {/* SEARCH BUTTON (MOBILE ONLY) */}
            <button
              className="md:hidden"
              onClick={() => setShowSearch(!showSearch)}
            >
              <Search />
            </button>

            {/* THEME TOGGLE */}
            <button
              onClick={toggleTheme}
              className={`w-11 h-5 rounded-full px-2px
  flex items-center cursor-pointer
  ${theme === "dark" ? "bg-gray-700" : "bg-gray-300"}
`}
            >
              <div
                className={`
      w-4 h-4 bg-white rounded-full
      transition-transform duration-300
      ${theme === "dark" ? "translate-x-6" : ""}
    `}
              />
            </button>

            {/* PROFILE (DESKTOP ONLY) */}
            <div className="hidden md:block cursor-pointer">
              {isLoggedIn ? (
                <User className="cursor-pointer" />
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
              <span
                className="
                  absolute -top-2 -right-2
                  bg-red-500 text-white
                  text-xs rounded-full px-1
                "
              >
                0
              </span>
            </div>
          </div>
        </div>
      </nav>

      {/* MOBILE SEARCH PANEL */}
      {showSearch && (
        <div
          className="
            md:hidden fixed top-14 w-full z-40
            bg-white dark:bg-black
            border-b border-gray-200 dark:border-gray-800
          "
        >
          <div className="px-4 py-4 space-y-4">
            <input
              type="text"
              placeholder="Search products..."
              className={`
  w-full rounded-full px-4 py-2 outline-none
  ${theme === "dark" ? "bg-[#111] text-white" : "bg-gray-100 text-black"}
`}
            />

            {/* AUTH */}
            {isLoggedIn ? (
              <button className="w-full flex items-center gap-2">
                <User /> Profile
              </button>
            ) : (
              <button
                onClick={() => navigate("/auth")}
                className="font-medium hover:underline cursor-pointer"
              >
                Sign In / Sign Up
              </button>
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default Navbar;
