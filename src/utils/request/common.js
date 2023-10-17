import instance from "./index";


//user
const LoginAPI = (params) => instance.post("user/login", params);
const UserInfo = (params) => instance.get(`user/info?uid=${params}`)
const AbatinFriendListAPI = (params) => instance.get(`user/friends/${params}`)



// article
export const AsyncArticleList = () => instance.get('/common/article/list');

//file
const UploadAPI = (url) => instance.post(`file/upload`, url);
const DelFileAPI = (rootFileUrl) => instance.delete(`file/delete`, rootFileUrl)
