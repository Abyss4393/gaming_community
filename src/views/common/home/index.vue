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
                        <a :href="'/abyss/article/'+item.id" target="_blank">
                            <div class="article-item-card-title">
                                <h5>{{ item.type }}</h5>
                                <h3 :title="item.title">{{ item.title }}</h3>
                            </div>
                            <div class="article-item-card-content">
                                {{ item.content.text }}
                            </div>
                            <div class="article-item-card-preview">
                                <img :src="i.url" alt="" v-for="i in item.content.imageList">
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
import { reactive, onMounted, onUnmounted } from 'vue';
import { ElCard, ElButton } from 'element-plus';
import { AsyncArticleList } from '@/utils/request/common.js'


const data = reactive({
    articleList: [],
    loadingMore: true
})
onMounted(async () => {
    const res = await AsyncArticleList();
    if (res.meta.code === 200) {
        data.articleList = res.data
        if (data.articleList.length > 2)
            data.articleList.splice(2, data.articleList.length - 1);
    }
    console.log(data.articleList);
})

const loadingMore = async () => {
    data.loadingMore = false;
    const res = await AsyncArticleList();
    if (res.meta.code === 200) {
        data.articleList = res.data
    } else
        data.loadingMore = true;

}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>