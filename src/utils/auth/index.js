
export const getToken = function () {
    var token = localStorage?.getItem("erabbit-client-pc-store") || {}
    return JSON.parse(token)?.token || null
}

export const getUid = function () {
    var store = localStorage?.getItem("erabbit-client-pc-store") || {}
    return JSON.parse(store).user.userInfo.data.id || 0
}