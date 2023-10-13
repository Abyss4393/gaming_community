<template>
    <div id="friend-list">
        <div class="topbar">

        </div>
        <div class="content">
            <ul v-if="data.friends.length !== 0">
                <li v-for="item in data.friends" :key="item" @click="instance.proxy.$router.push(`/friends?id=${item.id}`)">
                    <img :src="item.avatar" alt="">
                    <span>{{ item.nickname }}</span>
                    <div class="state">
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>
<script setup>
import { reactive, getCurrentInstance } from 'vue';
import { getUid } from '@/utils/auth/index.js'
import { AbatinFriendListAPI, UserInfo } from '@/utils/request/common.js'
const instance = getCurrentInstance();
const data = reactive({
    friends: []
})
const uid = getUid();
const cos = (id) => {
    console.log(id);
}
const listInit = async function () {
    const res = await AbatinFriendListAPI(uid);
    if (res.meta.code === 226)
        data.friends = res.data["friends_id"];
    data.friends.forEach(async (item, index = 0) => {
        const user = await UserInfo(item)
        if (user.meta.code === 200)
            data.friends[index] = user.data
        index + 1;

    })
}
listInit();
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>