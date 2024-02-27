<template>
    <div id="user-manage">
        <el-container>
            <el-header class="header"></el-header>
            <el-main>
                <el-skeleton :rows="12" :loading="loading" animated>
                    <template #default>
                        <el-card class="table">
                            <el-row>
                                <el-table ref="TabaleRef" :data="data.list" max-height="600"
                                    :default-sort="[{ prop: 'id', order: 'descending' }, { prop: 'createTime', order: 'descending' }, { prop: 'lastLoadingTime', order: 'descending' }]"
                                    highlight-current-row flxed>
                                    <el-table-column prop="id" sortable label="用户编号" width="120"></el-table-column>
                                    <el-table-column prop="username" label="用户账号" width="150"></el-table-column>
                                    <el-table-column label="用户密码" width="150">
                                        <template #default="scope">
                                            <span style="cursor: pointer;" @click="show(scope.$index)">
                                                {{ showPwd && activeIndex === scope.$index ? scope.row.password :
                                                    '*'.repeat(scope.row.password.length) }}
                                            </span>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="nickname" label="用户昵称" width="140"></el-table-column>
                                    <el-table-column label="头像" width="180">
                                        <template #default="{ row }">
                                            <el-image :src="row.avatar" style="width: 50px; height: 50px" />
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
                                    <el-table-column prop="createTime" sortable label="注册时间" width="180"></el-table-column>
                                    <el-table-column prop="lastLandingTime" sortable label="最近登录时间"
                                        width="180"></el-table-column>
                                    <el-table-column label="" width="200">
                                        <template #header>
                                            <el-input v-model="keyword" clearable>
                                                <template #append>
                                                    <el-button type="primary" :icon="Search" @click="search()" circle />
                                                </template>
                                            </el-input>
                                        </template>
                                        <template #default="scope">
                                            <el-tooltip effect="dark" content="修改" placement="top">
                                                <el-button type="primary" :icon="Edit" :enterable="false"
                                                    @click="beforeModify(scope.row)" />
                                            </el-tooltip>
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
                                    :total="data.total" @size-change="handleSizeChange"
                                    @current-change="handleCurrentChange" background>
                                </el-pagination>
                            </el-row>
                        </el-card>
                    </template>
                </el-skeleton>
                <span class="skeleton">1</span>
            </el-main>
        </el-container>
        <!--修改用户信息 -->
        <el-dialog class="modify-user" title="修改用户信息" v-model="modifyUserdialogVisible" align-center width="800">
            <el-form :model="modifyUser" ref="ModifyForm">
                <el-form-item prop="username" label="账号" required label-width="80px">
                    <el-col :span="16">
                        <el-input class="modify-item-input" type="text" placeholder="输入修改账号"
                            v-model="modifyUser.username" />
                    </el-col>
                </el-form-item>
                <el-form-item prop="password" label="密码" required label-width="80px">
                    <el-col :span="16">
                        <el-input class="modify-item-input" type="password" placeholder="输入修改账号密码"
                            v-model="modifyUser.password" show-password />
                    </el-col>
                </el-form-item>
                <el-form-item prop="nickname" label="昵称" required label-width="80px">
                    <el-col :span="16">
                        <el-input class="modify-item-input" type="text" placeholder="输入修改昵称"
                            v-model="modifyUser.nickname" />
                    </el-col>
                </el-form-item>
                <el-form-item prop="mobile" label="手机" required label-width="80px">
                    <el-col :span="16">
                        <el-input class="modify-item-input" type="text" placeholder="输入修改手机号" v-model="modifyUser.mobile" />
                    </el-col>
                </el-form-item>
                <el-form-item prop="email" label="邮箱" required label-width="80px">
                    <el-col :span="16">
                        <el-input class="modify-item-input" type="email" placeholder="输入修改邮箱" v-model="modifyUser.email" />
                    </el-col>
                </el-form-item>
                <el-form-item prop="gender" label="性别" required label-width="80px">
                    <el-col :span="16">
                        <el-select v-model="modifyUser.gender" placeholder="修改性别">
                            <el-option label="男" value="男" />
                            <el-option label="女" value="女" />
                            <el-option label="保密" value="保密" />
                        </el-select>
                    </el-col>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button plain @click="modifyUserdialogVisible = false">取消</el-button>
                    <el-button plain="" type="primary" @click="modify">确认</el-button>
                </div>
            </template>
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
import { SelectBatchUser, SearchUser, ModifyUser, DeleteUser } from '@/utils/request/admin';

