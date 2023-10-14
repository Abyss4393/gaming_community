import { global } from '@/main.js'
import { getUid } from "../auth";
const protocol = 'ws://';
const ip = 'localhost:';
const port = global.$config?.port || 2766;
const uid = getUid();

// 初始化 websocekt
export const initws = function (url, toId) {
    let ws_url = `${protocol}` + `${ip}` + `${port}` + url + uid + "/" + toId;
    return new WebSocket(ws_url);
}







// WebSocket心跳检测
export var ws_heartCheck = {
    timeout: 1000 * 60 * 5,			// 10秒一次心跳
    timeoutObj: null,		// 执行心跳的定时器
    serverTimeoutObj: null,	// 服务器超时定时器
    reset: function () {		// 重置方法
        clearTimeout(this.timeoutObj);
        clearTimeout(this.serverTimeoutObj);
        return this;
    },
    start: function (socket) {		// 启动方法
        var _this = this;
        _this.timeoutObj = setTimeout(function () {
            if (socket.readyState === WebSocket.OPEN) {
                // 这里发送一个心跳信息，后端收到后，返回一个消息，在onmessage拿到返回的心跳（信息）就说明连接正常
                socket.send("waiting");
                _this.reset().start(socket);
            }
            // 如果超过一定时间还没重置，说明后端主动断开了
            self.serverTimeoutObj = setTimeout(function () {
                // 如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
                socket.close();
            }, _this.timeout);
        }, _this.timeout);
    }
}
