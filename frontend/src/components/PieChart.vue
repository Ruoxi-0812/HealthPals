<template>
  <div class="line-main" :style="{ backgroundColor: bag }">
    <div ref="chart" :style="{ width: width, height: height }"></div>
    <!-- <div>
      <span class="tag" :style="{ color: fontColor }">
        <i class="el-icon-pie-chart"></i>
        {{ tag }}</span>
    </div> -->
  </div>
</template>
<script>
import * as echarts from "echarts";
export default {
  name: "PieChart",
  props: {
    types: {
      type: Array,
      default: [],
    },
    values: {
      type: Array,
      default: [],
    },
    width: {
      type: String,
      default: "100%",
    },
    // tag: {
    //   type: String,
    //   default: 'PieChart'
    // },
    height: {
      type: String,
      default: "243px",
    },
    bag: {
      type: String,
      default: "#fff",
    },
    fontColor: {
      type: String,
      default: "#333",
    },
  },
  data() {
    return {
      chart: null,
    };
  },
  watch: {
    types() {
      this.$nextTick(() => this.initChart());
    },
    values: {
      handler() {
        this.$nextTick(() => this.initChart());
      },
      deep: true,
    },
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      if (!this.$refs.chart) return;
      if (this.chart) this.chart.dispose();
      this.chart = echarts.init(this.$refs.chart);

      const palette = [
        "#2a9d6f",
        "#7ec9a8",
        "#e59b2e",
        "#5b9fd4",
        "#c97c3a",
        "#9b8ed4",
        "#4d8b73",
      ];
      const hasData =
        Array.isArray(this.values) &&
        this.values.length > 0 &&
        this.values.some((v) => Number(v) > 0);

      const option = hasData
        ? {
            tooltip: {
              trigger: "item",
              formatter: "{b}: {c} ({d}%)",
            },
            series: [
              {
                type: "pie",
                radius: ["42%", "68%"],
                center: ["50%", "52%"],
                avoidLabelOverlap: true,
                itemStyle: {
                  borderRadius: 6,
                  borderColor: "#fff",
                  borderWidth: 2,
                },
                labelLine: {
                  length: 10,
                  length2: 8,
                  lineStyle: { color: "rgba(53, 82, 71, 0.35)" },
                },
                label: {
                  show: true,
                  position: "outside",
                  color: "#355247",
                  fontSize: 11,
                  fontWeight: 600,
                  formatter: "{b}\n{d}%",
                },
                emphasis: {
                  scale: true,
                  scaleSize: 6,
                  itemStyle: {
                    shadowBlur: 12,
                    shadowColor: "rgba(42, 157, 111, 0.25)",
                  },
                },
                data: this.values.map((value, index) => ({
                  name: this.types[index],
                  value: value,
                })),
                color: palette,
              },
            ],
          }
        : {
            graphic: [
              {
                type: "text",
                left: "center",
                top: "middle",
                style: {
                  text: "No distribution data yet",
                  fill: "rgba(53, 82, 71, 0.45)",
                  fontSize: 14,
                  fontWeight: 500,
                },
              },
            ],
          };

      this.chart.setOption(option, true);
    },
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose();
    }
  },
};
</script>

<style scoped lang="scss">
.line-main {
  width: 100%;
  box-sizing: border-box;
}
</style>
