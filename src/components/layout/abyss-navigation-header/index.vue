<template>
    <div id="abyss-header">
        <div v-if="instance.proxy.$route.path != rootRoutePath && canGoBack" class="previous">
            <img :src="previous" @click="previousPage" alt="previous">
        </div>
        <div class="navbar">
            <ul>
                <li v-for="item, index in link" :key="index" :class="index === 3 ? 'nav-animation' : ''">
                    <a :href="item.href">
                        <p>{{ item.title }}</p>
                    </a>
                </li>
            </ul>
        </div>
        <div class="search">
            <el-autocomplete v-model="data.query" :fetch-suggestions="querySearchAsync" placeholder="请输入内容"
                @select.enter.native="search">
                <template #default="{ item }">
                    <div class="history">{{ item }}</div>
                </template>
            </el-autocomplete>
            <img @click="search" :src="require('@/assets/static/icons/search.png')">
        </div>

        <div class="notification">
            <el-badge :value="notifications" :hidden="0 === notifications" :max="99" @click="toNotification">
                <p>通知</p>
            </el-badge>
        </div>
        <div class="avatar">
            <el-tooltip effect="light" popper-class="popper" placement="bottom">
                <img :src="url" alt="默认头像">

                <template #content>
                    <ul>
                        <li v-for="item, index in router">
                            <img :src="item.url" alt="">
                            <span @click="transfor(item.path, index)">{{ item.title }}</span>
                        </li>
                    </ul>
                </template>
            </el-tooltip>
        </div>
        <div class="login">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/login' }">
                    <span class="login-item">登录</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item class="login-item">
                    <a href="/register">
                        <span>注册</span>
                    </a>
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
    </div>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted, reactive } from 'vue';
import { useStore } from 'vuex';
import { ElAutocomplete, ElMessageBox, ElMessage } from 'element-plus';
import { getUid } from '@/utils/auth';
import { AsyncQueryHistory } from '@/utils/request/common.js';
import { GetNotifications } from '@/utils/request/notification.js';

const canGoBack = ref(false);
const uid = getUid();
const loginState = useStore().getters["user/getLoginState"];
const store = useStore();
const instance = getCurrentInstance();
const notifications = ref(0);

const data = reactive({
    query: '',
})

const rootRoutePath = '/abyss/';
const previous = require('@/assets/static/icons/previous.png')
const url = store.getters["user/getAvatar"]


onMounted(async () => {
    if (loginState) {
        canGoBack.value = window.history.state.back != null;
        const notifi = await GetNotifications(uid, true);
        notifications.value = notifi.data.length;
    }
})

const link = [{
    title: "首页",
    href: '/abyss/'
},
{
    title: "攻略",
    href: '/abyss/strategy'
},
{
    title: "O.o",
    href: '/abyss/o.O'
},
{
    title: "Chat",
    href: '/chat'
},
];

const router = [{
    url: require('@/assets/static/icons/center.png'),
    title: "个人中心",
    path: `/abyss/accountCenter/postList?id=${uid}`
}, {
    url: require('@/assets/static/icons/exit.png'),
    title: "退出登录",
    path: '/abyss/'
}];

const transfor = (path, index) => {
    if (uid != 0) {
        if (index === 1) {
            ElMessageBox.confirm(
                '确定要退出当前账号',
                '提示',
                {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            )
                .then(() => {
                    store.commit('user/resetUserInfo');
                    store.commit("user/setLoginState", false);
                    window.location.href = instance.proxy.$router.resolve(path).href;
                    ElMessage.success('已退出');

                })
                .catch(error => error)
            return;
        }
        let newWindow = window.open('', '_blank');
        newWindow.location.href = instance.proxy.$router.resolve(path).href;
    }
    else ElMessage.info('请登录');
}

const querySearchAsync = async (queryString, cb) => {
    function dely(array) {
        setTimeout(() => cb(array), 1000)
    }
    if (!queryString) {
        const history = await AsyncQueryHistory();
        dely(history.data)
    } else {
        dely(['recommand'])
    }

}

const search = () => {
    console.log(data.query);
}

const previousPage = () => instance.proxy.$router.back();

const toNotification = () => {
    if (0 == uid) {
        ElMessage.info('请先登录');
        return;
    }
    instance.proxy.$router.push(`/abyss/notifications/system?id=${uid}`);
}

</script>

<style lang='less' scoped>
@import url('./index.less');
</style>