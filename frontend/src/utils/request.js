import axios from "axios";
import { getToken } from "@/utils/storage.js";
import { startRequest, endRequest } from "@/utils/loadingBar.js";

const URL_API =
  process.env.VUE_APP_API_BASE_URL ||
  "http://localhost:21090/api/personal-heath/v1.0";

const request = axios.create({
  baseURL: URL_API,
  timeout: 8000,
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
