<template>
  <div class="search-page">
    <div v-if="loading" class="search-page__surface nb-surface">
      <div class="search-page__skeleton-grid">
        <div
          v-for="n in 6"
          :key="'sk-' + n"
          class="search-page__skeleton-card skeleton-pulse"
        />
      </div>
    </div>

    <template v-else>
      <div
        v-if="newsData.length"
        class="search-page__toolbar nb-surface--sm"
      >
        <p class="search-page__count">
          <span class="search-page__count-num">{{ resultCount }}</span>
          {{ resultCount === 1 ? "article" : "articles" }}
          <template v-if="displayQuery">
            for
            <span class="search-page__query">“{{ displayQuery }}”</span>
          </template>
        </p>
        <router-link class="search-page__browse" to="/news-record">
          News home
          <i class="el-icon-right" aria-hidden="true" />
        </router-link>
      </div>

      <div
        v-if="newsData.length === 0"
        class="search-page__empty nb-surface--sm"
      >
        <el-empty description="">
          <template slot="image">
            <div class="search-page__empty-icon" aria-hidden="true">
              <i class="el-icon-search" />
            </div>
          </template>
          <template slot="description">
            <p class="search-page__empty-title">No articles found</p>
            <p class="search-page__empty-text">
              <template v-if="displayQuery">
                Nothing matched “{{ displayQuery }}”. Try a shorter keyword or
                check spelling.
              </template>
              <template v-else>
                Try a shorter keyword, check spelling, or browse topics from
                News home.
              </template>
            </p>
            <router-link class="search-page__empty-cta" to="/news-record">
              Browse News home
            </router-link>
          </template>
        </el-empty>
      </div>

      <div v-else class="search-page__surface nb-surface">
        <div class="search-page__grid">
          <article
            v-for="(news, index) in newsData"
            :key="news.id || index"
            class="feed-card search-card"
            tabindex="0"
            role="link"
            @click="newsItemClick(news)"
            @keydown.enter.prevent="newsItemClick(news)"
          >
            <div class="feed-card__media">
              <img
                :src="newsCoverSrc(news.cover, news.id)"
                :alt="news.name"
                :data-news-id="news.id"
                class="feed-card__img"
                loading="lazy"
                referrerpolicy="no-referrer"
                @error="onCoverImgError"
              />
            </div>
            <div class="feed-card__body">
              <h2 class="feed-card__title">{{ news.name }}</h2>
              <div class="feed-card__meta">
                <span v-if="news.tagName" class="pill pill--ghost">{{
                  news.tagName
                }}</span>
                <time
                  v-if="postedLabel(news.createTime)"
                  class="search-card__time"
                  :datetime="news.createTime"
                >
                  {{ postedLabel(news.createTime) }}
                </time>
              </div>
            </div>
          </article>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { formatDateShort } from "@/utils/data";
import { newsCoverSrc, onCoverImgError } from "@/utils/coverImage";

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
    resultCount() {
      return this.total > 0 ? this.total : this.newsData.length;
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
    newsCoverSrc,
    onCoverImgError,
    postedLabel(time) {
      const label = formatDateShort(time);
      return label === "—" ? "" : `Posted ${label}`;
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
$ink: #24332b;
$ink-soft: rgba(36, 51, 43, 0.62);
$accent: #3d8b6a;
$mint: rgba(126, 197, 160, 0.15);
$accent-soft: rgba(61, 139, 106, 0.12);
$radius-md: 16px;
$shadow: 0 8px 22px rgba(53, 92, 75, 0.08);
$shadow-hover: 0 14px 32px rgba(53, 92, 75, 0.14);

.search-page {
  width: 100%;
  max-width: none;
  margin: 0;
  box-sizing: border-box;
}

.search-page__toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px 16px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 14px;
}

.search-page__count {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: $ink-soft;
  line-height: 1.45;
}

.search-page__count-num {
  font-weight: 700;
  color: $ink;
}

.search-page__query {
  font-weight: 650;
  color: $ink;
}

.search-page__browse {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 650;
  color: $accent;
  text-decoration: none;
  transition: color 0.15s ease;

  &:hover {
    color: darken($accent, 8%);
  }

  i {
    font-size: 14px;
  }
}

.search-page__surface {
  padding: 18px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: $radius-md;
  box-shadow: $shadow;
}

.search-page__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 18px;
  align-content: start;
  width: 100%;
}

.search-page__skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 18px;
  padding: 18px;
  width: 100%;
}

.search-page__skeleton-card {
  border-radius: 14px;
  min-height: 280px;
  background: rgba(126, 197, 160, 0.16);
}

.skeleton-pulse {
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

.feed-card {
  height: 100%;
  margin: 0;
  background: #fff;
  border-radius: $radius-md;
  border: 1px solid rgba(126, 197, 160, 0.2);
  box-shadow: $shadow;
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
    transform: translateY(-3px);
    box-shadow: $shadow-hover;
    border-color: rgba(61, 139, 106, 0.35);
  }

  &:focus-visible {
    box-shadow:
      0 0 0 3px rgba(126, 197, 160, 0.35),
      $shadow-hover;
  }
}

.feed-card__media {
  aspect-ratio: 16 / 10;
  overflow: hidden;
  background: $mint;
}

.feed-card__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.35s ease;
}

.feed-card:hover .feed-card__img {
  transform: scale(1.03);
}

.feed-card__body {
  padding: 16px 16px 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 108px;
}

.feed-card__title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  color: $ink;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
  min-height: 2.8em;
}

.feed-card__meta {
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 10px;
  font-size: 12px;
  color: $ink-soft;
  padding-top: 4px;
}

.search-card__time {
  font-weight: 500;
  white-space: nowrap;
}

.pill {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  background: $accent-soft;
  color: darken($accent, 6%);
  border: 1px solid rgba(61, 139, 106, 0.2);

  &--ghost {
    background: rgba(232, 244, 238, 0.9);
    color: $ink-soft;
    border-color: rgba(126, 197, 160, 0.28);
  }
}

.search-page__empty {
  padding: 48px 24px 56px;
  text-align: center;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: $radius-md;
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
  color: $ink;
}

.search-page__empty-text {
  margin: 0 auto 16px;
  max-width: 40ch;
  font-size: 14px;
  line-height: 1.55;
  color: $ink-soft;
}

.search-page__empty-cta {
  display: inline-flex;
  align-items: center;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 650;
  color: #fff;
  background: #2a9d6f;
  border-radius: 999px;
  text-decoration: none;
  box-shadow: 0 2px 10px rgba(42, 157, 111, 0.22);
  transition:
    background 0.15s ease,
    transform 0.12s ease;

  &:hover {
    background: #248760;
    transform: translateY(-1px);
  }
}

@media (max-width: 640px) {
  .search-page__grid,
  .search-page__skeleton-grid {
    grid-template-columns: 1fr;
  }
}
</style>
