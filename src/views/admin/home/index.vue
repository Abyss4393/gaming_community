<template>
    <div id="admin-home">
        <el-container>
            <el-aside width="200px">
                <div class="sys-title">
                    <img src="" alt="">
                    <span title="game community">游戏社区后台管理系统</span>
                </div>
                <el-menu active-text-color="#ffd04b" background-color="#1b435c" :default-active="onRoutes"
                    text-color="#fff">
                    <div v-for="menuItem, index in  menu " :key="index">
                        <el-sub-menu v-if="menuItem.isGroup" :index="menuItem.path">
                            <template #title>
                                <el-icon>
                                    <DataLine />
                                </el-icon>
                                <span>{{ menuItem.groupName }}</span>
                            </template>
                            <el-menu-item-group>
                                <el-menu-item v-for="item, subIndex in menuItem.group" :key="subIndex"
                                    @click.capture="to(item)" :index="item.path">
                                    {{ item.title }}
                                </el-menu-item>
                            </el-menu-item-group>
                        </el-sub-menu>
                        <el-menu-item v-else="!menuItem.isGroup" :index="menuItem.path" @click="to(menuItem)">
                            <el-icon v-if="menuItem.title === '首页'">
                                <House />
                            </el-icon>
                            <el-icon v-if="menuItem.title === '用户管理'">
                                <User />
                            </el-icon>
                            <el-icon v-if="menuItem.title === '帖子管理'">
                                <Document />
                            </el-icon>
                            <el-icon v-if="menuItem.title === '评论管理'">
                                <ChatLineSquare />
                            </el-icon>
                            <el-icon v-if="menuItem.title === '回复管理'">
                                <ChatDotSquare />
                            </el-icon>
                            <el-icon v-if="menuItem.title === '反馈'">
                                <Lightning />
                            </el-icon>
                            <span>{{ menuItem.title }}</span>
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
                                    <img class="back" :src="require('@/assets/static/icons/admin_left.png')"
                                        @click="back()">
                                    <div class="home">
                                        <img :src="require('@/assets/static/icons/admin_home.png')" @click="goIndex()">
                                        <span @click="goIndex()">首页</span>
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
import { getCurrentInstance, ref, computed, nextTick } from 'vue';
import { ElContainer, ElAside, ElHeader, ElMain, ElTabs, ElTabPane, ElMessageBox, ElMessage, ElNotification } from 'element-plus';
import { User, Document, ChatLineSquare, ChatDotSquare, House, Lightning, DataLine } from '@element-plus/icons-vue'

const instance = getCurrentInstance();
const route = instance.proxy.$route;
const router = instance.proxy.$router;
let tabIndex = ref(0)

const menu = ref([
    {
        isGroup: false,
        title: '首页',
        path: '/admin/index'
    },
    {
        isGroup: false,
        title: '用户管理',
        path: '/admin/manage/user'
    }, {
        isGroup: false,
        title: '帖子管理',
        path: '/admin/manage/article'
    }, {
        isGroup: false,
        title: '评论管理',
        path: '/admin/manage/commnet'
    },
    {
        isGroup: false,
        title: '回复管理',
        path: '/admin/manage/reply'
    },
    {
        isGroup: false,
        title: '反馈',
        path: '/admin/manage/12'
    }
]);

const onRoutes = computed({
    get() {
        return route.path;
    }
})


const tabs = ref([])



const to = (target) => {
    router.push(target.path);
    addTab(target);

}

const back = () => {
    if (-1 === tabIndex.value) return;
    else {
        tabIndex.value = --tabIndex.value;
        if (0 === tabIndex.value) router.push('/admin/index')
        else {
            tabs.value.forEach(item => {
                if (item.name === tabIndex.value)
                    router.push(item.path)
            })
        };
    }
}
const goIndex = () => {
    router.push('/admin/index')
    tabIndex.value = -1;
}

const addTab = (targetTab) => {
    if (targetTab.title === '首页') {
        tabIndex.value = -1;
        return;
    }
    const tabTitleMap = tabs.value.map(tab => tab.title);
    if (!tabTitleMap.includes(targetTab.title)) {
        let newName;
        if (tabTitleMap.length === 0) newName = `${++tabIndex.value}`;
        else newName = tabTitleMap.length - 1;
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
    nextTick(() => {
        const tempTabs = tabs.value;
        let activeName = tabIndex.value;
        if (activeName === targetName) {
            tempTabs.forEach((tab, index) => {
                if (tab.name === targetName) {
                    const nextTab = tempTabs[index + 1] || tempTabs[index - 1];
                    if (nextTab) {
                        activeName = nextTab.name;
                        router.push(nextTab.path);
                    } else router.push('/admin/index');
                }
            });
            tabIndex.value = activeName;
        }
        tabIndex.value = activeName;
        tabs.value = tempTabs.filter(tab => tab.name !== targetName);
    })
    console.log('当前', onRoutes);
}
const changeTab = (targetName) => {
    nextTick(() => {
        const tempTabs = tabs.value;
        tempTabs.forEach(tab => {
            if (tab.name === targetName) {
                router.push(tab.path);
            }
        })
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
            router.push('/admin/login')
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