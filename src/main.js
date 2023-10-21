import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'
import '@/assets/global/global.css'


const app = createApp(App)
app.use(store).use(router).use(ElementPlus).mount('#app')
axios.get('/config.json').then(res => {
    app.config.globalProperties.$config = res.data
})
export const global = app.config.globalProperties

