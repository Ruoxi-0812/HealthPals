<template>
  <div class="messages-page">
    <header class="messages-page__top">
      <div class="messages-page__top-inner">
        <Logo sysName="HealthPals" :bag="'#2f4a40'" :size="32" />
        <div class="messages-page__user">
          <UserAvatar
            :size="32"
            :src="userInfo.userAvatar"
            :name="userInfo.userName"
          />
          <span class="messages-page__user-name">{{ userInfo.userName }}</span>
        </div>
      </div>
    </header>

    <main class="messages-page__main">
      <header class="messages-page__header">
        <button type="button" class="messages-page__back" @click="goBack">
          <i class="el-icon-arrow-left" aria-hidden="true" />
          Back
        </button>
      </header>

      <div class="messages-page__shell nb-surface">
        <aside class="messages-page__nav">
          <div class="messages-page__nav-head">
            <h1 class="messages-page__inbox-title">Inbox</h1>
            <span
              v-if="unreadCount"
              class="messages-page__unread-pill"
              :aria-label="`${unreadCount} unread`"
            >
              {{ unreadCount }} unread
            </span>
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

          <nav class="messages-page__folders" role="tablist">
            <button
              v-for="(messageType, index) in messageTypes"
              :key="index"
              type="button"
              role="tab"
              class="messages-page__folder"
              :class="{ 'is-active': activeFilter === messageType.type }"
              :aria-selected="activeFilter === messageType.type"
              @click="messageTypeSelected(messageType.type)"
            >
              <i :class="folderIcon(messageType.type)" aria-hidden="true" />
              <span class="messages-page__folder-label">{{
                typeLabel(messageType)
              }}</span>
              <span
                v-if="tabCount(messageType.type)"
                class="messages-page__folder-count"
              >
                {{ tabCount(messageType.type) }}
              </span>
            </button>
          </nav>
        </aside>

        <section class="messages-page__panel">
          <div class="messages-page__panel-head">
            <h2 class="messages-page__panel-title">
              {{ activeFolderLabel }}
            </h2>
            <p class="messages-page__panel-meta">
              {{ filteredMessageList.length }}
              {{
                filteredMessageList.length === 1 ? "message" : "messages"
              }}
            </p>
          </div>

          <div class="messages-page__scroll">
            <div
              v-if="filteredMessageList.length === 0"
              class="messages-page__empty"
            >
              <div class="messages-page__empty-art" aria-hidden="true">
                <i class="el-icon-message" />
              </div>
              <p class="messages-page__empty-title">All quiet here</p>
            </div>

            <ul v-else class="messages-page__list">
              <li
                v-for="(message, index) in filteredMessageList"
                :key="message.id || index"
                class="messages-page__row"
                :class="itemClass(message)"
              >
                <div class="messages-page__row-avatar">
                  <img
                    v-if="message.messageType === 1 || message.messageType === 2"
                    class="messages-page__avatar-img"
                    :src="message.senderAvatar"
                    alt=""
                  />
                  <span
                    v-else-if="message.messageType === 3"
                    class="messages-page__avatar-icon messages-page__avatar-icon--alert"
                    aria-hidden="true"
                  >
                    <i class="el-icon-warning-outline" />
                  </span>
                  <span
                    v-else
                    class="messages-page__avatar-icon messages-page__avatar-icon--system"
                    aria-hidden="true"
                  >
                    <i class="el-icon-bell" />
                  </span>
                </div>

                <div class="messages-page__row-main">
                  <div class="messages-page__row-top">
                    <span class="messages-page__row-from">{{
                      rowFrom(message)
                    }}</span>
                    <time
                      class="messages-page__row-time"
                      :datetime="message.createTime"
                      :title="message.createTime"
                    >
                      {{ parseTime(message.createTime) }}
                    </time>
                  </div>
                  <p class="messages-page__row-preview">
                    <template
                      v-if="message.messageType === 3 && healthAlertParts(message)"
                    >
                      <span class="messages-page__preview-alert">
                        {{ healthAlertParts(message).lead }}
                        <strong>{{ healthAlertParts(message).metric }}</strong>
                        {{ healthAlertParts(message).tail }}
                      </span>
                    </template>
                    <template v-else-if="message.messageType === 2">
                      <i class="el-icon-star-on messages-page__like-icon" />
                      {{ messagePreview(message) }}
                    </template>
                    <template v-else>{{ messagePreview(message) }}</template>
                  </p>
                  <div
                    v-if="message.messageType === 1"
                    class="messages-page__row-actions"
                  >
                    <button
                      type="button"
                      class="messages-page__reply"
                      @click="replyUser(message)"
                    >
                      Reply
                    </button>
                  </div>
                </div>

                <span
                  v-if="!message.isRead"
                  class="messages-page__unread-dot"
                  aria-label="Unread"
                />
              </li>
            </ul>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script>
