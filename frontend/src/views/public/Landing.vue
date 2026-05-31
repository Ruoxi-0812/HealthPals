<template>
  <div class="landing-page">
    <header class="landing-page__top">
      <div class="landing-page__top-inner">
        <Logo :bag="colorLogo" sysName="HealthPals" :size="40" />
        <nav class="landing-page__top-actions">
          <router-link class="landing-page__link" to="/login"
            >Sign in</router-link
          >
          <router-link
            class="landing-page__btn landing-page__btn--ghost"
            to="/register"
          >
            Create account
          </router-link>
        </nav>
      </div>
    </header>

    <main class="landing-page__body">
      <section class="landing-hero nb-surface">
        <div class="landing-hero__copy">
          <p class="landing-hero__eyebrow">Wellness, simplified</p>
          <h1 class="landing-hero__title">
            One calm place for your health routine.
          </h1>
          <p class="landing-hero__text">
            Track your records, read better wellness content, and keep
            everything in one simple space.
          </p>

          <div class="landing-hero__actions">
            <router-link
              class="landing-page__btn landing-page__btn--primary"
              to="/login"
            >
              Sign in
            </router-link>
            <router-link
              class="landing-page__btn landing-page__btn--ghost"
              to="/register"
            >
              Register
            </router-link>
          </div>
        </div>

        <div class="landing-art" aria-hidden="true">
          <div class="landing-art__halo"></div>
          <div class="landing-art__badge">Private health hub</div>

          <div class="landing-art__device">
            <div class="landing-art__device-top">
              <span class="landing-art__device-dot"></span>
              <span class="landing-art__device-dot"></span>
              <span class="landing-art__device-dot"></span>
              <span class="landing-art__device-search"></span>
            </div>

            <div class="landing-art__device-body">
              <aside class="landing-art__sidebar">
                <span
                  class="landing-art__nav-item landing-art__nav-item--active"
                ></span>
                <span class="landing-art__nav-item"></span>
                <span class="landing-art__nav-item"></span>
                <span class="landing-art__nav-item"></span>
              </aside>

              <div class="landing-art__canvas">
                <div class="landing-art__summary">
                  <div class="landing-art__summary-copy">
                    <span class="landing-art__summary-label"
                      >Daily snapshot</span
                    >
                    <span class="landing-art__summary-value"
                      >Feeling balanced today</span
                    >
                  </div>
                  <span class="landing-art__summary-pill">Stable</span>
                </div>

                <div class="landing-art__panel landing-art__panel--chart">
                  <div class="landing-art__panel-head">
                    <span class="landing-art__panel-title">Weekly trend</span>
                    <span class="landing-art__panel-chip">7d</span>
                  </div>
                  <div class="landing-art__chart">
                    <span class="landing-art__chart-grid"></span>
                    <span class="landing-art__chart-line"></span>
                    <span
                      class="landing-art__chart-dot landing-art__chart-dot--one"
                    ></span>
                    <span
                      class="landing-art__chart-dot landing-art__chart-dot--two"
                    ></span>
                    <span
                      class="landing-art__chart-dot landing-art__chart-dot--three"
                    ></span>
                    <span
                      class="landing-art__chart-dot landing-art__chart-dot--four"
                    ></span>
                  </div>
                </div>

                <div class="landing-art__metrics">
                  <div class="landing-art__metric-card">
                    <span class="landing-art__metric-value">72</span>
                    <span class="landing-art__metric-label">Heart rate</span>
                  </div>
                  <div class="landing-art__metric-card">
                    <span class="landing-art__metric-value">8h</span>
                    <span class="landing-art__metric-label">Sleep</span>
                  </div>
                </div>

                <div class="landing-art__panel landing-art__panel--feed">
                  <div class="landing-art__feed-row">
                    <span class="landing-art__feed-icon"></span>
                    <div class="landing-art__feed-copy">
                      <span
                        class="landing-art__feed-line landing-art__feed-line--short"
                      ></span>
                      <span class="landing-art__feed-line"></span>
                    </div>
                  </div>
                  <div class="landing-art__feed-row">
                    <span
                      class="landing-art__feed-icon landing-art__feed-icon--soft"
                    ></span>
                    <div class="landing-art__feed-copy">
                      <span
                        class="landing-art__feed-line landing-art__feed-line--shorter"
                      ></span>
                      <span class="landing-art__feed-line"></span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import Logo from "@/components/Logo.vue";

export default {
  name: "PublicLandingPage",
  components: { Logo },
  data() {
    return {
      colorLogo: "#2f4a40",
    };
  },
};
</script>

