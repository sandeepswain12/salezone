import { createContext, useContext, useState, useEffect } from "react";
import cartService from "../services/cartService";
import { useAuth } from "./AuthContext";
import { useToast } from "./ToastContext";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();

  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchCart = async () => {
    if (!isAuthenticated || !user?.userId) return;

    try {
      setLoading(true);
      const data = await cartService.getCart(user.userId);
      setCartItems(data.items ?? []);
    } catch (error) {
      console.error("Cart fetch failed:", error);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = async (productId) => {
    if (!user?.userId) {
      showToast("Please login first", "error");
      return;
    }

    try {
      await cartService.addToCart(user.userId, productId, 1);
      showToast("Added to cart 🛒", "success");
      fetchCart();
    } catch (error) {
      showToast("Failed to add to cart", "error");
    }
  };

  const removeItem = async (cartItemId) => {
    try {
      await cartService.removeCartItem(user.userId, cartItemId);
      fetchCart();
    } catch (error) {
      showToast("Failed to remove item", "error");
    }
  };

  const updateQuantity = async (cartItemId, quantity) => {
    try {
      await cartService.updateCartItem(user.userId, cartItemId, quantity);
      fetchCart();
    } catch (error) {
      showToast("Failed to update quantity", "error");
    }
  };

  const totalAmount = cartItems.reduce(
    (total, item) => total + item.totalPrice,
    0
  );

  const cartCount = cartItems.reduce((count, item) => count + item.quantity, 0);

  useEffect(() => {
    if (isAuthenticated && user?.userId) {
      fetchCart();
    }
  }, [isAuthenticated, user]);

  return (
    <CartContext.Provider
      value={{
        cartItems,
        addToCart,
        removeItem,
        updateQuantity,
        totalAmount,
        cartCount,
        loading,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
