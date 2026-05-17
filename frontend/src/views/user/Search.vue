<template>
  <div class="search-page">
    <header class="search-page__head section-head">
      <div>
        <span class="section-head__eyebrow">Search</span>
        <h1 class="section-head__title">Results</h1>
        <p v-if="displayQuery" class="section-head__lede">
          Showing matches for
          <strong class="search-page__query">“{{ displayQuery }}”</strong>
        </p>
        <p v-else class="section-head__lede">
          Showing the latest catalog.
          <span class="section-head__hint">Tip: use the bar above to narrow results.</span>
        </p>
      </div>
      <p v-if="newsData.length" class="search-page__count">
        {{ newsData.length }}
        {{ newsData.length === 1 ? "article" : "articles" }}
      </p>
    </header>

    <div v-if="loading" class="search-page__loading">
      <div
        v-for="n in 6"
        :key="'sk-' + n"
        class="search-page__skeleton skeleton-pulse"
      />
    </div>

    <div v-else-if="newsData.length === 0" class="search-page__empty nb-surface--sm">
      <el-empty description="">
        <template slot="image">
          <div class="search-page__empty-icon" aria-hidden="true">
            <i class="el-icon-search" />
          </div>
        </template>
        <template slot="description">
          <p class="search-page__empty-title">No articles found</p>
          <p class="search-page__empty-text">
            Try a shorter keyword, check spelling, or browse topics from
            <router-link class="search-page__inline-link" to="/news-record">
              News home
            </router-link>
            .
          </p>
        </template>
      </el-empty>
    </div>

    <div v-else-if="!loading" class="search-page__grid">
      <article
        v-for="(news, index) in newsData"
        :key="news.id || index"
        class="search-card"
        tabindex="0"
        role="link"
        @click="newsItemClick(news)"
        @keydown.enter.prevent="newsItemClick(news)"
      >
        <div class="search-card__media">
          <img
            :src="news.cover"
            :alt="news.name"
            class="search-card__img"
            loading="lazy"
            @error="onImgError"
          />
        </div>
        <div class="search-card__body">
          <h2 class="search-card__title">{{ news.name }}</h2>
          <div class="search-card__meta">
            <span class="search-card__pill">{{ news.tagName }}</span>
            <time class="search-card__time" :datetime="news.createTime">
              Posted {{ parseTime(news.createTime) }}
            </time>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
import { timeAgo } from "@/utils/data";

