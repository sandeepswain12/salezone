import { useTheme } from "../../context/ThemeContext";

const CartSkeleton = () => {
  const { theme } = useTheme();

  const skeleton = theme === "dark" ? "bg-gray-800" : "bg-gray-200";
  const cardBg = theme === "dark" ? "bg-[#121212]" : "bg-gray-100";
  const summaryBg = theme === "dark" ? "bg-[#0f0f0f]" : "bg-gray-100";

  return (
    <section className="max-w-7xl mx-auto px-4 py-12 animate-pulse">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <div className={`h-8 w-48 ${skeleton} rounded`}></div>
        <div className={`h-6 w-24 ${skeleton} rounded`}></div>
      </div>

      <div className="grid gap-8 md:grid-cols-3">
        {/* Left Side Items */}
        <div className="md:col-span-2 space-y-6">
          {[1, 2, 3].map((_, i) => (
            <div key={i} className={`flex gap-5 p-5 rounded-2xl ${cardBg}`}>
              <div className={`w-24 h-24 ${skeleton} rounded-lg`}></div>

              <div className="flex-1 space-y-4">
                <div className={`h-5 w-3/4 ${skeleton} rounded`}></div>
                <div className={`h-4 w-1/3 ${skeleton} rounded`}></div>

                <div className={`h-10 w-28 ${skeleton} rounded-lg`}></div>
              </div>

              <div className={`h-6 w-6 ${skeleton} rounded`}></div>
            </div>
          ))}
        </div>

        {/* Right Side Summary */}
        <div className={`p-6 rounded-xl ${summaryBg} space-y-4`}>
          <div className={`h-6 w-32 ${skeleton} rounded`}></div>

          <div className={`h-4 w-full ${skeleton} rounded`}></div>
          <div className={`h-4 w-full ${skeleton} rounded`}></div>
          <div className={`h-4 w-full ${skeleton} rounded`}></div>

          <div className={`h-10 w-full ${skeleton} rounded-xl mt-6`}></div>
        </div>
      </div>
    </section>
  );
};

export default CartSkeleton;
