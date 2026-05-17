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
