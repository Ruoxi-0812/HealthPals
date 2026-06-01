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
  15: "img/articles/heart-healthy-real.jpg",
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
    return generatedNewsCoverSrc(newsId);
  }
  const trimmed = cover.trim();
  if (!trimmed) return generatedNewsCoverSrc(newsId);
  for (const [legacy, local] of LEGACY_COVER_REWRITES) {
    if (trimmed.includes(legacy)) return toAbsoluteAsset(local);
  }
  if (trimmed.startsWith("/img/articles/")) {
    return toAbsoluteAsset(trimmed);
  }
  if (!trimmed.startsWith("http")) {
    return generatedNewsCoverSrc(newsId || trimmed);
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
  el.src = generatedNewsCoverSrc(el.dataset.newsId || el.alt || el.src);
}

function svgDataUri(svg) {
  return `data:image/svg+xml,${encodeURIComponent(svg)}`;
}

function hashString(input) {
  const str = String(input || "");
  let hash = 0;
  for (let i = 0; i < str.length; i += 1) {
    hash = (hash * 31 + str.charCodeAt(i)) >>> 0;
  }
  return hash;
}

function generatedNewsCoverSrc(seedInput) {
  const seed = hashString(seedInput || "article");
  const palettes = [
    ["#eef8f2", "#cfe9da", "#2a9d6f", "#f3b562"],
    ["#eef3ff", "#d7defd", "#667eea", "#8fd3ff"],
    ["#fff8ef", "#ffe1c4", "#d98b4a", "#f2bf63"],
    ["#eefbff", "#d5f4ff", "#299fbd", "#7ed6ea"],
    ["#f5f1ff", "#ded5ff", "#7c6bd6", "#9ed5bc"],
  ];
  const [bg, bgSoft, accent, accentSoft] = palettes[seed % palettes.length];
  const circleX = 770 + (seed % 180);
  const circleY = 150 + (seed % 90);
  const waveY = 330 + (seed % 48);
  const code = String(seedInput || "HP")
    .replace(/[^a-z0-9]/gi, "")
    .slice(-3)
    .toUpperCase() || "HP";

  return svgDataUri(
    `<svg xmlns="http://www.w3.org/2000/svg" width="900" height="560" viewBox="0 0 900 560" fill="none">
      <defs>
        <linearGradient id="bg" x1="0" y1="0" x2="900" y2="560" gradientUnits="userSpaceOnUse">
          <stop stop-color="${bgSoft}"/>
          <stop offset="1" stop-color="${bg}"/>
        </linearGradient>
        <linearGradient id="wave" x1="0" y1="${waveY}" x2="900" y2="560" gradientUnits="userSpaceOnUse">
          <stop stop-color="${accent}" stop-opacity=".2"/>
          <stop offset="1" stop-color="${accentSoft}" stop-opacity=".34"/>
        </linearGradient>
      </defs>
      <rect width="900" height="560" fill="url(#bg)"/>
      <circle cx="${circleX}" cy="${circleY}" r="138" fill="rgba(255,255,255,.36)"/>
      <circle cx="${circleX}" cy="${circleY}" r="82" fill="rgba(255,255,255,.22)"/>
      <path d="M0 ${waveY}C132 ${waveY - 48} 248 ${waveY - 72} 394 ${waveY - 42}C568 ${waveY - 6} 690 ${waveY + 38} 900 ${waveY - 18}V560H0V${waveY}Z" fill="url(#wave)"/>
      <path d="M176 332c0-40 29-70 67-70 24 0 42 11 54 28 12-17 30-28 54-28 38 0 67 30 67 70 0 61-64 98-121 150-57-52-121-89-121-150Z" fill="${accent}" fill-opacity=".16"/>
      <path d="M130 366h88l26-52 38 94 35-67h78" stroke="${accent}" stroke-width="16" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity=".74"/>
      <text x="64" y="96" fill="${accent}" font-family="Arial, Helvetica, sans-serif" font-size="26" font-weight="700" letter-spacing="5">${code}</text>
    </svg>`,
  );
}

