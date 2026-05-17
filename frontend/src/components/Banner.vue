<template>
  <div class="banner-wrap">
    <img
      class="banner-image"
      :src="bannerCoverSrc"
      style="min-height: 218px; max-height: 308px"
      :style="{ width: width, borderRadius: borderRadius }"
      referrerpolicy="no-referrer"
      @error="onCoverImgError"
    />
    <div class="tip-name" role="button" tabindex="0" @click="onClick" @keydown.enter.prevent="onClick">
      <p class="banner-title">{{ activeData.name }}</p>
      <div class="point-container">
        <span
          v-for="(point, indexPoint) in data"
          :key="indexPoint"
          :style="{
            backgroundColor: indexPoint === index ? 'rgb(254, 244, 203)' : '',
            height: indexPoint === index ? '15px' : '10px',
            width: indexPoint === index ? '30px' : '20px',
          }"
        >
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { newsCoverSrc, onCoverImgError } from "@/utils/coverImage";

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
      handler() {
        if (!this.data || !this.data.length) {
          this.activeData = {};
          if (this.timerId != null) {
            clearInterval(this.timerId);
            this.timerId = null;
          }
          return;
        }
        this.activeData = { ...this.data[0] };
        this.index = 0;
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
      timerId: null,
    };
  },
  computed: {
    bannerCoverSrc() {
      return newsCoverSrc(
        this.activeData && this.activeData.cover,
        this.activeData && this.activeData.id,
      );
    },
  },
  beforeDestroy() {
    if (this.timerId != null) {
      clearInterval(this.timerId);
    }
  },
  methods: {
    onCoverImgError,
    onClick() {
      this.$emit("on-click", this.activeData);
    },
    config() {
      if (this.timerId != null) {
        clearInterval(this.timerId);
      }
      if (!this.data || this.data.length === 0) {
        return;
      }
      this.timerId = setInterval(() => {
        this.index = (this.index + 1) % this.data.length;
        this.activeData = { ...this.data[this.index] };
      }, this.time);
    },
  },
};
</script>

<style scoped lang="scss">
.tip-name {
  text-align: center;
  width: calc(100% - 18px);
  padding: 12px 18px 14px;
  color: #2d453c;
  margin: 0;
  border-radius: 14px;
  cursor: pointer;
  background-color: rgba(255, 255, 255, 0.9);
  position: absolute;
  bottom: 9px;
  left: 9px;
  backdrop-filter: blur(4px);
  box-sizing: border-box;

  &:focus {
    outline: none;
  }

  &:focus-visible {
    outline: 2px solid rgba(42, 157, 111, 0.55);
    outline-offset: 2px;
  }
}

.banner-title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, "Times New Roman", serif);
  font-size: clamp(1.05rem, 2.35vw + 0.65rem, 1.85rem);
  font-weight: 600;
  line-height: 1.28;
  letter-spacing: -0.02em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}

.point-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 12px;

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
  width: 100%;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 14px 36px rgba(53, 92, 75, 0.14);
}

.banner-image {
  display: block;
  width: 100%;
  object-fit: cover;
}
</style>
