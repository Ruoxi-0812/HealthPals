<template>
  <div class="ev-comments">
    <header class="ev-comments__head">
      <div class="ev-comments__heading">
        <h2 class="ev-comments__title">
          Comments
          <span class="ev-comments__count">{{ evaluationsCount }}</span>
        </h2>
        <span class="ev-comments__accent" aria-hidden="true" />
      </div>
    </header>

    <div class="ev-comments__composer">
      <UserAvatar class="ev-comments__avatar" :src="userData.userAvatar" />
      <div class="ev-panel" :class="{ 'is-focused': isFocused }">
        <div class="ev-panel__top">
          <span class="ev-panel__label">Add a comment</span>
        </div>
        <textarea
          v-model="content"
          class="ev-panel__input"
          placeholder="Share your thoughts about this article..."
          rows="1"
          @focus="onFocus"
          @blur="onBlur"
        />
        <div class="ev-panel__foot">
          <span class="ev-panel__counter">{{ content.length }} / 300</span>
          <el-button
            type="primary"
            size="small"
            class="ev-panel__submit"
            @click="commentClick"
          >
            Post
          </el-button>
        </div>
      </div>
    </div>

    <div v-if="!commentList.length" class="ev-empty">
      <div class="ev-empty__icon">
        <i class="el-icon-chat-dot-round" aria-hidden="true" />
      </div>
      <p class="ev-empty__title">No comments yet</p>
    </div>

    <div v-else class="ev-thread">
      <div
        v-for="(comment, index) in commentList"
        :key="'c-' + (comment.id || index)"
        class="ev-comment"
      >
        <div class="ev-comment__row">
          <UserAvatar class="ev-comment__avatar" size="large" :src="comment.userAvatar" />
          <div class="ev-comment__main">
            <div class="ev-comment__headline">
              <span class="ev-comment__name">{{ comment.userName }}</span>
              <span v-if="comment.userId == userId" class="ev-badge">You</span>
            </div>
            <p class="ev-comment__body">{{ comment.content }}</p>
            <div class="ev-comment__actions">
              <span class="ev-muted">{{ comment.time }}</span>
              <el-popconfirm
                v-if="comment.userId == userId"
                popper-class="hp-popconfirm"
                confirm-button-text="Delete"
                cancel-button-text="Cancel"
                confirm-button-type="danger"
                icon="el-icon-warning"
                icon-color="#c98a2e"
                title="Delete this comment?"
                @confirm="deleteComment(comment)"
              >
                <button slot="reference" type="button" class="ev-link">
                  <i class="el-icon-delete" />
                  Remove
                </button>
              </el-popconfirm>
              <button
                type="button"
                class="ev-link"
                @click="toggleReplyInput(comment)"
              >
                <i class="el-icon-chat-dot-round" />
                Reply<span v-if="comment.childTotal"> · {{ comment.childTotal }}</span>
              </button>
              <button
                type="button"
                class="ev-link"
                :class="{ 'is-on': comment.upvoteFlag }"
                @click="upvote(comment)"
              >
                <i
                  :class="
                    comment.upvoteFlag ? 'el-icon-star-on' : 'el-icon-star-off'
                  "
                />
                {{ comment.upvoteFlag && comment.upvoteCount ? comment.upvoteCount + " likes" : "Like" }}
              </button>
            </div>

            <div v-if="comment.showReplyInput" class="ev-reply-box">
              <div class="ev-panel is-nested is-open">
                <textarea
                  v-model="replyContent"
                  class="ev-panel__input"
                  rows="3"
                  :placeholder="replyText"
                />
                <div class="ev-panel__foot">
                  <span class="ev-panel__counter"
                    >{{ replyContent.length }} / 300</span
                  >
                  <el-button
                    type="primary"
                    size="small"
                    class="ev-panel__submit"
                    @click="submitReply(comment)"
                  >
                    Reply
                  </el-button>
                </div>
              </div>
            </div>

            <div
              v-for="(commentChild, cidx) in comment.commentChildVOS || []"
              :key="'r-' + (commentChild.id || cidx)"
              class="ev-nested"
            >
              <div class="ev-nested__bar" aria-hidden="true" />
              <div class="ev-nested__inner">
                <div class="ev-comment__headline ev-comment__headline--compact">
                  <UserAvatar
                    class="ev-comment__avatar-sm"
                    size="small"
                    :src="commentChild.userAvatar"
                  />
                  <span class="ev-comment__name">{{ commentChild.userName }}</span>
                  <span v-if="commentChild.userId == userId" class="ev-badge"
                    >You</span
                  >
                  <template v-if="commentChild.replierName != null">
                    <span class="ev-reply-pill">
                      <span class="ev-reply-pill__to">to</span>
                      <UserAvatar
                        class="ev-comment__avatar-xs"
                        size="small"
                        :src="commentChild.replierAvatar"
                      />
                      <span class="ev-comment__name ev-comment__name--dim">{{
                        commentChild.replierName
                      }}</span>
                      <span
                        v-if="commentChild.replierId == userId"
                        class="ev-badge ev-badge--ghost"
                        >You</span
                      >
                    </span>
                  </template>
                </div>
                <p class="ev-comment__body ev-comment__body--nested">
                  {{ commentChild.content }}
                </p>
                <div class="ev-comment__actions">
                  <span class="ev-muted">{{ commentChild.time }}</span>
                  <el-popconfirm
                    v-if="commentChild.userId == userId"
                    popper-class="hp-popconfirm"
                    confirm-button-text="Delete"
                    cancel-button-text="Cancel"
                    confirm-button-type="danger"
                    icon="el-icon-warning"
                    icon-color="#c98a2e"
                    title="Delete this comment?"
                    @confirm="deleteComment(commentChild)"
                  >
                    <button slot="reference" type="button" class="ev-link">
                      <i class="el-icon-delete" />
                      Remove
                    </button>
                  </el-popconfirm>
                  <button
                    type="button"
                    class="ev-link"
                    @click="toggleReplyInput1(commentChild)"
                  >
                    <i class="el-icon-chat-dot-round" />
                    Reply
                  </button>
                  <button
                    type="button"
                    class="ev-link"
                    :class="{ 'is-on': commentChild.upvoteFlag }"
                    @click="upvote(commentChild)"
                  >
                    <i
                      :class="
                        commentChild.upvoteFlag
                          ? 'el-icon-star-on'
                          : 'el-icon-star-off'
                      "
                    />
                    {{
                      commentChild.upvoteFlag && commentChild.upvoteCount
                        ? commentChild.upvoteCount + " likes"
                        : "Like"
                    }}
                  </button>
                </div>
                <div
                  v-if="commentChild.replyInputStatus"
                  class="ev-reply-box ev-reply-box--tight"
                >
                  <div class="ev-panel is-nested is-open">
                    <textarea
                      v-model="replyChildContent"
                      class="ev-panel__input"
                      rows="3"
                      :placeholder="replyText"
                    />
                    <div class="ev-panel__foot">
                      <span class="ev-panel__counter"
                        >{{ replyChildContent.length }} / 300</span
                      >
                      <el-button
                        type="primary"
                        size="small"
                        class="ev-panel__submit"
                        @click="submitReply1(commentChild)"
                      >
                        Reply
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      class="ev-report-dialog"
      title="Report this comment"
      :visible.sync="dialogVisibleReport"
      width="30%"
    >
      <el-row
        v-for="(item, index) in reports"
        :key="index"
        style="margin-top: 10px"
      >
        <el-row style="padding-bottom: 10px; user-select: none"
          >*{{ item.name }}</el-row
        >
        <el-row>
          <span
            v-for="(itemChild, indexChild) in item.list"
            :key="indexChild"
            @click="reportItemClick(itemChild)"
          >
            <button
              :style="{
                border: itemChild.isSelected
                  ? '1px solid #4b87bc'
                  : '1px solid #f4f4f4',
              }"
              class="reportItem"
            >
              {{ itemChild.name }}
            </button>
          </span>
        </el-row>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <button class="cannel-btn" @click="dialogVisibleReport = false">
          Cancel
        </button>
        <button class="yes-btn" @click="operationReport">
          Confirmed report
        </button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { timeAgo } from "@/utils/data";
