import { createContext, useContext, useState, useEffect } from "react";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState(() => {
    const saved = localStorage.getItem("salezone_cart");
    return saved ? JSON.parse(saved) : [];
  });

  useEffect(() => {
    localStorage.setItem("salezone_cart", JSON.stringify(cart));
  }, [cart]);

  const addToCart = (product) => {
    setCart((prev) => {
      const exists = prev.find((item) => item.productId === product.productId);

      if (exists) {
        return prev.map((item) =>
          item.productId === product.productId
            ? { ...item, qty: item.qty + 1 }
            : item
        );
      }

      return [...prev, { ...product, qty: 1 }];
    });
  };

  const removeFromCart = (productId) => {
    setCart((prev) => prev.filter((item) => item.productId !== productId));
  };

  const increaseQty = (productId) => {
    setCart((prev) =>
      prev.map((item) =>
        item.productId === productId ? { ...item, qty: item.qty + 1 } : item
      )
    );
  };

  const decreaseQty = (productId) => {
    setCart((prev) =>
      prev.map((item) =>
        item.productId === productId && item.qty > 1
          ? { ...item, qty: item.qty - 1 }
          : item
      )
    );
  };

  const clearCart = () => setCart([]);

  const subtotal = cart.reduce(
    (total, item) => total + item.discountedPrice * item.qty,
    0
  );

  const cartCount = cart.reduce((count, item) => count + item.qty, 0);

  return (
    <CartContext.Provider
      value={{
        cart,
        addToCart,
        removeFromCart,
        increaseQty,
        decreaseQty,
        clearCart,
        subtotal,
        cartCount,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