import Logo from "@/components/Logo";
import UserAvatar from "@/components/UserAvatar.vue";
import { timeAgo } from "@/utils/data";

export default {
  name: "MessageCenter",
  components: { Logo, UserAvatar },
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
    activeFolderLabel() {
      const match = this.messageTypes.find((t) => t.type === this.activeFilter);
      return match ? this.typeLabel(match) : "All messages";
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
    folderIcon(type) {
      const map = {
        null: "el-icon-message",
        1: "el-icon-chat-line-square",
        2: "el-icon-star-off",
        3: "el-icon-warning-outline",
        4: "el-icon-bell",
      };
      return map[type] || "el-icon-folder";
    },
    rowFrom(message) {
      if (message.messageType === 1 || message.messageType === 2) {
        return message.senderName || "Someone";
      }
      return this.typeChipLabel(message.messageType);
    },
    messagePreview(message) {
      if (message.messageType === 1) {
        const parts = this.commentDeal(message.content);
        return parts[2] || message.content || "";
      }
      return (message.content || "").replace(/\s+/g, " ").trim();
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
    goBack() {
      this.$router.push("/user");
    },
  },
};
</script>

<style scoped lang="scss">
$ink: var(--nb-ink, #24332b);
$mint: rgba(126, 197, 160, 0.22);
$accent: #2a9d6f;
$warn: #d97706;

.messages-page {
  min-height: 100vh;
  box-sizing: border-box;
  background:
    radial-gradient(
      ellipse 80% 50% at 50% -10%,
      rgba(126, 197, 160, 0.28),
      transparent
    ),
    var(--nb-bg-soft, #e7f6ee);
}

.messages-page__top {
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid $mint;
  box-shadow: 0 4px 18px rgba(53, 92, 75, 0.07);
  backdrop-filter: blur(8px);
}

.messages-page__top-inner {
  width: 100%;
  max-width: none;
  margin: 0 auto;
  padding: 12px clamp(16px, 4vw, 40px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  box-sizing: border-box;
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
  width: 100%;
  max-width: none;
  margin: 0;
  padding: clamp(16px, 3vw, 28px) clamp(12px, 4vw, 40px) 48px;
  box-sizing: border-box;
}

.messages-page__header {
  width: 100%;
  max-width: none;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.messages-page__back {
  appearance: none;
  border: none;
  background: rgba(255, 255, 255, 0.85);
  padding: 8px 14px;
  border-radius: 999px;
  font: inherit;
  font-size: 14px;
  font-weight: 600;
  color: #355247;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid rgba(126, 197, 160, 0.35);
  box-shadow: var(--nb-shadow-sm, 0 4px 12px rgba(53, 92, 75, 0.08));

  &:hover {
    background: #fff;
    color: #2a9d6f;
  }
}

.messages-page__shell {
  display: grid;
  grid-template-columns: minmax(200px, 260px) minmax(0, 1fr);
  min-height: min(72vh, 720px);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.97);
  border: 1px solid $mint;
  border-radius: var(--nb-radius, 18px);
  box-shadow: var(--nb-shadow, 0 14px 36px rgba(53, 92, 75, 0.12));
}

.messages-page__nav {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px 14px;
  border-right: 1px solid rgba(126, 197, 160, 0.22);
  background: rgba(247, 251, 248, 0.65);
}

.messages-page__nav-head {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 10px;
  padding: 0 6px 4px;
}

.messages-page__inbox-title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.35rem;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: $ink;
}

.messages-page__unread-pill {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  background: $accent;
  border-radius: 999px;
}

.messages-page__mark-read {
  appearance: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  padding: 9px 12px;
  font: inherit;
  font-size: 12px;
  font-weight: 650;
  color: #355247;
  background: #fff;
  border: 1px solid rgba(42, 157, 111, 0.32);
  border-radius: 10px;
  transition:
    background 0.15s ease,
    opacity 0.15s ease;

  &:hover:not(:disabled) {
    background: rgba(42, 157, 111, 0.08);
  }

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }
}

.messages-page__folders {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 4px;
}

.messages-page__folder {
  appearance: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  text-align: left;
  color: #5a7268;
  background: transparent;
  border: none;
  border-radius: 10px;
  transition:
    background 0.15s ease,
    color 0.15s ease;

  i {
    font-size: 16px;
    opacity: 0.85;
  }

  &:hover:not(.is-active) {
    background: rgba(255, 255, 255, 0.7);
    color: #2a6b52;
  }

  &.is-active {
    background: #fff;
    color: #1f4d3a;
    box-shadow: 0 2px 10px rgba(53, 92, 75, 0.08);

    .messages-page__folder-count {
      background: rgba(42, 157, 111, 0.14);
      color: $accent;
    }
  }
}

.messages-page__folder-label {
  flex: 1;
  min-width: 0;
}

.messages-page__folder-count {
  min-width: 22px;
  padding: 1px 7px;
  font-size: 11px;
  font-weight: 700;
  text-align: center;
  border-radius: 999px;
  background: rgba(53, 92, 75, 0.1);
  color: #5a7268;
}

.messages-page__panel {
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 0;
}

.messages-page__panel-head {
  flex-shrink: 0;
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px 16px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.2);
}

.messages-page__panel-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
  color: $ink;
}

