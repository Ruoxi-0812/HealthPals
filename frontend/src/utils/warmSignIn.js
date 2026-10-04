import request from "./request";
// Wake the service while the visitor fills in the form or opens Google.
let pending;
export function warmSignIn() {
  if (!pending) {
    pending = request
      .get("/health/ready", { timeout: 120000 })
      .catch(() => {})
      .finally(() => {
        pending = null;
      });
  }
  return pending;
}
