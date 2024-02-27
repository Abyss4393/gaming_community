import instance from "./index";


//user
export const LoginAPI = (params) => instance.post('user/login', params);
export const UserInfo = (params) => instance.get(`user/info?uid=${params}`)
export const ObtainFriendList = (uid) => instance.get(`user/friends/${uid}`)
export const PostArticle = (params) => instance.post("user/article/post", params);
export const UpdateUser = (params) => instance.post('user/update', params)
// article
export const GetArticleListByPid = (pid) => instance.get(`user/article/list/${pid}`)
export const AsyncArticleList = () => instance.get('common/article/list');
export const AsyncArticleById = (aid) => instance.get(`common/article/${aid}`);
// upvote
export const AsyncArticleUpvoteStatus = (uid, aid) => instance.get(`user/article/upvote/${uid}/${aid}`);
//collect
export const AsyncArticleCollectStatus = (uid, aid) => instance.get(`user/article/collect/${uid}/${aid}`)
export const AsyncUserCollections = (uid) => instance.get(`user/collect/${uid}`)
// comment 
export const PostComment = (params) => instance.post('user/comment', params);
export const AsyncUserComments = (uid) => instance.get(`user/comment/${uid}`)

export const DelCommentByIds = (aid, uid) => instance.delete(`user/comment/delete/${aid}/${uid}`)
// reply
export const PostReply = (params) => instance.post('user/reply', params);
export const AsyncUserReplies = (uid) => instance.get(`user/reply/${uid}`)
export const AsyncAticleReplyTrees = (aid, commentId, replyId) => instance.get(`user/reply/tree/${aid}?comment_id=${commentId}&reply_id=${replyId}`)
//file
export const UploadAPI = (url) => instance.post(`file/upload`, url);
export const DelFileAPI = (rootFileUrl) => instance.delete(`file/delete`, rootFileUrl)
// upvote
export const AsyncArticleUpvote = (uid, aid) => instance.get(`common/article/upvote/${uid}/${aid}`)
// trample
export const AsyncArticleTrample = (aid) => instance.get(`common/article/trample${aid}`)
// colloct
export const AsyncArticleCollect = (params) => instance.post('common/article/collect', params)
// friend
export const AddFriend = (uid, fid) => instance.get(`user/friend/add/${uid}/${fid}`)
export const AsyncUserFriendStatus = (uid, fid) => instance.get(`user/friend/status/${uid}/${fid}`)
// history
export const AsyncQueryHistory = () => instance.get('common/history')