<template>
  <div class="home-page">
    <div class="home-inner">
      <!-- Hero：全宽轮播 + 下方精选（无左右分栏，避免一侧留白） -->
      <section class="home-hero" v-if="newsTopList.length">
        <div class="hero-banner-wide">
          <Banner
            :data="newsTopList"
            @on-click="onBannerClick"
            border-radius="16px"
          />
        </div>
        <div class="hero-featured-block">
          <header class="section-head section-head--featured">
            <span class="eyebrow">Featured</span>
            <h2 class="heading">Curated for your wellbeing</h2>
            <p class="lede">
              Hand-picked stories and practical tips from our editors.
            </p>
          </header>
          <div
            class="featured-grid-fluid"
            :class="'featured-cols--' + featuredColsModifier"
          >
            <article
              v-for="(news, index) in displayTopNews"
              :key="'top-' + index"
              class="featured-card"
              @click="newsItemClick(news)"
            >
              <div class="featured-card__media">
                <img
                  :src="news.cover"
                  :alt="news.name"
                  class="featured-card__img"
                />
              </div>
              <div class="featured-card__body">
                <h3 class="featured-card__title">{{ news.name }}</h3>
                <div class="featured-card__meta">
                  <span class="pill">{{ news.tagName }}</span>
                  <time>{{ parseTime(news.createTime) }}</time>
                </div>
              </div>
            </article>
          </div>
        </div>
      </section>

      <section class="home-hero hero--empty" v-else>
        <div class="empty-hero">
          <el-empty description="No featured articles yet" :image-size="120" />
        </div>
      </section>

      <!-- Categories -->
      <section class="home-tags">
        <header class="section-head section-head--compact">
          <span class="eyebrow">Topics</span>
          <h2 class="heading">Browse by category</h2>
        </header>
        <div class="tags-surface">
          <TagLine :dataList="tagsList" @on-click="tagOnClick" />
        </div>
      </section>

      <!-- Feed -->
      <section class="home-feed">
        <header class="section-head section-head--compact feed-head">
          <div>
            <span class="eyebrow">Library</span>
            <h2 class="heading">Latest articles</h2>
            <p class="lede lede--muted">
              Updated health news and guides. Click a card to read more.
            </p>
          </div>
        </header>

        <el-row v-if="newsList.length === 0" class="feed-empty">
          <el-empty description="No news available" />
        </el-row>

        <div v-else class="feed-masonry">
          <article
            v-for="(news, index) in newsList"
            :key="'news-' + index"
            class="feed-card"
            @click="newsItemClick(news)"
          >
            <div class="feed-card__media">
              <img
                :src="news.cover"
                :alt="news.name"
                class="feed-card__img"
              />
            </div>
            <div class="feed-card__body">
              <h3 class="feed-card__title">{{ news.name }}</h3>
              <div class="feed-card__meta">
                <span class="pill pill--ghost">{{ news.tagName }}</span>
                <time>{{ parseTime(news.createTime) }}</time>
              </div>
            </div>
          </article>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import TagLine from "@/components/TagLine";
import Banner from "@/components/Banner";
import { timeAgo } from "@/utils/data";

export default {
  components: { TagLine, Banner },
  data() {
    return {
      tagsList: [],
      newsList: [],
      newsTopList: [],
      newQueryDto: { tagId: null },
    };
  },
  computed: {
    displayTopNews() {
      return (this.newsTopList || []).slice(0, 6);
    },
    featuredColsModifier() {
      const n = this.displayTopNews.length;
      return Math.min(Math.max(n, 1), 6);
    },
  },
  created() {
    this.loadAllTags();
    this.loadAllNews();
    this.loadAllTopNews();
  },
  methods: {
    onBannerClick(banner) {
      sessionStorage.setItem("newsInfo", JSON.stringify(banner));
      this.$router.push("/news-detail");
    },
    newsItemClick(news) {
      sessionStorage.setItem("newsInfo", JSON.stringify(news));
      this.$router.push("/news-detail");
    },
    parseTime(time) {
      return timeAgo(time);
    },
    tagOnClick(tags) {
      this.newQueryDto.tagId = tags.id;
      this.loadAllNews();
    },
    loadAllTags() {
      this.$axios.post("/tags/query", {}).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.tagsList = data.data;
          this.tagsList.unshift({ name: "All", id: null });
        }
      });
    },
    loadAllTopNews() {
      const newQueryDto = { isTop: true };
      this.$axios.post("/news/query", newQueryDto).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.newsTopList = data.data;
        }
      });
    },
    loadAllNews() {
      this.$axios.post("/news/query", this.newQueryDto).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.newsList = data.data;
        }
      });
    },
  },
};
</script>

<style scoped lang="scss">
$ink: #1e2f28;
$ink-soft: #4a6358;
$mint: #e8f4ee;
$accent: #3d8b6a;
$accent-soft: rgba(61, 139, 106, 0.12);
$radius-lg: 20px;
$radius-md: 14px;
$shadow: 0 12px 32px rgba(30, 47, 40, 0.08);
$shadow-hover: 0 20px 40px rgba(30, 47, 40, 0.12);

.home-page {
  min-height: 100%;
  width: 100%;
  box-sizing: border-box;
  background: linear-gradient(
    180deg,
    rgba(232, 244, 238, 0.45) 0%,
    rgba(255, 255, 255, 0.94) 14%,
    #f6faf8 100%
  );
  padding: 4px 0 40px;
}

