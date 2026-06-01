<template>
  <div class="news-detail">
    <div class="news-detail__toolbar">
      <button type="button" class="news-detail__back" @click="goBack">
        <i class="el-icon-arrow-left" aria-hidden="true" />
        <span>Back to news</span>
      </button>
    </div>

    <el-row :gutter="28" class="news-detail__row">
      <el-col :xs="24" :md="16" class="news-detail__main-wrap">
        <article
          :key="'article-' + (newsInfo.id || 'loading')"
          class="news-detail__article nb-surface"
        >
          <div class="news-detail__hero">
            <img
              class="news-detail__hero-img"
              :src="newsCoverSrc(newsInfo.cover, newsInfo.id)"
              :alt="newsInfo.name || 'Article cover'"
              referrerpolicy="no-referrer"
              @error="onCoverImgError"
            />
            <div class="news-detail__hero-overlay"></div>
          </div>

          <div class="news-detail__header">
            <div class="news-detail__header-main">
              <div class="news-detail__meta">
                <span class="news-detail__tag">{{ newsInfo.tagName || "Wellness" }}</span>
                <time class="news-detail__time" :datetime="newsInfo.createTime">{{
                  parseTime(newsInfo.createTime)
                }}</time>
                <span class="news-detail__read-time">{{ estimatedReadMinutes }} min read</span>
              </div>

              <h1 class="news-detail__title">{{ newsInfo.name }}</h1>
            </div>

            <button
              type="button"
              class="news-detail__save"
              :class="{ 'is-saved': saveFlag }"
              @click="saveNewsOperation"
            >
              {{ !saveFlag ? "Save article" : "Saved" }}
            </button>
          </div>

          <div
            v-if="articleHasContent"
            class="news-detail__body"
            v-html="newsInfo.content"
          ></div>
          <p v-else class="news-detail__empty">This article has no body text yet.</p>

          <div class="news-detail__comments">
            <Evaluations
              :key="'comments-' + (newsInfo.id || 0)"
              :content-id="Number(newsInfo.id)"
              content-type="NEWS"
            />
          </div>
        </article>
      </el-col>

      <el-col :xs="24" :md="8" class="news-detail__aside-wrap">
        <aside class="news-detail__aside nb-surface">
          <p class="news-detail__aside-eyebrow">Continue reading</p>
          <h2 class="news-detail__aside-title">Recommended</h2>
          <p class="news-detail__aside-sub">
            {{ newsTopList.length || 0 }} more picks from our editors.
          </p>
          <ul class="news-detail__rec-list">
            <li
              v-for="(news, index) in newsTopList"
              :key="news.id || index"
              class="news-detail__rec-item"
              @click="newsItemClick(news)"
            >
              <div class="news-detail__rec-card">
                <img
                  class="news-detail__rec-img"
                  :src="newsCoverSrc(news.cover, news.id)"
                  :alt="news.name"
                  loading="lazy"
                  referrerpolicy="no-referrer"
                  @error="onCoverImgError"
                />
                <div class="news-detail__rec-body">
                  <h3 class="news-detail__rec-title">{{ news.name }}</h3>
                  <div class="news-detail__rec-foot">
                    <span class="news-detail__rec-tag">{{ news.tagName }}</span>
                    <span class="news-detail__rec-time">{{
                      parseTime(news.createTime)
                    }}</span>
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </aside>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { timeAgo } from "@/utils/data";
import Evaluations from "@/components/Evaluations.vue";
import {
  newsCoverSrc,
  onCoverImgError,
  pickUniqueCoverNews,
} from "@/utils/coverImage";

