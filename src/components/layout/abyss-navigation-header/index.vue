<template>
    <div id="abyss-header">
        <div class="navbar">
            <ul>
                <li v-for="item, index in link" :key="index">
                    <a href="">
                        <p>{{ item }}</p>
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
                            <span @click="instance.proxy.$router.push(item.path)">{{ item.title }}</span>
                        </li>
                    </ul>
                </template>
            </el-tooltip>
        </div>
    </div>
</template>
<script setup>
import { getCurrentInstance } from 'vue';
import { useStore } from 'vuex';
import abyssResearch from '../../func/abyss-research/index'
const store = useStore();
const instance = getCurrentInstance();
const link = ["首页", "攻略", "官方", "观测区", "更多"];
const router = [{
    url: require('@/assets/static/icons/center.png'),
    title: "个人中心",
    path: '/abyss/accountCenter'
}, {
    url: require('@/assets/static/icons/more.png'),
    title: "更多",
    path: '/more'
}];
const url = store.getters["user/getAvatar"]
const handler = (arg) => {
    console.log(arg);
}
</script>
<style lang="less" scoped>@import url('./index.less');</style>