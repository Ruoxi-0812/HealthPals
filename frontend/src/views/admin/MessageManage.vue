<template>
  <div>
    <AdminPageShell page-class="admin-page--messages">
      <template #toolbar>
<div class="admin-toolbar-row">
        <el-date-picker
          size="small"
          class="admin-date-picker"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Message start"
          end-placeholder="Message end"
        />
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="messageQueryDto.content"
          placeholder="Message content"
          clearable
          @clear="handleFilterClear"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="handleFilter"
          />
        </el-input>
        <div class="admin-page__toolbar-actions">
          <el-tooltip content="Targeted push" placement="bottom">
            <button
              type="button"
              class="admin-toolbar-btn admin-toolbar-btn--primary"
              aria-label="Targeted push"
              @click="allMessagePush"
            >
              <i class="el-icon-plus" aria-hidden="true" />
              <span>Targeted push</span>
            </button>
          </el-tooltip>
        </div>
      </div>
      </template>
<el-table
        stripe
        row-key="id"
        :row-class-name="rowClassName"
        :data="tableData"
        class="admin-table-full"
        empty-text="No messages match your filters."
      >
        <el-table-column
          width="190"
          label="Delivery"
          class-name="admin-col-msg-meta"
        >
          <template slot-scope="scope">
            <div class="admin-msg-meta">
              <div class="admin-msg-meta__tags">
                <span
                  v-if="!scope.row.isRead"
                  class="admin-badge admin-badge--nowrap admin-badge--ok"
                >
                  Unread
                </span>
                <span
                  class="admin-badge admin-badge--nowrap"
                  :class="typeBadgeClass(scope.row.messageType)"
                >
                  {{ typeLabel(scope.row.messageType) }}
                </span>
              </div>
              <span class="admin-msg-meta__receiver">{{
                scope.row.receiverName || "—"
              }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="content"
          min-width="520"
          label="Message"
          class-name="admin-col-msg-body"
        >
          <template slot-scope="scope">
            <p
              v-if="healthAlertParts(scope.row)"
              class="admin-msg-content admin-msg-content--alert"
            >
              <span>{{ healthAlertParts(scope.row).lead }} </span>
              <strong class="admin-msg-content__metric">{{
                healthAlertParts(scope.row).metric
              }}</strong>
              <span> {{ healthAlertParts(scope.row).tail }}</span>
            </p>
            <p v-else class="admin-msg-content">{{ scope.row.content }}</p>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          width="180"
          label="Sent"
          class-name="admin-col-recorded"
        >
          <template slot-scope="scope">
            <el-tooltip
              :content="formatDateTimeFull(scope.row.createTime)"
              placement="top"
              :disabled="!scope.row.createTime"
            >
              <span class="admin-health-recorded">{{
                formatRecordedLine(scope.row.createTime)
              }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          label="Actions"
          width="92"
          align="center"
          header-align="center"
          class-name="admin-col-actions"
        >
          <template slot-scope="scope">
            <div class="admin-row-actions">
              <el-tooltip content="Delete message" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--danger"
                  aria-label="Delete message"
                  @click="handleDelete(scope.row)"
                >
                  <i class="el-icon-delete" aria-hidden="true" />
                </button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="admin-pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      />
    </AdminPageShell>

    <el-dialog
      custom-class="hp-dialog"
      :show-close="true"
      append-to-body
      :visible.sync="dialogMessageOperation"
      width="420px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Messages</span>
        <h2 class="hp-dialog__title">Push notification</h2>
      </div>
      <div class="hp-dialog__body">
        <label class="hp-field">
          <span class="hp-field__label">Content</span>
          <el-input
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            placeholder="Message content"
            v-model="messageContent"
          />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--ghost"
          @click="dialogMessageOperation = false"
        >
          Cancel
        </button>
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="messagePushOperation"
        >
          Confirm push
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AdminPageShell from "@/components/admin/AdminPageShell.vue";

import {
  formatDateShort,
  formatTimeShort,
  formatDateTimeFull,
} from "@/utils/data";

export default {
  components: { AdminPageShell },
  data() {
    return {
      data: {},
      filterText: "",
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      tableData: [],
      searchTime: [],
      selectedRows: [],
      messageQueryDto: {},
      messageTypeList: [],
      dialogMessageOperation: false,
      messageContent: "",
    };
  },
  created() {
    this.fetchFreshData();
    this.loadAllMessageType();
  },
  methods: {
    formatDateShort,
    formatTimeShort,
    formatDateTimeFull,
    formatRecordedLine(createTime) {
      const date = formatDateShort(createTime);
      const time = formatTimeShort(createTime);
      if (!time || date === "—") {
        return date;
      }
      return `${date}, ${time}`;
    },
    typeLabel(messageType) {
      const map = {
        1: "Comment reply",
        2: "Comment like",
        3: "Metric reminder",
        4: "System",
      };
      return map[messageType] || "Message";
    },
    typeIcon(messageType) {
      const map = {
        1: "el-icon-chat-line-round",
        2: "el-icon-star-on",
        3: "el-icon-warning-outline",
        4: "el-icon-bell",
      };
      return map[messageType] || "el-icon-message";
    },
    typeBadgeClass(messageType) {
      const map = {
        1: "admin-badge--type-comment",
        2: "admin-badge--type-like",
        3: "admin-badge--type-metric",
        4: "admin-badge--type-system",
      };
      return map[messageType] || "admin-badge--role-user";
    },
    healthAlertParts(message) {
      if (message.messageType !== 3) {
        return null;
      }
      const text = (message.content || "").trim();
      if (!text) {
        return null;
      }
      const bracket = text.match(/【([^】]+)】/);
      if (bracket) {
        const idx = text.indexOf(bracket[0]);
        return {
          lead: text.slice(0, idx).trim() || "Recorded",
          metric: bracket[1],
          tail: text.slice(idx + bracket[0].length).trim(),
        };
      }
      return null;
    },
    rowClassName({ row }) {
      if (!row.isRead) {
        return "admin-msg-row--unread";
      }
      if (row.messageType === 3) {
        return "admin-msg-row--alert";
      }
      return "";
    },
    messagePushOperation() {
      const message = {
        content: this.messageContent,
      };
      this.$axios
        .post("/message/systemInfoUsersSave", message)
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.$notify({
              duration: 2000,
              title: "Push",
              message: "Success",
              type: "success",
            });
            this.dialogMessageOperation = false;
            this.messageContent = "";
            this.fetchFreshData();
          }
        });
    },
    allMessagePush() {
      this.dialogMessageOperation = true;
    },
    loadAllMessageType() {
      this.$axios.get("/message/types").then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.messageTypeList = data.data;
        }
      });
    },
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete message",
        text: `This action cannot be undone. Continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(`/message/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: "Deleted",
              text: response.data.msg,
              icon: "success",
              showConfirmButton: false,
              timer: 2000,
            });
            this.selectedRows = [];
            this.fetchFreshData();
          }
        } catch (e) {
          console.error(`Error deleting message:`, e);
        }
      }
    },
    async fetchFreshData() {
      try {
        let startTime = null;
        let endTime = null;
        if (this.searchTime != null && this.searchTime.length === 2) {
          const [startDate, endDate] = await Promise.all(
            this.searchTime.map((date) => date.toISOString()),
          );
          startTime = `${startDate.split("T")[0]}T00:00:00`;
          endTime = `${endDate.split("T")[0]}T23:59:59`;
        }
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          startTime,
          endTime,
          ...this.messageQueryDto,
        };
        const response = await this.$axios.post("/message/query", params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Error fetching message data:", error);
      }
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.messageQueryDto = {};
      this.searchTime = [];
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchFreshData();
    },
    handleDelete(row) {
      this.selectedRows = [row];
      this.batchDelete();
    },
  },
};
</script>
