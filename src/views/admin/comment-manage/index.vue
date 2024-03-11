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
                            <el-table-column label="审核状态" width="80">

                                <template #default="{ row }">
                                    <el-tag v-if="row.approved === 1" type="success">已审核</el-tag>
                                    <el-tag v-else-if="row.approved === 0" type="warning">待审核</el-tag>
                                    <el-tag v-else type="error">未通过</el-tag>
                                </template>

                            </el-table-column>
                            <el-table-column prop="commentLike" sortable label="点赞数" width="100" />
                            <el-table-column prop="commentDislike" sortable label="拉踩数" width="100" />
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
                                            @click="remove(scope.row.aid, scope.row.uid)"></el-button>
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
        <!--查看评论 -->
        <el-dialog class="preview" v-model="previewVisible" title="查看评论" width="30%" draggable overflow left>
            <el-divider style="margin-top: -24px;" border-style="dashed" />
            <div v-html="previewComment.content"></div>

            <template #footer>
                <div class="preview-footer">
                    <div class="view"></div>
                    <div class="like">
                        <img :src="require('@/assets/static/icons/zan.png')" alt="like">
                        <span>{{ previewComment.commentLike }}</span>
                    </div>
                    <div class="dislike">
                        <img :src="require('@/assets/static/icons/zancopy.png')" alt="dislike">
                        <span>{{ previewComment.commentDislike }}</span>
                    </div>
                </div>
            </template>
        </el-dialog>

        <el-dialog v-model="auditDialogVisible" title="审核驳回" width="30%" draggable overflow>
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
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import {
    ElContainer, ElMain, ElCard, ElDialog, ElRow,
    ElForm, ElDivider, ElInput, ElMessageBox, ElNotification,
    ElTable, ElTableColumn, ElPagination, ElTooltip, ElButton
} from 'element-plus';
import { Tools, Delete, Search } from '@element-plus/icons-vue';
import { SelectBatchComment, SearchComment, DeleteComment, ConfirmComment, RejectComment } from '@/utils/request/admin';

const TabaleRef = ref(null);
const instance = getCurrentInstance();
const loading = ref(true);
const previewVisible = ref(false);
const previewComment = ref({});
const keyword = ref('');
const auditDialogVisible = ref(false);
const auditReason = ref('');
const auditComment = ref({})
const data = reactive({
    list: [],
    currentPage: null,
    pageSize: null,
    total: null,

});

const AsyncComment = async (currentPage = 1, pageSize = 10) => {
    const res = await SelectBatchComment(currentPage, pageSize)
    if (res.meta.code === 200) {
        data.list = res.data.data;
        data.currentPage = parseInt(res.data.currentPage);
        data.pageSize = parseInt(res.data.pageSize);
        data.total = parseInt(res.data.total);
    }
    localStorage.setItem("admin_temp_comments", JSON.stringify(res.data));
}

onMounted(() => {
    setTimeout(() => { loading.value = false; }, 2000);
    let tempdata = localStorage.getItem("admin_temp_comments");
    if (tempdata == null) {
        AsyncComment();
        return;
    }
    let users = JSON.parse(tempdata);
    data.list = users.data;
    data.currentPage = parseInt(users.currentPage);
    data.pageSize = parseInt(users.pageSize);
    data.total = parseInt(users.total);
    setTimeout(() => localStorage.removeItem("admin_temp_comments"), 1000 * 60 * 2)
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

const showAudit = (row) => {
    auditDialogVisible.value = true
    auditComment.value = row;

}



const search = async (currentPage = 1, pageSize = 10) => {
    const searchRes = await SearchComment(keyword.value, currentPage, pageSize);
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

const remove = (aid, uid) => {
    ElMessageBox.confirm(
        '此操作将永久删除该评论以及其包含下的所有回复, 是否继续?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        const delRes = await DeleteComment(aid, uid);
        if (delRes.meta.code === 223) {
            ElNotification({
                title: '系统消息',
                message: delRes.meta.msg,
                type: 'success',
            })
            AsyncComment(data.currentPage, data.pageSize);
        } else
            ElNotification({
                title: '系统消息',
                message: '删除失败',
                type: 'error',
            })
    }).catch(err => err)
}

const confirm = async (row) => {
    console.log(row);
    const confirm = await ConfirmComment({
        id: row.id,
        uid: row.uid
    });
    if (confirm.meta.code === 200) {
        ElNotification({
            title: '系统消息',
            message: '操作成功',
            type: 'success',
        })
        AsyncComment(data.currentPage, data.pageSize);
    } else ElNotification({
        title: '系统消息',
        message: '操作失败',
        type: 'error',
    })
}


const reject = async () => {
    const reject = await RejectComment({
        comment: {
            id: auditComment.value.id,
            uid: auditComment.value.uid
        },
        notificationContent: auditReason.value,
    })
    if (reject.meta.code === 200) {
        ElNotification({
            title: '系统消息',
            message: '操作成功',
            type: 'success',
        })
        AsyncComment(data.currentPage, data.pageSize);
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
