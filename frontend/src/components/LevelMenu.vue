<template>
  <div class="top-bar">
    <div class="top-bar__inner">
      <div class="top-bar__start">
        <div class="top-bar__brand">
          <Logo sysName="HealthPals" :bag="'rgb(8, 24, 16)'" :size="34" />
        </div>
        <nav class="top-bar__nav" aria-label="Main">
          <button
            v-for="{ item, index } in visibleMenuEntries"
            :key="item.path + '-' + index"
            type="button"
            class="top-bar__link"
            :class="{ 'is-active': $route.path === item.path }"
            @click="menuClick(item.path)"
          >
            <span>{{ item.name }}</span>
          </button>
        </nav>
      </div>

      <div class="top-bar__today" aria-label="Today wellness status">
        <span class="top-bar__today-dot" aria-hidden="true" />
        <span class="top-bar__today-text">
          <strong>Today</strong>
          <span>{{ todayLabel }}</span>
        </span>
        <span class="top-bar__today-action">Balanced</span>
      </div>

      <div class="top-bar__actions">
        <div class="top-bar__search" role="search">
          <i class="el-icon-search top-bar__search-icon" aria-hidden="true" />
          <input
            v-model="filterText"
            type="search"
            class="top-bar__search-input"
            placeholder="Search articles…"
            autocomplete="off"
            @keyup.enter="search"
          />
          <button type="button" class="top-bar__search-btn" @click="search">
            Search
          </button>
        </div>

        <button
          type="button"
          class="top-bar__chip top-bar__chip--accent"
          @click="healthDataRecord"
        >
          <i class="el-icon-connection" aria-hidden="true" />
          <span>Health Records</span>
        </button>

        <button
          type="button"
          class="top-bar__icon-btn"
          aria-label="Messages"
          @click="messageCenter"
        >
          <el-badge
            v-if="noReadMsg !== 0"
            :value="noReadMsg"
            class="top-bar__badge-wrap"
          >
            <i class="el-icon-chat-dot-round" />
          </el-badge>
          <i v-else class="el-icon-chat-dot-round" />
        </button>

        <el-dropdown
          class="top-bar__user"
          trigger="click"
          placement="bottom-end"
        >
          <button type="button" class="top-bar__user-trigger">
            <UserAvatar :size="30" :src="localUserInfo.url" />
            <span class="top-bar__user-name">{{ localUserInfo.name }}</span>
            <i class="el-icon-arrow-down el-icon--right" aria-hidden="true" />
          </button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              icon="el-icon-setting"
              @click.native="openSettings"
            >
              Settings
            </el-dropdown-item>
            <el-dropdown-item
              icon="el-icon-back"
              @click.native="loginOut"
              divided
            >
              Log out
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
import { clearToken } from "@/utils/storage.js";
import Logo from "@/components/Logo.vue";
import UserAvatar from "@/components/UserAvatar.vue";

