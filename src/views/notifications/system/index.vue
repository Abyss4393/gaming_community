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
                        <div class="delete" @click="remove(notification.id)">
                            <el-icon>
                                <CloseBold />
                            </el-icon>
                        </div>
                        <div class="base">
                            <el-badge :value="'new'" :hidden="notification.isRead === 1" @click="read(notification.id)">
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
import { CloseBold } from '@element-plus/icons-vue';
import { DeleteManagerNotification, GetNotifications, Read } from '@/utils/request/notification';
import { getPassTime } from '@/utils/timestamp';
import { useRoute, useRouter } from 'vue-router';
import { DeleteUserNotification } from '@/utils/request/notification';

const current_id = useRoute().query.id;
const router = useRouter();
const notifications = ref([]);

const asyncUserNotification = async () => {
    const res = await GetNotifications(current_id, false,null);
    console.log(res);
    if (res.meta.code === 200) {
        notifications.value = res.data;
    }

}
const time = computed(() => {
    return (time) => {
        return getPassTime(time);
    }
})

onMounted(() => asyncUserNotification())

const read = async (id) => {
    const res = await Read(id);
    if (res.meta.code === 200) {
        router.push('/abyss');
    }
}

const remove = async (id) => {
    const res = await DeleteUserNotification(id);
    console.log(res)
}
</script>

<style lang='less' scoped>
@import url('./index.less');
</style>
