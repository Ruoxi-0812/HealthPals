<template>
  <div class="menu-container">
    <div class="menu-side" :class="{ 'menu-side-narrow': flag }">
      <div style="display: flex; align-items: center">
        <Logo
          style="padding: 0 40px; margin: 10px 0"
          sysName="HealthPals"
          :flag="flag"
          :bag="colorLogo"
        />
      </div>
      <div style="margin-top: 12px">
        <AdminMenu
          :flag="flag"
          :routes="adminRoutes"
          :bag="bagMenu"
          @select="handleRouteSelect"
        />
      </div>
    </div>
    <div class="main">
      <div class="header-section">
        <LevelHeader
          @eventListener="eventListener"
          @selectOperation="selectOperation"
          :tag="tag"
          :userInfo="userInfo"
        />
      </div>
      <div class="content-section">
        <router-view></router-view>
      </div>
    </div>
    <!-- Personal Center -->
    <el-dialog :show-close="false" :visible.sync="dialogOperaion" width="26%">
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
            <img
              v-if="userInfo.url"
              :src="userInfo.url"
              style="width: 80px; height: 80px"
            />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row>
          <p style="font-size: 12px; padding: 3px 0">
            <span class="modelName">*User Name</span>
          </p>
          <input
            class="input-title"
            v-model="userInfo.name"
            placeholder="User Name"
          />
        </el-row>
        <el-row>
          <p style="font-size: 12px; padding: 3px 0">
            <span class="modelName">*User Email</span>
          </p>
          <input
            class="input-title"
            v-model="userInfo.email"
            placeholder="User Email"
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
          >Revise</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import request from "@/utils/request.js";
import router from "@/router/index";
import { clearToken } from "@/utils/storage";
import AdminMenu from "@/components/VerticalMenu.vue";
import Logo from "@/components/Logo.vue";
import LevelHeader from "@/components/LevelHeader.vue";
export default {
  name: "Admin",
  components: {
    Logo,
    LevelHeader,
    AdminMenu,
  },
  data() {
    return {
      adminRoutes: [],
      activeIndex: "",
      userInfo: {
        id: null,
        url: "",
        name: "",
        role: null,
        email: "",
      },
      flag: false,
      tag: "Visualization",
      bag: "rgb(246,246,246)",
      colorLogo: "rgb(51,51,51)",
      bagMenu: "rgb(248,248,248)",
      dialogOperaion: false,
    };
  },
  created() {
    let menus = router.options.routes.filter(
      (route) => route.path == "/admin",
    )[0];
    this.adminRoutes = menus.children;
    this.tokenCheckLoad();
    this.menuOperationHistory();
  },

  methods: {
    async updateUserInfo() {
      try {
        const userUpdateDTO = {
          userAvatar: this.userInfo.url,
          userName: this.userInfo.name,
          userEmail: this.userInfo.email,
        };
        const resposne = await this.$axios.put(`/user/update`, userUpdateDTO);
        const { data } = resposne;
        if (data.code === 200) {
          this.dialogOperaion = false;
          this.tokenCheckLoad();
          this.$swal.fire({
            title: "Modification of personal information",
            text: data.msg,
            icon: "success",
            showConfirmButton: false,
            timer: 1000,
          });
        }
      } catch (e) {
        this.dialogOperaion = false;
        this.$swal.fire({
          title: "Modification of personal information Error",
          text: e,
          icon: "error",
          showConfirmButton: false,
          timer: 2000,
        });
        console.error(`Modification of personal information Error:${e}`);
      }
    },
    handleAvatarSuccess(res, file) {
      if (res.code !== 200) {
        this.$message.error(`Avatar upload error`);
        return;
      }
      this.$message.success(`Avatar upload success`);
      this.userInfo.url = res.data;
    },
    eventListener(event) {
      if (event === "center") {
        this.dialogOperaion = true;
      }
      if (event === "loginOut") {
        this.loginOut();
      }
    },
    async loginOut() {
      const confirmed = await this.$swalConfirm({
        title: "Logging out?",
        text: `Need to log in again after logging out?`,
        icon: "warning",
      });
      if (confirmed) {
        this.$swal.fire({
          title: "Exit Login Successful",
          text: "1s and then return to the login page",
          icon: "success",
          showConfirmButton: false,
          timer: 1000,
        });
        setTimeout(() => {
          clearToken();
          this.$router.push("/login");
        }, 1000);
      }
    },
    menuOperationHistory() {
      this.flag = sessionStorage.getItem("flag") === "true";
    },
    selectOperation(flag) {
      this.flag = flag;
    },
    handleRouteSelect(index) {
      let ary = this.adminRoutes.filter((entity) => entity.path == index);
      this.tag = ary[0].name;
      if (this.$router.currentRoute.fullPath == index) {
        return;
      }
      this.$router.push(index);
    },

    async tokenCheckLoad() {
      try {
        const res = await request.get("user/auth");

        if (res.data.code === 400) {
          this.$message.error(res.data.msg);
          this.$router.push("/login");
          return;
        }
        const {
          id,
          userAvatar: url,
          userName: name,
          userRole: role,
          userEmail: email,
        } = res.data.data;
        this.userInfo = { id, url, name, role, email };
        const rolePath = role === 1 ? "/admin" : "/user";
        const targetMenu = router.options.routes.find(
          (route) => route.path === rolePath,
        );
        if (targetMenu) {
          this.routers = targetMenu.children;
        } else {
          console.warn(
            `A route corresponding to the role was not found: ${rolePath}`,
          );
        }
      } catch (error) {
        console.error(
          "An error occurred while obtaining user authentication information:",
          error,
        );
        this.$message.error(
          "Authentication information failed to load, please try again!",
        );
      }
    },
  },
};
</script>
<style scoped lang="scss">
.menu-container {
  display: flex;
  height: 100vh;
  width: 100%;

  .menu-side {
    width: 253px;
    min-width: 115px;
    height: 100vh;
    padding-top: 10px;
    box-sizing: border-box;
    transition: width 0.3s ease;
    background-color: rgb(248, 248, 248);
    border-right: 1px solid rgb(240, 240, 240);
  }

  .menu-side-narrow {
    width: 115px;
  }

  .main {
    flex-grow: 1;
    overflow-x: hidden;

    .header-section {
      max-width: 100%;
      padding: 0 15px;
    }

    .content-section {
      flex-grow: 1;
      padding: 5px;
      overflow-y: auto;
    }
  }
}
</style>
