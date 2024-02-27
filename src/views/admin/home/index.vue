<template>
    <div id="index">
        <el-container>
            <el-aside width="200px">
                <div class="sys-title">
                    <img src="" alt="">
                    <span title="game community">游戏社区后台管理系统</span>
                </div>
                <el-menu active-text-color="#ffd04b" background-color="#1b435c" default-active="1" text-color="#fff">
                    <div v-for="menuItem, index in  menu " :key="index">
                        <el-sub-menu v-if="menuItem.isGroup" :index="index + 1 + ''">
                            <template #title>
                                <span>{{ menuItem.groupName }}</span>
                            </template>
                            <el-menu-item-group>
                                <el-menu-item v-for="item, subIndex in menuItem.group" :key="subIndex" @click="to(item)"
                                    :index="`${index + 1}` + '-' + `${subIndex + 1}` + ''">{{ item.title }}</el-menu-item>
                            </el-menu-item-group>
                        </el-sub-menu>
                        <el-menu-item v-else="!menuItem.isGroup" :index="index + 1 + ''" @click="to(menuItem)">
                            {{ menuItem.title }}
                        </el-menu-item>
                    </div>
                </el-menu>
            </el-aside>
            <el-main>
                <el-header>
                    <el-card>
                        <div class="header">
                            <div class="user">
                                <img :src="require('@/assets/static/icons/admin.png')" alt="">
                                <span>admin</span>
                            </div>
                            <img class="exit" :src="require('@/assets/static/icons/admin_exit.png')" @click="exit">
                        </div>
                        <el-row>
                            <el-col :span="2">
                                <div class="navigate">
                                    <img class="back" :src="require('@/assets/static/icons/admin_left.png')" @click="back">
                                    <div class="home">
                                        <img :src="require('@/assets/static/icons/admin_home.png')" @click="home">
                                        <span>首页</span>
                                    </div>
                                </div>
                            </el-col>
                            <el-col :span="22">
                                <el-tabs type="border-card" v-model="tabIndex" closable @tab-remove="removeTab"
                                    @tab-change="changeTab">
                                    <el-tab-pane v-for=" item  in  tabs " :key="item.name" :name="item.name"
                                        :label="item.title"></el-tab-pane>
                                </el-tabs>
                            </el-col>
                        </el-row>
                    </el-card>
                </el-header>
                <div class="router">
                    <router-view></router-view>
                </div>
            </el-main>
        </el-container>
    </div>
</template>
<script setup>
import { getCurrentInstance, ref } from 'vue';
import { ElContainer, ElAside, ElHeader, ElMain, ElTabs, ElTabPane, ElMessageBox, ElMessage, ElNotification } from 'element-plus';

const router = getCurrentInstance().proxy.$router;

const menu = ref([
    {
        isGroup: false,
        title: '用户管理',
        path: '/admin/manage/user'
    }, {
        isGroup: false,
        title: '帖子管理',
        path: '/admin/manage/article'
    }, {
        isGroup: true,
        groupName: '审核管理',
        group: [{
            title: '审核帖子',
            path: '/admin/audit/article'
        }, {
            title: '审核评论',
            path: '/admin/audit/comment'
        },{
            title: '审核回复',
            path: '/admin/audit/article'
        }]
    },{
        isGroup: false,
        title: '反馈',
        path: '/admin/manage/12'
    }
]);

const tabs = ref([{
    title: '用户管理',
    name: '1',
    path: '/admin/manage/user'
},
{
    title: '帖子管理',
    name: '2',
    path: '/admin/manage/article'
}])
let tabIndex = ref('1')


const to = (target) => {
    router.push(target.path);
    addTab(target)

}

const addTab = (targetTab) => {
    const tabTitleMap = tabs.value.map(tab => tab.title)
    if (!tabTitleMap.includes(targetTab.title)) {
        const newName = `${++tabIndex.value}`;
        tabs.value.push({
            title: targetTab.title,
            name: newName,
            path: targetTab.path
        })
        tabIndex.value = newName;
    }
    tabs.value.forEach(tab => {
        if (tab.title === targetTab.title) {
            const redirectName = tab.name;
            tabIndex.value = redirectName;
        }
    })


}
const removeTab = (targetName) => {
    const tempTabs = tabs.value;
    let activeName = tabIndex.value;
    if (activeName === targetName) {
        tempTabs.forEach((tab, index) => {
            if (tab.name === targetName) {
                const nextTab = tempTabs[index + 1] || tempTabs[index - 1];
                if (nextTab) {
                    activeName = nextTab.name;
                }
            }
        });
        tabIndex.value = activeName;
    }
    tabIndex.value = activeName;
    tabs.value = tempTabs.filter(tab => tab.name !== targetName);
    if (tabIndex !== 2)
        router.back();
}
const changeTab = (targetName) => {
    const tempTabs = tabs.value;
    tempTabs.forEach(tab => {
        if (tab.name === targetName) {
            router.push(tab.path);
        }
    })
}

const exit = () => {
    ElMessageBox.confirm(
        '确定要退出当前账号',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            ElMessage({
                type: 'success',
                message: 'Delete completed',
            })
        })
        .catch(() => {
            ElNotification({
                title: '系统消息',
                message: '登出已取消',
                type: 'info',
            })
        })
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>