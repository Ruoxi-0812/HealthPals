<template>
  <span class="logo">
    <el-image
      class="logo-image"
      :style="{ width: `${size}px`, height: `${size}px` }"
      :src="logoSrc"
      fit="contain"
    ></el-image>
    <div v-if="!flag">
      <span :style="{ color: bag, display: 'block' }">{{ sysName }}</span>
    </div>
  </span>
</template>
<script>
export default {
  name: "Logo",
  data() {
    return {};
  },
  props: {
    flag: {
      type: Boolean,
      required: false,
    },
    sysName: {
      type: String,
      required: false,
    },
    bag: {
      type: String,
      default: "#1c1c1c",
    },
    size: {
      type: Number,
      default: 36,
    },
  },
  computed: {
    logoSrc() {
      const base = process.env.BASE_URL || "/";
      return `${base}logo.png`.replace(/([^:]\/)\/+/g, "$1");
    },
  },
  created() {},
  methods: {},
};
</script>
<style scoped lang="scss">
.logo {
  color: rgb(8, 24, 16) !important;
  font-family: var(--nb-font-display);
  font-weight: 700;
  font-size: 20px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  user-select: none;
  line-height: 1;

  span {
    margin-left: 8px;
    color: #666;
  }
}

.logo-image {
  flex-shrink: 0;
}

.logo-image :deep(.el-image__inner) {
  object-fit: contain !important;
}
</style>
