<template>
  <div class="admin-settings-page">
    <div class="admin-settings-page__layout">
      <aside class="admin-settings-page__nav nb-surface">
        <button type="button" class="admin-settings-page__back" @click="goBack">
          <i class="el-icon-arrow-left" aria-hidden="true" />
          <span>Back</span>
        </button>
        <h1 class="admin-settings-page__title">Settings</h1>
        <nav class="admin-settings-page__menu" role="tablist">
          <button
            v-for="item in sections"
            :key="item.id"
            type="button"
            role="tab"
            class="admin-settings-page__menu-item"
            :class="{ 'is-active': activeSection === item.id }"
            :aria-selected="activeSection === item.id"
            @click="activeSection = item.id"
          >
            <i :class="item.icon" aria-hidden="true" />
            <span>{{ item.label }}</span>
          </button>
        </nav>
      </aside>

      <section class="admin-settings-page__panel nb-surface">
        <div v-show="activeSection === 'profile'" class="admin-settings-panel">
          <header class="admin-settings-panel__head">
            <h2 class="admin-settings-panel__title">Profile</h2>
          </header>

          <div class="admin-settings-profile__summary">
            <div class="admin-settings-profile__summary-media">
              <el-upload
                class="hp-dialog__avatar-uploader avatar-uploader admin-settings-profile__uploader"
                action="/api/personal-heath/v1.0/file/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
              >
                <img
                  v-if="profile.url"
                  :src="profile.url"
                  class="admin-settings-profile__avatar"
                  alt=""
                />
                <span v-else class="admin-settings-profile__avatar-placeholder">
                  <i class="el-icon-plus" aria-hidden="true" />
                </span>
              </el-upload>
              <span class="admin-settings-profile__photo-hint">Change photo</span>
            </div>

            <div class="admin-settings-profile__summary-body">
              <p class="admin-settings-profile__display-name">
                {{ profile.name || "Administrator" }}
              </p>
              <p class="admin-settings-profile__display-email">
                {{ profile.email || "Add an email address" }}
              </p>
              <div class="admin-settings-profile__badges">
                <span class="admin-settings-profile__role-pill">{{ roleLabel }}</span>
                <span v-if="memberSince" class="admin-settings-profile__since">
                  Member since {{ memberSince }}
                </span>
              </div>
            </div>
          </div>

          <h3 class="admin-settings-profile__section-title">Account details</h3>
          <dl class="admin-settings-profile__meta">
            <div class="admin-settings-profile__meta-row">
              <dt>Sign-in ID</dt>
              <dd>{{ profile.account || "—" }}</dd>
            </div>
            <div class="admin-settings-profile__meta-row">
              <dt>User ID</dt>
              <dd>{{ profile.id != null ? profile.id : "—" }}</dd>
            </div>
            <div class="admin-settings-profile__meta-row">
              <dt>Account type</dt>
              <dd>{{ roleLabel }}</dd>
            </div>
            <div v-if="memberSince" class="admin-settings-profile__meta-row">
              <dt>Joined</dt>
              <dd>{{ memberSince }}</dd>
            </div>
          </dl>

          <h3 class="admin-settings-profile__section-title">Edit profile</h3>
          <div class="admin-settings-panel__fields admin-settings-panel__fields--profile">
            <label class="hp-field admin-settings-panel__field">
              <span class="hp-field__label">Display name</span>
              <input
                v-model="profile.name"
                class="hp-field__input"
                type="text"
                placeholder="How your name appears in the admin console"
                autocomplete="username"
              />
            </label>

            <label class="hp-field admin-settings-panel__field">
              <span class="hp-field__label">Email</span>
              <input
                v-model="profile.email"
                class="hp-field__input"
                type="email"
                placeholder="For recovery and admin notices"
                autocomplete="email"
              />
            </label>
          </div>

          <h3 class="admin-settings-profile__section-title">Shortcuts</h3>
          <div class="admin-settings-profile__links">
            <router-link class="admin-settings-profile__link" to="/adminLayout">
              <i class="el-icon-pie-chart" aria-hidden="true" />
              Dashboard
            </router-link>
            <router-link class="admin-settings-profile__link" to="/userManage">
              <i class="el-icon-user" aria-hidden="true" />
              User Manage
            </router-link>
            <router-link class="admin-settings-profile__link" to="/newsManage">
              <i class="el-icon-document" aria-hidden="true" />
              News Manage
            </router-link>
            <router-link class="admin-settings-profile__link" to="/messageManage">
              <i class="el-icon-message" aria-hidden="true" />
              Message Manage
            </router-link>
          </div>

          <div class="admin-settings-panel__actions">
            <button
              type="button"
              class="admin-settings-btn admin-settings-btn--primary"
              :disabled="profileSaving"
              @click="updateUserInfo"
            >
              {{ profileSaving ? "Saving..." : "Save profile" }}
            </button>
          </div>
        </div>

        <div v-show="activeSection === 'password'" class="admin-settings-panel">
          <header class="admin-settings-panel__head">
            <h2 class="admin-settings-panel__title">Password</h2>
          </header>

          <div class="admin-settings-password">
            <section class="admin-settings-password__card">
              <div class="admin-settings-password__card-head">
                <h3 class="admin-settings-password__card-title">Update password</h3>
              </div>

              <div class="admin-settings-panel__fields admin-settings-panel__fields--narrow">
                <label class="hp-field admin-settings-panel__field">
                  <span class="hp-field__label">Current password</span>
                  <input
                    v-model="pwdEntity.oldPwd"
                    class="hp-field__input"
                    type="password"
                    placeholder="Enter current password"
                    autocomplete="current-password"
                  />
                </label>

                <label class="hp-field admin-settings-panel__field">
                  <span class="hp-field__label">New password</span>
                  <input
                    v-model="pwdEntity.newPwd"
                    class="hp-field__input"
                    type="password"
                    placeholder="Set new password"
                    autocomplete="new-password"
                  />
                </label>

                <label class="hp-field admin-settings-panel__field">
                  <span class="hp-field__label">Confirm new password</span>
                  <input
                    v-model="pwdEntity.againPwd"
                    class="hp-field__input"
                    type="password"
                    placeholder="Re-enter new password"
                    autocomplete="new-password"
                  />
                </label>
              </div>

              <div class="admin-settings-panel__actions admin-settings-panel__actions--tight">
                <button
                  type="button"
                  class="admin-settings-btn admin-settings-btn--primary"
                  :disabled="pwdSaving"
                  @click="updateUserPwd"
                >
                  {{ pwdSaving ? "Updating..." : "Update password" }}
                </button>
              </div>
            </section>
          </div>
        </div>

        <div v-show="activeSection === 'account'" class="admin-settings-panel">
          <header class="admin-settings-panel__head">
            <h2 class="admin-settings-panel__title">Account</h2>
          </header>

          <div class="admin-settings-panel__account-card">
            <div class="admin-settings-panel__account-user">
              <UserAvatar :size="48" :src="profile.url" :name="profile.name" />
              <div>
                <p class="admin-settings-panel__account-name">
                  {{ profile.name || "Administrator" }}
                </p>
                <p class="admin-settings-panel__account-email">
                  {{ profile.email || "No email set" }}
                </p>
              </div>
            </div>
            <button
              type="button"
              class="admin-settings-btn admin-settings-btn--ghost"
              @click="loginOut"
            >
              <i class="el-icon-switch-button" aria-hidden="true" />
              Log out
            </button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { clearToken } from "@/utils/storage.js";
