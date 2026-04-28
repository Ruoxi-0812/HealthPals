<template>
  <el-row style="background-color: #ffffff; padding: 5px 0; border-radius: 5px">
    <el-row style="padding: 10px; margin-left: 5px">
      <el-row>
        <el-input
          size="small"
          style="width: 188px; margin-left: 5px; margin-right: 6px"
          v-model="tagsQueryDto.name"
          placeholder="Tag Name"
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
            @click="add()"
            ><i class="el-icon-plus"></i>Add Tag</el-button
          >
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 20px; border-top: 1px solid rgb(245, 245, 245)">
      <el-table row-key="id" :data="tableData" style="width: 100%">
        <el-table-column prop="name" label="Tag Name"></el-table-column>
        <el-table-column label="Actions" width="120">
          <template slot-scope="scope">
            <span class="text-button" @click="handleEdit(scope.row)">Edit</span>
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
    <el-dialog :show-close="false" :visible.sync="dialogOperaion" width="24%">
      <div slot="title">
        <p class="dialog-title">{{ !isOperation ? "Add Tag" : "Edit Tag" }}</p>
      </div>
      <div style="padding: 0 20px; margin-bottom: 30px">
        <el-row>
          <input
            style="font-size: 34px"
            class="dialog-input"
            v-model="data.name"
            placeholder="Tag Name"
          />
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          size="small"
          v-if="!isOperation"
          style="
            background-color: rgb(96, 98, 102);
            color: rgb(247, 248, 249);
            border: none;
          "
          class="customer"
          type="info"
          @click="addOperation()"
          >Add</el-button
        >
        <el-button
          size="small"
          v-else
          style="
            background-color: rgb(96, 98, 102);
            color: rgb(247, 248, 249);
            border: none;
          "
          class="customer"
          type="info"
          @click="updateOperation()"
          >Update</el-button
        >
        <el-button
          class="customer"
          size="small"
          style="background-color: rgb(241, 241, 241); border: none"
          @click="cancel"
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
      userPwd: "",
      data: {},
      filterText: "",
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogOperaion: false, // Dialog visibility control
      isOperation: false, // Flag to identify add or edit operation
      tableData: [],
      searchTime: [],
      selectedRows: [],
      status: null,
      tagsQueryDto: {}, // Search criteria
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
    handleAvatarSuccess(res, file) {
      if (res.code !== 200) {
        this.$message.error(`Tag avatar upload failed`);
        return;
      }
      this.$message.success(`Tag avatar uploaded successfully`);
      this.data.userAvatar = res.data;
      console.log(this.data);
    },
    // Handle multi-selection
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
        title: "Delete Tag Data",
        text: `Deletion is irreversible, do you want to continue?`,
        icon: "warning",
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map((entity) => entity.id);
          const response = await this.$axios.post(`/tags/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: "Delete Notification",
              text: response.data.msg,
              icon: "success",
              showConfirmButton: false,
              timer: 2000,
            });
            this.cancel();
            this.fetchFreshData();
            return;
          }
        } catch (e) {
          console.error(`Tag deletion error:`, e);
        }
      }
    },
    resetQueryCondition() {
      this.tagsQueryDto = {};
      this.searchTime = [];
      this.fetchFreshData();
    },
    // Update tag information
    async updateOperation() {
      try {
        const response = await this.$axios.put("/tags/update", this.data);
        this.$swal.fire({
          title: "Tag Update",
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
    // Add tag information
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
      try {
        // Request parameters
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
<style scoped lang="scss"></style>
