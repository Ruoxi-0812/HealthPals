<template>
  <div class="messages-page">
    <header class="messages-page__top">
      <div class="messages-page__top-inner">
        <Logo sysName="HealthPals" :bag="'#2f4a40'" :size="32" />
        <div class="messages-page__user">
          <el-avatar :size="32" :src="userInfo.userAvatar" />
          <span class="messages-page__user-name">{{ userInfo.userName }}</span>
        </div>
      </div>
    </header>

    <main class="messages-page__main">
      <section class="messages-page__card nb-surface">
        <div class="messages-page__head">
          <div>
            <h1 class="messages-page__title">Inbox</h1>
            <p class="messages-page__subtitle">
              Comments, likes, and gentle reminders—nothing urgent, just for you.
            </p>
          </div>
          <button
            type="button"
            class="messages-page__mark-read"
            @click="clearMessage"
          >
            <i class="el-icon-circle-check" aria-hidden="true" />
            Mark all read
          </button>
        </div>

        <div class="messages-page__tabs" role="tablist">
          <button
            v-for="(messageType, index) in messageTypes"
            :key="index"
            type="button"
            role="tab"
            class="messages-page__tab"
            :class="{ 'is-active': activeFilter === messageType.type }"
            :aria-selected="activeFilter === messageType.type"
            @click="messageTypeSelected(messageType.type)"
          >
            {{ typeLabel(messageType) }}
          </button>
        </div>

        <div class="messages-page__body">
          <div v-if="messageList.length === 0" class="messages-page__empty">
            <el-empty description="">
              <template slot="image">
                <div class="messages-page__empty-art" aria-hidden="true">
                  <i class="el-icon-message" />
                </div>
              </template>
              <template slot="description">
                <p class="messages-page__empty-title">All quiet here</p>
                <p class="messages-page__empty-text">
                  When someone comments, likes your post, or the app has a health
                  tip, it’ll land in this inbox.
                </p>
              </template>
            </el-empty>
          </div>

          <ul v-else class="messages-page__list">
            <li
              v-for="(message, index) in messageList"
              :key="message.id || index"
              class="messages-page__item"
              :class="{ 'is-unread': !message.isRead }"
            >
              <div class="messages-page__item-avatar-wrap">
                <img
                  v-if="message.messageType === 1 || message.messageType === 2"
                  class="messages-page__item-avatar"
                  :src="message.senderAvatar"
                  alt=""
                />
                <span
                  v-else-if="message.messageType === 3"
                  class="messages-page__item-badge messages-page__item-badge--metric"
                  aria-hidden="true"
                >
                  <i class="el-icon-data-analysis" />
                </span>
                <span
                  v-else
                  class="messages-page__item-badge messages-page__item-badge--system"
                  aria-hidden="true"
                >
                  <i class="el-icon-bell" />
                </span>
              </div>
              <div class="messages-page__item-body">
                <div
                  v-if="message.messageType === 1 || message.messageType === 2"
                  class="messages-page__item-sender"
                >
                  {{ message.senderName }}
                </div>
                <p class="messages-page__item-text">
                  <template v-if="message.messageType === 1">
                    {{ commentDeal(message.content)[2] }}
                  </template>
                  <template v-else>{{ message.content }}</template>
                </p>
                <div class="messages-page__item-meta">
                  <time class="messages-page__time">{{ message.createTime }}</time>
                  <button
                    v-if="message.messageType === 1"
                    type="button"
                    class="messages-page__reply"
                    @click="replyUser(message)"
                  >
                    Reply
                  </button>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </section>

      <p class="messages-page__home">
        <router-link class="messages-page__home-link" to="/news-record">
          ← Back to Home
        </router-link>
      </p>
    </main>
  </div>
</template>

<script>
import Logo from "@/components/Logo";
import Swal from "sweetalert2";

