<template>
  <div class="news-detail">
    <el-row :gutter="28" class="news-detail__row">
      <el-col :xs="24" :md="16" class="news-detail__main-wrap">
        <article class="news-detail__article nb-surface">
          <h1 class="news-detail__title">{{ newsInfo.name }}</h1>

          <div class="news-detail__meta">
            <span class="news-detail__tag">{{ newsInfo.tagName }}</span>
            <time class="news-detail__time" :datetime="newsInfo.createTime">{{
              parseTime(newsInfo.createTime)
            }}</time>
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
            class="news-detail__body"
            v-html="newsInfo.content"
          />

          <div class="news-detail__comments">
            <Evaluations :contentId="newsInfo.id" content-type="NEWS" />
          </div>
        </article>
      </el-col>

      <el-col :xs="24" :md="8" class="news-detail__aside-wrap">
        <aside class="news-detail__aside">
          <h2 class="news-detail__aside-title">Recommended</h2>
          <p class="news-detail__aside-sub">
            More from our editors—tap a card to read.
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
                  :src="news.cover"
                  :alt="news.name"
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
  created() {
    this.getStorageInfo();
    this.loadAllTopNews();
  },
  methods: {
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
      this.newsInfo = news;
      sessionStorage.setItem("newsInfo", JSON.stringify(news));
      this.loadSaveStatus();
      this.loadAllTopNews();
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
      if (this.newsInfo && this.newsInfo.id != null) {
        this.loadSaveStatus();
      }
    },
    loadAllTopNews() {
      const newQueryDto = { isTop: true };
      this.$axios.post("/news/query", newQueryDto).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          const currentId = this.newsInfo && this.newsInfo.id;
          const list = (data.data || []).filter((n) => n.id !== currentId);
          this.newsTopList = list.slice(0, 6);
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

.news-detail__row {
  width: 100%;
}

.news-detail__article {
  padding: clamp(22px, 3vw, 36px);
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(126, 197, 160, 0.22);
  margin-bottom: 24px;
}

.news-detail__title {
  margin: 0 0 18px;
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
  padding: 14px 0 22px;
  margin-bottom: 8px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.2);
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

.news-detail__save {
  margin-left: auto;
  appearance: none;
  cursor: pointer;
  padding: 8px 18px;
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
.news-detail__body {
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
    border-radius: 12px;
    margin: 1em 0;
  }
}

.news-detail__comments {
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid rgba(126, 197, 160, 0.2);
}

.news-detail__aside {
  position: sticky;
  top: 88px;
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
  margin: 0 0 18px;
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

  .news-detail__save {
    margin-left: 0;
    width: 100%;
    margin-top: 4px;
  }

  .news-detail__meta {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
