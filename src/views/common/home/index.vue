<template>
    <div class="container">
        <div class="container-main">
            <div class="article-banner">
                <el-carousel :interval="3000" type="card" height="20rem">
                    <el-carousel-item v-for="item, index in banners" :key="index">
                        <div class="hot">
                            <img :src="require('@/assets/static/icons/hot.gif')" alt="">
                        </div>
                        <img :src="item" alt="banner">
                    </el-carousel-item>
                </el-carousel>
            </div>
            <div class="article-list-container">
                <div class="article-list-body">
                    <div v-for="item, index in  data.articleList" :key="item.id">
                        <el-card v-if="item.approved === 1" class="aticle-item-card" shadow="hover">
                            <div class="article-item-card-header">
                                <div class="article-item-header-avatar" @click="gocenter(item.posterId)">
                                    <img :src="item.posterData.avatar" alt="poster_avatar">
                                </div>
                                <div class="article-item-header-nickname">
                                    <span>{{ item.posterData.nickname }}</span>
                                </div>
                                <div class="article-item-header-time">
                                    <span> {{ time(item.postTime) }}</span>
                                </div>
                                <div class="follow">
                                    <el-button v-if="!item.isFollow && uid != item.posterId" type="primary" round
                                        @click="follow(item.posterId)">
                                        <span>关注</span>
                                    </el-button>
                                </div>
                            </div>
                            <a :href="`/abyss/article/${item.id}?poster_id=${item.posterId}`" target="_blank">
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
                                    <el-image v-for="image, imageIndex in  inner.imageList " :key="imageIndex"
                                        :src="image.url" :preview-src-list="filter(inner.imageList)"
                                        :append-to-body="true" @click="(e) => e.stopPropagation()" />
                                    <div v-if="inner.videoList">视频...</div>
                                </div>
                            </div>
                            <div class="article-item-card-footer">
                                <div>
                                    <img :src="item.isUpvote ? require('@/assets/static/icons/zan-active.png') : require('@/assets/static/icons/zan.png')"
                                        alt="" @click="activeLike(index)">
                                    <span>{{ item.articleLike }}</span>
                                </div>
                                <div>
                                    <img :src="item.isDislike ? require('@/assets/static/icons/zancopy-active.png') : require('@/assets/static/icons/zancopy.png')"
                                        alt="" @click="activeDislike(index)">
                                    <span>{{ item.articleDislike }}</span>
                                </div>
                            </div>

                        </el-card>
                    </div>
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
            <div class="recommend">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            <span>推荐帖子</span>
                            <el-icon>
                                <Aim />
                            </el-icon>
                        </div>
                    </template>
                    <div class="recommend-list">
                        <div class="recommend-item" v-for="item, index in recommends" :key="index"
                            @click="transforRecommend(item.article.id)">
                            <div class="rank">
                                <img :src="require('@/assets/static/icons/rank.png')" alt="">
                                <span>TOP:{{ index + 1 }}</span>
                            </div>
                            <div class="recommend-article">
                                <div class="avatar">
                                    <img :src="item.avatar" alt="">
                                </div>
                                <div class="type">
                                    <span>
                                        <el-tag>{{ item.article.type }}</el-tag>
                                    </span>
                                </div>
                                <div class="title">
                                    <span>{{ item.article.title }}</span>
                                </div>

                            </div>
                        </div>
                        <el-divider content-position="left">END</el-divider>
                    </div>
                </el-card>
            </div>
        </div>
    </div>
</template>
<script setup>
import { reactive, onMounted, computed, getCurrentInstance, ref } from 'vue';
import { ElCard, ElButton, ElImage, ElCarousel, ElMessage, ElIcon } from 'element-plus';
import { Aim } from '@element-plus/icons-vue'
import {
    AsyncArticleList, AsyncUserFriendStatus, AsyncArticleUpvoteStatus, AsyncArticleDislikeStatus,
    AsyncArticleUpvote, AsyncArticleDislike, CancelUpvote, CancelDislike, AddFriend
} from '@/utils/request/common.js';
import { RecommendForUser } from '@/utils/request/recommend';
import { getUid } from '@/utils/auth';
import { useStore } from 'vuex';
import { getPassTime } from '@/utils/timestamp/index';

