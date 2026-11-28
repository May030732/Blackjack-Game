import { setStatus, showToast } from "../common/ui.js";
import { getGameState, sendGameAction } from "../api/game-api.js";

const phaseLabels = {
  ready: "等待牌局資料",
  betting: "等待下注",
  dealing: "發牌中",
  "player-turn": "輪到玩家操作",
  "dealer-turn": "莊家回合",
  settled: "本局已結算",
  "game-over": "本次遊戲結束",
};

const resultLabels = {
  PLAYER_WIN: "玩家勝",
  DEALER_WIN: "莊家勝",
  PUSH: "平手",
};

const ranks = {
  ACE: "A",
  TWO: "2",
  THREE: "3",
  FOUR: "4",
  FIVE: "5",
  SIX: "6",
  SEVEN: "7",
  EIGHT: "8",
  NINE: "9",
  TEN: "10",
  JACK: "J",
  QUEEN: "Q",
  KING: "K",
};

const suits = {
  SPADES: "♠",
  HEARTS: "♥",
  CLUBS: "♣",
  DIAMONDS: "♦",
};

const phaseLabel = document.querySelector("[data-current-phase]");
const stages = document.querySelectorAll("[data-game-stage]");
const actionButtons = document.querySelectorAll("[data-game-action]");
const betInput = document.querySelector("#bet-amount");
const refreshButton = document.querySelector("[data-next-round]");

const dealerCards = document.querySelector("[data-dealer-cards]");
const playerCards = document.querySelector("[data-player-cards]");
const currentBet = document.querySelector("[data-current-bet]");
const currentChips = document.querySelector("[data-current-chips]");

let availableActions = new Set();
let busy = false;

function updateControls() {
  for (const button of actionButtons) {
    button.disabled = busy || !availableActions.has(button.dataset.gameAction);
  }

  if (betInput) {
    betInput.disabled = busy || !availableActions.has("bet");
  }

  if (refreshButton) {
    refreshButton.disabled = busy;
  }
}

function renderCards(container, cards) {
  if (!container || !Array.isArray(cards)) return;

  const elements = cards.map((card) => {
    const element = document.createElement("div");
    element.className =
      "flex h-36 w-24 items-center justify-center rounded-xl border text-3xl";

    // 莊家暗牌只顯示牌背。
    if (card.hidden) {
      element.classList.add("card-back", "text-primary");
      element.textContent = "21";
      element.setAttribute("aria-label", "莊家暗牌");
      return element;
    }

    const redSuit = card.suit === "HEARTS" || card.suit === "DIAMONDS";
    element.classList.add("playing-card", redSuit ? "text-red-700" : "text-black");
    element.textContent = `${ranks[card.rank] ?? "?"}${suits[card.suit] ?? ""}`;
    return element;
  });

  // 用最新手牌替換舊畫面，避免重複顯示同一張牌。
  container.replaceChildren(...elements);
}

function renderGameState(state) {
  if (!state || !Object.hasOwn(phaseLabels, state.phase)) {
    throw new Error("收到無法識別的遊戲畫面狀態。");
  }

  for (const stage of stages) {
    stage.hidden = stage.dataset.gameStage !== state.phase;
  }

  const details = [phaseLabels[state.phase]];

  if (
    state.phase !== "ready" &&
    state.phase !== "betting" &&
    Number.isInteger(state.playerScore)
  ) {
    details.push(`玩家 ${state.playerScore} 點`);
  }

  if (state.result) {
    details.push(resultLabels[state.result] ?? state.result);
  }

  if (phaseLabel) {
    phaseLabel.textContent = details.join("｜");
  }

  renderCards(dealerCards, state.dealerCards);
  renderCards(playerCards, state.playerCards);

  if (currentBet) {
    currentBet.textContent = state.currentBet ?? "—";
  }

  if (currentChips) {
    currentChips.textContent = state.chips ?? "—";
  }

  availableActions = new Set(state.availableActions ?? []);
  updateControls();
}

async function updateGame(request) {
  if (busy) return;

  // 等目前的請求完成，再開放下一次操作。
  busy = true;
  updateControls();

  try {
    const state = await request();
    renderGameState(state);
    setStatus("牌局狀態已更新。", "info");
  } catch (error) {
    const message = error.message || "無法更新牌局，請稍後再試。";
    setStatus(message, "error");
    showToast(message, "error");
  } finally {
    busy = false;
    updateControls();
  }
}

for (const button of actionButtons) {
  button.addEventListener("click", () => {
    const action = button.dataset.gameAction;

    if (busy || !availableActions.has(action)) return;

    let bet;

    // 只有下注時才讀取金額。
    if (action === "bet") {
      bet = betInput?.valueAsNumber;

      if (!Number.isInteger(bet) || bet < 1) {
        showToast("請輸入大於 0 的整數下注金額。", "error");
        return;
      }
    }

    updateGame(() => sendGameAction(action, bet));
  });
}

refreshButton?.addEventListener("click", () => {
  updateGame(getGameState);
});

await updateGame(getGameState);