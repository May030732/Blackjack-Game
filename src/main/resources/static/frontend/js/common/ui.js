const toastTimer = { id: null };

export function showToast(message, icon = "info") {
  const toast = document.querySelector("[data-toast]");
  if (!toast) return;

  const text = toast.querySelector("[data-toast-message]");
  const symbol = toast.querySelector("[data-toast-icon]");
  if (text) text.textContent = message;
  if (symbol) symbol.textContent = icon;

  toast.classList.add("is-visible");
  window.clearTimeout(toastTimer.id);
  toastTimer.id = window.setTimeout(() => {
    toast.classList.remove("is-visible");
  }, 3000);
}

export function setStatus(message, kind = "info") {
  const status = document.querySelector("[data-page-status]");
  if (!status) return;

  status.hidden = false;
  status.dataset.kind = kind;
  const messageNode = status.querySelector("[data-status-message]");
  if (messageNode) messageNode.textContent = message;
}

export function openModal(id) {
  const modal = document.getElementById(id);
  if (!modal) return;
  modal.hidden = false;
  modal.setAttribute("aria-hidden", "false");
  modal.querySelector("[data-modal-close]")?.focus();
}

export function closeModal(modal) {
  if (!modal) return;
  modal.hidden = true;
  modal.setAttribute("aria-hidden", "true");
}

document.addEventListener("click", (event) => {
  const openButton = event.target.closest("[data-modal-open]");
  if (openButton) openModal(openButton.dataset.modalOpen);

  const closeButton = event.target.closest("[data-modal-close]");
  if (closeButton) closeModal(closeButton.closest("[data-modal]"));

  if (event.target.matches("[data-modal]")) closeModal(event.target);
});

document.addEventListener("keydown", (event) => {
  if (event.key !== "Escape") return;
  document.querySelectorAll("[data-modal]:not([hidden])").forEach(closeModal);
});
