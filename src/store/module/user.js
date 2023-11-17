export default {
    namespaced: true,
    state() {
        return {
            loginState: false,
            userInfo: {
                data: {
                    avatar: require('@/assets/static/resource/default.png'),
                    nickname: ''
                }
            }
        }
    },
    mutations: {
        setAvatar(state, url) {
            state.userInfo.data.avator = url
        },
        setName(state, nickname) {
            state.userInfo.nickname = nickname
        },
        setUserInfo(state, obj) {
            state.userInfo = obj
        },
        setUserInfoData(state, obj) {
            state.userInfo.data = obj
        },
        setLoginState(state, newState) {
            state.userInfo = newState
        },
        resetUserInfo(state) {
            state.userInfo = {
                data: {
                    avatar: require('@/assets/static/resource/default.png'),
                    nickname: ''
                }
            }
        },

    },
    getters: {
        getAvatar(state) {
            return state.userInfo.data.avatar
        },
        getNickname(state) {
            return state.userInfo.data.nickname
        }
        ,
        getUserInfo(state) {
            return state.userInfo
        },
        getloginState(stete) {
            return stete.loginState
        }
    },
    actons: {
        asyncUpdateUserInfo(store, { payload, seconds = 1000 }) {
            setTimeout(() => {
                store.commit("setUserInfo", payload)
            }, seconds)
        }
    }

}