const instance = getCurrentInstance();
const store = useStore();
const uid = getUid();
const loginState = useStore().getters['user/getLoginState'];
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


const recommends = ref([]);

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
const time = computed(() => {
    return (time) => {
        return getPassTime(time);
    }
})

const asyncArticles = async () => {
    changeHasMask(true);
    const res = await AsyncArticleList();
    if (res.meta.code === 200) {
        data.articleList = res.data
        if (data.articleList.length > DATA_MAX_SIZE)
            data.articleList.splice(DATA_MAX_SIZE, data.articleList.length - 1);
    }

    const recommend = await RecommendForUser(uid);
    if (recommend.meta.code === 200) {
        recommends.value = recommend.data
    }
    if (loginState) {
        data.articleList.forEach(async (item) => {
            const isFollow = await AsyncUserFriendStatus(uid, item.posterId);
            const upvoteStatus = await AsyncArticleUpvoteStatus(uid, item.id);
            const dislikeStatus = await AsyncArticleDislikeStatus(uid, item.id);
            if (isFollow.meta.code === 200)
                item.isFollow = isFollow.data.isFriend;
            else item.isFollow = false;
            if (upvoteStatus.meta.code === 200)
                item.isUpvote = upvoteStatus.data.isUpvote;
            else item.isUpvote = false;
            if (dislikeStatus.meta.code === 200)
                item.isDislike = dislikeStatus.data.isDislike;
            else item.isDislike = false;
        })
    }
    asyncChangeHasMask(400);
}
onMounted(() => asyncArticles())


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

// 点赞
const activeLike = async function (index) {
    if (data.articleList[index].isUpvote) {
        if (data.articleList[index].articleLike !== 0) {
            data.articleList[index].articleLike -= 1;
        }

    } else data.articleList[index].articleLike += 1;
    if (data.articleList[index].isUpvote) {
        const cancel = await CancelUpvote(uid, data.articleList[index].id)
        if (cancel.meta.code === 200)
            ElMessage.success('取消点赞成功')
        else ElMessage.info('请勿重复操作')
    } else {
        const upvoted = await AsyncArticleUpvote(uid, data.articleList[index].id)
        if (upvoted.meta.code === 241)
            ElMessage.success(upvoted.meta.msg)
        else ElMessage.info('已经点赞过，请勿重复操作')
    }
    data.articleList[index].isUpvote = !data.articleList[index].isUpvote;

}
// 拉踩
const activeDislike = async function (index) {
    if (data.articleList[index].isDislike) {
        if (data.articleList[index].articleDislike !== 0) {
            data.articleList[index].articleDislike -= 1;
        }

    } else data.articleList[index].articleDislike += 1;
    if (data.articleList[index].isDislike) {
        const cancel = await CancelDislike(uid, data.articleList[index].id)
        if (cancel.meta.code === 200)
            ElMessage.success('取消拉踩成功')
        else ElMessage.info('请勿重复操作')
    } else {
        const disliked = await AsyncArticleDislike(uid, data.articleList[index].id)
        if (disliked.meta.code === 243)
            ElMessage.success(disliked.meta.msg)
        else ElMessage.info('已经拉踩过，请勿重复操作')
    }
    data.articleList[index].isDislike = !data.articleList[index].isDislike;
}

// 关注
const follow = async (id) => {
    const res = await AddFriend(uid, id)
    if (res.meta.code === 271) {
        asyncArticles();
        ElMessage.success(res.meta.msg)
    }
    else ElMessage.error(res.meta.msg)
}

// 前往推荐
const transforRecommend = (recommendId) => {
    let newWindow = window.open('', '_blank');
    let path = `/abyss/article/${recommendId}?id=${uid}`
    newWindow.location.href = instance.proxy.$router.resolve(path).href;

}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>