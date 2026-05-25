<template>
  <div class="health-data">
    <div class="health-data__toolbar nb-surface--sm">
      <div class="health-data__toolbar-fields">
        <div class="health-data__toolbar-metric">
          <label class="health-data__label" for="metric-chart-select">Metric</label>
          <el-select
            id="metric-chart-select"
            v-model="userHealthQueryDto.healthModelConfigId"
            class="health-data__select"
            size="small"
            placeholder="Choose metric"
            :disabled="!usersHealthModelConfig.length"
            @change="modelChange"
          >
            <el-option
              v-for="model in usersHealthModelConfig"
              :key="model.id"
              :label="model.name"
              :value="model.id"
            />
          </el-select>
        </div>
        <div class="health-data__toolbar-range">
          <label class="health-data__label" for="chart-time-range">Time range</label>
          <el-select
            id="chart-time-range"
            v-model="userHealthQueryDto.time"
            class="health-data__select health-data__select--range"
            size="small"
            :disabled="!usersHealthModelConfig.length"
            @change="onSelectedTime"
          >
            <el-option
              v-for="range in timeRanges"
              :key="range.value"
              :label="range.label"
              :value="range.value"
            />
          </el-select>
        </div>
      </div>
      <button type="button" class="health-data__cta" @click="toRecord">
        Log a reading
        <i class="el-icon-right" aria-hidden="true" />
      </button>
    </div>

    <section class="health-data__chart-panel nb-surface">
      <div v-if="chartLoading" class="health-data__chart-skeleton skeleton-pulse" />
      <div
        v-else-if="!chartHasData"
        class="health-data__chart-empty"
      >
        <div class="health-data__chart-empty-icon" aria-hidden="true">
          <i class="el-icon-data-line" />
        </div>
        <p class="health-data__chart-empty-title">No trend yet</p>
        <p class="health-data__chart-empty-text">
          Log readings for
          <strong>{{ selectedMetricName }}</strong>
          or try a longer time range.
        </p>
        <button type="button" class="health-data__chart-empty-btn" @click="toRecord">
          Log a reading
        </button>
      </div>
      <LineChart
        v-else
        height="300px"
        :show-toolbar="false"
        :time-range="userHealthQueryDto.time"
        :values="values"
        :date="dates"
      />
    </section>

    <section class="health-data__table-panel nb-surface">
      <div class="health-data__table-toolbar">
        <p v-if="totalItems" class="health-data__table-count">
          {{ totalItems }}
          {{ totalItems === 1 ? "reading" : "readings" }}
        </p>
        <div class="health-data__filters">
          <el-select
            v-model="healthModelConfigId"
            class="health-data__select health-data__select--wide"
            size="small"
            placeholder="All indicators"
            clearable
            @change="fetchFreshData"
          >
            <el-option :key="'all'" label="All indicators" :value="null" />
            <el-option
              v-for="model in usersHealthModelConfig"
              :key="model.id"
              :label="model.name"
              :value="model.id"
            />
          </el-select>
          <el-date-picker
            v-model="searchTime"
            class="health-data__daterange"
            size="small"
            type="daterange"
            range-separator="to"
            start-placeholder="From"
            end-placeholder="To"
            @change="timeChange"
          />
        </div>
      </div>

      <el-table
        v-loading="tableLoading"
        stripe
        class="health-data__table admin-table-full"
        row-key="id"
        :data="tableData"
        empty-text="No readings yet. Log your first value to see it here."
      >
        <el-table-column
          prop="name"
          min-width="200"
          label="Reading"
          class-name="health-data__col-reading"
        >
          <template slot-scope="scope">
            <div class="health-data__reading">
              <span class="health-data__indicator">
                <i class="el-icon-data-line" aria-hidden="true" />
                {{ scope.row.name }}
              </span>
              <span
                v-if="readingCode(scope.row)"
                class="health-data__reading-code"
                >{{ readingCode(scope.row) }}</span
              >
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="value"
          width="120"
          label="Value"
          sortable
          align="right"
          header-align="right"
          class-name="health-data__col-value"
        >
          <template slot-scope="scope">
            <span class="health-data__value">
              <strong>{{ scope.row.value }}</strong>
              <span v-if="displayUnit(scope.row.unit)" class="health-data__unit">{{
                displayUnit(scope.row.unit)
              }}</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column
          width="108"
          label="Status"
          align="center"
          header-align="center"
          class-name="health-data__col-status"
        >
          <template slot-scope="scope">
            <span
              v-if="statusCheck(scope.row)"
              class="health-badge health-badge--ok health-badge--nowrap"
            >
              <i class="el-icon-circle-check" aria-hidden="true" />
              Normal
            </span>
            <el-tooltip
              v-else
              effect="dark"
              content="Outside your usual range — worth a second look."
              placement="top"
            >
              <span class="health-badge health-badge--warn health-badge--nowrap">
                <i class="el-icon-warning-outline" aria-hidden="true" />
                Review
              </span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          width="140"
          label="Logged"
          sortable
          class-name="health-data__col-recorded"
        >
          <template slot-scope="scope">
            <el-tooltip
              :content="formatDateTimeFull(scope.row.createTime)"
              placement="top"
              :disabled="!scope.row.createTime"
            >
              <span class="health-data__recorded">{{
                formatRecordedLine(scope.row.createTime)
              }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          label=""
          width="64"
          align="center"
          header-align="center"
          class-name="health-data__col-actions"
        >
          <template slot-scope="scope">
            <el-tooltip content="Remove" placement="top">
              <button
                type="button"
                class="health-data__row-del"
                aria-label="Remove reading"
                @click="handleDelete(scope.row)"
              >
                <i class="el-icon-delete" aria-hidden="true" />
              </button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="showTablePager" class="health-data__footer">
        <el-pagination
          class="health-data__pager"
          small
          :current-page="currentPage"
          :page-size="pageSize"
          layout="prev, pager, next"
          :total="totalItems"
          @current-change="handleCurrentChange"
        />
        <el-select
          v-model="pageSize"
          class="health-data__page-size"
          size="mini"
          @change="handleSizeChange"
        >
          <el-option :value="10" label="10 / page" />
          <el-option :value="20" label="20 / page" />
          <el-option :value="50" label="50 / page" />
        </el-select>
      </div>
    </section>
  </div>
</template>

<script>
import LineChart from "@/components/LineChart.vue";
import {
  formatDateTimeFull,
  formatRecordedLine,
  HEALTH_TIME_RANGES,
} from "@/utils/data";

export default {
  components: { LineChart },
  data() {
    return {
      usersHealthModelConfig: [],
      userHealthQueryDto: { time: 365 },
      values: [],
      dates: [],
      tableData: [],
      selectedRows: [],
      currentPage: 1,
      pageSize: 20,
      totalItems: 0,
      searchTime: [],
      healthModelConfigId: null,
      chartLoading: false,
      tableLoading: false,
      timeRanges: HEALTH_TIME_RANGES,
    };
  },
  created() {
    this.loadHealthModelConfig();
    this.fetchFreshData();
  },
  computed: {
    showTablePager() {
      return this.totalItems > this.pageSize;
    },
    chartHasData() {
      return Array.isArray(this.values) && this.values.length > 0;
    },
    selectedMetricName() {
      const id = this.userHealthQueryDto.healthModelConfigId;
      const model = this.usersHealthModelConfig.find((m) => m.id === id);
      return model ? model.name : "this metric";
    },
  },
  methods: {
    formatDateTimeFull,
    formatRecordedLine,
    displayUnit(unit) {
      if (unit == null || unit === "") {
        return "";
      }
      const raw = String(unit).trim();
      if (!raw || /^none$/i.test(raw)) {
        return "";
      }
      return raw.length <= 4 ? raw.toLowerCase() : raw;
    },
    readingCode(row) {
      const symbol = (row.symbol || "").trim();
      const name = (row.name || "").trim();
      if (!symbol || /^none$/i.test(symbol) || symbol === name) {
        return "";
      }
      return symbol;
    },
    timeChange() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleDelete(row) {
      this.selectedRows = [row];
      this.batchDelete();
    },
    statusCheck(data) {
      const inputValue = data.value;
      const valueRange = data.valueRange;
      if (valueRange != null && inputValue != null) {
        const aryValueRange = valueRange.split(",");
        const minValue = aryValueRange[0];
        const maxValue = aryValueRange[1];
        return (
          Number(inputValue) > Number(minValue) &&
          Number(inputValue) < Number(maxValue)
        );
      }
      return true;
    },
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message("No data selected");
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete reading",
        text: "Once deleted, it cannot be recovered. Continue?",
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (!confirmed) {
        this.selectedRows = [];
        return;
      }
      try {
        const ids = this.selectedRows.map((entity) => entity.id);
        const response = await this.$axios.post(`/user-health/batchDelete`, ids);
        if (response.data.code === 200) {
          this.$swal.fire({
            title: "Deleted",
            text: response.data.msg,
            icon: "success",
            showConfirmButton: false,
            timer: 2000,
          });
          this.selectedRows = [];
          this.fetchFreshData();
          this.loadUserModelHavaRecord();
        }
      } catch (e) {
        this.$swal.fire({
          title: "Error",
          text: String(e),
          icon: "error",
          showConfirmButton: false,
          timer: 2000,
        });
        console.error("Error deleting health record:", e);
      } finally {
        this.selectedRows = [];
      }
    },
    async fetchFreshData() {
      this.tableLoading = true;
      try {
        let startTime = null;
        let endTime = null;
        if (this.searchTime != null && this.searchTime.length === 2) {
          const [startDate, endDate] = await Promise.all(
            this.searchTime.map((date) => date.toISOString()),
          );
          startTime = `${startDate.split("T")[0]}T00:00:00`;
          endTime = `${endDate.split("T")[0]}T23:59:59`;
        }
        const userInfo = sessionStorage.getItem("userInfo");
        const userEntitySave = JSON.parse(userInfo);
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          startTime: startTime,
          endTime: endTime,
          healthModelConfigId: this.healthModelConfigId,
          userId: userEntitySave.id,
        };
        const response = await this.$axios.post("/user-health/query", params);
        const { data } = response;
        this.tableData = data.data || [];
        this.totalItems = data.total;
      } catch (error) {
        console.error("Error querying user health record information:", error);
      } finally {
        this.tableLoading = false;
      }
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchFreshData();
    },
    loadUserModelHavaRecord() {
      const id = this.userHealthQueryDto.healthModelConfigId;
      if (id == null) {
        this.values = [];
        this.dates = [];
        this.chartLoading = false;
        return;
      }
      this.chartLoading = true;
      this.$axios
        .get(`/user-health/timeQuery/${id}/${this.userHealthQueryDto.time}`)
        .then((response) => {
          const { data } = response;
          if (data.code === 200 && data.data && data.data.length) {
            this.values = data.data.map((entity) => entity.value).reverse();
            this.dates = data.data.map((entity) => entity.createTime).reverse();
          } else {
            this.values = [];
            this.dates = [];
          }
        })
        .catch(() => {
          this.values = [];
          this.dates = [];
        })
        .finally(() => {
          this.chartLoading = false;
        });
    },
    modelChange() {
      this.loadUserModelHavaRecord();
    },
    loadHealthModelConfig() {
      this.$axios.post("/health-model-config/modelList").then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.usersHealthModelConfig = data.data || [];
          this.defaultLoad();
        }
      });
    },
    defaultLoad() {
      if (!this.usersHealthModelConfig.length) {
        this.userHealthQueryDto = { time: 365 };
        this.values = [];
        this.dates = [];
        return;
      }
      this.userHealthQueryDto.healthModelConfigId =
        this.usersHealthModelConfig[0].id;
      this.userHealthQueryDto.time = 365;
      this.loadUserModelHavaRecord();
    },
    onSelectedTime(time) {
      this.userHealthQueryDto.time = time;
      this.loadUserModelHavaRecord();
    },
    toRecord() {
      this.$router.push("/record");
    },
  },
};
</script>

