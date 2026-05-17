<template>
  <div class="menu-container admin-shell">
    <aside class="menu-side" :class="{ 'menu-side-narrow': flag }">
      <div class="menu-side__brand">
        <Logo sysName="HealthPals" :flag="flag" :bag="colorLogo" />
      </div>
      <AdminMenu
        :flag="flag"
        :routes="adminRoutes"
        :bag="bagMenu"
        @select="handleRouteSelect"
      />
    </aside>
    <div class="main">
      <header class="header-section">
        <LevelHeader
          @eventListener="eventListener"
          @selectOperation="selectOperation"
          :tag="tag"
          :userInfo="userInfo"
        />
      </header>
      <div class="content-section admin-shell__content">
        <router-view />
      </div>
    </div>

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
            <img
              v-if="userInfo.url"
              :src="userInfo.url"
              class="hp-dialog__avatar"
            />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Username</span>
          <input
            v-model="userInfo.name"
            class="hp-field__input"
            type="text"
            placeholder="Display name"
          />
        </label>
        <label class="hp-field">
          <span class="hp-field__label">Email</span>
          <input
            v-model="userInfo.email"
            class="hp-field__input"
            type="email"
            placeholder="you@example.com"
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
      colorLogo: "#2f4a40",
      bagMenu: "#ffffff",
      dialogOperaion: false,
    };
  },
  created() {
    const menus = router.options.routes.filter(
      (route) => route.path === "/admin",
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
            title: "Profile updated",
            text: data.msg,
            icon: "success",
            showConfirmButton: false,
            timer: 1000,
          });
        }
      } catch (e) {
        this.dialogOperaion = false;
        this.$swal.fire({
          title: "Update failed",
          text: String(e),
          icon: "error",
          showConfirmButton: false,
          timer: 2000,
        });
        console.error(`Profile update error: ${e}`);
      }
    },
    handleAvatarSuccess(res) {
      if (res.code !== 200) {
        this.$message.error(`Avatar upload error`);
        return;
      }
      this.$message.success(`Avatar uploaded`);
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
      const confirmed = await this.$swalLogout();
      if (confirmed) {
        clearToken();
        this.$router.push("/login");
      }
    },
    menuOperationHistory() {
      this.flag = sessionStorage.getItem("flag") === "true";
    },
    selectOperation(flag) {
      this.flag = flag;
    },
    handleRouteSelect(index) {
      const ary = this.adminRoutes.filter((entity) => entity.path === index);
      this.tag = ary[0].name;
      if (this.$router.currentRoute.fullPath === index) {
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
      } catch (error) {
        console.error("Auth load error:", error);
        this.$message.error(
          "Authentication failed to load, please try again.",
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
  background: var(--nb-bg-soft, #e7f6ee);

  .menu-side {
    width: 248px;
    min-width: 115px;
    height: 100vh;
    padding: 12px 0 16px;
    box-sizing: border-box;
    transition: width 0.25s ease;
    background: #fff;
    border-right: 1px solid rgba(126, 197, 160, 0.22);
    box-shadow: 4px 0 18px rgba(53, 92, 75, 0.04);
    display: flex;
    flex-direction: column;
  }

  .menu-side-narrow {
    width: 115px;
  }

  .menu-side__brand {
    padding: 8px 16px 4px;
  }

  .main {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .header-section {
      flex-shrink: 0;
      padding: 0 4px 0 0;
      background: #fff;
      border-bottom: 1px solid rgba(126, 197, 160, 0.18);
    }
  }
}
</style>
