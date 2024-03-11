import instance from "./index";

export const getNotifications = (id, unread = false, type = null) => instance.get(`sys/notification/user/${id}?unread=${unread}&type=${type}`)

export const getManageNotifications = (id) => instance.get(`sys/notification/${id}`)