<template>
  <div class="auth-page">
    <main class="auth-shell">
      <section class="auth-brand" aria-label="HealthPals">
        <div class="auth-brand__mark">
          <Logo :bag="colorLogo" sysName="HealthPals" :size="54" flag />
        </div>
        <h1>HealthPals</h1>
        <p>Personal Health Management Platform</p>
      </section>

      <section class="auth-card">
        <div class="auth-card__header">
          <h2>Sign in</h2>
          <p>Welcome back to your personal health hub.</p>
        </div>

        <div class="auth-field">
          <input v-model="act" class="auth-input" placeholder="Account" />
        </div>
        <div class="auth-field">
          <input
            v-model="pwd"
            class="auth-input"
            type="password"
            placeholder="Password"
          />
        </div>

        <button class="auth-primary" type="button" @click="login">
          Login Now
        </button>

        <div class="oauth-wrap">
          <div class="oauth-divider" role="separator">
            <span class="oauth-divider__text">or</span>
          </div>
          <GoogleSignInButton mode="login" />
        </div>

        <div class="auth-tip">
          <p>
            Don't have an account?
            <a
              class="auth-link"
              href="#/register"
              @click.prevent="toDoRegister"
            >
              Register
            </a>
          </p>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
const ADMIN_ROLE = 1;
const USER_ROLE = 2;
const DELAY_TIME = 1300;

import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";
import md5 from "js-md5";
import GoogleSignInButton from "@/components/GoogleSignInButton.vue";
import Logo from "@/components/Logo.vue";

