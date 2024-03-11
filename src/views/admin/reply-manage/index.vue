<template>
    <div id="reply-manage">
        <el-container>
            <el-main>
                <el-card class="table">
                    <el-row>
                        <el-table ref="TabaleRef" :data="data.list" max-height="600" table-layout="auto"
                            highlight-current-row flxed>
                            <el-table-column prop="id" sortable label="评论编号" width="100" />
                            <el-table-column prop="replier" label="回复人" width="120" />
                            <el-table-column label="父级编号">
                                <el-table-column prop="parentCommentId" sortable label="父级评论" width="120" />
                                <el-table-column label="父级回复" width="120">
                                    <template #default="{ row }">
                                        <span>{{ row.parentReplyId === -1 ? '暂无上级回复' : row.parentReplyId }}</span>
                                    </template>
                                </el-table-column>
                            </el-table-column>
                            <el-table-column prop="commenter" label="评论人" width="100" />
                            <el-table-column label="回复内容" width="90">

                                <template #default="{ row }">
                                    <el-button type="primary" round @click="showPreview(row)">预览</el-button>
                                </template>
                            </el-table-column>
                            <el-table-column prop="replyLike" sortable label="点赞数" width="100" />
                            <el-table-column prop="replyDislike" sortable label="拉踩数" width="100" />
                            <el-table-column prop="replyTime" sortable label="评论时间" width="180" />
                            <el-table-column label="审核状态" width="120">

                                <template #default="{ row }">
                                    <el-tag v-if="row.approved === 1" type="success">已审核</el-tag>
                                    <el-tag v-else-if="row.approved === 0" type="warning">待审核</el-tag>
                                    <el-tag v-else type="danger">未通过</el-tag>
                                </template>

                            </el-table-column>
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

        <!--查看回复 -->
        <el-dialog class="preview" v-model="previewVisible" title="查看回复" width="30%" draggable overflow left>
            <el-divider style="margin-top: -24px;" border-style="dashed" />
            <div v-html="previewReply.content"></div>

            <template #footer>
                <div class="preview-footer">
                    <div class="view"></div>
                    <div class="like">
                        <img :src="require('@/assets/static/icons/zan.png')" alt="like">
                        <span>{{ previewReply.replyLike }}</span>
                    </div>
                    <div class="dislike">
                        <img :src="require('@/assets/static/icons/zancopy.png')" alt="dislike">
                        <span>{{ previewReply.replyDislike }}</span>
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
    ElDivider, ElInput, ElMessageBox, ElNotification,
    ElTable, ElTableColumn, ElPagination, ElTooltip, ElButton
} from 'element-plus';
import { Tools, Delete, Search } from '@element-plus/icons-vue';
import { SelectBatchReply, SearchReply, ConfirmReply, RejectReply, DeleteReply } from '@/utils/request/admin';


const TabaleRef = ref(null);
const instance = getCurrentInstance();
const loading = ref(true);
const previewVisible = ref(false);
const previewReply = ref({});
const keyword = ref('');
const auditDialogVisible = ref(false);
const auditReason = ref('');
const auditReply = ref({})
const data = reactive({
    list: [],
    currentPage: null,
    pageSize: null,
    total: null,

});

const AsyncRply = async (currentPage = 1, pageSize = 10) => {
    const res = await SelectBatchReply(currentPage, pageSize)
    console.log(res);
    if (res.meta.code === 200) {
        data.list = res.data.data;
        data.currentPage = parseInt(res.data.currentPage);
        data.pageSize = parseInt(res.data.pageSize);
        data.total = parseInt(res.data.total);
    }
    localStorage.setItem("admin_temp_replies", JSON.stringify(res.data));
}

onMounted(() => {
    setTimeout(() => { loading.value = false; }, 2000);
    let tempdata = localStorage.getItem("admin_temp_replies");
    if (tempdata == null) {
        AsyncRply();
        return;
    }
    let users = JSON.parse(tempdata);
    data.list = users.data;
    data.currentPage = parseInt(users.currentPage);
    data.pageSize = parseInt(users.pageSize);
    data.total = parseInt(users.total);
    setTimeout(() => localStorage.removeItem("admin_temp_replies"), 1000 * 60 * 2)
})

const handleCurrentChange = (newval) => {
    data.currentPage = newval;
    if (keyword.value != '') search();
    AsyncRply(newval, data.pageSize);
}

const handleSizeChange = (newval) => {
    data.pageSize = newval;
    if (keyword.value != '') search();
    AsyncRply(data.currentPage, newval);
}

const showPreview = (row) => {
    previewVisible.value = true;
    previewReply.value = row;
}

const showAudit = (row) => {
    auditDialogVisible.value = true
    auditReply.value = row;

}


const remove = (id) => {
    ElMessageBox.confirm(
        '此操作将永久删除该评论以及其包含下的所有回复, 是否继续?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        const delRes = await DeleteReply(id);
        if (delRes.meta.code === 223) {
            ElNotification({
                title: '系统消息',
                message: delRes.meta.msg,
                type: 'success',
            })
            AsyncRply(data.currentPage, data.pageSize);
        } else
            ElNotification({
                title: '系统消息',
                message: '删除失败',
                type: 'error',
            })
    }).catch(err => err)
}


const search = async (currentPage = 1, pageSize = 10) => {
    const searchRes = await SearchReply(keyword.value, currentPage, pageSize);
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

const confirm = async (row) => {
    const confirm = await ConfirmReply({
        id: row.id,
        userId: row.userId
    });
    if (confirm.meta.code === 200) {
        ElNotification({
            title: '系统消息',
            message: '操作成功',
            type: 'success',
        })
        AsyncRply(data.currentPage, data.pageSize);
    } else ElNotification({
        title: '系统消息',
        message: '操作失败',
        type: 'error',
    })
}


const reject = async () => {
    const reject = await RejectReply({
        reply: {
            id: auditReply.value.id,
            userId: auditReply.value.userId
        },
        notificationContent: auditReason.value,
    })
    if (reject.meta.code === 200) {
        ElNotification({
            title: '系统消息',
            message: '操作成功',
            type: 'success',
        })
        AsyncRply(data.currentPage, data.pageSize);
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
