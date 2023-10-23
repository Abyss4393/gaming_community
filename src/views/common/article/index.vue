<template>
    <div id="abyss-article">
        <div class="container">
            <el-card class="container-main">
                <div class="title">
                    <h2>「{{ data.articleData.title }}」</h2>
                </div>
                <div class="page-info">
                    <span>来源：{{ data.articleData.type }}</span>
                    <span class="passive">
                        <img :src="require('@/assets/static/icons/message.png')" alt="">
                        {{ data.articleData.passivenessCount }}</span>
                    <span class="positive">
                        <img :src="require('@/assets/static/icons/zan.png')" alt="">
                        {{ data.articleData.positivenessCount }}</span>
                    <span class="collect">
                        <img :src="require('@/assets/static/icons/collection.png')" alt="">
                        {{ data.articleData.collectCount }}</span>
                </div>
                <div class="post-timestamp">
                    <span>帖子发布时间</span>
                    <span>{{ data.articleData.postTime }}</span>
                </div>
                <div class="content">

                </div>
                <div class="footer">
                    <div class="left">
                        <img :src="require('@/assets/static/icons/zan.png')" alt="">
                        <span>{{ data.articleData.passivenessCount  }}</span>
                    </div>
                    <div class="right">
                        <img :src="require('@/assets/static/icons/collection.png')" alt="">
                        <span>{{ data.articleData.positivenessCount  }}</span>
                    </div>
                </div>

                <div class="reply">
                    
                </div>
            </el-card>
            <div class="container-sub">
                <el-card class="article-page-author">
                    <img :src="data.posterData.avatar" alt="">
                    <div class="article-author-info">
                        <h4>{{ data.posterData.nickname }}</h4>
                    </div>
                </el-card>
            </div>
        </div>
        <div class="article-actions">
            <div class="article-ations-item" v-for="item, index in data.ationIcons" @click="handler(index)">
                <img :src="item" alt="">
                <span :title="item">0</span>
            </div>
        </div>
    </div>
</template>
<script setup>
import { reactive, onMounted, getCurrentInstance } from 'vue';
import { ElCard } from 'element-plus';
import { AsyncArticleById } from '@/utils/request/common.js';

const instance = getCurrentInstance();

const data = reactive({
    articleId: null,
    articleData: {},
    posterData: {
        avatar: '',
        nickname: '',
    },
    ationIcons: [
        require('@/assets/static/icons/message.png'),
        require('@/assets/static/icons/collection.png'),
        require('@/assets/static/icons/zan.png'),
    ]
})

onMounted(async function init() {
    data.articleId = instance.proxy.$route.params.id;
    const res = await AsyncArticleById(data.articleId);
    if (res.meta.code === 200) {
        data.articleData = res.data;
        data.posterData.avatar = res.data.posterData.avatar;
        data.posterData.nickname = res.data.posterData.nickname;
    }
    console.log(data.articleData);
})

const handler = (index) => {
    console.log(index);
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>