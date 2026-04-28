<template>
  <el-row>
    <el-row v-if="newsSaveList.length === 0">
      <el-empty description="No saved news yet"></el-empty>
    </el-row>
    <el-row style="margin-top: 20px">
      <el-col
        @click.native="newsItemClick(news)"
        :span="4"
        :key="index"
        v-for="(news, index) in newsSaveList"
      >
        <div style="padding: 0 10px">
          <img
            :src="news.cover"
            :alt="news.name"
            style="width: 100%; height: 118px; border-radius: 5px"
          />
          <h3 class="news-title">{{ news.name }}</h3>
          <div style="font-size: 12px">
            <span class="news-tags">{{ news.tagName }}</span>
            <span style="margin-left: 10px"
              >Saved on {{ parseTime(news.createTime) }}</span
            >
          </div>
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
      newsSaveList: [],
    };
  },
  created() {
    this.loadAllSaveNews();
  },
  methods: {
    parseTime(time) {
      return timeAgo(time);
    },
    newsItemClick(newsSave) {
      // Assemble news data
      const news = {
        id: newsSave.newsId,
        name: newsSave.name,
        content: newsSave.content,
        createTime: newsSave.createTime,
        tagName: newsSave.tagName,
      };
      sessionStorage.setItem("newsInfo", JSON.stringify(news));
      this.$router.push("/news-detail");
    },
    loadAllSaveNews() {
      // Query conditions, including user ID
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
.news-tags {
  display: inline-block;
  padding: 2px 5px;
  background-color: rgb(222, 243, 251);
  color: #1d3cc4;
  border-radius: 3px;
}

.news-title {
  overflow: hidden;
  /* Displays ellipsis for clipped text */
  text-overflow: ellipsis;
  /* Prevents text wrapping */
  white-space: nowrap;
}
</style>
