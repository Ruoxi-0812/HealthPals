// A failed/unknown response keeps its key, including across reloads in this tab.
// Only fingerprints and random request keys are stored, not readings.
export async function submitHealthReadings(
  client,
  readings,
  userId,
  env = window,
) {
  if (userId == null) throw new Error("Please log in again before saving.");
  const payload = readings.map((reading) => ({
    healthModelConfigId: Number(reading.healthModelConfigId),
    value: String(reading.value).trim(),
  }));
  const bytes = new TextEncoder().encode(JSON.stringify(payload));
  const digest = await env.crypto.subtle.digest("SHA-256", bytes);
  const fingerprint = Array.from(new Uint8Array(digest), (byte) =>
    byte.toString(16).padStart(2, "0"),
  ).join("");
  const slot = `health-submission:${userId}:${fingerprint}`;
  let key = env.sessionStorage.getItem(slot);
  if (!key) {
    key = Array.from(env.crypto.getRandomValues(new Uint8Array(16)), (byte) =>
      byte.toString(16).padStart(2, "0"),
    ).join("");
    // Persist before sending; if storage fails, do not send an unretryable request.
    env.sessionStorage.setItem(slot, key);
  }
  const response = await client.post("/user-health/save", payload, {
    headers: { "Idempotency-Key": key },
  });
  if (response.data.code === 200 && env.sessionStorage.getItem(slot) === key) {
    env.sessionStorage.removeItem(slot);
  }
  return response;
}