export default {
  components: { Evaluations },
  name: "NewsDetail",
  data() {
    return {
      newsInfo: {},
      newsTopList: [],
      saveFlag: false,
      newsSaveList: [],
    };
  },
  computed: {
    articleHasContent() {
      const html = (this.newsInfo && this.newsInfo.content) || "";
      const text = html.replace(/<[^>]+>/g, "").replace(/&nbsp;/gi, " ").trim();
      return text.length > 0;
    },
    articlePlainText() {
      return ((this.newsInfo && this.newsInfo.content) || "")
        .replace(/<[^>]+>/g, " ")
        .replace(/&nbsp;/gi, " ")
        .replace(/\s+/g, " ")
        .trim();
    },
    estimatedReadMinutes() {
      if (!this.articlePlainText) {
        return 1;
      }
      const words = this.articlePlainText.split(/\s+/).filter(Boolean).length;
      return Math.max(1, Math.round(words / 180));
    },
  },
  watch: {
    "$route.query.id"(id) {
      if (id != null && id !== "") {
        this.fetchArticleById(Number(id));
      }
    },
  },
  created() {
    this.bootstrapArticle();
    this.loadAllTopNews();
  },
  methods: {
    newsCoverSrc,
    onCoverImgError,
    goBack() {
      if (window.history.length > 1) {
        this.$router.go(-1);
        return;
      }
      if (this.$route.path !== "/news-record") {
        this.$router.push("/news-record");
      }
    },
    loadSaveStatus() {
      if (this.newsInfo == null || this.newsInfo.id == null) {
        return;
      }
      const newsSaveQueryDto = {
        newsId: this.newsInfo.id,
      };
      this.$axios
        .post("/news-save/queryUser", newsSaveQueryDto)
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.saveFlag = data.data.length !== 0;
          }
        });
    },
    saveNewsOperation() {
      this.$axios
        .post("/news-save/operation", { newsId: this.newsInfo.id })
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.$message.success(
              !this.saveFlag ? "Saved Successfully" : "Unsaved Successfully",
            );
            this.saveFlag = !this.saveFlag;
          }
        });
    },
    newsItemClick(news) {
      if (news == null || news.id == null) return;
      const nextId = String(news.id);
      if (this.$route.query.id === nextId) {
        this.fetchArticleById(Number(news.id));
        return;
      }
      this.$router
        .replace({ path: "/news-detail", query: { id: nextId } })
        .catch(() => {});
    },
    bootstrapArticle() {
      const routeId = this.$route.query.id;
      if (routeId != null && routeId !== "") {
        this.fetchArticleById(Number(routeId));
        return;
      }
      this.getStorageInfo();
      if (this.newsInfo && this.newsInfo.id != null) {
        this.fetchArticleById(Number(this.newsInfo.id));
        if (!this.$route.query.id) {
          this.$router
            .replace({
              path: "/news-detail",
              query: { id: String(this.newsInfo.id) },
            })
            .catch(() => {});
        }
      }
    },
    fetchArticleById(id) {
      if (!id || Number.isNaN(id)) return;
      this.$axios
        .post("/news/query", { id })
        .then((response) => {
          const { data } = response;
          if (data.code === 200 && data.data && data.data.length) {
            const article =
              data.data.find((n) => Number(n.id) === Number(id)) || data.data[0];
            this.newsInfo = { ...article };
            sessionStorage.setItem("newsInfo", JSON.stringify(this.newsInfo));
            this.loadSaveStatus();
            this.loadAllTopNews();
            this.scrollToTop();
          }
        })
        .catch(() => {});
    },
    scrollToTop() {
      const el = document.querySelector(".content-container");
      if (el) el.scrollTo({ top: 0, behavior: "smooth" });
      else window.scrollTo({ top: 0, behavior: "smooth" });
    },
    parseTime(time) {
      return timeAgo(time);
    },
    getStorageInfo() {
      const newInfo = sessionStorage.getItem("newsInfo");
      if (newInfo) {
        try {
          this.newsInfo = JSON.parse(newInfo);
        } catch {
          this.newsInfo = {};
        }
      }
    },
    loadAllTopNews() {
      const newQueryDto = { isTop: true };
      this.$axios.post("/news/query", newQueryDto).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          const currentId = this.newsInfo && this.newsInfo.id;
          const list = (data.data || []).filter((n) => n.id !== currentId);
          this.newsTopList = pickUniqueCoverNews(list, 3);
        }
      });
    },
  },
};
</script>

<style scoped lang="scss">
.news-detail {
  width: 100%;
  max-width: min(1120px, 94vw);
  margin: 0 auto;
  padding: clamp(16px, 3vw, 28px) clamp(12px, 2vw, 20px) 40px;
  box-sizing: border-box;
}

.news-detail__toolbar {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 14px;
}

