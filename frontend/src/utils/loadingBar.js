import Vue from "vue";

// A tiny reactive counter shared by every in-flight API request, backing the
// global top loading bar (see components/GlobalLoadingBar.vue). Using a Vue
// instance as the store keeps this reactive without pulling in Vuex just for
// one number.
const state = new Vue({
  data: {
    activeRequests: 0,
  },
});

export function startRequest() {
  state.activeRequests += 1;
}

export function endRequest() {
  state.activeRequests = Math.max(0, state.activeRequests - 1);
}

export default state;
