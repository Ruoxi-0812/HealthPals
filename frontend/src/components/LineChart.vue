<template>
  <div class="line-main">
    <div>
      <span class="tag">{{ tag }}</span>
      <span class="time-show">
        <span class="top-bar" style="font-size: 12px">Timing</span>
        <el-select
          size="mini"
          style="width: 90px"
          v-model="selectedValue"
          placeholder="deadlines"
        >
          <el-option
            v-for="item in options"
            :key="item.num"
            :label="item.name"
            :value="item.num"
          >
          </el-option>
        </el-select>
      </span>
    </div>
    <div ref="chart" :style="{ width: '100%', height: height }"></div>
  </div>
</template>
<script>
import * as echarts from "echarts";
export default {
  name: "DialogLine",
  props: {
    tag: {
      type: String,
      default: "LineChart",
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
  watch: {
    selectedValue: {
      handler(newVal, oldVal) {
        this.$emit("on-selected", newVal);
      },
      deep: true,
      immediate: false,
    },
    values: {
      handler(newVal, oldVal) {
        this.initChart();
      },
      deep: true,
      immediate: false,
    },
  },
  data() {
    return {
      chart: null,
      options: [
        { num: 7, name: "Within 7 days" },
        { num: 30, name: "Within 30 days" },
        { num: 60, name: "Within 60 days" },
      ],
      selectedValue: "",
    };
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.chart);
      let option = {
        grid: {
          left: 30,
          right: 10,
          top: 50,
          borderWidth: 0,
        },
        title: {
          text: "",
          color: "#ffffff",
        },
        tooltip: {
          trigger: "axis",
          formatter: "{b}->{c}",
        },
        legend: {
          data: [""],
        },
        xAxis: {
          data: this.date,
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: {
            color: "rgb(51，51，51)",
            fontSize: "14",
          },
        },
        yAxis: {
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: {
            color: "rgb(51，51，51)",
            fontSize: "14",
          },
        },
        series: [
          {
            name: "",
            type: "line",
            smooth: true,
            data: this.values,
            areaStyle: {
              color: "rgba(144, 191, 237, 0.3)",
            },
            lineStyle: {
              color: "#5B8FF9",
            },
            itemStyle: {
              color: "#5B8FF9",
              borderColor: "#5B8FF9",
              borderWidth: 2,
            },
            label: {
              show: true,
              position: "top",
              color: "rgb(102, 102, 102)",
            },
          },
        ],
      };
      this.chart.setOption(option);
    },
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
  },
};
</script>
<style scoped lang="scss">
.line-main {
  margin-bottom: 5px;
  border-radius: 3px;

  .tag {
    font-size: 14px;
    padding: 15px 16px;
    display: inline-block;
    color: #6f6d6d;
    font-weight: bold;
  }

  .time-show {
    padding: 15px 6px;
    float: right;
  }
}
</style>
