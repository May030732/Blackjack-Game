import { attachPasswordVisibility } from "../common/forms.js";
import { showToast } from "../common/ui.js";
import { login } from "../api/auth-api.js";

const form = document.querySelector("#login-form");
const password = document.querySelector("#password");
attachPasswordVisibility(document.querySelector("#toggle-password"), password);

form?.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!form.reportValidity()) return;

  const submit = form.querySelector("[type=submit]");
  submit.disabled = true;
  try {
    const data = new FormData(form);
    await login({
      username: String(data.get("username") || "").trim(),
      password: String(data.get("password") || ""),
    });
    showToast("登入要求已送出。", "lock_open");
  } catch (error) {
    showToast(error.message || "登入失敗，請稍後再試。", "error");
  } finally {
    submit.disabled = false;
  }
});
