<template>
    <div id="dialogue">
        <div v-for="item in 300">dawddad</div>
        <!-- <JwChat ref="jwChat" v-model="data.msg" :config="data.config" :taleList="data.renderList" scrollType="scroll"
            :toolConfig="toolConfig" :placeholder="placeholder" :quickList="quickList" @enter="bindEnter"
            @clickTalk="talkEvent">
        </JwChat> -->
    </div>
</template>
<script setup>
import { reactive } from 'vue';
import { useRoute, onBeforeRouteUpdate } from 'vue-router';
import { init, ws_heartCheck } from '@/utils/websocket';
import { QuickTextLists, ToolBarConfig } from './index'
const data = reactive({
    uid: '',
    msg: '',
    config: {
        historyConfig: {
            show: false,
            tip: "加载更多提示框,可以直接使用组件的",
            callback: null,
        }
    },
    renderList: [],
    quickList: QuickTextLists,
    toolConfig: ToolBarConfig,
    placeholder: ''
})
const route = useRoute()
onBeforeRouteUpdate(to => {
    console.log(to.query.id);
    data.uid = route.params.id
})



// event1

const bindEnter = function (e) {
    console.log(data.msg);
}

// event2
const toolEvent = function (type, plyload) {
    console.log(type, plyload);
}

// event3
const talkEvent = function (plyload) {
    console.log(plyload);
}



const socket = init("/server/personal_chat/", data.uid instanceof Number ? data.uid : 0);
const chckedHeath = ws_heartCheck;
socket.onopen = async () => {
    console.log("websocket已连接！");
}

socket.onmessage = async (msg) => {
    var states = msg.data;
    if (states == 'waiting') {
        chckedHeath.reset()
    } else {
        console.log(JSON.parse(msg.data));
    }
    chckedHeath.start(socket);

}
socket.onclose = async (msg) => {
    console.log("websocket已关闭", msg);
}
socket.onerror = async (e) => {
    console.log("服务端发生错误：", e);
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>