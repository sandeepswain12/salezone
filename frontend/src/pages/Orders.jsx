import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useTheme } from "../context/ThemeContext";
import orderService from "../services/orderService";
import { useNavigate } from "react-router-dom";
import OrdersSkeleton from "../components/skeleton/OrdersSkeleton";

const Orders = () => {
  const { user, isAuthenticated, loading: authLoading } = useAuth();
  const { theme } = useTheme();
  const navigate = useNavigate();

  const [orders, setOrders] = useState([]);
  const [ordersLoading, setOrdersLoading] = useState(true);

  useEffect(() => {
    if (authLoading) return;

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
        setOrdersLoading(false);
      }
    };

    fetchOrders();
  }, [user, isAuthenticated, authLoading, navigate]);

  const getStatusStyle = (status) => {
    switch (status) {
      case "DELIVERED":
        return "bg-green-500/10 text-green-600";
      case "SHIPPED":
        return "bg-blue-500/10 text-blue-600";
      case "CANCELLED":
        return "bg-red-500/10 text-red-600";
      case "PENDING":
        return "bg-yellow-500/10 text-yellow-600";
      default:
        return "bg-gray-500/10 text-gray-600";
    }
  };

  const formatDate = (date) => {
    if (!date) return "N/A";
    return new Date(date).toLocaleString();
  };

  if (ordersLoading) {
    return <OrdersSkeleton />;
  }

  if (!orders.length) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <h2 className="text-2xl font-bold mb-3">No Orders Yet</h2>
        <p className="opacity-70 mb-6">
          Looks like you haven't placed any orders yet.
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

  return (
    <section className="max-w-7xl mx-auto px-4 py-12">
      <h1 className="text-3xl font-bold mb-10">My Orders</h1>

      <div className="space-y-8">
        {orders.map((order) => (
          <div
            key={order.orderId}
            className={`p-6 rounded-2xl border transition ${
              theme === "dark"
                ? "bg-[#0f0f0f] border-gray-800"
                : "bg-white border-gray-200 shadow-sm"
            }`}
          >
            {/* Order Header */}
            <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4 mb-6">
              <div>
                <p className="text-sm opacity-60">Order ID</p>
                <h3 className="font-semibold break-all">{order.orderId}</h3>

                <p className="text-sm opacity-60 mt-1">
                  Placed on {formatDate(order.orderedDate)}
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

            {/* Ordered Products */}
            <div className="space-y-4 mb-6">
              {order.orderItems?.map((item) => (
                <div
                  key={item.orderItemId}
                  className="flex items-center gap-4 border-t pt-4"
                >
                  <img
                    src={`/products/${item.product.productImageName}`}
                    alt={item.product.title}
                    className="w-16 h-16 rounded-lg object-cover"
                  />

                  <div className="flex-1">
                    <p className="font-medium">{item.product.title}</p>

                    <p className="text-sm opacity-70">Qty: {item.quantity}</p>
                  </div>

                  <p className="font-semibold">
                    ₹{item.totalPrice?.toLocaleString()}
                  </p>
                </div>
              ))}
            </div>

            {/* Order Summary */}
            <div className="grid md:grid-cols-4 gap-4 text-sm">
              <div>
                <p className="opacity-60">Total Amount</p>
                <p className="font-semibold text-lg">
                  ₹{order.orderAmount?.toLocaleString()}
                </p>
              </div>

              <div>
                <p className="opacity-60">Payment Status</p>
                <p className="font-medium">{order.paymentStatus}</p>
              </div>

              <div>
                <p className="opacity-60">Payment Method</p>
                <p className="font-medium">{order.paymentMethod}</p>
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