import UserAvatar from "@/components/UserAvatar.vue";
export default {
  components: { UserAvatar },
  props: {
    contentId: {
      type: Number,
      default: "",
    },
    contentType: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      userData: {},
      noteData: {},
      commentContent: "",
      content: "",
      commentList: [],
      replyContent: "",
      id: null,
      isFocused: false,
      strLength: "0/300",
      replyText: "",
      userId: "",
      dialogVisibleReport: false,
      reports: [],
      selectdStatus: false,
      evaluationsCount: 0,
      comment: {},
      replyChildContent: "",
    };
  },
  watch: {
    contentId(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.loadCommentList();
      }
    },
    content() {
      if (this.content === "") {
        this.isFocused = false;
        return;
      }
    },
  },
  created() {
    this.getUserInfo();
    this.loadCommentList();
  },
  methods: {
    getUserInfo() {
      const userInfo = sessionStorage.getItem("userInfo");
      if (!userInfo) {
        this.userData = {};
        return;
      }
      try {
        this.userData = JSON.parse(userInfo);
        this.userId = this.userData.id;
      } catch {
        this.userData = {};
      }
    },

    sanitizeCommentText(raw) {
      if (raw == null) {
        return "";
      }
      let s = String(raw);
      s = s.replace(/\\'/g, "'");
      s = s.replace(/\\"/g, '"');
      return s;
    },

    upvote(comment) {
      const evaluations = {
        id: comment.id,
      };
      this.$axios
        .put(`evaluations/update`, evaluations)
        .then((res) => {
          if (res.data.code == 200) {
            console.log(JSON.stringify(res.data.data));

            comment.upvoteFlag = res.data.data.flag;
            comment.upvoteCount = Number(res.data.data.num);
          }
        })
        .catch((err) => {
          console.error(`The like status setting is abnormal -> `, err);
        });
    },

    operationReport() {
      let reportItem = [];
      this.reports.forEach((entity) => {
        let entityReport = entity.list.filter((child) => child.isSelected);
        if (entityReport.length != 0) {
          reportItem = entityReport;
        }
      });
      if (!reportItem.length) {
        this.$message(`Please check the report item`);
        return;
      }
      this.$axios
        .get(
          `evaluations-reports/report/${this.comment.id}/${reportItem[0].name}`,
        )
        .then((res) => {
          this.dialogVisibleReport = false;
          if (res.data.code == 200) {
            this.$swal.fire({
              title: "Reporting operation",
              text: "Reporting success",
              icon: "success",
              showConfirmButton: false,
              timer: 1100,
            });
          } else {
            this.$swal.fire({
              title: "Reporting operation",
              text: res.data.msg,
              icon: "error",
              showConfirmButton: false,
              timer: 1100,
            });
          }
        })
        .catch((err) => {
          console.error(`Comment report exception -> `, err);
        });
    },

    reportItemClick(itemChild) {
      this.reports.forEach((entity) => {
        entity.list.forEach((child) => {
          child.isSelected = false;
        });
      });
      itemChild.isSelected = true;
    },
    reportList() {
      this.$axios
        .get(`evaluations/reportList`)
        .then((res) => {
          if (res.data.code == 200) {
            this.reports = [];
            res.data.data.forEach((entity) => {
              let report = { name: entity.name };
              let resportList = [];
              entity.list.forEach((listItem) => {
                let reportChild = {};
                reportChild.name = listItem;
                reportChild.isSelected = false;
                resportList.push(reportChild);
              });
              report.list = resportList;
              this.reports.push(report);
            });
          }
        })
        .catch((err) => {
          console.error(`Failed to load the report item -> `, err);
        });
    },
    reportComment(comment) {
      this.reportList();
      this.dialogVisibleReport = true;
      this.comment = comment;
    },
    deleteComment(comment) {
      this.$axios
        .delete(`evaluations/delete/${comment.id}`)
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success(res.data.msg);
            this.loadCommentList();
          }
        })
        .catch((err) => {
          console.error(`Comment exception -> `, err);
        });
    },
    onFocus() {
      this.isFocused = true;
    },

    onBlur() {
      if (this.content === "") {
        this.isFocused = false;
        return;
      }
      this.isFocused = true;
    },
    commentClick() {
      if (this.content == "") {
        this.$swal.fire({
          title: "Content prompt",
          text: "The comments are empty",
          icon: "success",
          showConfirmButton: false,
          timer: 800,
        });
        return;
      }
      const evaluations = {
        contentType: this.contentType,
        content: this.content,
        contentId: this.contentId,
      };
      this.$axios
        .post(`evaluations/insert`, evaluations)
        .then((res) => {
          if (res.data.code == 200) {
            this.content = "";
            this.$swal.fire({
              title: "Comment operation",
              text: "Comment success",
              icon: "success",
              showConfirmButton: false,
              timer: 1100,
            });
            setTimeout(() => {
              this.loadCommentList();
            }, 1100);
          } else {
            this.$swal.fire({
              title: "Comment exception",
              text: res.data.msg,
              icon: "error",
              showConfirmButton: false,
              timer: 1100,
            });
          }
        })
        .catch((err) => {
          console.error(`Comment exception -> `, err);
        });
    },

    toggleReplyInput(comment) {
      this.replyText = `Say something to ${comment.userName}…`;
      if (comment.showReplyInput == null) {
        comment.showReplyInput = false;
      }
      comment.showReplyInput = !comment.showReplyInput;
    },

    toggleReplyInput1(comment) {
      this.replyText = `Quick reply to ${comment.userName}…`;
      if (comment.replyInputStatus == null) {
        comment.replyInputStatus = false;
      }
      comment.replyInputStatus = !comment.replyInputStatus;
    },

    submitReply(comment) {
      if (this.replyContent == "") {
        this.$message(`Comments cannot be empty`);
        return;
      }
      const evaluationsDTO = {
        contentType: this.contentType,
        content: this.replyContent,
        contentId: this.contentId,
        parentId: comment.id,
      };
      this.$axios
        .post(`evaluations/insert`, evaluationsDTO)
        .then((res) => {
          if (res.data.code == 200) {
            this.replyContent = "";
            comment.showReplyInput = false;
            this.$swal.fire({
              title: "Reply operation",
              text: "Reply success",
              icon: "success",
              showConfirmButton: false,
              timer: 1300,
            });
            setTimeout(() => {
              this.loadCommentList();
            }, 1300);
          } else {
            this.$swal.fire({
              title: "Comment exception",
              text: res.data.msg,
              icon: "error",
              showConfirmButton: false,
              timer: 1100,
            });
          }
        })
        .catch((err) => {
          console.error(`Comment exception -> `, err);
        });
    },

    submitReply1(comment) {
      if (this.replyChildContent == "") {
        this.$message(`Comments cannot be empty`);
        return;
      }
      const evaluationsDTO = {
        replierId: comment.userId,
        contentType: this.contentType,
        content: this.replyChildContent,
        contentId: this.contentId,
        parentId: comment.parentId,
      };
      this.$axios
        .post(`evaluations/insert`, evaluationsDTO)
        .then((res) => {
          if (res.data.code == 200) {
            this.replyChildContent = "";
            comment.replyInputStatus = false;
            this.$swal.fire({
              title: "Reply operation",
              text: "Reply success",
              icon: "success",
              showConfirmButton: false,
              timer: 1300,
            });
            setTimeout(() => {
              this.loadCommentList();
            }, 1300);
          } else {
            this.$swal.fire({
              title: "Comment exception",
              text: res.data.msg,
              icon: "error",
              showConfirmButton: false,
              timer: 1100,
            });
          }
        })
        .catch((err) => {
          console.error(`Comment exception -> `, err);
        });
    },
    goBack() {
      this.$router.go(-1);
    },

    loadCommentList() {
      this.$axios
        .get(`evaluations/list/${this.contentId}/${this.contentType}`)
        .then((res) => {
          if (res.data.code == 200) {
            this.commentList = res.data.data.data;
            this.evaluationsCount = res.data.data.evaluationsCount;

            this.commentList.forEach((entity) => {
              entity.content = this.sanitizeCommentText(entity.content);
              entity.time = timeAgo(entity.createTime);
              const children = entity.commentChildVOS || [];
              children.forEach((child) => {
                child.content = this.sanitizeCommentText(child.content);
                child.time = timeAgo(child.createTime);
              });
              entity.commentChildVOS = children;
            });
          }
        })
        .catch((err) => {
          console.error(`Comment query Exception -> `, err);
        });
    },
  },
};
</script>
<style lang="scss">
$ev-accent: #2a9d6f;
$ev-accent-dark: #248760;
$ev-ink: #24332b;
$ev-muted: rgba(36, 51, 43, 0.55);
$ev-border: rgba(126, 197, 160, 0.22);

