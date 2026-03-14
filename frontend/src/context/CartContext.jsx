import { createContext, useContext, useState, useEffect } from "react";
import cartService from "../services/cartService";
import { useAuth } from "./AuthContext";
import { useToast } from "./ToastContext";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();

  const [cart, setCart] = useState(null); // store full cart
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(false);

  // Fetch Cart
  const fetchCart = async () => {
    if (!isAuthenticated || !user?.userId) return;

    try {
      setLoading(true);
      const data = await cartService.getCart(user.userId);

      setCart(data); // ✅ store full cart
      setCartItems(data.items ?? []);
    } catch (error) {
      console.error("Cart fetch failed:", error);
    } finally {
      setLoading(false);
    }
  };

  // Add To Cart
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

  // Remove Item
  const removeItem = async (cartItemId) => {
    try {
      await cartService.removeCartItem(user.userId, cartItemId);
      fetchCart();
    } catch (error) {
      showToast("Failed to remove item", "error");
    }
  };

  // Update Quantity
  const updateQuantity = async (cartItemId, newQuantity) => {
    if (newQuantity < 1) return;

    // Save old state for rollback
    const previousItems = [...cartItems];

    try {
      // Optimistic UI update
      setCartItems((prev) =>
        prev.map((item) =>
          item.cartItemId === cartItemId
            ? {
                ...item,
                quantity: newQuantity,
                totalPrice: item.product.discountedPrice * newQuantity,
              }
            : item
        )
      );

      // API call
      await cartService.updateCartItem(user.userId, cartItemId, newQuantity);
    } catch (error) {
      showToast("Failed to update quantity", "error");

      // Rollback if failed
      setCartItems(previousItems);
    }
  };

  // Clear Cart
  const clearCart = async () => {
    try {
      await cartService.clearCart(user.userId);
      setCart(null); // ✅ clear cart object
      setCartItems([]);
    } catch (error) {
      showToast("Failed to clear cart", "error");
    }
  };

  // Calculations
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
        cart,
        cartId: cart?.cartId, // expose cartId
        cartItems,
        addToCart,
        removeItem,
        updateQuantity,
        clearCart,
        totalAmount,
        cartCount,
        loading,
        fetchCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
