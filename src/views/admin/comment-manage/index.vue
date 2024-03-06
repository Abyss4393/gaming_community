<template>
    <div id="comment-manage">
        <el-container>
            <el-main>
                <el-card class="table">
                    <el-row>
                        <el-table ref="TabaleRef" :data="data.list" max-height="600" table-layout="auto"
                            highlight-current-row flxed>
                            <el-table-column prop="id" sortable label="评论编号" width="120" />
                            <el-table-column prop="nickname" label="发帖人" width="150" />
                            <el-table-column label="评论内容" width="100">
                                <template #default="{ row }">
                                    <el-button type="warning" round @click="showPreview(row)">预览</el-button>
                                </template>
                            </el-table-column>
                            <el-table-column prop="commentTime" sortable label="评论时间" width="180" />
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
                                    <el-tooltip effect="dark" content="审核" placement="right">
                                        <el-button type="primary" :icon="Tools" :enterable="false"
                                            @click="aduit(scope.row.id)"></el-button>
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
        <!--查看评论 -->
        <el-dialog class="preview" v-model="previewVisible" title="查看评论" width="30%" draggable overflow left>
            <div v-html="previewComment.content"></div>
            <template #footer="{ row }">
                <span>s1</span>
            </template>
        </el-dialog>
    </div>
</template>
<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import {
    ElContainer, ElHeader, ElMain, ElCard, ElDialog, ElRow,
    ElForm, ElDivider, ElInput, ElMessageBox, ElNotification,
    ElTable, ElTableColumn, ElPagination, ElTooltip, ElButton
} from 'element-plus';
import { Tools, Delete, Search } from '@element-plus/icons-vue';
import { SelectBatchComment } from '@/utils/request/admin';

const TabaleRef = ref(null);
const instance = getCurrentInstance();
const loading = ref(true);
const previewVisible = ref(false);
const previewComment = ref({});
const keyword = ref('');
const data = reactive({
    list: [],
    currentPage: null,
    pageSize: null,
    total: null,

});

const AsyncComment = async (currentPage = 1, pageSize = 10) => {
    const res = await SelectBatchComment(currentPage, pageSize)
    console.log(res);
    if (res.meta.code === 200) {
        data.list = res.data.data;
        data.currentPage = parseInt(res.data.currentPage);
        data.pageSize = parseInt(res.data.pageSize);
        data.total = parseInt(res.data.total);
    }
}

onMounted(() => {
    setTimeout(() => { loading.value = false; }, 2000);
    let tempdata = localStorage.getItem("admin_temp_comment");
    if (tempdata == null) {
        AsyncComment();
        return;
    }
    let users = JSON.parse(tempdata);
    data.list = users.data;
    data.currentPage = parseInt(users.currentPage);
    data.pageSize = parseInt(users.pageSize);
    data.total = parseInt(users.total);
    setTimeout(() => localStorage.removeItem("admin_temp_comment"), 1000 * 60 * 2)
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
    previewComment.value = row;
}

</script>
<style lang='less' scoped>
@import url('./index.less');
</style>
