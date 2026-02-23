const Pagination = ({ page, totalPages, onPrev, onNext }) => {
  if (totalPages <= 1) return null;

  return (
    <div className="flex justify-center items-center gap-4 mt-8">
      <button
        disabled={page === 0}
        onClick={onPrev}
        className="px-4 py-2 border rounded text-sm disabled:opacity-40"
      >
        Prev
      </button>

      <span className="text-xs opacity-70">
        Page {page + 1} of {totalPages}
      </span>

      <button
        disabled={page + 1 >= totalPages}
        onClick={onNext}
        className="px-4 py-2 border rounded text-sm disabled:opacity-40"
      >
        Next
      </button>
    </div>
  );
};

export default Pagination;