<style scoped lang="scss">
.health-data {
  width: 100%;
  max-width: none;
  margin: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.health-data__toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px 16px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 14px;
}

.health-data__toolbar-fields {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 12px 20px;
  min-width: 0;
}

.health-data__toolbar-metric,
.health-data__toolbar-range {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px 12px;
  min-width: 0;
}

.health-data__select--range {
  min-width: 168px;
}

.health-data__label {
  font-size: 13px;
  font-weight: 650;
  color: #355247;
}

.health-data__select {
  min-width: 200px;

  :deep(.el-input__inner) {
    border-radius: 10px;
    border-color: rgba(126, 197, 160, 0.45);
  }

  &--wide {
    min-width: 200px;
  }
}

.health-data__cta {
  appearance: none;
  cursor: pointer;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  font-family: var(--nb-font, system-ui, sans-serif);
  font-size: 14px;
  font-weight: 650;
  color: #fff;
  background: #2a9d6f;
  border: none;
  border-radius: 999px;
  box-shadow: 0 2px 10px rgba(42, 157, 111, 0.28);
  transition:
    transform 0.12s ease,
    background 0.15s ease;

  i {
    font-size: 14px;
  }

  &:hover {
    transform: translateY(-1px);
    background: #248f63;
  }

  &:active {
    transform: translateY(0);
  }
}

