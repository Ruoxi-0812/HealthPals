<template>
  <el-row style="background-color: #ffffff; padding: 5px 0; border-radius: 5px">
    <el-row style="padding: 10px; margin-left: 10px">
      <el-row>
        <el-select
          @change="changeNewsTag"
          size="small"
          v-model="newsQueryDto.tagId"
          placeholder="News Category"
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
          style="margin-left: 5px; width: 220px"
          v-model="searchTime"
          type="daterange"
          range-separator="to"
          start-placeholder="Publish Start"
          end-placeholder="Publish End"
        >
        </el-date-picker>
        <el-input
          size="small"
          style="width: 188px; margin-left: 5px; margin-right: 6px"
          v-model="newsQueryDto.name"
          placeholder="News Title"
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
            ><i class="el-icon-plus"></i>Add News</el-button
          >
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 20px; border-top: 1px solid rgb(245, 245, 245)">
      <el-table
        row-key="id"
        @selection-change="handleSelectionChange"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="cover" width="80" label="Cover">
          <template slot-scope="scope">
            <img
              :src="scope.row.cover"
              style="width: 48px; height: 34px; border-radius: 5px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="tagName" width="138" label="Category">
          <template slot-scope="scope">
            <span
              ><i class="el-icon-discount" style="margin-right: 3px"></i>
              {{ scope.row.tagName }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="isTop" width="128" label="Recommended">
          <template slot-scope="scope">
            <i
              v-if="!scope.row.isTop"
              style="margin-right: 5px"
              class="el-icon-warning"
            ></i>
            <i
              v-else
              style="margin-right: 5px; color: rgb(253, 199, 50)"
              class="el-icon-success"
            ></i>
            <el-tooltip
              v-if="!scope.row.isTop"
              class="item"
              effect="dark"
              content="Not recommended, will not be displayed"
              placement="bottom-end"
            >
              <span
                style="
                  cursor: pointer;
                  text-decoration: underline;
                  text-decoration-style: dashed;
                "
                >Not Recommended</span
              >
            </el-tooltip>
            <span v-else>Recommended</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          width="168"
          label="Publish Time"
        ></el-table-column>
        <el-table-column prop="name" label="Title"></el-table-column>
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
    <el-dialog
      :show-close="false"
      :visible.sync="dialogUserOperaion"
      width="50%"
    >
      <div slot="title">
        <p class="dialog-title">
          {{ !isOperation ? "Add News" : "Edit News" }}
        </p>
      </div>
      <div style="padding: 0 20px">
        <!-- Cover -->
        <el-row style="margin-top: 10px">
          <p>*Cover</p>
          <el-upload
            class="avatar-uploader"
            action="/api/personal-heath/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img
              v-if="data.cover"
              :src="data.cover"
              style="height: 120px; width: 188px"
            />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <!-- Title -->
        <el-row>
          <p>*Title</p>
          <input
            style="font-size: 32px; line-height: 45px"
            class="dialog-input"
            v-model="data.name"
            placeholder="Title"
          />
        </el-row>
        <!-- Category -->
        <el-row style="margin: 12px 0">
          <el-row>
            <span class="dialog-hover">Category</span>
          </el-row>
          <el-radio-group style="margin-top: 10px" v-model="data.tagId">
            <el-radio
              :key="index"
              :label="tag.id"
              v-for="(tag, index) in tagsList"
              >{{ tag.name }}</el-radio
            >
          </el-radio-group>
        </el-row>
        <!-- Recommended -->
        <el-row style="margin: 12px 0">
          <el-row>
            <span class="dialog-hover">Recommended</span>
          </el-row>
          <el-switch
            style="user-select: none; padding: 0 6px"
            v-model="data.isTop"
            active-color="#13ce66"
            inactive-color="rgb(226, 226, 226)"
          >
          </el-switch>
        </el-row>
        <el-row>
          <p>*Content Description</p>
          <Editor
            height="calc(100vh - 500px)"
            :receiveContent="data.content"
            @on-receive="onReceiveContent"
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
          >Edit</el-button
        >
        <el-button
          class="customer"
          size="small"
          style="background-color: rgb(241, 241, 241); border: none"
          @click="dialogUserOperaion = false"
          >Cancel</el-button
        >
      </span>
    </el-dialog>
  </el-row>
</template>

<script>
import Editor from "@/components/Editor";
export default {
  components: { Editor },
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

<style scoped lang="scss">
.tag-tip {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 5px;
  background-color: rgb(245, 245, 245);
  color: rgb(104, 118, 130);
}

.input-def {
  height: 40px;
  line-height: 40px;
  outline: none;
  border: none;
  font-size: 20px;
  color: rgb(102, 102, 102);
  font-weight: 900;
  width: 100%;
}

.dialog-footer {
  /* Center the buttons */
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Adjust spacing between buttons if needed */
.customer {
  margin: 0 8px;
}
</style>
