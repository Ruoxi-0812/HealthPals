/** Local default when URL is missing or fails to load. */
const BASE = (process.env.BASE_URL || "/").replace(/\/?$/, "/");
export const DEFAULT_NEWS_COVER = `${BASE}img/articles/default.jpg`;

/** Broken legacy Unsplash IDs → local assets (works even if DB was not migrated). */
const LEGACY_COVER_REWRITES = [
  ["photo-1520206183501-b80d53754c1e", "img/articles/screen-sleep.jpg"],
  ["photo-1548839140-29a7492991ca", "img/articles/hydration.jpg"],
  ["photo-1416879595882-3373a0480b2a", "img/articles/allergies.jpg"],
  ["photo-1512621776951-a57141f2eefd", "img/articles/allergies.jpg"],
];

const LOCAL_BY_NEWS_ID = {
  3: "img/articles/health-indicators.jpg",
  4: "img/articles/principles-health.jpg",
  12: "img/articles/diet-cells.jpg",
  15: "img/articles/heart-healthy.jpg",
  16: "img/articles/screen-sleep.jpg",
  18: "img/articles/hydration.jpg",
  20: "img/articles/allergies.jpg",
};

function toAbsoluteAsset(path) {
  if (!path) return DEFAULT_NEWS_COVER;
  if (path.startsWith("http://") || path.startsWith("https://")) return path;
  const normalized = path.replace(/^\//, "");
  return `${BASE}${normalized}`;
}

export function newsCoverSrc(cover, newsId) {
  if (newsId != null && LOCAL_BY_NEWS_ID[newsId]) {
    return toAbsoluteAsset(LOCAL_BY_NEWS_ID[newsId]);
  }
  if (cover == null || typeof cover !== "string") {
    return DEFAULT_NEWS_COVER;
  }
  const trimmed = cover.trim();
  if (!trimmed) return DEFAULT_NEWS_COVER;
  for (const [legacy, local] of LEGACY_COVER_REWRITES) {
    if (trimmed.includes(legacy)) return toAbsoluteAsset(local);
  }
  if (trimmed.startsWith("/img/articles/")) {
    return toAbsoluteAsset(trimmed);
  }
  if (!trimmed.startsWith("http")) {
    return DEFAULT_NEWS_COVER;
  }
  return trimmed;
}

export function coverKey(cover, newsId) {
  return newsCoverSrc(cover, newsId).split("?")[0];
}

/** Pick up to `limit` items with unique cover images (first wins). limit <= 0 means no cap. */
export function pickUniqueCoverNews(list, limit = 3) {
  const seen = new Set();
  const out = [];
  const cap = limit > 0 ? limit : Number.POSITIVE_INFINITY;
  for (const item of list || []) {
    const key = coverKey(item && item.cover, item && item.id);
    if (seen.has(key)) continue;
    seen.add(key);
    out.push(item);
    if (out.length >= cap) break;
  }
  return out;
}

export function onCoverImgError(event) {
  const el = event && event.target;
  if (!el || el.dataset.fallbackApplied === "1") return;
  el.dataset.fallbackApplied = "1";
  el.src = DEFAULT_NEWS_COVER;
}
