<template>
    <div id="article-manage">
        <el-container>
            <el-main>

                <el-card class="table">
                    <el-row>
                        <el-table ref="TabaleRef" :data="data.list" max-height="600"
                            :default-sort="[{ prop: 'id', order: 'descending' }, { prop: 'createTime', order: 'descending' }, { prop: 'lastLoadingTime', order: 'descending' }]"
                            highlight-current-row flxed>
                            <el-table-column prop="id" sortable label="帖子编号" width="120"></el-table-column>
                            <el-table-column prop="posterName" label="发帖人" width="150"></el-table-column>
                            <el-table-column prop="title" label="标题" width="140"></el-table-column>
                            <el-table-column prop="contentDes" label="内容简介" width="100"></el-table-column>
                            <el-table-column label="内容" width="80">
                                <template #default="{ row }">
                                    <el-button type="warning" round @click="showPreview(row)">预览</el-button>
                                </template>
                            </el-table-column>
                            <el-table-column prop="type" label="类型" width="80">
                                <template #default="{ row }">
                                    <el-tag>{{ row.type }}</el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="positivenessCount" sortable label="点赞数" width="100" />
                            <el-table-column prop="passivenessCount" sortable label="拉踩数" width="100" />
                            <el-table-column prop="collectCount" sortable label="收藏数" width="100" />
                            <el-table-column prop="postTime" sortable label="发布时间" width="180" />
                            <el-table-column label="" width="200">
                                <template #header>
                                    <el-input v-model="keyword" clearable>
                                        <template #append>
                                            <el-button type="primary" :icon="Search" @click="search()" circle />
                                        </template>
                                    </el-input>
                                </template>
                                <template #default="scope">
                                    <el-tooltip effect="dark" content="删除" placement="top">
                                        <el-button type="danger" :icon="Delete" :enterable="false"
                                            @click="remove(scope.row.id)"></el-button>
                                    </el-tooltip>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-row>
                    <el-row class="page">
                        <el-pagination v-model:current-page="data.currentPage" :page-sizes="[5, 10, 20]"
                            v-model:page-size="data.pageSize" layout="total, sizes, prev, pager, next, jumper"
                            :total="data.total" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                            background>
                        </el-pagination>
                    </el-row>
                </el-card>
            </el-main>
        </el-container>
        <!-- preview -->
        <el-dialog v-model="previewVisible" title="预览" width="50%"  draggable overflow center>
            <h1 class="preview-title">标题：{{ previewArticle.title }}</h1>
            <div class="preview-content" v-for="preItem, index in previewArticle.content.contentList" :key="index">
                <div class="preview-text" v-html="preItem.text"></div>
                <div class="preview-video" v-for="video,vItem in preItem.videoList" :key="vItem">
                    <video controls :src="video.url"></video>
                </div>
            </div>
        </el-dialog>
    </div>
</template>
<script setup>
import { onMounted, reactive, ref, getCurrentInstance } from 'vue';
import {
    ElContainer, ElHeader, ElMain, ElCard, ElSkeleton, ElRow,
    ElForm, ElFormItem, ElInput, ElMessageBox, ElNotification,
    ElTable, ElTableColumn, ElPagination, ElTooltip, ElButton
} from 'element-plus';
import { Edit, Delete, Search } from '@element-plus/icons-vue';
import { SelectBatchArticle } from '@/utils/request/admin';

const instance = getCurrentInstance();
const loading = ref(true);
const previewVisible = ref(false);
const previewArticle = ref({});
const keyword = ref('');
const data = reactive({
    list: [],
    currentPage: null,
    pageSize: null,
    total: null,

});

const AsyncArticle = async (currentPage = 1, pageSize = 10) => {
    const res = await SelectBatchArticle(currentPage, pageSize)
    if (res.meta.code === 200) {
        data.list = res.data.data;
        data.currentPage = parseInt(res.data.currentPage);
        data.pageSize = parseInt(res.data.pageSize);
        data.total = parseInt(res.data.total);
    }
}

onMounted(() => {
    setTimeout(() => { loading.value = false; }, 15000);
    let tempdata = localStorage.getItem("admin_temp_article");
    if (tempdata == null) {
        AsyncArticle();
        return;
    }
    let users = JSON.parse(tempdata);
    data.list = users.data;
    data.currentPage = parseInt(users.currentPage);
    data.pageSize = parseInt(users.pageSize);
    data.total = parseInt(users.total);
    setTimeout(() => localStorage.removeItem("admin_temp_article"), 1000 * 60 * 2)
})

const handleCurrentChange = (newval) => {
    data.currentPage = newval;
    if (keyword.value != '') search();
    AsyncArticle(newval, data.pageSize);
}

const handleSizeChange = (newval) => {
    data.pageSize = newval;
    if (keyword.value != '') search();
    AsyncArticle(data.currentPage, newval);
}

const showPreview = (row) => {
    previewVisible.value = true;
    previewArticle.value = row;
    previewArticle.value.content = JSON.parse(row.content);
    console.log(previewArticle.value.content);
}
</script>
<style lang='less' scoped>
@import url('./index.less');
</style>
