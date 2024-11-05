import axios from "axios";
import Cookie from "js-cookie";
// import { BaseApi } from "./BaseApi";
const BaseApi = "http://localhost:3000";
export const axiosInstance = axios.create({
    baseURL: BaseApi,
    headers: {
        "Content-Type": "application/json",
    },
});
export const axiosInstanceAuth = axios.create({
    baseURL: BaseApi,
    headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + Cookie.get("token"),
        // console.log(Cookie.get("access_token"))
    },
});
