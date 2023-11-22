import instance from "./index";


//user
export const LoginAPI = (params) => instance.post('user/login', params);
export const UserInfo = (params) => instance.get(`user/info?uid=${params}`)
export const AbatinFriendListAPI = (params) => instance.get(`user/friends/${params}`)
export const PostArticle = (params) => instance.post("user/article/post", params);
export const GetArticleListByPid = (pid) => instance.get(`user/article/list/${pid}`)
export const UpdateUser = (params) => instance.post('user/update', params)
// article
export const AsyncArticleList = () => instance.get('common/article/list');
export const AsyncArticleById = (aid) => instance.get(`common/article/${aid}`);
// upvote
export const AsyncArticleUpvoteStatus = (uid, aid) => instance.get(`user/article/upvote/${uid}/${aid}`);

//collect
export const AsyncArticleCollectStatus = (uid, aid) => instance.get(`user/article/collect/${uid}/${aid}`)
export const AsyncUserCollections = (uid) => instance.get(`user/collect/${uid}`)
// comment 
export const PostComment = (params) => instance.post('user/comment', params);
//file
export const UploadAPI = (url) => instance.post(`file/upload`, url);
export const DelFileAPI = (rootFileUrl) => instance.delete(`file/delete`, rootFileUrl)
