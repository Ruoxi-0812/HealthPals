<template>
  <div class="health-data">
    <section class="health-data__hero nb-surface">
      <div class="health-data__hero-text">
        <p class="health-data__eyebrow">
          Track habits, see patterns, feel a little smarter every week.
        </p>
        <h1 class="health-data__title">Your trends, one gentle chart.</h1>
        <p class="health-data__lede">
          Pick a metric, scroll your history, then log something new when you’re
          ready—no pressure, just progress.
        </p>
      </div>
      <button type="button" class="health-data__cta" @click="toRecord">
        Log a reading
        <i class="el-icon-right" aria-hidden="true" />
      </button>
    </section>

    <section class="health-data__chart-section">
      <div class="health-data__chart-head">
        <label class="health-data__label" for="metric-chart-select">Metric</label>
        <el-select
          id="metric-chart-select"
          v-model="userHealthQueryDto.healthModelConfigId"
          class="health-data__select"
          size="small"
          placeholder="Choose what to plot"
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
      <LineChart
        height="360px"
        tag=""
        :values="values"
        :date="dates"
        @on-selected="onSelectedTime"
      />
    </section>

    <section class="health-data__table-section nb-surface">
      <h2 class="health-data__section-title">All logged readings</h2>
      <p class="health-data__section-hint">
        Filter by type or date range, then tidy up rows you don’t need.
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
          start-placeholder="Start"
          end-placeholder="End"
          @change="timeChange"
        />
      </div>

      <el-table
        class="health-data__table"
        row-key="id"
        :data="tableData"
        empty-text="No readings yet"
        @selection-change="handleSelectionChange"
      >
        <el-table-column prop="name" label="Indicator">
          <template slot-scope="scope">
            <span
              ><i class="el-icon-paperclip health-data__clip" aria-hidden="true" />
              {{ scope.row.name }}</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="value" width="148" label="Value" sortable>
          <template slot-scope="scope">
            <span class="health-data__value"
              >{{ scope.row.value }}&nbsp;{{ scope.row.unit }}</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="symbol" width="108" label="Code" />
        <el-table-column prop="name" width="100" label="Status">
          <template slot-scope="scope">
            <i
              v-if="!statusCheck(scope.row)"
              class="el-icon-warning health-data__status-ic"
              aria-hidden="true"
            />
            <i
              v-else
              class="el-icon-success health-data__status-ic health-data__status-ic--ok"
              aria-hidden="true"
            />
            <el-tooltip
              v-if="!statusCheck(scope.row)"
              effect="dark"
              content="Outside your usual range — worth a second look."
              placement="bottom-end"
            >
              <span class="health-data__status-link">Review</span>
            </el-tooltip>
            <span v-else class="health-data__status-ok">On track</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          width="178"
          label="Logged at"
          sortable
        />
        <el-table-column label="Actions" width="88">
          <template slot-scope="scope">
            <button
              type="button"
              class="health-data__row-del"
              @click="handleDelete(scope.row)"
            >
              Remove
            </button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="health-data__pager"
        :current-page="currentPage"
        :page-sizes="[10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </section>
  </div>
</template>

<script>
import LineChart from "@/components/LineChart.vue";

export default {
  components: { LineChart },
  data() {
    return {
      usersHealthModelConfig: [],
      modelConfigList: [],
      userHealthQueryDto: {},
      values: [],
      dates: [],
      tableData: [],
      selectedRows: [],
      currentPage: 1,
      pageSize: 20,
      totalItems: 0,
      searchTime: [],
      healthModelConfigId: null,
    };
  },
  created() {
    this.loadHealthModelConfig();
    this.fetchFreshData();
  },
  methods: {
    timeChange() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
    statusCheck(data) {
      const inputValue = data.value;
      const valueRange = data.valueRange;
      if (valueRange !== null && inputValue !== null) {
        const aryValueRange = valueRange.split(",");
        const minValue = aryValueRange[0];
        const maxValue = aryValueRange[1];
        return (
          Number(inputValue) > Number(minValue) &&
          Number(inputValue) < Number(maxValue)
        );
      }
    },
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete Health Record Data",
        text: `Once deleted, it cannot be recovered. Do you want to continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(`/user-health/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: "Delete Notification",
              text: response.data.msg,
              icon: "success",
              showConfirmButton: false,
              timer: 2000,
            });
            this.fetchFreshData();
            return;
          }
        } catch (e) {
          this.$swal.fire({
            title: "Error Notification",
            text: e,
            icon: "error",
            showConfirmButton: false,
            timer: 2000,
          });
          console.error(`Error deleting user health record information:`, e);
        }
      }
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    async fetchFreshData() {
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
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Error querying user health record information:", error);
      }
    },
    handleFilterClear() {
      this.filterText = "";
      this.handleFilter();
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    resetQueryCondition() {
      this.searchTime = [];
      this.healthModelConfigId = null;
      this.fetchFreshData();
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
        return;
      }
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
        });
    },
    modelChange() {
      this.loadUserModelHavaRecord();
    },
    modelUserChange() {
      this.fetchFreshData();
    },
    loadHealthModelConfig() {
      this.$axios.post("/health-model-config/modelList").then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.usersHealthModelConfig = data.data;
          this.modelConfigList = data.data;
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
    timeSelected() {},
    toRecord() {
      this.$router.push("/record");
    },
  },
};
</script>

