<template>
  <div
    v-if="visible"
    class="global-loading-bar"
    role="progressbar"
    aria-label="Loading"
    aria-live="polite"
  />
</template>

<script>
// A safety-net indicator that sits on top of every page: whenever any API
// request is in flight (tracked in utils/loadingBar.js via the axios
// interceptors in utils/request.js), show a slim animated bar at the very
// top of the viewport. Per-page skeletons remain the primary, nicer-looking
// loading UI; this just guarantees there's never a silent gap where a slow
// or cold-starting backend makes the app look like it's doing nothing.
import loadingBarState from "@/utils/loadingBar.js";

export default {
  name: "GlobalLoadingBar",
  computed: {
    visible() {
      return loadingBarState.activeRequests > 0;
    },
  },
};
</script>

<style scoped lang="scss">
.global-loading-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  z-index: 3000;
  background: linear-gradient(
    90deg,
    rgba(42, 157, 111, 0.15) 0%,
    #2a9d6f 50%,
    rgba(42, 157, 111, 0.15) 100%
  );
  background-size: 200% 100%;
  animation: global-loading-bar-sweep 1.1s ease-in-out infinite;
}

@keyframes global-loading-bar-sweep {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .global-loading-bar {
    animation: none;
    background: #2a9d6f;
  }
}
</style>
