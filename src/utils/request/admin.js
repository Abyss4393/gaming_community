import instance from "./index";

export const SelectBatchUser = (currentPage = 1, pageSize = 10) => instance.get(`admin/user/page?currentPage=${currentPage}&pageSize=${pageSize}`)

export const SearchUser = (keyword, currentPage = 1, pageSize = 10) => instance.post(`admin/user/search?currentPage=${currentPage}&pageSize=${pageSize}`, { keyword: keyword })

export const ModifyUser = (user) => instance.post('admin/user/modify', user)

export const DeleteUser = (id) => instance.delete(`admin/user/delete?id=${id}`)

export const SelectBatchArticle = (currentPage = 1, pageSize = 10) => instance.get(`admin/article/page?currentPage=${currentPage}&pageSize=${pageSize}`)


export const SearchArticle = (keyword, currentPage = 1, pageSize = 10) => instance.post(`admin/article/search?currentPage=${currentPage}&pageSize=${pageSize}`, { keyword: keyword })

export const ConfirmArticle = (article) => instance.post('admin/audit/article/confirm', article)

export const RejectArticle = (articleVo) => instance.post('admin/audit/article/reject', articleVo)

export const DeleteArticle = (id) => instance.delete(`admin/article/delete?id=${id}`)


export const SelectBatchComment = (currentPage = 1, pageSize = 10) => instance.get(`admin/comment/page?currentPage=${currentPage}&pageSize=${pageSize}`)

export const SearchComment = (keyword, currentPage = 1, pageSize = 10) => instance.post(`admin/comment/search?currentPage=${currentPage}&pageSize=${pageSize}`, { keyword: keyword })

export const DeleteComment = (aid, uid) => instance.delete(`admin/comment/delete/${aid}/${uid}`)

export const ConfirmComment = (comment) => instance.post('admin/audit/comment/confirm', comment)

export const RejectComment = (commentVo) => instance.post('admin/audit/comment/reject', commentVo)


export const SelectBatchReply = (currentPage = 1, pageSize = 10) => instance.get(`admin/reply/page?currentPage=${currentPage}&pageSize=${pageSize}`)

export const SearchReply = (keyword, currentPage = 1, pageSize = 10) => instance.post(`admin/reply/search?currentPage=${currentPage}&pageSize=${pageSize}`, { keyword: keyword })

export const ConfirmReply = (reply) => instance.post('admin/audit/reply/confirm', reply)

export const RejectReply = (replyVo) => instance.post('admin/audit/reply/reject', replyVo)

export const DeleteReply = (id) => instance.delete(`admin/reply/delete/${id}`)

