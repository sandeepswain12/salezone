import { useTheme } from "../context/ThemeContext";

const Footer = () => {
  const { theme } = useTheme();

  const bg =
    theme === "dark" ? "bg-black text-white" : "bg-[#f8f9fb] text-black";
  const border = theme === "dark" ? "border-gray-800" : "border-gray-300";
  const muted = theme === "dark" ? "text-gray-400" : "text-gray-600";

  return (
    <footer className={`${bg} border-t ${border} mt-20`}>
      <div className="max-w-7xl mx-auto px-6 py-14 grid gap-10 sm:grid-cols-2 md:grid-cols-4">
        {/* BRAND */}
        <div>
          <h3 className="text-2xl font-bold mb-3">SaleZone</h3>
          <p className={`text-sm leading-relaxed ${muted}`}>
            SaleZone is your trusted online shopping destination for
            electronics, fashion, and daily essentials at the best prices.
          </p>
        </div>

        {/* SHOP */}
        <div>
          <h4 className="font-semibold mb-4">Shop</h4>
          <ul className={`space-y-3 text-sm ${muted}`}>
            <li className="hover:underline cursor-pointer">Electronics</li>
            <li className="hover:underline cursor-pointer">Fashion</li>
            <li className="hover:underline cursor-pointer">Groceries</li>
            <li className="hover:underline cursor-pointer">Home & Living</li>
          </ul>
        </div>

        {/* COMPANY */}
        <div>
          <h4 className="font-semibold mb-4">Company</h4>
          <ul className={`space-y-3 text-sm ${muted}`}>
            <li className="hover:underline cursor-pointer">About Us</li>
            <li className="hover:underline cursor-pointer">Careers</li>
            <li className="hover:underline cursor-pointer">Contact</li>
            <li className="hover:underline cursor-pointer">Blog</li>
          </ul>
        </div>

        {/* SUPPORT */}
        <div>
          <h4 className="font-semibold mb-4">Support</h4>
          <ul className={`space-y-3 text-sm ${muted}`}>
            <li className="hover:underline cursor-pointer">Help Center</li>
            <li className="hover:underline cursor-pointer">Privacy Policy</li>
            <li className="hover:underline cursor-pointer">
              Terms & Conditions
            </li>
            <li className="hover:underline cursor-pointer">Returns</li>
          </ul>
        </div>
      </div>

      {/* BOTTOM BAR */}
      <div className={`border-t ${border}`}>
        <div className="max-w-7xl mx-auto px-6 py-4 flex flex-col md:flex-row items-center justify-between gap-4">
          <p className={`text-sm ${muted}`}>
            © {new Date().getFullYear()} SaleZone. All rights reserved.
          </p>

          <div className={`text-sm ${muted}`}>
            Made with ❤️ for modern shopping
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
