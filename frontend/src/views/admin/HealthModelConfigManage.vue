<template>
  <div>
    <AdminPageShell page-class="admin-page--models">
      <template #toolbar>
<div class="admin-toolbar-row">
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="healthModelConfigQueryDto.name"
          placeholder="Model name"
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
          <el-tooltip content="Add model" placement="bottom">
            <button
              type="button"
              class="admin-toolbar-btn admin-toolbar-btn--primary"
              aria-label="Add model"
              @click="add()"
            >
              <i class="el-icon-plus" aria-hidden="true" />
              <span>Add model</span>
            </button>
          </el-tooltip>
        </div>
      </div>
      </template>
<el-table
        stripe
        v-loading="tableLoading"
        row-key="id"
        @selection-change="handleSelectionChange"
        :data="tableData"
        class="admin-table-full"
        :empty-text="tableLoading ? ' ' : 'No models match your search.'"
      >
        <el-table-column prop="cover" width="72" label="Icon">
          <template slot-scope="scope">
            <img
              :src="scope.row.cover"
              class="admin-table-thumb--sm admin-table-thumb--model"
              alt=""
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          min-width="200"
          label="Name"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <span class="admin-model-name">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="isGlobal"
          label="Scope"
          width="100"
          align="center"
          header-align="center"
          class-name="admin-col-center"
        >
          <template slot-scope="scope">
            <span
              class="admin-badge admin-badge--nowrap"
              :class="
                scope.row.isGlobal
                  ? 'admin-badge--role-admin'
                  : 'admin-badge--role-user'
              "
            >
              {{ scope.row.isGlobal ? "Global" : "Private" }}
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="userName"
          width="100"
          label="Owner"
          show-overflow-tooltip
          class-name="admin-col-nowrap"
        >
          <template slot-scope="scope">
            <span class="admin-model-meta">{{
              scope.row.userName || "—"
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="valueRange"
          width="112"
          label="Threshold"
          align="center"
          header-align="center"
          class-name="admin-col-center admin-col-nowrap"
        >
          <template slot-scope="scope">
            <span class="admin-model-meta admin-model-meta--mono">{{
              scope.row.valueRange || "—"
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="unit"
          width="80"
          label="Unit"
          align="center"
          header-align="center"
          class-name="admin-col-center admin-col-nowrap"
        >
          <template slot-scope="scope">
            <span class="admin-model-meta">{{ scope.row.unit || "—" }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="symbol"
          width="88"
          label="Symbol"
          align="center"
          header-align="center"
          class-name="admin-col-center admin-col-nowrap"
        >
          <template slot-scope="scope">
            <span class="admin-model-meta admin-model-meta--mono">{{
              scope.row.symbol || "—"
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="detail"
          min-width="140"
          label="Description"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <span class="admin-model-desc">{{ scope.row.detail || "—" }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="Actions"
          width="118"
          align="center"
          header-align="center"
          class-name="admin-col-actions"
        >
          <template slot-scope="scope">
            <div class="admin-row-actions">
              <el-tooltip content="Edit model" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--primary"
                  aria-label="Edit model"
                  @click="handleEdit(scope.row)"
                >
                  <i class="el-icon-edit" aria-hidden="true" />
                </button>
              </el-tooltip>
              <el-tooltip content="Delete model" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--danger"
                  aria-label="Delete model"
                  @click="handleDelete(scope.row)"
                >
                  <i class="el-icon-delete" aria-hidden="true" />
                </button>
              </el-tooltip>
            </div>
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
    </AdminPageShell>

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
        <label class="hp-field"><span class="hp-field__label">Name</span><input v-model="data.name" class="hp-field__input" placeholder="Model name" /></label>
        <label class="hp-field"><span class="hp-field__label">Unit</span><input v-model="data.unit" class="hp-field__input" placeholder="e.g. kg, bpm" /></label>
        <label class="hp-field"><span class="hp-field__label">Symbol</span><input v-model="data.symbol" class="hp-field__input" placeholder="Short label" /></label>
        <label class="hp-field"><span class="hp-field__label">Threshold (min,max)</span><input v-model="data.valueRange" class="hp-field__input" placeholder="3000,10000" /></label>
        <label class="hp-field">
          <span class="hp-field__label">Description</span>
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4 }" placeholder="Brief introduction" v-model="data.detail" />
        </label>
        <div class="admin-switch-row">
          <span class="admin-switch-row__label">Global model</span>
          <el-switch
            v-model="data.isGlobal"
            active-color="#2a9d6f"
            inactive-color="#e0e0e0"
            active-text="Yes"
            inactive-text="No"
          />
        </div>
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
import AdminPageShell from "@/components/admin/AdminPageShell.vue";

export default {
  components: { AdminPageShell },
  data() {
    return {
      data: { cover: "", isGlobal: true },
      filterText: "",
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogUserOperaion: false,
      isOperation: false,
      tableData: [],
      tableLoading: true,
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
      this.data = { cover: "", isGlobal: true };
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
      } finally {
        this.tableLoading = false;
      }
    },
    add() {
      this.isOperation = false;
      this.data = { cover: "", isGlobal: true };
      this.dialogUserOperaion = true;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.healthModelConfigQueryDto = {};
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
      this.dialogUserOperaion = true;
      this.isOperation = true;
      this.data = { ...row };
    },
    handleDelete(row) {
      this.selectedRows = [row];
      this.batchDelete();
    },
  },
};
</script>
<style scoped lang="scss"></style>