.health-data__chart-panel {
  padding: 12px 16px 8px;
  overflow: hidden;
  min-height: 280px;

  :deep(.line-main) {
    border: none;
    box-shadow: none;
    background: transparent;
    padding: 0;
  }

  :deep(.line-main__chart) {
    min-height: 280px;
  }
}

.health-data__chart-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 280px;
  padding: 32px 24px;
  text-align: center;
}

.health-data__chart-empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(42, 157, 111, 0.12);
  color: #2a9d6f;
  font-size: 26px;
  margin-bottom: 4px;
}

.health-data__chart-empty-title {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--nb-ink, #24332b);
}

.health-data__chart-empty-text {
  margin: 0;
  max-width: 320px;
  font-size: 14px;
  line-height: 1.5;
  color: rgba(36, 51, 43, 0.58);

  strong {
    color: #355247;
    font-weight: 650;
  }
}

.health-data__chart-empty-btn {
  appearance: none;
  cursor: pointer;
  margin-top: 8px;
  padding: 9px 18px;
  font-size: 14px;
  font-weight: 650;
  color: #2a9d6f;
  background: rgba(42, 157, 111, 0.1);
  border: 1px solid rgba(42, 157, 111, 0.35);
  border-radius: 999px;
  transition:
    background 0.15s ease,
    transform 0.12s ease;

  &:hover {
    background: rgba(42, 157, 111, 0.18);
    transform: translateY(-1px);
  }
}

