<template>
  <div>
    <AdminPageShell page-class="admin-page--news">
      <template #toolbar>
<div class="admin-toolbar-row">
        <el-select
          class="admin-filter-select"
          @change="changeNewsTag"
          size="small"
          v-model="newsQueryDto.tagId"
          placeholder="Category"
        >
          <el-option
            v-for="tag in tagsList"
            :key="tag.id"
            :label="tag.name"
            :value="tag.id"
          >
          </el-option>
        </el-select>
        <el-date-picker
          size="small"
          class="admin-date-picker"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Published from"
          end-placeholder="Published to"
        >
        </el-date-picker>
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="newsQueryDto.name"
          placeholder="Article title"
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
          <el-tooltip content="Add article" placement="bottom">
            <button
              type="button"
              class="admin-toolbar-btn admin-toolbar-btn--primary"
              aria-label="Add article"
              @click="add()"
            >
              <i class="el-icon-plus" aria-hidden="true" />
              <span>Add article</span>
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
        :empty-text="tableLoading ? ' ' : 'No articles match your filters.'"
      >
        <el-table-column min-width="280" label="Article">
          <template slot-scope="scope">
            <div class="admin-article-cell">
              <img
                :src="newsCoverSrc(scope.row.cover, scope.row.id)"
                :data-news-id="scope.row.id"
                class="admin-table-thumb admin-table-thumb--article"
                :alt="scope.row.name || 'Article cover'"
                referrerpolicy="no-referrer"
                @error="onCoverImgError"
              />
              <div class="admin-article-cell__body">
                <span class="admin-article-title">{{ scope.row.name }}</span>
                <span
                  v-if="scope.row.tagName"
                  class="admin-badge admin-badge--role-user admin-badge--nowrap"
                >
                  <i class="el-icon-folder-opened" aria-hidden="true" />
                  {{ scope.row.tagName }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="isTop"
          width="100"
          label="Featured"
          class-name="admin-col-nowrap"
        >
          <template slot-scope="scope">
            <span
              v-if="scope.row.isTop"
              class="admin-badge admin-badge--ok admin-badge--nowrap"
            >
              <i class="el-icon-star-on" aria-hidden="true" />
              Featured
            </span>
            <span v-else class="admin-model-meta">—</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          width="148"
          label="Published"
          class-name="admin-col-recorded"
        >
          <template slot-scope="scope">
            <el-tooltip
              :content="formatDateTimeFull(scope.row.createTime)"
              placement="top"
              :disabled="!scope.row.createTime"
            >
              <span class="admin-health-recorded">{{
                formatRecordedLine(scope.row.createTime)
              }}</span>
            </el-tooltip>
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
              <el-tooltip content="Edit article" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--primary"
                  aria-label="Edit article"
                  @click="handleEdit(scope.row)"
                >
                  <i class="el-icon-edit" aria-hidden="true" />
                </button>
              </el-tooltip>
              <el-tooltip content="Delete article" placement="top">
                <button
                  type="button"
                  class="admin-row-actions__btn admin-row-actions__btn--icon admin-row-actions__btn--danger"
                  aria-label="Delete article"
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
      custom-class="hp-dialog admin-dialog-editor"
      :show-close="true"
      append-to-body
      :visible.sync="dialogUserOperaion"
      width="860px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">News</span>
        <h2 class="hp-dialog__title">{{ !isOperation ? "Add article" : "Edit article" }}</h2>
      </div>
      <div class="hp-dialog__body admin-form-stack">
        <label class="hp-field">
          <span class="hp-field__label">Cover</span>
          <el-upload class="hp-dialog__avatar-uploader avatar-uploader" action="/api/personal-heath/v1.0/file/upload" :show-file-list="false" :on-success="handleAvatarSuccess">
            <img
              v-if="data.cover"
              :src="newsCoverSrc(data.cover, data.id)"
              :data-news-id="data.id"
              class="admin-cover-preview"
              alt=""
              referrerpolicy="no-referrer"
              @error="onCoverImgError"
            />
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </label>
        <label class="hp-field"><span class="hp-field__label">Title</span><input v-model="data.name" class="hp-field__input" type="text" placeholder="Article title" /></label>
        <label class="hp-field">
          <span class="hp-field__label">Category</span>
          <el-radio-group v-model="data.tagId">
            <el-radio :key="index" :label="tag.id" v-for="(tag, index) in tagsList">{{ tag.name }}</el-radio>
          </el-radio-group>
        </label>
        <div class="admin-switch-row">
          <span class="admin-switch-row__label">Featured on home</span>
          <el-switch v-model="data.isTop" active-color="#2a9d6f" inactive-color="#e0e0e0" />
        </div>
        <label class="hp-field">
          <span class="hp-field__label">Content</span>
          <Editor height="min(360px, 50vh)" :receiveContent="data.content" @on-receive="onReceiveContent" />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button type="button" class="hp-dialog__btn hp-dialog__btn--ghost" @click="dialogUserOperaion = false">Cancel</button>
        <button v-if="!isOperation" type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="addOperation()">Publish</button>
        <button v-else type="button" class="hp-dialog__btn hp-dialog__btn--primary" @click="updateOperation()">Save</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AdminPageShell from "@/components/admin/AdminPageShell.vue";

import Editor from "@/components/Editor";
import {
  formatDateTimeFull,
  formatRecordedLine,
} from "@/utils/data";
import { newsCoverSrc, onCoverImgError } from "@/utils/coverImage";

export default {
  components: { AdminPageShell, Editor },
  data() {
    return {
      userPwd: "",
      data: { cover: "" },
      filterText: "",
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogUserOperaion: false, // Dialog visibility control
      isOperation: false, // Flag to identify add or edit operation
      tableData: [],
      tableLoading: true,
      searchTime: [],
      selectedRows: [],
      status: null,
      newsQueryDto: {}, // Search criteria
      messsageContent: "",
      tagsList: [],
    };
  },
  created() {
    this.fetchFreshData();
    this.loadAllTags();
  },
  methods: {
    newsCoverSrc,
    onCoverImgError,
    formatDateTimeFull,
    formatRecordedLine,
    changeNewsTag(tagId) {
      this.newsQueryDto.tagId = tagId;
      this.fetchFreshData();
    },
    onReceiveContent(html) {
      this.data.content = html;
    },

    loadAllTags() {
      this.$axios.post(`/tags/query`, {}).then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.tagsList = data.data;
          this.tagsList.unshift({ name: "All", id: null });
        }
      });
    },
    handleAvatarSuccess(res, file) {
      if (res.code !== 200) {
        this.$message.error(`News cover upload failed`);
        return;
      }
      this.data.cover = "";
      this.$message.success(`News cover uploaded successfully`);
      this.data.cover = res.data;
    },

    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    // Batch delete data
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete News Data",
        text: `Deletion is irreversible, do you want to continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(`/news/batchDelete`, ids);
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
          console.error(`News deletion error:`, e);
        }
      }
    },
    resetQueryCondition() {
      this.newsQueryDto = {};
      this.searchTime = [];
      this.fetchFreshData();
    },
    // Update news information
    async updateOperation() {
      try {
        const response = await this.$axios.put("/news/update", this.data);
        this.$swal.fire({
          title: "News Update",
          text: response.data.msg,
          icon: response.data.code === 200 ? "success" : "error",
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Submission failed, please try again later!");
      }
    },
    // Add news information
    async addOperation() {
      try {
        const response = await this.$axios.post("/news/save", this.data);
        this.$message[response.data.code === 200 ? "success" : "error"](
          response.data.msg,
        );
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error("Error submitting form:", error);
        this.$message.error("Submission failed, please try again later!");
      }
    },
    closeDialog() {
      this.dialogUserOperaion = false;
    },
    clearFormData() {
      this.data = {};
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
        // Request parameters
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          startTime: startTime,
          endTime: endTime,
          ...this.newsQueryDto,
        };
        const response = await this.$axios.post("/news/query", params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Error fetching news data:", error);
      } finally {
        this.tableLoading = false;
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
      row.userPwd = null;
      this.data = { ...row };
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
  },
};
</script>
