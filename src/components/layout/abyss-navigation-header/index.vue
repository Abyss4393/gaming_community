<template>
    <div id="abyss-header">
        <div v-if="instance.proxy.$route.path != rootRoutePath && canGoBack" class="previous">
            <img :src="previous" @click="previousPage" alt="">
        </div>
        <div class="navbar">
            <ul>
                <li v-for="item, index in link" :key="index">
                    <a :href="item.href">
                        <p>{{ item.title }}</p>
                    </a>
                </li>
            </ul>
        </div>
        <abyss-research class="search" @val="handler" />
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
import { ref, getCurrentInstance, watch, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import abyssResearch from '../../func/abyss-research/index';
import { getUid } from '@/utils/auth';
import { ElMessage } from 'element-plus';

const routerInstance = useRouter();
const canGoBack = ref(false);
const uid = getUid();
const store = useStore();
const instance = getCurrentInstance();


const rootRoutePath = '/abyss/';
const previous = require('@/assets/static/icons/previous.png')
const url = store.getters["user/getAvatar"]

console.log(window.history.state.back != null);

onMounted(() => canGoBack.value = window.history.state.back != null)

const link = [{
    title: "首页",
    href: '/abyss/'
}, {
    title: "攻略",
    href: '/abyss/'
}, {
    title: "官方",
    href: '/abyss/'
}, {
    title: "观测区",
    href: '/abyss/'
}, {
    title: "更多",
    href: '/abyss/'
}];
const router = [{
    url: require('@/assets/static/icons/center.png'),
    title: "个人中心",
    path: '/abyss/accountCenter'
}, {
    url: require('@/assets/static/icons/more.png'),
    title: "更多",
    path: '/more'
}];


const transfor = (path) => {
    if (uid != null) instance.proxy.$router.push(path);
    else ElMessage.info('请登录');
}

const previousPage = () => instance.proxy.$router.back();

const handler = (arg) => {
    console.log(arg);
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>