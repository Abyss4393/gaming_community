import instance from "./index";


//user
export const LoginAPI = (params) => instance.post("user/login", params);


export const UserInfo = (params) => instance.get(`user/info?uid=${params}`)
export const AbatinFriendListAPI = (params) => instance.get(`user/friends/${params}`)
//file
const UploadAPI = (url) => instance.post(`file/upload`, url);
const DelFileAPI = (rootFileUrl) => instance.delete(`file/delete`, rootFileUrl)
