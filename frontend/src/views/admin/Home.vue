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
    };
  },
  created() {
    const menus = router.options.routes.filter(
      (route) => route.path === "/admin",
    )[0];
    this.adminRoutes = menus.children;
    this.tokenCheckLoad();
    this.menuOperationHistory();
    this.syncTagFromRoute(this.$route.path);
  },
  mounted() {
    this.$root.$on("app:user-profile-updated", this.handleUserProfileUpdated);
  },
  beforeDestroy() {
    this.$root.$off("app:user-profile-updated", this.handleUserProfileUpdated);
  },
  watch: {
    "$route.path"(path) {
      this.syncTagFromRoute(path);
    },
  },

  methods: {
    eventListener(event) {
      if (event === "center") {
        this.openSettings();
      }
      if (event === "loginOut") {
        this.loginOut();
      }
    },
    openSettings() {
      if (this.$route.path !== "/adminSettings") {
        this.$router.push("/adminSettings");
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
    syncTagFromRoute(path) {
      const matched = this.adminRoutes.find((entity) => entity.path === path);
      this.tag = matched ? matched.name : "Dashboard";
    },
    handleUserProfileUpdated(payload) {
      if (!payload) {
        return;
      }
      this.userInfo = {
        ...this.userInfo,
        id: payload.id != null ? payload.id : this.userInfo.id,
        url: payload.url != null ? payload.url : this.userInfo.url,
        name: payload.name != null ? payload.name : this.userInfo.name,
        email: payload.email != null ? payload.email : this.userInfo.email,
        role: payload.role != null ? payload.role : this.userInfo.role,
      };
    },
    handleRouteSelect(index) {
      const ary = this.adminRoutes.filter((entity) => entity.path === index);
      if (ary.length) {
        this.tag = ary[0].name;
      }
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
    background: #fff;
    border-right: 1px solid rgba(126, 197, 160, 0.22);
    box-shadow: 4px 0 18px rgba(53, 92, 75, 0.04);
    display: flex;
    flex-direction: column;
  }

  .menu-side-narrow {
    width: 96px;
    min-width: 96px;
  }

  .menu-side__brand {
    padding: 8px 16px 4px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
  }

  .menu-side-narrow .menu-side__brand {
    padding: 8px 0 6px;
    justify-content: center;
  }

  .menu-side-narrow .menu-side__brand :deep(.logo) {
    width: auto;
    justify-content: center;
    transform: translateX(-3px);
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
