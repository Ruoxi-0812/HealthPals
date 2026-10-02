import axios from "axios";
import { getToken } from "@/utils/storage.js";
import { startRequest, endRequest } from "@/utils/loadingBar.js";

const URL_API =
  process.env.VUE_APP_API_BASE_URL ||
  "http://localhost:21090/api/personal-heath/v1.0";

const request = axios.create({
  baseURL: URL_API,
  // Render's free tier can still take a while to answer the very first
  // request after a deploy or a gap in the keep-warm ping (see
  // .github/workflows/keep-backend-warm.yml), even once mostly kept warm.
  // 8s was tight enough that a single slow response silently failed page
  // loads that have no retry UI (e.g. Home.vue's featured/latest articles).
  timeout: 30000,
});

request.interceptors.request.use(
  (config) => {
    startRequest();
    const token = getToken();
    if (token !== null) {
      config.headers["token"] = token;
    }
    return config;
  },
  (error) => {
    endRequest();
    return Promise.reject(error);
  },
);

request.interceptors.response.use(
  (response) => {
    endRequest();
    return response;
  },
  (error) => {
    endRequest();
    return Promise.reject(error);
  },
);
export default request;
