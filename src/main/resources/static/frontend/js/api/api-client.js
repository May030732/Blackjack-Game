export async function apiRequest(endpoint, options = {}) {
  if (typeof endpoint !== "string" || endpoint.trim() === "") {
    throw new Error("後端 API 尚未設定，請先確認並填入 API 路徑。");
  }

  const headers = new Headers(options.headers || {});
  if (options.body !== undefined && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(endpoint, {
    ...options,
    headers,
    credentials: "include",
  });

  const contentType = response.headers.get("content-type") || "";
  const payload = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const detail =
      typeof payload === "object" && payload !== null && "message" in payload
        ? payload.message
        : `伺服器回應 ${response.status}`;
    throw new Error(detail);
  }

  return payload;
}
