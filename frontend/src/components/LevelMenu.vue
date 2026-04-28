<template>
  <div class="nav">
    <Logo sysName="HealthPals" />
    <div
      v-if="!item.isHidden"
      :style="{
        color: selectedIndex === index ? '#1c1c1c' : 'rgb(102 102 102)',
      }"
      class="menu-item"
      v-for="(item, index) in menus"
      :key="index"
      @click="menuClick(`${item.path}`, index)"
    >
      <span>
        <i :class="item.icon"></i>
        <span>&nbsp; {{ item.name }}</span>
      </span>
    </div>
    <div class="search">
      <input
        placeholder="Search information"
        @keyup.enter="search"
        v-model="filterText"
      />
      <span @click="search">Search Now</span>
    </div>
    <div class="record">
      <span @click="healthDataRecord" style="margin: 14px 10px">
        <i class="el-icon-connection"></i>
        Health Records
      </span>
    </div>
    <div class="messsage">
      <el-badge v-if="noReadMsg !== 0" :value="noReadMsg">
        <span class="message-span" @click="messageCenter">
          <i class="el-icon-chat-dot-round"></i>
        </span>
      </el-badge>
      <span v-else class="message-span" @click="messageCenter">
        <i class="el-icon-chat-dot-round"></i>
      </span>
    </div>
    <div>
      <el-dropdown class="user-dropdown">
        <span
          class="el-dropdown-link"
          style="display: flex; align-items: center"
        >
          <el-avatar
            :size="25"
            :src="userInfo.url"
            style="margin-top: 0"
          ></el-avatar>
          <span
            class="userName"
            style="
              margin-left: 5px;
              font-size: 14px;
              width: 100px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            "
            >{{ userInfo.name }}</span
          >
          <i
            class="el-icon-arrow-down el-icon--right"
            style="margin-left: 5px"
          ></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-user" @click.native="userCenterPanel"
            >Personal Center</el-dropdown-item
          >
          <el-dropdown-item
            icon="el-icon-warning-outline"
            @click.native="resetPwd"
            >Change Password</el-dropdown-item
          >
          <el-dropdown-item icon="el-icon-back" @click.native="loginOut"
            >Log out</el-dropdown-item
          >
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>
<script>
import { clearToken } from "@/utils/storage.js";
import Logo from "@/components/Logo.vue";
export default {
  name: "UserMenu",
  components: {
    Logo,
  },
  data() {
    return {
      selectedIndex: 0,
      messagePath: "/message",
      loginPath: "/login",
      sysName: "HealthPals",
      defaultPath: "/news-record",
      filterText: "",
      noReadMsg: 0,
    };
  },
  props: {
    menus: {
      type: Array,
      required: true,
    },

    userInfo: {
      type: Object,
      required: true,
    },
  },
  mounted() {
    this.pathToDo(this.defaultPath);
    this.loadMsgCount();
  },
  methods: {
    search() {
      if (this.$route.path === "/search-detail") {
        sessionStorage.setItem("keyWord", this.filterText);
        return;
      }
      sessionStorage.setItem("keyWord", this.filterText);
      this.$emit("eventListener", "search-detail");
    },

    userCenterPanel() {
      this.$emit("eventListener", "center");
    },

    resetPwd() {
      this.$emit("eventListener", "resetPwd");
    },

    loginOut() {
      this.$emit("eventListener", "loginOut");
    },

    dietRecord() {
      this.$emit("eventListener", "dietRecord");
    },

    healthDataRecord() {
      this.$emit("eventListener", "healthDataRecord");
    },

    async loadMsgCount() {
      const userInfo = sessionStorage.getItem("userInfo");
      const userInfoEntity = JSON.parse(userInfo);
      const messageQueryDto = { userId: userInfoEntity.id, isRead: false };
      const response = await this.$axios.post(
        `/message/query`,
        messageQueryDto,
      );
      const { data } = response;
      if (data.code === 200) {
        this.noReadMsg = data.data.length;
      }
    },

    pathToDo(path) {
      if (this.$route.path !== path) {
        this.$router.push(path);
      }
    },

    menuClick(path, index) {
      this.selectedIndex = index;
      this.pathToDo(path);
    },

    messageCenter() {
      this.selectedIndex = null;
      this.pathToDo(this.messagePath);
    },
    // 退出登录
    async out() {
      const confirmed = await this.$swalConfirm({
        title: "Logout or not",
        text: `After logging out, you will have to log in again to use the system`,
        icon: "warning",
      });
      if (confirmed) {
        this.$swal.fire({
          title: "Log out",
          text: "You have successfully logged out.",
          icon: "success",
          showConfirmButton: false,
          timer: 1300,
        });
        setTimeout(() => {
          clearToken();
          this.$router.push("/loginPath");
        }, 1300);
      } else {
        console.log("Canceled the logout operation");
      }
    },
  },
};
</script>
<style scoped lang="scss">
.nav {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  height: 80px;

  .menu-item {
    font-size: 14px;
    cursor: pointer;
  }

  .search {
    border-radius: 15px;
    font-size: 14px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    input {
      outline: none;
      border: none;
      border-left: 1px solid rgb(51, 51, 51);
      border-top: 1px solid rgb(51, 51, 51);
      border-bottom: 1px solid rgb(51, 51, 51);
      padding: 6px 10px;
      font-size: 12px;
    }

    input:focus {
      color: rgb(31, 31, 31);
    }

    span:hover {
      background-color: rgb(31, 31, 31);
    }

    span {
      background-color: rgb(51, 51, 51);
      display: inline-block;
      cursor: pointer;
      font-size: 12px;
      color: rgb(245, 245, 245);
      padding: 6px 10px;
      border-top-right-radius: 15px;
      border-bottom-right-radius: 15px;
    }
  }

  .record:hover {
    background-color: rgb(40, 150, 103);
  }

  .messsage:hover {
    background-color: rgb(40, 150, 103);
  }

  .messsage {
    background-color: rgb(56, 183, 129);
    cursor: pointer;
    padding: 2px 14px;
    border-radius: 10px;
    color: rgb(255, 255, 255);
  }

  .record {
    font-size: 14px;
    cursor: pointer;
    background-color: rgb(56, 183, 129);
    color: rgb(255, 255, 255);
    padding: 4px 10px;
    border-radius: 15px;
  }
}
</style>