import { formatDateShort } from "@/utils/data";
import UserAvatar from "@/components/UserAvatar.vue";

export default {
  components: { UserAvatar },
  name: "AdminSettingsPage",
  data() {
    return {
      activeSection: "profile",
      sections: [
        { id: "profile", label: "Profile", icon: "el-icon-user" },
        { id: "password", label: "Password", icon: "el-icon-lock" },
        { id: "account", label: "Account", icon: "el-icon-switch-button" },
      ],
      profile: {
        id: null,
        url: "",
        name: "",
        email: "",
        account: "",
        role: null,
        createTime: null,
      },
      pwdEntity: { oldPwd: "", newPwd: "", againPwd: "" },
      profileSaving: false,
      pwdSaving: false,
    };
  },
  computed: {
    roleLabel() {
      return this.profile.role === 1 ? "Administrator" : "Member";
    },
    memberSince() {
      const label = formatDateShort(this.profile.createTime);
      return label === "—" ? "" : label;
    },
  },
  async created() {
    await this.loadProfile();
  },
  methods: {
    goBack() {
      if (window.history.length > 1) {
        this.$router.go(-1);
        return;
      }
      if (this.$route.path !== "/adminLayout") {
        this.$router.push("/adminLayout");
      }
    },
    applyUserToProfile(user) {
      if (!user) return;
      this.profile = {
        id: user.id,
        url: user.userAvatar || "",
        name: user.userName || "",
        email: user.userEmail || "",
        account: user.userAccount || "",
        role: user.userRole != null ? user.userRole : this.profile.role,
        createTime: user.createTime || null,
      };
    },
    async loadProfile() {
      try {
        const response = await this.$axios.get("/user/auth");
        const { data } = response;
        if (data.code === 200 && data.data) {
          this.applyUserToProfile(data.data);
          sessionStorage.setItem("userInfo", JSON.stringify(data.data));
          return;
        }
      } catch (e) {
        console.error("Admin profile auth load failed:", e);
      }
      const raw = sessionStorage.getItem("userInfo");
      if (!raw) return;
      try {
        this.applyUserToProfile(JSON.parse(raw));
      } catch {
        /* ignore */
      }
    },
    syncUserInfoToApp() {
      let stored = {};
      try {
        stored = JSON.parse(sessionStorage.getItem("userInfo") || "{}");
      } catch {
        stored = {};
      }
      const payload = {
        ...stored,
        id: this.profile.id,
        userAvatar: this.profile.url,
        userName: this.profile.name,
        userEmail: this.profile.email,
        userAccount: this.profile.account || stored.userAccount,
        userRole: this.profile.role != null ? this.profile.role : stored.userRole,
        createTime: this.profile.createTime || stored.createTime,
      };
      sessionStorage.setItem("userInfo", JSON.stringify(payload));
      this.$root.$emit("app:user-profile-updated", {
        id: payload.id,
        url: payload.userAvatar,
        name: payload.userName,
        email: payload.userEmail,
        role: payload.userRole,
      });
    },
    async handleAvatarSuccess(res) {
      if (res.code !== 200) {
        this.$message.error("Avatar upload failed");
        return;
      }
      this.profile.url = res.data;
      try {
        const response = await this.$axios.put("/user/update", {
          userAvatar: this.profile.url,
        });
        if (response.data.code === 200) {
          this.syncUserInfoToApp();
          this.$message.success("Avatar updated");
          return;
        }
        this.$message.error(response.data.msg || "Avatar update failed");
      } catch (e) {
        this.$message.error("Avatar update failed");
      }
    },
    async updateUserInfo() {
      this.profileSaving = true;
      try {
        const userUpdateDTO = {
          userAvatar: this.profile.url,
          userName: this.profile.name,
          userEmail: this.profile.email,
        };
        const response = await this.$axios.put("/user/update", userUpdateDTO);
        const { data } = response;
        if (data.code === 200) {
          this.syncUserInfoToApp();
          this.$swalToast({
            title: "Profile updated",
            text: data.msg,
            icon: "success",
            timer: 1000,
          });
        }
      } catch (e) {
        this.$swalToast({
          title: "Update failed",
          text: String(e),
          icon: "error",
          timer: 2000,
        });
      } finally {
        this.profileSaving = false;
      }
    },
    async updateUserPwd() {
      const { oldPwd, newPwd, againPwd } = this.pwdEntity;
      if (!oldPwd || !newPwd || !againPwd) {
        this.$message("No field can be empty");
        return;
      }
      if (newPwd !== againPwd) {
        this.$message("Passwords do not match");
        return;
      }
      this.pwdSaving = true;
      try {
        const pwdDTO = {
          oldPwd: this.$md5(this.$md5(oldPwd)),
          newPwd: this.$md5(this.$md5(newPwd)),
        };
        const response = await this.$axios.put("/user/updatePwd", pwdDTO);
        const { data } = response;
        if (data.code === 200) {
          this.pwdEntity = { oldPwd: "", newPwd: "", againPwd: "" };
          this.$swalToast({
            title: "Password updated",
            text: data.msg,
            icon: "success",
            timer: 1000,
          });
          setTimeout(() => {
            clearToken();
            this.$router.push("/login");
          }, 1200);
        } else {
          this.$message.error(data.msg);
        }
      } catch (e) {
        this.$message.error(String(e));
      } finally {
        this.pwdSaving = false;
      }
    },
    async loginOut() {
      const confirmed = await this.$swalLogout();
      if (confirmed) {
        clearToken();
        this.$router.push("/login");
      }
    },
  },
};
</script>