<style scoped lang="scss">
.health-data {
  width: 100%;
  max-width: min(1080px, 94vw);
  margin: 0 auto;
  padding: clamp(12px, 2vw, 24px) clamp(10px, 1.5vw, 16px) 40px;
  box-sizing: border-box;
}

.health-data__hero {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: clamp(20px, 3vw, 28px);
  margin-bottom: 22px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
}

.health-data__eyebrow {
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: #5a7a6d;
  text-transform: uppercase;
}

.health-data__title {
  margin: 0 0 10px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: clamp(1.35rem, 2.4vw, 1.75rem);
  font-weight: 600;
  letter-spacing: -0.02em;
  line-height: 1.25;
  color: var(--nb-ink, #24332b);
}

.health-data__lede {
  margin: 0;
  max-width: 46ch;
  font-size: 15px;
  line-height: 1.6;
  color: var(--nb-muted, rgba(36, 51, 43, 0.62));
}

.health-data__cta {
  appearance: none;
  cursor: pointer;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 22px;
  font-family: var(--nb-font, system-ui, sans-serif);
  font-size: 14px;
  font-weight: 650;
  color: #fff;
  background: #2a3f36;
  border: none;
  border-radius: 999px;
  box-shadow: 0 4px 14px rgba(42, 63, 54, 0.25);
  transition:
    transform 0.12s ease,
    filter 0.12s ease;

  i {
    font-size: 14px;
  }

  &:hover {
    transform: translateY(-1px);
    filter: brightness(1.05);
  }

  &:active {
    transform: translateY(0);
  }
}

.health-data__chart-section {
  margin-bottom: 24px;
}

.health-data__chart-head {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
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
    min-width: 220px;
  }
}

.health-data__table-section {
  padding: clamp(18px, 2.5vw, 26px);
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
}

.health-data__section-title {
  margin: 0 0 6px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--nb-ink, #24332b);
}

.health-data__section-hint {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--nb-muted, rgba(36, 51, 43, 0.55));
}

.health-data__filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.health-data__daterange {
  min-width: 260px;

  :deep(.el-range-editor.el-input__inner) {
    border-radius: 10px;
    border-color: rgba(126, 197, 160, 0.45);
  }
}

.health-data__table {
  :deep(th) {
    font-weight: 650;
    color: #355247;
  }
}

.health-data__clip {
  margin-right: 6px;
  color: #2a9d6f;
}

.health-data__value {
  font-weight: 700;
  color: var(--nb-ink, #24332b);
}

.health-data__status-ic {
  margin-right: 6px;
  color: #c97c3a;
}

.health-data__status-ic--ok {
  color: #2a9d6f;
}

.health-data__status-link {
  text-decoration: underline;
  text-decoration-style: dashed;
  cursor: help;
  color: #8a4b16;
  font-size: 13px;
}

.health-data__status-ok {
  font-size: 13px;
  color: #4a8f72;
  font-weight: 600;
}

.health-data__row-del {
  appearance: none;
  border: none;
  background: none;
  cursor: pointer;
  padding: 0;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  color: #a85a5a;

  &:hover {
    text-decoration: underline;
  }
}

.health-data__pager {
  margin-top: 18px;
  text-align: right;
}
</style>
