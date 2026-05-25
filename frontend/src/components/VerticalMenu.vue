<template>
  <el-menu
    class="admin-menu"
    :class="{ 'is-collapsed': flag }"
    :collapse-transition="false"
    :collapse="flag"
    :default-active="activeIndex"
    background-color="transparent"
    text-color="#5a7268"
    active-text-color="#1f4d3a"
    @select="handleSelect"
  >
    <el-menu-item
      v-for="(item, index) in routes"
      :key="index"
      v-if="(!item.children || item.children.length === 0) && !item.isHidden"
      :index="item.path"
      :class="{ 'is-active': activeIndex === item.path }"
    >
      <i :class="item.icon" class="admin-menu__icon" />
      <span slot="title" class="admin-menu__label">{{ item.name }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  name: "AdminMenu",
  data() {
    return {
      activeIndex: "1",
      isCollapse: true,
      selectedMenuItem: "",
    };
  },
  props: {
    routes: {
      type: Array,
      required: true,
    },
    flag: {
      type: Boolean,
      required: true,
    },
    bag: {
      type: String,
      default: "#FFFFFF",
    },
  },
  created() {
    const saveLastPath = sessionStorage.getItem("activeMenuItem");
    if (saveLastPath === null) {
      this.handleSelect("/adminLayout");
    } else {
      this.handleSelect(saveLastPath);
    }
  },
  methods: {
    handleSelect(index) {
      this.activeIndex = index;
      this.$emit("select", this.activeIndex);
      sessionStorage.setItem("activeMenuItem", this.activeIndex);
    },
  },
};
</script>

<style scoped lang="scss">
.admin-menu {
  border-right: none !important;
  padding: 8px 12px !important;
  flex: 1;
  overflow-y: auto;
}

.admin-menu.is-collapsed {
  padding-left: 10px !important;
  padding-right: 10px !important;
}

.admin-menu__icon {
  font-size: 18px;
  color: #4d8b73;
  width: 22px;
  text-align: center;
  flex-shrink: 0;
}

.admin-menu__label {
  font-size: 14px;
  font-weight: 600;
}

.admin-menu :deep(.el-menu-item) {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 46px !important;
  line-height: 46px !important;
  margin: 4px 0;
  padding-left: 14px !important;
  padding-right: 12px !important;
  border-radius: 12px;
  user-select: none;
}

.admin-menu.is-collapsed :deep(.el-menu-item) {
  justify-content: center;
  gap: 0;
  padding-left: 0 !important;
  padding-right: 0 !important;
}

.admin-menu :deep(.el-menu-item [class^="el-icon-"]) {
  margin-right: 0;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(42, 157, 111, 0.1) !important;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: rgba(42, 157, 111, 0.14) !important;
  color: #1f4d3a !important;
  font-weight: 700;
  box-shadow: inset 3px 0 0 #2a9d6f;
}
</style>