<style scoped lang="scss">
$ink: var(--nb-ink, #24332b);
$accent: #2a9d6f;

.admin-settings-page {
  width: 100%;
  box-sizing: border-box;
}

.admin-settings-page__layout {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
}

.admin-settings-page__nav {
  position: sticky;
  top: 0;
  padding: 18px 14px;
  background: rgba(247, 251, 248, 0.86);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 16px;
  box-shadow: 0 10px 26px rgba(53, 92, 75, 0.08);
}

.admin-settings-page__title {
  margin: 0 0 16px;
  padding: 0 8px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.35rem;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: $ink;
}

.admin-settings-page__back {
  appearance: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin: 0 8px 14px;
  padding: 8px 10px;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  color: #426555;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(126, 197, 160, 0.28);
  border-radius: 10px;
  transition:
    background 0.15s ease,
    color 0.15s ease,
    border-color 0.15s ease;

  &:hover {
    background: #fff;
    color: #1f4d3a;
    border-color: rgba(42, 157, 111, 0.35);
  }

  i {
    font-size: 14px;
  }
}

.admin-settings-page__menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.admin-settings-page__menu-item {
  appearance: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 11px 12px;
  font: inherit;
  font-size: 14px;
  font-weight: 600;
  text-align: left;
  color: #5a7268;
  background: transparent;
  border: none;
  border-radius: 10px;
  transition:
    background 0.15s ease,
    color 0.15s ease;

  i {
    font-size: 17px;
    opacity: 0.9;
  }

  &:hover:not(.is-active) {
    background: rgba(255, 255, 255, 0.75);
    color: #2a6b52;
  }

  &.is-active {
    background: #fff;
    color: #1f4d3a;
    box-shadow: 0 2px 10px rgba(53, 92, 75, 0.08);
  }
}

.admin-settings-page__panel {
  background: rgba(255, 255, 255, 0.97);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 18px;
  box-shadow: 0 12px 32px rgba(53, 92, 75, 0.1);
  padding: clamp(22px, 3vw, 32px) clamp(20px, 4vw, 40px);
  min-width: 0;
  min-height: 70vh;
}

.admin-settings-panel__head {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.18);
}

