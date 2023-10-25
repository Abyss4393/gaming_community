<template>
    <div id="abyss-account-center">
        <div class="account-container">
            <div class="account-header">
                <div class="avatar">
                    <img :src="userInfo.avatar" alt="">
                </div>
                <div class="account-details">
                    <div class="nickname">
                        <h2>{{ userInfo.nickname }}</h2>
                        <el-button type="primary">subescribe</el-button>
                    </div>
                    <div class="gender">
                        {{ userInfo.gender }}
                    </div>
                    <div class="mobile">
                        {{ userInfo.mobile }}
                    </div>
                    <div class="email">
                        {{ userInfo.email }}
                    </div>
                    <div class="login">
                        最近登录时间：{{ userInfo.lastLandingTime }}
                    </div>
                </div>
            </div>

            <div class="account-body">
                <el-card class="account-side">
                    <div class="menu-header">
                        个人中心
                    </div>
                    <div class="menu">
                        <ul>
                            <li class="menu-item" v-for="item, index in data.menuList" :key="index"
                                @click="loginout(index)">
                                <a :href="item.herf === '' ? null : item.herf" @mouseenter="onMouseenter(item)"
                                    @mouseleave="onMouseleave(item)">
                                    <img :src="choose(item)" alt="">
                                    <span>{{ item.text }}
                                    </span></a>
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
import { ElCard, ElButton } from 'element-plus';
import { computed, reactive } from 'vue';

const userInfo = useStore().getters['user/getUserInfo'].data;

const data = reactive({
    menuList: [{
        img: require('@/assets/static/icons/post.png'),
        active: require('@/assets/static/icons/post-active.png'),
        text: '我的帖子',
        herf: `/abyss/accountCent/postList?author_id=${userInfo.id}`,
        hover: false
    }, {
        img: require('@/assets/static/icons/reply.png'),
        active: require('@/assets/static/icons/reply-active.png'),
        text: '我的回复',
        herf: `/abyss/accountCent/replyList?author_id=${userInfo.id}`,
        hover: false
    },
    {
        img: require('@/assets/static/icons/comment.png'),
        active: require('@/assets/static/icons/comment-active.png'),
        text: '我的评论',
        herf: `/abyss/accountCent/comments?author_id=${userInfo.id}`,
        hover: false
    },
    {
        img: require('@/assets/static/icons/center-collection.png'),
        active: require('@/assets/static/icons/center-collection-active.png'),
        text: '我的收藏',
        herf: `/abyss/accountCent/collections?author_id=${userInfo.id}`,
        hover: false
    },
    {
        img: require('@/assets/static/icons/user.png'),
        active: require('@/assets/static/icons/user-active.png'),
        text: '编辑资料',
        herf: `/abyss/accountCent/edit?author_id=${userInfo.id}`,
        hover: false
    },
    {
        img: require('@/assets/static/icons/loginout.png'),
        active: require('@/assets/static/icons/loginout-active.png'),
        text: '退出登录',
        herf: '',
        hover: false
    }]
})

const onMouseenter = (item) => item.hover = true
const onMouseleave = (item) => item.hover = false
const choose = computed(() => {
    return (item) => {
        if (item.hover) return item.active
        return item.img
    }
})

const loginout = (index) => {
    if (index === data.menuList.length - 1) {

    }
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>