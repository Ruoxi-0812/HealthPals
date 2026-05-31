<template>
  <div class="landing-page">
    <main class="landing-page__shell">
      <section class="landing-page__brand" aria-label="HealthPals">
        <div class="landing-page__mark">
          <Logo :bag="colorLogo" sysName="HealthPals" :size="74" flag />
        </div>
        <h1>HealthPals</h1>
        <p>Personal Health Management Platform</p>
      </section>

      <section class="landing-card">
        <div class="landing-card__header">
          <h2>Sign in</h2>
          <p>Use your HealthPals account to access your health hub.</p>
        </div>

        <router-link class="landing-card__primary-action" to="/login">
          Sign in with HealthPals
        </router-link>

        <p class="landing-card__support">
          New to HealthPals?
          <router-link to="/register">Create an account</router-link>
        </p>
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
  display: grid;
  place-items: center;
  background:
    linear-gradient(90deg, rgba(232, 247, 239, 0.88), rgba(247, 252, 249, 0)),
    linear-gradient(180deg, #f8fcfa 0%, #edf8f2 54%, #e7f6ee 100%);
  padding: 44px 24px;
  box-sizing: border-box;
}

.landing-page__shell {
  width: min(100%, 580px);
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 34px;
}

.landing-page__brand {
  text-align: center;
  color: var(--nb-ink, #24332b);

  h1 {
    margin: 18px 0 8px;
    font-family: var(--nb-font-display, Georgia, serif);
    font-size: 38px;
    line-height: 1.12;
    letter-spacing: 0;
  }

  p {
    margin: 0;
    font-size: 17px;
    font-weight: 600;
    line-height: 1.5;
    color: rgba(53, 82, 71, 0.64);
  }
}

.landing-page__mark {
  width: 88px;
  height: 88px;
  margin: 0 auto;
  display: grid;
  place-items: center;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  box-shadow:
    0 18px 42px rgba(53, 92, 75, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.landing-card {
  padding: 42px 48px 38px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(126, 197, 160, 0.24);
  box-shadow:
    0 32px 80px rgba(53, 92, 75, 0.14),
    inset 0 1px 0 rgba(255, 255, 255, 0.86);
  backdrop-filter: blur(16px);
}

.landing-card__header {
  h2 {
    margin: 0 0 14px;
    font-family: var(--nb-font-display, Georgia, serif);
    font-size: 34px;
    line-height: 1.25;
    color: #24332b;
  }

  p {
    margin: 0;
    max-width: 100%;
    font-size: 22px;
    line-height: 1.5;
    color: rgba(53, 82, 71, 0.68);
  }
}

.landing-card__primary-action {
  width: 100%;
  min-height: 70px;
  margin-top: 42px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  border-radius: 14px;
  text-decoration: none;
  color: #fff;
  background: linear-gradient(180deg, #69c99a 0%, #43a875 100%);
  border: 1px solid rgba(48, 141, 94, 0.32);
  box-shadow:
    0 12px 22px rgba(52, 152, 102, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.24);
  font-size: 21px;
  font-weight: 750;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease,
    background 0.15s ease;

  &:hover {
    transform: translateY(-1px);
    background: linear-gradient(180deg, #60c391 0%, #399b69 100%);
    box-shadow:
      0 14px 24px rgba(52, 152, 102, 0.22),
      inset 0 1px 0 rgba(255, 255, 255, 0.24);
  }
}

.landing-card__support {
  margin: 32px 0 0;
  text-align: center;
  font-size: 18px;
  line-height: 1.6;
  color: rgba(53, 82, 71, 0.58);

  a {
    color: #2a9d6f;
    font-weight: 700;
    text-decoration: none;
  }
}

@media (max-width: 760px) {
  .landing-page__shell {
    width: min(100%, 520px);
    gap: 30px;
  }

  .landing-page__mark {
    width: 86px;
    height: 86px;
  }

  .landing-page__mark :deep(.logo-image) {
    width: 72px !important;
    height: 72px !important;
  }

  .landing-page__brand {
    h1 {
      margin-top: 16px;
      font-size: 34px;
    }

    p {
      font-size: 16px;
    }
  }

  .landing-card {
    padding: 38px 34px 34px;
  }

  .landing-card__header {
    h2 {
      font-size: 32px;
      margin-bottom: 14px;
    }

    p {
      font-size: 21px;
    }
  }

  .landing-card__primary-action {
    min-height: 72px;
    margin-top: 42px;
    font-size: 21px;
  }

  .landing-card__support {
    margin-top: 32px;
    font-size: 18px;
  }
}

@media (max-width: 420px) {
  .landing-page {
    align-items: start;
    padding: 48px 16px 24px;
  }

  .landing-card {
    padding: 30px 22px 28px;
  }

  .landing-card__header p {
    max-width: none;
    font-size: 18px;
  }

  .landing-card__primary-action {
    min-height: 62px;
    font-size: 18px;
  }

  .landing-card__support {
    font-size: 16px;
  }
}
</style>