.ev-comments {
  width: 100%;
  font-family: var(--nb-font, system-ui, sans-serif);
  color: $ev-ink;
}

.ev-comments__head {
  margin-bottom: 20px;
}

.ev-comments__heading {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ev-comments__title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.35rem;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: $ev-ink;
  display: flex;
  align-items: center;
  gap: 10px;
}

.ev-comments__accent {
  width: 68px;
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(42, 157, 111, 0.1), rgba(42, 157, 111, 0.26));
  border: 1px solid rgba(42, 157, 111, 0.16);
}

.ev-comments__count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 1.6rem;
  padding: 2px 10px;
  font-family: var(--nb-font, system-ui, sans-serif);
  font-size: 13px;
  font-weight: 600;
  color: #1a5c4a;
  background: rgba(42, 157, 111, 0.14);
  border: 1px solid rgba(42, 157, 111, 0.28);
  border-radius: 999px;
}

.ev-comments__composer {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  margin-bottom: 28px;
}

.ev-comments__avatar {
  flex-shrink: 0;
  margin-top: 4px;
}

.ev-panel {
  flex: 1;
  min-width: 0;
  background: linear-gradient(180deg, rgba(248, 252, 250, 0.98) 0%, rgba(242, 248, 244, 0.95) 100%);
  border: 1px solid rgba(126, 197, 160, 0.3);
  border-radius: 20px;
  padding: 12px 16px 50px;
  position: relative;
  box-shadow: 0 14px 30px rgba(53, 92, 75, 0.08);
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;

  &.is-focused,
  &.is-open {
    border-color: rgba(42, 157, 111, 0.5);
    background: #fff;
    box-shadow:
      0 0 0 4px rgba(42, 157, 111, 0.12),
      0 18px 36px rgba(53, 92, 75, 0.12);
  }

  &.is-nested {
    padding-bottom: 50px;
    border-radius: 18px;
    box-shadow: none;
  }
}

