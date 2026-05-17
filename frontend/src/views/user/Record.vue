<template>
  <div class="record-page">
    <header class="record-page__header">
      <button type="button" class="record-page__back" @click="goBack">
        <i class="el-icon-arrow-left" aria-hidden="true" />
        Back
      </button>
      <h1 class="record-page__title">Health Records</h1>
      <span class="record-page__header-spacer" aria-hidden="true" />
    </header>

    <div class="record-page__layout">
      <aside class="record-page__sidebar nb-surface">
        <el-tabs v-model="activeName" class="record-page__tabs" @tab-click="handleClick">
          <el-tab-pane label="Global models" name="first" />
          <el-tab-pane label="My models" name="second" />
        </el-tabs>

        <button type="button" class="record-page__btn-primary" @click="addModel">
          Add a model
          <i class="el-icon-right" aria-hidden="true" />
        </button>

        <div class="record-page__search">
          <label class="record-page__label" for="model-search">Find by name</label>
          <div class="record-page__search-row">
            <el-input
              id="model-search"
              v-model="userHealthModel.name"
              class="record-page__search-input"
              size="small"
              placeholder="Type to filter…"
              clearable
              @clear="handleFilterClear"
            />
            <el-button
              type="primary"
              class="record-page__search-btn"
              size="small"
              @click="searModel"
            >
              Search
            </el-button>
          </div>
        </div>

        <div class="record-page__list">
          <button
            v-for="(model, index) in modelList"
            :key="model.id || index"
            type="button"
            class="record-page__model"
            :class="{ 'is-selected': isModelPicked(model) }"
            @click="modelSelected(model)"
          >
            <img class="record-page__model-cover" :src="model.cover" :alt="''" />
            <div class="record-page__model-body">
              <span class="record-page__model-name">{{ model.name }}</span>
              <span class="record-page__model-hint">{{ modelHint(model) }}</span>
              <div v-if="!model.isGlobal" class="record-page__model-actions">
                <button
                  type="button"
                  class="record-page__link"
                  @click.stop="updateModel(model)"
                >
                  Edit
                </button>
                <button
                  type="button"
                  class="record-page__link record-page__link--danger"
                  @click.stop="deleteModel(model)"
                >
                  Delete
                </button>
              </div>
            </div>
          </button>
        </div>
      </aside>

      <main class="record-page__main nb-surface">
        <div class="record-page__main-head">
          <h2 class="record-page__panel-title">Data entry</h2>
          <button
            v-if="selectedModel.length"
            type="button"
            class="record-page__reset"
            @click="clearData"
          >
            Clear form
          </button>
        </div>

        <div v-if="selectedModel.length === 0" class="record-page__empty">
          <el-empty description="">
            <template slot="image">
              <div class="record-page__empty-icon" aria-hidden="true">
                <i class="el-icon-notebook-2" />
              </div>
            </template>
            <template slot="description">
              <p class="record-page__empty-title">Pick something to log</p>
              <p class="record-page__empty-text">
                Choose one or more models on the left. Your inputs stay here until
                you save.
              </p>
            </template>
          </el-empty>
        </div>

        <div v-else class="record-page__fields">
          <div
            v-for="(model, index) in selectedModel"
            :key="model.id || index"
            class="record-page__field"
          >
            <label class="record-page__field-label" :for="'val-' + model.id">{{
              model.name
            }}</label>
            <span class="record-page__field-unit">{{ model.unit }}</span>
            <input
              :id="'val-' + model.id"
              v-model="model.value"
              class="record-page__field-input"
              type="text"
              inputmode="decimal"
              :placeholder="'Typical range: ' + model.valueRange"
            />
          </div>
        </div>

        <div v-if="selectedModel.length" class="record-page__footer">
          <button type="button" class="record-page__btn-save" @click="toRecord">
            Save readings
            <i class="el-icon-right" aria-hidden="true" />
          </button>
          <p class="record-page__footer-hint">
            We’ll send you back to the app home after a good save.
          </p>
        </div>
      </main>
    </div>

    <el-dialog
      :show-close="false"
      :visible.sync="dialogUserOperaion"
      custom-class="record-page__dialog"
      width="440px"
    >
      <div slot="title">
        <p class="record-page__dialog-title">
          {{ !isOperation ? "Add health model" : "Edit health model" }}
        </p>
      </div>
      <div class="record-page__dialog-body">
        <p class="record-page__dialog-field-label">Icon</p>
        <el-row class="record-page__dialog-row">
          <el-upload
            class="avatar-uploader"
            action="/api/personal-heath/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img
              v-if="data.cover"
              :src="data.cover"
              class="record-page__dialog-thumb"
            />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-row>
        <p class="record-page__dialog-field-label">Configuration name *</p>
        <input
          v-model="data.name"
          class="record-page__dialog-input"
          placeholder="e.g. Morning walk distance"
        />
        <p class="record-page__dialog-field-label">Unit *</p>
        <input v-model="data.unit" class="record-page__dialog-input" placeholder="km, bpm…" />
        <p class="record-page__dialog-field-label">Short code *</p>
        <input
          v-model="data.symbol"
          class="record-page__dialog-input"
          placeholder="Shown in tables (or “none”)"
        />
        <p class="record-page__dialog-field-label">Healthy range * (min, max)</p>
        <input
          v-model="data.valueRange"
          class="record-page__dialog-input"
          placeholder="e.g. 60,120"
        />
        <p class="record-page__dialog-field-label">Description *</p>
        <el-input
          v-model="data.detail"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 4 }"
          placeholder="What should future-you remember about this metric?"
        />
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          v-if="!isOperation"
          size="small"
          type="primary"
          class="record-page__dialog-btn"
          @click="addOperation"
        >
          Add
        </el-button>
        <el-button
          v-else
          size="small"
          type="primary"
          class="record-page__dialog-btn"
          @click="updateOperation"
        >
          Save changes
        </el-button>
        <el-button size="small" @click="cannel">Cancel</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "HealthRecordPage",
  data() {
    return {
      data: { cover: "" },
      userInfo: {},
      modelList: [],
      activeName: "first",
      userHealthModel: { isGlobal: true },
      dialogUserOperaion: false,
      isOperation: false,
      userId: null,
      selectedModel: [],
    };
  },
  created() {
    this.getUserInfo();
    this.getAllModelConfig();
    this.getUser();
  },
  methods: {
    modelHint(model) {
      const u = (model.unit || "").trim();
      const s = (model.symbol || "").trim();
      const symNone = !s || s.toLowerCase() === "none";
      if (!u && symNone) {
        return "Tap to add to today’s form";
      }
      if (u && symNone) {
        return `Measured in ${u} — add a number you’re happy with`;
      }
      if (u && s) {
        return `${u} · ${s}`;
      }
      return s || "Custom tracking";
    },
    isModelPicked(model) {
      return this.selectedModel.some((m) => m.id === model.id);
    },
    async clearData() {
      const confirmed = await this.$swalConfirm({
        title: "Clear this form?",
        text: "You’ll need to pick models again from the list.",
        icon: "question",
        confirmButtonText: "Clear",
      });
      if (confirmed) {
        this.selectedModel = [];
      }
    },
    cannel() {
      this.data = {};
      this.dialogUserOperaion = false;
      this.isOperation = false;
      this.cover = "";
    },
    updateOperation() {
      this.$axios.put("/health-model-config/update", this.data).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.dialogUserOperaion = false;
          this.isOperation = false;
          this.data = {};
          this.$swal.fire({
            title: "Updated",
            text: "Model updated successfully",
            icon: "success",
            showConfirmButton: false,
            timer: 1000,
          });
          this.getAllModelConfig();
        }
      });
    },
    updateModel(model) {
      this.data = { ...model };
      this.dialogUserOperaion = true;
      this.isOperation = true;
    },
    async deleteModel(model) {
      const confirmed = await this.$swalConfirm({
        title: `Delete “${model.name}”?`,
        text: "This cannot be undone.",
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        const ids = [model.id];
        this.$axios.post("/health-model-config/batchDelete", ids).then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.$swal.fire({
              title: "Removed",
              text: "Model deleted",
              icon: "success",
              showConfirmButton: false,
              timer: 1000,
            });
            this.getAllModelConfig();
            this.selectedModel = this.selectedModel.filter(
              (entity) => entity.id !== model.id,
            );
          }
        });
      }
    },
    goBack() {
      this.$router.push("/user");
    },
    toRecord() {
      const missing = this.selectedModel.some(
        (m) => m.value === undefined || String(m.value).trim() === "",
      );
      if (missing) {
        this.$message.warning("Fill in each value before saving.");
        return;
      }
      const userHealths = this.selectedModel.map((entity) => ({
        healthModelConfigId: entity.id,
        value: entity.value,
      }));
      this.$axios.post("/user-health/save", userHealths).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.$notify({
            title: "Saved",
            message: "Your readings were stored.",
            type: "success",
          });
          setTimeout(() => {
            this.$router.push("/user");
          }, 2000);
        }
      });
    },
    modelSelected(model) {
      const saveFlag = this.selectedModel.find((entity) => entity.id === model.id);
      if (!saveFlag) {
        this.selectedModel.push({ ...model, value: model.value || "" });
      }
    },
    searModel() {
      this.getAllModelConfig();
    },
    handleFilterClear() {
      this.userHealthModel.name = "";
      this.getAllModelConfig();
    },
    handleAvatarSuccess(res) {
      if (res.code !== 200) {
        this.$message.error("Upload failed");
        return;
      }
      this.$message.success("Image uploaded");
      this.data.cover = res.data;
    },
    getUser() {
      const userInfo = sessionStorage.getItem("userInfo");
      const entity = JSON.parse(userInfo);
      this.userId = entity.id;
    },
    async addOperation() {
      try {
        this.data.userId = this.userId;
        const response = await this.$axios.post("/health-model-config/save", this.data);
        this.$message[response.data.code === 200 ? "success" : "error"](response.data.msg);
        if (response.data.code === 200) {
          this.dialogUserOperaion = false;
          this.getAllModelConfig();
          this.data = {};
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Something went wrong. Try again.");
      }
    },
    addModel() {
      this.data = {
        cover: "",
        name: "",
        unit: "",
        symbol: "",
        valueRange: "",
        detail: "",
      };
      this.dialogUserOperaion = true;
      this.isOperation = false;
    },
    handleClick() {
      this.userHealthModel = {};
      if (this.activeName === "first") {
        this.userHealthModel.isGlobal = true;
      } else {
        const userInfo = sessionStorage.getItem("userInfo");
        const entity = JSON.parse(userInfo);
        this.userHealthModel.userId = entity.id;
      }
      this.getAllModelConfig();
    },
    getAllModelConfig() {
      this.$axios.post("/health-model-config/query", this.userHealthModel).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.modelList = data.data;
        }
      });
    },
    getUserInfo() {
      const userInfo = sessionStorage.getItem("userInfo");
      this.userInfo = JSON.parse(userInfo);
    },
  },
};
</script>

