import { global } from "@/main";
import { getUid } from "../auth";
import { ws_heartCheck } from "../websocket";

const root = Symbol('root')

const currentId = getUid();
const host = global.$config.websocket.config.host;
const port = global.$config.websocket.config.port;
const rootURL = global.$config.websocket.config.url;
const hasSSL = global.$config.websocket.config.hasSSL;

export class AbyssWS {

    constructor({ type: type, to: tid }) {
        this[root] = {
            host: host,
            port: port,
            url: rootURL[type],
            from: currentId,
            to: tid,
            hasSSL: hasSSL,
            websocket: null
        }
        this._heartCheck = ws_heartCheck;
        this.pool = {};
    }


    _onopen = e => {
        console.log("websocket已连接");
        this._heartCheck.reset().start(this[root.websocket])

    }


    _onmessage = e => {
        if (e.data != null)
            this._heartCheck.reset().start(this[root].websocket);
        if ((e instanceof Object && 7 < e.data.length)) {
            let receiveResult = JSON.parse(e.data)
            let senderId = receiveResult.senderId;
            if (this[root].from !== senderId) {
                this.handlerReceived(e);
            }
        };
    }

    _onclose = e => {
        console.log("websocket重新连接");
    }

    _onerror = e => { }
    /**
     * 收到服务端数据的事件
     * @param {Object} msg 
     */

    handlerReceived = () => { }

    /**
     * 
     * 开启websocket服务
     * @param {*} onReceived  传递一个处理函数
     * @returns 
     */
    open = (onReceived) => {
        return new Promise((resovle, reject) => {
            if (typeof (WebSocket) == 'undefined')
                reject("浏览器不兼容websocket!")
            let _this = this[root];
            let _server = _this.hasSSL ? 'wss://' : 'ws://';
            _server += _this.host;
            _server += ':';
            _server += _this.port;
            _server += _this.url;
            _server += _this.from;
            _server += '/';
            _server += _this.to;
            try {
                _this.websocket = new WebSocket(_server);
                this.handlerReceived = onReceived;
            } catch (err) {
                reject(err);
            }
            _this.websocket.onopen = (e) => {
                this._onopen(e);
                resovle(e);
            };
            _this.websocket.onmessage = (e) => this._onmessage(e);
            _this.websocket.onclose = (e) => this._onclose(e);
            _this.websocket.onerror = (e) => {
                this._onerror(e);
                reject(e);
            }

        })
    }

    /**
     * 创建消息体
     * @param {Object} res 
     * @param {String} type 
     * @returns Promise
     */
    _createBaseMessageBody = (res, type) => {
        return new Promise((resolve, reject) => {
            if (!res instanceof Object && typeof (res.type) == 'string')
                reject('请求参数错误！')
            let sfn, ffn;
            sfn = res['success'];
            ffn = res['fail'];
            if (type === res.type) {
                let _this = this[root];
                let sendData = JSON.stringify({
                    type: res.type,
                    content: res.content
                })
                if (_this.websocket.readyState === WebSocket.OPEN)
                    _this.websocket.send(sendData);
                resolve(sfn());
            } else {
                reject(ffn());
            }
        })
    }


    createTextMessage = res => {
        return this._createBaseMessageBody(res, 'text');
    }

    createImageMessage = res => {
        return this._createBaseMessageBody(res, 'image');
    }

    createVideoMessage = res => {
        return this._createBaseMessageBody(res, 'video');
    }

    createAudioMessage = res => {
        return this._createBaseMessageBody(res, 'audio');
    }
    close = function () {
        if (this[root].websocket) {
            this[root].websocket.close();
            this[root].websocket = null;
        }
    }
}