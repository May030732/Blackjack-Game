import { setStatus, showToast } from "../common/ui.js";
import { getHistory } from "../api/history-api.js";

const form = document.querySelector("#history-search");
const results = document.querySelector("[data-history-results]");

async function loadHistory(query = "") {
  if (results) results.textContent = "正在載入歷史紀錄…";

  try {
    const payload = await getHistory(query);
    const records = Array.isArray(payload) ? payload : payload?.records;
    if (!Array.isArray(records)) {
      throw new Error("歷史紀錄 API 回傳格式尚未確認。");
    }
    if (results) {
      results.textContent = records.length
        ? "已取得紀錄；請依後端資料格式完成欄位綁定。"
        : "目前沒有歷史對局紀錄。";
    }
  } catch (error) {
    if (results) results.textContent = "尚未載入歷史對局紀錄。";
    setStatus(error.message || "無法載入歷史紀錄。", "error");
  }
}

form?.addEventListener("submit", (event) => {
  event.preventDefault();
  const query = String(new FormData(form).get("query") || "").trim();
  loadHistory(query);
});

document.querySelector("[data-history-retry]")?.addEventListener("click", () => {
  loadHistory();
});

if (form) {
  showToast("歷史紀錄 API 尚待設定。", "info");
}
