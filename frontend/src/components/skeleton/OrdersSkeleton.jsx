import { useTheme } from "../../context/ThemeContext";

const OrdersSkeleton = () => {
  const { theme } = useTheme();

  const skeleton = theme === "dark" ? "bg-gray-800" : "bg-gray-200";

  return (
    <section className="max-w-7xl mx-auto px-4 py-12 animate-pulse">
      {/* Page Title */}
      <div className={`h-8 w-48 ${skeleton} rounded mb-10`}></div>

      <div className="space-y-8">
        {[...Array(3)].map((_, index) => (
          <div
            key={index}
            className={`p-6 rounded-2xl border ${
              theme === "dark"
                ? "bg-[#0f0f0f] border-gray-800"
                : "bg-white border-gray-200 shadow-sm"
            }`}
          >
            {/* Order Header */}
            <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4 mb-6">
              <div className="space-y-2">
                <div className={`h-3 w-20 ${skeleton} rounded`}></div>
                <div className={`h-4 w-64 ${skeleton} rounded`}></div>
                <div className={`h-3 w-40 ${skeleton} rounded`}></div>
              </div>

              <div className={`h-6 w-24 ${skeleton} rounded-full`}></div>
            </div>

            {/* Products */}
            <div className="space-y-4 mb-6">
              {[...Array(2)].map((_, i) => (
                <div key={i} className="flex items-center gap-4 border-t pt-4">
                  <div className={`w-16 h-16 ${skeleton} rounded-lg`}></div>

                  <div className="flex-1 space-y-2">
                    <div className={`h-4 w-40 ${skeleton} rounded`}></div>
                    <div className={`h-3 w-20 ${skeleton} rounded`}></div>
                  </div>

                  <div className={`h-4 w-16 ${skeleton} rounded`}></div>
                </div>
              ))}
            </div>

            {/* Summary */}
            <div className="grid md:grid-cols-4 gap-4">
              {[...Array(4)].map((_, i) => (
                <div key={i} className="space-y-2">
                  <div className={`h-3 w-24 ${skeleton} rounded`}></div>
                  <div className={`h-4 w-20 ${skeleton} rounded`}></div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

export default OrdersSkeleton;
