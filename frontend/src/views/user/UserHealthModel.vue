<template>
  <div>
    <div
      style="
        border-radius: 5px;
        padding: 20px 0 60px 0;
        width: 100%;
        background-color: #fafafa;
      "
    >
      <div
        style="
          height: 100px;
          line-height: 100px;
          text-align: center;
          font-size: 24px;
        "
      >
        HealthPals, Healthy Life, Start Now!
      </div>
      <div
        style="
          height: 50px;
          line-height: 50px;
          text-align: center;
          font-size: 30px;
          font-weight: bolder;
        "
      >
        Every little change is worth recording.
        <span
          @click="toRecord"
          style="
            cursor: pointer;
            padding: 5px 10px;
            background-color: #000;
            border-radius: 5px;
            color: #fff;
          "
        >
          Go to Record
          <i class="el-icon-right"></i>
        </span>
      </div>
    </div>
    <div style="padding: 30px 0">
      <div style="margin: 20px 0">
        <!-- Select specific indicator model -->
        <el-select
          size="small"
          @change="modelChange"
          v-model="userHealthQueryDto.healthModelConfigId"
          placeholder="Please select"
        >
          <el-option
            v-for="model in usersHealthModelConfig"
            :key="model.id"
            :label="model.name"
            :value="model.id"
          >
          </el-option>
        </el-select>
      </div>
      <div>
        <LineChart
          @on-selected="onSelectedTime"
          height="500px"
          tag=""
          :values="values"
          :date="dates"
        />
      </div>
    </div>
    <div>
      <h2 style="padding-left: 20px; border-left: 2px solid rgb(43, 121, 203)">
        Health Indicator Data
      </h2>
      <el-row style="padding: 10px; margin-left: 10px">
        <el-row
          style="
            display: flex;
            justify-content: left;
            align-items: center;
            gap: 10px;
          "
        >
          <el-select
            size="small"
            @change="fetchFreshData"
            v-model="healthModelConfigId"
            placeholder="Select Model"
          >
            <el-option :key="null" label="All" :value="null"> </el-option>
            <el-option
              v-for="model in usersHealthModelConfig"
              :key="model.id"
              :label="model.name"
              :value="model.id"
            >
            </el-option>
          </el-select>
          <el-date-picker
            size="small"
            @change="timeChange"
            style="width: 220px"
            v-model="searchTime"
            type="daterange"
            range-separator="to"
            start-placeholder="Start Date"
            end-placeholder="End Date"
          >
          </el-date-picker>
        </el-row>
      </el-row>
      <el-row style="margin: 0 20px; border-top: 1px solid rgb(245, 245, 245)">
        <el-table
          row-key="id"
          @selection-change="handleSelectionChange"
          :data="tableData"
        >
          <el-table-column prop="name" label="Indicator">
            <template slot-scope="scope">
              <span
                ><i class="el-icon-paperclip" style="margin-right: 3px"></i
                >{{ scope.row.name }}</span
              >
            </template>
          </el-table-column>
          <el-table-column prop="value" width="148" label="Value" sortable>
            <template slot-scope="scope">
              <span style="font-weight: 800"
                >{{ scope.row.value }}&nbsp;{{ scope.row.unit }}</span
              >
            </template>
          </el-table-column>
          <el-table-column
            prop="symbol"
            width="108"
            label="Model Symbol"
          ></el-table-column>
          <el-table-column prop="name" width="88" label="Status">
            <template slot-scope="scope">
              <i
                v-if="!statusCheck(scope.row)"
                style="margin-right: 5px"
                class="el-icon-warning"
              ></i>
              <i
                v-else
                style="margin-right: 5px; color: rgb(253, 199, 50)"
                class="el-icon-success"
              ></i>
              <el-tooltip
                v-if="!statusCheck(scope.row)"
                class="item"
                effect="dark"
                content="Abnormal indicator, remind user to take action"
                placement="bottom-end"
              >
                <span
                  style="
                    text-decoration: underline;
                    text-decoration-style: dashed;
                  "
                  >Abnormal</span
                >
              </el-tooltip>
              <span v-else>Normal</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="createTime"
            width="178"
            label="Record Time"
            sortable
          ></el-table-column>
          <el-table-column label="Actions" width="80">
            <template slot-scope="scope">
              <span class="text-button" @click="handleDelete(scope.row)"
                >Delete</span
              >
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          style="margin: 20px 0"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalItems"
        ></el-pagination>
      </el-row>
    </div>
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
      userHealthQueryDto: {}, // Query parameters
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
      this.current = 1;
      this.fetchFreshData();
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
    // Determine if the user input value is normal or abnormal, and assign a status
    statusCheck(data) {
      // User input value
      const inputValue = data.value;
      // Normal value range
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
    // Batch delete data
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete Health Record Data",
        text: `Once deleted, it cannot be recovered. Do you want to continue?`,
        icon: "warning",
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(
            `/user-health/batchDelete`,
            ids,
          );
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
    // Executes function after clicking search
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    // Load user's own health record data
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
        // Request parameters
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
    // Clear button inside input field
    handleFilterClear() {
      this.filterText = "";
      this.handleFilter();
    },
    // Checkbox selection
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    // Reset conditions
    resetQueryCondition() {
      this.searchTime = [];
      this.healthModelConfigId = null;
      this.fetchFreshData();
    },
    // Change items per page
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchFreshData();
    },
    // Change current page
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchFreshData();
    },
    // Query user's specific recorded values within a specified time range
    loadUserModelHavaRecord() {
      this.$axios
        .get(
          `/user-health/timeQuery/${this.userHealthQueryDto.healthModelConfigId}/${this.userHealthQueryDto.time}`,
        )
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            // Process retrieved data for visualization
            this.values = data.data.map((entity) => entity.value).reverse();
            this.dates = data.data.map((entity) => entity.createTime).reverse();
          }
        });
    },
    // Model selection method
    modelChange(day) {
      this.loadUserModelHavaRecord();
    },
    // Specific model selection in the table
    modelUserChange() {
      // If users select directly, data returns immediately
      this.fetchFreshData();
    },
    // Query user-configured models and global models
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
    // Default load
    defaultLoad() {
      this.userHealthQueryDto.healthModelConfigId =
        this.usersHealthModelConfig[0].id;
      this.userHealthQueryDto.time = 365;
      this.loadUserModelHavaRecord();
    },
    // Callback after selecting a specific event range in the line chart
    onSelectedTime(time) {
      this.userHealthQueryDto.time = time;
      this.loadUserModelHavaRecord();
    },
    // Data returned from the component
    timeSelected() {},
    toRecord() {
      this.$router.push("/record");
    },
  },
};
</script>
<style scoped lang="scss">
.status-success {
  display: inline-block;
  padding: 1px 5px;
  border-radius: 2px;
  background-color: rgb(201, 237, 249);
  color: rgb(111, 106, 196);
  font-size: 12px;
}

.status-error {
  display: inline-block;
  padding: 1px 5px;
  border-radius: 2px;
  background-color: rgb(233, 226, 134);
  color: rgb(131, 138, 142);
  color: rgb(111, 106, 196);
  font-size: 12px;
}
</style>
