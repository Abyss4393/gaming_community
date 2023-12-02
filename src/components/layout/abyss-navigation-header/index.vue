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
        <div class="avator">
            <el-tooltip effect="light" popper-class="popper" placement="bottom">
                <img :src="url" alt="默认头像">
                <template #content>
                    <ul>
                        <li v-for="item in router">
                            <img :src="item.url" alt="">
                            <span @click="transfor(item.path)">{{ item.title }}</span>
                        </li>
                    </ul>
                </template>
            </el-tooltip>
        </div>
    </div>
</template>
<script setup>
import { ref, getCurrentInstance, onMounted, reactive } from 'vue';
import { useStore } from 'vuex';
import { ElAutocomplete } from 'element-plus';
import { getUid } from '@/utils/auth';
import { ElMessage } from 'element-plus';
import { AsyncQueryHistory } from '@/utils/request/common.js';

const canGoBack = ref(false);
const uid = getUid();
const store = useStore();
const instance = getCurrentInstance();

const data = reactive({
    query: '',
})

const rootRoutePath = '/abyss/';
const previous = require('@/assets/static/icons/previous.png')
const url = store.getters["user/getAvatar"]


onMounted(() => canGoBack.value = window.history.state.back != null)

const link = [{
    title: "首页",
    href: '/abyss/'
},
{
    title: "攻略",
    href: '/abyss/'
},
{
    title: "官方",
    href: '/abyss/'
},
{
    title: "Chat",
    href: '/chat'
},
{
    title: "观测区",
    href: '/abyss/'
},
{
    title: "更多",
    href: '/abyss/'
}];

const router = [{
    url: require('@/assets/static/icons/center.png'),
    title: "个人中心",
    path: `/abyss/accountCenter/postList?id=${uid}`
}, {
    url: require('@/assets/static/icons/more.png'),
    title: "更多",
    path: '/more'
}];

const transfor = (path) => {
    if (uid != null) instance.proxy.$router.push(path);
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


</script>
<style lang="less" scoped>
@import url('./index.less');
</style>