.admin-settings-panel__title {
  margin: 0 0 8px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.2rem;
  font-weight: 600;
  color: $ink;
}

.admin-settings-panel__lede {
  margin: 0;
  max-width: 48ch;
  font-size: 14px;
  line-height: 1.5;
  color: rgba(36, 51, 43, 0.58);
}

.admin-settings-profile__summary {
  display: flex;
  flex-wrap: wrap;
  gap: 20px 28px;
  align-items: center;
  padding: 20px;
  margin-bottom: 22px;
  background: linear-gradient(
    135deg,
    rgba(231, 246, 238, 0.85) 0%,
    rgba(255, 255, 255, 0.95) 55%
  );
  border: 1px solid rgba(126, 197, 160, 0.28);
  border-radius: 16px;
}

.admin-settings-profile__summary-media {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.admin-settings-profile__uploader :deep(.el-upload) {
  border-radius: 50%;
  cursor: pointer;
}

.admin-settings-profile__avatar,
.admin-settings-profile__avatar-placeholder {
  width: 108px;
  height: 108px;
  border-radius: 50%;
}

.admin-settings-profile__avatar {
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 6px 20px rgba(53, 92, 75, 0.15);
  display: block;
}

.admin-settings-profile__avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(126, 197, 160, 0.2);
  color: #3d8b6f;
  font-size: 28px;
  border: 2px dashed rgba(42, 157, 111, 0.35);
}

.admin-settings-profile__photo-hint {
  font-size: 12px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.55);
}

.admin-settings-profile__summary-body {
  flex: 1;
  min-width: 200px;
}

.admin-settings-profile__display-name {
  margin: 0 0 6px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.35rem;
  font-weight: 600;
  color: $ink;
  letter-spacing: -0.02em;
}

.admin-settings-profile__display-email {
  margin: 0 0 12px;
  font-size: 14px;
  color: rgba(53, 82, 71, 0.68);
  word-break: break-word;
}

.admin-settings-profile__badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 12px;
  align-items: center;
}

.admin-settings-profile__role-pill {
  display: inline-flex;
  padding: 4px 12px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: #1f6b4f;
  background: rgba(42, 157, 111, 0.14);
  border: 1px solid rgba(42, 157, 111, 0.28);
  border-radius: 999px;
}

.admin-settings-profile__since {
  font-size: 13px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.55);
}

