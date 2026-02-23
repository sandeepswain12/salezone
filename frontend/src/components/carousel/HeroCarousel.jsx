import { useEffect, useState, useRef } from "react";
import { useTheme } from "../../context/ThemeContext";

const slides = [
  {
    title: "Books for Every Mind 📚",
    subtitle: "Novels, Academics & Self-Growth",
    cta: "Browse Books",
    image: "/carousel/books.jpg",
  },
  {
    title: "Latest Electronics ⚡",
    subtitle: "Mobiles, Laptops & Smart Gadgets",
    cta: "Explore Electronics",
    image: "/carousel/mobiles.jpg",
  },
  {
    title: "Fashion That Defines You 👕",
    subtitle: "Trending Styles for Men & Women",
    cta: "Shop Fashion",
    image: "/carousel/fashion.jpg",
  },
  {
    title: "Smart Home Appliances 🏠",
    subtitle: "Comfort, Style & Technology",
    cta: "View Appliances",
    image: "/carousel/home.jpg",
  },
  {
    title: "Sports & Fitness Gear 🏋️",
    subtitle: "Train Hard. Stay Fit.",
    cta: "Shop Fitness",
    image: "/carousel/gym.jpg",
  },
];

const HeroCarousel = () => {
  const { theme } = useTheme();
  const [index, setIndex] = useState(0);
  const intervalRef = useRef(null);
  const touchStartX = useRef(0);

  const startAutoSlide = () => {
    intervalRef.current = setInterval(() => {
      setIndex((prev) => (prev + 1) % slides.length);
    }, 3000);
  };

  const stopAutoSlide = () => {
    clearInterval(intervalRef.current);
  };

  useEffect(() => {
    startAutoSlide();
    return stopAutoSlide;
  }, []);

  const nextSlide = () => {
    stopAutoSlide();
    setIndex((prev) => (prev + 1) % slides.length);
    startAutoSlide();
  };

  const prevSlide = () => {
    stopAutoSlide();
    setIndex((prev) => (prev - 1 + slides.length) % slides.length);
    startAutoSlide();
  };

  /* 👉 TOUCH EVENTS FOR MOBILE */
  const handleTouchStart = (e) => {
    touchStartX.current = e.touches[0].clientX;
  };

  const handleTouchEnd = (e) => {
    const touchEndX = e.changedTouches[0].clientX;
    const diff = touchStartX.current - touchEndX;

    if (diff > 50) nextSlide(); // swipe left
    if (diff < -50) prevSlide(); // swipe right
  };

  return (
    <section
      className={`w-full overflow-hidden ${
        theme === "dark" ? "bg-black text-white" : "bg-white text-black"
      }`}
    >
      <div
        className="relative h-[55vh] md:h-[70vh]"
        onTouchStart={handleTouchStart}
        onTouchEnd={handleTouchEnd}
      >
        {/* SLIDES */}
        {slides.map((slide, i) => (
          <div
            key={i}
            className={`absolute inset-0 transition-opacity duration-700 ${
              i === index ? "opacity-100" : "opacity-0"
            }`}
          >
            <img
              src={slide.image}
              alt={slide.title}
              className="w-full h-full object-cover"
            />

            <div
              className={`absolute inset-0 ${
                theme === "dark" ? "bg-black/60" : "bg-white/60"
              }`}
            />

            <div className="absolute inset-0 flex items-center">
              <div className="max-w-7xl mx-auto px-6">
                <h1 className="text-2xl md:text-5xl font-bold mb-3">
                  {slide.title}
                </h1>
                <p className="text-base md:text-xl mb-5">{slide.subtitle}</p>
                <button className="px-6 py-3 rounded-full bg-blue-600 text-white font-semibold hover:bg-blue-700 transition">
                  {slide.cta}
                </button>
              </div>
            </div>
          </div>
        ))}

        {/* DESKTOP ARROWS ONLY */}
        <button
          onClick={prevSlide}
          className="hidden md:flex absolute left-5 top-1/2 -translate-y-1/2
          bg-black/50 text-white w-12 h-12 rounded-full
          items-center justify-center hover:bg-black/70 transition"
        >
          ❮
        </button>

        <button
          onClick={nextSlide}
          className="hidden md:flex absolute right-5 top-1/2 -translate-y-1/2
          bg-black/50 text-white w-12 h-12 rounded-full
          items-center justify-center hover:bg-black/70 transition"
        >
          ❯
        </button>

        {/* DOTS */}
        <div className="absolute bottom-4 w-full flex justify-center gap-2">
          {slides.map((_, i) => (
            <button
              key={i}
              onClick={() => setIndex(i)}
              className={`w-3 h-3 rounded-full ${
                i === index
                  ? "bg-blue-600"
                  : theme === "dark"
                  ? "bg-gray-600"
                  : "bg-gray-300"
              }`}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default HeroCarousel;
