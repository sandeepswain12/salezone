import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const ProtectedRoute = ({ children, requiredRole }) => {
  const { user, loading, roles } = useAuth();

  // Wait for auth restore
  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  // Not logged in
  if (!user) {
    return <Navigate to="/auth" replace />;
  }

  // Role-based check
  if (requiredRole && (!roles || !roles.includes(requiredRole))) {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default ProtectedRoute;
