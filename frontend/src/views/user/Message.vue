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
          <div class="messages-page__head-copy">
            <span class="messages-page__eyebrow">Notifications</span>
            <div class="messages-page__title-row">
              <h1 class="messages-page__title">Inbox</h1>
              <span
                v-if="unreadCount > 0"
                class="messages-page__unread-pill"
                :aria-label="`${unreadCount} unread`"
              >
                {{ unreadCount }} new
              </span>
            </div>
            <p class="messages-page__subtitle">
              Comments, likes, and gentle reminders—nothing urgent, just for you.
            </p>
            <p v-if="messageList.length" class="messages-page__stats">
              {{ messageList.length }}
              {{ messageList.length === 1 ? "message" : "messages" }}
              <span v-if="unreadCount" class="messages-page__stats-dot">·</span>
              <span v-if="unreadCount">{{ unreadCount }} unread</span>
            </p>
          </div>
          <button
            type="button"
            class="messages-page__mark-read"
            :disabled="!unreadCount"
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
            <span
              v-if="tabCount(messageType.type)"
              class="messages-page__tab-count"
            >
              {{ tabCount(messageType.type) }}
            </span>
          </button>
        </div>

        <div class="messages-page__body">
          <div
            v-if="filteredMessageList.length === 0"
            class="messages-page__empty"
          >
            <div class="messages-page__empty-art" aria-hidden="true">
              <i class="el-icon-message" />
            </div>
            <p class="messages-page__empty-title">All quiet here</p>
            <p class="messages-page__empty-text">
              When someone comments, likes your post, or the app has a health tip,
              it’ll land in this inbox.
            </p>
          </div>

          <ul v-else class="messages-page__list">
            <li
              v-for="(message, index) in filteredMessageList"
              :key="message.id || index"
              class="messages-page__item"
              :class="itemClass(message)"
            >
              <span
                v-if="!message.isRead"
                class="messages-page__item-dot"
                aria-hidden="true"
              />

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
                  <i class="el-icon-warning-outline" />
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
                <div class="messages-page__item-top">
                  <span
                    class="messages-page__type-chip"
                    :class="'messages-page__type-chip--' + message.messageType"
                  >
                    {{ typeChipLabel(message.messageType) }}
                  </span>
                  <time
                    class="messages-page__time"
                    :datetime="message.createTime"
                    :title="message.createTime"
                  >
                    {{ parseTime(message.createTime) }}
                  </time>
                </div>

                <div
                  v-if="message.messageType === 1 || message.messageType === 2"
                  class="messages-page__item-sender"
                >
                  {{ message.senderName }}
                </div>

                <p
                  v-if="message.messageType === 3 && healthAlertParts(message)"
                  class="messages-page__item-text messages-page__item-text--alert"
                >
                  <span class="messages-page__alert-lead">
                    {{ healthAlertParts(message).lead }}
                  </span>
                  <strong class="messages-page__metric-name">
                    {{ healthAlertParts(message).metric }}
                  </strong>
                  <span>{{ healthAlertParts(message).tail }}</span>
                </p>
                <p v-else class="messages-page__item-text">
                  <template v-if="message.messageType === 1">
                    {{ commentDeal(message.content)[2] }}
                  </template>
                  <template v-else-if="message.messageType === 2">
                    <i class="el-icon-star-on messages-page__like-icon" />
                    {{ message.content }}
                  </template>
                  <template v-else>{{ message.content }}</template>
                </p>

                <div class="messages-page__item-meta">
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
import { timeAgo } from "@/utils/data";

