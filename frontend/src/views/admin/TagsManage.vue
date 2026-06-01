<template>
  <div>
    <AdminPageShell page-class="admin-page--tags">
      <template #toolbar>
        <div class="admin-toolbar-row">
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="tagsQueryDto.name"
          placeholder="Tag name"
          clearable
          @clear="handleFilterClear"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="handleFilter"
          />
        </el-input>
        <div class="admin-page__toolbar-actions">
          <el-tooltip content="Add tag" placement="bottom">
            <button
              type="button"
              class="admin-toolbar-btn admin-toolbar-btn--primary"
              aria-label="Add tag"
              @click="add()"
            >
              <i class="el-icon-plus" aria-hidden="true" />
              <span>Add tag</span>
            </button>
          </el-tooltip>
        </div>
      </div>
      </template>
      <el-table
        stripe
        v-loading="tableLoading"
        row-key="id"
        :data="tableData"
        class="admin-table-full"
        :empty-text="tableLoading ? ' ' : 'No tags match your search.'"
      >
        <el-table-column prop="name" min-width="240" label="Tag name">
          <template slot-scope="scope">
            <span class="admin-tag-cell">
              <i class="el-icon-collection-tag admin-tag-cell__icon" aria-hidden="true" />
              <span class="admin-tag-cell__name">{{ scope.row.name }}</span>
            </span>
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
              <el-tooltip content="Edit tag" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--primary"
                  aria-label="Edit tag"
                  @click="handleEdit(scope.row)"
                >
                  <i class="el-icon-edit" aria-hidden="true" />
                </button>
              </el-tooltip>
              <el-tooltip content="Delete tag" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--danger"
                  aria-label="Delete tag"
                  @click="handleDelete(scope.row)"
                >
                  <i class="el-icon-delete" aria-hidden="true" />
                </button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="admin-pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      />
    </AdminPageShell>

    <el-dialog
      custom-class="hp-dialog"
      :show-close="true"
      :visible.sync="dialogOperaion"
      width="420px"
      append-to-body
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Tags</span>
        <h2 class="hp-dialog__title">
          {{ !isOperation ? "Add tag" : "Edit tag" }}
        </h2>
      </div>
      <div class="hp-dialog__body">
        <label class="hp-field">
          <span class="hp-field__label">Name</span>
          <input
            v-model="data.name"
            class="hp-field__input"
            placeholder="Tag name"
          />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--ghost"
          @click="cancel"
        >
          Cancel
        </button>
        <button
          v-if="!isOperation"
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="addOperation()"
        >
          Add
        </button>
        <button
          v-else
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="updateOperation()"
        >
          Save
        </button>
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
      data: {},
      filterText: "",
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogOperaion: false,
      isOperation: false,
      tableData: [],
      tableLoading: true,
      searchTime: [],
      selectedRows: [],
      tagsQueryDto: {},
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    cancel() {
      this.data = {};
      this.dialogOperaion = false;
      this.isOperation = false;
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
        title: "Delete tag",
        text: `This cannot be undone. Continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(`/tags/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: "Deleted",
              text: response.data.msg,
              icon: "success",
              showConfirmButton: false,
              timer: 2000,
            });
            this.cancel();
            this.fetchFreshData();
          }
        } catch (e) {
          console.error(`Tag deletion error:`, e);
        }
      }
    },
    async updateOperation() {
      try {
        const response = await this.$axios.put("/tags/update", this.data);
        this.$swal.fire({
          title: "Tag updated",
          text: response.data.msg,
          icon: response.data.code === 200 ? "success" : "error",
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.cancel();
          this.fetchFreshData();
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Submission failed, please try again later!");
      }
    },
    async addOperation() {
      try {
        const response = await this.$axios.post("/tags/save", this.data);
        this.$message[response.data.code === 200 ? "success" : "error"](
          response.data.msg,
        );
        if (response.data.code === 200) {
          this.cancel();
          this.fetchFreshData();
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Submission failed, please try again later!");
      }
    },
    async fetchFreshData() {
      this.tableLoading = true;
      try {
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          ...this.tagsQueryDto,
        };
        const response = await this.$axios.post("/tags/query", params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Error fetching tag data:", error);
      } finally {
        this.tableLoading = false;
      }
    },
    add() {
      this.dialogOperaion = true;
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
      this.dialogOperaion = true;
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
