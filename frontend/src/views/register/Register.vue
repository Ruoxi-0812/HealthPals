<template>
  <div class="register-containel">
    <div class="register-panel nb-surface">
      <div class="welcome-panel">
        <div class="welcome-pill">Create your wellness space</div>
        <h1>Join HealthPals</h1>
        <p>
          Build your personal health archive, follow wellness news, and track
          your progress day by day.
        </p>
        <div class="welcome-meta">
          <span>Fast signup</span>
          <span>Private records</span>
          <span>Smart tracking</span>
        </div>
      </div>

      <div class="form-panel">
        <div class="logo">
          <Logo sysName="No account? Sign up now" />
        </div>
        <div class="text">
          <input v-model="name" class="act" placeholder="Username" />
        </div>
        <div class="text">
          <input
            v-model="pwd"
            class="pwd"
            type="password"
            placeholder="Enter Password"
          />
        </div>
        <div class="text">
          <input
            v-model="pwdConfirm"
            class="pwd"
            type="password"
            placeholder="Confirm Password"
          />
        </div>
        <div>
          <span class="register-btn" @click="registerFunc">Sign Up Now</span>
        </div>
        <div class="oauth-wrap">
          <span class="oauth-divider">or</span>
          <button class="google-btn" @click="signUpWithGoogle">
            Continue with Google
          </button>
        </div>
        <div class="tip">
          <p>
            Already have an account?
            <span class="no-act" @click="toDoLogin">Return to Login</span>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const DELAY_TIME = 1300;
const ADMIN_ROLE = 1;
const USER_ROLE = 2;
const GOOGLE_CLIENT_ID = process.env.VUE_APP_GOOGLE_CLIENT_ID;
import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";
import md5 from "js-md5";
import Logo from "@/components/Logo.vue";

export default {
  name: "Register",
  components: { Logo },
  data() {
    return {
      pwd: "", // Password
      pwdConfirm: "", // Confirm Password
      name: "", // Username
      googleScriptLoading: null,
    };
  },
  methods: {
    // Navigate back to the login page
    toDoLogin() {
      this.$router.push("/login");
    },

    async registerFunc() {
      if (!this.pwd || !this.pwdConfirm || !this.name) {
        this.$swal.fire({
          title: "Validation Check",
          text: "Username or password cannot be empty",
          icon: "error",
          showConfirmButton: false,
          timer: DELAY_TIME,
        });
        return;
      }
      if (this.pwd !== this.pwdConfirm) {
        this.$swal.fire({
          title: "Validation Check",
          text: "Passwords do not match",
          icon: "error",
          showConfirmButton: false,
          timer: DELAY_TIME,
        });
        return;
      }

      const hashedPwd = md5(md5(this.pwd));
      const normalizedName = this.name.trim();
      const paramDTO = {
        userAccount: normalizedName,
        userPwd: hashedPwd,
        userName: normalizedName,
      };

      try {
        const { data } = await request.post(`user/register`, paramDTO);
        if (data.code !== 200) {
          this.$swal.fire({
            title: "Registration Failed",
            text: data.msg,
            icon: "error",
            showConfirmButton: false,
            timer: DELAY_TIME,
          });
          return;
        }
        // Notify success with Swal and redirect to login page
        this.$swal.fire({
          title: "Registration Successful",
          text: "Redirecting to login page...",
          icon: "success",
          showConfirmButton: false,
          timer: DELAY_TIME,
        });

        setTimeout(() => {
          this.$router.push("/login");
        }, DELAY_TIME);
      } catch (error) {
        console.error("Registration request error:", error);
      }
    },
    signUpWithGoogle() {
      this.signInWithGoogle();
    },
    navigateToRole(role) {
      switch (role) {
        case ADMIN_ROLE:
          this.$router.push("/admin");
          break;
        case USER_ROLE:
          this.$router.push("/user");
          break;
        default:
          this.$router.push("/user");
      }
    },
    async signInWithGoogle() {
      if (!GOOGLE_CLIENT_ID) {
        this.$message.error("Missing VUE_APP_GOOGLE_CLIENT_ID configuration");
        return;
      }
      await this.ensureGoogleScript();
      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: this.handleGoogleCredential,
      });
      window.google.accounts.id.prompt();
    },
    async handleGoogleCredential(response) {
      if (!response || !response.credential) {
        this.$message.error("Google authentication failed");
        return;
      }
      try {
        const { data } = await request.post("user/google-login", {
          idToken: response.credential,
        });
        if (data.code !== 200) {
          this.$message.error(data.msg || "Google sign-up failed");
          return;
        }
        setToken(data.data.token);
        this.navigateToRole(data.data.role);
      } catch (error) {
        console.error("Google register request error:", error);
        this.$message.error("Google sign-up request failed");
      }
    },
    ensureGoogleScript() {
      if (window.google && window.google.accounts && window.google.accounts.id) {
        return Promise.resolve();
      }
      if (this.googleScriptLoading) {
        return this.googleScriptLoading;
      }
      this.googleScriptLoading = new Promise((resolve, reject) => {
        const script = document.createElement("script");
        script.src = "https://accounts.google.com/gsi/client";
        script.async = true;
        script.defer = true;
        script.onload = resolve;
        script.onerror = reject;
        document.head.appendChild(script);
      });
      return this.googleScriptLoading;
    },
  },
};
</script>