.admin-settings-profile__section-title {
  margin: 0 0 14px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(53, 82, 71, 0.55);
}

.admin-settings-profile__meta {
  margin: 0 0 28px;
  padding: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px 24px;
}

.admin-settings-profile__meta-row {
  margin: 0;

  dt {
    margin: 0 0 4px;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    color: rgba(53, 82, 71, 0.48);
  }

  dd {
    margin: 0;
    font-size: 14px;
    font-weight: 600;
    color: $ink;
    word-break: break-word;
  }
}

.admin-settings-profile__links {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 8px;
}

.admin-settings-profile__link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 650;
  color: #355247;
  text-decoration: none;
  background: rgba(231, 246, 238, 0.65);
  border: 1px solid rgba(126, 197, 160, 0.35);
  border-radius: 999px;
  transition:
    background 0.15s ease,
    color 0.15s ease;

  i {
    font-size: 15px;
    color: $accent;
  }

  &:hover {
    background: rgba(42, 157, 111, 0.12);
    color: #1f4d3a;
  }
}

.admin-settings-panel__fields {
  display: flex;
  flex-direction: column;
  gap: 14px;

  &--narrow {
    max-width: 420px;
  }

  &--profile {
    max-width: 480px;
    margin-bottom: 24px;
  }
}

.admin-settings-panel__field {
  margin: 0;
}

.admin-settings-panel__actions {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(126, 197, 160, 0.15);
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.admin-settings-panel__actions--tight {
  margin-top: 18px;
}

.admin-settings-password {
  max-width: 460px;
}

.admin-settings-password__card {
  background: rgba(247, 251, 248, 0.86);
  border: 1px solid rgba(126, 197, 160, 0.26);
  border-radius: 16px;
  padding: 20px;
}

.admin-settings-password__card-head {
  margin-bottom: 18px;
}

.admin-settings-password__card-title {
  margin: 0 0 6px;
  font-size: 1rem;
  font-weight: 700;
  color: $ink;
}

.admin-settings-password__card-text {
  margin: 0;
  font-size: 13px;
  line-height: 1.5;
  color: rgba(36, 51, 43, 0.58);
}

.admin-settings-panel__account-card {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  max-width: 520px;
  padding: 20px;
  background: rgba(247, 251, 248, 0.9);
  border: 1px solid rgba(126, 197, 160, 0.28);
  border-radius: 14px;
}

.admin-settings-panel__account-user {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.admin-settings-panel__account-name {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 700;
  color: $ink;
}

.admin-settings-panel__account-email {
  margin: 0;
  font-size: 13px;
  color: rgba(53, 82, 71, 0.65);
  word-break: break-word;
}

.admin-settings-btn {
  appearance: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: 1px solid rgba(126, 197, 160, 0.38);
  border-radius: 999px;
  padding: 10px 20px;
  font: inherit;
  font-size: 14px;
  font-weight: 650;
  cursor: pointer;
  transition:
    background 0.15s ease,
    border-color 0.15s ease,
    color 0.15s ease,
    transform 0.12s ease;

  &:disabled {
    opacity: 0.65;
    cursor: not-allowed;
  }

  &--primary {
    color: #fff;
    background: $accent;
    border-color: rgba(42, 157, 111, 0.55);
    box-shadow: 0 2px 10px rgba(42, 157, 111, 0.22);

    &:hover:not(:disabled) {
      background: #248f63;
      transform: translateY(-1px);
    }
  }

  &--ghost {
    color: #9b3d3d;
    background: rgba(255, 245, 245, 0.95);
    border-color: rgba(201, 90, 90, 0.35);

    &:hover:not(:disabled) {
      background: rgba(201, 90, 90, 0.1);
    }
  }
}

@media (max-width: 900px) {
  .admin-settings-page__layout {
    grid-template-columns: 1fr;
  }

  .admin-settings-page__nav {
    position: static;
  }
}

@media (max-width: 720px) {
  .admin-settings-page__menu {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 6px;
  }

  .admin-settings-page__menu-item {
    width: auto;
    flex: 1 1 auto;
    min-width: calc(33% - 6px);
    justify-content: center;
  }

  .admin-settings-profile__summary {
    flex-direction: column;
    text-align: center;
  }

  .admin-settings-profile__summary-body {
    width: 100%;
  }

  .admin-settings-profile__badges {
    justify-content: center;
  }
}
</style>