.messages-page__panel-meta {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.55);
}

.messages-page__scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.messages-page__empty {
  padding: 56px 24px;
  text-align: center;
}

.messages-page__empty-art {
  width: 72px;
  height: 72px;
  margin: 0 auto 14px;
  border-radius: 18px;
  background: rgba(126, 197, 160, 0.22);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #3d8b6f;
}

.messages-page__empty-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 650;
  color: $ink;
}

.messages-page__empty-text {
  margin: 0 auto;
  font-size: 14px;
  line-height: 1.55;
  color: rgba(36, 51, 43, 0.58);
  max-width: 36ch;
}

.messages-page__list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.messages-page__row {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 14px 20px 14px 18px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.16);
  background: #fff;
  transition: background 0.15s ease;

  &:hover {
    background: rgba(247, 251, 248, 0.95);
  }

  &.is-unread {
    background: rgba(231, 246, 238, 0.45);

    .messages-page__row-from {
      font-weight: 750;
      color: $ink;
    }
  }

  &.is-type-3 {
    border-left: 3px solid rgba(217, 119, 6, 0.55);
    padding-left: 15px;

    &.is-unread {
      background: rgba(255, 251, 243, 0.9);
    }
  }

  &:last-child {
    border-bottom: none;
  }
}

.messages-page__row-avatar {
  flex-shrink: 0;
}

.messages-page__avatar-img {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgba(126, 197, 160, 0.25);
}

.messages-page__avatar-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;

  &--alert {
    background: linear-gradient(145deg, #f59e0b, $warn);
  }

  &--system {
    background: linear-gradient(145deg, #94a3b8, #64748b);
  }
}

.messages-page__row-main {
  flex: 1;
  min-width: 0;
}

.messages-page__row-top {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 4px;
}

.messages-page__row-from {
  font-size: 14px;
  font-weight: 650;
  color: #355247;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.messages-page__row-time {
  flex-shrink: 0;
  font-size: 12px;
  color: rgba(53, 82, 71, 0.5);
  font-weight: 500;
}

.messages-page__row-preview {
  margin: 0;
  font-size: 13px;
  line-height: 1.5;
  color: rgba(36, 51, 43, 0.72);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}

.messages-page__preview-alert strong {
  font-weight: 700;
  color: #92400e;
}

.messages-page__like-icon {
  margin-right: 4px;
  color: #f59e0b;
  font-size: 14px;
  vertical-align: -1px;
}

.messages-page__row-actions {
  margin-top: 8px;
}

.messages-page__reply {
  appearance: none;
  cursor: pointer;
  padding: 4px 12px;
  font: inherit;
  font-size: 12px;
  font-weight: 650;
  color: $accent;
  background: transparent;
  border: 1px solid rgba(42, 157, 111, 0.28);
  border-radius: 999px;
  transition: background 0.15s ease;

  &:hover {
    background: rgba(42, 157, 111, 0.1);
  }
}

.messages-page__unread-dot {
  flex-shrink: 0;
  width: 8px;
  height: 8px;
  margin-top: 6px;
  border-radius: 50%;
  background: $accent;
}

.is-type-3 .messages-page__unread-dot {
  background: $warn;
}

@media (max-width: 768px) {
  .messages-page__shell {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .messages-page__nav {
    border-right: none;
    border-bottom: 1px solid rgba(126, 197, 160, 0.22);
    padding-bottom: 12px;
  }

  .messages-page__folders {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 6px;
  }

  .messages-page__folder {
    width: auto;
    flex: 1 1 auto;
    min-width: calc(50% - 6px);
    justify-content: center;
  }

  .messages-page__folder-label {
    flex: 0 1 auto;
  }

  .messages-page__scroll {
    max-height: min(60vh, 520px);
  }
}
</style>