export default {
  name: "MessageCenter",
  components: { Logo },
  data() {
    return {
      userInfo: {},
      messageQueryDto: {},
      messageList: [],
      messageTypes: [],
      message: {},
      activeFilter: null,
    };
  },
  computed: {
    filteredMessageList() {
      if (this.activeFilter == null) {
        return this.messageList;
      }
      return this.messageList.filter(
        (m) => m.messageType === this.activeFilter,
      );
    },
    unreadCount() {
      return this.messageList.filter((m) => !m.isRead).length;
    },
  },
  created() {
    this.getUserInfo();
    this.loadAllUsersMessage();
    this.loadAllMessageType();
  },
  activated() {
    this.loadAllUsersMessage();
  },
  methods: {
    parseTime(time) {
      return timeAgo(time);
    },
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
    typeChipLabel(messageType) {
      const map = {
        1: "Comment",
        2: "Like",
        3: "Health alert",
        4: "System",
      };
      return map[messageType] || "Message";
    },
    tabCount(type) {
      if (type == null) {
        return this.messageList.length;
      }
      return this.messageList.filter((m) => m.messageType === type).length;
    },
    itemClass(message) {
      return {
        "is-unread": !message.isRead,
        [`is-type-${message.messageType}`]: true,
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

      const reading = text.match(
        /^Your\s+(.+?)\s+reading\s+\(([^)]+)\)/i,
      );
      if (reading) {
        return {
          lead: "Your",
          metric: `${reading[1]} · ${reading[2]}`,
          tail: text.slice(reading[0].length).trim(),
        };
      }

      return null;
    },
    commentDeal(content) {
      return content.split(";");
    },
    replyUser(message) {
      this.$swal
        .fire({
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
            return undefined;
          },
        })
        .then((result) => {
          if (result.isConfirmed && result.value) {
            this.saveCommentData(
              message.senderId,
              result.value,
              this.commentDeal(message.content),
            );
          }
        });
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
      if (!this.unreadCount) return;
      const confirmed = await this.$swalConfirm({
        title: "Mark everything read?",
        text: "Unread badges will clear. You can still scroll history.",
        icon: "question",
        confirmButtonText: "Mark all read",
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
    messageTypeSelected(messageType) {
      this.activeFilter = messageType;
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
      const query = { userId: entity.id };
      this.$axios.post("/message/query", query).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.messageList = data.data || [];
        }
      });
    },
  },
};
</script>

<style scoped lang="scss">
$ink: var(--nb-ink, #24332b);
$mint: rgba(126, 197, 160, 0.22);
$accent: #2a9d6f;
$warn: #d97706;
$warn-bg: #fff8ed;
$warn-border: rgba(217, 119, 6, 0.35);

.messages-page {
  min-height: 100vh;
  box-sizing: border-box;
  background:
    radial-gradient(ellipse 80% 50% at 50% -10%, rgba(126, 197, 160, 0.28), transparent),
    var(--nb-bg-soft, #e7f6ee);
}

.messages-page__top {
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid $mint;
  box-shadow: 0 4px 18px rgba(53, 92, 75, 0.07);
  backdrop-filter: blur(8px);
}

.messages-page__top-inner {
  max-width: 720px;
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
  color: $ink;
}

.messages-page__main {
  max-width: 720px;
  margin: 0 auto;
  padding: clamp(20px, 4vw, 32px) 18px 52px;
}

.messages-page__card {
  padding: clamp(22px, 3vw, 30px);
  background: rgba(255, 255, 255, 0.97);
  border: 1px solid $mint;
  border-radius: var(--nb-radius, 22px);
  box-shadow: var(--nb-shadow, 0 14px 36px rgba(53, 92, 75, 0.14));
}

.messages-page__head {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 22px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.2);
}

.messages-page__eyebrow {
  display: inline-block;
  margin-bottom: 8px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: $accent;
}

.messages-page__title-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.messages-page__title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: clamp(1.45rem, 3vw, 1.75rem);
  font-weight: 600;
  letter-spacing: -0.03em;
  color: $ink;
}

.messages-page__unread-pill {
  display: inline-flex;
  align-items: center;
  padding: 4px 11px;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #3cb07e, $accent);
  border-radius: 999px;
  box-shadow: 0 2px 10px rgba(42, 157, 111, 0.35);
}