export default {
  name: "Login",
  components: { Logo, GoogleSignInButton },
  data() {
    return {
      act: "", // Account
      pwd: "", // Password
      colorLogo: "#2f4a40",
    };
  },
  methods: {
    safePush(target) {
      const resolved = this.$router.resolve(target).route;
      if (resolved.fullPath === this.$route.fullPath) {
        return;
      }
      this.$router.push(target).catch((error) => {
        if (error && error.name !== "NavigationDuplicated") {
          throw error;
        }
      });
    },
    // Redirect to Registration Page
    toDoRegister() {
      this.safePush("/register");
    },
    async login() {
      if (!this.act || !this.pwd) {
        this.$swalToast({
          title: "Input Validation",
          text: "Account or password cannot be empty",
          icon: "error",
          timer: DELAY_TIME,
        });
        return;
      }

      const hashedPwd = md5(md5(this.pwd));
      const paramDTO = { userAccount: this.act, userPwd: hashedPwd };

      try {
        const { data } = await request.post(`user/login`, paramDTO);
        if (data.code !== 200) {
          this.$swalToast({
            title: "Login Failed",
            text: data.msg,
            icon: "error",
            timer: DELAY_TIME,
          });
          return;
        }
        setToken(data.data.token);

        // Delay before redirecting based on user role
        setTimeout(() => {
          const { role } = data.data;
          this.navigateToRole(role);
        }, DELAY_TIME);
      } catch (error) {
        console.error("Login request error:", error);
        this.$message.error("Login request failed, please try again!");
      }
    },
    navigateToRole(role) {
      switch (role) {
        case ADMIN_ROLE:
          this.safePush("/admin");
          break;
        case USER_ROLE:
          this.safePush("/user");
          break;
        default:
          console.warn("Unknown role type:", role);
          break;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
* {
  user-select: none;
}

.auth-page {
  min-height: 100svh;
  display: grid;
  place-items: center;
  padding: clamp(18px, 4vh, 38px) 18px;
  box-sizing: border-box;
  background:
    linear-gradient(90deg, rgba(232, 247, 239, 0.88), rgba(247, 252, 249, 0)),
    linear-gradient(180deg, #f8fcfa 0%, #edf8f2 54%, #e7f6ee 100%);
  font-family: var(--nb-font);
}

.auth-shell {
  width: min(100%, 460px);
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vh, 28px);
}

.auth-brand {
  text-align: center;
  color: var(--nb-ink, #24332b);

  h1 {
    margin: 12px 0 5px;
    font-family: var(--nb-font-display, Georgia, serif);
    font-size: 34px;
    line-height: 1.12;
    letter-spacing: 0;
  }

  p {
    margin: 0;
    font-size: 15px;
    font-weight: 600;
    line-height: 1.5;
    color: rgba(53, 82, 71, 0.64);
  }
}

.auth-brand__mark {
  width: clamp(58px, 9vh, 72px);
  height: clamp(58px, 9vh, 72px);
  margin: 0 auto;
  display: grid;
  place-items: center;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  box-shadow:
    0 16px 34px rgba(53, 92, 75, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.auth-card {
  padding: clamp(24px, 4vh, 34px) 36px clamp(24px, 3.5vh, 30px);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(126, 197, 160, 0.24);
  box-shadow:
    0 28px 70px rgba(53, 92, 75, 0.13),
    inset 0 1px 0 rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(16px);
}

.auth-card__header {
  margin-bottom: clamp(16px, 3vh, 22px);

  h2 {
    margin: 0 0 8px;
    font-family: var(--nb-font-display, Georgia, serif);
    font-size: 32px;
    line-height: 1.2;
    color: #24332b;
  }

  p {
    margin: 0;
    font-size: 16px;
    line-height: 1.55;
    color: rgba(53, 82, 71, 0.68);
  }
}

.auth-field + .auth-field {
  margin-top: 12px;
}

.auth-input {
  width: 100%;
  height: clamp(46px, 6.5vh, 54px);
  padding: 0 16px;
  box-sizing: border-box;
  border-radius: 12px;
  border: 1px solid rgba(126, 197, 160, 0.34);
  background: rgba(255, 255, 255, 0.94);
  color: var(--nb-ink, #24332b);
  font-family: var(--nb-font);
  font-size: 16px;
  font-weight: 700;
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;

  &::placeholder {
    color: #8ba79b;
    font-size: 15px;
    font-weight: 500;
  }

  &:focus {
    outline: none;
    border-color: #69c99a;
    background: #ffffff;
    box-shadow: 0 0 0 4px rgba(126, 197, 160, 0.22);
  }
}

.auth-primary {
  width: 100%;
  min-height: clamp(48px, 6.5vh, 56px);
  margin-top: clamp(16px, 3vh, 22px);
  border: 1px solid rgba(48, 141, 94, 0.32);
  border-radius: 13px;
  background: linear-gradient(180deg, #69c99a 0%, #43a875 100%);
  color: #fff;
  box-shadow:
    0 12px 22px rgba(52, 152, 102, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.24);
  cursor: pointer;
  font-family: var(--nb-font);
  font-size: 16px;
  font-weight: 800;
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

.oauth-wrap {
  margin-top: clamp(12px, 2vh, 18px);
}

.oauth-divider {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: clamp(12px, 2vh, 18px) 0 10px;

  &::before,
  &::after {
    content: "";
    flex: 1;
    min-width: 0;
    height: 1px;
    background: rgba(126, 197, 160, 0.28);
  }
}

.oauth-divider__text {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: 650;
  text-transform: lowercase;
  letter-spacing: 0.06em;
  color: #5a7a6d;
  padding: 5px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.36);
}

.auth-tip {
  position: relative;
  z-index: 3;
  margin-top: clamp(12px, 2vh, 18px);
  text-align: center;

  p {
    margin: 0;
    font-size: 14px;
    line-height: 1.6;
    color: rgba(53, 82, 71, 0.62);
  }
}

@media (max-height: 760px) and (min-width: 521px) {
  .auth-brand h1 {
    font-size: 30px;
  }

  .auth-brand p {
    font-size: 14px;
  }

  .auth-card__header h2 {
    font-size: 28px;
  }

  .auth-card__header p {
    font-size: 14px;
  }
}

.auth-link {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 4px;
  margin-left: 6px;
  color: #2d8f62;
  font-weight: 750;
  cursor: pointer;
  text-decoration: none;
}

@media (max-width: 520px) {
  .auth-page {
    align-items: start;
    padding-top: 42px;
  }

  .auth-card {
    padding: 28px 22px 26px;
  }
}
</style>
