<template>
    <div class="test">
        <input multiple="multiple" type="file" @change="handlerFile"/>
    </div>
</template>
<script setup>
import { reactive } from 'vue';
const socketUrl = "ws://localhost:2766/server/common_chat/1/29901"
const data = reactive({
    type: "image",
    content: {
        image: ''
    }
})
const socket = new WebSocket(socketUrl);
socket.onopen = () => {
    console.log("连接成功");
}
const handlerFile = (e) =>{
    const file = Array.prototype.slice.call(e.target.files)
    var fr = new FileReader();
    
    fr.readAsBinaryString(file[0])
    fr.onload = function(){
        data.content.image = this.result;
        
    }
    socket.send(JSON.stringify(data))
    
}
</script>