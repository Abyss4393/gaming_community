<template>
    <div id="abyss-account-center">
        <div class="account-container">
            <div class="account-header">
                <div class="avatar">
                    <img :src="data.rederUserInfo.avatar" alt="avatar">
                </div>
                <div class="account-details">
                    <div class="nickname">
                        <h2>{{ data.rederUserInfo.nickname }}</h2>
                        <el-button v-if="current_id == userInfo.id" type="primary" @click="edit()">编辑</el-button>
                        <el-button v-else type="primary" @click="follow()">
                            {{ data.isAttented ? '√已关注' : '关注' }}
                        </el-button>
                    </div>
                    <div class="gender">
                        {{ data.rederUserInfo.gender }}
                    </div>
                    <div class="mobile">
                        {{ data.rederUserInfo.mobile }}
                    </div>
                    <div class="email">
                        {{ data.rederUserInfo.email }}
                    </div>
                    <div class="login">
                        最近登录时间：{{ data.rederUserInfo.lastLandingTime }}
                    </div>
                </div>
            </div>

            <div class="account-body">
                <el-card class="account-side">
                    <div class="menu-header">
                        个人中心
                    </div>
                    <div class="menu">
                        <ul v-if="current_id == userInfo.id">
                            <li class="menu-item" v-for="item, index in data.menuList" :key="index" @click="active(index)">
                                <a :href="item.herf === '' ? null : item.herf" @mouseenter="onMouseenter(item)"
                                    @mouseleave="onMouseleave(item)">
                                    <img :src="choose(item)" alt="">
                                    <span>{{ item.text }}</span>
                                </a>
                            </li>
                        </ul>
                        <ul v-else>
                            <li class="menu-item" v-for="currnetItem, currnetIndex in data.currentMenuList"
                                :key="currnetIndex">
                                <a :href="currnetItem.herf === '' ? null : currnetItem.herf"
                                    @mouseenter="onMouseenter(currnetItem)" @mouseleave="onMouseleave(currnetItem)">
                                    <img :src="choose(currnetItem)" alt="">
                                    <span>{{ currnetItem.text }}</span>
                                </a>
                            </li>
                        </ul>
                    </div>
                </el-card>
                <el-card class="account-main">
                    <router-view />
                </el-card>
            </div>
        </div>
    </div>
</template>
<script setup>
import { useStore } from 'vuex';
import { ElCard, ElButton, ElMessage } from 'element-plus';
import { computed, reactive, getCurrentInstance, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { UserInfo, AddFriend } from "@/utils/request/common.js";

const store = useStore();
const instance = getCurrentInstance();
const current_id = useRoute().query.id;
const userInfo = useStore().getters['user/getUserInfo'].data;


const data = reactive({
    menuList: [{
        img: require('@/assets/static/icons/post.png'),
        active: require('@/assets/static/icons/post-active.png'),
        text: '我的帖子',
        herf: `/abyss/accountCenter/postList?id=${userInfo.id}`,
        hover: false,

    }, {
        img: require('@/assets/static/icons/reply.png'),
        active: require('@/assets/static/icons/reply-active.png'),
        text: '我的回复',
        herf: `/abyss/accountCenter/replyList?id=${userInfo.id}`,
        hover: false,

    },
    {
        img: require('@/assets/static/icons/comment.png'),
        active: require('@/assets/static/icons/comment-active.png'),
        text: '我的评论',
        herf: `/abyss/accountCenter/comments?id=${userInfo.id}`,
        hover: false,

    },
    {
        img: require('@/assets/static/icons/center-collection.png'),
        active: require('@/assets/static/icons/center-collection-active.png'),
        text: '我的收藏',
        herf: `/abyss/accountCenter/collections?id=${userInfo.id}`,
        hover: false,

    },
    {
        img: require('@/assets/static/icons/user.png'),
        active: require('@/assets/static/icons/user-active.png'),
        text: '编辑资料',
        herf: `/abyss/accountCenter/edit?id=${userInfo.id}`,
        hover: false,

    },
    {
        img: require('@/assets/static/icons/loginout.png'),
        active: require('@/assets/static/icons/loginout-active.png'),
        text: '退出登录',
        herf: '',
        hover: false,
    }],
    currentMenuList: [
        {
            img: require('@/assets/static/icons/post.png'),
            active: require('@/assets/static/icons/post-active.png'),
            text: '帖子',
            herf: `/abyss/accountCenter/postList?id=${current_id}`,
            hover: false,

        }, {
            img: require('@/assets/static/icons/reply.png'),
            active: require('@/assets/static/icons/reply-active.png'),
            text: '回复',
            herf: `/abyss/accountCenter/replyList?id=${current_id}`,
            hover: false,
        }, {
            img: require('@/assets/static/icons/comment.png'),
            active: require('@/assets/static/icons/comment-active.png'),
            text: '评论',
            herf: `/abyss/accountCenter/comments?id=${current_id}`,
            hover: false,
        },
        {
            img: require('@/assets/static/icons/center-collection.png'),
            active: require('@/assets/static/icons/center-collection-active.png'),
            text: '收藏',
            herf: `/abyss/accountCenter/collections?id=${current_id}`,
            hover: false,
        }, {
            img: require('@/assets/static/icons/attention.png'),
            active: require('@/assets/static/icons/attention-active.png'),
            text: '关注',
            herf: '',
            hover: false,
        },],
    rederUserInfo: {},
    isAttented: false
})

onMounted(async function init() {
    if (current_id == userInfo.id) {
        data.rederUserInfo = userInfo;
        return
    }
    const res = await UserInfo(current_id)
    if (res.meta.code === 200)
        data.rederUserInfo = res.data
})

const onMouseenter = (item) => item.hover = true
const onMouseleave = (item) => item.hover = false
const choose = computed(() => {
    return (item) => {
        if (item.hover) return item.active;
        return item.img
    }
})

const follow = async () => {
    const res = await AddFriend(userInfo.id, current_id)
    console.log(res);
    if (res.meta.code === 271)
        ElMessage.success(res.meta.msg)
    else ElMessage.error(res.meta.msg)
}

const edit = () => instance.proxy.$router.push(`/abyss/accountCenter/edit?id=${userInfo.id}`)

const active = (index) => {
    data.menuList[index].focus = true;
    if (index === data.menuList.length - 1) {
        store.commit('user/resetUserInfo');
        location.reload();
        instance.proxy.$router.replace('/abyss/');
    }
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>