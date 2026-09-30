<template>
  <div id="app">
    <GlobalLoadingBar />
    <router-view />
  </div>
</template>

<script>
import GlobalLoadingBar from "@/components/GlobalLoadingBar.vue";

export default {
  name: "App",
  components: { GlobalLoadingBar },
};
</script>

<style lang="scss">
#app {
  height: 100%;
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: var(--nb-font);
}
</style>
