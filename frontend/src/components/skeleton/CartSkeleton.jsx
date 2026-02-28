const CartSkeleton = () => {
  return (
    <section className="max-w-7xl mx-auto px-4 py-12 animate-pulse">
      <div className="flex justify-between items-center mb-8">
        <div className="h-8 w-48 bg-gray-300 dark:bg-gray-700 rounded"></div>
        <div className="h-6 w-24 bg-gray-300 dark:bg-gray-700 rounded"></div>
      </div>

      <div className="grid gap-8 md:grid-cols-3">
        {/* Left Side Items */}
        <div className="md:col-span-2 space-y-6">
          {[1, 2, 3].map((_, i) => (
            <div
              key={i}
              className="flex gap-5 p-5 rounded-2xl bg-gray-100 dark:bg-[#121212]"
            >
              <div className="w-24 h-24 bg-gray-300 dark:bg-gray-700 rounded-lg"></div>

              <div className="flex-1 space-y-4">
                <div className="h-5 w-3/4 bg-gray-300 dark:bg-gray-700 rounded"></div>
                <div className="h-4 w-1/3 bg-gray-300 dark:bg-gray-700 rounded"></div>

                <div className="h-10 w-28 bg-gray-300 dark:bg-gray-700 rounded-lg"></div>
              </div>

              <div className="h-6 w-6 bg-gray-300 dark:bg-gray-700 rounded"></div>
            </div>
          ))}
        </div>

        {/* Right Side Summary */}
        <div className="p-6 rounded-xl bg-gray-100 dark:bg-[#0f0f0f] space-y-4">
          <div className="h-6 w-32 bg-gray-300 dark:bg-gray-700 rounded"></div>

          <div className="h-4 w-full bg-gray-300 dark:bg-gray-700 rounded"></div>
          <div className="h-4 w-full bg-gray-300 dark:bg-gray-700 rounded"></div>
          <div className="h-4 w-full bg-gray-300 dark:bg-gray-700 rounded"></div>

          <div className="h-10 w-full bg-gray-300 dark:bg-gray-700 rounded-xl mt-6"></div>
        </div>
      </div>
    </section>
  );
};

export default CartSkeleton;
