<template>
  <div class="banner-wrap">
    <img
      class="banner-image"
      :src="activeData.cover"
      style="min-height: 218px; max-height: 308px"
      :style="{ width: width, borderRadius: borderRadius }"
    />
    <h3 class="tip-name">
      <div @click="onClick" style="font-size: 12px">{{ activeData.name }}</div>
      <div class="point-container">
        <span
          :style="{
            backgroundColor: index === indexPoint ? 'rgb(254, 244, 203)' : '',
            height: index === indexPoint ? '15px' : '10px',
            width: index === indexPoint ? '30px' : '20px',
          }"
          v-for="(point, indexPoint) in data"
          :key="index"
        >
        </span>
      </div>
    </h3>
  </div>
</template>

<script>
export default {
  name: "Banner",
  props: {
    data: {
      type: Array,
      required: true,
    },
    width: {
      type: String,
      default: "100%",
    },
    borderRadius: {
      type: String,
      default: "10px",
    },
    time: {
      type: Number,
      default: 3000,
    },
  },
  watch: {
    data: {
      handler(v1, v2) {
        this.activeData = { ...this.data[0] };
        this.config();
      },
      deep: true,
      immediate: true,
    },
  },
  data() {
    return {
      activeData: {},
      index: 0,
    };
  },
  methods: {
    onClick(data) {
      this.$emit("on-click", this.activeData);
    },
    config() {
      setInterval(() => {
        this.index = this.index >= this.data.length ? 0 : this.index;
        this.activeData = this.data[this.index++];
      }, this.time);
    },
  },
};
</script>

<style scoped lang="scss">
.tip-name {
  text-align: center;
  width: calc(100% - 18px);
  padding: 10px 0 12px 0;
  color: #2d453c;
  margin: 0;
  border-radius: 14px;
  cursor: pointer;
  background-color: rgba(255, 255, 255, 0.9);
  position: absolute;
  bottom: 9px;
  left: 9px;
  backdrop-filter: blur(4px);
}

.point-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 10px;

  span {
    width: 20px;
    height: 10px;
    background-color: #d2e8dc;
    border-radius: 5px;
  }

  span:hover {
    background-color: #8bc7a5;
  }
}

.banner-wrap {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 14px 36px rgba(53, 92, 75, 0.14);
}

.banner-image {
  display: block;
  object-fit: cover;
}
</style>
