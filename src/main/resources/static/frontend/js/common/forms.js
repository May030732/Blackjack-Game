export function attachPasswordVisibility(button, input) {
  if (!button || !input) return;

  button.addEventListener("click", () => {
    const reveal = input.type === "password";
    input.type = reveal ? "text" : "password";
    button.setAttribute("aria-pressed", String(reveal));
    const icon = button.querySelector("[data-password-icon]");
    if (icon) icon.textContent = reveal ? "visibility_off" : "visibility";
  });
}

export function attachPasswordMatch(password, confirmation) {
  if (!password || !confirmation) return;

  const validate = () => {
    confirmation.setCustomValidity(
      confirmation.value && confirmation.value !== password.value
        ? "兩次輸入的密碼不相符。"
        : "",
    );
  };

  password.addEventListener("input", validate);
  confirmation.addEventListener("input", validate);
}
