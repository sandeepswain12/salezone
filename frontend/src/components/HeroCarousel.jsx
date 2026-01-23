import { useEffect, useState } from "react";
import { useTheme } from "../context/ThemeContext";

const slides = [
  // {
  //   title: "Big Sale is Live 🔥",
  //   subtitle: "Up to 50% off on top brands",
  //   cta: "Shop Now",
  //   image:
  //     "https://images.unsplash.com/photo-1515169067865-5387ec356754?q=80&w=1600",
  // },
  {
    title: "Latest Electronics ⚡",
    subtitle: "Smartphones, Laptops & more",
    cta: "Explore",
    image:
      "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=1600",
  },
  {
    title: "Fashion Trends 👕",
    subtitle: "New arrivals for everyone",
    cta: "View Collection",
    image:
      "https://images.unsplash.com/photo-1521334884684-d80222895322?q=80&w=1600",
  },
  {
    title: "Home Essentials 🏠",
    subtitle: "Upgrade your living space",
    cta: "Shop Home",
    image:
      "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?q=80&w=1600",
  },
  {
    title: "Groceries Delivered 🛒",
    subtitle: "Fresh items at your doorstep",
    cta: "Order Now",
    image:
      "https://images.unsplash.com/photo-1542838132-92c53300491e?q=80&w=1600",
  },
];

const HeroCarousel = () => {
  const { theme } = useTheme();
  const [index, setIndex] = useState(0);

  // Auto slide
  useEffect(() => {
    const timer = setInterval(() => {
      setIndex((prev) => (prev + 1) % slides.length);
    }, 3000);
    return () => clearInterval(timer);
  }, []);

  return (
    <section
      className={`w-full overflow-hidden
        ${theme === "dark" ? "bg-black text-white" : "bg-white text-black"}
      `}
    >
      <div className="relative h-[60vh] md:h-[70vh]">
        {/* SLIDES */}
        {slides.map((slide, i) => (
          <div
            key={i}
            className={`absolute inset-0 transition-opacity duration-700
              ${i === index ? "opacity-100" : "opacity-0"}
            `}
          >
            {/* Background image */}
            <img
              src={slide.image}
              alt={slide.title}
              className="w-full h-full object-cover"
            />

            {/* Overlay */}
            <div
              className={`absolute inset-0
                ${theme === "dark" ? "bg-black/60" : "bg-white/60"}
              `}
            />

            {/* Content */}
            <div className="absolute inset-0 flex items-center">
              <div className="max-w-7xl mx-auto px-6">
                <h1 className="text-3xl md:text-5xl font-bold mb-4">
                  {slide.title}
                </h1>
                <p className="text-lg md:text-xl mb-6">{slide.subtitle}</p>
                <button
                  className="px-6 py-3 rounded-full font-semibold
                  bg-blue-600 text-white hover:bg-blue-700 transition"
                >
                  {slide.cta}
                </button>
              </div>
            </div>
          </div>
        ))}

        {/* DOTS */}
        <div className="absolute bottom-5 w-full flex justify-center gap-2">
          {slides.map((_, i) => (
            <button
              key={i}
              onClick={() => setIndex(i)}
              className={`w-3 h-3 rounded-full transition
                ${
                  i === index
                    ? "bg-blue-600"
                    : theme === "dark"
                    ? "bg-gray-600"
                    : "bg-gray-300"
                }
              `}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default HeroCarousel;
