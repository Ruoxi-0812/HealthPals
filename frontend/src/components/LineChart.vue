<template>
  <div class="line-main">
    <div class="line-main__toolbar">
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
            v-for="item in options"
            :key="item.num"
            :label="item.name"
            :value="item.num"
          />
        </el-select>
      </span>
    </div>
    <div ref="chart" class="line-main__chart" :style="{ height: height }"></div>
  </div>
</template>
<script>
import * as echarts from "echarts";
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
  },
  data() {
    return {
      chart: null,
      options: [
        { num: 7, name: "Last 7 days" },
        { num: 30, name: "Last 30 days" },
        { num: 60, name: "Last 60 days" },
        { num: 90, name: "Last 90 days" },
        { num: 180, name: "Last 6 months" },
        { num: 365, name: "Last 12 months" },
      ],
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
  },
  mounted() {
    this.selectedValue = 365;
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
        option = {
          grid: {
            left: 48,
            right: 24,
            top: 28,
            bottom: 32,
            borderWidth: 0,
          },
          tooltip: {
            trigger: "axis",
            formatter: "{b} → {c}",
          },
          xAxis: {
            type: "category",
            data: this.date,
            axisLine: { lineStyle: { color: "rgba(126, 197, 160, 0.45)" } },
            axisTick: { show: false },
            axisLabel: {
              color: axisColor,
              fontSize: 11,
              rotate: this.date.length > 8 ? 28 : 0,
            },
          },
          yAxis: {
            type: "value",
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
              smooth: true,
              symbolSize: 6,
              data: this.values,
              areaStyle: {
                color: "rgba(42, 157, 111, 0.14)",
              },
              lineStyle: {
                width: 2,
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
  font-size: 13px;
  font-weight: 600;
  color: var(--nb-muted, #5c6560);
}

.line-main__timing {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.line-main__timing-label {
  font-size: 13px;
  font-weight: 600;
  color: #355247;
}

.line-main__select {
  min-width: 168px;

  :deep(.el-input__inner) {
    border-radius: 999px;
    border-color: rgba(126, 197, 160, 0.45);
    font-weight: 500;
  }
}

.line-main__chart {
  width: 100%;
  min-height: 220px;
}
</style>
