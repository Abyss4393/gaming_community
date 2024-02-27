<template>
    <div class="container">
        <div class="container-main">
            <div class="article-banner">
                <el-carousel :interval="3000" type="card" height="20rem">
                    <el-carousel-item v-for="item, index in banners" :key="index">
                        <img :src="item" alt="banner">
                    </el-carousel-item>
                </el-carousel>
            </div>
            <div class="article-list-container">
                <div class="article-list-body">
                    <el-card class="aticle-item-card" shadow="hover" v-for="item in  data.articleList " :key="item.id">
                        <div class="article-item-card-header">
                            <div class="article-item-header-avatar" @click="gocenter(item.posterId)">
                                <img :src="item.posterData.avatar" alt="poster_avatar">
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
                            <div class="article-item-card-describe" v-if="item.contentDes != null">
                                <h4>{{ item.contentDes }}</h4>
                            </div>
                            <div class="article-item-card-content">
                                <div v-if="item.content.contentList.length != 0 && item.content.contentList[0].text"
                                    v-html="item.content.contentList[0].text"></div>
                            </div>
                        </a>
                        <div class="article-item-card-preview" v-if="item.content.contentList.length != 0">
                            <div v-for="inner, innerIndex in   item.content.contentList " :key="innerIndex">
                                <el-image v-for="image, imageIndex in  inner.imageList " :key="imageIndex" :src="image.url"
                                    :preview-src-list="filter(inner.imageList)" :append-to-body="true" @click="(e) =>
                                        e.stopPropagation()" />
                                <div v-if="inner.videoList">视频...</div>
                            </div>
                        </div>
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
                    <div class="post-inner-item" @click="to(index)" v-for=" fn, index  in  post_data ">
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
import { ElCard, ElButton, ElImage, ElCarousel, ElMessage } from 'element-plus';
import { AsyncArticleList } from '@/utils/request/common.js';
import { getUid } from '@/utils/auth';
import { useStore } from 'vuex';

const instance = getCurrentInstance();
const store = useStore();
const uid = getUid();
const DATA_MAX_SIZE = 10;
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

function changeHasMask(states) {
    store.commit("setHasMask", states);
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

const to = (index) => {
    const page = index + 1;
    if (null != uid) {
        instance.proxy.$router.push(`/abyss/new_article/0/${page}`)
    } else ElMessage.info('请登录！')

}

const gocenter = (params) => {
    let centerPage = window.open('', '_blank');
    centerPage.location.href = instance.proxy.$router.resolve(`/abyss/accountCenter/postList?id=${params}`).href;
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>