<template>
    <div id="admin-index">
        <el-container>
            <el-header>
                <div class="header">
                    <el-breadcrumb :separator-icon="ArrowRight">
                        <el-breadcrumb-item>
                            <el-badge :value="6"><el-button>待处理事项</el-button>
                            </el-badge>
                        </el-breadcrumb-item>
                        <el-breadcrumb-item><el-button>首页</el-button></el-breadcrumb-item>
                    </el-breadcrumb>
                </div>
            </el-header>
            <el-main>
                <div ref="chartsRef" id="charts">

                </div>
            </el-main>
        </el-container>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { ElContainer, ElHeader, ElMain, ElBreadcrumb, ElBreadcrumbItem, ElBadge, ElButton } from 'element-plus';
import { ArrowRight } from '@element-plus/icons-vue';
const chartsRef = ref(null);

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
});

</script>
<style lang='less' scoped>
@import url('./index.less');
</style>
