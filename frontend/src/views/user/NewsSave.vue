<template>
  <div class="save-page">
    <header class="save-page__head section-head">
      <div>
        <span class="section-head__eyebrow">Library</span>
        <h1 class="section-head__title">My favorites</h1>
        <p class="section-head__lede">
          Articles you saved for later. Open any card to pick up where you left
          off.
        </p>
      </div>
      <p v-if="newsSaveList.length" class="save-page__count">
        {{ newsSaveList.length }}
        {{ newsSaveList.length === 1 ? "article" : "articles" }} saved
      </p>
    </header>

    <div v-if="newsSaveList.length === 0" class="save-page__empty nb-surface--sm">
      <el-empty description="">
        <template slot="image">
          <div class="save-page__empty-icon" aria-hidden="true">
            <i class="el-icon-star-off" />
          </div>
        </template>
        <template slot="description">
          <p class="save-page__empty-title">No saved articles yet</p>
          <p class="save-page__empty-text">
            When you find something worth revisiting, save it from the article
            page—it will show up here.
          </p>
          <router-link class="save-page__empty-cta" to="/news-record">
            Browse the news home
          </router-link>
        </template>
      </el-empty>
    </div>

    <div v-else class="save-page__grid">
      <article
        v-for="(news, index) in newsSaveList"
        :key="news.id || news.newsId || index"
        class="save-card"
        tabindex="0"
        role="link"
        @click="newsItemClick(news)"
        @keydown.enter.prevent="newsItemClick(news)"
      >
        <div class="save-card__media">
          <img
            :src="news.cover"
            :alt="news.name"
            class="save-card__img"
            loading="lazy"
            @error="onImgError"
          />
        </div>
        <div class="save-card__body">
          <h2 class="save-card__title">{{ news.name }}</h2>
          <div class="save-card__meta">
            <span class="save-card__pill">{{ news.tagName }}</span>
            <time class="save-card__time" :datetime="news.createTime">
              Saved {{ parseTime(news.createTime) }}
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
  name: "NewsSave",
  data() {
    return {
      newsSaveList: [],
    };
  },
  created() {
    this.loadAllSaveNews();
  },
  methods: {
    onImgError(e) {
      const el = e && e.target;
      if (el) el.src = BLANK_COVER;
    },
    parseTime(time) {
      return timeAgo(time);
    },
    newsItemClick(newsSave) {
      if (newsSave && newsSave.newsId != null) {
        this.$router.push({
          path: "/news-detail",
          query: { id: String(newsSave.newsId) },
        });
      }
    },
    loadAllSaveNews() {
      const userInfo = sessionStorage.getItem("userInfo");
      const userInfoEntity = JSON.parse(userInfo);
      const newsSaveQueryDto = {
        userId: userInfoEntity.id,
      };
      this.$axios
        .post("/news-save/query", newsSaveQueryDto)
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.newsSaveList = data.data;
          }
        });
    },
  },
};
</script>

<style scoped lang="scss">
.save-page {
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
  max-width: 48ch;
  font-size: 14px;
  line-height: 1.55;
  color: var(--nb-muted, rgba(36, 51, 43, 0.62));
}

.save-page__count {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: rgba(36, 51, 43, 0.55);
}

.save-page__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
  align-content: start;
  justify-items: stretch;
}

.save-card {
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

.save-card__media {
  aspect-ratio: 16 / 10;
  overflow: hidden;
  background: rgba(126, 197, 160, 0.15);
}

.save-card__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.save-card__body {
  padding: 14px 16px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.save-card__title {
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

.save-card__meta {
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 12px;
  font-size: 12px;
  color: rgba(36, 51, 43, 0.55);
}

.save-card__pill {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  font-family: var(--nb-font, system-ui, sans-serif);
  background: rgba(42, 157, 111, 0.1);
  color: #2a6b52;
  border: 1px solid rgba(42, 157, 111, 0.22);
}

.save-card__time {
  font-weight: 500;
  font-family: var(--nb-font, system-ui, sans-serif);
}

.save-page__empty {
  padding: 48px 24px 56px;
  text-align: center;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 16px;
}

.save-page__empty :deep(.el-empty__description) {
  padding-top: 6px;
}

.save-page__empty-icon {
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

.save-page__empty-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 650;
  color: var(--nb-ink, #24332b);
}

.save-page__empty-text {
  margin: 0 auto 16px;
  max-width: 40ch;
  font-size: 14px;
  line-height: 1.55;
  color: var(--nb-muted, rgba(36, 51, 43, 0.62));
}

.save-page__empty-cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 18px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 650;
  text-decoration: none;
  color: #fff;
  background: #2a9d6f;
  border: 1px solid #248760;
  transition: background 0.15s ease;

  &:hover {
    background: #248760;
  }
}
</style>