function newsHeroTheme(article) {
  const text =
    `${(article && article.name) || ""} ${(article && article.tagName) || ""}`.toLowerCase();

  if (/heart|cardio|cholesterol|pressure|habit|diet|nutrition|meal|food/.test(text)) {
    return {
      bg: "#f6fbf7",
      bgSoft: "#d7f0e1",
      accent: "#2a9d6f",
      accentSoft: "#f3b562",
      motif:
        '<path d="M340 332c0-44 31-77 73-77 26 0 46 12 59 31 13-19 33-31 59-31 42 0 73 33 73 77 0 67-70 108-132 164-63-56-132-97-132-164Z" fill="rgba(42,157,111,.14)"/><path d="M274 369h96l28-56 42 102 38-73h84" stroke="rgba(42,157,111,.72)" stroke-width="18" stroke-linecap="round" stroke-linejoin="round" fill="none"/><circle cx="920" cy="176" r="120" fill="rgba(243,181,98,.18)"/><circle cx="1000" cy="264" r="44" fill="rgba(42,157,111,.12)"/>',
    };
  }

  if (/sleep|night|rest|screen|bedtime/.test(text)) {
    return {
      bg: "#eef3ff",
      bgSoft: "#d7defd",
      accent: "#667eea",
      accentSoft: "#8fd3ff",
      motif:
        '<path d="M878 158c-12 88-86 148-176 148-22 0-41-3-59-9 28 49 82 81 144 81 91 0 166-70 176-160-25 15-54 24-85 24Z" fill="rgba(102,126,234,.28)"/><circle cx="292" cy="200" r="6" fill="rgba(102,126,234,.35)"/><circle cx="344" cy="152" r="8" fill="rgba(143,211,255,.42)"/><circle cx="402" cy="214" r="5" fill="rgba(102,126,234,.28)"/><path d="M260 396c52-54 108-81 168-81 74 0 126 35 180 91" stroke="rgba(102,126,234,.58)" stroke-width="18" stroke-linecap="round" fill="none"/>',
    };
  }

  if (/water|hydration|drink|fluid/.test(text)) {
    return {
      bg: "#eefbff",
      bgSoft: "#d5f4ff",
      accent: "#29a9d6",
      accentSoft: "#7ed6ea",
      motif:
        '<path d="M442 190c72 98 108 160 108 220 0 71-48 120-118 120s-118-49-118-120c0-60 36-122 128-220Z" fill="rgba(41,169,214,.18)"/><path d="M734 274c44 62 66 104 66 142 0 47-32 82-78 82s-78-35-78-82c0-39 22-80 90-142Z" fill="rgba(126,214,234,.28)"/><path d="M232 414c66-43 129-64 190-64 82 0 153 26 221 78" stroke="rgba(41,169,214,.62)" stroke-width="18" stroke-linecap="round" fill="none"/>',
    };
  }

  if (/allergy|allergies|pollen|sneeze|season/.test(text)) {
    return {
      bg: "#fff8ef",
      bgSoft: "#ffe9cf",
      accent: "#e58f4e",
      accentSoft: "#f6c46a",
      motif:
        '<circle cx="450" cy="310" r="66" fill="rgba(229,143,78,.18)"/><circle cx="450" cy="198" r="48" fill="rgba(246,196,106,.22)"/><circle cx="450" cy="422" r="48" fill="rgba(246,196,106,.22)"/><circle cx="338" cy="310" r="48" fill="rgba(246,196,106,.22)"/><circle cx="562" cy="310" r="48" fill="rgba(246,196,106,.22)"/><path d="M706 402c32-72 94-126 183-157" stroke="rgba(229,143,78,.52)" stroke-width="18" stroke-linecap="round" fill="none"/><circle cx="942" cy="214" r="22" fill="rgba(229,143,78,.16)"/><circle cx="1000" cy="258" r="14" fill="rgba(246,196,106,.24)"/>',
    };
  }

  return {
    bg: "#f5faf7",
    bgSoft: "#deefe5",
    accent: "#2f7f78",
    accentSoft: "#8fc9bc",
    motif:
      '<rect x="250" y="176" width="360" height="256" rx="48" fill="rgba(47,127,120,.12)"/><path d="M300 250h260M300 310h220M300 370h180" stroke="rgba(47,127,120,.58)" stroke-width="18" stroke-linecap="round"/><circle cx="900" cy="208" r="110" fill="rgba(143,201,188,.2)"/><circle cx="994" cy="284" r="46" fill="rgba(47,127,120,.12)"/>',
  };
}

