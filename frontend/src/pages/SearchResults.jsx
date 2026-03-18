import { useParams } from "react-router-dom";
import { useEffect, useState, useCallback } from "react";
import { searchProducts } from "../services/ProductService";
import { getCategories } from "../services/CategoryService";
import { useTheme } from "../context/ThemeContext";

import ProductGridSkeleton from "../components/skeleton/ProductGridSkeleton";
import ProductCard from "../components/product/ProductCard";
import BackButton from "../components/ui/BackButton";
import Pagination from "../components/ui/Pagination";

// ─── Theme token maps ─────────────────────────────────────────────────────────

const TOKENS = {
  light: {
    bg: "#f7f8fc",
    surface: "#ffffff",
    surface2: "#f0f2f8",
    border: "#e3e6ef",
    text: "#111827",
    textMuted: "#6b7280",
    accent: "#2563eb",
    accentHover: "#1d4ed8",
    accentFg: "#ffffff",
    badgeBg: "#dbeafe",
    badgeText: "#1e40af",
    errorBg: "#fef2f2",
    errorText: "#991b1b",
    errorBorder: "#fecaca",
    inputBg: "#ffffff",
    shadow: "0 2px 12px rgba(0,0,0,.08)",
  },
  dark: {
    bg: "#0f0f0f",
    surface: "#0f0f0f",
    surface2: "#0f0f0f",
    border: "#1f2937",
    text: "#ffffff",
    textMuted: "#9ca3af",
    accent: "#3b82f6",
    accentHover: "#6366f1",
    accentFg: "#ffffff",
    badgeBg: "#1e3a5f",
    badgeText: "#93c5fd",
    errorBg: "#2d1a1a",
    errorText: "#fca5a5",
    errorBorder: "#374151",
    inputBg: "#000000",
    shadow: "0 2px 12px rgba(0,0,0,.4)",
  },
};

// ─── Icons ────────────────────────────────────────────────────────────────────

const FilterIcon = () => (
  <svg
    width="14"
    height="14"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
  </svg>
);

const SearchIcon = () => (
  <svg
    width="14"
    height="14"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <circle cx="11" cy="11" r="8" />
    <line x1="21" y1="21" x2="16.65" y2="16.65" />
  </svg>
);

const XIcon = ({ size = 11 }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2.5"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <line x1="18" y1="6" x2="6" y2="18" />
    <line x1="6" y1="6" x2="18" y2="18" />
  </svg>
);

const ChevronDown = () => (
  <svg
    width="13"
    height="13"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2.5"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <polyline points="6 9 12 15 18 9" />
  </svg>
);

const EmptyIcon = () => (
  <svg
    width="52"
    height="52"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="1"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <circle cx="11" cy="11" r="8" />
    <line x1="21" y1="21" x2="16.65" y2="16.65" />
    <line x1="8" y1="11" x2="14" y2="11" />
  </svg>
);

// ─── Constants ────────────────────────────────────────────────────────────────

const DEFAULT_FILTERS = {
  sortBy: "title",
  sortDir: "asc",
  minPrice: "",
  maxPrice: "",
  categoryId: "",
};

const SORT_LABELS = {
  "title-asc": "A → Z",
  "price-asc": "Price ↑",
  "price-desc": "Price ↓",
  "createdAt-desc": "Newest",
};

// ─── Main Component ───────────────────────────────────────────────────────────

