<template>
  <el-row style="background-color: #ffffff; padding: 5px 0; border-radius: 5px">
    <el-row style="padding: 10px; margin-left: 10px">
      <el-row>
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
          style="width: 188px; margin-left: 5px; margin-right: 6px"
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
      </el-row>
    </el-row>
    <el-row style="margin: 0 20px; border-top: 1px solid rgb(245, 245, 245)">
      <el-table
        row-key="id"
        @selection-change="handleSelectionChange"
        :data="tableData"
      >
        <el-table-column prop="name" width="88" label="Status">
          <template slot-scope="scope">
            <i
              v-if="!statusCheck(scope.row)"
              style="margin-right: 5px"
              class="el-icon-warning"
            ></i>
            <i
              v-else
              style="margin-right: 5px; color: rgb(253, 199, 50)"
              class="el-icon-success"
            ></i>
            <el-tooltip
              v-if="!statusCheck(scope.row)"
              class="item"
              effect="dark"
              content="Abnormal indicators, remind user to handle them promptly"
              placement="bottom-end"
            >
              <span
                style="
                  text-decoration: underline;
                  text-decoration-style: dashed;
                "
                >Abnormal</span
              >
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
      <el-pagination
        style="margin: 20px 0"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      ></el-pagination>
    </el-row>
    <el-dialog
      :show-close="false"
      :visible.sync="dialogUserOperaion"
      width="26%"
    >
      <div slot="title">
        <p class="dialog-title">
          {{
            !isOperation ? "Add User Health Record" : "Edit User Health Record"
          }}
        </p>
      </div>
      <div style="padding: 0 20px">
        <!-- Icon -->
        <el-row style="margin-top: 20px">
          <el-upload
            class="avatar-uploader"
            action="/api/personal-heath/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img
              v-if="data.cover"
              :src="data.cover"
              style="height: 44px; width: 44px"
            />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <!-- Configuration Name -->
        <el-row style="padding: 0 20px 0 0">
          <p>
            <span class="modelName">*Configuration Name</span>
          </p>
          <input
            class="input-title"
            v-model="data.name"
            style="border-radius: 5px; background-color: #f1f1f1"
          />
        </el-row>
        <!-- Unit -->
        <el-row style="padding: 0 20px 0 0">
          <p style="font-size: 12px; padding: 3px 0">
            <span class="modelName">*Unit</span>
          </p>
          <input
            class="input-title"
            v-model="data.unit"
            style="border-radius: 5px; background-color: #f1f1f1"
          />
        </el-row>
        <!-- Symbol -->
        <el-row style="padding: 0 20px 0 0">
          <p style="font-size: 12px; padding: 3px 0">
            <span class="modelName">*Symbol</span>
          </p>
          <input
            class="input-title"
            v-model="data.symbol"
            style="border-radius: 5px; background-color: #f1f1f1"
          />
        </el-row>
        <!-- Description -->
        <el-row style="padding: 0 20px 0 0">
          <p style="font-size: 12px; padding: 3px 0">
            <span class="modelName">*Description</span>
          </p>
          <el-input
            style="border-radius: 5px; background-color: #f1f1f1"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3 }"
            placeholder="Description"
            v-model="data.detail"
          >
          </el-input>
        </el-row>
        <!-- Normal Value Range -->
        <el-row style="padding: 0 20px 0 0">
          <p style="font-size: 12px; padding: 3px 0">
            <span class="modelName">*Normal Value Range</span>
          </p>
          <el-slider v-model="valuesRange" range show-stops :max="1000">
          </el-slider>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          size="small"
          v-if="!isOperation"
          style="background-color: rgb(43, 121, 203); border: none"
          class="customer"
          type="info"
          @click="addOperation"
          >Add</el-button
        >
        <el-button
          size="small"
          v-else
          style="background-color: rgb(43, 121, 203); border: none"
          class="customer"
          type="info"
          @click="updateOperation"
          >Update</el-button
        >
        <el-button
          class="customer"
          size="small"
          style="background-color: rgb(241, 241, 241); border: none"
          @click="dialogUserOperaion = false"
          >Cancel</el-button
        >
      </span>
    </el-dialog>
  </el-row>
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
        this.data.valueRange = this.valuesRange.join(",");
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
        this.data.valueRange = this.valuesRange.join(",");
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
      if (row.valueRange !== null) {
        this.valuesRange = row.valueRange.split(",");
      }
      this.data = { ...row };
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
  },
};
</script>

<style scoped lang="scss">
.status-success {
  display: inline-block;
  padding: 1px 5px;
  border-radius: 2px;
  background-color: rgb(201, 237, 249);
  color: rgb(111, 106, 196);
  font-size: 12px;
}

.status-error {
  display: inline-block;
  padding: 1px 5px;
  border-radius: 2px;
  background-color: rgb(233, 226, 134);
  color: rgb(131, 138, 142);
  color: rgb(111, 106, 196);
  font-size: 12px;
}

.tag-tip {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 5px;
  background-color: rgb(245, 245, 245);
  color: rgb(104, 118, 130);
}

.input-def {
  height: 40px;
  line-height: 40px;
  outline: none;
  border: none;
  font-size: 20px;
  color: rgb(102, 102, 102);
  font-weight: 900;
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  align-items: center;
}

.customer {
  margin: 0 8px;
}
</style>