export function newsHeroCoverSrc(article) {
  if (article && article.id != null && LOCAL_BY_NEWS_ID[article.id]) {
    return toAbsoluteAsset(LOCAL_BY_NEWS_ID[article.id]);
  }

  const theme = newsHeroTheme(article);
  const seed = hashString(
    `${(article && article.id) || ""}-${(article && article.name) || ""}-${(article && article.tagName) || ""}`,
  );
  const ringX = 860 + (seed % 120);
  const ringY = 150 + (seed % 80);
  const waveY = 476 + (seed % 36);
  const code = ((article && article.tagName) || "Article")
    .replace(/[^a-z0-9]/gi, " ")
    .trim()
    .split(/\s+/)
    .map((part) => part[0] || "")
    .join("")
    .slice(0, 3)
    .toUpperCase();

  return svgDataUri(
    `<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="640" viewBox="0 0 1200 640" fill="none">
      <defs>
        <linearGradient id="bg" x1="0" y1="0" x2="1200" y2="640" gradientUnits="userSpaceOnUse">
          <stop stop-color="${theme.bgSoft}"/>
          <stop offset="1" stop-color="${theme.bg}"/>
        </linearGradient>
        <linearGradient id="wave" x1="162" y1="442" x2="1112" y2="606" gradientUnits="userSpaceOnUse">
          <stop stop-color="${theme.accent}" stop-opacity=".18"/>
          <stop offset="1" stop-color="${theme.accentSoft}" stop-opacity=".3"/>
        </linearGradient>
      </defs>
      <rect width="1200" height="640" rx="0" fill="url(#bg)"/>
      <path d="M0 ${waveY}C170 ${waveY - 56} 324 ${waveY - 92} 510 ${waveY - 56}C698 ${waveY - 20} 932 ${waveY + 16} 1200 ${waveY - 36}V640H0V${waveY}Z" fill="url(#wave)"/>
      <circle cx="${ringX}" cy="${ringY}" r="138" fill="rgba(255,255,255,.34)"/>
      <circle cx="${ringX}" cy="${ringY}" r="90" fill="rgba(255,255,255,.22)"/>
      ${theme.motif}
      <text x="110" y="124" fill="${theme.accent}" font-family="Arial, Helvetica, sans-serif" font-size="28" font-weight="700" letter-spacing="4">${code || "ART"}</text>
    </svg>`,
  );
}

