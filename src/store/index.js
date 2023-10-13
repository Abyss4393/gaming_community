import { createStore } from 'vuex'
import createPersistedstate from 'vuex-persistedstate'
import user from './module/user'
export default createStore({
  state: {
    hasMask: false,
    token: null
  },
  getters: {
    getHasMask(state) {
      return state.hasMask;
    },
    getToken(state) {
      return state.token
    }
  },
  mutations: {
    setHasMask(state, newval) {
      state.hasMask = newval
    },
    setToken(state, token) {
      state.token = token
    }
  },
  actions: {
    updateHasMask(store, seconds) {
      setTimeout(() => {
        store.commit("setHasMask", !store.state.hasMask);
      }, seconds)
    },
    updateToken(store, { token, seconds = 1000 }) {
      setTimeout(() => {
        store.commit("setToken", token);
      }, seconds)
    }
  },
  modules: {
    user
  },
  plugins: [
    createPersistedstate({
      key: "erabbit-client-pc-store",
      path: ["user"]
    })
  ]
})
