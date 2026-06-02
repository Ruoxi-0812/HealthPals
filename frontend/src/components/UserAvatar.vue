<template>
  <el-avatar :size="size" :shape="shape" v-bind="$attrs">
    <img
      v-if="src && !imageFailed"
      :src="src"
      referrerpolicy="no-referrer"
      alt=""
      @error="imageFailed = true"
    />
    <span v-else class="user-avatar__initials">{{ initials }}</span>
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
    name: {
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
  data() {
    return {
      imageFailed: false,
    };
  },
  computed: {
    initials() {
      const text = (this.name || "").trim();
      if (!text) {
        return "HP";
      }
      const parts = text.split(/\s+/).filter(Boolean);
      if (parts.length >= 2) {
        return `${parts[0][0]}${parts[1][0]}`.toUpperCase();
      }
      return text.slice(0, 2).toUpperCase();
    },
  },
  watch: {
    src() {
      this.imageFailed = false;
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

.user-avatar__initials {
  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  color: #2f6f55;
  background:
    radial-gradient(circle at 35% 25%, rgba(255, 255, 255, 0.72), transparent 36%),
    linear-gradient(135deg, #e4f5ec, #c8ead8);
  font-size: 0.72em;
  font-weight: 800;
  letter-spacing: 0;
}
</style>
