<template>
    <div id="abyss-my-reply">
        <div class="header">
            我的回复
        </div>
        <div class="list-body" v-if="data.list.length !== 0">
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
        <div v-else>
            <el-empty description="你还没有任何回复" />
        </div>
    </div>
</template>
<script setup>
import { useRoute } from 'vue-router';
import { reactive } from 'vue';
import { ElEmpty } from 'element-plus'

const icon = require('@/assets/static/icons/time.png');
const uid = useRoute().query['author_id'];


const data = reactive({
    list: []
});


</script>
<style lang="less" scoped>
@import url('./index.less');
</style>