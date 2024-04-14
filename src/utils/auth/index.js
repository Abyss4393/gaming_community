
export const getToken = function () {
    var token = localStorage.getItem("erabbit-client-pc-store")
    return token == 'undefined' ? null : JSON.parse(token).token
}

export const getUid = function () {
    let store = localStorage.getItem("erabbit-client-pc-store")
    let user = JSON.parse(store).user;
    let loginState = user.loginState;
    return loginState ? JSON.parse(store).user.userInfo.data.id : 0;
}