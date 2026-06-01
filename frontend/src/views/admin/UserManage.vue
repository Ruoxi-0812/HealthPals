<template>
  <div>
    <AdminPageShell page-class="admin-page--users">
      <template #toolbar>
<div class="admin-toolbar-row">
        <el-date-picker
          size="small"
          class="admin-date-picker"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Registered from"
          end-placeholder="Registered to"
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
          <el-tooltip content="Add user" placement="bottom">
            <button
              type="button"
              class="admin-toolbar-btn admin-toolbar-btn--primary"
              aria-label="Add user"
              @click="add()"
            >
              <i class="el-icon-plus" aria-hidden="true" />
              <span>Add user</span>
            </button>
          </el-tooltip>
        </div>
      </div>
      </template>
<el-table
        stripe
        v-loading="tableLoading"
        @selection-change="handleSelectionChange"
        :data="tableData"
        class="admin-table-full"
        :empty-text="tableLoading ? ' ' : 'No users match your filters.'"
      >
        <el-table-column prop="userAvatar" width="76" label="Avatar">
          <template slot-scope="scope">
            <UserAvatar
              :size="36"
              class="admin-user-avatar"
              :src="scope.row.userAvatar"
            />
          </template>
        </el-table-column>
        <el-table-column prop="userName" min-width="120" label="Name">
          <template slot-scope="scope">
            <span class="admin-user-cell-name">{{ scope.row.userName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userAccount" width="120" label="Account">
          <template slot-scope="scope">
            <span class="admin-user-cell-account">{{
              scope.row.userAccount
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="userEmail"
          min-width="200"
          label="Email"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <span>{{ scope.row.userEmail || "—" }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="userRole"
          width="88"
          label="Role"
          align="center"
          header-align="center"
          class-name="admin-col-center"
        >
          <template slot-scope="scope">
            <span
              class="admin-badge"
              :class="
                scope.row.userRole === 1
                  ? 'admin-badge--role-admin'
                  : 'admin-badge--role-user'
              "
            >
              {{ scope.row.userRole === 1 ? "Admin" : "User" }}
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="isLogin"
          width="118"
          label="Login"
          align="center"
          header-align="center"
          class-name="admin-col-center"
        >
          <template slot-scope="scope">
            <el-tooltip
              v-if="scope.row.isLogin"
              effect="dark"
              content="Suspended — user cannot sign in until an admin restores access."
              placement="top"
            >
              <span class="admin-badge admin-badge--warn">
                <i class="el-icon-lock" aria-hidden="true" />
                Suspended
              </span>
            </el-tooltip>
            <span v-else class="admin-badge admin-badge--ok">
              <i class="el-icon-circle-check" aria-hidden="true" />
              Active
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="isWord"
          width="118"
          label="Posting"
          align="center"
          header-align="center"
          class-name="admin-col-center"
        >
          <template slot-scope="scope">
            <el-tooltip
              v-if="scope.row.isWord"
              effect="dark"
              content="Muted — user cannot post comments until an admin unmute them."
              placement="top"
            >
              <span class="admin-badge admin-badge--warn">
                <i class="el-icon-microphone" aria-hidden="true" />
                Muted
              </span>
            </el-tooltip>
            <span v-else class="admin-badge admin-badge--ok">
              <i class="el-icon-chat-dot-round" aria-hidden="true" />
              Allowed
            </span>
          </template>
        </el-table-column>
        <el-table-column
          :sortable="true"
          prop="createTime"
          width="128"
          label="Registered"
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
          width="172"
          align="center"
          header-align="center"
          class-name="admin-col-actions"
        >
          <template slot-scope="scope">
            <div class="admin-row-actions">
              <el-tooltip content="Account status" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon"
                  aria-label="Account status"
                  @click="handleStatus(scope.row)"
                >
                  <i class="el-icon-s-operation" aria-hidden="true" />
                </button>
              </el-tooltip>
              <el-tooltip content="Edit user" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--primary"
                  aria-label="Edit user"
                  @click="handleEdit(scope.row)"
                >
                  <i class="el-icon-edit" aria-hidden="true" />
                </button>
              </el-tooltip>
              <el-tooltip content="Delete user" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--danger"
                  aria-label="Delete user"
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
      ></el-pagination>
    </AdminPageShell>

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
            <img
              v-if="userAvatar"
              :src="userAvatar"
              class="hp-dialog__avatar"
            />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Username</span>
          <input
            v-model="data.userName"
            class="hp-field__input"
            type="text"
            placeholder="Display name"
          />
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Account</span>
          <input
            v-model="data.userAccount"
            class="hp-field__input"
            type="text"
            placeholder="Login account"
            :disabled="isOperation"
          />
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Email</span>
          <input
            v-model="data.userEmail"
            class="hp-field__input"
            type="email"
            placeholder="Email address"
          />
        </label>
        <label v-if="!isOperation" class="hp-field">
          <span class="hp-field__label">Password</span>
          <input
            v-model="userPwd"
            class="hp-field__input"
            type="password"
            placeholder="At least 6 characters"
          />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--ghost"
          @click="cancel"
        >
          Cancel
        </button>
        <button
          v-if="!isOperation"
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="addOperation()"
        >
          Add user
        </button>
        <button
          v-else
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="updateOperation()"
        >
          Save
        </button>
      </div>
    </el-dialog>
    <el-dialog
      custom-class="hp-dialog"
      :show-close="true"
      append-to-body
      :visible.sync="dialogStatusOperation"
      width="440px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Users</span>
        <h2 class="hp-dialog__title">Account status</h2>
      </div>
      <div class="hp-dialog__body admin-form-stack">
        <div class="admin-switch-row">
          <span class="admin-switch-row__label">Suspend login</span>
          <el-switch
            v-model="data.isLogin"
            active-color="#2a9d6f"
            inactive-color="#e0e0e0"
            active-text="Yes"
            inactive-text="No"
          />
        </div>
        <div class="admin-switch-row">
          <span class="admin-switch-row__label">Mute comments</span>
          <el-switch
            v-model="data.isWord"
            active-color="#2a9d6f"
            inactive-color="#e0e0e0"
            active-text="Yes"
            inactive-text="No"
          />
        </div>
        <div class="admin-switch-row">
          <span class="admin-switch-row__label">Administrator role</span>
          <el-switch
            v-model="roleStatus"
            active-color="#2a9d6f"
            inactive-color="#e0e0e0"
            active-text="Admin"
            inactive-text="User"
          />
        </div>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--ghost"
          @click="cancel"
        >
          Cancel
        </button>
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="confirmStatus"
        >
          Confirm
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AdminPageShell from "@/components/admin/AdminPageShell.vue";
import UserAvatar from "@/components/UserAvatar.vue";

import {
  formatDateShort,
  formatTimeShort,
  formatDateTimeFull,
} from "@/utils/data";

export default {
  components: { AdminPageShell, UserAvatar },
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
      dialogUserOperation: false,
      isOperation: false,
      tableData: [],
      tableLoading: true,
      searchTime: [],
      selectedRows: [],
      status: null,
      userQueryDto: {},
      messageContent: "",
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    formatDateShort,
    formatTimeShort,
    formatDateTimeFull,
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
        if (operation) {
          param.isLogin = status;
        } else {
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
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
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
      } finally {
        this.tableLoading = false;
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
