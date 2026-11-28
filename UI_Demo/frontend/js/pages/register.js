import { attachPasswordMatch, attachPasswordVisibility } from "../common/forms.js";
import { showToast } from "../common/ui.js";
import { register } from "../api/auth-api.js";

const form = document.querySelector("#register-form");
const password = document.querySelector("#password");
const confirmation = document.querySelector("#confirm-password");

attachPasswordMatch(password, confirmation);
attachPasswordVisibility(document.querySelector("#toggle-password"), password);
attachPasswordVisibility(
  document.querySelector("#toggle-confirm-password"),
  confirmation,
);

form?.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!form.reportValidity()) return;

  const submit = form.querySelector("[type=submit]");
  submit.disabled = true;
  try {
    const data = new FormData(form);
    await register({
      username: String(data.get("username") || "").trim(),
      password: String(data.get("password") || ""),
    });
    showToast("註冊要求已送出。", "how_to_reg");
  } catch (error) {
    showToast(error.message || "註冊失敗，請稍後再試。", "error");
  } finally {
    submit.disabled = false;
  }
});
