<template>
  <div class="user-container">
    <div class="menus-container">
      <!-- Menu Directory -->
      <UserMenu
        :menus="routers"
        :userInfo="userInfo"
        @eventListener="eventListener"
      />
    </div>
    <div class="content-container">
      <!-- Router Outlet -->
      <keep-alive include="HomePage">
        <router-view class="route-container" />
      </keep-alive>
    </div>
    <!-- Personal Center Dialog -->
    <el-dialog
      custom-class="hp-dialog"
      :show-close="true"
      :visible.sync="dialogOperaion"
      width="440px"
      append-to-body
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Profile</span>
        <h2 class="hp-dialog__title">Personal center</h2>
      </div>
      <div class="hp-dialog__body">
        <label class="hp-field">
          <span class="hp-field__label">Avatar</span>
          <el-upload
            class="hp-dialog__avatar-uploader avatar-uploader"
            action="/api/personal-heath/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="data.url" :src="data.url" class="hp-dialog__avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Username</span>
          <input
            v-model="data.name"
            class="hp-field__input"
            type="text"
            placeholder="Your display name"
            autocomplete="username"
          />
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Email</span>
          <input
            v-model="data.email"
            class="hp-field__input"
            type="email"
            placeholder="you@example.com"
            autocomplete="email"
          />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--ghost"
          @click="dialogOperaion = false"
        >
          Cancel
        </button>
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="updateUserInfo"
        >
          Save changes
        </button>
      </div>
    </el-dialog>
    <!-- Reset Password -->
    <el-dialog
      custom-class="hp-dialog"
      :show-close="true"
      :visible.sync="dialogRetPwdOperaion"
      width="440px"
      append-to-body
      :close-on-click-modal="false"
      @open="onResetPwdOpen"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Security</span>
        <h2 class="hp-dialog__title">Reset password</h2>
        <p class="hp-dialog__hint">
          You’ll sign in again after your password is updated.
        </p>
      </div>
      <div class="hp-dialog__body">
        <label class="hp-field">
          <span class="hp-field__label">Current password</span>
          <input
            v-model="pwdEntity.oldPwd"
            class="hp-field__input"
            type="password"
            placeholder="Enter current password"
            autocomplete="current-password"
          />
        </label>
        <label class="hp-field">
          <span class="hp-field__label">New password</span>
          <input
            v-model="pwdEntity.newPwd"
            class="hp-field__input"
            type="password"
            placeholder="At least 6 characters"
            autocomplete="new-password"
          />
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Confirm new password</span>
          <input
            v-model="pwdEntity.againPwd"
            class="hp-field__input"
            type="password"
            placeholder="Re-enter new password"
            autocomplete="new-password"
          />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--ghost"
          @click="dialogRetPwdOperaion = false"
        >
          Cancel
        </button>
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="updateUserPwd"
        >
          Update password
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { clearToken } from "@/utils/storage.js";
import router from "@/router";
import UserMenu from "@/components/LevelMenu.vue";