export default {
  name: "MessageCenter",
  components: { Logo },
  data() {
    return {
      userInfo: {},
      messageQueryDto: {},
      messageList: [],
      messageTypes: [],
      dialogEvaluationsOperation: false,
      message: {},
      activeFilter: null,
    };
  },
  created() {
    this.getUserInfo();
    this.loadAllUsersMessage();
    this.loadAllMessageType();
  },
  methods: {
    typeLabel(messageType) {
      const raw = (messageType.detail || "").trim();
      const lower = raw.toLowerCase();
      if (messageType.type === null || lower === "all messages") {
        return "All";
      }
      if (lower === "comment") return "Comments";
      if (lower === "like") return "Likes";
      if (lower === "indicator reminder") return "Health tips";
      if (lower === "systematic notification") return "System";
      if (!raw) return "All";
      return raw.charAt(0).toUpperCase() + raw.slice(1);
    },
    commentDeal(content) {
      return content.split(";");
    },
    replyUser(message) {
      Swal.fire({
        title: `Reply to ${message.senderName}`,
        input: "text",
        inputPlaceholder: "Say something friendly…",
        showCancelButton: true,
        confirmButtonText: "Send",
        cancelButtonText: "Cancel",
        inputValidator: (value) => {
          if (!value) {
            return "Type a short reply first";
          }
          this.saveCommentData(
            message.senderId,
            value,
            this.commentDeal(message.content),
          );
        },
      }).then(() => {});
    },
    saveCommentData(senderId, content, ary) {
      const comment = {
        content,
        parentId: ary[0],
        contentType: "NEWS",
        contentId: ary[1],
        replierId: senderId,
      };
      this.$axios
        .post("/evaluations/insert", comment)
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.$notify({
              title: "Sent",
              message: "Your reply is live.",
              type: "success",
            });
          }
        })
        .catch((error) => {
          console.log("Reply error:", error);
        });
    },
    async clearMessage() {
      const confirmed = await this.$swalConfirm({
        title: "Mark everything read?",
        text: "Unread badges will clear. You can still scroll history.",
        icon: "warning",
      });
      if (confirmed) {
        this.$axios.put("/message/clearMessage").then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.loadAllUsersMessage();
          }
        });
      }
    },
    evaluationsPut() {
      this.$axios.put("/message/clearMessage").then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.loadAllUsersMessage();
        }
      });
    },
    replyEvalustions(message) {
      this.message = message;
      this.dialogEvaluationsOperation = true;
    },
    messageTypeSelected(messageType) {
      this.activeFilter = messageType;
      this.messageQueryDto.messageType = messageType;
      this.loadAllUsersMessage();
    },
    getUserInfo() {
      const userInfo = sessionStorage.getItem("userInfo");
      this.userInfo = JSON.parse(userInfo);
    },
    loadAllMessageType() {
      this.$axios.get("/message/types").then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.messageTypes = data.data;
          const messageType = { type: null, detail: "All Messages" };
          this.messageTypes.unshift(messageType);
          this.activeFilter = null;
        }
      });
    },
    loadAllUsersMessage() {
      const userInfo = sessionStorage.getItem("userInfo");
      const entity = JSON.parse(userInfo);
      this.messageQueryDto.userId = entity.id;
      this.$axios.post("/message/query", this.messageQueryDto).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.messageList = data.data;
        }
      });
    },
  },
};
</script>

