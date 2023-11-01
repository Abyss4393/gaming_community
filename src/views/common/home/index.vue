<template>
    <div class="container">
        <div class="container-main">
            <div class="article-banner">
                <el-carousel :interval="3000" type="card" height="20rem">
                    <el-carousel-item v-for="item, index in banners" :key="index">
                        <img :src="item" alt="">
                    </el-carousel-item>
                </el-carousel>
            </div>
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
                                <div v-html="item.content.text"></div>
                            </div>
                            <div class="article-item-card-preview" v-if="item.content.imageList">
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
            <div class="article-post">
                <div class="post-inner">
                    <div class="post-inner-item" @click="to(index)" v-for="fn, index in post_data">
                        <img :src="require('@/assets/static/icons/bottomarrow.png')" alt="">
                        <span>{{ fn.fnc }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { reactive, onMounted, computed, getCurrentInstance } from 'vue';
import { useStore } from 'vuex';
import { ElCard, ElButton, ElImage, ElCarousel } from 'element-plus';
import { AsyncArticleList } from '@/utils/request/common.js'
import { getUid } from '@/utils/auth';

const instance = getCurrentInstance();
const uid = getUid();
const store = useStore();
const DATA_MAX_SIZE = 2;
const banners = [
    require('@/assets/static/image/mv1.jpg'),
    require('@/assets/static/image/mv2.jpg'),
    require('@/assets/static/image/mv3.jpg')
]

const post_data = [{
    fnc: '发布帖子',
}, {
    fnc: '发布图片',
},
{
    fnc: '发布视频'
}]

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
    if (data.articleList != null) {
        changeHasMask(true);
        const res = await AsyncArticleList();
        if (res.meta.code === 200) {
            data.articleList = res.data
            if (data.articleList.length > DATA_MAX_SIZE)
                data.articleList.splice(DATA_MAX_SIZE, data.articleList.length - 1);
        }
        asyncChangeHasMask(400);
    }
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

const to = (index) => {
    const page = index + 1;
    instance.proxy.$router.push(`/abyss/new_article/0/${page}?author_id=${uid}`)
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>