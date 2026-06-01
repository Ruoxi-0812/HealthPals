<template>
  <div class="record-page">
    <header class="record-page__header">
      <button type="button" class="record-page__back" @click="goBack">
        <i class="el-icon-arrow-left" aria-hidden="true" />
        Back
      </button>
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
            <img
              class="record-page__model-cover"
              :src="modelCoverSrc(model)"
              :alt="model.name || ''"
            />
            <div class="record-page__model-body">
              <span class="record-page__model-name">{{ model.name }}</span>
              <span class="record-page__model-hint">{{ modelHint(model) }}</span>
              <span v-if="isModelPicked(model)" class="record-page__model-badge"
                >In form</span
              >
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
          <div class="record-page__main-head-text">
            <h2 class="record-page__panel-title">Data entry</h2>
            <p v-if="selectedModel.length" class="record-page__panel-meta">
              {{ selectedModel.length }}
              {{ selectedModel.length === 1 ? "metric" : "metrics" }} selected
            </p>
          </div>
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
          <div class="record-page__empty-intro">
            <div class="record-page__empty-icon" aria-hidden="true">
              <i class="el-icon-notebook-2" />
            </div>
            <p class="record-page__empty-title">Pick something to log</p>
            <p class="record-page__empty-text">
              Choose metrics from the left to start logging.
            </p>
          </div>

          <div v-if="recentModels.length || suggestedBundles.length" class="record-page__empty-side">
            <section class="record-page__section-card">
              <div class="record-page__section-head">
                <p class="record-page__quick-label">Start faster</p>
              </div>

              <div v-if="recentModels.length" class="record-page__compact-group">
                <p class="record-page__compact-label">Recently used</p>
                <div class="record-page__compact-list">
                  <button
                    v-for="model in recentModels"
                    :key="'recent-' + model.id"
                    type="button"
                    class="record-page__compact-pill"
                    @click="modelSelected(model)"
                  >
                    {{ model.name }}
                  </button>
                </div>
              </div>

              <div v-if="suggestedBundles.length" class="record-page__compact-group">
                <p class="record-page__compact-label">Suggested sets</p>
                <div class="record-page__bundle-list">
                  <button
                    v-for="bundle in suggestedBundles"
                    :key="bundle.id"
                    type="button"
                    class="record-page__bundle"
                    @click="selectBundle(bundle)"
                  >
                    <div class="record-page__bundle-top">
                      <span class="record-page__bundle-name">{{ bundle.label }}</span>
                      <span class="record-page__bundle-count">
                        {{ bundle.models.length }}
                      </span>
                    </div>
                    <p class="record-page__bundle-preview">
                      {{ bundle.models.map((model) => model.name).join(" · ") }}
                    </p>
                  </button>
                </div>
              </div>
            </section>
          </div>
          <p
            v-else
            class="record-page__empty-fallback"
          >
            No models in this tab yet. Switch tabs or add your own metric.
          </p>
        </div>

        <div v-else class="record-page__selected">
          <div class="record-page__selected-head">
            <p class="record-page__selected-label">Selected</p>
            <p class="record-page__selected-copy">
              Tap any chip to remove it.
            </p>
          </div>
          <div class="record-page__selected-chips">
            <button
              v-for="model in selectedModel"
              :key="'selected-' + model.id"
              type="button"
              class="record-page__selected-chip"
              @click="modelSelected(model)"
            >
              <span class="record-page__selected-chip-name">{{ model.name }}</span>
              <span
                v-if="displayUnit(model.unit)"
                class="record-page__selected-chip-unit"
              >
                {{ displayUnit(model.unit) }}
              </span>
              <i class="el-icon-close" aria-hidden="true" />
            </button>
          </div>
        </div>

        <div v-if="selectedModel.length" class="record-page__fields">
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
import { healthModelCoverSrc } from "@/utils/coverImage";