export default {
  name: "UserMain",
  components: {
    UserMenu,
  },
  data() {
    return {
      userInfo: {},
      data: {},
      pwdEntity: { oldPwd: "", newPwd: "", againPwd: "" },
      dialogOperaion: false,
      dialogRetPwdOperaion: false,
      foodList: [],
      routers: [],
      isCheckFood: [],
      isCheckHealthModelConfig: [],
      healthModelConfig: [],
      selecedFoodIndex: 0,
      selecedHealthModelIndex: 0,
      dietDialog: false,
      healthModelConfigDialog: false,
    };
  },
  created() {
    this.tokenCheckLoad();
  },
  methods: {
    healthModelChange() {
      const healthModel =
        this.healthModelConfig[this.selecedHealthModelIndex - 2];
      const exists = this.isCheckHealthModelConfig.some(
        (item) => item.id === healthModel.id,
      );
      if (!exists) {
        this.isCheckHealthModelConfig.unshift(healthModel);
      } else {
        console.log("Indicator already added");
      }
    },
    onResetPwdOpen() {
      this.pwdEntity = { oldPwd: "", newPwd: "", againPwd: "" };
    },
    updateUserPwd() {
      this.resetPwd();
    },
    async updateUserInfo() {
      try {
        const userUpdateDTO = {
          userAvatar: this.data.url,
          userName: this.data.name,
          userEmail: this.data.email,
        };
        const response = await this.$axios.put(`/user/update`, userUpdateDTO);
        const { data } = response;
        if (data.code === 200) {
          this.dialogOperaion = false;
          this.tokenCheckLoad();
          this.$swalToast({
            title: "Profile updated",
            text: data.msg,
            icon: "success",
            timer: 1000,
          });
        }
      } catch (e) {
        this.dialogOperaion = false;
        this.$swalToast({
          title: "Update failed",
          text: String(e),
          icon: "error",
          timer: 2000,
        });
        console.error(`Error updating personal information: ${e}`);
      }
    },
    async resetPwd() {
      try {
        const { oldPwd, newPwd, againPwd } = this.pwdEntity;
        if (!oldPwd || !newPwd || !againPwd) {
          this.$message(`No field can be empty`);
          return;
        }
        if (newPwd !== againPwd) {
          this.$message(`Passwords do not match`);
          return;
        }
        const pwdDTO = {
          oldPwd: this.$md5(this.$md5(oldPwd)),
          newPwd: this.$md5(this.$md5(newPwd)),
        };
        const response = await this.$axios.put(`/user/updatePwd`, pwdDTO);
        const { data } = response;
        if (data.code === 200) {
          this.dialogRetPwdOperaion = false;
          this.$swalToast({
            title: "Password updated",
            text: data.msg,
            icon: "success",
            timer: 1000,
          });
          setTimeout(() => {
            clearToken();
            this.$router.push(`/login`);
          }, 1200);
        } else {
          this.$message.error(data.msg);
        }
      } catch (e) {
        this.dialogOperaion = false;
        this.$error(data.msg);
        console.error(`Error updating password: ${e}`);
      }
    },
    handleAvatarSuccess(res, file) {
      if (res.code !== 200) {
        this.$message.error(`Avatar upload error`);
        return;
      }
      this.$message.success(`Avatar uploaded successfully`);
      this.data.url = res.data;
    },
    // Listen for menu click events
    eventListener(event) {
      // Personal Center
      if (event === "center") {
        this.dialogOperaion = !this.dialogOperaion;
        return;
      }
      // Reset Password
      if (event === "resetPwd") {
        this.dialogRetPwdOperaion = true;
        return;
      }
      // Search
      if (event === "search-detail") {
        this.$router.push("/search-detail");
        return;
      }
      // Log Out
      if (event === "loginOut") {
        this.loginOutOperation();
        return;
      }
      // Health Data Records
      if (event === "healthDataRecord") {
        this.$router.push("/record");
        return;
      }
    },
    async loginOutOperation() {
      const confirmed = await this.$swalLogout();
      if (confirmed) {
        clearToken();
        this.$router.push("/login");
      }
    },
    // Token validation
    async tokenCheckLoad() {
      try {
        const res = await this.$axios.get("user/auth");
        // Validation failed
        if (res.data.code === 400) {
          clearToken();
          this.$message.error(res.data.msg);
          this.$router.push("/login");
          return;
        }
        const {
          id: userId,
          userAvatar,
          userName,
          userRole,
          userEmail,
        } = res.data.data;
        // Store user information
        sessionStorage.setItem("userInfo", JSON.stringify(res.data.data));
        this.userInfo = {
          id: userId,
          url: userAvatar,
          name: userName,
          role: userRole,
          email: userEmail,
        };
        this.data = { ...this.userInfo };
        // Parse routes based on role
        const roleRouteKey = userRole === 1 ? "admin" : "user";
        const roleRoute = router.options.routes.find((route) =>
          route.path.startsWith(`/${roleRouteKey}`),
        );
        if (roleRoute) {
          this.routers = roleRoute.children;
        } else {
          console.error("Failed to find corresponding route for the role");
        }
      } catch (error) {
        console.error("Error during token validation:", error);
      }
    },
  },
};
</script>

<style scoped lang="scss">
#nutrition-select {
  margin: 10px;
  padding: 5px;
  font-size: 16px;
  border: none;
  outline: none;
  width: 60%;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.6);
}

.removeFood {
  color: rgba(0, 0, 0, 0.6);
  user-select: none;
  cursor: pointer;
  display: inline-block;
  margin-top: 10px;
}

.removeFood:hover {
  color: #1c1c1c;
}

label {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.6);
}

.content-container {
  padding: 20px clamp(16px, 4vw, 96px) 40px;
  box-sizing: border-box;
  min-height: calc(100vh - 96px);
  overflow-x: hidden;
}

/* 整段顶栏与视口同宽；白底条放在容器上，避免子级略窄时露出 body 绿底 */
.user-container {
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  min-height: 100%;
}

.menus-container {
  position: sticky;
  top: 0;
  z-index: 1000;
  flex-shrink: 0;
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  background: #ffffff;
  border-bottom: 1px solid rgba(42, 157, 111, 0.12);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.04);
}

</style>
