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
      <router-view class="route-container"></router-view>
    </div>
    <!-- Personal Center Dialog -->
    <el-dialog :show-close="false" :visible.sync="dialogOperaion" width="30%">
      <div slot="title" style="padding: 25px 0 0 20px">
        <span style="font-size: 18px; font-weight: 800">Personal Center</span>
      </div>
      <el-row style="padding: 10px 20px 20px 20px">
        <el-row>
          <p style="font-size: 12px; padding: 3px 0; margin-bottom: 10px">
            <span class="modelName">*Avatar</span>
          </p>
          <el-upload
            class="avatar-uploader"
            action="/api/personal-heath/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="data.url" :src="data.url" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row>
          <p style="font-size: 12px; padding: 3px 0">
            <span class="modelName">*Username</span>
          </p>
          <input
            class="modelInput"
            type="text"
            v-model="data.name"
            placeholder="Username"
          />
        </el-row>
        <el-row>
          <p style="font-size: 12px; padding: 3px 0">
            <span class="modelName">*Email</span>
          </p>
          <input
            class="modelInput"
            type="text"
            v-model="data.email"
            placeholder="Email"
          />
        </el-row>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button
          class="customer"
          size="small"
          style="background-color: rgb(241, 241, 241); border: none"
          @click="dialogOperaion = false"
          >Cancel</el-button
        >
        <el-button
          size="small"
          style="background-color: #15559a; border: none"
          class="customer"
          type="info"
          @click="updateUserInfo"
          >Update</el-button
        >
      </span>
    </el-dialog>
    <!-- Reset Password -->
    <el-dialog
      :show-close="false"
      :visible.sync="dialogRetPwdOperaion"
      width="26%"
    >
      <div slot="title" style="padding: 25px 0 0 20px">
        <span style="font-size: 18px; font-weight: 800">Reset Password</span>
      </div>
      <el-row style="padding: 10px 20px 20px 20px">
        <el-row>
          <p style="font-size: 12px; padding: 3px 0; margin-bottom: 10px">
            <span class="modelName">*Old Password</span>
          </p>
          <input
            class="modelInput"
            type="password"
            v-model="pwdEntity.oldPwd"
            placeholder="Old Password"
          />
        </el-row>
        <el-row>
          <p style="font-size: 12px; padding: 3px 0; margin-bottom: 10px">
            <span class="modelName">*New Password</span>
          </p>
          <input
            class="modelInput"
            type="password"
            v-model="pwdEntity.newPwd"
            placeholder="New Password"
          />
        </el-row>
        <el-row>
          <p style="font-size: 12px; padding: 3px 0; margin-bottom: 10px">
            <span class="modelName">*Confirm Password</span>
          </p>
          <input
            class="modelInput"
            type="password"
            v-model="pwdEntity.againPwd"
            placeholder="Confirm Password"
          />
        </el-row>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button
          class="customer"
          size="small"
          style="background-color: rgb(241, 241, 241); border: none"
          @click="dialogRetPwdOperaion = false"
          >Cancel</el-button
        >
        <el-button
          size="small"
          style="background-color: #15559a; border: none"
          class="customer"
          type="info"
          @click="updateUserPwd"
          >Update</el-button
        >
      </span>
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
          this.$swal.fire({
            title: "Update Personal Info",
            text: data.msg,
            icon: "success",
            showConfirmButton: false,
            timer: 1000,
          });
        }
      } catch (e) {
        this.dialogOperaion = false;
        this.$swal.fire({
          title: "Error updating personal information",
          text: e,
          icon: "error",
          showConfirmButton: false,
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
          this.$swal.fire({
            title: "Password Updated",
            text: data.msg,
            icon: "success",
            showConfirmButton: false,
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
      const confirmed = await this.$swalConfirm({
        title: "Log Out",
        text: `After logging out, you will need to log in again to use the system features!`,
        icon: "warning",
      });
      if (confirmed) {
        // Clear Token and redirect to login page
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

.modelInput {
  outline: none;
  border: none;
  font-size: 22px;
  width: 60%;
  font-weight: 800;
}

.avatar {
  width: 88px;
  height: 88px;
}
</style>
