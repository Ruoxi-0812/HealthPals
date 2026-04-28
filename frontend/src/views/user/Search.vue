<template>
  <el-row>
    <el-row v-if="newsData.length === 0">
      <el-empty description="No related news found"></el-empty>
    </el-row>
    <el-row v-else style="margin-top: 20px">
      <el-col
        class="new-item"
        @click.native="newsItemClick(news)"
        :span="4"
        :key="index"
        v-for="(news, index) in newsData"
      >
        <img :src="news.cover" :alt="news.name" />
        <h3 class="news-title">{{ news.name }}</h3>
        <div style="font-size: 12px">
          <span class="news-tags">{{ news.tagName }}</span>
          <span style="margin-left: 10px"
            >Saved on {{ parseTime(news.createTime) }}</span
          >
        </div>
      </el-col>
    </el-row>
  </el-row>
</template>
<script>
import { timeAgo } from "@/utils/data";
export default {
  data() {
    return {
      keyWord: "",
      newsData: [],
      total: 0,
    };
  },
  created() {
    this.keyWordParse();
    this.keyListener();
  },
  methods: {
    newsItemClick(news) {
      sessionStorage.setItem("newsInfo", JSON.stringify(news));
      this.$router.push("/news-detail");
    },
    parseTime(time) {
      return timeAgo(time);
    },
    nameClick(news) {
      sessionStorage.setItem("newsInfo", JSON.stringify(news));
      this.$router.push(`/news-detail`);
    },
    keyWordParse() {
      this.keyWord = sessionStorage.getItem("keyWord");
      this.fetchData();
    },
    keyListener() {
      setInterval(() => {
        const key = sessionStorage.getItem("keyWord");
        // No change, do nothing
        if (key === this.keyWord) {
          return;
        } else {
          this.keyWord = key;
          this.fetchData();
        }
      }, 100);
    },
    async fetchData() {
      try {
        const newsQueryDto = { name: this.keyWord };
        const response = await this.$axios.post(`/news/query`, newsQueryDto);
        const { data } = response;
        this.newsData = data.data;
        this.total = data.total;
      } catch (e) {
        console.error(`Error loading news list: ${e}`);
      }
    },
  },
};
</script>
<style scoped lang="scss">
.news-tags {
  display: inline-block;
  padding: 2px 5px;
  background-color: rgb(222, 243, 251);
  color: #1d3cc4;
  border-radius: 3px;
}

.new-item:hover {
  background-color: rgb(248, 248, 248);
}

.new-item {
  margin-bottom: 10px;
  padding: 10px;
  box-sizing: border-box;

  img {
    width: 100%;
    height: 118px;
    border-radius: 3px;
  }
}

.news-title {
  overflow: hidden;
  /* Show ellipsis for trimmed text */
  text-overflow: ellipsis;
  /* Prevent text wrapping */
  white-space: nowrap;
}
</style>