.messages-page__subtitle {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  color: var(--nb-muted, rgba(36, 51, 43, 0.58));
  max-width: 42ch;
}

.messages-page__stats {
  margin: 10px 0 0;
  font-size: 13px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.72);

  &-dot {
    margin: 0 4px;
    opacity: 0.5;
  }
}

.messages-page__mark-read {
  appearance: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 11px 18px;
  font: inherit;
  font-size: 13px;
  font-weight: 650;
  color: #355247;
  background: #fff;
  border: 1px solid rgba(42, 157, 111, 0.35);
  border-radius: 999px;
  box-shadow: 0 2px 8px rgba(53, 92, 75, 0.08);
  transition:
    background 0.18s ease,
    color 0.18s ease,
    transform 0.18s ease,
    box-shadow 0.18s ease,
    opacity 0.18s ease;

  &:hover:not(:disabled) {
    background: rgba(42, 157, 111, 0.1);
    color: #1f3d32;
    transform: translateY(-1px);
    box-shadow: 0 4px 14px rgba(42, 157, 111, 0.18);
  }

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
    box-shadow: none;
  }
}

.messages-page__tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
  padding: 4px;
  background: rgba(231, 246, 238, 0.65);
  border-radius: 999px;
  border: 1px solid rgba(126, 197, 160, 0.25);
}

.messages-page__tab {
  appearance: none;
  cursor: pointer;
  border: 1px solid transparent;
  background: transparent;
  padding: 8px 14px;
  border-radius: 999px;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  color: #5a7268;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition:
    background 0.18s ease,
    color 0.18s ease,
    box-shadow 0.18s ease;

  &:hover:not(.is-active) {
    background: rgba(255, 255, 255, 0.75);
    color: #2a6b52;
  }

  &.is-active {
    background: #fff;
    border-color: rgba(42, 157, 111, 0.28);
    color: #1f4d3a;
    box-shadow: 0 2px 12px rgba(53, 92, 75, 0.1);

    .messages-page__tab-count {
      background: rgba(42, 157, 111, 0.14);
      color: $accent;
    }
  }
}

.messages-page__tab-count {
  min-width: 20px;
  padding: 1px 7px;
  font-size: 11px;
  font-weight: 700;
  border-radius: 999px;
  background: rgba(53, 92, 75, 0.1);
  color: #5a7268;
}

.messages-page__empty {
  padding: 48px 20px 40px;
  text-align: center;
  border-radius: 18px;
  border: 1px dashed rgba(126, 197, 160, 0.45);
  background: rgba(247, 251, 248, 0.8);
}

.messages-page__empty-art {
  width: 80px;
  height: 80px;
  margin: 0 auto 14px;
  border-radius: 22px;
  background: linear-gradient(145deg, rgba(126, 197, 160, 0.35), rgba(42, 157, 111, 0.2));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: #3d8b6f;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.messages-page__empty-title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 650;
  color: $ink;
}

.messages-page__empty-text {
  margin: 0 auto;
  font-size: 14px;
  line-height: 1.6;
  color: var(--nb-muted, rgba(36, 51, 43, 0.58));
  max-width: 36ch;
}

.messages-page__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.messages-page__item {
  position: relative;
  display: flex;
  gap: 14px;
  padding: 16px 18px;
  border-radius: 18px;
  border: 1px solid rgba(126, 197, 160, 0.24);
  background: #fff;
  box-shadow: 0 2px 10px rgba(53, 92, 75, 0.05);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 22px rgba(53, 92, 75, 0.1);
    border-color: rgba(42, 157, 111, 0.32);
  }

  &.is-unread {
    border-color: rgba(42, 157, 111, 0.38);
    background: linear-gradient(
      105deg,
      rgba(42, 157, 111, 0.07) 0%,
      #fff 42%
    );
    box-shadow:
      inset 4px 0 0 $accent,
      0 4px 16px rgba(42, 157, 111, 0.1);
  }

  &.is-type-3 {
    border-color: $warn-border;
    background: $warn-bg;

    &.is-unread {
      box-shadow:
        inset 4px 0 0 $warn,
        0 4px 16px rgba(217, 119, 6, 0.12);
    }

    &:hover {
      border-color: rgba(217, 119, 6, 0.5);
    }
  }
}