<style lang="scss" scoped>
* {
  user-select: none;
}

.register-containel {
  width: 100%;
  min-height: 100vh;
  background-color: transparent;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;

  .register-panel {
    margin: 0 auto;
    width: min(920px, 92vw);
    height: auto;
    padding: 18px;
    display: grid;
    grid-template-columns: 1.2fr 1fr;
    gap: 16px;

    .welcome-panel {
      border-radius: 18px;
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
      border-radius: 18px;
      padding: 26px;
      background: rgba(255, 255, 255, 0.72);
      border: 1px solid rgba(126, 197, 160, 0.2);
      backdrop-filter: blur(6px);

      .logo {
        margin: 8px 0 26px 0;
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
        border: 1px solid rgba(126, 197, 160, 0.45);
        border-radius: 12px;
        font-weight: 800;
        font-size: 18px;
        margin-top: 13px;
      }

      .act:focus,
      .pwd:focus {
        outline: none;
        box-shadow: 0 0 0 4px rgba(126, 197, 160, 0.18);
      }
    }
  }

  .register-btn {
    display: inline-block;
    text-align: center;
    border-radius: 12px;
    margin-top: 20px;
    height: 43px;
    line-height: 43px;
    width: 100%;
    background-color: var(--nb-accent);
    font-size: 14px !important;
    border: 1px solid rgba(126, 197, 160, 0.45);
    box-shadow: var(--nb-shadow-sm);
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

  .register-btn:hover {
    transform: translate(-1px, -1px);
    filter: brightness(1.02);
  }

  .register-btn:active {
    transform: translate(2px, 2px);
    box-shadow: 0 0 0 rgba(0, 0, 0, 0);
  }

  .oauth-wrap {
    margin-top: 10px;
  }

  .oauth-divider {
    display: block;
    text-align: center;
    font-size: 12px;
    color: #7a958a;
    margin: 8px 0;
  }

  .google-btn {
    width: 100%;
    height: 44px;
    border-radius: 12px;
    border: 1px solid rgba(126, 197, 160, 0.45);
    background: #ffffff;
    color: #355247;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.15s ease;
  }

  .google-btn:hover {
    background: #f4faf6;
    transform: translateY(-1px);
  }

  .tip {
    margin: 20px 0;

    p {
      padding: 3px 0;
      font-size: 14px;
      margin: 0;
      color: #5f7d71;

      i {
        margin-right: 3px;
      }

      span {
        color: #355247;
        border-radius: 2px;
        margin: 0 6px;
      }

      .no-act:hover {
        color: #6fb892;
        cursor: pointer;
      }
    }
  }
}

@media (max-width: 880px) {
  .register-containel {
    .register-panel {
      width: min(420px, 92vw);
      grid-template-columns: 1fr;
      padding: 14px;
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
