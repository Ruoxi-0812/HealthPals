<template>
  <div class="admin-page admin-page--dashboard">
    <div class="admin-page__body admin-page__body--dashboard">
      <div class="admin-dashboard__grid">
      <div class="admin-dashboard__side">
        <section class="admin-dashboard__card admin-dashboard__card--chart">
          <h2 class="admin-dashboard__card-title">Distribution</h2>
          <PieChart
            height="240px"
            fontColor="#24332b"
            bag="transparent"
            :values="pieValues"
            :types="pieTypes"
          />
        </section>

        <section class="admin-dashboard__card admin-dashboard__card--messages">
          <div class="admin-message-card__header">
            <h2 class="admin-dashboard__card-title">Recent messages</h2>
            <span v-if="messageList.length" class="admin-message-card__count">
              {{ messageList.length }}
            </span>
          </div>
          <div v-if="messageList.length" class="admin-message-list">
            <article
              v-for="(message, idx) in messageList"
              :key="message.id || idx"
              class="admin-message-item"
              :class="messageItemClass(message)"
            >
              <span
                class="admin-message-item__icon"
                :class="'admin-message-item__icon--type-' + message.messageType"
                aria-hidden="true"
              >
                <i :class="messageIcon(message.messageType)" />
              </span>
              <div class="admin-message-item__body">
                <div class="admin-message-item__meta">
                  <span class="admin-message-item__name">{{
                    message.receiverName
                  }}</span>
                  <span
                    class="admin-message-item__chip"
                    :class="'admin-message-item__chip--type-' + message.messageType"
                  >
                    {{ typeLabel(message.messageType) }}
                  </span>
                  <time
                    class="admin-message-item__time"
                    :datetime="message.createTime"
                    :title="message.createTime"
                  >
                    {{ time(message.createTime) }}
                  </time>
                </div>
                <p
                  v-if="message.messageType === 3 && healthAlertParts(message)"
                  class="admin-message-item__text admin-message-item__text--alert"
                >
                  <span>{{ healthAlertParts(message).lead }} </span>
                  <strong class="admin-message-item__metric">{{
                    healthAlertParts(message).metric
                  }}</strong>
                  <span> {{ healthAlertParts(message).tail }}</span>
                </p>
                <p v-else class="admin-message-item__text">
                  {{ message.content }}
                </p>
              </div>
            </article>
          </div>
          <p v-else class="admin-dashboard__empty">No messages yet.</p>
        </section>
      </div>

      <div class="admin-dashboard__charts">
        <section class="admin-dashboard__card admin-dashboard__card--chart">
          <LineChart
            height="290px"
            tag="Total Users"
            @on-selected="userDatesSelected"
            :values="userValues"
            :date="userDates"
          />
        </section>
        <section class="admin-dashboard__card admin-dashboard__card--chart">
          <LineChart
            height="290px"
            tag="Health Metrics"
            @on-selected="modelDatesSelected"
            :values="modelValues"
            :date="modelDates"
          />
        </section>
      </div>
    </div>
    </div>
  </div>
</template>

<script>
import LineChart from "@/components/LineChart";
import PieChart from "@/components/PieChart";
import { timeAgo } from "@/utils/data";

export default {
  components: { LineChart, PieChart },
  data() {
    return {
      userValues: [],
      userDates: [],
      modelDates: [],
      modelValues: [],
      pieValues: [],
      pieTypes: [],
      messageList: [],
    };
  },
  created() {
    this.userDatesSelected(365);
    this.modelDatesSelected(365);
    this.loadPieCharts();
    this.loadMessages();
  },
  methods: {
    time(createTime) {
      return timeAgo(createTime);
    },
    typeLabel(messageType) {
      const map = {
        1: "Comment",
        2: "Like",
        3: "Health alert",
        4: "System",
      };
      return map[messageType] || "Message";
    },
    messageIcon(messageType) {
      const map = {
        1: "el-icon-chat-line-round",
        2: "el-icon-star-on",
        3: "el-icon-warning-outline",
        4: "el-icon-bell",
      };
      return map[messageType] || "el-icon-message";
    },
    messageItemClass(message) {
      return {
        "admin-message-item--unread": !message.isRead,
        "admin-message-item--alert": message.messageType === 3,
      };
    },
    healthAlertParts(message) {
      const text = (message.content || "").trim();
      if (!text) return null;

      const bracket = text.match(/【([^】]+)】/);
      if (bracket) {
        const idx = text.indexOf(bracket[0]);
        return {
          lead: text.slice(0, idx).trim() || "Recorded",
          metric: bracket[1],
          tail: text.slice(idx + bracket[0].length).trim(),
        };
      }

      const reading = text.match(/^Your\s+(.+?)\s+reading\s+\(([^)]+)\)/i);
      if (reading) {
        return {
          lead: "Your",
          metric: `${reading[1]} · ${reading[2]}`,
          tail: text.slice(reading[0].length).trim(),
        };
      }

      return null;
    },
    loadMessages() {
      const messageQueryDto = {
        current: 1,
        size: 4,
      };
      this.$axios.post(`/message/query`, messageQueryDto).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.messageList = data.data || [];
        }
      });
    },
    loadPieCharts() {
      this.$axios.get(`/views/staticControls`).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.pieValues = data.data.map((entity) => entity.count);
          this.pieTypes = data.data.map((entity) => entity.name);
        }
      });
    },
    modelDatesSelected(time) {
      this.$axios.get(`/user-health/daysQuery/${time}`).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.modelValues = data.data.map((entity) => entity.count);
          this.modelDates = data.data.map((entity) => entity.name);
        }
      });
    },
    userDatesSelected(time) {
      this.$axios.get(`/user/daysQuery/${time}`).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.userValues = data.data.map((entity) => entity.count);
          this.userDates = data.data.map((entity) => entity.name);
        }
      });
    },
  },
};
</script>

<style scoped lang="scss">
.admin-dashboard__side {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.admin-dashboard__empty {
  margin: 0;
  font-size: 14px;
  color: var(--nb-muted);
}
</style>
