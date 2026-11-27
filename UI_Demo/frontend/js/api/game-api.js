import { apiRequest } from "./api-client.js";
import { API_ENDPOINTS } from "./endpoints.js";

export function getGameState() {
  return apiRequest(API_ENDPOINTS.gameState);
}

export function sendGameAction(action) {
  return apiRequest(API_ENDPOINTS.gameAction, {
    method: "POST",
    body: JSON.stringify({ action }),
  });
}