function healthTheme(model) {
  const name = `${(model && model.name) || ""} ${(model && model.symbol) || ""}`.toLowerCase();
  if (/heart|pulse|bpm/.test(name)) {
    return {
      code: "HR",
      bg: "#2a9d6f",
      bgSoft: "#7fd0ac",
      motif: '<path d="M14 34h8l4-9 6 16 5-10h13" stroke="rgba(255,255,255,.9)" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round" fill="none"/>',
    };
  }
  if (/pressure|mmhg/.test(name)) {
    return {
      code: "BP",
      bg: "#4d8b73",
      bgSoft: "#9ed5bc",
      motif: '<path d="M16 38a16 16 0 1 1 32 0" stroke="rgba(255,255,255,.88)" stroke-width="2.6" fill="none" stroke-linecap="round"/><path d="M32 38l8-10" stroke="rgba(255,255,255,.95)" stroke-width="2.6" stroke-linecap="round"/>',
    };
  }
  if (/weight|kg|lb|bmi/.test(name)) {
    return {
      code: "WT",
      bg: "#5b8def",
      bgSoft: "#a7c2ff",
      motif: '<path d="M19 41c0-7 6-12 13-12s13 5 13 12" stroke="rgba(255,255,255,.9)" stroke-width="2.6" fill="none" stroke-linecap="round"/><circle cx="32" cy="29" r="3.5" fill="rgba(255,255,255,.92)"/>',
    };
  }
  if (/step|run|walk|exercise|activity/.test(name)) {
    if (/morning|sunrise|am/.test(name)) {
      return {
        code: "AM",
        bg: "#f08c5a",
        bgSoft: "#ffd8b8",
        motif: '<path d="M18 41h28" stroke="rgba(255,255,255,.85)" stroke-width="2.4" stroke-linecap="round"/><path d="M23 41a9 9 0 0 1 18 0" stroke="rgba(255,255,255,.9)" stroke-width="2.6" fill="none" stroke-linecap="round"/><path d="M53 30l6-7 5 6 9-13" stroke="rgba(255,255,255,.92)" stroke-width="2.8" stroke-linecap="round" stroke-linejoin="round" fill="none"/>',
      };
    }
    if (/night|evening|pm/.test(name)) {
      return {
        code: "PM",
        bg: "#667eea",
        bgSoft: "#b9c6ff",
        motif: '<path d="M20 42l7-9 5 6 9-13" stroke="rgba(255,255,255,.92)" stroke-width="2.8" stroke-linecap="round" stroke-linejoin="round" fill="none"/><path d="M62 18a10 10 0 1 0 6 18 12 12 0 1 1-6-18Z" fill="rgba(255,255,255,.92)"/>',
      };
    }
    return {
      code: "STP",
      bg: "#f08c5a",
      bgSoft: "#ffd0b8",
      motif: '<path d="M18 40l8-10 6 7 10-14" stroke="rgba(255,255,255,.92)" stroke-width="2.8" stroke-linecap="round" stroke-linejoin="round" fill="none"/>',
    };
  }
  if (/alanine|alt|lab|glucose|sugar|cholesterol/.test(name)) {
    return {
      code: "LAB",
      bg: "#8b6bd6",
      bgSoft: "#c9b5ff",
      motif: '<path d="M24 18v10l-8 14a4 4 0 0 0 3.5 6h25a4 4 0 0 0 3.5-6L40 28V18" stroke="rgba(255,255,255,.9)" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" fill="none"/>',
    };
  }
  if (/sleep|night/.test(name)) {
    return {
      code: "ZZ",
      bg: "#6f7fd9",
      bgSoft: "#b7c0ff",
      motif: '<path d="M38 18a12 12 0 1 0 8 22 14 14 0 1 1-8-22Z" fill="rgba(255,255,255,.92)"/>',
    };
  }
  const rawCode = ((model && model.symbol) || (model && model.name) || "HM")
    .replace(/[^a-z0-9]/gi, " ")
    .trim()
    .split(/\s+/)
    .map((part) => part[0] || "")
    .join("")
    .slice(0, 3)
    .toUpperCase();
  return {
    code: rawCode || "HM",
    bg: "#2f7f78",
    bgSoft: "#9dd4cd",
    motif: '<path d="M18 41h28M18 32h20M18 23h24" stroke="rgba(255,255,255,.9)" stroke-width="2.6" stroke-linecap="round"/>',
  };
}

function generatedHealthModelCover(model) {
  const theme = healthTheme(model);
  return svgDataUri(
    `<svg xmlns="http://www.w3.org/2000/svg" width="96" height="96" viewBox="0 0 96 96" fill="none">
      <defs>
        <linearGradient id="g" x1="14" y1="10" x2="82" y2="86" gradientUnits="userSpaceOnUse">
          <stop stop-color="${theme.bgSoft}"/>
          <stop offset="1" stop-color="${theme.bg}"/>
        </linearGradient>
      </defs>
      <rect width="96" height="96" rx="24" fill="url(#g)"/>
      <circle cx="74" cy="22" r="12" fill="rgba(255,255,255,.18)"/>
      <circle cx="23" cy="72" r="9" fill="rgba(255,255,255,.14)"/>
      ${theme.motif}
      <text x="16" y="80" fill="white" font-family="Arial, Helvetica, sans-serif" font-size="20" font-weight="700" letter-spacing=".5">${theme.code}</text>
    </svg>`,
  );
}

export function healthModelCoverSrc(model) {
  const cover = model && typeof model.cover === "string" ? model.cover.trim() : "";
  if (cover && model && !model.isGlobal) {
    if (cover.startsWith("http://") || cover.startsWith("https://")) return cover;
    if (cover.startsWith("/")) return cover;
    if (cover.startsWith("data:image/")) return cover;
    return toAbsoluteAsset(cover);
  }
  return generatedHealthModelCover(model);
}