<style scoped lang="scss">
.record-page {
  min-height: 100vh;
  box-sizing: border-box;
  padding: clamp(12px, 2vw, 20px) clamp(12px, 2.5vw, 32px) 40px;
  background: var(--nb-bg-soft, #e7f6ee);
}

.record-page__header {
  max-width: 1200px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.record-page__back {
  appearance: none;
  border: none;
  background: rgba(255, 255, 255, 0.85);
  padding: 8px 14px;
  border-radius: 999px;
  font: inherit;
  font-size: 14px;
  font-weight: 600;
  color: #355247;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid rgba(126, 197, 160, 0.35);
  box-shadow: var(--nb-shadow-sm, 0 4px 12px rgba(53, 92, 75, 0.08));

  &:hover {
    background: #fff;
    color: #2a9d6f;
  }
}

.record-page__title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: clamp(1.2rem, 2.2vw, 1.5rem);
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--nb-ink, #24332b);
  text-align: center;
  flex: 1;
}

.record-page__header-spacer {
  width: 140px;
  flex-shrink: 0;
}

.record-page__layout {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(260px, 320px) 1fr;
  gap: 20px;
  align-items: start;
}

@media (max-width: 900px) {
  .record-page__layout {
    grid-template-columns: 1fr;
  }

  .record-page__header-spacer {
    display: none;
  }

  .record-page__title {
    text-align: right;
    flex: 0;
  }
}

.record-page__sidebar,
.record-page__main {
  padding: clamp(16px, 2vw, 22px);
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(126, 197, 160, 0.22);
}

.record-page__tabs {
  margin-bottom: 14px;

  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }

  :deep(.el-tabs__item) {
    font-weight: 600;
    color: var(--nb-muted, #5c6560);
  }

  :deep(.el-tabs__item.is-active) {
    color: #2a9d6f;
  }

  :deep(.el-tabs__active-bar) {
    background-color: #2a9d6f;
  }
}

.record-page__btn-primary {
  appearance: none;
  cursor: pointer;
  width: 100%;
  margin-bottom: 18px;
  padding: 11px 16px;
  font: inherit;
  font-size: 14px;
  font-weight: 650;
  color: #fff;
  background: #2a9d6f;
  border: none;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 3px 12px rgba(42, 157, 111, 0.28);
  transition:
    filter 0.15s ease,
    transform 0.12s ease;

  &:hover {
    filter: brightness(1.04);
    transform: translateY(-1px);
  }
}

.record-page__search {
  margin-bottom: 14px;
}

.record-page__label {
  display: block;
  font-size: 12px;
  font-weight: 650;
  color: #5a7a6d;
  margin-bottom: 6px;
}

.record-page__search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.record-page__search-input {
  flex: 1;
  min-width: 0;

  :deep(.el-input__inner) {
    border-radius: 10px;
    border-color: rgba(126, 197, 160, 0.45);
  }
}

.record-page__search-btn {
  border-radius: 999px;
  font-weight: 600;
  background: #2a9d6f !important;
  border-color: #2a9d6f !important;
}

.record-page__list {
  max-height: min(520px, calc(100vh - 340px));
  overflow-y: auto;
  padding-right: 4px;
  margin-right: -4px;
}

.record-page__model {
  display: flex;
  gap: 12px;
  width: 100%;
  text-align: left;
  padding: 12px;
  margin-bottom: 8px;
  border: 2px solid rgba(126, 197, 160, 0.2);
  border-radius: 14px;
  background: rgba(247, 251, 248, 0.65);
  cursor: pointer;
  font: inherit;
  transition:
    border-color 0.15s ease,
    background 0.15s ease,
    box-shadow 0.15s ease;

  &:hover {
    border-color: rgba(42, 157, 111, 0.35);
    background: #fff;
  }

  &.is-selected {
    border-color: #2a9d6f;
    background: rgba(42, 157, 111, 0.08);
    box-shadow: 0 0 0 3px rgba(42, 157, 111, 0.12);
  }
}

.record-page__model-cover {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  object-fit: cover;
  flex-shrink: 0;
}

.record-page__model-body {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.record-page__model-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--nb-ink, #24332b);
  line-height: 1.25;
}

.record-page__model-hint {
  font-size: 12px;
  line-height: 1.4;
  color: rgba(53, 82, 71, 0.72);
}

.record-page__model-actions {
  display: flex;
  gap: 10px;
  margin-top: 4px;
}

.record-page__link {
  appearance: none;
  border: none;
  background: none;
  padding: 0;
  font: inherit;
  font-size: 12px;
  font-weight: 600;
  color: #2a9d6f;
  cursor: pointer;

  &--danger {
    color: #b45353;
  }
}

.record-page__main-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.22);
}

