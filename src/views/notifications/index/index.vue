<template>
    <div id="notifications">
        <div class="container">
            <div class="left">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            <span>消息中心</span>
                        </div>
                    </template>
                    <ul class="navigate">
                        <li v-for="menu, index in menuList" :key="index">
                            <a :href="menu.herf" @mouseenter="onMouseenter(menu)" @mouseleave="onMouseleave(menu)">
                                <img :src="choose(menu)" />
                                <span>{{ menu.title }}</span>
                            </a>
                        </li>
                    </ul>
                </el-card>
            </div>
            <div class="right">
                <el-main>
                    <router-view />
                </el-main>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { ElMain } from 'element-plus';
import { Notification } from '@element-plus/icons-vue';
import { getUid } from '@/utils/auth';

const current_id = getUid();

const menuList = ref([{
    img: require('@/assets/static/icons/system-notification.png'),
    active: require('@/assets/static/icons/system-notification-active.png'),
    title: '系统通知',
    herf: `/abyss/notifications/system?id=${current_id}`,
    hover: false,
}, {
    img: require('@/assets/static/icons/zan.png'),
    active: require('@/assets/static/icons/zan-active.png'),
    title: '收到的赞',
    herf: `/abyss/notifications/system?id=${current_id}`,
    hover: false,
}])

const onMouseenter = (item) => item.hover = true
const onMouseleave = (item) => item.hover = false
const choose = computed(() => {
    return (item) => {
        if (item.hover) return item.active;
        return item.img
    }
})
</script>

<style lang='less' scoped>
@import url('./index.less');
</style>
