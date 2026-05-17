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
          start-placeholder="Registration Start"
          end-placeholder="Registration End"
        >
        </el-date-picker>
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="userQueryDto.userName"
          placeholder="Username"
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
          <el-button type="primary" size="small"
            @click="add()"
            ><i class="el-icon-plus"></i>Add User</el-button
          >
        </div>
      </div>
    </div>

    <div class="admin-page__body">
      <el-table
        @selection-change="handleSelectionChange"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="userAvatar" width="68" label="Avatar">
          <template slot-scope="scope">
            <el-avatar
              :size="25"
              :src="scope.row.userAvatar"
              style="margin-top: 10px"
            ></el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="Name"></el-table-column>
        <el-table-column
          prop="userAccount"
          width="128"
          label="Account"
        ></el-table-column>
        <el-table-column
          prop="userEmail"
          width="168"
          label="Email"
        ></el-table-column>
        <el-table-column prop="userRole" width="68" label="Role">
          <template slot-scope="scope">
            <span>{{ scope.row.userRole === 1 ? "Admin" : "User" }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="isLogin" width="108" label="Suspension">
          <template slot-scope="scope">
            <i
              v-if="scope.row.isLogin"
              class="el-icon-warning admin-status-ic"
            ></i>
            <i
              v-else
              class="el-icon-success admin-status-ic admin-status-ic--ok"
            ></i>
            <el-tooltip
              v-if="scope.row.isLogin"
              class="item"
              effect="dark"
              content="Once suspended, the user cannot log in. Only an admin can restore access."
              placement="bottom-end"
            >
              <span class="admin-status-link">Suspended</span
              >
            </el-tooltip>
            <span v-else>Active</span>
          </template>
        </el-table-column>
        <el-table-column prop="isWord" width="108" label="Mute">
          <template slot-scope="scope">
            <i
              v-if="scope.row.isWord"
              class="el-icon-warning admin-status-ic"
            ></i>
            <i
              v-else
              class="el-icon-success admin-status-ic admin-status-ic--ok"
            ></i>
            <el-tooltip
              v-if="scope.row.isWord"
              class="item"
              effect="dark"
              content="Once muted, the user cannot comment. Only an admin can unmute them."
              placement="bottom-end"
            >
              <span class="admin-status-link">Muted</span
              >
            </el-tooltip>
            <span v-else>Active</span>
          </template>
        </el-table-column>
        <el-table-column
          :sortable="true"
          prop="createTime"
          width="168"
          label="Registered On"
        ></el-table-column>
        <el-table-column label="Actions" width="170">
          <template slot-scope="scope">
            <span class="text-button" @click="handleStatus(scope.row)"
              >Account Status</span
            >
            <span class="text-button" @click="handleEdit(scope.row)">Edit</span>
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
      :visible.sync="dialogUserOperation"
      width="480px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Users</span>
        <h2 class="hp-dialog__title">
          {{ !isOperation ? "Add user" : "Edit user" }}
        </h2>
      </div>
      <div class="hp-dialog__body admin-form-stack">
        <label class="hp-field">
          <span class="hp-field__label">Avatar</span>
          <el-upload
            class="hp-dialog__avatar-uploader avatar-uploader"
            action="/api/personal-heath/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="userAvatar" :src="userAvatar" class="hp-dialog__avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Username</span>
          <input v-model="data.userName" class="hp-field__input" type="text" placeholder="Display name" />
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Account</span>
          <input v-model="data.userAccount" class="hp-field__input" type="text" placeholder="Login account" :disabled="isOperation" />
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Email</span>
          <input v-model="data.userEmail" class="hp-field__input" type="email" placeholder="Email address" />
        </label>
        <label v-if="!isOperation" class="hp-field">
          <span class="hp-field__label">Password</span>
          <input v-model="userPwd" class="hp-field__input" type="password" placeholder="At least 6 characters" />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button type="button" class="hp-dialog__btn hp-dialog__btn--ghost" @click="cancel">Cancel</button>
        <button v-if="!isOperation" type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="addOperation()">Add user</button>
        <button v-else type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="updateOperation()">Save</button>
      </div>
    </el-dialog>
    <el-dialog custom-class="hp-dialog" :show-close="true" append-to-body :visible.sync="dialogStatusOperation" width="440px">
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Users</span>
        <h2 class="hp-dialog__title">Account status</h2>
      </div>
      <div class="hp-dialog__body admin-form-stack">
        <div class="admin-switch-row">
          <span class="admin-switch-row__label">Suspend login</span>
          <el-switch v-model="data.isLogin" active-color="#2a9d6f" inactive-color="#e0e0e0" active-text="Yes" inactive-text="No" />
        </div>
        <div class="admin-switch-row">
          <span class="admin-switch-row__label">Mute comments</span>
          <el-switch v-model="data.isWord" active-color="#2a9d6f" inactive-color="#e0e0e0" active-text="Yes" inactive-text="No" />
        </div>
        <div class="admin-switch-row">
          <span class="admin-switch-row__label">Administrator role</span>
          <el-switch v-model="roleStatus" active-color="#2a9d6f" inactive-color="#e0e0e0" active-text="Admin" inactive-text="User" />
        </div>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button type="button" class="hp-dialog__btn hp-dialog__btn--ghost" @click="cancel">Cancel</button>
        <button type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="confirmStatus">Confirm</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      roleStatus: false,
      userPwd: "",
      userAvatar: "",
      data: {},
      filterText: "",
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogStatusOperation: false,
      dialogUserOperation: false, // Toggle switch
      isOperation: false, // Toggle switch to identify add or update
      tableData: [],
      searchTime: [],
      selectedRows: [],
      status: null,
      userQueryDto: {}, // Search conditions
      messageContent: "",
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    confirmStatus() {
      const userUpdateDto = {
        id: this.data.id,
        isLogin: this.data.isLogin,
        isWord: this.data.isWord,
        userRole: this.roleStatus ? 1 : 2,
      };
      this.$axios
        .put(`/user/backUpdate`, userUpdateDto)
        .then((res) => {
          if (res.data.code === 200) {
            this.$notify({
              duration: 2000,
              title: "Operation Feedback",
              message: "Operation Successful",
              type: "success",
            });
            this.dialogStatusOperation = false;
            this.fetchFreshData();
          }
        })
        .catch((error) => {
          console.log("Error updating status: " + error);
        });
    },
    handleStatus(data) {
      this.dialogStatusOperation = true;
      this.roleStatus = data.userRole === 1;
      this.data = data;
    },
    handleAvatarSuccess(res, file) {
      if (res.code !== 200) {
        this.$notify({
          duration: 2000,
          title: "Avatar Upload",
          message: "Failed",
          type: "error",
        });
        return;
      }
      this.$notify({
        duration: 2000,
        title: "Avatar Upload",
        message: "Successful",
        type: "success",
      });
      this.userAvatar = res.data;
    },
    switchChange() {
      this.fetchFreshData();
    },
    async handleSwitchChange(id, status, operation) {
      try {
        let param = { id: id };
        // Login status
        if (operation) {
          param.isLogin = status;
        } else {
          // Comment status
          param.isWord = status;
        }
        const response = await this.$axios.put(`/user/backUpdate`, param);
        if (response.data.code === 200) {
          this.$notify({
            duration: 2000,
            title: "Operation Notice",
            message: "Successful",
            type: "success",
          });
          this.cancel();
        }
      } catch (e) {
        console.error(`Error updating user status: ${e}`);
      }
    },
    // Selection in multi-select checkbox
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
        title: "Delete User Data",
        text: `This action cannot be undone. Continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(`/user/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$notify({
              duration: 2000,
              title: "Delete Operation",
              message: "Successful",
              type: "success",
            });
            this.cancel();
            this.fetchFreshData();
            return;
          }
        } catch (e) {
          console.error(`Error deleting user data: `, e);
        }
      }
    },
    resetQueryCondition() {
      this.userQueryDto = {};
      this.searchTime = [];
      this.fetchFreshData();
    },
    // Update information
    async updateOperation() {
      if (this.userPwd !== "") {
        const pwd = this.$md5(this.$md5(this.userPwd));
        this.data.userPwd = pwd;
      } else {
        this.data.userPwd = null;
      }
      this.data.userAvatar = this.userAvatar;
      try {
        const response = await this.$axios.put("/user/backUpdate", this.data);
        if (response.data.code === 200) {
          this.fetchFreshData();
          this.cancel();
          this.$notify({
            duration: 2000,
            title: "Update Operation",
            message: "Successful",
            type: "success",
          });
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Submission failed. Please try again later.");
      }
    },
    // Add information
    async addOperation() {
      if (this.userPwd !== "") {
        this.data.userPwd = this.$md5(this.$md5(this.userPwd));
      } else {
        this.data.userPwd = null;
      }
      this.data.userAvatar = this.userAvatar;
      try {
        const response = await this.$axios.post("/user/insert", this.data);
        this.$message[response.data.code === 200 ? "success" : "error"](
          response.data.msg,
        );
        if (response.data.code === 200) {
          this.fetchFreshData();
          this.cancel();
          this.$notify({
            duration: 2000,
            title: "Add Operation",
            message: "Successful",
            type: "success",
          });
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Submission failed. Please try again later.");
      }
    },
    cancel() {
      this.userAvatar = "";
      this.userPwd = "";
      this.data = {};
      this.isOperation = false;
      this.dialogStatusOperation = false;
      this.dialogUserOperation = false;
    },
    async fetchFreshData() {
      try {
        this.tableData = [];
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
          key: this.filterText,
          startTime: startTime,
          endTime: endTime,
          ...this.userQueryDto,
        };
        const response = await this.$axios.post("/user/query", params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Error fetching user data:", error);
      }
    },
    add() {
      this.dialogUserOperation = true;
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
      this.dialogUserOperation = true;
      this.isOperation = true;
      row.userPwd = null;
      this.userAvatar = row.userAvatar;
      this.data = { ...row };
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
  },
};
</script>