<style scoped lang="scss">
.landing-page {
  min-height: 100svh;
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(
      ellipse 72% 48% at 50% -10%,
      rgba(126, 197, 160, 0.28),
      transparent
    ),
    var(--nb-bg-soft, #e7f6ee);
}

.landing-page__top {
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.84);
  border-bottom: 1px solid rgba(126, 197, 160, 0.18);
  backdrop-filter: blur(10px);
}

.landing-page__top-inner {
  width: min(1240px, calc(100% - 20px));
  margin: 0 auto;
  padding: 10px 0;
  min-height: 58px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.landing-page__top-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.landing-page__body {
  width: min(1240px, calc(100% - 20px));
  margin: 0 auto;
  padding: 30px 0 35px;
  flex: 1;
  display: flex;
  min-height: calc(100svh - 59px);
  height: calc(100svh - 59px);
  box-sizing: border-box;
}

.landing-hero {
  flex: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 34px;
  align-items: center;
  padding: clamp(24px, 3.2vw, 38px);
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 28px;
  box-shadow: 0 18px 42px rgba(53, 92, 75, 0.1);
  min-height: calc(100svh - 111px);
  height: calc(100svh - 111px);
  box-sizing: border-box;
}

.landing-hero__eyebrow {
  margin: 0 0 12px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #2a9d6f;
}

.landing-hero__title {
  margin: 0 0 16px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: clamp(2rem, 4vw, 3.3rem);
  line-height: 1.08;
  color: var(--nb-ink, #24332b);
  letter-spacing: -0.03em;
}

.landing-hero__text {
  margin: 0;
  max-width: 34ch;
  font-size: 16px;
  line-height: 1.7;
  color: rgba(53, 82, 71, 0.75);
}

.landing-hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 24px;
}

.landing-art {
  position: relative;
  min-height: 440px;
  border-radius: 28px;
  background:
    radial-gradient(
      circle at 18% 82%,
      rgba(150, 219, 187, 0.3),
      transparent 26%
    ),
    radial-gradient(
      circle at 85% 16%,
      rgba(163, 230, 206, 0.34),
      transparent 30%
    ),
    linear-gradient(
      180deg,
      rgba(246, 252, 249, 0.98),
      rgba(236, 247, 241, 0.95)
    );
  border: 1px solid rgba(126, 197, 160, 0.2);
  overflow: hidden;
}

.landing-art__halo {
  position: absolute;
  inset: 28px;
  border-radius: 28px;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.34),
    rgba(255, 255, 255, 0.08)
  );
  border: 1px solid rgba(255, 255, 255, 0.28);
}

.landing-art__badge {
  position: absolute;
  top: 28px;
  right: 28px;
  z-index: 3;
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(126, 197, 160, 0.16);
  color: #3f6b59;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  box-shadow: 0 10px 22px rgba(53, 92, 75, 0.08);
}

.landing-art__device {
  position: absolute;
  inset: 54px 38px 36px;
  z-index: 2;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.55);
  box-shadow:
    0 22px 40px rgba(53, 92, 75, 0.09),
    inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(10px);
  overflow: hidden;
}

.landing-art__device-top {
  height: 42px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 14px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.12);
  background: rgba(255, 255, 255, 0.5);
}

.landing-art__device-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(126, 197, 160, 0.42);
}

.landing-art__device-search {
  width: 110px;
  height: 10px;
  margin-left: auto;
  border-radius: 999px;
  background: rgba(126, 197, 160, 0.2);
}

.landing-art__device-body {
  display: grid;
  grid-template-columns: 64px minmax(0, 1fr);
  min-height: calc(100% - 42px);
}

.landing-art__sidebar {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
  padding: 18px 0;
  border-right: 1px solid rgba(126, 197, 160, 0.1);
  background: rgba(248, 252, 250, 0.6);
}

.landing-art__nav-item {
  width: 26px;
  height: 26px;
  border-radius: 10px;
  background: rgba(126, 197, 160, 0.18);

  &--active {
    background: rgba(42, 157, 111, 0.28);
    box-shadow: inset 3px 0 0 rgba(42, 157, 111, 0.75);
  }
}

.landing-art__canvas {
  padding: 16px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  grid-template-rows: auto 1fr auto;
  gap: 14px;
}

.landing-art__summary,
.landing-art__panel,
.landing-art__metric-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid rgba(126, 197, 160, 0.14);
  box-shadow: 0 10px 22px rgba(53, 92, 75, 0.06);
}

.landing-art__summary {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
}

.landing-art__summary-copy {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.landing-art__summary-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(53, 82, 71, 0.5);
}

.landing-art__summary-value {
  font-size: 14px;
  font-weight: 700;
  color: #355247;
}

