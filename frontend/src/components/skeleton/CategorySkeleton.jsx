const CategorySkeleton = ({ theme }) => {
  return (
    <div
      className={`
        snap-start shrink-0
        w-[150px] sm:w-[170px] md:w-[180px]
        rounded-2xl p-6
        animate-pulse
        ${
          theme === "dark"
            ? "bg-zinc-900 border border-zinc-800"
            : "bg-white shadow-sm"
        }
      `}
    >
      {/* Icon Skeleton */}
      <div
        className={`
          w-14 h-14 mx-auto mb-4 rounded-xl
          ${theme === "dark" ? "bg-zinc-800" : "bg-zinc-200"}
        `}
      />

      {/* Title Skeleton */}
      <div
        className={`
          h-4 w-20 mx-auto rounded-md
          ${theme === "dark" ? "bg-zinc-800" : "bg-zinc-200"}
        `}
      />
    </div>
  );
};

export default CategorySkeleton;
