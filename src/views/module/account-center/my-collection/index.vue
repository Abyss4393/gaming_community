<template>
    <div id="abyss-my-collection">
        <div class="header">
            我的收藏
        </div>
        <div class="list-body" v-if="data.list.length !== 0">
            <ul>
                <li v-for="item, index in data.list" :key="index">
                    <a :href="`/abyss/article/${item.id}`" target="_blank">
                        <div class="_inner">
                            <img :src="icon" alt="time">
                            <span>发布时间 {{ item.postTime.slice(0, 10) }}</span>
                        </div>
                        <div class="_main">
                            <h2>{{ item.title }}</h2>
                        </div>
                    </a>
                </li>
            </ul>
        </div>
        <div v-else>
            <el-empty description="你还没有任何收藏的帖子，再去逛逛吧！" />
        </div>
    </div>
</template>
<script setup>
import { useRoute } from 'vue-router';
import { onMounted, reactive } from 'vue';
import { ElEmpty } from 'element-plus';
import { AsyncUserCollections } from '@/utils/request/common.js'

const uid = useRoute().query.id;
const icon = require('@/assets/static/icons/time.png');

const data = reactive({
    list: []
});

onMounted(async function init() {
    const res = await AsyncUserCollections(uid);
    if (res.meta.code === 200) {
        if (res.data != null)
            data.list = res.data;
    }
})
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>