<template>
  <div>
    <AdminPageShell page-class="admin-page--health-records">
      <template #toolbar>
<div class="admin-toolbar-row">
        <el-date-picker
          size="small"
          class="admin-date-picker"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Recorded from"
          end-placeholder="Recorded to"
        >
        </el-date-picker>
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="userHealthQueryDto.userId"
          placeholder="User ID"
          clearable
          @clear="handleFilterClear"
        >
          <el-button
            slot="append"
            @click="handleFilter"
            icon="el-icon-search"
          ></el-button>
        </el-input>
        <div class="admin-page__toolbar-actions">
          <el-tooltip content="Add record" placement="bottom">
            <button
              type="button"
              class="admin-toolbar-btn admin-toolbar-btn--primary"
              aria-label="Add record"
              @click="add()"
            >
              <i class="el-icon-plus" aria-hidden="true" />
              <span>Add record</span>
            </button>
          </el-tooltip>
        </div>
      </div>
      </template>
<el-table
        stripe
        v-loading="tableLoading"
        row-key="id"
        :row-class-name="rowClassName"
        @selection-change="handleSelectionChange"
        :data="tableData"
        class="admin-table-full"
        :empty-text="tableLoading ? ' ' : 'No health records match your filters.'"
      >
        <el-table-column
          prop="value"
          min-width="380"
          label="Record"
          sortable
          class-name="admin-col-reading"
        >
          <template slot-scope="scope">
            <div class="admin-health-record-cell">
              <div class="admin-health-record-cell__top">
                <el-tooltip
                  v-if="!statusCheck(scope.row)"
                  effect="dark"
                  content="Out of range — user should review this reading."
                  placement="top"
                >
                  <span class="admin-badge admin-badge--warn admin-badge--nowrap">
                    <i class="el-icon-warning-outline" aria-hidden="true" />
                    Abnormal
                  </span>
                </el-tooltip>
                <span
                  v-else
                  class="admin-badge admin-badge--ok admin-badge--nowrap"
                >
                  <i class="el-icon-circle-check" aria-hidden="true" />
                  Normal
                </span>
                <span class="admin-health-value">
                  <strong>{{ scope.row.value }}</strong>
                  <span
                    v-if="scope.row.unit"
                    class="admin-health-value__unit"
                    >{{ scope.row.unit }}</span
                  >
                </span>
              </div>
              <span class="admin-tag-cell admin-tag-cell--subtle">
                <i
                  class="el-icon-data-line admin-tag-cell__icon"
                  aria-hidden="true"
                />
                <span class="admin-tag-cell__name">{{ scope.row.name }}</span>
              </span>
              <p
                v-if="scope.row.valueRange"
                class="admin-health-record-cell__threshold"
              >
                Range {{ scope.row.valueRange }}
              </p>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="userName"
          width="170"
          label="User"
          show-overflow-tooltip
          class-name="admin-col-user-cell"
        >
          <template slot-scope="scope">
            <div class="admin-health-user">
              <span class="admin-health-user__name">{{
                scope.row.userName || "—"
              }}</span>
              <span v-if="scope.row.userId" class="admin-health-user__id"
                >#{{ scope.row.userId }}</span
              >
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          width="128"
          label="Recorded"
          sortable
          show-overflow-tooltip
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
              <el-tooltip content="Edit record" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--primary"
                  aria-label="Edit record"
                  @click="handleEdit(scope.row)"
                >
                  <i class="el-icon-edit" aria-hidden="true" />
                </button>
              </el-tooltip>
              <el-tooltip content="Delete record" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--danger"
                  aria-label="Delete record"
                  @click="handleDelete(scope.row)"
                >
                  <i class="el-icon-delete" aria-hidden="true" />
                </button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="admin-pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      ></el-pagination>
    </AdminPageShell>

    <el-dialog
      custom-class="hp-dialog admin-dialog-wide"
      :show-close="true"
      append-to-body
      :visible.sync="dialogUserOperaion"
      width="480px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Health records</span>
        <h2 class="hp-dialog__title">{{ !isOperation ? "Add record" : "Edit record" }}</h2>
      </div>
      <div class="hp-dialog__body admin-form-stack">
        <label class="hp-field"><span class="hp-field__label">User ID</span><input v-model.number="data.userId" class="hp-field__input" type="number" placeholder="User id" /></label>
        <label class="hp-field"><span class="hp-field__label">Health model ID</span><input v-model.number="data.healthModelConfigId" class="hp-field__input" type="number" placeholder="Model config id" /></label>
        <label class="hp-field"><span class="hp-field__label">Recorded value</span><input v-model="data.value" class="hp-field__input" type="text" placeholder="Measurement value" /></label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button type="button" class="hp-dialog__btn hp-dialog__btn--ghost" @click="closeDialog">Cancel</button>
        <button v-if="!isOperation" type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="addOperation">Add</button>
        <button v-else type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="updateOperation">Save</button>
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
      dialogUserOperaion: false, // Toggle
      isOperation: false, // Toggle - Indicates whether adding or modifying
      tableData: [],
      tableLoading: true,
      searchTime: [],
      selectedRows: [],
      status: null,
      userHealthQueryDto: {}, // Search conditions
      messsageContent: "",
      tagsList: [],
      valuesRange: [10, 50],
    };
  },
  watch: {
    dialogUserOperaion(open) {
      if (!open) {
        this.isOperation = false;
        this.data = {};
      }
    },
  },
  created() {
    this.fetchFreshData();
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
    rowClassName({ row }) {
      return this.statusCheck(row) ? "" : "admin-health-row--abnormal";
    },
    statusCheck(data) {
      // User input value
      const inputValue = data.value;
      // Normal value range
      const valueRange = data.valueRange;
      if (valueRange !== null && inputValue !== null) {
        const aryValueRange = valueRange.split(",");
        const minValue = aryValueRange[0];
        const maxValue = aryValueRange[1];
        return (
          Number(inputValue) > Number(minValue) &&
          Number(inputValue) < Number(maxValue)
        );
      }
    },
    handleAvatarSuccess(res, file) {
      if (res.code !== 200) {
        this.$message.error(`Error uploading health record cover`);
        return;
      }
      this.$message.success(`Health record cover uploaded successfully`);
      this.data.cover = res.data;
    },
    // Handle multi-selection
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    // Batch delete data
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete user health records",
        text: `This action cannot be undone, do you want to continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(
            `/user-health/batchDelete`,
            ids,
          );
          if (response.data.code === 200) {
            this.$swal.fire({
              title: "Delete notification",
              text: response.data.msg,
              icon: "success",
              showConfirmButton: false,
              timer: 2000,
            });
            this.fetchFreshData();
            return;
          }
        } catch (e) {
          this.$swal.fire({
            title: "Error notification",
            text: e,
            icon: "error",
            showConfirmButton: false,
            timer: 2000,
          });
          console.error(`Error deleting user health records:`, e);
        }
      }
    },
    resetQueryCondition() {
      this.userHealthQueryDto = {};
      this.searchTime = [];
      this.fetchFreshData();
    },
    // Update record
    async updateOperation() {
      try {
const response = await this.$axios.put(
          "/user-health/update",
          this.data,
        );
        this.$swal.fire({
          title: "User health record updated",
          text: response.data.msg,
          icon: response.data.code === 200 ? "success" : "error",
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Submission failed, please try again later!");
      }
    },
    // Add new record
    async addOperation() {
      try {
        // Convert range array to a comma-separated string
const response = await this.$axios.post("/user-health/save", this.data);
        this.$message[response.data.code === 200 ? "success" : "error"](
          response.data.msg,
        );
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Submission failed, please try again later!");
      }
    },
    closeDialog() {
      this.dialogUserOperaion = false;
    },
    clearFormData() {
      this.data = {};
    },
    async fetchFreshData() {
      this.tableLoading = true;
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
        // Request parameters
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          startTime: startTime,
          endTime: endTime,
          ...this.userHealthQueryDto,
        };
        const response = await this.$axios.post("/user-health/query", params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Error fetching user health records:", error);
      } finally {
        this.tableLoading = false;
      }
    },
    add() {
      this.isOperation = false;
      this.data = {};
      this.dialogUserOperaion = true;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.userHealthQueryDto = {};
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
    handleEdit(row) {
      this.isOperation = true;
      this.data = { ...row };
      this.dialogUserOperaion = true;
    },
    handleDelete(row) {
      this.selectedRows = [row];
      this.batchDelete();
    },
  },
};
</script>
