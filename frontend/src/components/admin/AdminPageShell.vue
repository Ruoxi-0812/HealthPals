<template>
  <div class="admin-page admin-page--list" :class="pageClass">
    <div v-if="$slots.toolbar" class="admin-page__toolbar">
      <slot name="toolbar" />
    </div>
    <div class="admin-page__body">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  name: "AdminPageShell",
  props: {
    pageClass: {
      type: String,
      default: "",
    },
  },
};
</script>