.landing-art__summary-pill,
.landing-art__panel-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(231, 246, 238, 0.92);
  color: #2a6b52;
  font-size: 11px;
  font-weight: 700;
}

.landing-art__panel {
  padding: 14px;
}

.landing-art__panel--chart {
  min-height: 164px;
}

.landing-art__panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.landing-art__panel-title {
  font-size: 13px;
  font-weight: 700;
  color: #355247;
}

.landing-art__chart {
  position: relative;
  height: 108px;
  border-radius: 16px;
  background: linear-gradient(
    180deg,
    rgba(247, 251, 248, 0.92),
    rgba(238, 247, 242, 0.72)
  );
  overflow: hidden;
}

.landing-art__chart-grid,
.landing-art__chart-line,
.landing-art__chart-dot {
  position: absolute;
}

.landing-art__chart-grid {
  inset: 0;
  background: repeating-linear-gradient(
    to bottom,
    transparent 0 26px,
    rgba(126, 197, 160, 0.12) 26px 27px
  );
}

.landing-art__chart-line {
  left: 18px;
  right: 18px;
  bottom: 26px;
  height: 52px;
  border-bottom: 4px solid rgba(42, 157, 111, 0.66);
  border-radius: 0 0 42px 42px;
  transform: skewX(-24deg);
}

.landing-art__chart-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #2a9d6f;
  box-shadow: 0 0 0 4px rgba(42, 157, 111, 0.12);

  &--one {
    left: 26px;
    bottom: 54px;
  }

  &--two {
    left: 90px;
    bottom: 72px;
  }

  &--three {
    left: 154px;
    bottom: 62px;
  }

  &--four {
    right: 28px;
    bottom: 86px;
  }
}

.landing-art__metrics {
  display: grid;
  gap: 12px;
}

.landing-art__metric-card {
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.landing-art__metric-value {
  font-size: 22px;
  font-weight: 750;
  color: #2a9d6f;
}

.landing-art__metric-label {
  font-size: 12px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.65);
}

.landing-art__panel--feed {
  grid-column: 1 / -1;
  padding: 14px;
}

.landing-art__feed-row {
  display: flex;
  align-items: center;
  gap: 12px;

  & + & {
    margin-top: 12px;
  }
}

.landing-art__feed-icon {
  width: 34px;
  height: 34px;
  border-radius: 999px;
  background: rgba(42, 157, 111, 0.18);

  &--soft {
    background: rgba(126, 197, 160, 0.16);
  }
}

.landing-art__feed-copy {
  flex: 1;
  display: grid;
  gap: 8px;
}

.landing-art__feed-line {
  display: block;
  height: 9px;
  border-radius: 999px;
  background: rgba(167, 217, 193, 0.38);

  &--short {
    width: 74%;
  }

  &--shorter {
    width: 56%;
  }
}

.landing-page__link {
  color: #355247;
  text-decoration: none;
  font-size: 13px;
  font-weight: 650;
}

.landing-page__btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 122px;
  padding: 11px 18px;
  border-radius: 999px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 700;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease,
    background 0.15s ease;

  &--primary {
    color: #fff;
    background: #2a9d6f;
    box-shadow: 0 8px 18px rgba(42, 157, 111, 0.24);

    &:hover {
      transform: translateY(-1px);
      background: #248f63;
    }
  }

  &--ghost {
    color: #355247;
    background: rgba(255, 255, 255, 0.94);
    border: 1px solid rgba(126, 197, 160, 0.34);

    &:hover {
      transform: translateY(-1px);
      background: rgba(247, 251, 248, 1);
    }
  }
}

.landing-page__top-actions .landing-page__btn {
  min-width: 0;
  padding: 9px 16px;
}

@media (max-width: 980px) {
  .landing-page {
    display: block;
  }

  .landing-page__body {
    display: block;
    min-height: auto;
    height: auto;
  }

  .landing-hero {
    grid-template-columns: 1fr;
    min-height: auto;
    height: auto;
  }
}

@media (max-width: 720px) {
  .landing-page__top-inner,
  .landing-page__body {
    width: min(100%, calc(100% - 20px));
  }

  .landing-page__top-inner {
    flex-direction: column;
    align-items: flex-start;
  }

  .landing-art {
    min-height: 320px;
  }

  .landing-art__device {
    inset: 44px 20px 24px;
  }

  .landing-art__device-body {
    grid-template-columns: 52px minmax(0, 1fr);
  }

  .landing-art__canvas {
    grid-template-columns: 1fr;
  }

  .landing-art__panel--feed {
    grid-column: auto;
  }
}
</style>
