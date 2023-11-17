
export const getToken = function () {
    var token = localStorage.getItem("erabbit-client-pc-store")
    return token == null ? null : JSON.parse(token).token
}

export const getUid = function () {
    var store = localStorage.getItem("erabbit-client-pc-store")
    return store == null ? null : JSON.parse(store).user.userInfo.data.id;
}