const instance = getCurrentInstance();
const loading = ref(true);
const activeIndex = ref('0');
const showPwd = ref(false);
const TabaleRef = ref();
const data = reactive({
    list: [],
    currentPage: null,
    pageSize: null,
    total: null,

});
const modifyUserdialogVisible = ref(false);
const keyword = ref('');
const modifyUser = reactive({});
const AsyncUser = async (currentPage = 1, pageSize = 10) => {
    const res = await SelectBatchUser(currentPage, pageSize);
    if (res.meta.code === 200) {
        data.list = res.data.data;
        data.currentPage = parseInt(res.data.currentPage);
        data.pageSize = parseInt(res.data.pageSize);
        data.total = parseInt(res.data.total);
    }
    localStorage.setItem("admin_temp_users", JSON.stringify(res.data));

}
onMounted(() => {
    setTimeout(() => { loading.value = false; }, 2000);
    let tempdata = localStorage.getItem("admin_temp_users");
    if (tempdata == null) {
        AsyncUser();
        return;
    }
    let users = JSON.parse(tempdata);
    data.list = users.data;
    data.currentPage = parseInt(users.currentPage);
    data.pageSize = parseInt(users.pageSize);
    data.total = parseInt(users.total);
    setTimeout(() => localStorage.removeItem("admin_temp_users"), 1000 * 60 * 2)
})

const handleCurrentChange = (newval) => {
    data.currentPage = newval;
    if (keyword.value != '') search();
    AsyncUser(newval, data.pageSize);
}

const handleSizeChange = (newval) => {
    data.pageSize = newval;
    if (keyword.value != '') search();
    AsyncUser(data.currentPage, newval);
}

const show = (index) => {
    activeIndex.value = index;
    showPwd.value = !showPwd.value;
}

const search = async (currentPage = 1, pageSize = 10) => {
    const searchRes = await SearchUser(keyword.value, currentPage, pageSize);
    console.log(searchRes);
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

const beforeModify = (user) => {
    modifyUserdialogVisible.value = true;
    Object.assign(modifyUser, user);
}

const modify = async () => {
    let user = modifyUser;
    const modRes = await ModifyUser({
        id: user.id,
        username: user.username,
        password: user.password,
        nickname: user.nickname,
        gender: user.gender,
        email: user.email,
        phone: user.phone,
        role: user.role,
    });
    if (modRes.meta.code === 224) {
        ElNotification({
            title: '系统消息',
            message: modRes.meta.msg,
            type: 'success',
        })
        AsyncUser(data.currentPage, data.pageSize);
    } else
        ElNotification({
            title: '系统消息',
            message: '更新失败',
            type: 'error',
        })
    modifyUserdialogVisible.value = false;
}

const remove = (id) => {
    ElMessageBox.confirm(
        '此操作将永久删除该用户, 是否继续?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        const delRes = await DeleteUser(id);
        console.log(delRes);
        if (delRes.meta.code === 223) {
            ElNotification({
                title: '系统消息',
                message: delRes.meta.msg,
                type: 'success',
            })
            AsyncUser(data.currentPage, data.pageSize);
        } else
            ElNotification({
                title: '系统消息',
                message: '删除失败',
                type: 'error',
            })
    }).catch(err => err)
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>
