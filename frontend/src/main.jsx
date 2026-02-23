import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import { ThemeProvider } from "./context/ThemeContext";
import { CartProvider } from "./context/CartContext";
import { AuthProvider } from "./context/AuthContext";
import { ToastProvider } from "./context/ToastContext";
import AuthGate from "./components/ui/AuthGate";
import "./index.css";

ReactDOM.createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <ThemeProvider>
      <AuthProvider>
        <CartProvider>
          <AuthGate>
            <ToastProvider>
              <App />
            </ToastProvider>
          </AuthGate>
        </CartProvider>
      </AuthProvider>
    </ThemeProvider>
  </BrowserRouter>
);