.health-data__chart-skeleton {
  height: 280px;
  border-radius: 12px;
  background: rgba(126, 197, 160, 0.14);
}

.skeleton-pulse {
  animation: health-data-skeleton 1.2s ease-in-out infinite;
}

@keyframes health-data-skeleton {
  0%,
  100% {
    opacity: 0.55;
  }
  50% {
    opacity: 1;
  }
}

.health-data__table-panel {
  padding: 18px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 16px;
  box-shadow: 0 8px 22px rgba(53, 92, 75, 0.08);
}

.health-data__table-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px 16px;
  margin-bottom: 14px;
}

.health-data__table-count {
  margin: 0;
  font-size: 13px;
  font-weight: 650;
  color: rgba(36, 51, 43, 0.55);
}

.health-data__filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  margin-left: auto;
}

.health-data__daterange {
  min-width: 240px;

  :deep(.el-range-editor.el-input__inner) {
    border-radius: 10px;
    border-color: rgba(126, 197, 160, 0.45);
  }
}

.health-data__table {
  width: 100% !important;

  :deep(th) {
    background: rgba(231, 246, 238, 0.75) !important;
    color: #355247 !important;
    font-weight: 700 !important;
    border-bottom: 1px solid rgba(126, 197, 160, 0.25) !important;
  }

  :deep(th > .cell) {
    white-space: nowrap !important;
    padding-top: 10px !important;
    padding-bottom: 10px !important;
    line-height: 1.2 !important;
  }

  :deep(td > .cell) {
    padding-top: 8px !important;
    padding-bottom: 8px !important;
  }

  :deep(td) {
    border-bottom: 1px solid rgba(126, 197, 160, 0.12) !important;
  }

  :deep(.el-table__body tr:hover > td) {
    background: rgba(231, 246, 238, 0.35) !important;
  }

  :deep(thead th.health-data__col-actions > .cell),
  :deep(td.health-data__col-actions > .cell) {
    padding-left: 8px !important;
    padding-right: 8px !important;
    text-align: center !important;
  }

  :deep(thead th.health-data__col-value > .cell),
  :deep(td.health-data__col-value > .cell) {
    text-align: right !important;
  }

  :deep(thead th.health-data__col-status > .cell),
  :deep(td.health-data__col-status > .cell) {
    text-align: center !important;
  }
}

