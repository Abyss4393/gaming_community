import instance from "./index";


//user
export const LoginAPI = (params) => instance.post("user/login", params);
export const UserInfo = (params) => instance.get(`user/info?uid=${params}`)
export const AbatinFriendListAPI = (params) => instance.get(`user/friends/${params}`)
export const PostArticle = (params) => instance.post("user/article/post", params);


// article
export const AsyncArticleList = () => instance.get('/common/article/list');
export const AsyncArticleById = (aid) => instance.get(`common/article/${aid}`);

//file
export const UploadAPI = (url) => instance.post(`file/upload`, url);
export const DelFileAPI = (rootFileUrl) => instance.delete(`file/delete`, rootFileUrl)
