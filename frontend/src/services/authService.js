const BASE_URL = "http://localhost:8089/salezone/ecom/auth";

class AuthService {
  // 🔓 SIGNUP
  async signup(signupData) {
    const res = await fetch(`${BASE_URL}/signup`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(signupData),
    });

    if (!res.ok) {
      throw new Error("Signup failed");
    }

    return res.json();
  }

  // 🔐 LOGIN (Basic Auth)
  async login(email, password) {
    const authHeader = "Basic " + btoa(`${email}:${password}`);

    const res = await fetch(`${BASE_URL}/login`, {
      method: "GET",
      headers: {
        Authorization: authHeader,
      },
    });

    if (!res.ok) {
      throw new Error("Invalid credentials");
    }

    const data = await res.text(); // or json later

    // store auth
    localStorage.setItem("basicAuth", authHeader);

    return data;
  }

  // 🚪 LOGOUT
  logout() {
    localStorage.removeItem("basicAuth");
    localStorage.removeItem("user");
  }

  // ✅ CHECK LOGIN
  isLoggedIn() {
    return !!localStorage.getItem("basicAuth");
  }

  // 🔑 GET AUTH HEADER
  getAuthHeader() {
    return localStorage.getItem("basicAuth");
  }
}

export default new AuthService();
