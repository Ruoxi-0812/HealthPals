import test from "node:test";
import assert from "node:assert/strict";
import { webcrypto } from "node:crypto";
import { readFile } from "node:fs/promises";

const source = await readFile(new URL("../src/utils/healthSubmission.js", import.meta.url), "utf8");
const { submitHealthReadings } = await import(`data:text/javascript;base64,${Buffer.from(source).toString("base64")}`);
const readings = [{ healthModelConfigId: 1, value: "21" }];
function environment() {
  const store = new Map();
  return {
    crypto: webcrypto,
    sessionStorage: {
      getItem: (key) => store.get(key) ?? null,
      setItem: (key, value) => store.set(key, value),
      removeItem: (key) => store.delete(key),
    },
    store,
  };
}

test("lost response retains key across retry/reload; acknowledged success starts a new submission", async () => {
  const env = environment();
  const keys = [];
  const client = { post: async (url, payload, config) => {
    assert.equal(url, "/user-health/save");
    assert.deepEqual(payload, readings);
    keys.push(config.headers["Idempotency-Key"]);
    if (keys.length === 1) throw new Error("response lost after commit");
    return { data: { code: 200 } };
  }};
  await assert.rejects(submitHealthReadings(client, readings, 7, env));
  assert.equal(env.store.size, 1);
  await submitHealthReadings(client, readings, 7, { ...env });
  assert.equal(keys[0], keys[1]);
  assert.equal(env.store.size, 0);
  await submitHealthReadings(client, readings, 7, env);
  assert.notEqual(keys[1], keys[2]);
});

test("different payloads and users have separate retry identities", async () => {
  const env = environment();
  const keys = [];
  const client = { post: async (_url, _payload, config) => {
    keys.push(config.headers["Idempotency-Key"]);
    throw new Error("offline");
  }};
  await assert.rejects(submitHealthReadings(client, readings, 7, env));
  await assert.rejects(submitHealthReadings(client, [{ healthModelConfigId: 1, value: "22" }], 7, env));
  await assert.rejects(submitHealthReadings(client, readings, 8, env));
  await assert.rejects(submitHealthReadings(client, readings, 7, env));
  assert.equal(new Set(keys.slice(0, 3)).size, 3);
  assert.equal(keys[0], keys[3]);
});

test("normalizes values and ignores client-supplied ownership fields", async () => {
  const env = environment();
  const keys = [];
  const client = { post: async (_url, payload, config) => {
    assert.deepEqual(payload, readings);
    keys.push(config.headers["Idempotency-Key"]);
    return { data: { code: 400 } };
  }};
  await submitHealthReadings(client, [{ healthModelConfigId: "1", value: " 21 ", userId: 999 }], 7, env);
  await submitHealthReadings(client, readings, 7, env);
  assert.equal(keys[0], keys[1]);
  assert.equal(env.store.size, 1);
});

test("overlapping requests reuse the persisted key", async () => {
  const env = environment();
  const keys = [];
  let release;
  const bothStarted = new Promise((resolve) => { release = resolve; });
  const client = { post: async (_url, _payload, config) => {
    keys.push(config.headers["Idempotency-Key"]);
    if (keys.length === 2) release();
    await bothStarted;
    return { data: { code: 200 } };
  }};
  await Promise.all([submitHealthReadings(client, readings, 7, env), submitHealthReadings(client, readings, 7, env)]);
  assert.equal(keys[0], keys[1]);
});

test("storage failure or missing user prevents an unsafe request", async () => {
  const env = environment();
  let calls = 0;
  const client = { post: async () => { calls++; } };
  await assert.rejects(submitHealthReadings(client, readings, null, env));
  env.sessionStorage.setItem = () => { throw new Error("storage full"); };
  await assert.rejects(submitHealthReadings(client, readings, 7, env));
  assert.equal(calls, 0);
});
