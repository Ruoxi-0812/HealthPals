export function scrollPageToTop() {
  if (typeof window === "undefined" || typeof document === "undefined") {
    return;
  }

  const targets = [
    document.querySelector(".content-container"),
    document.scrollingElement,
    document.documentElement,
    document.body,
  ].filter(Boolean);

  const seen = new Set();
  targets.forEach((target) => {
    if (seen.has(target)) {
      return;
    }
    seen.add(target);

    if (typeof target.scrollTo === "function") {
      target.scrollTo({ top: 0, left: 0, behavior: "auto" });
    } else {
      target.scrollTop = 0;
      target.scrollLeft = 0;
    }
  });

  if (typeof window.scrollTo === "function") {
    window.scrollTo({ top: 0, left: 0, behavior: "auto" });
  }
}
