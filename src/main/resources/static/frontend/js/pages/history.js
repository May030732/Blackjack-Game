import { setStatus } from "../common/ui.js";
import { getHistory } from "../api/history-api.js";

const form = document.querySelector("#history-search");
const results = document.querySelector("table.history-table tbody");
const retryButton = document.querySelector("[data-history-retry]");

const resultLabels = {
  PLAYER_WIN: "玩家勝",
  DEALER_WIN: "莊家勝",
  PUSH: "平手",
};

let lastQuery = "";
let latestRequest = 0;

function showTableMessage(message) {
  const row = document.createElement("tr");
  const cell = row.insertCell();

  cell.colSpan = 6;
  cell.className = "p-5 text-center text-on-surface-variant";
  cell.textContent = message;

  results.replaceChildren(row);
}

function renderRecords(records) {
  const rows = document.createDocumentFragment();

  for (const record of records) {
    const row = document.createElement("tr");

    // 順序和 history.html 的六個表頭相同。
    const values = [
      record.id,
      record.betAmount,
      record.playerScore,
      record.dealerScore,
      resultLabels[record.result] ?? record.result,
      record.playedAt,
    ];

    for (const value of values) {
      const cell = row.insertCell();
      cell.className = "p-4";
      cell.textContent = value ?? "—";
    }

    rows.append(row);
  }

  results.replaceChildren(rows);
}

async function loadHistory(query = "") {
  if (!results) return;

  lastQuery = query;
  const requestId = ++latestRequest;
  showTableMessage("正在載入歷史紀錄…");

  try {
    const records = await getHistory(query);
    if (requestId !== latestRequest) return;

    if (!Array.isArray(records)) {
      throw new Error("歷史紀錄 API 回傳格式不正確。");
    }

    if (records.length === 0) {
      showTableMessage("目前沒有符合的對局紀錄。");
    } else {
      renderRecords(records);
    }

    setStatus(`查詢完成，共 ${records.length} 筆紀錄。`, "info");
  } catch (error) {
    if (requestId !== latestRequest) return;

    showTableMessage("無法載入歷史紀錄。");
    setStatus(error.message || "請稍後再試。", "error");
  }
}

form?.addEventListener("submit", (event) => {
  event.preventDefault();
  const query = String(new FormData(form).get("query") ?? "").trim();
  loadHistory(query);
});

retryButton?.addEventListener("click", () => {
  loadHistory(lastQuery);
});

loadHistory();