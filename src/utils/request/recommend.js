import instance from "./index";

export const RecommendForUser = (id) => instance.get(`recommend/for/user?id=${id}`)
export const GetRecommends = () => instance.get('recommend/list')
export const SetRecommend = (params) => instance.post('recommend/set',params)
export const CancelRecommend = (id) => instance.delete(`recommend/cancel/${id}`)