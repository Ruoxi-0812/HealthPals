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
          start-placeholder="Message start"
          end-placeholder="Message end"
        />
        <el-input
          size="small"
          class="admin-filter-input"
          v-model="messageQueryDto.content"
          placeholder="Message content"
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
          <el-button type="primary" size="small" @click="allMessagePush">
            <i class="el-icon-plus" /> Targeted push
          </el-button>
        </div>
      </div>
    </div>

    <div class="admin-page__body">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="name" width="98" label="Read status">
          <template slot-scope="scope">
            <span>{{ scope.row.isRead ? "Read" : "Unread" }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="messageType" width="148" label="Type">
          <template slot-scope="scope">
            <span v-if="scope.row.messageType === 1">Comment reply</span>
            <span v-else-if="scope.row.messageType === 2">Comment like</span>
            <span v-else-if="scope.row.messageType === 3">Metric reminder</span>
            <span v-else>System</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="receiverName"
          width="108"
          label="Receiver"
        />
        <el-table-column prop="content" label="Message" />
        <el-table-column prop="createTime" width="168" label="Sent" />
        <el-table-column label="Actions" width="88">
          <template slot-scope="scope">
            <span class="text-button" @click="handleDelete(scope.row)">
              Delete
            </span>
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
    </div>

    <el-dialog
      custom-class="hp-dialog"
      :show-close="true"
      append-to-body
      :visible.sync="dialogMessageOperation"
      width="420px"
    >
      <div slot="title" class="hp-dialog__head">
        <span class="hp-dialog__eyebrow">Messages</span>
        <h2 class="hp-dialog__title">Push notification</h2>
      </div>
      <div class="hp-dialog__body">
        <label class="hp-field">
          <span class="hp-field__label">Content</span>
          <el-input
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            placeholder="Message content"
            v-model="messageContent"
          />
        </label>
      </div>
      <div slot="footer" class="hp-dialog__footer">
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--ghost"
          @click="dialogMessageOperation = false"
        >
          Cancel
        </button>
        <button
          type="button"
          class="hp-dialog__btn hp-dialog__btn--primary"
          @click="messagePushOperation"
        >
          Confirm push
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      data: {},
      filterText: "",
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      tableData: [],
      searchTime: [],
      selectedRows: [],
      messageQueryDto: {},
      messageTypeList: [],
      dialogMessageOperation: false,
      messageContent: "",
    };
  },
  created() {
    this.fetchFreshData();
    this.loadAllMessageType();
  },
  methods: {
    messagePushOperation() {
      const message = {
        content: this.messageContent,
      };
      this.$axios
        .post("/message/systemInfoUsersSave", message)
        .then((response) => {
          const { data } = response;
          if (data.code === 200) {
            this.$notify({
              duration: 2000,
              title: "Push",
              message: "Success",
              type: "success",
            });
            this.dialogMessageOperation = false;
            this.messageContent = "";
          }
        });
    },
    allMessagePush() {
      this.dialogMessageOperation = true;
    },
    loadAllMessageType() {
      this.$axios.get("/message/types").then((response) => {
        const { data } = response;
        if (data.code === 200) {
          this.messageTypeList = data.data;
        }
      });
    },
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete message",
        text: `This action cannot be undone. Continue?`,
        icon: "warning",
        danger: true,
        confirmButtonText: "Delete",
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(`/message/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: "Deleted",
              text: response.data.msg,
              icon: "success",
              showConfirmButton: false,
              timer: 2000,
            });
            this.fetchFreshData();
          }
        } catch (e) {
          console.error(`Error deleting message:`, e);
        }
      }
    },
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
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          startTime,
          endTime,
          ...this.messageQueryDto,
        };
        const response = await this.$axios.post("/message/query", params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error("Error fetching message data:", error);
      }
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
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    },
  },
};
</script>

<style scoped lang="scss"></style>
