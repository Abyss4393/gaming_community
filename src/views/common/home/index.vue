<template>
    <div class="container">
        <div class="container-main">
            <div class="article-list-container">
                <div class="article-list-body">
                    <el-card class="aticle-item-card" shadow="hover" v-for="item in data.articleList" :key="item.id">
                        <div class="article-item-card-header">
                            <div class="article-item-header-avatar">
                                <img :src="item.posterData.avatar" alt="">
                            </div>
                            <div class="article-item-header-nickname">
                                <span>{{ item.posterData.nickname }}</span>
                            </div>
                            <div class="article-item-header-time">
                                <span> {{ item.postTime.slice(0, 10) }}</span>
                            </div>
                        </div>
                        <a :href="'/abyss/article/' + item.id" target="_blank">
                            <div class="article-item-card-title">
                                <h3>{{ item.type }}</h3>
                                <h1 :title="item.title">{{ item.title }}</h1>
                            </div>
                            <div class="article-item-card-content">
                                {{ item.content.text }}
                            </div>
                            <div class="article-item-card-preview">
                                <el-image :src="i.url" :preview-src-list="filter(item.content.imageList)" @click.stop="(e) => {
                                    if (e.preventDefault()) e.preventDefault();
                                    else e.returnValue = false;
                                }" v-for="i in item.content.imageList" />
                            </div>
                        </a>
                        <div class="article-item-card-footer">
                        </div>
                    </el-card>
                    <el-card class="load-more" v-if="data.loadingMore">
                        <el-button type="primary" :text="true" @click="loadingMore">点击加载更多</el-button>
                    </el-card>
                </div>
            </div>
        </div>
        <div class="container-sub">
            gwgeggqgqgg
        </div>
    </div>
</template>
<script setup>
import { reactive, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { ElCard, ElButton, ElImage } from 'element-plus';
import { AsyncArticleList } from '@/utils/request/common.js'

const store = useStore();
const DATA_MAX_SIZE = 2;

function changeHasMask(params) {
    store.commit("setHasMask", params);
}

function asyncChangeHasMask(secondes) {
    store.dispatch("updateHasMask", secondes)
}

const data = reactive({
    articleList: [],
    loadingMore: true
})
const filter = computed(() => {
    return (list) => {
        const tempList = [];
        for (let i of list) {
            tempList.push(i.url)
        }
        return tempList;
    }
})
onMounted(async () => {
    changeHasMask(true);
    const res = await AsyncArticleList();
    if (res.meta.code === 200) {
        data.articleList = res.data
        if (data.articleList.length > DATA_MAX_SIZE)
            data.articleList.splice(DATA_MAX_SIZE, data.articleList.length - 1);
    }
    asyncChangeHasMask(400);
})


const loadingMore = async () => {
    changeHasMask(true);
    data.loadingMore = false;
    const res = await AsyncArticleList();
    if (res.meta.code === 200) {
        data.articleList = res.data
    } else
        data.loadingMore = true;
    asyncChangeHasMask(200);
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>