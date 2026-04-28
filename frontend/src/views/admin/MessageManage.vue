<template>
  <el-row
    style="background-color: #ffffff; padding: 10px 0; border-radius: 5px"
  >
    <el-row style="padding: 10px; margin-left: 5px">
      <el-row>
        <el-date-picker
          size="small"
          style="margin-left: 10px; width: 220px"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Message Start"
          end-placeholder="Message End"
        >
        </el-date-picker>
        <el-input
          size="small"
          style="width: 188px; margin-left: 5px; margin-right: 6px"
          v-model="messageQueryDto.content"
          placeholder="Message Content"
          clearable
          @clear="handleFilterClear"
        >
          <el-button
            slot="append"
            @click="handleFilter"
            icon="el-icon-search"
          ></el-button>
        </el-input>
        <span style="float: right">
          <el-button
            size="small"
            style="
              background-color: rgb(96, 98, 102);
              color: rgb(247, 248, 249);
              border: none;
            "
            class="customer"
            type="info"
            @click="allMessagePush"
            ><i class="el-icon-plus"></i>Targeted Push</el-button
          >
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 20px; border-top: 1px solid rgb(245, 245, 245)">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="name" width="98" label="Read Status">
          <template slot-scope="scope">
            <span>{{ scope.row.isRead ? "Read" : "Unread" }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="messageType" width="148" label="Message Type">
          <template slot-scope="scope">
            <span v-if="scope.row.messageType === 1">Comment Reply</span>
            <span v-else-if="scope.row.messageType === 2">Comment Like</span>
            <span v-else-if="scope.row.messageType === 3">Metric Reminder</span>
            <span v-else>System Notification</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="receiverName"
          width="108"
          label="Receiver"
        ></el-table-column>
        <el-table-column prop="content" label="Message Body"></el-table-column>
        <el-table-column
          prop="createTime"
          width="168"
          label="Sent Time"
        ></el-table-column>
        <el-table-column label="Actions" width="88">
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
    <!-- System-wide Message Push -->
    <el-dialog
      :show-title="false"
      :show-close="false"
      :visible.sync="dialogMessageOperation"
      width="24%"
    >
      <p style="padding: 20px 0 0 20px">Message Push</p>
      <div style="padding: 0 20px">
        <el-row>
          <span class="dialog-hover">Message Content</span>
          <el-input
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4 }"
            placeholder="Message Content"
            v-model="messageContent"
          >
          </el-input>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          size="small"
          style="background-color: rgb(43, 121, 203); border: none"
          class="customer"
          type="info"
          @click="messagePushOperation"
          >Confirm Push</el-button
        >
        <el-button
          class="customer"
          size="small"
          style="background-color: rgb(241, 241, 241); border: none"
          @click="dialogMessageOperation = false"
          >Cancel</el-button
        >
      </span>
    </el-dialog>
  </el-row>
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
      messageQueryDto: {}, // Search conditions
      messsageContent: "",
      messageTypeList: [],
      dialogMessageOperation: false, // Toggle variable for message dialog display
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
              title: "Push Operation",
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
    // Batch delete data
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`No data selected`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: "Delete Message Data",
        text: `This action cannot be undone. Do you want to proceed?`,
        icon: "warning",
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(`/message/batchDelete`, ids);
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
          console.error(`Error deleting message information:`, e);
        }
      }
    },
    resetQueryCondition() {
      this.messageQueryDto = {};
      this.searchTime = [];
      this.fetchFreshData();
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
        // Request parameters
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          startTime: startTime,
          endTime: endTime,
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