.record-page__panel-title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--nb-ink, #24332b);
}

.record-page__reset {
  appearance: none;
  border: none;
  background: none;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  color: #5a7a6d;
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 3px;

  &:hover {
    color: #2a9d6f;
  }
}

.record-page__empty {
  padding: 32px 16px 48px;

  :deep(.el-empty__description) {
    margin-top: 8px;
  }
}

.record-page__empty-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 12px;
  border-radius: 18px;
  background: rgba(126, 197, 160, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  color: #5a8f7a;
}

.record-page__empty-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 650;
  color: var(--nb-ink, #24332b);
}

.record-page__empty-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  color: var(--nb-muted, rgba(36, 51, 43, 0.6));
  max-width: 36ch;
  margin-left: auto;
  margin-right: auto;
}

.record-page__fields {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.record-page__field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-page__field-label {
  font-size: 14px;
  font-weight: 650;
  color: #355247;
}

.record-page__field-unit {
  font-size: 12px;
  color: rgba(53, 82, 71, 0.65);
  font-weight: 600;
  margin-top: -4px;
}

.record-page__field-input {
  font: inherit;
  font-size: 18px;
  font-weight: 650;
  padding: 14px 16px;
  border-radius: 12px;
  border: 2px solid rgba(126, 197, 160, 0.35);
  outline: none;
  background: #f8fbf9;
  color: var(--nb-ink, #24332b);
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease;

  &:focus {
    border-color: #2a9d6f;
    background: #fff;
    box-shadow: 0 0 0 3px rgba(42, 157, 111, 0.15);
  }

  &::placeholder {
    color: rgba(36, 51, 43, 0.35);
    font-weight: 500;
    font-size: 14px;
  }
}

.record-page__footer {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid rgba(126, 197, 160, 0.22);
}

.record-page__btn-save {
  appearance: none;
  cursor: pointer;
  padding: 12px 24px;
  font: inherit;
  font-size: 15px;
  font-weight: 650;
  color: #fff;
  background: #1f3d32;
  border: none;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 16px rgba(31, 61, 50, 0.25);
  transition:
    filter 0.15s ease,
    transform 0.12s ease;

  &:hover {
    filter: brightness(1.06);
    transform: translateY(-1px);
  }
}

.record-page__footer-hint {
  margin: 12px 0 0;
  font-size: 13px;
  color: var(--nb-muted, rgba(36, 51, 43, 0.55));
}

.record-page__dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 650;
  color: var(--nb-ink, #24332b);
}

.record-page__dialog-body {
  padding: 0 4px 8px;
}

.record-page__dialog-field-label {
  margin: 14px 0 6px;
  font-size: 13px;
  font-weight: 600;
  color: #355247;
}

.record-page__dialog-row {
  margin-top: 6px;
}

.record-page__dialog-thumb {
  height: 64px;
  width: 64px;
  border-radius: 12px;
  object-fit: cover;
}

.record-page__dialog-input {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  font: inherit;
  font-size: 14px;
  border-radius: 10px;
  border: 1px solid rgba(126, 197, 160, 0.45);
  outline: none;

  &:focus {
    border-color: #2a9d6f;
  }
}

.record-page__dialog-btn {
  background: #2a9d6f !important;
  border-color: #2a9d6f !important;
}
</style>
