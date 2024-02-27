<template>
    <div id="chat">
        <el-container>
            <el-aside>
                <el-row :xs="24" :sm="24" :md="11" :lg="10" :xl="9" class="speparation" />
                <div class="friend-list">
                    <!-- <div class="topbar">

                    </div> -->
                    <div class="content">
                        <ul v-if="data.chats.length !== 0">
                            <li v-for="item in data.chats" :key="item" @click="createChat(item.id)">
                                <img :src="item.avatar" alt="">
                                <span>{{ item.nickname }}</span>
                                <div class="state">
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </el-aside>
            <el-main>
                <router-view />
            </el-main>
        </el-container>
    </div>
</template>
<script setup>
import { reactive, getCurrentInstance, onMounted } from 'vue';
import { ElAside, ElContainer, ElMain, ElRow } from 'element-plus';
import { ObtainFriendList, UserInfo } from '@/utils/request/common.js'
import { getUid } from '@/utils/auth';
import { useStore } from 'vuex';

const uid = getUid();
const store = useStore();
const instance = getCurrentInstance();

const data = reactive({
    chats: []
})

function changeHasMask(states) {
    store.commit("setHasMask", states);
}

function asyncChangeHasMask(secondes) {
    store.dispatch("updateHasMask", secondes)
}

async function init() {
    const res = await ObtainFriendList(uid);
    if (res.meta.code === 226)
        data.chats = res.data.friendIds;
    data.chats.forEach(async (item, index = 0) => {
        const user = await UserInfo(item)
        if (user.meta.code === 200) {
            data.chats[index] = user.data;
        }
        index + 1;
    })
}

onMounted(() => {
    changeHasMask(true);
    init();
    asyncChangeHasMask(500);
})

const createChat = (id) => instance.proxy.$router.push(`/chat/group?to=${id}&type=private`)
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>