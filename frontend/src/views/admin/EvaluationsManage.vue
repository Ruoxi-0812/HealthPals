<template>
  <div class="admin-page">
    <div class="admin-page__toolbar">
      <div class="admin-toolbar-row">
        <el-date-picker
          size="small"
          style="width: 220px"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Comments begin"
          end-placeholder="Comments end"
        >
        </el-date-picker>
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="evalustionsQueryDto.content"
          placeholder="Comments content"
          clearable
          @clear="handleFilterClear"
        >
          <el-button
            slot="append"
            @click="handleFilter"
            icon="el-icon-search"
          ></el-button>
        </el-input>
      </div>
    </div>

    <div class="admin-page__body">
      <el-table
        row-key="id"
        @selection-change="handleSelectionChange"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="content" label="text">
          <template slot-scope="scope">
            <el-tooltip
              class="item"
              effect="dark"
              :content="scope.row.content"
              placement="bottom"
            >
              <div class="cell-name">{{ scope.row.content }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="upvoteList" width="60" label="like">
          <template slot-scope="scope">
            <span
              v-if="scope.row.upvoteList !== null"
              style="font-size: 16px; font-weight: bolder"
              >{{ scope.row.upvoteList.split(",").length }}</span
            >
            <span v-else style="font-size: 16px; font-weight: bolder">0</span>
          </template>
        </el-table-column>
        <el-table-column prop="contentType" width="100" label="Mount Source">
          <template slot-scope="scope">
            <span>{{ scope.row.contentType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" width="168" label="Comment on">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" width="120" label="Commentator">
          <template slot-scope="scope">
            <span v-html="highlightKeyword(scope.row.userName)"></span>
          </template>
        </el-table-column>
        <el-table-column prop="replierName" width="120" label="Commented on">
          <template slot-scope="scope">
            <span v-html="highlightKeyword(scope.row.replierName)"></span>
          </template>
        </el-table-column>
        <el-table-column prop="parentId" width="120" label="level">
          <template slot-scope="scope">
            <i
              v-if="scope.row.parentId === null"
              class="el-icon-warning admin-status-ic"
            ></i>
            <i
              v-else
              class="el-icon-success admin-status-ic admin-status-ic--ok"
            ></i>
            <span
              v-if="scope.row.parentId === null"
              style="text-decoration: underline; text-decoration-style: dashed"
              >parent-level</span
            >
            <span v-else>sub-level</span>
          </template>
        </el-table-column>
        <el-table-column label="data manipulation" width="100px" fixed="right">
          <template slot-scope="scope">
            <span class="text-button" @click="handleDelete(scope.row)"
              >delete</span
            >
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="admin-pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      ></el-pagination>
    </div>
    <el-dialog
      custom-class="hp-dialog admin-dialog-wide"
      :show-close="true"
      append-to-body
      :visible.sync="reportDialog"
      width="640px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Comments</span>
        <h2 class="hp-dialog__title">Report details</h2>
      </div>
      <div class="hp-dialog__body admin-report-grid">
        <PieChart :types="types" :values="values" />
        <div>
          <p v-if="!reportsDate.length" class="admin-dashboard__empty">No data available</p>
          <div v-else class="admin-form-stack" style="display:grid;grid-template-columns:repeat(2,1fr);gap:10px;">
            <div v-for="(entity, index) in reportsDate" :key="index" class="admin-report-stat">
              <div class="admin-report-stat__count">{{ entity.count }}</div>
              <div class="admin-report-stat__name">{{ entity.name }}</div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="reportDialog = false">Close</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Editor from "@/components/Editor";
import PieChart from "@/components/PieChart";
export default {
  components: {
    Editor,
    PieChart,
  },
  data() {
    return {
      data: { cover: "" },
      reportsDate: [],
      filterText: "",
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogOperaion: false,
      isOperation: false,
      searchTime: [],
      selectedRows: [],
      html: {},
      fileList: [],
      dynamicTags: ["Healthy", "Wellness"],
      inputVisible: false,
      inputValue: "",
      coverDialog: false,
      commentDialog: false,
      evaluationsList: [],
      count: 0,
      evaluationsDelete: false,
      idToDelete: null,
      contentDialog: false,
      reportDialog: false,
      types: [],
      values: [],
      evalustionsQueryDto: {},
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    keySearch() {
      this.fetchFreshData();
    },
    reportsList(row) {
      this.reportDialog = true;
      this.reportList(row.id);
    },
    async reportList(id) {
      const response = await this.$axios(
        `/evaluations-reports/reportCount/${id}`,
      );
      const { data } = response;
      this.reportsDate = data.data;
      this.types = this.reportsDate.map((entity) => entity.name);
      this.values = this.reportsDate.map((entity) => entity.count);
    },
    showContent(news) {
      this.data = news;
      this.contentDialog = true;
    },
    async removeEvaluations(id) {
      this.evaluationsDelete = true;
      this.idToDelete = id;
    },
    confirmDelete() {
      this.$axios
        .delete(`/evaluations/delete/${this.idToDelete}`)
        .then((response) => {
          this.$message[response.data.code === 200 ? "success" : "error"](
            response.data.msg,
          );
          if (response.data.code === 200) {
            this.evaluationsDelete = false;
            this.loadEvaluationsList();
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    handleEvaluationsClose() {
      this.commentDialog = false;
    },

    commentList(news) {
      this.data = news;
      this.commentDialog = true;
      this.loadEvaluationsList();
    },

    async loadEvaluationsList() {
      try {
        const response = await this.$axios.get(
          `/evaluations/list/${this.data.id}/evaluations`,
        );
        this.evaluationsList = response.data.data.data;
        this.count = response.data.data.count;
      } catch (error) {
        console.error(`Load comment list exception:`, error);
      }
    },

    showPic(news) {
      this.data = news;
      this.coverDialog = true;
    },
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },
    showInput() {
      this.inputVisible = true;
      this.$nextTick((_) => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.dynamicTags.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = "";
    },
    goBack() {
      this.data = {};
      this.dialogOperaion = false;
    },
    goBackPage() {
      this.data = {};
      this.commentDialog = false;
    },

    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },

    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete comment data",
        text: `Deleted unrecoverable, do you continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(
            `/evaluations/batchDelete`,
            ids,
          );
          if (response.data.code === 200) {
            this.$swal.fire({
              title: "Delete Hint",
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
            title: "Error Hint",
            text: e,
            icon: "error",
            showConfirmButton: false,
            timer: 2000,
          });
          console.error(`Comment message deletion anomaly: `, e);
        }
      }
    },

    highlightKeyword(text) {
      if (text == null) {
        return;
      }
      if (this.filterText != "") {
        const regex = new RegExp(this.filterText, "gi");
        return text.replace(
          regex,
          '<span class="highlight" style="background-color: #bec936;color:#373834;">$&</span>',
        );
      }
      return text;
    },
    resetQueryCondition() {
      this.filterText = "";
      this.searchTime = "";
      this.fetchFreshData();
    },
    async updateOperation() {
      try {
        const response = await this.$axios.put(
          "/evaluations/update",
          this.data,
        );
        this.$message[response.data.code === 200 ? "success" : "error"](
          response.data.msg,
        );
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error("Error when submitting form:", error);
        this.$message.error("Submission failed, please try again later!");
      }
    },
    async addOperation() {
      this.data.tag = this.dynamicTags.join(",");
      try {
        const response = await this.$axios.post("/evaluations/save", this.data);
        this.$message[response.data.code === 200 ? "success" : "error"](
          response.data.msg,
        );
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error("Error when submitting form:", error);
        this.$message.error("Submission failed, please try again later!");
      }
    },

    closeDialog() {
      this.dialogOperaion = false;
    },

    clearFormData() {
      this.data = {};
      this.html = "";
      this.fileList = [];
    },
    async fetchFreshData() {
      try {
        let startTime = "";
        let endTime = "";
        if (this.searchTime !== null && this.searchTime.length === 2) {
          const [startDate, endDate] = await Promise.all(
            this.searchTime.map((date) => date.toISOString()),
          );
          startTime = `${startDate.split("T")[0]}T00:00:00`;
          endTime = `${endDate.split("T")[0]}T23:59:59`;
        }

        const params = {
          current: this.currentPage,
          size: this.pageSize,
          startTime: startTime,
          endTime: endTime,
          ...this.evalustionsQueryDto,
        };

        let response = await this.$axios.post("/evaluations/query", params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Fetch data failed:", error);
      }
    },
    add() {
      this.dialogOperaion = true;
    },
    handleFilter() {
      this.currentPage = 1;
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
    handleEdit(row) {
      this.dialogOperaion = true;
      this.isOperation = true;
      this.data = row;
      console.log(this.data);
    },
    async handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
  },
};
</script>
<style scoped lang="scss">
.main {
  padding: 50px 0;
  border-radius: 3px;
  display: flex;
  justify-content: center;

  .item:last-child {
    border-right: none;
  }

  .count,
  .tag {
    text-align: center;
  }

  .count {
    height: 30px;
    line-height: 30px;
    font-size: 26px;
    font-weight: bold;
    color: rgb(120 102 102);
  }

  .tag {
    height: 30px;
    line-height: 30px;
    font-size: 12px;
    color: rgb(120 102 102);
  }
}

.tag {
  color: #1e80ff;
  background-color: #eaf2ff;
  padding: 3px 4px;
  border-radius: 3px;
  font-size: 10px;
  margin-right: 5px;
}

.tag1 {
  color: #1c1c1c;
  background-color: #d7d8b8;
  padding: 3px 4px;
  border-radius: 3px;
  font-size: 10px;
  margin-right: 5px;
}
</style>
