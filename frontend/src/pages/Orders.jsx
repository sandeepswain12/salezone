import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useTheme } from "../context/ThemeContext";
import orderService from "../services/orderService";
import { useNavigate } from "react-router-dom";

const Orders = () => {
  const { user, isAuthenticated } = useAuth();
  const { theme } = useTheme();
  const navigate = useNavigate();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate("/auth");
      return;
    }

    if (!user?.userId) return;

    const fetchOrders = async () => {
      try {
        const data = await orderService.getOrdersByUser(user.userId);
        setOrders(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error("Failed to fetch orders:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [user, isAuthenticated, navigate]);

  const getStatusStyle = (status) => {
    switch (status) {
      case "DELIVERED":
        return "bg-green-500/10 text-green-600";
      case "SHIPPED":
        return "bg-blue-500/10 text-blue-600";
      case "CANCELLED":
        return "bg-red-500/10 text-red-600";
      case "PAID":
        return "bg-purple-500/10 text-purple-600";
      default:
        return "bg-yellow-500/10 text-yellow-600";
    }
  };

  const formatDate = (date) => {
    if (!date) return "N/A";
    return new Date(date).toLocaleString();
  };

  /* ---------------- Loading State ---------------- */
  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <div className="animate-pulse space-y-4">
          <div className="h-6 bg-gray-300 rounded w-1/3 mx-auto" />
          <div className="h-24 bg-gray-200 rounded-xl" />
          <div className="h-24 bg-gray-200 rounded-xl" />
        </div>
      </div>
    );
  }

  /* ---------------- Empty State ---------------- */
  if (!orders.length) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <h2 className="text-2xl font-bold mb-3">No Orders Yet 📦</h2>
        <p className="opacity-70 mb-6">
          Looks like you haven't placed any orders.
        </p>
        <button
          onClick={() => navigate("/")}
          className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          Start Shopping
        </button>
      </div>
    );
  }

  /* ---------------- Orders List ---------------- */
  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      <h1 className="text-3xl font-bold mb-10">My Orders</h1>

      <div className="space-y-6">
        {orders.map((order) => (
          <div
            key={order.orderId}
            className={`p-6 rounded-2xl border transition hover:shadow-lg ${
              theme === "dark"
                ? "bg-[#0f0f0f] border-gray-800"
                : "bg-white border-gray-200"
            }`}
          >
            {/* Header */}
            <div className="flex flex-col md:flex-row md:justify-between md:items-center mb-4 gap-3">
              <div>
                <p className="text-sm opacity-60">Order ID</p>
                <h3 className="font-semibold break-all">{order.orderId}</h3>
                <p className="text-sm opacity-60 mt-1">
                  Placed on {formatDate(order.orderDate)}
                </p>
              </div>

              <span
                className={`px-4 py-1 text-sm rounded-full font-medium ${getStatusStyle(
                  order.orderStatus
                )}`}
              >
                {order.orderStatus}
              </span>
            </div>

            {/* Body */}
            <div className="grid md:grid-cols-3 gap-4 text-sm">
              <div>
                <p className="opacity-60">Total Amount</p>
                <p className="font-semibold text-lg">
                  ₹{order.orderAmount?.toLocaleString() ?? "0"}
                </p>
              </div>

              <div>
                <p className="opacity-60">Payment Status</p>
                <p className="font-medium">{order.paymentStatus}</p>
              </div>

              <div>
                <p className="opacity-60">Billing</p>
                <p className="font-medium">{order.billingName}</p>
                <p className="opacity-70 text-xs">{order.billingAddress}</p>
                <p className="opacity-70 text-xs">{order.billingPhone}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

export default Orders;
