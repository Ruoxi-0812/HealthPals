<template>
  <div class="save-page">
    <div v-if="loading" class="save-page__surface nb-surface">
      <div class="save-page__skeleton-grid">
        <div
          v-for="n in 6"
          :key="'sk-' + n"
          class="save-page__skeleton-card skeleton-pulse"
        />
      </div>
    </div>

    <template v-else>
      <div
        v-if="newsSaveList.length"
        class="save-page__toolbar nb-surface--sm"
      >
        <p class="save-page__count">
          {{ newsSaveList.length }}
          {{ newsSaveList.length === 1 ? "article" : "articles" }} saved
        </p>
        <router-link class="save-page__browse" to="/news-record">
          Browse articles
          <i class="el-icon-right" aria-hidden="true" />
        </router-link>
      </div>

      <div
        v-if="newsSaveList.length === 0"
        class="save-page__empty nb-surface--sm"
      >
        <el-empty description="">
          <template slot="image">
            <div class="save-page__empty-icon" aria-hidden="true">
              <i class="el-icon-star-off" />
            </div>
          </template>
          <template slot="description">
            <p class="save-page__empty-title">No saved articles yet</p>
            <p class="save-page__empty-text">
              Save articles from the news feed to find them here later.
            </p>
            <router-link class="save-page__empty-cta" to="/news-record">
              Browse the news home
            </router-link>
          </template>
        </el-empty>
      </div>

      <div v-else class="save-page__surface nb-surface">
        <div class="save-page__grid">
          <article
            v-for="(news, index) in newsSaveList"
            :key="news.id || news.newsId || index"
            class="feed-card save-card"
            tabindex="0"
            role="link"
            @click="newsItemClick(news)"
            @keydown.enter.prevent="newsItemClick(news)"
          >
            <div class="feed-card__media save-card__media">
              <img
                :src="newsCoverSrc(news.cover, news.newsId)"
                :alt="news.name"
                :data-news-id="news.newsId"
                class="feed-card__img"
                loading="lazy"
                referrerpolicy="no-referrer"
                @error="onCoverImgError"
              />
              <button
                type="button"
                class="save-card__unsave"
                aria-label="Remove from favorites"
                :disabled="removingId === news.newsId"
                @click.stop="unsaveNews(news)"
              >
                <i class="el-icon-star-on" aria-hidden="true" />
              </button>
            </div>
            <div class="feed-card__body">
              <h2 class="feed-card__title save-card__title">{{ news.name }}</h2>
              <div class="feed-card__meta">
                <span v-if="news.tagName" class="pill pill--ghost">{{
                  news.tagName
                }}</span>
                <time :datetime="news.createTime">
                  Saved {{ parseTime(news.createTime) }}
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
import { timeAgo } from "@/utils/data";
import { newsCoverSrc, onCoverImgError } from "@/utils/coverImage";

export default {
  name: "NewsSave",
  data() {
    return {
      newsSaveList: [],
      loading: true,
      removingId: null,
    };
  },
  created() {
    this.loadAllSaveNews();
  },
  methods: {
    newsCoverSrc,
    onCoverImgError,
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
    async unsaveNews(news) {
      if (!news || news.newsId == null || this.removingId != null) {
        return;
      }
      this.removingId = news.newsId;
      try {
        const response = await this.$axios.post("/news-save/operation", {
          newsId: news.newsId,
        });
        const { data } = response;
        if (data.code === 200) {
          this.newsSaveList = this.newsSaveList.filter(
            (item) => item.newsId !== news.newsId,
          );
          this.$message.success("Removed from favorites");
        } else {
          this.$message.error(data.msg || "Could not remove save");
        }
      } catch (e) {
        this.$message.error(String(e));
      } finally {
        this.removingId = null;
      }
    },
    loadAllSaveNews() {
      this.loading = true;
      const userInfo = sessionStorage.getItem("userInfo");
      let userInfoEntity;
      try {
        userInfoEntity = JSON.parse(userInfo);
      } catch {
        this.loading = false;
        this.newsSaveList = [];
        return;
      }
      const newsSaveQueryDto = {
        userId: userInfoEntity.id,
      };
      this.$axios
        .post("/news-save/query", newsSaveQueryDto)
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.newsSaveList = data.data || [];
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped lang="scss">
$ink: #24332b;
$ink-soft: rgba(36, 51, 43, 0.62);
$accent: #3d8b6a;
$accent-soft: rgba(61, 139, 106, 0.12);
$mint: rgba(126, 197, 160, 0.15);
$shadow: 0 8px 22px rgba(53, 92, 75, 0.08);
$shadow-hover: 0 14px 32px rgba(53, 92, 75, 0.14);
$radius-md: 16px;

.save-page {
  width: 100%;
  max-width: none;
  margin: 0;
  box-sizing: border-box;
}

.save-page__toolbar {
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

.save-page__count {
  margin: 0;
  font-size: 13px;
  font-weight: 650;
  color: $ink-soft;
}

.save-page__browse {
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

.save-page__surface {
  padding: 18px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: $radius-md;
  box-shadow: $shadow;
}

.save-page__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 18px;
  align-content: start;
  width: 100%;
}

.save-page__skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 18px;
  padding: 18px;
  width: 100%;
}

.save-page__skeleton-card {
  border-radius: 14px;
  min-height: 280px;
  background: rgba(126, 197, 160, 0.16);
}

.skeleton-pulse {
  animation: save-skeleton-pulse 1.2s ease-in-out infinite;
}

@keyframes save-skeleton-pulse {
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

.save-card__media {
  position: relative;
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
  transform: scale(1.04);
}

.save-card__unsave {
  position: absolute;
  top: 10px;
  right: 10px;
  appearance: none;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #c97c3a;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 10px rgba(53, 92, 75, 0.15);
  cursor: pointer;
  opacity: 0;
  transform: translateY(4px);
  transition:
    opacity 0.15s ease,
    transform 0.15s ease,
    background 0.15s ease;

  &:hover:not(:disabled) {
    background: #fff;
    color: #9b3d3d;
  }

  &:disabled {
    cursor: wait;
    opacity: 1;
  }
}

.feed-card:hover .save-card__unsave,
.feed-card:focus-within .save-card__unsave {
  opacity: 1;
  transform: translateY(0);
}

.feed-card__body {
  padding: 16px 16px 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.save-card__title {
  -webkit-line-clamp: 3;
}

.feed-card__title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: $ink;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.feed-card__meta {
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: $ink-soft;

  time {
    opacity: 0.88;
  }
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

.save-page__empty {
  padding: 48px 24px 56px;
  text-align: center;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: $radius-md;
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
  color: $ink;
}

.save-page__empty-text {
  margin: 0 auto 16px;
  max-width: 40ch;
  font-size: 14px;
  line-height: 1.55;
  color: $ink-soft;
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
