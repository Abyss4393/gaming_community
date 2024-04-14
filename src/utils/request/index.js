import axios from "axios";
import { global } from '@/main.js'
import { getToken } from "../auth";


const instance = axios.create({
    baseURL: "http://localhost:2766/api/private/v1/community/",
    timeout: 30000,
})

instance.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    config.headers['token'] = getToken()
    return config
}, error => {
    return Promise.reject(error);
})

instance.interceptors.response.use(response => {
    return response.data
})

export default instance