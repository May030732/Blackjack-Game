import { setStatus, showToast } from "../common/ui.js";
import { getGameState, sendGameAction } from "../api/game-api.js";

const knownPhases = new Set([
  "ready",
  "betting",
  "dealing",
  "player-turn",
  "dealer-turn",
  "settled",
  "game-over",
]);

const phaseLabels = {
  ready: "等待牌局資料",
  betting: "等待下注",
  dealing: "發牌中",
  "player-turn": "輪到玩家操作",
  "dealer-turn": "莊家回合",
  settled: "本局已結算",
  "game-over": "本次遊戲結束",
};

function renderGameState(viewState) {
  if (!viewState || !knownPhases.has(viewState.phase)) {
    throw new Error("收到無法識別的遊戲畫面狀態。");
  }

  document.querySelectorAll("[data-game-stage]").forEach((stage) => {
    stage.hidden = stage.dataset.gameStage !== viewState.phase;
  });

  const phaseLabel = document.querySelector("[data-current-phase]");
  if (phaseLabel) phaseLabel.textContent = phaseLabels[viewState.phase];

  const actions = new Set(Array.isArray(viewState.availableActions)
    ? viewState.availableActions
    : []);
  document.querySelectorAll("[data-game-action]").forEach((button) => {
    button.disabled = !actions.has(button.dataset.gameAction);
  });
}

document.querySelectorAll("[data-game-action]").forEach((button) => {
  button.addEventListener("click", async () => {
    button.disabled = true;
    try {
      const state = await sendGameAction(button.dataset.gameAction);
      renderGameState(state);
    } catch (error) {
      showToast(error.message || "無法送出遊戲操作。", "error");
      button.disabled = false;
    }
  });
});

document.querySelector("[data-next-round]")?.addEventListener("click", async () => {
  try {
    renderGameState(await getGameState());
  } catch (error) {
    showToast(error.message || "無法取得牌局狀態。", "error");
  }
});

try {
  renderGameState(await getGameState());
} catch (error) {
  setStatus(error.message || "目前無法取得牌局狀態。", "error");
  renderGameState({ phase: "ready", availableActions: [] });
}
