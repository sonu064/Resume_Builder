window.Auth = window.Auth || {};

const TOKEN_KEY = "resumeBuilderToken";
const API_BASE_KEY = "resumeBuilderApiBase";

function getApiBase() {
  // Allow overriding backend base for local setups.
  return localStorage.getItem(API_BASE_KEY) || "http://localhost:8080";
}

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

async function apiFetch(path, options = {}) {
  const token = getToken();
  const url = getApiBase() + path;

  const headers = options.headers || {};
  if (token && !headers["Authorization"]) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(url, {
    ...options,
    headers
  });

  let payload = null;
  try {
    payload = await res.json();
  } catch (_) {
    // ignore non-json
  }

  if (!res.ok) {
    const msg = payload && payload.message ? payload.message : `Request failed (${res.status})`;
    const err = new Error(msg);
    err.status = res.status;
    err.payload = payload;
    throw err;
  }

  return payload;
}

window.Auth = {
  getToken,
  setToken,
  clearToken,
  getApiBase,
  login: async function (email, password) {
    const payload = await apiFetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });
    if (payload && payload.token) setToken(payload.token);
    return payload;
  },
  register: async function (fullName, email, password) {
    const payload = await apiFetch("/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ fullName, email, password })
    });
    if (payload && payload.token) setToken(payload.token);
    return payload;
  },
  apiFetch
};

