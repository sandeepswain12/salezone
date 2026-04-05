import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from "react";
import { useAuth } from "./AuthContext";
import { useToast } from "./ToastContext";
import wishlistService from "../services/wishlistService";

const WishlistContext = createContext();

export const WishlistProvider = ({ children }) => {
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const [wishlistIds, setWishlistIds] = useState(new Set());
  const [loading, setLoading] = useState(false);

  const fetchWishlist = useCallback(async () => {
    if (!isAuthenticated || !user?.userId) return;
    try {
      setLoading(true);
      const data = await wishlistService.getWishlist(user.userId);
      setWishlistIds(new Set(data.products.map((p) => p.productId)));
    } catch {
      // silently fail — wishlist is non-critical
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, user?.userId]);

  useEffect(() => {
    fetchWishlist();
  }, [fetchWishlist]);

  const toggleWishlist = async (productId) => {
    if (!isAuthenticated) {
      showToast("Please sign in to use wishlist", "info");
      return;
    }
    const isWishlisted = wishlistIds.has(productId);

    // Optimistic update
    setWishlistIds((prev) => {
      const next = new Set(prev);
      isWishlisted ? next.delete(productId) : next.add(productId);
      return next;
    });

    try {
      if (isWishlisted) {
        await wishlistService.removeFromWishlist(user.userId, productId);
        showToast("Removed from wishlist", "info");
      } else {
        await wishlistService.addToWishlist(user.userId, productId);
        showToast("Added to wishlist ❤️", "success");
      }
    } catch {
      // Rollback on failure
      setWishlistIds((prev) => {
        const next = new Set(prev);
        isWishlisted ? next.add(productId) : next.delete(productId);
        return next;
      });
      showToast("Something went wrong", "error");
    }
  };

  return (
    <WishlistContext.Provider
      value={{ wishlistIds, toggleWishlist, loading, fetchWishlist }}
    >
      {children}
    </WishlistContext.Provider>
  );
};

export const useWishlist = () => useContext(WishlistContext);