.ev-panel__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.ev-panel__label {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: rgba(53, 82, 71, 0.55);
}

.ev-panel__input {
  display: block;
  width: 100%;
  min-height: 92px;
  margin: 0;
  padding: 4px 2px;
  border: none;
  outline: none;
  resize: vertical;
  font: inherit;
  font-size: 15px;
  line-height: 1.55;
  color: $ev-ink;
  background: transparent;
  user-select: text;

  &::placeholder {
    color: rgba(36, 51, 43, 0.38);
  }
}

.ev-comments__composer .ev-panel__input {
  min-height: 40px;
  font-size: 14px;
}

.ev-comments__composer .ev-panel {
  border-radius: 18px;
  padding: 10px 14px 44px;
}

.ev-comments__composer .ev-panel__top {
  margin-bottom: 6px;
}

.ev-panel__foot {
  position: absolute;
  left: 16px;
  right: 16px;
  bottom: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ev-panel__counter {
  font-size: 12px;
  color: $ev-muted;
}

.ev-panel__submit.el-button {
  border-radius: 999px;
  font-weight: 600;
  min-width: 92px;
  padding-left: 18px;
  padding-right: 18px;
  box-shadow: 0 10px 20px rgba(42, 157, 111, 0.2);
}

.ev-panel__submit.el-button--primary {
  background: $ev-accent;
  border-color: $ev-accent;

  &:hover,
  &:focus {
    background: $ev-accent-dark;
    border-color: $ev-accent-dark;
  }
}

.ev-empty {
  display: grid;
  place-items: center;
  text-align: center;
  padding: 28px 20px;
  border: 1px dashed rgba(126, 197, 160, 0.34);
  border-radius: 18px;
  background: rgba(248, 252, 250, 0.78);
}

.ev-empty__icon {
  display: grid;
  place-items: center;
  width: 52px;
  height: 52px;
  margin-bottom: 12px;
  border-radius: 50%;
  background: rgba(42, 157, 111, 0.1);
  color: $ev-accent-dark;
  font-size: 22px;
}

.ev-empty__title {
  margin: 0;
  font-size: 16px;
  font-weight: 650;
  color: #355247;
}

.ev-thread {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.ev-comment {
  position: relative;
}

.ev-comment__row {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.ev-comment__avatar {
  flex-shrink: 0;
}

.ev-comment__main {
  flex: 1;
  min-width: 0;
  padding: 16px 18px 14px;
  border: 1px solid $ev-border;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 10px 24px rgba(53, 92, 75, 0.05);
}

.ev-comment__headline {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 10px;
  margin-bottom: 6px;

  &--compact {
    margin-bottom: 4px;
    align-items: center;
  }
}

.ev-comment__avatar-sm {
  flex-shrink: 0;
}

.ev-comment__avatar-xs :deep(img) {
  width: 22px !important;
  height: 22px !important;
}

.ev-comment__name {
  font-weight: 650;
  font-size: 15px;
  color: #355247;

  &--dim {
    font-weight: 600;
    color: rgba(53, 82, 71, 0.78);
  }
}

.ev-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 999px;
  color: #1a5c4a;
  background: rgba(42, 157, 111, 0.12);
  border: 1px solid rgba(42, 157, 111, 0.25);

  &--ghost {
    background: rgba(42, 157, 111, 0.06);
  }
}

.ev-reply-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
  padding: 4px 10px 4px 6px;
  margin-left: 4px;
  border-radius: 999px;
  background: rgba(126, 197, 160, 0.12);
  border: 1px solid rgba(126, 197, 160, 0.28);

  &__to {
    font-size: 11px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    color: $ev-muted;
  }
}

.ev-comment__body {
  margin: 0 0 12px;
  font-size: 15px;
  line-height: 1.65;
  color: rgba(36, 51, 43, 0.94);
  white-space: pre-wrap;
  word-break: break-word;
  user-select: text;

  &--nested {
    margin-bottom: 10px;
  }
}

.ev-comment__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 16px;
  padding-top: 2px;
}