.home-inner {
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.section-head {
  margin-bottom: 16px;

  &--compact {
    margin-bottom: 12px;
  }

  &--tight {
    margin-bottom: 12px;

    .lede {
      max-width: 42ch;
    }
  }

  &--featured {
    text-align: center;
    margin-bottom: 22px;

    .lede {
      max-width: 40ch;
      margin-left: auto;
      margin-right: auto;
    }
  }
}

.eyebrow {
  display: inline-block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: $accent;
  margin-bottom: 6px;
}

.heading {
  margin: 0 0 8px;
  font-size: clamp(1.35rem, 2.2vw, 1.65rem);
  font-weight: 700;
  color: $ink;
  letter-spacing: -0.02em;
  line-height: 1.25;
}

.lede {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  color: $ink-soft;
  max-width: 36ch;

  &--muted {
    opacity: 0.92;
  }
}

/* Hero：全宽横图 + 下方卡片，无侧栏空白 */
.home-hero {
  margin-bottom: 22px;
}

.hero-banner-wide {
  width: 100%;
  box-sizing: border-box;
  margin-bottom: 28px;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: $shadow;
  aspect-ratio: 2.2 / 1;
  max-height: min(380px, 42vw);

  @media (max-width: 768px) {
    aspect-ratio: 16 / 10;
    max-height: 260px;
    margin-bottom: 22px;
  }

  ::v-deep .banner-wrap {
    width: 100%;
    height: 100%;
    min-height: 0;
    max-height: none !important;
    box-shadow: none;
    display: flex;
    flex-direction: column;
  }

  ::v-deep .banner-image {
    flex: 1 1 auto;
    width: 100% !important;
    min-height: 0 !important;
    max-height: none !important;
    height: 100% !important;
    object-fit: cover;
    object-position: center center;
  }

  ::v-deep .tip-name {
    width: calc(100% - 16px);
    left: 8px;
    bottom: 8px;
    padding: 12px 16px 14px;
  }

  ::v-deep .banner-title {
    font-size: clamp(1.08rem, 2.5vw + 0.55rem, 2rem);
  }
}

.hero-featured-block {
  width: 100%;
  min-width: 0;
}

.featured-grid-fluid {
  width: 100%;
  box-sizing: border-box;
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(min(100%, 260px), 1fr));
}

@media (min-width: 992px) {
  .featured-grid-fluid.featured-cols--1 {
    grid-template-columns: minmax(0, 1fr);
    max-width: 480px;
    margin: 0 auto;
  }

  .featured-grid-fluid.featured-cols--2 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .featured-grid-fluid.featured-cols--3 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .featured-grid-fluid.featured-cols--4 {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .featured-grid-fluid.featured-cols--5,
  .featured-grid-fluid.featured-cols--6 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 991px) and (min-width: 600px) {
  .featured-grid-fluid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 599px) {
  .featured-grid-fluid {
    grid-template-columns: minmax(0, 1fr);
  }
}

.hero--empty {
  margin-bottom: 20px;
}

.empty-hero {
  background: rgba(255, 255, 255, 0.72);
  border-radius: $radius-lg;
  border: 1px solid rgba(126, 197, 160, 0.22);
  padding: 24px;
  box-shadow: $shadow;
}

.featured-card {
  height: 100%;
  background: #fff;
  border-radius: $radius-md;
  border: 1px solid rgba(126, 197, 160, 0.22);
  box-shadow: $shadow;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease,
    border-color 0.22s ease;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-hover;
    border-color: rgba(61, 139, 106, 0.35);
  }

  &__media {
    position: relative;
    aspect-ratio: 3 / 2;
    overflow: hidden;
    background: $mint;
  }

  &__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 0.35s ease;
  }

  &:hover &__img {
    transform: scale(1.04);
  }

  &__body {
    padding: 14px 14px 16px;
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  &__title {
    margin: 0;
    font-size: 14px;
    font-weight: 600;
    color: $ink;
    line-height: 1.35;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  &__meta {
    margin-top: auto;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: $ink-soft;

    time {
      opacity: 0.85;
    }
  }
}

/* Tags */
.home-tags {
  margin-bottom: 26px;
}

.tags-surface {
  background: rgba(255, 255, 255, 0.82);
  border-radius: $radius-lg;
  border: 1px solid rgba(126, 197, 160, 0.2);
  box-shadow: $shadow;
  padding: 6px 16px 10px;
}

/* Feed */
.home-feed {
  padding-bottom: 8px;
}

.feed-head {
  align-items: flex-end;
}

.feed-empty {
  padding: 48px 0;
  background: rgba(255, 255, 255, 0.65);
  border-radius: $radius-lg;
  border: 1px dashed rgba(126, 197, 160, 0.35);
}

.feed-masonry {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
  margin-top: 6px;
}

.feed-card {
  height: 100%;
  margin-bottom: 0;
  background: #fff;
  border-radius: $radius-md;
  border: 1px solid rgba(126, 197, 160, 0.2);
  box-shadow: $shadow;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-3px);
    box-shadow: $shadow-hover;
  }

  &__media {
    aspect-ratio: 16 / 10;
    overflow: hidden;
    background: $mint;
  }

  &__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  &__body {
    padding: 16px 16px 18px;
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  &__title {
    margin: 0;
    font-size: 15px;
    font-weight: 600;
    color: $ink;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  &__meta {
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
</style>
