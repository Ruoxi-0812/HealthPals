<template>
  <div class="google-signin">
    <div
      v-if="googleConfigured && !loadError"
      ref="googleShell"
      class="google-signin__button-wrap"
      :class="{ 'is-submitting': submitting }"
    >
      <button type="button" class="google-signin__shell" :disabled="submitting">
        {{ submitting ? "Verifying your Google account..." : "Continue with Google" }}
      </button>
      <div ref="googleButtonMount" class="google-signin__host-overlay" />
    </div>
    <button
      v-else
      type="button"
      class="google-signin__fallback"
      disabled
    >
      Continue with Google
    </button>
    <div v-if="loadError" class="google-signin__note google-signin__note--error">
      {{ loadError }}
    </div>
  </div>
</template>

<script>
import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";

const ADMIN_ROLE = 1;
const USER_ROLE = 2;
const GOOGLE_CLIENT_ID = process.env.VUE_APP_GOOGLE_CLIENT_ID;

export default {
  name: "GoogleSignInButton",
  props: {
    mode: {
      type: String,
      default: "login",
    },
  },
  data() {
    return {
      googleScriptLoading: null,
      loadError: "",
      submitting: false,
      buttonWidth: 360,
    };
  },
  computed: {
    googleConfigured() {
      return Boolean(GOOGLE_CLIENT_ID && GOOGLE_CLIENT_ID.trim());
    },
  },
  async mounted() {
    if (!this.googleConfigured) {
      return;
    }
    await this.mountGoogleButton();
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
    navigateToRole(role) {
      switch (role) {
        case ADMIN_ROLE:
          this.safePush("/admin");
          break;
        case USER_ROLE:
          this.safePush("/user");
          break;
        default:
          this.safePush("/user");
      }
    },
    async mountGoogleButton() {
      this.loadError = "";
      try {
        await this.ensureGoogleScript();
        await this.$nextTick();
        if (!this.$refs.googleButtonMount || !this.$refs.googleShell) {
          return;
        }
        this.buttonWidth = Math.max(
          240,
          Math.round(this.$refs.googleShell.getBoundingClientRect().width || 360),
        );
        this.$refs.googleButtonMount.innerHTML = "";
        window.google.accounts.id.initialize({
          client_id: GOOGLE_CLIENT_ID,
          callback: this.handleGoogleCredential,
        });
        window.google.accounts.id.renderButton(this.$refs.googleButtonMount, {
          type: "standard",
          theme: "outline",
          size: "large",
          text: this.mode === "register" ? "signup_with" : "continue_with",
          shape: "pill",
          logo_alignment: "left",
          width: this.buttonWidth,
        });
      } catch (error) {
        console.error("Google button mount error:", error);
        this.loadError = "Unable to load Google sign-in right now.";
      }
    },
    async handleGoogleCredential(response) {
      if (!response || !response.credential) {
        this.$message.error("Google authentication failed");
        return;
      }
      this.submitting = true;
      try {
        const { data } = await request.post("user/google-login", {
          idToken: response.credential,
        });
        if (data.code !== 200) {
          this.$message.error(
            data.msg ||
              (this.mode === "register"
                ? "Google sign-up failed"
                : "Google login failed"),
          );
          return;
        }
        setToken(data.data.token);
        this.navigateToRole(data.data.role);
      } catch (error) {
        console.error("Google auth request error:", error);
        this.$message.error("Google sign-in request failed");
      } finally {
        this.submitting = false;
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
        script.onerror = () => {
          this.googleScriptLoading = null;
          reject(new Error("Failed to load Google Identity Services script"));
        };
        document.head.appendChild(script);
      });
      return this.googleScriptLoading;
    },
  },
};
</script>

<style scoped lang="scss">
.google-signin {
  width: 100%;
}

.google-signin__button-wrap {
  position: relative;
  width: 100%;
}

.google-signin__shell,
.google-signin__fallback {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  border: 1px solid rgba(126, 197, 160, 0.45);
  background: #ffffff;
  color: #355247;
  font-family: var(--nb-font);
  font-weight: 700;
  font-size: 14px;
}

.google-signin__shell {
  pointer-events: none;
  transition: all 0.15s ease;
}

.google-signin__button-wrap:hover .google-signin__shell {
  background: #f4faf6;
  transform: translateY(-1px);
}

.google-signin__host-overlay {
  position: absolute;
  inset: 0;
  z-index: 2;
  opacity: 0;
  overflow: hidden;
}

.google-signin__host-overlay :deep(div),
.google-signin__host-overlay :deep(iframe) {
  width: 100% !important;
  min-width: 100% !important;
  height: 44px !important;
}

.google-signin__fallback {
  background: rgba(255, 255, 255, 0.78);
  color: rgba(53, 82, 71, 0.45);
  cursor: not-allowed;
}

.google-signin__button-wrap.is-submitting .google-signin__shell {
  background: rgba(255, 255, 255, 0.9);
  transform: none;
}

.google-signin__button-wrap.is-submitting .google-signin__host-overlay {
  pointer-events: none;
}

.google-signin__note {
  margin: 8px 0 0;
  font-size: 12px;
  line-height: 1.5;
  color: rgba(53, 82, 71, 0.62);
  text-align: center;

  &--error {
    color: #9b3d3d;
  }
}
</style>
