import { apiRequest } from "./api-client.js";
import { API_ENDPOINTS } from "./endpoints.js";

export function login(credentials) {
  return apiRequest(API_ENDPOINTS.login, {
    method: "POST",
    body: JSON.stringify(credentials),
  });
}

export function register(account) {
  return apiRequest(API_ENDPOINTS.register, {
    method: "POST",
    body: JSON.stringify(account),
  });
}
