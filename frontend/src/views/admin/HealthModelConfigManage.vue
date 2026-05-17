<template>
  <div class="admin-page">
    <div class="admin-page__toolbar">
      <div class="admin-toolbar-row">
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="healthModelConfigQueryDto.name"
          placeholder="Configuration name"
          clearable
          @clear="handleFilterClear"
        >
          <el-button
            slot="append"
            @click="handleFilter"
            icon="el-icon-search"
          ></el-button>
        </el-input>
        <div class="admin-page__toolbar-actions">
          <el-button type="primary" size="small"
            @click="add()"
            ><i class="el-icon-plus"></i>New Models</el-button
          >
        </div>
      </div>
    </div>

    <div class="admin-page__body">
      <el-table
        row-key="id"
        @selection-change="handleSelectionChange"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="cover" width="80" label="Diagram">
          <template slot-scope="scope">
            <img
              :src="scope.row.cover"
              style="width: 30px; height: 30px; border-radius: 5px"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          width="218"
          label="Model Name"
        ></el-table-column>
        <el-table-column prop="isGlobal" label="Limits" width="128">
          <template slot-scope="scope">
            <span>{{
              scope.row.isGlobal ? "Global Model" : "Private Model"
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="userName"
          width="108"
          label="Configure Users"
        ></el-table-column>
        <el-table-column
          prop="valueRange"
          width="128"
          label="value range"
        ></el-table-column>
        <el-table-column prop="unit" width="88" label="unit"></el-table-column>
        <el-table-column
          prop="symbol"
          width="88"
          label="symbol"
        ></el-table-column>
        <el-table-column
          prop="detail"
          label="Model Introduction"
        ></el-table-column>
        <el-table-column label="Manipulate" width="120">
          <template slot-scope="scope">
            <span class="text-button" @click="handleEdit(scope.row)">Edit</span>
            <span class="text-button" @click="handleDelete(scope.row)"
              >Delete</span
            >
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="admin-pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      ></el-pagination>
    </div>
    <el-dialog
      custom-class="hp-dialog admin-dialog-wide"
      :show-close="true"
      append-to-body
      :visible.sync="dialogUserOperaion"
      width="520px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Health models</span>
        <h2 class="hp-dialog__title">{{ !isOperation ? "Add model" : "Edit model" }}</h2>
      </div>
      <div class="hp-dialog__body admin-form-stack">
        <label class="hp-field">
          <span class="hp-field__label">Icon</span>
          <el-upload class="hp-dialog__avatar-uploader avatar-uploader" action="/api/personal-heath/v1.0/file/upload" :show-file-list="false" :on-success="handleAvatarSuccess">
            <img v-if="data.cover" :src="data.cover" class="admin-icon-preview" />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </label>
        <label class="hp-field"><span class="hp-field__label">Name</span><input v-model="data.name" class="hp-field__input" placeholder="Configuration name" /></label>
        <label class="hp-field"><span class="hp-field__label">Unit</span><input v-model="data.unit" class="hp-field__input" placeholder="e.g. kg, bpm" /></label>
        <label class="hp-field"><span class="hp-field__label">Symbol</span><input v-model="data.symbol" class="hp-field__input" placeholder="Short label" /></label>
        <label class="hp-field"><span class="hp-field__label">Thresholds (min,max)</span><input v-model="data.valueRange" class="hp-field__input" placeholder="3000,10000" /></label>
        <label class="hp-field">
          <span class="hp-field__label">Description</span>
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4 }" placeholder="Brief introduction" v-model="data.detail" />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button type="button" class="hp-dialog__btn hp-dialog__btn--ghost" @click="cannel()">Cancel</button>
        <button v-if="!isOperation" type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="addOperation">Add</button>
        <button v-else type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="updateOperation">Save</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      data: { cover: "" },
      filterText: "",
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogUserOperaion: false,
      isOperation: false,
      tableData: [],
      searchTime: [],
      selectedRows: [],
      status: null,
      healthModelConfigQueryDto: {},
      messsageContent: "",
      tagsList: [],
      valuesRange: [10, 50],
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    handleAvatarSuccess(res, file) {
      this.$notify({
        duration: 2000,
        title: "Icon Upload",
        message: res.code === 200 ? "Success" : "Error",
        type: res.code === 200 ? "success" : "error",
      });
      this.data.cover = res.data;
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
        title: "Deleting Health Model Data",
        text: `Deleted unrecoverable, do you continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(
            `/health-model-config/batchDelete`,
            ids,
          );
          if (response.data.code === 200) {
            this.$notify({
              duration: 2000,
              title: "Delete operation",
              message: "Success",
              type: "success",
            });
            this.fetchFreshData();
            return;
          }
        } catch (e) {
          console.error(`Health Model Information Deletion Exception：`, e);
        }
      }
    },
    resetQueryCondition() {
      this.healthModelConfigQueryDto = {};
      this.searchTime = [];
      this.fetchFreshData();
    },

    async updateOperation() {
      this.$axios
        .put("/health-model-config/update", this.data)
        .then((res) => {
          if (res.data.code === 200) {
            this.cannel();
            this.fetchFreshData();
            this.$notify({
              duration: 2000,
              title: "Revise Operation",
              message: "Success",
              type: "success",
            });
          }
        })
        .catch((error) => {
          console.log("Model Modification Exception=>", error);
        });
    },
    cannel() {
      this.dialogUserOperaion = false;
      this.isOperation = false;
      this.data = {};
      this.valueRange = null;
    },

    addOperation() {
      this.$axios
        .post("/health-model-config/config/save", this.data)
        .then((res) => {
          if (res.data.code === 200) {
            this.cannel();
            this.fetchFreshData();
            this.$notify({
              duration: 2000,
              title: "New operations",
              message: "Success",
              type: "success",
            });
          }
        })
        .catch((error) => {
          console.log("Model Addition Exception=>", error);
        });
    },

    async fetchFreshData() {
      try {
        this.tableData = [];
        let startTime = null;
        let endTime = null;
        if (this.searchTime != null && this.searchTime.length === 2) {
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
          ...this.healthModelConfigQueryDto,
        };
        const response = await this.$axios.post(
          "/health-model-config/query",
          params,
        );
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Query Health Model Information Exception:", error);
      }
    },
    add() {
      this.dialogUserOperaion = true;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.filterText = "";
      this.handleFilter();
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
      this.dialogUserOperaion = true;
      this.isOperation = true;
      this.data = { ...row };
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
  },
};
</script>
<style scoped lang="scss"></style>
