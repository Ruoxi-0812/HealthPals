<template>
  <div class="line-main">
    <div v-if="showToolbar" class="line-main__toolbar">
      <span v-if="tag" class="line-main__tag">{{ tag }}</span>
      <span class="line-main__timing">
        <span class="line-main__timing-label">Time range</span>
        <el-select
          v-model="selectedValue"
          class="line-main__select"
          size="small"
          placeholder="Choose range"
          @change="emitSelection"
        >
          <el-option
            v-for="item in rangeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </span>
    </div>
    <div ref="chart" class="line-main__chart" :style="{ height: height }"></div>
  </div>
</template>
<script>
import * as echarts from "echarts";
import {
  HEALTH_TIME_RANGES,
  formatChartAxisLabel,
  formatDateTimeFull,
} from "@/utils/data";

export default {
  name: "DialogLine",
  props: {
    tag: {
      type: String,
      default: "",
    },
    values: {
      type: Array,
      required: true,
    },
    date: {
      type: Array,
      required: true,
    },
    height: {
      type: String,
      default: "220px",
    },
    showToolbar: {
      type: Boolean,
      default: true,
    },
    timeRange: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      chart: null,
      rangeOptions: HEALTH_TIME_RANGES,
      selectedValue: 365,
    };
  },
  watch: {
    values: {
      handler() {
        this.$nextTick(() => this.initChart());
      },
      deep: true,
    },
    date: {
      handler() {
        this.$nextTick(() => this.initChart());
      },
      deep: true,
    },
    timeRange(val) {
      if (val != null && val !== this.selectedValue) {
        this.selectedValue = val;
      }
    },
  },
  mounted() {
    this.selectedValue = this.timeRange != null ? this.timeRange : 365;
    this.$nextTick(() => this.initChart());
    window.addEventListener("resize", this.handleResize);
  },
  methods: {
    emitSelection(val) {
      this.$emit("on-selected", val);
    },
    handleResize() {
      if (this.chart) {
        this.chart.resize();
      }
    },
    initChart() {
      if (!this.$refs.chart) {
        return;
      }
      if (this.chart) {
        this.chart.dispose();
      }
      this.chart = echarts.init(this.$refs.chart);
      const axisColor = "#5c7268";
      const hasData =
        Array.isArray(this.values) &&
        this.values.length > 0 &&
        Array.isArray(this.date) &&
        this.date.length > 0;
      let option;
      if (!hasData) {
        option = {
          graphic: [
            {
              type: "text",
              left: "center",
              top: "middle",
              style: {
                text: "No readings in this range yet.\nLog a value or pick a longer time window.",
                fill: "rgba(36, 51, 43, 0.45)",
                fontSize: 14,
                fontWeight: 500,
                textAlign: "center",
              },
            },
          ],
          xAxis: { show: false },
          yAxis: { show: false },
          series: [],
        };
      } else {
        const pointCount = this.values.length;
        const axisLabels = this.date.map((d) =>
          formatChartAxisLabel(d, this.date),
        );
        const numericValues = this.values.map((v) => Number(v));
        const minVal = Math.min(...numericValues.filter((n) => !isNaN(n)));
        const maxVal = Math.max(...numericValues.filter((n) => !isNaN(n)));
        const yPadding =
          minVal === maxVal
            ? Math.max(Math.abs(minVal) * 0.15, 1)
            : (maxVal - minVal) * 0.12;

        option = {
          grid: {
            left: 48,
            right: 24,
            top: pointCount === 1 ? 36 : 28,
            bottom: pointCount > 6 ? 48 : 36,
            borderWidth: 0,
          },
          tooltip: {
            trigger: "axis",
            formatter: (params) => {
              const p = params[0];
              if (!p) {
                return "";
              }
              const full = formatDateTimeFull(this.date[p.dataIndex]);
              return `${full || p.name}<br/><strong>${p.value}</strong>`;
            },
          },
          xAxis: {
            type: "category",
            data: axisLabels,
            boundaryGap: pointCount <= 2,
            axisLine: { lineStyle: { color: "rgba(126, 197, 160, 0.45)" } },
            axisTick: { show: false },
            axisLabel: {
              color: axisColor,
              fontSize: 11,
              rotate: pointCount > 8 ? 28 : 0,
              hideOverlap: true,
            },
          },
          yAxis: {
            type: "value",
            scale: pointCount > 1 && minVal !== maxVal,
            min:
              pointCount === 1 && !isNaN(minVal)
                ? Math.max(0, minVal - yPadding)
                : undefined,
            max:
              pointCount === 1 && !isNaN(maxVal)
                ? maxVal + yPadding
                : undefined,
            axisLine: { show: false },
            axisTick: { show: false },
            splitLine: {
              lineStyle: { color: "rgba(126, 197, 160, 0.2)" },
            },
            axisLabel: {
              color: axisColor,
              fontSize: 12,
            },
          },
          series: [
            {
              type: "line",
              smooth: pointCount > 2,
              showSymbol: true,
              symbolSize: pointCount === 1 ? 12 : pointCount <= 4 ? 8 : 6,
              data: this.values,
              areaStyle:
                pointCount > 1
                  ? { color: "rgba(42, 157, 111, 0.14)" }
                  : undefined,
              lineStyle: {
                width: pointCount === 1 ? 0 : 2,
                color: "#2a9d6f",
              },
              itemStyle: {
                color: "#2a9d6f",
                borderColor: "#fff",
                borderWidth: 2,
              },
            },
          ],
        };
      }
      this.chart.setOption(option, true);
    },
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.handleResize);
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
  },
};
</script>
<style scoped lang="scss">
.line-main {
  width: 100%;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 18px;
  box-shadow: var(--nb-shadow-sm, 0 8px 20px rgba(53, 92, 75, 0.08));
  padding: 6px 4px 12px;
  box-sizing: border-box;
}

.line-main__toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 16px 4px;
}

.line-main__tag {
  font-size: 1rem;
  font-weight: 600;
  font-family: var(--nb-font-display, Georgia, serif);
  color: var(--nb-ink, #24332b);
}

.line-main__timing {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.line-main__timing-label {
  font-size: 13px;
  font-weight: 600;
  color: #355247;
}

.line-main__select {
  min-width: 168px;

  :deep(.el-input__inner) {
    height: 32px;
    border-radius: 10px;
    background: #f3faf6 !important;
    border: 1px solid rgba(126, 197, 160, 0.38) !important;
    font-weight: 500;
    color: var(--nb-ink, #24332b);

    &:focus {
      border-color: rgba(42, 157, 111, 0.55) !important;
      box-shadow: 0 0 0 3px rgba(42, 157, 111, 0.15);
    }
  }
}

.line-main__chart {
  width: 100%;
  min-height: 220px;
}
</style>