.news-detail__back {
  appearance: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 9px 14px;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  color: #36584a;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 999px;
  box-shadow: 0 8px 24px rgba(53, 92, 75, 0.08);
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease,
    border-color 0.15s ease;

  &:hover {
    transform: translateY(-1px);
    border-color: rgba(42, 157, 111, 0.35);
    box-shadow: 0 12px 24px rgba(53, 92, 75, 0.12);
  }
}

.news-detail__row {
  width: 100%;
}

.news-detail__article {
  overflow: hidden;
  padding: 0;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(126, 197, 160, 0.22);
  margin-bottom: 24px;
  box-shadow: 0 18px 40px rgba(53, 92, 75, 0.1);
}

.news-detail__hero {
  position: relative;
  height: clamp(180px, 28vw, 270px);
  overflow: hidden;
  background: linear-gradient(135deg, #dfeee7 0%, #f6fbf8 100%);
}

.news-detail__hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.news-detail__hero-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, rgba(12, 28, 20, 0.04) 0%, rgba(12, 28, 20, 0.4) 100%),
    linear-gradient(120deg, rgba(42, 157, 111, 0.18) 0%, rgba(255, 255, 255, 0) 55%);
}

.news-detail__header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  padding: clamp(22px, 3vw, 32px) clamp(22px, 3vw, 36px) 24px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.2);
}

.news-detail__header-main {
  min-width: 0;
}

.news-detail__title {
  margin: 0;
  font-family: var(--nb-font-display);
  font-size: clamp(1.55rem, 2.8vw, 2.15rem);
  font-weight: 600;
  letter-spacing: -0.02em;
  line-height: 1.25;
  color: var(--nb-ink);
}

.news-detail__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 16px;
  margin-bottom: 16px;
}

.news-detail__tag {
  display: inline-block;
  padding: 5px 12px;
  font-size: 12px;
  font-weight: 600;
  color: #1a5c4a;
  background: rgba(42, 157, 111, 0.12);
  border: 1px solid rgba(42, 157, 111, 0.22);
  border-radius: 999px;
}

.news-detail__time {
  font-size: 13px;
  color: var(--nb-muted);
}

.news-detail__read-time {
  font-size: 13px;
  font-weight: 600;
  color: #356b55;
}

.news-detail__summary {
  margin: 14px 0 0;
  max-width: 62ch;
  font-size: 15px;
  line-height: 1.7;
  color: rgba(36, 51, 43, 0.68);
}

.news-detail__save {
  flex-shrink: 0;
  appearance: none;
  cursor: pointer;
  align-self: flex-start;
  padding: 10px 18px;
  font-family: var(--nb-font);
  font-size: 13px;
  font-weight: 600;
  border-radius: 999px;
  border: 2px solid #2a9d6f;
  color: #fff;
  background: #2a9d6f;
  box-shadow: 0 2px 8px rgba(42, 157, 111, 0.28);
  transition:
    background 0.15s ease,
    color 0.15s ease,
    border-color 0.15s ease,
    box-shadow 0.15s ease;

  &:hover {
    background: #248760;
    border-color: #248760;
    box-shadow: 0 4px 14px rgba(42, 157, 111, 0.32);
  }

  &.is-saved {
    color: #248760;
    background: rgba(42, 157, 111, 0.12);
    border-color: rgba(42, 157, 111, 0.45);
    box-shadow: none;
  }

  &.is-saved:hover {
    background: rgba(42, 157, 111, 0.2);
  }
}

/* Prose for server-rendered article HTML */
.news-detail__empty {
  margin: 0 clamp(22px, 3vw, 36px) 1.5em;
  padding: 16px 18px;
  border-radius: 12px;
  background: rgba(232, 244, 238, 0.65);
  color: #4a6358;
  font-size: 15px;
}

