<template>
  <div class="admin-dashboard">
    <header class="admin-dashboard__intro">
      <span class="admin-dashboard__eyebrow">Overview</span>
      <h1 class="admin-dashboard__title">Indicator panel</h1>
      <p class="admin-dashboard__lede">
        User growth, health metrics, and recent system messages at a glance.
      </p>
    </header>

    <div class="admin-dashboard__grid">
      <div class="admin-dashboard__side">
        <section class="admin-dashboard__card">
          <h2 class="admin-dashboard__card-title">Distribution</h2>
          <PieChart
            fontColor="#24332b"
            bag="#f3faf6"
            :values="pieValues"
            :types="pieTypes"
          />
        </section>

        <section class="admin-dashboard__card">
          <h2 class="admin-dashboard__card-title">Recent messages</h2>
          <div v-if="messageList.length" class="admin-message-list">
            <article
              v-for="(message, idx) in messageList"
              :key="message.id || idx"
              class="admin-message-item"
            >
              <div class="admin-message-item__name">
                {{ message.receiverName }}
              </div>
              <p class="admin-message-item__text">{{ message.content }}</p>
              <time class="admin-message-item__time">{{
                time(message.createTime)
              }}</time>
            </article>
          </div>
          <p v-else class="admin-dashboard__empty">No messages yet.</p>
        </section>
      </div>

      <div class="admin-dashboard__charts">
        <section class="admin-dashboard__card">
          <LineChart
            height="290px"
            tag="Total Users"
            @on-selected="userDatesSelected"
            :values="userValues"
            :date="userDates"
          />
        </section>
        <section class="admin-dashboard__card">
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
.admin-dashboard__intro {
  margin-bottom: 18px;
}

.admin-dashboard__eyebrow {
  display: inline-block;
  margin-bottom: 6px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #2a9d6f;
}

.admin-dashboard__title {
  margin: 0 0 8px;
  font-family: var(--nb-font-display);
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--nb-ink);
}

.admin-dashboard__lede {
  margin: 0;
  font-size: 14px;
  color: var(--nb-muted);
  max-width: 52ch;
}

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