export default {
  name: "HealthRecordPage",
  data() {
    return {
      data: { cover: "" },
      userInfo: {},
      modelList: [],
      recentRecords: [],
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
    this.getUser();
    this.getAllModelConfig();
    this.loadRecentRecords();
  },
  computed: {
    quickPickModels() {
      return (this.modelList || []).slice(0, 6);
    },
    recentModels() {
      if (!this.recentRecords.length || !this.modelList.length) {
        return [];
      }
      const byId = new Map(this.modelList.map((model) => [model.id, model]));
      const seen = new Set();
      const recent = [];
      this.recentRecords.forEach((record) => {
        const id = record.healthModelConfigId;
        if (!id || seen.has(id) || !byId.has(id)) {
          return;
        }
        seen.add(id);
        recent.push(byId.get(id));
      });
      return recent.slice(0, 4);
    },
    suggestedBundles() {
      const definitions = [
        {
          id: "daily-basics",
          label: "Daily basics",
          description: "Core everyday wellness checks.",
          keywords: ["weight", "blood pressure", "heart rate"],
        },
        {
          id: "activity-check",
          label: "Activity check-in",
          description: "Useful after walking or workouts.",
          keywords: ["step", "run", "exercise", "heart rate"],
        },
        {
          id: "lab-follow-up",
          label: "Lab follow-up",
          description: "Track numbers you want to keep an eye on.",
          keywords: ["alanine", "alt", "sugar", "glucose", "cholesterol"],
        },
      ];
      const used = new Set();
      const bundles = definitions
        .map((bundle) => {
          const models = this.modelList.filter((model) => {
            const haystack = `${model.name || ""} ${model.symbol || ""}`.toLowerCase();
            return (
              !used.has(model.id) &&
              bundle.keywords.some((keyword) => haystack.includes(keyword))
            );
          });
          const picked = models.slice(0, 3);
          picked.forEach((model) => used.add(model.id));
          if (picked.length < 2) {
            return null;
          }
          return {
            ...bundle,
            models: picked,
          };
        })
        .filter(Boolean);
      if (!bundles.length && this.quickPickModels.length >= 3) {
        return [
          {
            id: "starter-set",
            label: "Starter set",
            description: "A simple place to start logging today.",
            models: this.quickPickModels.slice(0, 3),
          },
        ];
      }
      return bundles;
    },
  },
  methods: {
    modelCoverSrc(model) {
      return healthModelCoverSrc(model);
    },
    displayUnit(unit) {
      const raw = (unit || "").trim();
      if (!raw || /^none$/i.test(raw)) {
        return "";
      }
      return raw;
    },
    modelHint(model) {
      const u = (model.unit || "").trim();
      const s = (model.symbol || "").trim();
      const symNone = !s || /^none$/i.test(s);
      if (!u && symNone) {
        return "Tap to add to form";
      }
      if (u && symNone) {
        return `Measured in ${u}`;
      }
      if (u && s && s.toLowerCase() !== u.toLowerCase()) {
        return `${u} · ${s}`;
      }
      if (u) {
        return u;
      }
      return s || "Custom tracking";
    },
    isModelPicked(model) {
      return this.selectedModel.some((m) => m.id === model.id);
    },
    addModels(models) {
      (models || []).forEach((model) => {
        if (!this.isModelPicked(model)) {
          this.selectedModel.push({ ...model, value: model.value || "" });
        }
      });
    },
    selectBundle(bundle) {
      this.addModels(bundle.models);
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
      const idx = this.selectedModel.findIndex((entity) => entity.id === model.id);
      if (idx >= 0) {
        this.selectedModel.splice(idx, 1);
      } else {
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
    loadRecentRecords() {
      if (this.userId == null) {
        return;
      }
      this.$axios
        .post("/user-health/query", {
          userId: this.userId,
          current: 1,
          size: 24,
        })
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.recentRecords = data.data || [];
          }
        })
        .catch((error) => {
          console.error("Error loading recent health records:", error);
          this.recentRecords = [];
        });
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
  width: 100%;
  max-width: none;
  margin: 0 0 16px;
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
  width: 100%;
  max-width: none;
  margin: 0;
  display: grid;
  grid-template-columns: minmax(280px, 340px) minmax(0, 1fr);
  gap: 16px;
  align-items: stretch;
  min-height: calc(100vh - 100px);
}

@media (max-width: 900px) {
  .record-page__layout {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .record-page__empty {
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
  border-radius: 16px;
  box-shadow: 0 8px 22px rgba(53, 92, 75, 0.08);
}

.record-page__sidebar {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.record-page__main {
  display: flex;
  flex-direction: column;
  min-height: 0;
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
  flex: 1;
  max-height: min(560px, calc(100vh - 320px));
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

.record-page__model-badge {
  align-self: flex-start;
  margin-top: 2px;
  padding: 2px 8px;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: #2a9d6f;
  background: rgba(42, 157, 111, 0.12);
  border-radius: 999px;
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
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.22);
  flex-shrink: 0;
}

.record-page__main-head-text {
  min-width: 0;
}

.record-page__panel-title {
  margin: 0;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--nb-ink, #24332b);
}

.record-page__panel-meta {
  margin: 6px 0 0;
  font-size: 13px;
  font-weight: 600;
  color: rgba(36, 51, 43, 0.52);
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
  flex: 1;
  display: grid;
  grid-template-columns: minmax(220px, 1fr) minmax(280px, 1.2fr);
  gap: clamp(20px, 4vw, 40px);
  align-items: start;
  padding: 8px 0 16px;
  min-height: 320px;
}

.record-page__empty-intro {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  text-align: left;
}

.record-page__empty-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  border-radius: 18px;
  background: rgba(126, 197, 160, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #5a8f7a;
}

.record-page__empty-title {
  margin: 0 0 8px;
  font-size: 1.2rem;
  font-weight: 650;
  color: var(--nb-ink, #24332b);
}

.record-page__empty-text {
  margin: 0 0 16px;
  font-size: 14px;
  line-height: 1.55;
  color: var(--nb-muted, rgba(36, 51, 43, 0.6));
  max-width: 34ch;
}

.record-page__quick {
  min-width: 0;
}

.record-page__empty-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.record-page__section-card {
  padding: 14px;
  background: rgba(247, 251, 248, 0.7);
  border: 1px solid rgba(126, 197, 160, 0.24);
  border-radius: 14px;
}

.record-page__section-head {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 6px 10px;
  margin-bottom: 12px;
}

.record-page__quick-label {
  margin: 0;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: rgba(36, 51, 43, 0.45);
}

.record-page__section-copy {
  font-size: 12px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.52);
}

.record-page__compact-group + .record-page__compact-group {
  margin-top: 14px;
}

.record-page__compact-label {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  color: #355247;
}

.record-page__compact-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.record-page__compact-pill {
  appearance: none;
  cursor: pointer;
  padding: 8px 12px;
  font: inherit;
  font-size: 12px;
  font-weight: 650;
  color: #355247;
  background: #fff;
  border: 1px solid rgba(126, 197, 160, 0.32);
  border-radius: 999px;
  transition:
    background 0.15s ease,
    border-color 0.15s ease;

  &:hover {
    background: rgba(231, 246, 238, 0.8);
    border-color: rgba(42, 157, 111, 0.45);
  }
}

.record-page__quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.record-page__quick-card {
  appearance: none;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding: 12px;
  text-align: left;
  font: inherit;
  background: rgba(247, 251, 248, 0.9);
  border: 1px solid rgba(126, 197, 160, 0.28);
  border-radius: 14px;
  transition:
    border-color 0.15s ease,
    background 0.15s ease,
    transform 0.12s ease;

  &:hover {
    border-color: rgba(42, 157, 111, 0.45);
    background: #fff;
    transform: translateY(-2px);
  }
}

.record-page__quick-cover {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  object-fit: cover;
}

.record-page__quick-name {
  font-size: 13px;
  font-weight: 700;
  color: var(--nb-ink, #24332b);
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.record-page__quick-hint {
  font-size: 11px;
  line-height: 1.35;
  color: rgba(53, 82, 71, 0.65);
}

.record-page__bundle-list {
  display: grid;
  gap: 10px;
}

.record-page__bundle {
  appearance: none;
  cursor: pointer;
  width: 100%;
  padding: 10px 12px;
  text-align: left;
  font: inherit;
  background: #fff;
  border: 1px solid rgba(126, 197, 160, 0.28);
  border-radius: 12px;
  transition:
    border-color 0.15s ease,
    transform 0.12s ease,
    box-shadow 0.15s ease;

  &:hover {
    border-color: rgba(42, 157, 111, 0.42);
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(53, 92, 75, 0.08);
  }
}

.record-page__bundle-top {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.record-page__bundle-name {
  font-size: 13px;
  font-weight: 700;
  color: var(--nb-ink, #24332b);
}

.record-page__bundle-count {
  font-size: 11px;
  font-weight: 700;
  color: #2a9d6f;
  background: rgba(42, 157, 111, 0.12);
  border-radius: 999px;
  padding: 2px 8px;
}

.record-page__bundle-text {
  margin: 6px 0 0;
  font-size: 12px;
  line-height: 1.4;
  color: rgba(53, 82, 71, 0.66);
}

.record-page__bundle-preview {
  margin: 6px 0 0;
  font-size: 12px;
  line-height: 1.4;
  color: rgba(36, 51, 43, 0.72);
}

.record-page__empty-fallback {
  margin: 0;
  padding: 24px;
  font-size: 14px;
  line-height: 1.5;
  color: rgba(36, 51, 43, 0.55);
  background: rgba(231, 246, 238, 0.5);
  border-radius: 12px;
  border: 1px dashed rgba(126, 197, 160, 0.35);
}

.record-page__selected {
  margin-bottom: 18px;
  padding: 14px 16px;
  background: rgba(247, 251, 248, 0.82);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 14px;
}

.record-page__selected-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.record-page__selected-label {
  margin: 0;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(53, 82, 71, 0.52);
}

.record-page__selected-copy {
  margin: 4px 0 0;
  font-size: 13px;
  color: rgba(53, 82, 71, 0.68);
}

.record-page__selected-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.record-page__selected-chip {
  appearance: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font: inherit;
  background: #fff;
  color: #355247;
  border: 1px solid rgba(126, 197, 160, 0.35);
  border-radius: 999px;
  transition:
    border-color 0.15s ease,
    background 0.15s ease;

  &:hover {
    background: rgba(231, 246, 238, 0.8);
    border-color: rgba(42, 157, 111, 0.45);
  }

  i {
    font-size: 12px;
    color: rgba(53, 82, 71, 0.6);
  }
}

.record-page__selected-chip-name {
  font-size: 13px;
  font-weight: 700;
}

.record-page__selected-chip-unit {
  font-size: 11px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.55);
}

.record-page__fields {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
  align-content: start;
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
  flex-shrink: 0;
  margin-top: auto;
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
