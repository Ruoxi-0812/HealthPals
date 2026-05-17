import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from "element-ui";
import locale from "element-ui/lib/locale/lang/en";
import "element-ui/lib/theme-chalk/index.css";
import { getToken } from "@/utils/storage.js";
import echarts from "echarts";
Vue.prototype.$echarts = echarts;
Vue.use(ElementUI, { locale });
Vue.use(VueRouter);

const routes = [
  {
    path: "*",
    redirect: "/login",
  },
  {
    path: "/login",
    component: () => import(`@/views/login/Login.vue`),
  },
  {
    path: "/register",
    component: () => import(`@/views/register/Register.vue`),
  },
  {
    path: "/message",
    component: () => import(`@/views/user/Message.vue`),
  },
  {
    path: "/record",
    component: () => import(`@/views/user/Record.vue`),
  },
  {
    path: "/admin",
    component: () => import(`@/views/admin/Home.vue`),
    meta: {
      requireAuth: true,
    },
    children: [
      {
        path: "/adminLayout",
        name: "Indicator Panel",
        icon: "el-icon-pie-chart",
        component: () => import(`@/views/admin/Main.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/userManage",
        name: "User Manage",
        icon: "el-icon-user",
        component: () => import(`@/views/admin/UserManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/tagsManage",
        name: "News Classification",
        icon: "el-icon-house",
        component: () => import(`@/views/admin/TagsManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/newsManage",
        name: "News Manage",
        icon: "el-icon-document",
        component: () => import(`@/views/admin/NewsManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/healthModelConfigManage",
        name: "Health Model Manage",
        icon: "el-icon-files",
        component: () => import(`@/views/admin/HealthModelConfigManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/userHealthManage",
        name: "Health Records",
        icon: "el-icon-c-scale-to-original",
        component: () => import(`@/views/admin/UserHealthManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/messageManage",
        name: "Message Manage",
        icon: "el-icon-message",
        component: () => import(`@/views/admin/MessageManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/evaluationsManage",
        name: "Comment Manage",
        icon: "el-icon-chat-dot-round",
        component: () => import(`@/views/admin/EvaluationsManage.vue`),
        meta: { requireAuth: true },
      },
    ],
  },
  {
    path: "/user",
    component: () => import(`@/views/user/Main.vue`),
    meta: {
      requireAuth: true,
    },
    /* 直接打开 /user 时没有子路由匹配，主区域会为空白；默认进首页 */
    redirect: "/news-record",
    children: [
      {
        name: "News home",
        path: "/news-record",
        component: () => import(`@/views/user/Home.vue`),
        meta: {
          requireAuth: true,
        },
      },
      {
        name: "My Favorites",
        path: "/my-save",
        component: () => import(`@/views/user/NewsSave.vue`),
        meta: {
          requireAuth: true,
        },
      },
      {
        name: "Health Data",
        path: "/user-health-model",
        component: () => import(`@/views/user/UserHealthModel.vue`),
        meta: {
          requireAuth: true,
        },
      },
      {
        name: "Ask Pal",
        path: "/health-assistant",
        component: () => import(`@/views/user/HealthAssistant.vue`),
        meta: {
          requireAuth: true,
        },
      },
      {
        name: "Health Information Details",
        path: "/news-detail",
        component: () => import(`@/views/user/NewsDetail.vue`),
        meta: {
          requireAuth: true,
        },
        isHidden: true,
      },
      {
        name: "Search Page",
        path: "/search-detail",
        component: () => import(`@/views/user/Search.vue`),
        meta: {
          requireAuth: true,
        },
        isHidden: true,
      },
    ],
  },
];
const router = new VueRouter({
  routes,
  mode: "history",
});
router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth) {
    const token = getToken();
    if (token !== null) {
      next();
    } else {
      next("/login");
    }
  } else {
    next();
  }
});
export default router;
