import instance from "./index";

export const SelectBatchUser = (currentPage = 1, pageSize = 10) => instance.get(`admin/user/page?currentPage=${currentPage}&pageSize=${pageSize}`)

export const SearchUser = (keyword, currentPage = 1, pageSize = 10) => instance.post(`admin/user/search?currentPage=${currentPage}&pageSize=${pageSize}`, { keyword: keyword })

export const ModifyUser = (user) => instance.post('admin/user/modify', user)

export const DeleteUser = (id) => instance.delete(`admin/user/delete?id=${id}`)

export const SelectBatchArticle = (currentPage = 1, pageSize = 10) => instance.get(`admin/article/page?currentPage=${currentPage}&pageSize=${pageSize}`)