/**
 * Parse backend LocalDateTime strings (yyyy-MM-dd HH:mm:ss) as local time.
 */
export function parseDateTime(value) {
  if (value == null || value === "") {
    return null;
  }
  if (value instanceof Date) {
    return isNaN(value.getTime()) ? null : value;
  }
  if (Array.isArray(value)) {
    const [y, mo, d, h = 0, mi = 0, s = 0] = value;
    return new Date(y, mo - 1, d, h, mi, s);
  }
  const str = String(value).trim();
  const localMatch = str.match(
    /^(\d{4})-(\d{2})-(\d{2})[ T](\d{2}):(\d{2})(?::(\d{2}))?/,
  );
  if (localMatch) {
    return new Date(
      parseInt(localMatch[1], 10),
      parseInt(localMatch[2], 10) - 1,
      parseInt(localMatch[3], 10),
      parseInt(localMatch[4], 10),
      parseInt(localMatch[5], 10),
      parseInt(localMatch[6] || "0", 10),
    );
  }
  const parsed = new Date(str);
  return isNaN(parsed.getTime()) ? null : parsed;
}

/**
 * Human-readable relative time (no seconds).
 */
export function timeAgo(dateInput) {
  const date = parseDateTime(dateInput);
  if (!date) {
    return "";
  }

  const now = Date.now();
  const then = date.getTime();
  let secondsPast = Math.floor((now - then) / 1000);

  if (secondsPast < 0) {
    secondsPast = 0;
  }

  if (secondsPast < 60) {
    return "Just now";
  }
  if (secondsPast < 3600) {
    const minutes = Math.floor(secondsPast / 60);
    return minutes === 1 ? "1 minute ago" : `${minutes} minutes ago`;
  }
  if (secondsPast < 86400) {
    const hours = Math.floor(secondsPast / 3600);
    return hours === 1 ? "1 hour ago" : `${hours} hours ago`;
  }
  if (secondsPast < 604800) {
    const days = Math.floor(secondsPast / 86400);
    return days === 1 ? "1 day ago" : `${days} days ago`;
  }
  if (secondsPast < 2592000) {
    const weeks = Math.floor(secondsPast / 604800);
    return weeks === 1 ? "1 week ago" : `${weeks} weeks ago`;
  }

  return date.toLocaleDateString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
}

/** Short date for tables (e.g. May 17 or May 17, 2025). */
export function formatDateShort(dateInput) {
  const date = parseDateTime(dateInput);
  if (!date) {
    return "—";
  }
  const now = new Date();
  const opts = { month: "short", day: "numeric" };
  if (date.getFullYear() !== now.getFullYear()) {
    opts.year = "numeric";
  }
  return date.toLocaleDateString(undefined, opts);
}

/** Time only (e.g. 1:26 PM). */
export function formatTimeShort(dateInput) {
  const date = parseDateTime(dateInput);
  if (!date) {
    return "";
  }
  return date.toLocaleTimeString(undefined, {
    hour: "2-digit",
    minute: "2-digit",
  });
}

/** Compact date + time for admin tables (e.g. May 17, 1:40 PM). */
export function formatRecordedLine(dateInput) {
  const date = formatDateShort(dateInput);
  const time = formatTimeShort(dateInput);
  if (!time || date === "—") {
    return date;
  }
  return `${date}, ${time}`;
}

/** Preset day counts for health trend charts. */
export const HEALTH_TIME_RANGES = [
  { value: 7, label: "Last 7 days" },
  { value: 30, label: "Last 30 days" },
  { value: 60, label: "Last 60 days" },
  { value: 90, label: "Last 90 days" },
  { value: 180, label: "Last 6 months" },
  { value: 365, label: "Last 12 months" },
];

/**
 * Compact labels for chart X-axis (avoids raw yyyy-MM-dd HH:mm:ss).
 */
export function formatChartAxisLabel(dateInput, allDates = []) {
  const date = parseDateTime(dateInput);
  if (!date) {
    return String(dateInput ?? "");
  }
  const parsed = (allDates || [])
    .map(parseDateTime)
    .filter((d) => d != null);
  if (parsed.length <= 1) {
    return formatRecordedLine(dateInput);
  }
  const sameDay = parsed.every(
    (d) => d.toDateString() === parsed[0].toDateString(),
  );
  if (sameDay) {
    return formatTimeShort(dateInput);
  }
  const spanMs =
    Math.max(...parsed.map((d) => d.getTime())) -
    Math.min(...parsed.map((d) => d.getTime()));
  if (spanMs < 48 * 60 * 60 * 1000) {
    return formatTimeShort(dateInput);
  }
  return formatDateShort(dateInput);
}

/** Full local date-time for tooltips. */
export function formatDateTimeFull(dateInput) {
  const date = parseDateTime(dateInput);
  if (!date) {
    return "";
  }
  return date.toLocaleString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}
