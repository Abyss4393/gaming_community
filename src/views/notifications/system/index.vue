<template>
    <div id="system-notification">
        <el-card>
            <template #header>
                <div class="header">
                    <span>系统通知</span>
                </div>
            </template>
            <ul>
                <li v-for="notification, index in notifications" :key="index">
                    <div class="item">
                        <div class="base">
                            <el-badge :value="'new'" :hidden="notification.isRead === 1">
                                <img :src="require('@/assets/static/icons/notification.png')" alt="">
                            </el-badge>
                            <div class="time">{{ time(notification.operateTime) }}</div>
                            <span>{{ notification.info }}</span>
                        </div>
                        <el-divider />
                    </div>
                </li>
            </ul>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElCard, ElDivider, ElBadge } from 'element-plus';
import { getNotifications } from '@/utils/request/notification';
import { useRoute } from 'vue-router';
import { getPassTime } from '@/utils/timestamp';

const current_id = useRoute().query.id;
const notifications = ref([]);

const AsyncUserNotification = async () => {
    const res = await getNotifications(current_id);
    if (res.meta.code === 200) {
        notifications.value = res.data;
    }

}
const time = computed(() => {
    return (time) => {
        return getPassTime(time);
    }
})

onMounted(() => AsyncUserNotification())
</script>

<style lang='less' scoped>
@import url('./index.less');
</style>