const SearchResults = () => {
  const { keyword } = useParams();
  const { theme } = useTheme(); // "light" | "dark"
  const t = TOKENS[theme] ?? TOKENS.light;

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [catLoading, setCatLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [error, setError] = useState(null);
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [filters, setFilters] = useState(DEFAULT_FILTERS);
  const [appliedFilters, setApplied] = useState(DEFAULT_FILTERS);

  const hasActiveFilters =
    appliedFilters.categoryId !== "" ||
    appliedFilters.minPrice !== "" ||
    appliedFilters.maxPrice !== "" ||
    appliedFilters.sortBy !== "title" ||
    appliedFilters.sortDir !== "asc";

  // ── Load categories ───────────────────────────────────────────────────────
  useEffect(() => {
    setCatLoading(true);
    getCategories()
      .then(setCategories)
      .catch((e) => console.error("Categories error:", e))
      .finally(() => setCatLoading(false));
  }, []);

  // ── Fetch products ────────────────────────────────────────────────────────
  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await searchProducts({
        keyword,
        pageNumber: page,
        sortBy: appliedFilters.sortBy,
        sortDir: appliedFilters.sortDir,
        minPrice: appliedFilters.minPrice
          ? Number(appliedFilters.minPrice)
          : undefined,
        maxPrice: appliedFilters.maxPrice
          ? Number(appliedFilters.maxPrice)
          : undefined,
        categoryId: appliedFilters.categoryId || undefined,
      });
      setProducts(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalItems(data.totalElements ?? data.content?.length ?? 0);
    } catch (e) {
      console.error("Search error:", e);
      setError("Something went wrong. Please try again.");
      setProducts([]);
    } finally {
      setLoading(false);
    }
  }, [keyword, page, appliedFilters]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  // ── Reset on keyword change ───────────────────────────────────────────────
  useEffect(() => {
    setPage(0);
    setFilters(DEFAULT_FILTERS);
    setApplied(DEFAULT_FILTERS);
  }, [keyword]);

  // ── Escape key closes filter panel ───────────────────────────────────────
  useEffect(() => {
    const h = (e) => {
      if (e.key === "Escape") setFiltersOpen(false);
    };
    window.addEventListener("keydown", h);
    return () => window.removeEventListener("keydown", h);
  }, []);

  // ── Filter handlers ───────────────────────────────────────────────────────
  const applyFilters = () => {
    const min = Number(filters.minPrice),
      max = Number(filters.maxPrice);
    if (filters.minPrice && filters.maxPrice && min > max) {
      alert("Min price cannot exceed max price.");
      return;
    }
    setPage(0);
    setApplied(filters);
    setFiltersOpen(false);
  };

  const clearFilters = () => {
    setFilters(DEFAULT_FILTERS);
    setApplied(DEFAULT_FILTERS);
    setPage(0);
  };

  const removeSingleFilter = (key) => {
    const updated = { ...appliedFilters, [key]: DEFAULT_FILTERS[key] };
    if (key === "sortBy") updated.sortDir = "asc";
    setApplied(updated);
    setFilters(updated);
    setPage(0);
  };

  const handleSortChange = (e) => {
    const [sortBy, sortDir] = e.target.value.split("-");
    setFilters((p) => ({ ...p, sortBy, sortDir }));
  };

  const getCategoryLabel = (id) =>
    categories.find((c) => String(c.categoryId) === String(id))?.title ?? id;

  const sortLabel =
    SORT_LABELS[`${appliedFilters.sortBy}-${appliedFilters.sortDir}`];

  // ── Style helpers (read from t = current theme tokens) ───────────────────

  const fieldInput = {
    width: "100%",
    padding: "0.48rem 0.75rem",
    background: t.inputBg,
    border: `1.5px solid ${t.border}`,
    borderRadius: "8px",
    fontSize: "0.875rem",
    color: t.text,
    fontFamily: "inherit",
    outline: "none",
    WebkitAppearance: "none",
    appearance: "none",
    boxSizing: "border-box",
    transition: "border-color 180ms",
  };

  const fieldLabel = {
    display: "block",
    fontSize: "0.7rem",
    fontWeight: 700,
    textTransform: "uppercase",
    letterSpacing: "0.07em",
    color: t.textMuted,
    marginBottom: "0.28rem",
  };

  const badgeStyle = {
    display: "inline-flex",
    alignItems: "center",
    gap: "0.3rem",
    padding: "0.25rem 0.6rem",
    background: t.badgeBg,
    color: t.badgeText,
    borderRadius: "100px",
    fontSize: "0.75rem",
    fontWeight: 600,
  };

  const btnGhost = {
    display: "inline-flex",
    alignItems: "center",
    gap: ".4rem",
    padding: ".48rem 1.1rem",
    borderRadius: "8px",
    fontSize: ".875rem",
    fontWeight: 600,
    cursor: "pointer",
    fontFamily: "inherit",
    border: `1.5px solid ${t.border}`,
    background: "transparent",
    color: t.textMuted,
    transition: "background 160ms, color 160ms",
  };

  const btnPrimary = {
    ...btnGhost,
    border: `1.5px solid ${t.accent}`,
    background: t.accent,
    color: t.accentFg,
  };

  // ─────────────────────────────────────────────────────────────────────────
  return (
    <div
      style={{
        fontFamily: "'DM Sans', system-ui, sans-serif",
        background: t.bg,
        color: t.text,
        minHeight: "100vh",
        padding: "2.5rem 1rem 4rem",
        transition: "background 220ms, color 220ms",
        boxSizing: "border-box",
      }}
    >
      {/* ── Static CSS (layout/animation only — no theme values) ────── */}
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap');
        *, *::before, *::after { box-sizing: border-box; }

        @keyframes srProgress {
          0%   { width: 0%;  opacity: 1; }
          65%  { width: 78%; opacity: 1; }
          100% { width: 95%; opacity: .3; }
        }
        @keyframes srSlide {
          from { opacity: 0; transform: translateY(-8px); }
          to   { opacity: 1; transform: translateY(0); }
        }

        .sr-filter-panel { animation: srSlide 160ms ease both; }

        /* Responsive grid */
        .sr-grid {
          display: grid; gap: 1rem;
          grid-template-columns: repeat(2, 1fr);
        }
        @media (min-width: 480px)  { .sr-grid { gap: 1.25rem; } }
        @media (min-width: 640px)  { .sr-grid { grid-template-columns: repeat(2, 1fr); } }
        @media (min-width: 900px)  { .sr-grid { grid-template-columns: repeat(4, 1fr); } }
        @media (min-width: 1200px) { .sr-grid { grid-template-columns: repeat(4, 1fr); gap: 1.5rem; } }

        /* Responsive filter grid */
        .sr-filter-grid {
          display: grid; grid-template-columns: 1fr; gap: 0.75rem;
        }
        @media (min-width: 480px)  { .sr-filter-grid { grid-template-columns: 1fr 1fr; } }
        @media (min-width: 768px)  { .sr-filter-grid { grid-template-columns: repeat(3, 1fr); } }
        @media (min-width: 1024px) { .sr-filter-grid { grid-template-columns: repeat(5, 1fr); } }

        /* Header row */
        .sr-header {
          display: flex; flex-direction: column; gap: .5rem;
          margin-bottom: 1.75rem;
        }
        @media (min-width: 640px) {
          .sr-header {
            flex-direction: row; align-items: baseline;
            justify-content: space-between;
          }
        }

        /* Toolbar row */
        .sr-toolbar {
          display: flex; align-items: center;
          justify-content: space-between;
          gap: .75rem; margin-bottom: 1rem; flex-wrap: wrap;
        }

        /* Quick sort hidden on mobile */
        .sr-quick-sort { display: none; align-items: center; gap: .5rem; }
        @media (min-width: 640px) { .sr-quick-sort { display: flex; } }

        /* Filter action buttons row */
        .sr-filter-actions {
          display: flex; gap: .5rem;
          grid-column: 1 / -1;
          justify-content: flex-end;
          margin-top: .25rem; flex-wrap: wrap;
        }

        /* Spinner input */
        input[type="number"]::-webkit-inner-spin-button,
        input[type="number"]::-webkit-outer-spin-button { -webkit-appearance: none; }
        input[type="number"] { -moz-appearance: textfield; }
      `}</style>

      {/* Top loading bar */}
      {loading && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            height: "3px",
            background: t.accent,
            borderRadius: "0 3px 3px 0",
            zIndex: 9999,
            animation: "srProgress 1.4s ease-in-out infinite",
          }}
          role="progressbar"
          aria-label="Loading"
        />
      )}

      <div style={{ maxWidth: "1280px", margin: "0 auto" }}>
        {/* Back */}
        <div style={{ marginBottom: "1.25rem" }}>
          <BackButton label="Back" />
        </div>

        {/* ── Header ─────────────────────────────────────────────────── */}
        <div className="sr-header">
          <h1
            style={{
              fontSize: "clamp(1.25rem, 3vw, 1.7rem)",
              fontWeight: 700,
              letterSpacing: "-.02em",
              lineHeight: 1.2,
              color: t.text,
              margin: 0,
            }}
          >
            Results for <span style={{ color: t.accent }}>"{keyword}"</span>
          </h1>
          {!loading && (
            <p
              style={{
                fontSize: ".875rem",
                color: t.textMuted,
                fontWeight: 500,
                margin: 0,
              }}
              aria-live="polite"
            >
              {totalItems > 0
                ? `${totalItems.toLocaleString()} product${
                    totalItems !== 1 ? "s" : ""
                  } found`
                : "No products found"}
            </p>
          )}
        </div>

        {/* ── Error banner ───────────────────────────────────────────── */}
        {error && (
          <div
            role="alert"
            style={{
              background: t.errorBg,
              border: `1.5px solid ${t.errorBorder}`,
              borderRadius: "10px",
              padding: "1rem 1.25rem",
              color: t.errorText,
              fontSize: ".9rem",
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              gap: "1rem",
              marginBottom: "1.5rem",
            }}
          >
            <span>{error}</span>
            <button
              onClick={fetchProducts}
              style={{
                fontSize: ".82rem",
                fontWeight: 700,
                cursor: "pointer",
                background: "none",
                border: "1.5px solid currentColor",
                borderRadius: "6px",
                padding: ".25rem .65rem",
                color: "inherit",
                fontFamily: "inherit",
              }}
            >
              Retry
            </button>
          </div>
        )}

        {/* ── Toolbar ────────────────────────────────────────────────── */}
        <div className="sr-toolbar">
          {/* Filter toggle */}
          <button
            onClick={() => setFiltersOpen((o) => !o)}
            aria-expanded={filtersOpen}
            aria-controls="sr-filter-panel"
            style={{
              display: "inline-flex",
              alignItems: "center",
              gap: ".45rem",
              padding: ".45rem 1rem",
              background: t.surface,
              border: `1.5px solid ${filtersOpen ? t.accent : t.border}`,
              borderRadius: "8px",
              fontSize: ".875rem",
              fontWeight: 600,
              color: t.text,
              cursor: "pointer",
              fontFamily: "inherit",
              transition: "border-color 180ms, background 180ms",
            }}
          >
            <FilterIcon />
            Filters
            {hasActiveFilters && (
              <span
                style={{
                  width: "7px",
                  height: "7px",
                  background: t.accent,
                  borderRadius: "50%",
                  flexShrink: 0,
                }}
                aria-label="Filters active"
              />
            )}
          </button>

          {/* Quick sort (desktop only) */}
          <div className="sr-quick-sort">
            <span
              style={{
                fontSize: ".7rem",
                fontWeight: 700,
                textTransform: "uppercase",
                letterSpacing: ".06em",
                color: t.textMuted,
              }}
            >
              Sort
            </span>
            <div style={{ position: "relative", width: "185px" }}>
              <select
                style={{ ...fieldInput, paddingRight: "2rem" }}
                value={`${appliedFilters.sortBy}-${appliedFilters.sortDir}`}
                onChange={(e) => {
                  const [sortBy, sortDir] = e.target.value.split("-");
                  const updated = { ...appliedFilters, sortBy, sortDir };
                  setApplied(updated);
                  setFilters(updated);
                  setPage(0);
                }}
              >
                <option value="title-asc">A → Z</option>
                <option value="price-asc">Price: Low → High</option>
                <option value="price-desc">Price: High → Low</option>
                <option value="createdAt-desc">Newest</option>
              </select>
              <span
                style={{
                  position: "absolute",
                  right: ".6rem",
                  top: "50%",
                  transform: "translateY(-50%)",
                  pointerEvents: "none",
                  color: t.textMuted,
                }}
              >
                <ChevronDown />
              </span>
            </div>
          </div>
        </div>

        {/* ── Active filter badges ────────────────────────────────────── */}
        {hasActiveFilters && (
          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: ".4rem",
              alignItems: "center",
              marginBottom: "1rem",
            }}
          >
            {appliedFilters.categoryId && (
              <span style={badgeStyle}>
                Category: {getCategoryLabel(appliedFilters.categoryId)}
                <button
                  onClick={() => removeSingleFilter("categoryId")}
                  style={{
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                    color: "inherit",
                    padding: 0,
                    display: "inline-flex",
                    opacity: 0.7,
                  }}
                >
                  <XIcon />
                </button>
              </span>
            )}

            {(appliedFilters.sortBy !== "title" ||
              appliedFilters.sortDir !== "asc") && (
              <span style={badgeStyle}>
                Sort: {sortLabel}
                <button
                  onClick={() => removeSingleFilter("sortBy")}
                  style={{
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                    color: "inherit",
                    padding: 0,
                    display: "inline-flex",
                    opacity: 0.7,
                  }}
                >
                  <XIcon />
                </button>
              </span>
            )}

            {appliedFilters.minPrice && (
              <span style={badgeStyle}>
                Min: ₹{appliedFilters.minPrice}
                <button
                  onClick={() => removeSingleFilter("minPrice")}
                  style={{
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                    color: "inherit",
                    padding: 0,
                    display: "inline-flex",
                    opacity: 0.7,
                  }}
                >
                  <XIcon />
                </button>
              </span>
            )}

            {appliedFilters.maxPrice && (
              <span style={badgeStyle}>
                Max: ₹{appliedFilters.maxPrice}
                <button
                  onClick={() => removeSingleFilter("maxPrice")}
                  style={{
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                    color: "inherit",
                    padding: 0,
                    display: "inline-flex",
                    opacity: 0.7,
                  }}
                >
                  <XIcon />
                </button>
              </span>
            )}

            <button
              onClick={clearFilters}
              style={{
                background: "none",
                border: "none",
                cursor: "pointer",
                fontSize: ".78rem",
                fontWeight: 600,
                color: t.textMuted,
                textDecoration: "underline",
                padding: ".25rem 0",
                fontFamily: "inherit",
              }}
            >
              Clear all
            </button>
          </div>
        )}

        {/* ── Filter panel ────────────────────────────────────────────── */}
        {filtersOpen && (
          <div
            id="sr-filter-panel"
            className="sr-filter-panel"
            role="region"
            aria-label="Filter options"
            style={{
              background: t.surface,
              border: `1.5px solid ${t.border}`,
              borderRadius: "12px",
              padding: "1.25rem",
              marginBottom: "1.5rem",
              boxShadow: t.shadow,
            }}
          >
            <div className="sr-filter-grid">
              {/* Category */}
              <div>
                <label style={fieldLabel}>Category</label>
                <div style={{ position: "relative" }}>
                  <select
                    style={{ ...fieldInput, paddingRight: "2rem" }}
                    value={filters.categoryId}
                    disabled={catLoading}
                    onChange={(e) =>
                      setFilters((p) => ({ ...p, categoryId: e.target.value }))
                    }
                  >
                    <option value="">All Categories</option>
                    {categories.map((cat) => (
                      <option key={cat.categoryId} value={cat.categoryId}>
                        {cat.title}
                      </option>
                    ))}
                  </select>
                  <span
                    style={{
                      position: "absolute",
                      right: ".6rem",
                      top: "50%",
                      transform: "translateY(-50%)",
                      pointerEvents: "none",
                      color: t.textMuted,
                    }}
                  >
                    <ChevronDown />
                  </span>
                </div>
              </div>

              {/* Sort */}
              <div>
                <label style={fieldLabel}>Sort By</label>
                <div style={{ position: "relative" }}>
                  <select
                    style={{ ...fieldInput, paddingRight: "2rem" }}
                    value={`${filters.sortBy}-${filters.sortDir}`}
                    onChange={handleSortChange}
                  >
                    <option value="title-asc">A → Z</option>
                    <option value="price-asc">Price: Low → High</option>
                    <option value="price-desc">Price: High → Low</option>
                    <option value="createdAt-desc">Newest</option>
                  </select>
                  <span
                    style={{
                      position: "absolute",
                      right: ".6rem",
                      top: "50%",
                      transform: "translateY(-50%)",
                      pointerEvents: "none",
                      color: t.textMuted,
                    }}
                  >
                    <ChevronDown />
                  </span>
                </div>
              </div>

              {/* Min price */}
              <div>
                <label style={fieldLabel}>Min Price (₹)</label>
                <input
                  type="number"
                  inputMode="numeric"
                  placeholder="0"
                  min="0"
                  style={fieldInput}
                  value={filters.minPrice}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, minPrice: e.target.value }))
                  }
                />
              </div>

              {/* Max price */}
              <div>
                <label style={fieldLabel}>Max Price (₹)</label>
                <input
                  type="number"
                  inputMode="numeric"
                  placeholder="Any"
                  min="0"
                  style={fieldInput}
                  value={filters.maxPrice}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, maxPrice: e.target.value }))
                  }
                />
              </div>

              {/* Action buttons */}
              <div className="sr-filter-actions">
                <button
                  style={btnGhost}
                  onClick={() => {
                    setFilters(appliedFilters);
                    setFiltersOpen(false);
                  }}
                >
                  Cancel
                </button>
                <button style={btnGhost} onClick={clearFilters}>
                  Reset
                </button>
                <button style={btnPrimary} onClick={applyFilters}>
                  <SearchIcon /> Apply Filters
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ── Products / skeleton / empty ─────────────────────────────── */}
        {loading ? (
          <ProductGridSkeleton />
        ) : products.length === 0 ? (
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              padding: "5rem 1rem",
              gap: "1.25rem",
              textAlign: "center",
              color: t.textMuted,
            }}
          >
            <span style={{ opacity: 0.28 }}>
              <EmptyIcon />
            </span>
            <h3
              style={{
                fontSize: "1.1rem",
                fontWeight: 700,
                color: t.text,
                margin: 0,
              }}
            >
              No products found
            </h3>
            <p style={{ fontSize: ".9rem", margin: 0, maxWidth: "360px" }}>
              Nothing matched <strong>"{keyword}"</strong>.{" "}
              {hasActiveFilters ? (
                <>
                  Try removing some filters or{" "}
                  <button
                    onClick={clearFilters}
                    style={{
                      background: "none",
                      border: "none",
                      cursor: "pointer",
                      fontSize: ".9rem",
                      fontWeight: 600,
                      color: t.accent,
                      textDecoration: "underline",
                      padding: 0,
                      fontFamily: "inherit",
                    }}
                  >
                    clear all filters
                  </button>
                  .
                </>
              ) : (
                "Try a different search term."
              )}
            </p>
          </div>
        ) : (
          <div className="sr-grid" role="list" aria-label="Search results">
            {products.map((product) => (
              <ProductCard key={product.productId} product={product} />
            ))}
          </div>
        )}

        {/* ── Pagination ──────────────────────────────────────────────── */}
        {!loading && totalPages > 1 && (
          <div
            style={{
              marginTop: "3rem",
              display: "flex",
              justifyContent: "center",
            }}
          >
            <Pagination
              page={page}
              totalPages={totalPages}
              onPrev={() => setPage((p) => Math.max(p - 1, 0))}
              onNext={() => setPage((p) => (p < totalPages - 1 ? p + 1 : p))}
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchResults;
