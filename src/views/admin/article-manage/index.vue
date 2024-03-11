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
                            <el-table-column label="类型" width="80">

                                <template #default="{ row }">
                                    <el-tag v-if="row.approved === 1" type="success">已审核</el-tag>
                                    <el-tag v-else-if="row.approved === 0" type="warning">待审核</el-tag>
                                    <el-tag v-else type="error">未通过</el-tag>
                                </template>

                            </el-table-column>
                            <el-table-column prop="articleLike" sortable label="点赞数" width="100" />
                            <el-table-column prop="articleDislike" sortable label="拉踩数" width="100" />
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
                                    <el-popover placement="right" width="160" :disabled="scope.row.approved !== 0">
                                        <template #reference>
                                            <el-button type="primary" :icon="Tools" :enterable="false"
                                                :disabled="scope.row.approved !== 0" />
                                        </template>
                                        <el-button type="success" @click="confirm(scope.row)">通过</el-button>
                                        <el-button type="danger" @click="showAudit(scope.row)">驳回</el-button>
                                    </el-popover>

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
        <el-dialog class="preview" v-model="previewVisible" title="预览" width="50%" draggable overflow center>
            <div class="preview-title">标题：{{ previewArticle.title }}</div>
            <div class="preview-info">
                <el-tag peffect="light" type="success" size="large">{{ previewArticle.type }}</el-tag>
                <span>发布时间--.--:&gt;&gt;{{ previewArticle.postTime.slice(0, 10) }}</span>
            </div>
            <div class="preview-content" v-for="preItem, index in previewArticle.content.contentList" :key="index">
                <div class="preview-text" v-html="preItem.text"></div>
                <div class="preview-image" v-for="image, iItem in preItem.imageList" :key="iItem">
                    <el-image :src="image.url" lazy />
                </div>
                <el-divider>
                    <el-icon><star-filled /></el-icon>
                </el-divider>
                <div class="preview-video" v-for="video, vItem in preItem.videoList" :key="vItem">
                    <video controls :src="video.url" width="500"></video>
                </div>
                <el-divider content-position="left">Game community</el-divider>
                <div class="preview-desc">
                    <h3>内容简介：{{ previewArticle.contentDes }}</h3>
                </div>
            </div>

            <template #footer>
                <div class="preview-footer">
                    <div class="view"></div>
                    <div class="like">
                        <img :src="require('@/assets/static/icons/zan.png')" alt="like">
                        <span>{{ previewArticle.articleLike }}</span>
                    </div>
                    <div class="dislike">
                        <img :src="require('@/assets/static/icons/zancopy.png')" alt="dislike">
                        <span>{{ previewArticle.articleDislike }}</span>
                    </div>
                    <div class="collect">
                        <img :src="require('@/assets/static/icons/collection.png')" alt="collect">
                        <span>{{ previewArticle.collectCount }}</span>
                    </div>
                </div>
            </template>
        </el-dialog>

        <!--aduit -->
        <el-dialog v-model="auditDialogVisible" title="审核驳回" width="30%" draggable overflow>
            <el-divider style="margin-top: -24px;" border-style="dashed" />
            <div class="message-box">
                <el-input v-model="auditReason" type="textarea" :rows="4" placeholder="请输入驳回意见" />
            </div>

            <template #footer>
                <div class="dialog-footer">
                    <el-button plain @click="auditDialogVisible = false">取消</el-button>
                    <el-button plain="" type="primary" @click="reject()">确认</el-button>
                </div>
            </template>

        </el-dialog>

    </div>
</template>

<script setup>
import { onMounted, reactive, ref, getCurrentInstance } from 'vue';
import { StarFilled } from '@element-plus/icons-vue'
import {
    ElContainer, ElPopover, ElMain, ElCard, ElSkeleton, ElRow,
    ElForm, ElDivider, ElInput, ElMessageBox, ElNotification,
    ElTable, ElTableColumn, ElPagination, ElTooltip, ElButton
} from 'element-plus';
import { Tools, Delete, Search } from '@element-plus/icons-vue';
import { SelectBatchArticle, SearchArticle, ConfirmArticle, RejectArticle, DeleteArticle } from '@/utils/request/admin';

const instance = getCurrentInstance();
const loading = ref(true);
const previewVisible = ref(false);
const auditDialogVisible = ref(false);
const auditAticle = ref({});
const auditReason = ref('');
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
    localStorage.setItem("admin_temp_articles", JSON.stringify(res.data));
}

onMounted(() => {
    setTimeout(() => { loading.value = false; }, 2000);
    let tempdata = localStorage.getItem("admin_temp_articles");
    if (tempdata == null) {
        AsyncArticle();
        return;
    }
    let users = JSON.parse(tempdata);
    data.list = users.data;
    data.currentPage = parseInt(users.currentPage);
    data.pageSize = parseInt(users.pageSize);
    data.total = parseInt(users.total);
    setTimeout(() => localStorage.removeItem("admin_temp_articles"), 1000 * 60 * 2)
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
    if (row.content instanceof Object) previewArticle.value.content = row.content;
    else previewArticle.value.content = JSON.parse(row.content);
}

const search = async (currentPage = 1, pageSize = 10) => {
    const searchRes = await SearchArticle(keyword.value, currentPage, pageSize);
    if (searchRes.meta.code === 200) {
        instance.proxy.$nextTick(() => {
            data.list = searchRes.data.data;
            data.currentPage = searchRes.data.currentPage;
            data.pageSize = searchRes.data.pageSize;
            data.total = searchRes.data.total;
        })
        ElNotification({
            title: '系统消息',
            message: '已查询符合条件的数据',
            type: 'success',
        })
    }
}

const remove = (id) => {
    ElMessageBox.confirm(
        '此操作将永久删除该帖子以及其包含下的所有数据, 是否继续?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        const delRes = await DeleteArticle(id);
        if (delRes.meta.code === 223) {
            ElNotification({
                title: '系统消息',
                message: delRes.meta.msg,
                type: 'success',
            })
            AsyncArticle(data.currentPage, data.pageSize);
        } else
            ElNotification({
                title: '系统消息',
                message: '删除失败',
                type: 'error',
            })
    }).catch(err => err)
}

const confirm = async (row) => {
    const confirm = await ConfirmArticle({
        id: row.id,
        posterId: row.posterId
    });

    if (confirm.meta.code === 200) {
        ElNotification({
            title: '系统消息',
            message: '操作成功',
            type: 'success',
        })
        AsyncArticle(data.currentPage, data.pageSize);
    } else ElNotification({
        title: '系统消息',
        message: '操作失败',
        type: 'error',
    })
}

const showAudit = (row) => {
    auditDialogVisible.value = true
    auditAticle.value = row;
}

const reject = async () => {
    const reject = await RejectArticle({
        article: {
            id: auditAticle.value.id,
            posterId: auditAticle.value.posterId
        },
        notificationContent: auditReason.value,
    })
    if (reject.meta.code === 200) {
        ElNotification({
            title: '系统消息',
            message: '操作成功',
            type: 'success',
        })
        AsyncArticle(data.currentPage, data.pageSize);
    } else ElNotification({
        title: '系统消息',
        message: '操作失败',
        type: 'error',
    })
    auditDialogVisible.value = false;
}
</script>

<style lang='less' scoped>
@import url('./index.less');
</style>
