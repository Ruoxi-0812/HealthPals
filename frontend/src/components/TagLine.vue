<template>
  <div class="tag-wrap">
    <span v-for="(entity, index) in dataList" :key="index">
      <span
        class="tag-item"
        :class="{ active: tagSelected.name === entity.name }"
        @click="onClick(entity)"
      >
        {{ entity.name }}
      </span>
    </span>
  </div>
</template>

<script>
export default {
  name: "TagLine",
  data() {
    return {
      tagSelected: {},
    };
  },
  props: {
    dataList: {
      type: Array,
      required: true,
    },
  },
  mounted() {
    this.onClick({ name: "全部", id: null });
  },
  methods: {
    onClick(tag) {
      this.tagSelected = tag;
      this.$emit("on-click", tag);
    },
    all() {
      this.$emit("on-click", { id: null, name: "全部" });
    },
  },
};
</script>

<style scoped lang="scss">
.tag-item {
  font-size: 13px;
  display: inline-block;
  margin: 18px 10px 18px 0;
  padding: 7px 18px;
  cursor: pointer;
  user-select: none;
  border-radius: 999px;
  transition: all 0.2s ease;
  border: 1px solid rgba(126, 197, 160, 0.35);
  background: #f4faf6;
  color: #335247;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(53, 92, 75, 0.08);
}

.tag-item.active {
  background: #85c8a3;
  color: #fff;
  border-color: rgba(126, 197, 160, 0.8);
}

.tag-wrap {
  display: flex;
  flex-wrap: wrap;
}
</style>
