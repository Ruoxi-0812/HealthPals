<template>
  <div>
    <AdminPageShell page-class="admin-page--comments">
      <template #toolbar>
<div class="admin-toolbar-row">
        <el-date-picker
          size="small"
          class="admin-date-picker"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Comment from"
          end-placeholder="Comment to"
        />
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="evalustionsQueryDto.content"
          placeholder="Comment content"
          clearable
          @clear="handleFilterClear"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="handleFilter"
          />
        </el-input>
      </div>
      </template>
      <el-table
        stripe
        row-key="id"
        :row-class-name="rowClassName"
        @selection-change="handleSelectionChange"
        :data="tableData"
        class="admin-table-full"
        empty-text="No comments match your filters."
      >
        <el-table-column
          prop="content"
          min-width="560"
          label="Comment"
          class-name="admin-col-comment-body"
        >
          <template slot-scope="scope">
            <div class="admin-comment-cell">
              <el-tooltip
                :content="scope.row.content"
                placement="top"
                :disabled="!scope.row.content"
              >
                <p
                  class="admin-comment-body"
                  v-html="highlightKeyword(scope.row.content)"
                />
              </el-tooltip>
              <div class="admin-comment-cell__meta">
                <span
                  v-if="scope.row.parentId != null && scope.row.replierName"
                  class="admin-comment-author__reply"
                >
                  <i class="el-icon-right" aria-hidden="true" />
                  <span v-html="highlightKeyword(scope.row.replierName)" />
                </span>
                <span
                  class="admin-badge admin-badge--nowrap"
                  :class="
                    scope.row.parentId == null
                      ? 'admin-badge--warn'
                      : 'admin-badge--muted'
                  "
                >
                  {{
                    scope.row.parentId == null ? "Top-level" : "Reply"
                  }}
                </span>
                <span
                  class="admin-badge admin-badge--nowrap"
                  :class="sourceBadgeClass(scope.row.contentType)"
                >
                  {{ sourceLabel(scope.row.contentType) }}
                </span>
                <span
                  class="admin-comment-likes"
                  :class="{
                    'admin-comment-likes--zero': !likeCount(scope.row),
                  }"
                >
                  <i class="el-icon-star-on" aria-hidden="true" />
                  {{ likeCount(scope.row) }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          width="180"
          label="Author"
          class-name="admin-col-comment-author"
        >
          <template slot-scope="scope">
            <div class="admin-comment-author">
              <span
                class="admin-comment-author__name"
                v-html="highlightKeyword(scope.row.userName)"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          width="128"
          label="Posted"
          class-name="admin-col-recorded"
        >
          <template slot-scope="scope">
            <el-tooltip
              :content="formatDateTimeFull(scope.row.createTime)"
              placement="top"
              :disabled="!scope.row.createTime"
            >
              <div class="admin-cell-datetime">
                <span class="admin-cell-datetime__date">{{
                  formatDateShort(scope.row.createTime)
                }}</span>
                <span
                  v-if="formatTimeShort(scope.row.createTime)"
                  class="admin-cell-datetime__time"
                  >{{ formatTimeShort(scope.row.createTime) }}</span
                >
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          label="Actions"
          width="112"
          align="center"
          header-align="center"
          class-name="admin-col-actions"
        >
          <template slot-scope="scope">
            <div class="admin-row-actions">
              <el-tooltip content="View reports" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--primary"
                  aria-label="View reports"
                  @click="reportsList(scope.row)"
                >
                  <i class="el-icon-pie-chart" aria-hidden="true" />
                </button>
              </el-tooltip>
              <el-tooltip content="Delete comment" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--danger"
                  aria-label="Delete comment"
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
        :page-sizes="[20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      />
    </AdminPageShell>

    <el-dialog
      custom-class="hp-dialog admin-dialog-wide"
      :show-close="true"
      append-to-body
      :visible.sync="reportDialog"
      width="640px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Comments</span>
        <h2 class="hp-dialog__title">Report details</h2>
      </div>
      <div class="hp-dialog__body admin-report-grid">
        <PieChart :types="types" :values="values" />
        <div>
          <p v-if="!reportsDate.length" class="admin-dashboard__empty">
            No data available
          </p>
          <div
            v-else
            class="admin-report-stats-grid admin-form-stack"
          >
            <div
              v-for="(entity, index) in reportsDate"
              :key="index"
              class="admin-report-stat"
            >
              <div class="admin-report-stat__count">{{ entity.count }}</div>
              <div class="admin-report-stat__name">{{ entity.name }}</div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="reportDialog = false"
        >
          Close
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AdminPageShell from "@/components/admin/AdminPageShell.vue";

import PieChart from "@/components/PieChart";
import {
  formatDateShort,
  formatDateTimeFull,
  formatTimeShort,
} from "@/utils/data";

export default {
  components: { AdminPageShell, PieChart },
  data() {
    return {
      reportsDate: [],
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      searchTime: [],
      selectedRows: [],
      reportDialog: false,
      types: [],
      values: [],
      evalustionsQueryDto: {},
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    formatDateShort,
    formatDateTimeFull,
    formatTimeShort,
    likeCount(row) {
      if (!row.upvoteList) {
        return 0;
      }
      return row.upvoteList.split(",").filter((id) => id.trim()).length;
    },
    sourceLabel(contentType) {
      const map = {
        NEWS: "Article",
      };
      return map[contentType] || contentType || "—";
    },
    sourceBadgeClass(contentType) {
      const map = {
        NEWS: "admin-badge--type-comment",
      };
      return map[contentType] || "admin-badge--role-user";
    },
    rowClassName({ row }) {
      return row.parentId != null ? "admin-comment-row--reply" : "";
    },
    reportsList(row) {
      this.reportDialog = true;
      this.reportList(row.id);
    },
    async reportList(id) {
      const response = await this.$axios(
        `/evaluations-reports/reportCount/${id}`,
      );
      const { data } = response;
      this.reportsDate = data.data || [];
      this.types = this.reportsDate.map((entity) => entity.name);
      this.values = this.reportsDate.map((entity) => entity.count);
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete comment",
        text: `Deleted comments cannot be recovered. Continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(
            `/evaluations/batchDelete`,
            ids,
          );
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
          this.$swal.fire({
            title: "Error",
            text: String(e),
            icon: "error",
            showConfirmButton: false,
            timer: 2000,
          });
          console.error(`Comment deletion error:`, e);
        }
      }
    },
    highlightKeyword(text) {
      if (text == null) {
        return "";
      }
      const escaped = String(text)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
      const keyword = this.evalustionsQueryDto.content;
      if (keyword) {
        const regex = new RegExp(
          keyword.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"),
          "gi",
        );
        return escaped.replace(
          regex,
          '<span class="admin-search-highlight">$&</span>',
        );
      }
      return escaped;
    },
    async fetchFreshData() {
      try {
        let startTime = "";
        let endTime = "";
        if (this.searchTime !== null && this.searchTime.length === 2) {
          const [startDate, endDate] = await Promise.all(
            this.searchTime.map((date) => date.toISOString()),
          );
          startTime = `${startDate.split("T")[0]}T00:00:00`;
          endTime = `${endDate.split("T")[0]}T23:59:59`;
        }

        const params = {
          current: this.currentPage,
          size: this.pageSize,
          startTime: startTime,
          endTime: endTime,
          ...this.evalustionsQueryDto,
        };

        const response = await this.$axios.post("/evaluations/query", params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Fetch data failed:", error);
      }
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.evalustionsQueryDto = {};
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
    async handleDelete(row) {
      this.selectedRows = [row];
      await this.batchDelete();
    },
  },
};
</script>