<style scoped lang="scss">
.messages-page {
  min-height: 100vh;
  box-sizing: border-box;
  background: var(--nb-bg-soft, #e7f6ee);
}

.messages-page__top {
  background: #fff;
  border-bottom: 1px solid rgba(126, 197, 160, 0.22);
  box-shadow: 0 4px 14px rgba(53, 92, 75, 0.06);
}

.messages-page__top-inner {
  max-width: 880px;
  margin: 0 auto;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.messages-page__user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.messages-page__user-name {
  font-size: 14px;
  font-weight: 650;
  color: var(--nb-ink, #24332b);
}

.messages-page__main {
  max-width: 880px;
  margin: 0 auto;
  padding: clamp(16px, 3vw, 28px) 18px 48px;
}

.messages-page__card {
  padding: clamp(18px, 2.5vw, 26px);
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(126, 197, 160, 0.22);
}

.messages-page__head {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 18px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.18);
}

.messages-page__title {
  margin: 0 0 6px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.55rem;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--nb-ink, #24332b);
}

.messages-page__subtitle {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--nb-muted, rgba(36, 51, 43, 0.58));
  max-width: 46ch;
}

.messages-page__mark-read {
  appearance: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  font: inherit;
  font-size: 13px;
  font-weight: 650;
  color: #355247;
  background: rgba(42, 157, 111, 0.1);
  border: 1px solid rgba(42, 157, 111, 0.32);
  border-radius: 999px;
  transition:
    background 0.15s ease,
    color 0.15s ease;

  &:hover {
    background: rgba(42, 157, 111, 0.18);
    color: #1f3d32;
  }
}

.messages-page__tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 22px;
}

.messages-page__tab {
  appearance: none;
  cursor: pointer;
  border: 1px solid rgba(126, 197, 160, 0.35);
  background: rgba(255, 255, 255, 0.7);
  padding: 8px 16px;
  border-radius: 999px;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  color: #5a7268;
  transition:
    background 0.15s ease,
    border-color 0.15s ease,
    color 0.15s ease;

  &:hover {
    border-color: rgba(42, 157, 111, 0.45);
    color: #2a6b52;
  }

  &.is-active {
    background: #2a9d6f;
    border-color: #2a9d6f;
    color: #fff;
    box-shadow: 0 2px 10px rgba(42, 157, 111, 0.28);
  }
}

.messages-page__empty {
  padding: 36px 16px 28px;

  :deep(.el-empty__description) {
    padding-top: 4px;
  }
}

.messages-page__empty-art {
  width: 76px;
  height: 76px;
  margin: 0 auto 8px;
  border-radius: 20px;
  background: rgba(126, 197, 160, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  color: #4d8b73;
}

.messages-page__empty-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 650;
  color: var(--nb-ink, #24332b);
}

.messages-page__empty-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  color: var(--nb-muted, rgba(36, 51, 43, 0.58));
  max-width: 38ch;
  margin-left: auto;
  margin-right: auto;
}

.messages-page__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.messages-page__item {
  display: flex;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 16px;
  border: 1px solid rgba(126, 197, 160, 0.22);
  background: rgba(247, 251, 248, 0.65);
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease;

  &.is-unread {
    border-color: rgba(42, 157, 111, 0.4);
    background: rgba(42, 157, 111, 0.06);
    box-shadow: inset 3px 0 0 #2a9d6f;
  }
}

.messages-page__item-avatar-wrap {
  flex-shrink: 0;
}

.messages-page__item-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(53, 92, 75, 0.12);
}

.messages-page__item-badge {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;

  &--metric {
    background: linear-gradient(145deg, #6eb8f2, #4a90c9);
  }

  &--system {
    background: linear-gradient(145deg, #8b9dc3, #5c6d8a);
  }
}

.messages-page__item-body {
  min-width: 0;
  flex: 1;
}

.messages-page__item-sender {
  font-size: 14px;
  font-weight: 700;
  color: #355247;
  margin-bottom: 4px;
}

.messages-page__item-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  color: rgba(36, 51, 43, 0.88);
  word-break: break-word;
}

.messages-page__item-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.messages-page__time {
  font-size: 12px;
  color: rgba(53, 82, 71, 0.58);
  font-weight: 500;
}

.messages-page__reply {
  appearance: none;
  border: none;
  background: none;
  padding: 0;
  font: inherit;
  font-size: 12px;
  font-weight: 650;
  color: #2a9d6f;
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 2px;

  &:hover {
    color: #248760;
  }
}

.messages-page__home {
  margin: 20px 0 0;
  text-align: center;
}

.messages-page__home-link {
  font-size: 14px;
  font-weight: 600;
  color: #355247;
  text-decoration: none;

  &:hover {
    color: #2a9d6f;
    text-decoration: underline;
  }
}
</style>
