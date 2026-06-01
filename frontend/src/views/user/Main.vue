<template>
  <div class="user-container">
    <div class="menus-container">
      <UserMenu
        :menus="routers"
        :userInfo="userInfo"
        @eventListener="eventListener"
      />
    </div>
    <div class="content-container">
      <keep-alive include="HomePage">
        <router-view class="route-container" />
      </keep-alive>
    </div>
    <AskPalFloating />
  </div>
</template>

<script>
import { clearToken } from "@/utils/storage.js";
import { scrollPageToTop } from "@/utils/scroll";
import router from "@/router";
import UserMenu from "@/components/LevelMenu.vue";
import AskPalFloating from "@/components/AskPalFloating.vue";

export default {
  name: "UserMain",
  components: {
    UserMenu,
    AskPalFloating,
  },
  data() {
    return {
      userInfo: {},
      routers: [],
    };
  },
  created() {
    this.tokenCheckLoad();
    this.$root.$on("app:user-profile-updated", this.onProfileUpdated);
  },
  mounted() {
    this.$nextTick(() => this.scrollContentToTop());
  },
  beforeDestroy() {
    this.$root.$off("app:user-profile-updated", this.onProfileUpdated);
  },
  watch: {
    "$route.fullPath"() {
      this.$nextTick(() => this.scrollContentToTop());
    },
  },
  methods: {
    scrollContentToTop() {
      scrollPageToTop();
    },
    onProfileUpdated(payload) {
      if (!payload) {
        return;
      }
      this.userInfo = {
        id: payload.id,
        url: payload.url,
        name: payload.name,
        role: payload.role,
        email: payload.email,
      };
    },
    eventListener(event) {
      if (event === "search-detail") {
        this.$router.push("/search-detail");
        return;
      }
      if (event === "loginOut") {
        this.loginOutOperation();
        return;
      }
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
    async tokenCheckLoad() {
      try {
        const res = await this.$axios.get("user/auth");
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
        sessionStorage.setItem("userInfo", JSON.stringify(res.data.data));
        this.userInfo = {
          id: userId,
          url: userAvatar,
          name: userName,
          role: userRole,
          email: userEmail,
        };
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
.content-container {
  padding: 20px clamp(16px, 4vw, 96px) 40px;
  box-sizing: border-box;
  min-height: calc(100vh - 96px);
  overflow-x: hidden;
}

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
