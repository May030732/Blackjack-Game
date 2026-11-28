import { apiRequest } from "./api-client.js";
import { API_ENDPOINTS } from "./endpoints.js";

export function getHistory(query = "") {
  const endpoint = API_ENDPOINTS.history;
  const url = query && endpoint
    ? `${endpoint}${endpoint.includes("?") ? "&" : "?"}q=${encodeURIComponent(query)}`
    : endpoint;
  return apiRequest(url);
}
