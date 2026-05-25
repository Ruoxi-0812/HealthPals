<template>
  <span class="logo">
    <el-image
      class="logo-image"
      :style="{ width: `${size}px`, height: `${size}px` }"
      :src="logoSrc"
      fit="contain"
    ></el-image>
    <span v-if="!flag" class="logo__name" :style="{ color: bag }">{{ sysName }}</span>
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
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  user-select: none;
  line-height: 1;
  max-width: 100%;
  overflow: hidden;
}

.logo-image {
  flex-shrink: 0;
}

.logo__name {
  display: block;
  margin-left: 10px;
  color: #666;
  white-space: nowrap;
}

.logo-image :deep(.el-image__inner) {
  object-fit: contain !important;
}
</style>
