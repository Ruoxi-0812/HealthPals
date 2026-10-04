import test from "node:test";
import assert from "node:assert/strict";
import fs from "node:fs";
import vm from "node:vm";
function component(name, post) {
  const file = fs.readFileSync(
    new URL(`../src/views/user/${name}.vue`, import.meta.url),
    "utf8",
  );
  const script = file
    .split("<script>")[1]
    .split("</script>")[0]
    .replace(/import[\s\S]*?from\s+["'][^"']+["'];?/g, "")
    .replace("export default", "result =");
  const context = {
    result: null,
    sessionStorage: {
      setItem() {},
      getItem() {
        return null;
      },
    },
    pickUniqueCoverNews: (x) => x,
  };
  // Imported components/helpers are only needed by rendering, not these state transitions.
  for (const n of [
    "newsCoverSrc",
    "onCoverImgError",
    "Logo",
    "Evaluations",
    "UserAvatar",
    "TagLine",
    "Banner",
    "LevelHeader",
  ])
    context[n] = () => {};
  vm.createContext(context);
  vm.runInContext(script, context);
  const options = context.result;
  const instance = {
    ...options.data(),
    $axios: { post },
    $route: { query: {} },
    $message: { error() {} },
  };
  for (const [key, method] of Object.entries(options.methods))
    instance[key] = method.bind(instance);
  instance.scrollToTop = () => {};
  instance.loadSaveStatus = () => {};
  return instance;
}
test("invalid and missing article IDs end the skeleton without a request", async () => {
  const page = component("NewsDetail", () => {
    throw Error("unexpected request");
  });
  for (const id of [null, 0, NaN, -1]) {
    await page.fetchArticleById(id);
    assert.equal(page.articleLoading, false);
    assert.ok(page.articleError);
  }
  page.bootstrapArticle();
  assert.equal(page.articleLoading, false);
});
test("article network/business/not-found errors exit loading", async () => {
  for (const post of [
    async () => {
      throw Error("offline");
    },
    async () => ({ data: { code: 500 } }),
    async () => ({ data: { code: 200, data: [] } }),
  ]) {
    const page = component("NewsDetail", post);
    await page.fetchArticleById(1);
    assert.ok(page.articleError);
    assert.equal(page.articleLoading, false);
  }
});
test("older article completion cannot overwrite the current article or loading state", async () => {
  const pending = [];
  const page = component(
    "NewsDetail",
    () => new Promise((resolve) => pending.push(resolve)),
  );
  page.loadAllTopNews = () => {};
  const first = page.fetchArticleById(1),
    second = page.fetchArticleById(2);
  pending[0]({ data: { code: 200, data: [{ id: 1 }] } });
  await first;
  assert.equal(page.articleLoading, true);
  pending[1]({ data: { code: 200, data: [{ id: 2 }] } });
  await second;
  assert.equal(page.newsInfo.id, 2);
  assert.equal(page.articleLoading, false);
});
test("recommendation and message failures show failure, successful empty retry clears it", async () => {
  for (const [name, method, flag, loading] of [
    ["NewsDetail", "loadAllTopNews", "asideFailed", "asideLoading"],
    ["Message", "loadAllUsersMessage", "loadFailed", "loading"],
  ]) {
    let fail = true;
    const page = component(name, async () => ({
      data: { code: fail ? 500 : 200, data: [] },
    }));
    await page[method]();
    assert.equal(page[flag], true);
    assert.equal(page[loading], false);
    fail = false;
    await page[method]();
    assert.equal(page[flag], false);
    assert.equal(page[loading], false);
  }
});
test("home treats business errors as failures across all sections", async () => {
  const page = component("Home", async () => ({ data: { code: 500 } }));
  for (const [method, flag] of [
    ["loadAllTags", "tagsLoadFailed"],
    ["loadAllTopNews", "topLoadFailed"],
    ["loadAllNews", "feedLoadFailed"],
  ]) {
    await page[method]();
    assert.equal(page[flag], true);
  }
});