const BLANK_COVER =
  "data:image/svg+xml," +
  encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="320" height="200" viewBox="0 0 320 200"><rect fill="#e8f4ed" width="320" height="200"/><text x="160" y="105" text-anchor="middle" fill="#7a9a8a" font-family="system-ui,sans-serif" font-size="14">No image</text></svg>`,
  );

export default {
  name: "SearchPage",
  data() {
    return {
      keyWord: "",
      newsData: [],
      total: 0,
      loading: true,
    };
  },
  computed: {
    displayQuery() {
      return (this.keyWord || "").trim();
    },
  },
  created() {
    this.$root.$on("app:search-keyword-updated", this.onKeywordUpdated);
    this.keyWordParse();
    this.syncSearchInput();
  },
  beforeDestroy() {
    this.$root.$off("app:search-keyword-updated", this.onKeywordUpdated);
  },
  methods: {
    onImgError(e) {
      const el = e && e.target;
      if (el) el.src = BLANK_COVER;
    },
    onKeywordUpdated() {
      this.keyWordParse();
    },
    syncSearchInput() {
      const kw = sessionStorage.getItem("keyWord") || "";
      this.$root.$emit("app:search-sync-input", kw);
    },
    newsItemClick(news) {
      if (news && news.id != null) {
        this.$router.push({
          path: "/news-detail",
          query: { id: String(news.id) },
        });
      }
    },
    parseTime(time) {
      return timeAgo(time);
    },
    keyWordParse() {
      this.keyWord = sessionStorage.getItem("keyWord") || "";
      this.fetchData();
    },
    async fetchData() {
      this.loading = true;
      try {
        const newsQueryDto = { name: this.keyWord || "" };
        const response = await this.$axios.post(`/news/query`, newsQueryDto);
        const { data } = response;
        this.newsData = Array.isArray(data.data) ? data.data : [];
        this.total =
          data.total != null ? data.total : this.newsData.length;
      } catch (e) {
        console.error(`Error loading news list: ${e}`);
        this.newsData = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.search-page {
  max-width: 1200px;
  margin: 0 auto;
}

.section-head {
  margin-bottom: 22px;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.section-head__eyebrow {
  display: block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(36, 51, 43, 0.52);
  margin-bottom: 6px;
}

.section-head__title {
  margin: 0 0 8px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: clamp(1.45rem, 2.4vw, 1.75rem);
  font-weight: 600;
  color: var(--nb-ink, #24332b);
  letter-spacing: -0.02em;
}

.section-head__lede {
  margin: 0;
  max-width: 52ch;
  font-size: 14px;
  line-height: 1.55;
  color: var(--nb-muted, rgba(36, 51, 43, 0.62));
}

.section-head__hint {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: rgba(36, 51, 43, 0.48);
}

.search-page__query {
  font-weight: 650;
  color: var(--nb-ink, #24332b);
}

.search-page__count {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: rgba(36, 51, 43, 0.55);
}

.search-page__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
  align-content: start;
  justify-items: stretch;
}

.search-card {
  height: 100%;
  margin: 0;
  background: var(--nb-paper, #fff);
  border-radius: 16px;
  border: 1px solid rgba(126, 197, 160, 0.22);
  box-shadow: 0 8px 22px rgba(53, 92, 75, 0.08);
  overflow: hidden;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
  display: flex;
  flex-direction: column;
  outline: none;

  &:hover,
  &:focus-visible {
    transform: translateY(-4px);
    box-shadow: 0 14px 32px rgba(53, 92, 75, 0.14);
    border-color: rgba(42, 157, 111, 0.35);
  }

  &:focus-visible {
    box-shadow:
      0 0 0 3px rgba(126, 197, 160, 0.35),
      0 14px 32px rgba(53, 92, 75, 0.12);
  }
}

.search-card__media {
  aspect-ratio: 16 / 10;
  overflow: hidden;
  background: rgba(126, 197, 160, 0.15);
}

.search-card__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.search-card__body {
  padding: 14px 16px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-card__title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  color: var(--nb-ink, #24332b);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}

.search-card__meta {
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 12px;
  font-size: 12px;
  color: rgba(36, 51, 43, 0.55);
}

.search-card__pill {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  font-family: var(--nb-font, system-ui, sans-serif);
  background: rgba(29, 60, 196, 0.08);
  color: #1d3cc4;
  border: 1px solid rgba(29, 60, 196, 0.18);
}

.search-card__time {
  font-weight: 500;
  font-family: var(--nb-font, system-ui, sans-serif);
}

.search-page__loading {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
}

.search-page__skeleton {
  min-height: 280px;
  border-radius: 16px;
  background: rgba(126, 197, 160, 0.18);
  animation: search-skeleton-pulse 1.2s ease-in-out infinite;
}

@keyframes search-skeleton-pulse {
  0%,
  100% {
    opacity: 0.55;
  }
  50% {
    opacity: 1;
  }
}

.search-page__empty {
  padding: 48px 24px 56px;
  text-align: center;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 16px;
}

.search-page__empty :deep(.el-empty__description) {
  padding-top: 6px;
}

.search-page__empty-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 8px;
  border-radius: 18px;
  background: rgba(126, 197, 160, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #4d8b73;
}

.search-page__empty-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 650;
  color: var(--nb-ink, #24332b);
}

.search-page__empty-text {
  margin: 0 auto;
  max-width: 40ch;
  font-size: 14px;
  line-height: 1.55;
  color: var(--nb-muted, rgba(36, 51, 43, 0.62));
}

.search-page__inline-link {
  font-weight: 650;
  color: #2a6b52;
  text-decoration: underline;
  text-underline-offset: 2px;

  &:hover {
    color: #248760;
  }
}
</style>
