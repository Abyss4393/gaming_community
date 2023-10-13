<template>
    <div id="text">
        {{ title }}
        <div v-html="renderText">

        </div>
    </div>
</template>
<script setup>
let socket;
const title = 'nmz';
const renderText = ''

const init = () => {
    
    if (typeof (WebSocket) == "undefined")
        console.log("该浏览器不兼容websocket");
    else {
        console.log("浏览器兼容websocket");
        let socketUrl = `ws://localhost:2766/server/personal_chat/${title}`;
        if (socket != null) {
            socket.close();
            socket = null;
        }

        socket = new WebSocket(socketUrl);
        socket.onopen = function () {

        };
        socket.onmessage = function (msg) {
            renderText = msg.data
            console.log("msg", msg.data);;
        };
    }

}
init()
</script>