export default {
  name: "UserMenu",
  components: { Logo, UserAvatar },
  props: {
    menus: { type: Array, required: true },
    userInfo: { type: Object, required: true },
  },
  computed: {
    visibleMenuEntries() {
      return this.menus
        .map((item, index) => ({ item, index }))
        .filter(({ item }) => !item.isHidden);
    },
    todayLabel() {
      return new Intl.DateTimeFormat("en", {
        month: "short",
        day: "numeric",
      }).format(new Date());
    },
  },
  data() {
    return {
      messagePath: "/message",
      filterText: "",
      noReadMsg: 0,
      localUserInfo: {},
    };
  },
  watch: {
    userInfo: {
      deep: true,
      immediate: true,
      handler(val) {
        this.localUserInfo = { ...(val || {}) };
        if (val && val.id != null) this.loadMsgCount();
      },
    },
  },
  mounted() {
    if (this.userInfo && this.userInfo.id != null) this.loadMsgCount();
    const savedKw = sessionStorage.getItem("keyWord");
    if (typeof savedKw === "string") this.filterText = savedKw;
    this.$root.$on("app:search-sync-input", this.onSearchSyncInput);
    this.$root.$on("app:user-profile-updated", this.onProfileUpdated);
  },
  beforeDestroy() {
    this.$root.$off("app:search-sync-input", this.onSearchSyncInput);
    this.$root.$off("app:user-profile-updated", this.onProfileUpdated);
  },
  methods: {
    onSearchSyncInput(kw) {
      if (typeof kw === "string") this.filterText = kw;
    },
    onProfileUpdated(payload) {
      if (!payload) {
        return;
      }
      const nextUserInfo = { ...this.localUserInfo };
      if (payload.name != null) {
        nextUserInfo.name = payload.name;
      }
      if (payload.url != null) {
        nextUserInfo.url = payload.url;
      }
      this.localUserInfo = nextUserInfo;
    },
    search() {
      sessionStorage.setItem("keyWord", this.filterText);
      if (this.$route.path === "/search-detail") {
        this.$root.$emit("app:search-keyword-updated");
        return;
      }
      this.$emit("eventListener", "search-detail");
    },
    openSettings() {
      if (this.$route.path !== "/settings") {
        this.$router.push("/settings");
      }
    },
    loginOut() {
      this.$emit("eventListener", "loginOut");
    },
    dietRecord() {
      this.$emit("eventListener", "dietRecord");
    },
    healthDataRecord() {
      this.$emit("eventListener", "healthDataRecord");
    },
    async loadMsgCount() {
      let userId = this.userInfo && this.userInfo.id;
      if (userId == null) {
        const raw = sessionStorage.getItem("userInfo");
        if (!raw) {
          this.noReadMsg = 0;
          return;
        }
        let entity;
        try {
          entity = JSON.parse(raw);
        } catch {
          this.noReadMsg = 0;
          return;
        }
        if (!entity || entity.id == null) {
          this.noReadMsg = 0;
          return;
        }
        userId = entity.id;
      }
      try {
        const messageQueryDto = { userId, isRead: false };
        const response = await this.$axios.post(
          `/message/query`,
          messageQueryDto,
        );
        const { data } = response;
        if (data.code === 200) {
          this.noReadMsg = data.data.length;
        }
      } catch {
        this.noReadMsg = 0;
      }
    },
    pathToDo(path) {
      if (this.$route.path !== path) {
        this.$router.push(path);
      }
    },
    menuClick(path) {
      this.pathToDo(path);
    },
    messageCenter() {
      this.pathToDo(this.messagePath);
    },
    async out() {
      const confirmed = await this.$swalLogout();
      if (confirmed) {
        clearToken();
        this.$router.push("/login");
      }
    },
  },
};
</script>

<style scoped lang="scss">
$accent: #2a9d6f;
$accent-dark: #248760;
$text: #1a1a1a;
$text-muted: #5c6560;
/* Opaque bar so body gradient does not show through top edge */
$bar-bg: #ffffff;

.top-bar {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  background: $bar-bg;
  /* Bottom border drawn on .menus-container only — avoids double line */
  border-bottom: none;
  box-shadow: none;
}

/* Full viewport width; avoids large left gap from max-width + margin auto */
.top-bar__inner {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 10px clamp(12px, 3vw, 24px);
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 60px;
  box-sizing: border-box;
}

/* Shrink to content; do not use flex:1 to fill half the row */
.top-bar__start {
  display: flex;
  align-items: center;
  gap: clamp(10px, 2vw, 24px);
  min-width: 0;
  flex: 0 1 auto;
}

.top-bar__brand {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.top-bar__nav {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 2px;
  min-width: 0;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  flex: 0 1 auto;
  &::-webkit-scrollbar {
    display: none;
  }
}

.top-bar__link {
  appearance: none;
  border: none;
  background: transparent;
  font: inherit;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  color: $text-muted;
  font-size: 14px;
  white-space: nowrap;
  transition:
    color 0.15s ease,
    background 0.15s ease;

  &:hover {
    color: $text;
    background: rgba(42, 157, 111, 0.08);
  }

  &.is-active {
    color: $text;
    font-weight: 600;
    background: rgba(42, 157, 111, 0.14);
  }
}

.top-bar__actions {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  margin-left: clamp(18px, 4vw, 72px);
}

.top-bar__today {
  border: 1px solid rgba(126, 197, 160, 0.26);
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.92),
    rgba(244, 250, 247, 0.88)
  );
  color: #2f4a40;
  border-radius: 999px;
  padding: 7px 10px 7px 9px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  max-width: 260px;
  margin-left: clamp(8px, 2vw, 30px);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.9),
    0 1px 3px rgba(42, 157, 111, 0.06);
  transition:
    border-color 0.15s ease,
    background 0.15s ease;

  @media (max-width: 1280px) {
    display: none;
  }
}

