<template>
    <div id="abyss-article">
        <div class="container">
            <el-card class="container-main">
                dawd
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
            <div class="article-ations-item" v-for="item in data.ationIcons">
                <img :src="item" alt="">
                <span :title="item" @click="handler(item, index)">0</span>
            </div>
        </div>
    </div>
</template>
<script setup>
import { reactive, onMounted, computed, getCurrentInstance } from 'vue';
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
})



const handler = (data, index) => {

}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>