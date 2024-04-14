import instance from "./index";

export const GetNotifications = (id, unread = false, type = null) => instance.get(`sys/notification/user/${id}?unread=${unread}&type=${type}`)

export const Read = (id) => instance.get(`sys/notification/read/${id}`)

export const DeleteUserNotification = (id) => instance.delete(`sys/notification/delete/${id}`)

export const GetManageNotifications = (id) => instance.get(`sys/notification/manager`)


export const Cope = (id) => instance.get(`sys/notification/cope/${id}`)

export const DeleteManagerNotification = (id) => instance.delete(`sys/notification/manager/delete/${id}`)