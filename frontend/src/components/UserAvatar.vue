<template>
  <el-avatar :size="size" :shape="shape" v-bind="$attrs">
    <img v-if="src" :src="src" referrerpolicy="no-referrer" alt="" />
  </el-avatar>
</template>

<script>
export default {
  name: "UserAvatar",
  inheritAttrs: false,
  props: {
    src: {
      type: String,
      default: "",
    },
    size: {
      type: [Number, String],
      default: "large",
    },
    shape: {
      type: String,
      default: "circle",
    },
  },
};
</script>

<style scoped lang="scss">
.el-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
</style>
