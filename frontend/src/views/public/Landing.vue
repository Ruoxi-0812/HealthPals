<template>
  <div class="landing-page">
    <header class="landing-page__top">
      <div class="landing-page__top-inner">
        <Logo :bag="colorLogo" sysName="HealthPals" :size="40" />
        <nav class="landing-page__top-actions">
          <router-link class="landing-page__link" to="/login">Sign in</router-link>
          <router-link class="landing-page__btn landing-page__btn--ghost" to="/register">
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
            Track your records, read better wellness content, and keep everything in one
            simple space.
          </p>

          <div class="landing-hero__actions">
            <router-link class="landing-page__btn landing-page__btn--primary" to="/login">
              Sign in
            </router-link>
            <router-link class="landing-page__btn landing-page__btn--ghost" to="/register">
              Register
            </router-link>
          </div>
        </div>

        <div class="landing-art" aria-hidden="true">
          <div class="landing-art__glow landing-art__glow--one"></div>
          <div class="landing-art__glow landing-art__glow--two"></div>

          <div class="landing-art__card landing-art__card--top">
            <span class="landing-art__card-label">Healthy day</span>
            <div class="landing-art__line landing-art__line--short"></div>
            <div class="landing-art__line"></div>
          </div>

          <div class="landing-art__card landing-art__card--middle">
            <div class="landing-art__mascot">
              <span class="landing-art__leaf"></span>
              <span class="landing-art__eye landing-art__eye--left"></span>
              <span class="landing-art__eye landing-art__eye--right"></span>
              <span class="landing-art__blush landing-art__blush--left"></span>
              <span class="landing-art__blush landing-art__blush--right"></span>
              <span class="landing-art__smile"></span>
              <span class="landing-art__heart"></span>
            </div>
          </div>

          <div class="landing-art__card landing-art__card--bottom">
            <div class="landing-art__pill"></div>
            <div class="landing-art__pill landing-art__pill--ghost"></div>
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
  min-height: 100vh;
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
  padding: clamp(12px, 2vw, 18px) 0;
  flex: 1;
  display: flex;
  min-height: 0;
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
  min-height: 0;
  height: 100%;
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
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(245, 251, 247, 0.96), rgba(236, 247, 241, 0.92));
  border: 1px solid rgba(126, 197, 160, 0.2);
  overflow: hidden;
}

.landing-art__glow {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, rgba(42, 157, 111, 0.28), rgba(42, 157, 111, 0.08));

  &--one {
    width: 260px;
    height: 260px;
    right: -36px;
    top: -40px;
  }

  &--two {
    width: 160px;
    height: 160px;
    left: 18px;
    bottom: 28px;
  }
}

.landing-art__card {
  position: absolute;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(126, 197, 160, 0.2);
  box-shadow: 0 10px 24px rgba(53, 92, 75, 0.08);

  &--top {
    top: 36px;
    left: 34px;
    width: 240px;
    padding: 18px;
  }

  &--middle {
    top: 108px;
    right: 54px;
    width: 212px;
    height: 212px;
    display: grid;
    place-items: center;
  }

  &--bottom {
    left: 92px;
    bottom: 42px;
    width: 264px;
    padding: 16px;
    display: flex;
    gap: 10px;
    align-items: center;
  }
}

.landing-art__card-label {
  display: inline-block;
  margin-bottom: 12px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(53, 82, 71, 0.5);
}

.landing-art__line {
  height: 10px;
  border-radius: 999px;
  background: rgba(126, 197, 160, 0.18);

  & + & {
    margin-top: 10px;
  }

  &--short {
    width: 58%;
  }
}

.landing-art__mascot {
  position: relative;
  width: 118px;
  height: 142px;
  border-radius: 46% 46% 42% 42%;
  background: linear-gradient(180deg, #9cd9bd 0%, #78c29c 100%);
  box-shadow:
    inset 0 -10px 18px rgba(42, 157, 111, 0.18),
    0 10px 18px rgba(53, 92, 75, 0.08);
}

.landing-art__leaf {
  position: absolute;
  top: -12px;
  left: 56px;
  width: 20px;
  height: 28px;
  border-radius: 20px 20px 4px 20px;
  background: linear-gradient(180deg, #73c58f, #4fa66f);
  transform: rotate(-18deg);

  &::after {
    content: "";
    position: absolute;
    left: -9px;
    top: 10px;
    width: 14px;
    height: 4px;
    border-radius: 999px;
    background: rgba(67, 120, 86, 0.42);
    transform: rotate(32deg);
  }
}

.landing-art__eye {
  position: absolute;
  top: 56px;
  width: 10px;
  height: 14px;
  border-radius: 999px;
  background: #29483d;

  &--left {
    left: 34px;
  }

  &--right {
    right: 34px;
  }
}

.landing-art__blush {
  position: absolute;
  top: 78px;
  width: 18px;
  height: 10px;
  border-radius: 999px;
  background: rgba(255, 189, 189, 0.5);

  &--left {
    left: 18px;
  }

  &--right {
    right: 18px;
  }
}

.landing-art__smile {
  position: absolute;
  left: 50%;
  bottom: 32px;
  width: 34px;
  height: 16px;
  margin-left: -17px;
  border-bottom: 4px solid #29483d;
  border-radius: 0 0 24px 24px;
}

.landing-art__heart {
  position: absolute;
  left: 50%;
  top: 72px;
  width: 24px;
  height: 24px;
  margin-left: -12px;
  background: #fff;
  transform: rotate(45deg);
  border-radius: 4px;

  &::before,
  &::after {
    content: "";
    position: absolute;
    width: 24px;
    height: 24px;
    background: #fff;
    border-radius: 50%;
  }

  &::before {
    left: -12px;
    top: 0;
  }

  &::after {
    left: 0;
    top: -12px;
  }
}

.landing-art__pill {
  height: 14px;
  flex: 1;
  border-radius: 999px;
  background: rgba(42, 157, 111, 0.22);

  &--ghost {
    flex: 0.7;
    background: rgba(126, 197, 160, 0.16);
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

  .landing-art__card {
    &--top {
      width: 176px;
      left: 20px;
    }

    &--middle {
      width: 160px;
      height: 160px;
      right: 20px;
    }

    &--bottom {
      left: 46px;
      right: 20px;
      width: auto;
    }
  }
}
</style>