.messages-page__item-dot {
  position: absolute;
  top: 14px;
  right: 14px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: $accent;
  box-shadow: 0 0 0 3px rgba(42, 157, 111, 0.2);
}

.is-type-3 .messages-page__item-dot {
  background: $warn;
  box-shadow: 0 0 0 3px rgba(217, 119, 6, 0.2);
}

.messages-page__item-avatar-wrap {
  flex-shrink: 0;
}

.messages-page__item-avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 10px rgba(53, 92, 75, 0.14);
}

.messages-page__item-badge {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
  box-shadow: 0 3px 12px rgba(53, 92, 75, 0.15);

  &--metric {
    background: linear-gradient(145deg, #f59e0b, #d97706);
  }

  &--system {
    background: linear-gradient(145deg, #94a3b8, #64748b);
  }
}

.messages-page__item-body {
  min-width: 0;
  flex: 1;
}

.messages-page__item-top {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.messages-page__type-chip {
  display: inline-flex;
  padding: 3px 10px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  border-radius: 999px;

  &--1 {
    background: rgba(42, 157, 111, 0.12);
    color: #1f6b4f;
  }

  &--2 {
    background: rgba(245, 158, 11, 0.15);
    color: #b45309;
  }

  &--3 {
    background: rgba(217, 119, 6, 0.14);
    color: #b45309;
  }

  &--4 {
    background: rgba(100, 116, 139, 0.14);
    color: #475569;
  }
}

.messages-page__item-sender {
  font-size: 14px;
  font-weight: 700;
  color: #355247;
  margin-bottom: 6px;
}

.messages-page__item-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: rgba(36, 51, 43, 0.9);
  word-break: break-word;

  &--alert {
    line-height: 1.65;
  }
}

.messages-page__metric-name {
  display: inline;
  margin: 0 4px;
  padding: 1px 8px;
  font-weight: 700;
  color: #92400e;
  background: rgba(255, 255, 255, 0.75);
  border-radius: 6px;
  border: 1px solid rgba(217, 119, 6, 0.25);
}

.messages-page__alert-lead {
  margin-right: 2px;
}

.messages-page__like-icon {
  margin-right: 4px;
  color: #f59e0b;
  font-size: 15px;
  vertical-align: -1px;
}

.messages-page__item-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
}

.messages-page__time {
  font-size: 12px;
  color: rgba(53, 82, 71, 0.55);
  font-weight: 500;
}

.messages-page__reply {
  appearance: none;
  cursor: pointer;
  padding: 6px 14px;
  font: inherit;
  font-size: 12px;
  font-weight: 650;
  color: $accent;
  background: rgba(42, 157, 111, 0.08);
  border: 1px solid rgba(42, 157, 111, 0.28);
  border-radius: 999px;
  transition:
    background 0.15s ease,
    color 0.15s ease;

  &:hover {
    background: rgba(42, 157, 111, 0.16);
    color: #1f6b4f;
  }
}

.messages-page__home {
  margin: 24px 0 0;
  text-align: center;
}

.messages-page__home-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #355247;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 999px;
  transition:
    color 0.15s ease,
    background 0.15s ease;

  &:hover {
    color: $accent;
    background: rgba(42, 157, 111, 0.08);
  }
}

@media (max-width: 520px) {
  .messages-page__head {
    flex-direction: column;
  }

  .messages-page__mark-read {
    width: 100%;
    justify-content: center;
  }

  .messages-page__tabs {
    border-radius: 16px;
  }

  .messages-page__tab {
    flex: 1 1 auto;
    justify-content: center;
    min-width: calc(50% - 8px);
  }
}
</style>
