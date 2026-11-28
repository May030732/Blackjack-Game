import { apiRequest } from "./api-client.js";
import { API_ENDPOINTS } from "./endpoints.js";

export function getHistory(query = "") {
  const name = query.trim();
  const endpoint = API_ENDPOINTS.history;

  if (name === "") {
    return apiRequest(endpoint);
  }

  const params = new URLSearchParams({ name });
  return apiRequest(`${endpoint}/search?${params}`);
}