<template>
    <div id="abyss-my-post">
        <div class="header">
            我的帖子
        </div>
        <div class="list-body">
            <ul>
                <li v-for="item, index in data.list" :key="index">
                    <div class="_inner">
                        <img :src="icon" alt="time">
                        <span>{{ item.postTime }}</span>
                    </div>
                    <div class="_main">
                        <h2>{{ item.title }}</h2>
                    </div>
                    <div class="_reply">
                        回复：
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>
<script setup>
import { useRoute } from 'vue-router';
import { GetArticleListByPid } from '@/utils/request/common.js'
import { reactive } from 'vue';

const icon = require('@/assets/static/icons/time.png');
const uid = useRoute().query['author_id'];


const data = reactive({
    list: []
});

async function init() {
    const res = await GetArticleListByPid(uid);
    if (res.meta.code === 200) {
        data.list = res.data;
    }
}

init();
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>