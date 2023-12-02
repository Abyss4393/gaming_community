<template>
    <div id="abyss-my-post">
        <div class="header">
            我的帖子
        </div>
        <div class="list-body" v-if="data.list.length !== 0">
            <ul>
                <li v-for="item, index in data.list" :key="index">
                    <a :href="`/abyss/article/${item.id}`" target="_blank">
                        <div class="_inner">
                            <img :src="icon" alt="time">
                            <span>{{ item.postTime }}</span>
                        </div>
                        <div class="_main">
                            <h2>{{ item.title }}</h2>
                        </div>
                    </a>
                    <div class="_reply">
                        回复：
                    </div>
                </li>
            </ul>
        </div>
        <div v-else>
            <el-empty description='你还没有发布帖子' />
        </div>
    </div>
</template>
<script setup>
import { onMounted, reactive } from 'vue';
import { useRoute } from 'vue-router';
import { GetArticleListByPid } from '@/utils/request/common.js'
import { ElEmpty } from 'element-plus';

const icon = require('@/assets/static/icons/time.png');
const uid = useRoute().query.id;


const data = reactive({
    list: []
});


onMounted(async function init() {
    const res = await GetArticleListByPid(uid);
    if (res.meta.code === 200) {
        data.list = res.data;
    }
})
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>