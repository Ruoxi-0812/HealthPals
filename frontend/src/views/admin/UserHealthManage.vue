<template>
  <div class="admin-page">
    <div class="admin-page__toolbar">
      <div class="admin-toolbar-row">
        <el-date-picker
          size="small"
          style="width: 220px"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Record Start"
          end-placeholder="Record End"
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
      </div>
    </div>

    <div class="admin-page__body">
      <el-table
        row-key="id"
        @selection-change="handleSelectionChange"
        :data="tableData"
      >
        <el-table-column prop="name" width="88" label="Status">
          <template slot-scope="scope">
            <i
              v-if="!statusCheck(scope.row)"
              class="el-icon-warning admin-status-ic"
            ></i>
            <i
              v-else
              class="el-icon-success admin-status-ic admin-status-ic--ok"
            ></i>
            <el-tooltip
              v-if="!statusCheck(scope.row)"
              class="item"
              effect="dark"
              content="Abnormal indicators, remind user to handle them promptly"
              placement="bottom-end"
            >
              <span class="admin-status-link">Abnormal</span>
            </el-tooltip>
            <span v-else>Normal</span>
          </template>
        </el-table-column>
        <el-table-column prop="value" width="148" label="Record Value" sortable>
          <template slot-scope="scope">
            <span>{{ scope.row.value }}({{ scope.row.unit }})</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="Recorded By"></el-table-column>
        <el-table-column
          prop="valueRange"
          width="88"
          label="Threshold"
        ></el-table-column>
        <el-table-column prop="name" width="140" label="Model Name">
          <template slot-scope="scope">
            <span
              ><i class="el-icon-receiving" style="margin-right: 3px"></i
              >{{ scope.row.name }}</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="unit" width="88" label="Unit"></el-table-column>
        <el-table-column
          prop="symbol"
          width="88"
          label="Symbol"
        ></el-table-column>
        <el-table-column
          prop="userId"
          width="108"
          label="User ID"
          sortable
        ></el-table-column>
        <el-table-column
          prop="createTime"
          width="178"
          label="Record Time"
          sortable
        ></el-table-column>
        <el-table-column label="Actions" width="80">
          <template slot-scope="scope">
            <span class="text-button" @click="handleDelete(scope.row)"
              >Delete</span
            >
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
    </div>
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
export default {
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
    dialogUserOperaion(v1, v2) {
      if (!v1) {
        this.isOperation = !this.isOperation;
      }
      if (!v1 && v2) {
        this.data = {};
      }
    },
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    // Check if the user's input value is within the normal range and assign a status
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
      }
    },
    add() {
      this.dialogUserOperaion = true;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.filterText = "";
      this.handleFilter();
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
      this.dialogUserOperaion = true;
      this.isOperation = true;
this.data = { ...row };
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
  },
};
</script>
