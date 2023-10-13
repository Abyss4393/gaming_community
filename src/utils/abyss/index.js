import { getUid } from "../auth";
import { ws_heartCheck } from "../websocket";

export class AbyssWS {

    constructor(id) {
        this._server = `ws://localhost:2766/server/common_chat/${getUid()}/${id}`;
        this._socket = null;
        this._heartCheck = ws_heartCheck;
        this._pool = {};
    }

    _onopen = (onOpen) => (e) => { onOpen(e) }

    _onmessage = onMessage => (e) => {
        this._onReceiveMessage()
        if ((e instanceof Object && null != e.data)) {
            this._heartCheck.reset();
            if (null == this._pool['sys']) {
                this._pool['sys'] = e.data

                onMessage(e)
            }
            if ('waiting' !== e.data) {
                this._pool['temp'] = JSON.parse(e.data)

            }

        }
        this._heartCheck.start(this._socket)
    }

    _onclose = onClose => (e) => { onClose(e) }

    _onerror = onError => (e) => { onError(e) }

    _onReceiveMessage = onReceive => e => { onReceive(e) }

    _init = async (onMessage, onClose, onError) => {
        return new Promise((resovle, reject) => {
            if (typeof (WebSocket) == 'undefined')
                reject("浏览器不兼容websocket!")
            try {
                this._socket = new WebSocket(this._server);
            } catch (err) {
                reject(err);
            }
            console.log("websocket已连接!");
            this._socket.onopen = this._onopen(resovle);
            this._socket.onmessage = this._onmessage(onMessage);
            this._socket.onclose = this._onclose(onClose)
            this._socket.onerror = this._onerror(onError);
        })
    }



    _baseSendMessage = async (res, type) => {
        return new Promise((resolve, reject) => {
            if (!res instanceof Object)
                reject('请求体错误')
            let sfn, ffn;
            sfn = res['success'];
            ffn = res['fail'];
            if (typeof (res.type) == 'string' && type === res.type) {
                this._init(() => {
                    this._socket.send(JSON.stringify({
                        type: res.type,
                        content: res.content
                    }))
                }, sfn, ffn);
                resolve({
                    data: this._pool,
                    meta: {
                        code: 200,
                        msg: '请求成功'
                    }
                })
            } else {
                reject('发送的消息格式错误,请选择正确的类型！')
            }
        })
    }


    destory = function () {
        Object.keys(this._pool).forEach(item => {
            delete this._pool[item];
        })
    }

    createTextMessage = async res => {
        return this._baseSendMessage(res, 'text');
    }

    createImageMessage = async res => {
        return this._baseSendMessage(res, 'image');
    }


    setOnReceiveMessage = (fn) => this._onReceiveMessage = fn();
}