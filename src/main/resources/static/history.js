
const API_URL = "/api/history";

// ==========================================
// 功能 1：將英文遊戲結果轉換成中文
// ==========================================
function translateResult(result) {
    if (result === "PLAYER_WIN") return "勝利";
    if (result === "DEALER_WIN") return "失敗";
    if (result === "PUSH") return "平手";

    return result ?? "";
}

// ==========================================
// 功能 2：根據遊戲結果設定 CSS 顏色
// ==========================================
function getResultClass(result) {
    if (result === "PLAYER_WIN") return "result-win";
    if (result === "DEALER_WIN") return "result-lose";
    if (result === "PUSH") return "result-push";

    return "";
}

// ==========================================
// 功能 3：格式化籌碼變化
// ==========================================
function formatChipChange(chipChange) {
    if (chipChange == null) return "";

    const amount = Number(chipChange);

    if (amount > 0) {
        return "+" + amount;
    }

    return String(amount);
}

// ==========================================
// 功能 4：格式化遊戲時間
// ==========================================
function formatDateTime(dateTime) {
    if (!dateTime) return "";

    // 將 2026-11-22 07:39:17
    // 顯示成 2026/11/22 07:39
    const value = String(dateTime);

    return value
        .replace(/-/g, "/")
        .slice(0, 16);
}

// ==========================================
// 功能 5：顯示歷史紀錄
// ==========================================
function displayHistory(records, emptyMessage = "找不到遊戲歷史紀錄") {

    const tableBody =
        document.getElementById("historyTableBody");

    // 清空原本的資料
    tableBody.innerHTML = "";

    // 如果沒有紀錄
    if (!Array.isArray(records) || records.length === 0) {

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 8;
        cell.textContent = emptyMessage;
        cell.className = "empty-message";

        row.appendChild(cell);
        tableBody.appendChild(row);

        return;
    }

    // 將每一筆紀錄加入表格
    records.forEach(record => {

        const row = document.createElement("tr");

        const values = [
            record.id,
            record.uid,
            record.betAmount,
            record.playerScore,
            record.dealerScore,
            translateResult(record.result),
            formatChipChange(record.chipChange),
            formatDateTime(record.playedAt)
        ];

        values.forEach((value, index) => {

            const cell = document.createElement("td");

            cell.textContent = value ?? "";

            // 第 6 欄：遊戲結果
            if (index === 5) {
                cell.classList.add(
                    getResultClass(record.result)
                );
            }

            // 第 7 欄：籌碼變化
            if (index === 6) {

                const amount = Number(record.chipChange);

                if (record.chipChange != null) {
                    if (amount > 0) {
                        cell.classList.add("chip-positive");
                    } else if (amount < 0) {
                        cell.classList.add("chip-negative");
                    } else {
                        cell.classList.add("chip-zero");
                    }
                }
            }

            row.appendChild(cell);
        });

        tableBody.appendChild(row);
    });
}

// ==========================================
// 功能 6：根據玩家姓名搜尋
// ==========================================
async function searchHistory() {

    const name = document
        .getElementById("playerName")
        .value.trim();

    if (name === "") {
        alert("請輸入玩家姓名！");
        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/search?name=${encodeURIComponent(name)}`
        );

        if (!response.ok) {
            throw new Error("查詢失敗：" + response.status);
        }

        const records = await response.json();

        displayHistory(
            records,
            `找不到玩家「${name}」的遊戲紀錄`
        );

    } catch (error) {

        console.error(error);

        alert(
            "無法取得歷史紀錄，請確認 Spring Boot 是否啟動！"
        );
    }
}

// ==========================================
// 功能 7：查詢全部歷史紀錄
// ==========================================
async function loadAllHistory() {

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("查詢失敗：" + response.status);
        }

        const records = await response.json();

        displayHistory(
            records,
            "目前沒有任何遊戲歷史紀錄"
        );

    } catch (error) {

        console.error(error);

        alert("無法取得歷史紀錄！");
    }
}

// ==========================================
// 功能 8：網頁載入完成後綁定事件
// ==========================================
document.addEventListener("DOMContentLoaded", () => {

    // 搜尋按鈕
    document
        .getElementById("searchButton")
        .addEventListener("click", searchHistory);

    // 顯示全部按鈕
    document
        .getElementById("allButton")
        .addEventListener("click", loadAllHistory);

    // 按 Enter 也能搜尋
    document
        .getElementById("playerName")
        .addEventListener("keydown", event => {

            if (event.key === "Enter") {
                searchHistory();
            }
        });

    // 開啟網頁時自動載入全部紀錄
    loadAllHistory();
});