.ev-muted {
  font-size: 13px;
  color: $ev-muted;
}

.ev-link {
  appearance: none;
  border: none;
  background: none;
  cursor: pointer;
  padding: 0;
  margin: 0;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  color: #5a7a6d;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 10px;
  border-radius: 999px;
  transition:
    color 0.15s ease,
    background 0.15s ease;

  i {
    font-size: 14px;
  }

  &:hover {
    color: $ev-accent-dark;
    background: rgba(42, 157, 111, 0.08);
  }

  &.is-on {
    color: #1a5c4a;
    background: rgba(42, 157, 111, 0.1);
  }
}

.ev-reply-box {
  margin: 14px 0 6px;

  &--tight {
    margin-top: 12px;
  }
}

.ev-nested {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.ev-nested__bar {
  width: 3px;
  flex-shrink: 0;
  border-radius: 999px;
  background: linear-gradient(
    180deg,
    rgba(42, 157, 111, 0.45),
    rgba(126, 197, 160, 0.2)
  );
  align-self: stretch;
  min-height: 48px;
}

.ev-nested__inner {
  flex: 1;
  min-width: 0;
  padding: 12px 14px 8px;
  border-radius: 16px;
  background: rgba(244, 249, 246, 0.88);
  border: 1px solid rgba(126, 197, 160, 0.18);
}

.ev-report-dialog {
  border-radius: 12px;
}

.cannel-btn,
.yes-btn {
  padding: 0 15px 5px;
  font-size: 14px !important;
  margin: 0 10px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
}

.cannel-btn {
  color: #1c1c1c;
}

.yes-btn {
  background-color: #4b87bc;
  color: #eaf2ff;
}

.cannel-btn:hover {
  background-color: #f5f5f5;
}

.yes-btn:hover {
  background-color: #66a8e1;
}

.reportItem {
  display: inline-block;
  padding: 8px 22px;
  background-color: #f4f4f4;
  border: 1px solid #f4f4f4;
  margin: 5px 3px 5px 0;
  border-radius: 8px;
  cursor: pointer;
  user-select: none;
}

.reportItem:hover {
  border: 1px solid #4b87bc;
  color: #4b87bc;
}

@media (max-width: 720px) {
  .ev-comments__composer,
  .ev-comment__row {
    gap: 12px;
  }

  .ev-comments__composer,
  .ev-comment__row,
  .ev-comments__heading {
    align-items: flex-start;
  }

  .ev-panel__top {
    flex-direction: column;
    align-items: flex-start;
  }

  .ev-panel__foot {
    position: static;
    margin-top: 12px;
    padding-top: 10px;
    border-top: 1px solid rgba(126, 197, 160, 0.16);
  }

  .ev-panel {
    padding-bottom: 14px;
  }

  .ev-comments__composer .ev-panel__input {
    min-height: 34px;
  }

  .ev-comments__avatar,
  .ev-comment__avatar {
    transform: scale(0.94);
    transform-origin: top left;
  }

  .ev-comment__main {
    padding: 14px;
  }

  .ev-link {
    padding-left: 0;
    padding-right: 0;
    background: transparent;
  }
}
</style>