.top-bar__today-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #67c99a;
  box-shadow:
    0 0 0 4px rgba(103, 201, 154, 0.16),
    0 0 16px rgba(103, 201, 154, 0.35);
}

.top-bar__today-text {
  display: inline-flex;
  align-items: baseline;
  gap: 6px;
  font-size: 12px;
  line-height: 1;
  white-space: nowrap;

  strong {
    font-weight: 800;
    color: #24332b;
  }

  span {
    color: rgba(36, 51, 43, 0.52);
    font-weight: 650;
  }
}

.top-bar__today-action {
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(42, 157, 111, 0.07);
  color: rgba(36, 135, 96, 0.78);
  font-size: 11px;
  font-weight: 800;
  line-height: 1;
  white-space: nowrap;
  border: 1px solid rgba(42, 157, 111, 0.08);
}

.top-bar__search {
  display: flex;
  align-items: stretch;
  border: 1px solid rgba(126, 197, 160, 0.38);
  border-radius: 999px;
  overflow: hidden;
  background: #f3faf6;
  min-width: 0;
  max-width: min(300px, 32vw);
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;

  &:focus-within {
    border-color: rgba(42, 157, 111, 0.55);
    background: #fff;
    box-shadow:
      0 0 0 3px rgba(42, 157, 111, 0.18),
      0 2px 8px rgba(53, 92, 75, 0.08);
  }

  @media (max-width: 1100px) {
    max-width: 200px;
  }

  @media (max-width: 920px) {
    display: none;
  }
}

.top-bar__search-icon {
  align-self: center;
  margin-left: 12px;
  color: #4d8b73;
  font-size: 15px;
}

.top-bar__search-input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  background: transparent;
  padding: 8px 8px 8px 6px;
  font-size: 13px;
  color: $text;

  &::placeholder {
    color: rgba(53, 82, 71, 0.42);
  }
}

.top-bar__search-btn {
  appearance: none;
  border: none;
  cursor: pointer;
  padding: 0 14px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: #fff;
  background: $accent;
  transition: background 0.15s ease;

  &:hover {
    background: $accent-dark;
  }
}

.top-bar__chip {
  appearance: none;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  transition:
    background 0.15s ease,
    transform 0.1s ease;

  &--accent {
    color: #fff;
    background: $accent;
    box-shadow: 0 1px 2px rgba(42, 157, 111, 0.25);
  }

  &--accent:hover {
    background: $accent-dark;
  }

  &--accent:active {
    transform: scale(0.98);
  }

  @media (max-width: 640px) {
    span {
      display: none;
    }
    padding: 8px 12px;
  }
}

.top-bar__icon-btn {
  position: relative;
  appearance: none;
  border: none;
  background: rgba(42, 157, 111, 0.1);
  color: $accent-dark;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  transition: background 0.15s ease;

  &:hover {
    background: rgba(42, 157, 111, 0.18);
  }
}

.top-bar__badge-wrap {
  line-height: 1;
}

.top-bar__user {
  margin-left: 2px;
}

.top-bar__user-trigger {
  appearance: none;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: #fff;
  border-radius: 999px;
  padding: 4px 10px 4px 4px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  max-width: 200px;
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease;

  &:hover {
    border-color: rgba(42, 157, 111, 0.35);
    box-shadow: 0 2px 8px rgba(42, 157, 111, 0.08);
  }
}

.top-bar__user-name {
  font-size: 13px;
  font-weight: 600;
  color: $text;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
  max-width: 120px;
}
</style>
