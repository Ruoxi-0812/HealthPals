<template>
  <div class="admin-header">
    <button
      type="button"
      class="admin-header__toggle"
      :aria-label="showFlag ? 'Expand sidebar' : 'Collapse sidebar'"
      @click="operation"
    >
      <i :class="showFlag ? 'el-icon-s-unfold' : 'el-icon-s-fold'" />
    </button>

    <nav class="admin-header__breadcrumb" aria-label="Breadcrumb">
      <span class="admin-header__crumb">Admin</span>
      <span class="admin-header__sep">/</span>
      <span class="admin-header__crumb admin-header__crumb--current">{{
        tag || "Dashboard"
      }}</span>
    </nav>

    <el-dropdown class="admin-header__user" trigger="click" placement="bottom-end">
      <button type="button" class="admin-header__user-trigger">
        <el-avatar :size="32" :src="userInfo.url" />
        <span class="admin-header__user-name">{{ userInfo.name }}</span>
        <i class="el-icon-arrow-down el-icon--right" />
      </button>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          icon="el-icon-user"
          @click.native="userCenterPanel"
        >
          Personal center
        </el-dropdown-item>
        <el-dropdown-item icon="el-icon-switch-button" divided @click.native="loginOut">
          Log out
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: "LevelHeader",
  data() {
    return {
      showFlag: sessionStorage.getItem("flag") === "true",
    };
  },
  props: {
    tag: {
      type: String,
      required: true,
      default: "",
    },
    userInfo: {
      type: Object,
      required: true,
      default: () => ({}),
    },
    bag: {
      type: String,
      default: "",
    },
  },
  methods: {
    userCenterPanel() {
      this.$emit("eventListener", "center");
    },
    loginOut() {
      this.$emit("eventListener", "loginOut");
    },
    operation() {
      this.showFlag = !this.showFlag;
      sessionStorage.setItem("flag", this.showFlag);
      this.$emit("selectOperation", this.showFlag);
    },
  },
};
</script>

<style scoped lang="scss">
.admin-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 20px;
  min-height: 64px;
  box-sizing: border-box;
}

.admin-header__toggle {
  appearance: none;
  border: 1px solid rgba(126, 197, 160, 0.35);
  background: #f3faf6;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  cursor: pointer;
  color: #355247;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    background 0.15s ease,
    border-color 0.15s ease;

  &:hover {
    background: rgba(42, 157, 111, 0.12);
    border-color: rgba(42, 157, 111, 0.45);
  }
}

.admin-header__breadcrumb {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  font-size: 14px;
}

.admin-header__crumb {
  color: rgba(53, 82, 71, 0.65);
  font-weight: 600;

  &--current {
    color: var(--nb-ink, #24332b);
    font-family: var(--nb-font-display);
    font-weight: 600;
    font-size: 1.05rem;
  }
}

.admin-header__sep {
  color: rgba(126, 197, 160, 0.8);
}

.admin-header__user {
  margin-left: auto;
}

.admin-header__user-trigger {
  appearance: none;
  border: 1px solid rgba(126, 197, 160, 0.28);
  background: #fff;
  border-radius: 999px;
  padding: 4px 12px 4px 4px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font: inherit;
  transition: box-shadow 0.15s ease;

  &:hover {
    box-shadow: 0 2px 12px rgba(53, 92, 75, 0.1);
  }
}

.admin-header__user-name {
  font-size: 14px;
  font-weight: 650;
  color: #355247;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
