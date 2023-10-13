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
        setLoginState(state, newState) {
            state.userInfo = newState
        }
    },
    getters: {
        getAvatar(state) {
            return state.userInfo.data.avatar
        },
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