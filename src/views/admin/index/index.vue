<template>
    <div id="admin-index">
        <el-container>
            <el-header>
                <div class="header">
                    <el-breadcrumb :separator-icon="ArrowRight">
                        <el-breadcrumb-item>
                            <el-badge :value="unread"><el-button @click="show = !show">待处理事项</el-button>
                            </el-badge>
                        </el-breadcrumb-item>
                        <el-breadcrumb-item><el-button>首页</el-button></el-breadcrumb-item>
                    </el-breadcrumb>
                    <div class="notification-box" v-show="show">

                        <el-card v-for="notification, index in notificationList" :key="index">
                            <div class="notification-item">
                                <div class="delete" @click="remove(notification.id)">
                                    <el-icon>
                                        <CloseBold />
                                    </el-icon>
                                </div>
                                <el-tag type="info">事件：{{ notification.type }}</el-tag>
                                <el-alert v-if="0 === notification.isRead" title="待处理事件" type="warning" />
                                <span>--{{ notification.info }}--</span>
                                <el-button v-if="0 === notification.isRead" plain type="primary"
                                    @click="cope(notification.id, notification.type)">去处理</el-button>
                                <el-button v-else plain type="success" disabled>
                                    <el-icon><Select />已处理</el-icon>
                                </el-button>
                            </div>

                        </el-card>
                    </div>
                </div>
            </el-header>
            <el-main>
                <div ref="chartsRef" id="charts" />
            </el-main>
        </el-container>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { ElContainer, ElHeader, ElCard, ElTag, ElAlert, ElIcon, ElMain, ElBreadcrumb, ElBreadcrumbItem, ElBadge, ElButton } from 'element-plus';
import { ArrowRight, Select, CloseBold } from '@element-plus/icons-vue';
import { GetManageNotifications, Cope, DeleteManagerNotification } from '@/utils/request/notification';
import { useRouter } from 'vue-router';

const router = useRouter();
const chartsRef = ref(null);
const show = ref(false);
const notificationList = ref([]);
const unread = ref(0);

const asyncNotification = async () => {
    const res = await GetManageNotifications();
    if (res.meta.code === 200) {
        notificationList.value = res.data;
        notificationList.value.forEach(item => {
            if (item.isRead === 0) {
                unread.value++;
            }
        })
    }

}

onMounted(() => {
    // 初始化echarts实例
    const charts = echarts.init(chartsRef.value, null, { width: 580, height: 460 });
    const option = {
        xAxis: {
            splitLine: {
                show: false
            },
            type: 'category',
            data: ['1月份', '2月份', '3月份', '4月份', '5月份', '6月份', '7月份', '8月份', '9月份', '10月份', '11月份', '12月份'],
            inverse: true,
            animationDuration: 300,
            animationDurationUpdate: 300,
        },
        yAxis: {
            max: 'dataMax'
        },
        series: [
            {
                realtimeSort: true,
                name: '社区访问量',
                type: 'bar',
                data: [234, 246, 567, 778, 999, 1211, 1243, 2245, 4453, 11003, 12901, 13999],

            }
        ],
        legend: {
            show: true
        },
        animationDuration: 3000,
        animationDurationUpdate: 3000,
        animationEasing: 'linear',
        animationEasingUpdate: 'linear'
    };
    // 设置实例参数
    charts.setOption(option);

    asyncNotification();
});

const cope = async (id, type) => {
    let _type = type.toLowerCase();
    let path = `/admin/manage/${_type}`;
    const res = await Cope(id);
    console.log(res);
    router.push(path)
}

const remove = async (id) => {
    const del = await DeleteManagerNotification(id)
    if (del.meta.code === 223) {
        asyncNotification();
    }
}

</script>
<style lang='less' scoped>
@import url('./index.less');
</style>
