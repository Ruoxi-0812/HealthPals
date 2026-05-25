<template>
  <div class="login-container">
    <div class="login-panel nb-surface">
      <div class="welcome-panel">
        <div class="welcome-pill">Mental Wellness Companion</div>
        <h1>Start your healthier day</h1>
        <p>
          Track habits, read curated health tips, and keep your wellbeing
          records in one calm and friendly place.
        </p>
        <div class="welcome-meta">
          <span>Daily tracking</span>
          <span>Health news</span>
          <span>Personal insights</span>
        </div>
      </div>

      <div class="form-panel">
        <div class="logo">
          <Logo :bag="colorLogo" sysName="HealthPals" />
        </div>
        <div class="text">
          <input v-model="act" class="act" placeholder="Account" />
        </div>
        <div class="text">
          <input
            v-model="pwd"
            class="pwd"
            type="password"
            placeholder="Password"
          />
        </div>
        <div>
          <span class="login-btn" @click="login">Login Now</span>
        </div>
        <div class="oauth-wrap">
          <div class="oauth-divider" role="separator">
            <span class="oauth-divider__text">or</span>
          </div>
          <GoogleSignInButton mode="login" />
        </div>
        <div class="tip">
          <p>
            Don't have an account?
            <span class="no-act" @click="toDoRegister"
              >Click here to register</span
            >
          </p>
        </div>
      </div>
    </div>
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
.login-container {
  // background-image: url('/bag.png');
  // background-repeat:repeat;
  width: 100%;
  min-height: 100vh;
  background-color: transparent;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-family: var(--nb-font);

  .login-panel {
    width: min(1200px, 94vw);
    min-height: min(760px, 92vh);
    height: auto;
    padding: 20px;
    background-color: transparent;
    display: grid;
    grid-template-columns: 1.2fr 1fr;
    gap: 22px;

    .welcome-panel {
      border-radius: 28px;
      padding: 30px;
      background: linear-gradient(150deg, #8ecabf 0%, #7ec5a0 100%);
      color: #fff;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .welcome-pill {
        width: fit-content;
        padding: 6px 12px;
        border-radius: 999px;
        background: rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.35);
        font-size: 12px;
        margin-bottom: 12px;
      }

      h1 {
        margin: 0;
        font-size: 34px;
        line-height: 1.2;
        font-family: var(--nb-font-display);
        letter-spacing: -0.02em;
        font-weight: 600;
      }

      p {
        margin-top: 14px;
        line-height: 1.6;
        color: rgba(255, 255, 255, 0.9);
      }

      .welcome-meta {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        margin-top: 10px;

        span {
          padding: 6px 10px;
          border-radius: 999px;
          border: 1px solid rgba(255, 255, 255, 0.35);
          background: rgba(255, 255, 255, 0.14);
          font-size: 12px;
        }
      }
    }

    .form-panel {
      border-radius: 28px;
      padding: 26px 26px 28px;
      background: rgba(255, 255, 255, 0.72);
      border: 1px solid rgba(126, 197, 160, 0.2);
      backdrop-filter: blur(6px);
      display: flex;
      flex-direction: column;
      justify-content: center;
      min-height: 0;

      .logo {
        margin: 0 0 22px 0;
      }

      .act,
      .pwd {
        margin: 8px 0;
        height: 53px;
        line-height: 53px;
        width: 100%;
        padding: 0 15px;
        background-color: var(--nb-paper);
        box-sizing: border-box;
        border: 2px solid rgba(126, 197, 160, 0.28);
        border-radius: 12px;
        font-family: var(--nb-font);
        font-weight: 800;
        font-size: 18px;
        margin-top: 13px;
        color: var(--nb-ink);
        transition:
          border-color 0.18s ease,
          background-color 0.18s ease,
          box-shadow 0.18s ease;
      }

      .act::placeholder,
      .pwd::placeholder {
        color: #9eb6aa;
        font-weight: 400;
        font-size: 15px;
        letter-spacing: 0.01em;
        opacity: 1;
      }

      .act:focus,
      .pwd:focus {
        outline: none;
        border-color: #59a67c;
        background-color: rgba(126, 197, 160, 0.2);
        box-shadow:
          0 0 0 4px rgba(126, 197, 160, 0.28),
          0 1px 2px rgba(53, 92, 75, 0.06);
      }
    }
  }

  .login-btn {
    display: inline-block;
    text-align: center;
    border-radius: 12px;
    margin-top: 20px;
    height: 43px;
    line-height: 43px;
    width: 100%;
    background-color: var(--nb-accent);
    font-family: var(--nb-font);
    font-size: 14px !important;
    border: 1px solid rgba(126, 197, 160, 0.45);
    box-shadow:
      var(--nb-shadow-sm),
      0 3px 0 rgba(53, 92, 75, 0.12);
    color: rgb(250, 250, 250);
    padding: 0 !important;
    cursor: pointer;
    user-select: none;
    font-weight: 900;
    transition:
      transform 0.12s ease,
      box-shadow 0.12s ease,
      filter 0.12s ease;
  }

  .login-btn:hover {
    transform: translate(-1px, -1px);
    filter: brightness(1.02);
  }

  .login-btn:active {
    transform: translate(2px, 2px);
    box-shadow: 0 0 0 rgba(0, 0, 0, 0);
  }

  .tip {
    margin: 20px 0;

    p {
      padding: 3px 0;
      margin: 0;
      font-size: 14px;
      color: #5f7d71;

      i {
        margin-right: 3px;
      }

      .no-act {
        margin: 0 2px 0 6px;
        color: #348060;
        font-weight: 600;
        text-decoration: underline;
        text-underline-offset: 3px;
        text-decoration-thickness: 1px;
        cursor: pointer;
        transition: color 0.15s ease;
      }

      .no-act:hover {
        color: #25634a;
      }
    }
  }

  .oauth-wrap {
    margin-top: 10px;
  }

  .oauth-divider {
    display: flex;
    align-items: center;
    gap: 14px;
    margin: 18px 0 12px;

    &::before,
    &::after {
      content: "";
      flex: 1;
      min-width: 0;
      height: 1px;
      background: linear-gradient(
        90deg,
        rgba(126, 197, 160, 0.08),
        rgba(90, 120, 105, 0.42) 45%,
        rgba(90, 120, 105, 0.42) 55%,
        rgba(126, 197, 160, 0.08)
      );
    }
  }

  .oauth-divider__text {
    flex-shrink: 0;
    font-size: 11px;
    font-weight: 600;
    text-transform: lowercase;
    letter-spacing: 0.06em;
    color: #5a7a6d;
    padding: 5px 14px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.92);
    border: 1px solid rgba(126, 197, 160, 0.4);
    box-shadow: 0 1px 2px rgba(53, 92, 75, 0.06);
  }

  .google-btn {
    width: 100%;
    height: 44px;
    border-radius: 12px;
    border: 1px solid rgba(126, 197, 160, 0.45);
    background: #ffffff;
    color: #355247;
    font-family: var(--nb-font);
    font-weight: 700;
    cursor: pointer;
    transition: all 0.15s ease;
  }

  .google-btn:hover {
    background: #f4faf6;
    transform: translateY(-1px);
  }

}

@media (max-width: 880px) {
  .login-container {
    .login-panel {
      width: 100%;
      grid-template-columns: 1fr;
      min-height: auto;
      padding: 16px;
    }

    .form-panel {
      justify-content: flex-start;
      padding-top: 20px;
      padding-bottom: 24px;
    }

    .welcome-panel {
      padding: 20px !important;

      h1 {
        font-size: 26px !important;
      }
    }
  }
}
</style>