.news-detail__body {
  padding: 26px clamp(22px, 3vw, 36px) 0;
  font-family: var(--nb-font);
  font-size: 16px;
  line-height: 1.75;
  color: rgba(36, 51, 43, 0.92);

  :deep(p) {
    margin: 0 0 1.1em;
    text-align: justify;
    max-width: 68ch;
  }

  :deep(h3),
  :deep(h4) {
    font-family: var(--nb-font-display);
    font-weight: 600;
    color: var(--nb-ink);
    margin: 1.6em 0 0.65em;
    letter-spacing: -0.02em;
    line-height: 1.3;
  }

  :deep(h3) {
    font-size: 1.2rem;
  }

  :deep(h4) {
    font-size: 1.05rem;
  }

  :deep(ul),
  :deep(ol) {
    margin: 0 0 1.2em;
    padding-left: 1.35em;
    max-width: 68ch;
  }

  :deep(li) {
    margin-bottom: 0.5em;
    padding-left: 0.2em;
  }

  :deep(ul li::marker) {
    color: #2a9d6f;
  }

  :deep(strong) {
    font-weight: 650;
    color: var(--nb-ink);
  }

  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 16px;
    margin: 1.2em 0;
    box-shadow: 0 14px 28px rgba(53, 92, 75, 0.12);
  }

  :deep(a) {
    color: #2a8d66;
    text-decoration-color: rgba(42, 141, 102, 0.35);
    text-underline-offset: 0.18em;
  }

  :deep(blockquote) {
    margin: 1.35em 0;
    padding: 16px 18px;
    border-left: 4px solid #2a9d6f;
    border-radius: 0 14px 14px 0;
    background: rgba(232, 244, 238, 0.66);
    color: rgba(36, 51, 43, 0.8);
  }
}

.news-detail__comments {
  margin: 28px clamp(18px, 3vw, 30px) clamp(18px, 3vw, 30px);
  padding: 22px;
  border: 1px solid rgba(126, 197, 160, 0.18);
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(249, 252, 250, 0.98) 0%, rgba(242, 248, 244, 0.9) 100%);
}

.news-detail__aside {
  position: sticky;
  top: 88px;
  padding: 22px 18px 18px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(126, 197, 160, 0.2);
  box-shadow: 0 16px 34px rgba(53, 92, 75, 0.08);
}

.news-detail__aside-eyebrow {
  margin: 0 0 8px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(53, 82, 71, 0.52);
}

.news-detail__aside-title {
  margin: 0 0 6px;
  font-family: var(--nb-font-display);
  font-size: 1.35rem;
  font-weight: 600;
  color: var(--nb-ink);
  letter-spacing: -0.02em;
}

.news-detail__aside-sub {
  margin: 0 0 20px;
  font-size: 13px;
  line-height: 1.5;
  color: var(--nb-muted);
}

.news-detail__rec-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.news-detail__rec-item {
  margin-bottom: 14px;

  &:last-child {
    margin-bottom: 0;
  }
}

.news-detail__rec-card {
  display: flex;
  gap: 14px;
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(126, 197, 160, 0.22);
  cursor: pointer;
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease,
    transform 0.12s ease;

  &:hover {
    border-color: rgba(42, 157, 111, 0.35);
    box-shadow: var(--nb-shadow-sm);
    transform: translateY(-1px);
  }
}

.news-detail__rec-img {
  width: 100px;
  min-width: 100px;
  height: 72px;
  object-fit: cover;
  border-radius: 10px;
}

.news-detail__rec-body {
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.news-detail__rec-title {
  margin: 0 0 8px;
  font-family: var(--nb-font);
  font-size: 14px;
  font-weight: 650;
  line-height: 1.35;
  color: var(--nb-ink);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-detail__rec-foot {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 10px;
  font-size: 11px;
  align-items: center;
}

.news-detail__rec-tag {
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 600;
  color: #1a5c4a;
  background: rgba(42, 157, 111, 0.1);
  border: 1px solid rgba(42, 157, 111, 0.2);
}

.news-detail__rec-time {
  color: var(--nb-muted);
}

@media (max-width: 991px) {
  .news-detail__aside {
    position: static;
    margin-top: 8px;
  }

  .news-detail__header {
    flex-direction: column;
    align-items: stretch;
  }

  .news-detail__save {
    width: 100%;
  }

  .news-detail__meta {
    gap: 10px 12px;
  }
}

@media (max-width: 720px) {
  .news-detail {
    max-width: 100%;
    padding-inline: 10px;
  }

  .news-detail__hero {
    height: 190px;
  }

  .news-detail__header,
  .news-detail__body,
  .news-detail__empty {
    padding-left: 18px;
    padding-right: 18px;
  }

  .news-detail__comments {
    margin: 22px 14px 14px;
    padding: 16px;
  }

  .news-detail__rec-card {
    padding: 10px;
  }

  .news-detail__rec-img {
    width: 92px;
    min-width: 92px;
  }
}
</style>
