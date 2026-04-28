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
          start-placeholder="Registration Start"
          end-placeholder="Registration End"
        >
        </el-date-picker>
        <el-input
          size="small"
          style="width: 188px; margin-left: 5px; margin-right: 6px"
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
        <span style="float: right">
          <el-button
            size="small"
            style="
              background-color: rgb(96, 98, 102);
              color: rgb(247, 248, 249);
              border: none;
            "
            class="customer"
            type="info"
            @click="add()"
            ><i class="el-icon-plus"></i>Add User</el-button
          >
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 20px; border-top: 1px solid rgb(245, 245, 245)">
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
              style="margin-right: 5px"
              class="el-icon-warning"
            ></i>
            <i
              v-else
              style="margin-right: 5px; color: rgb(253, 199, 50)"
              class="el-icon-success"
            ></i>
            <el-tooltip
              v-if="scope.row.isLogin"
              class="item"
              effect="dark"
              content="Once suspended, the user cannot log in. Only an admin can restore access."
              placement="bottom-end"
            >
              <span
                style="
                  text-decoration: underline;
                  text-decoration-style: dashed;
                "
                >Suspended</span
              >
            </el-tooltip>
            <span v-else>Active</span>
          </template>
        </el-table-column>
        <el-table-column prop="isWord" width="108" label="Mute">
          <template slot-scope="scope">
            <i
              v-if="scope.row.isWord"
              style="margin-right: 5px"
              class="el-icon-warning"
            ></i>
            <i
              v-else
              style="margin-right: 5px; color: rgb(253, 199, 50)"
              class="el-icon-success"
            ></i>
            <el-tooltip
              v-if="scope.row.isWord"
              class="item"
              effect="dark"
              content="Once muted, the user cannot comment. Only an admin can unmute them."
              placement="bottom-end"
            >
              <span
                style="
                  text-decoration: underline;
                  text-decoration-style: dashed;
                "
                >Muted</span
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
      <el-pagination
        style="margin: 10px 0"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      ></el-pagination>
    </el-row>
    <!-- Operation Panel -->
    <el-dialog
      :show-close="false"
      :visible.sync="dialogUserOperation"
      width="25%"
    >
      <div slot="title">
        <p class="dialog-title">
          {{ !isOperation ? "Add User" : "Edit User Information" }}
        </p>
      </div>
      <div style="padding: 0 20px">
        <el-row>
          <el-upload
            class="avatar-uploader"
            action="/api/personal-health/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="userAvatar" :src="userAvatar" class="dialog-avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row>
          <span class="dialog-hover">Username</span>
          <input
            class="dialog-input"
            v-model="data.userName"
            placeholder="Username"
          />
          <span class="dialog-hover">Account</span>
          <input
            class="dialog-input"
            v-model="data.userAccount"
            placeholder="Account"
          />
          <span class="dialog-hover">Email</span>
          <input
            class="dialog-input"
            v-model="data.userEmail"
            placeholder="Email"
          />
          <span class="dialog-hover">Password</span>
          <input
            class="dialog-input"
            v-model="userPwd"
            type="password"
            placeholder="Password"
          />
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          size="small"
          v-if="!isOperation"
          style="
            background-color: rgb(96, 98, 102);
            color: rgb(247, 248, 249);
            border: none;
          "
          class="customer"
          type="info"
          @click="addOperation()"
          >Add</el-button
        >
        <el-button
          size="small"
          v-else
          style="
            background-color: rgb(96, 98, 102);
            color: rgb(247, 248, 249);
            border: none;
          "
          class="customer"
          type="info"
          @click="updateOperation()"
          >Update</el-button
        >
        <el-button
          class="customer"
          size="small"
          style="background-color: rgb(211, 241, 241); border: none"
          @click="cancel"
          >Cancel</el-button
        >
      </span>
    </el-dialog>
    <el-dialog
      :show-close="false"
      :visible.sync="dialogStatusOperation"
      width="25%"
    >
      <div slot="title">
        <p class="dialog-title">Account Status</p>
      </div>
      <div style="padding: 0 20px">
        <el-row>
          <el-switch
            active-color="rgb(230, 62, 49)"
            inactive-color="rgb(246,246,246)"
            v-model="data.isLogin"
            active-text="Suspend"
            inactive-text="Active"
          >
          </el-switch>
        </el-row>
        <el-row style="margin: 20px 0">
          <el-switch
            active-color="rgb(230, 62, 49)"
            inactive-color="rgb(246,246,246)"
            v-model="data.isWord"
            active-text="Mute"
            inactive-text="Active"
          >
          </el-switch>
        </el-row>
        <span class="dialog-hover">Set as Admin</span>
        <el-switch
          v-model="roleStatus"
          active-color="rgb(230, 62, 49)"
          inactive-color="rgb(246,246,246)"
        >
        </el-switch>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          size="small"
          style="
            background-color: rgb(96, 98, 102);
            color: rgb(247, 248, 249);
            border: none;
          "
          class="customer"
          type="info"
          @click="confirmStatus"
          >Confirm</el-button
        >
        <el-button
          class="customer"
          size="small"
          style="background-color: rgb(241, 241, 241); border: none"
          @click="cancel"
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
<style scoped lang="scss">
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
  /* Center the buttons */
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Adjust button spacing */
.customer {
  margin: 0 8px;
}
</style>