.health-data__reading {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.health-data__reading-code {
  font-size: 11px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.5);
  padding-left: 23px;
}

.health-data__indicator {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--nb-ink, #24332b);

  i {
    color: #4d8b73;
    font-size: 15px;
  }
}

.health-data__value {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;

  strong {
    font-variant-numeric: tabular-nums;
    color: var(--nb-ink, #24332b);
  }
}

.health-data__unit {
  font-size: 12px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.55);
}

.health-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
  border: 1px solid transparent;

  &--nowrap {
    white-space: nowrap;
  }

  i {
    font-size: 13px;
  }

  &--ok {
    color: #1f6b4f;
    background: rgba(42, 157, 111, 0.12);
    border-color: rgba(42, 157, 111, 0.28);
  }

  &--warn {
    color: #8a5a1a;
    background: rgba(229, 155, 46, 0.14);
    border-color: rgba(201, 124, 58, 0.35);
  }
}

.health-data__recorded {
  font-size: 13px;
  font-weight: 600;
  color: rgba(53, 82, 71, 0.78);
  white-space: nowrap;
}

.health-data__col-recorded > .cell {
  font-size: 13px;
  color: rgba(53, 82, 71, 0.72);
  white-space: nowrap !important;
}

.health-data__footer {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 10px 16px;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid rgba(126, 197, 160, 0.15);
}

.health-data__page-size {
  width: 96px;

  :deep(.el-input__inner) {
    border-radius: 8px;
    font-size: 12px;
  }
}

.health-data__row-del {
  appearance: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  padding: 0;
  border: 1px solid rgba(201, 90, 90, 0.35);
  border-radius: 9px;
  background: rgba(255, 245, 245, 0.95);
  color: #9b3d3d;
  cursor: pointer;
  transition:
    background 0.15s ease,
    border-color 0.15s ease;

  i {
    font-size: 15px;
  }

  &:hover {
    background: rgba(201, 90, 90, 0.12);
    border-color: rgba(167, 83, 90, 0.55);
  }
}

.health-data__pager {
  margin: 0;
  padding: 0;

  :deep(.el-pager li.active) {
    background-color: #2a9d6f !important;
  }
}
</style>
