<template>
    <div id="abyss-group-chat">
        <div class="chat-container">
            <div class="chat-title">
                <img :src="data.group.avatar" class="chat-avatar" />
                <div class="chat-name">{{ data.group.name }}</div>
            </div>
            <div class="chat-main" ref="scrollView">
                <div class="message-list" ref="messageList">
                    <div v-if="data.history.loading" class="history-loading">
                        <img :src="require('@/assets/static/icons/pending.gif')" />
                    </div>
                    <div v-else :class="data.history.allLoaded ? 'history-loaded' : 'load'"
                        @click="loadHistoryMessage(false)">
                        {{ data.history.allLoaded ? '已经没有更多的历史消息' : '获取历史消息' }}
                    </div>
                    <div v-for="(message, index) in data.history.messages" :key="index">
                        <div class="time-tips">{{ compution.renderMessageDate(message, index) }}</div>
                        <div class="message-recalled" v-if="message.recalled">
                            <div v-if="message.recaller.id === data.currentUser.id" class="message-recalled-self">
                                <div>你撤回了一条消息</div>
                                <span v-if="message.type === 'text' && Date.now() - message.timestamp < 60 * 1000"
                                    @click="editRecalledMessage(message.payload.text)">重新编辑</span>
                            </div>
                            <div v-else>{{ message.recaller.data.name }}撤回了一条消息</div>
                        </div>
                        <div class="message-item" v-else>
                            <div class="message-item-checkbox"
                                v-if="messageSelector.visible && message.status !== 'sending'">
                                <input class="input-checkbox" type="checkbox" :value="message.messageId"
                                    v-model="messageSelector.ids" @click="selectMessages">
                            </div>
                            <div class="message-item-content" :class="{ self: message.senderId === data.currentUser.id }">
                                <div class="sender-info">
                                    <img v-if="data.currentUser.id === message.senderId"
                                        :src="require(data.currentUser.avatar)" class="sender-avatar" />
                                    <img v-else :src="message.senderData.avatar" class="sender-avatar" />
                                </div>
                                <div class="message-content" @click.right="showActionPopup(data.message)">
                                    <div class="message-payload">
                                        <div class="pending" v-if="message.status === 'sending'"></div>
                                        <div class="send-fail" v-if="message.status === 'fail'"></div>
                                        <div v-if="message.type === 'text'" class="content-text"
                                            v-html="data.emoji.decoder.decode(message.payload.text)"></div>
                                        <div v-if="message.type === 'image'" class="content-image"
                                            @click="showImagePreviewPopup(message.payload.url)">
                                            <img :src="require(data.message.payload.thumbnail)" />
                                        </div>
                                        <a v-if="data.message.type === 'file'" :href="data.message.payload.url"
                                            target="_blank" download="download">
                                            <div class="content-file" title="点击下载">
                                                <div class="file-info">
                                                    <span class="file-name">{{ data.message.payload.name }}</span>
                                                    <span class="file-size">{{ (data.message.payload.size / 1024).toFixed(2)
                                                    }}KB</span>
                                                </div>
                                                <img class="file-img"
                                                    :src="require('@/assets/static/icons/file-icon.png')" />
                                            </div>
                                        </a>
                                        <div v-if="data.message.type === 'audio'" class="content-audio"
                                            @click="playAudio(data.message)">
                                            <div class="audio-facade"
                                                :style="{ width: Math.ceil(data.message.payload.duration) * 7 + 50 + 'px' }">
                                                <div class="audio-facade-bg"
                                                    :class="{ 'play-icon': data.audioPlayer.playingMessage === data.message }">
                                                </div>
                                                <div>{{ Math.ceil(message.payload.duration) || 1 }}<span>"</span></div>
                                            </div>
                                        </div>
                                        <abyss-video-player v-if="data.message.type === 'video'"
                                            :thumbnail="data.message.payload.thumbnail"
                                            :src="data.message.payload.video.url" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="chat-footer">
                <div class="action-delete" v-if="data.messageSelector.visible">
                    <img class="delete-btn" :src="require('@/assets/static/icons/delete.png')"
                        @click="deleteMultipleMessages" />
                    <div>删除</div>
                </div>
                <div class="action-box" v-else>
                    <div class="action-bar">
                        <!-- 表情 -->
                        <div class="action-item">
                            <div v-if="data.emoji.visible" class="emoji-box">
                                <img v-for="(emojiItem, emojiKey, index) in data.emoji.map" class="emoji-item" :key="index"
                                    :src="emoji.url + emojiItem" @click="chooseEmoji(emojiKey)" />
                            </div>
                            <i class="iconfont icon-smile" title="表情" @click="showEmojiBox"></i>
                        </div>
                        <!-- 图片 -->
                        <div class="action-item">
                            <label for="img-input">
                                <i class="iconfont icon-picture" title="图片"></i>
                            </label>
                            <input v-show="false" id="img-input" accept="image/*" multiple type="file"
                                @change="sendImageMessage" />
                        </div>
                        <!-- 视频 -->
                        <div class="action-item">
                            <label for="video-input"><i class="iconfont icon-film" title="视频"></i></label>
                            <input v-show="false" id="video-input" accept="video/*" type="file"
                                @change="sendVideoMessage" />
                        </div>
                        <!-- 文件 -->
                        <div class="action-item">
                            <label for="file-input">
                                <i class="iconfont icon-wj-wjj" title="文件"></i>
                            </label>
                            <input v-show="false" id="file-input" type="file" @change="sendFileMessage" />
                        </div>
                        <!-- 自定义-订单消息 -->
                        <div class="action-item">
                            <i class="iconfont icon-liebiao" title="订单" @click="showOrderMessageList"></i>
                        </div>
                    </div>

                    <!-- GoEasyIM最大支持3k的文本消息，如需发送长文本，需调整输入框maxlength值 -->
                    <div class="input-box">
                        <textarea ref="input" @focus="onInputFocus" @keyup.enter="sendTextMessage" v-model="text"
                            maxlength="700" autocomplete="off" class="input-content"></textarea>
                    </div>
                    <div class="send-box">
                        <button class="send-button" @click="sendTextMessage">发送</button>
                    </div>
                </div>
            </div>
            <!-- 语音播放器 -->
            <audio ref="audioPlayer" @ended="onAudioPlayEnd" @pause="onAudioPlayEnd"></audio>
            <!-- 图片预览弹窗 -->
            <div v-if="data.imagePreview.visible" class="image-preview">
                <img :src="require(data.imagePreview.url)" alt="图片" />
                <span class="close" @click="hideImagePreviewPopup">×</span>
            </div>
            <!-- 消息删除撤回弹窗 -->
            <div class="action-popup" v-if="data.actionPopup.visible" @click="data.actionPopup.visible = false">
                <div class="action-popup-main">
                    <div class="action-item" @click="deleteSingleMessage">删除</div>
                    <div class="action-item" v-if="data.actionPopup.recallable" @click="recallMessage">撤回</div>
                    <div class="action-item" @click="showCheckBox">多选</div>
                    <div class="action-item" @click="data.actionPopup.visible = false">取消</div>
                </div>
            </div>
        </div>
    </div>
</template>
  
<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue';
import { useStore } from 'vuex';
import { AbyssWS } from '@/utils/abyss/index'
import abyssVideoPlayer from '@/components/abyss-video-player/index'

const scrollView = ref(null);
const store = useStore();

const IMAGE_MAX_WIDTH = 200;
const IMAGE_MAX_HEIGHT = 150;
const IN_STANCE_GROUP = 'group';

var data = reactive({
    currentUser: null,
    group: {
        id: 29901,
        name: '薄昼交流群',
        avatar: require('@/assets/static/resource/default.png')
    },

    to: {},//用于创建消息时传入

    history: {
        messages: [],
        allLoaded: false,
        loading: true
    },

    text: '',

    //定义表情列表
    emoji: {
        url: null,
        map: null,
        visible: false,
        decoder: null,
    },
    // 图片预览弹出框
    imagePreview: {
        visible: false,
        url: ''
    },
    audioPlayer: {
        playingMessage: null,
    },
    // 展示消息删除弹出框
    actionPopup: {
        visible: false,
        message: null,
        recallable: false,
    },
    // 消息选择
    messageSelector: {
        visible: false,
        ids: []
    },
})

const socket = new AbyssWS(data.group.id);



const sendmesg = async () => {
    const data = await socket.createTextMessage({
        type: 'text',
        content: {
            text: '打得我区分开'
        },
        success: (e) => {
            console.log(e);
        },
        fail: (e) => {
            console.log(e);
        }
    })
    console.log("data",data);
}

sendmesg()
onMounted(() => {
    let user = store.getters["user/getUserInfo"];

    data.currentUser = user.data;
    data.to = {
        type: IN_STANCE_GROUP,
        id: data.group.id,
        data: { name: data.group.name, avatar: data.group.avatar }
    }
    loadHistoryMessage(true);

})




onUnmounted(() => {
    socket.close();
})
const compution = computed({
    renderMessageDate: function (message, index) {
        if (index === 0) {
            return '';
        } else {
            if (data.message.timestamp - data.history.messages[index - 1].timestamp > 5 * 60 * 1000) {
                return '';
            }
        }
        return '';
    }
})
const onReceivedGroupMessage = function (message) {
    let groupId = message.groupId;
    if (groupId === data.group.id) {
        data.history.messages.push(message);
        markGroupMessageAsRead();
    }
    scrollToBottom();
};
/**
 * 核心就是设置高度，产生明确占位
 *
 * 小  (宽度和高度都小于预设尺寸)
 *    设高=原始高度
 * 宽 (宽度>高度)
 *    高度= 根据宽度等比缩放
 * 窄  (宽度<高度)或方(宽度=高度)
 *    设高=MAX height
 *
 * @param width,height
 * @returns number
 */
const getImageHeight = function (width, height) {
    if (width < IMAGE_MAX_WIDTH && height < IMAGE_MAX_HEIGHT) {
        return height;
    } else if (width > height) {
        return IMAGE_MAX_WIDTH / width * height;
    } else if (width === height || width < height) {
        return IMAGE_MAX_HEIGHT;
    }
};
// const playAudio = function (audioMessage) {
//     let playingMessage = data.audioPlayer.playingMessage;
//     if (playingMessage) {
//         instance.proxy.$refs.audioPlayer.pause();
//         // 如果点击的消息正在播放，就认为是停止播放操作
//         if (playingMessage === audioMessage) {
//             return;
//         }
//     }
//     data.audioPlayer.playingMessage = audioMessage;
//     instance.proxy.$refs.audioPlayer.src = audioMessage.payload.url;
//     instance.proxy.$refs.audioPlayer.load();
//     instance.proxy.$refs.audioPlayer.currentTime = 0;
//     instance.proxy.$refs.audioPlayer.play();
// };
// const onAudioPlayEnd = function () {
//     data.audioPlayer.playingMessage = null;
// };
// const sendTextMessage = function () {
//     if (!data.text.trim()) {
//         console.log('输入为空');
//         return
//     }
//     // 发送消息api
//     // data.goEasy.im.createTextMessage({
//     //     text: data.text,
//     //     to: data.to,
//     //     onSuccess: (message) => {
//     //         data.sendMessage(message);
//     //         data.text = '';
//     //     },
//     //     onFailed: (err) => {
//     //         console.log("创建消息err:", err);
//     //     }
//     // });
// };
// const onInputFocus = function () {
//     data.emoji.visible = false;
// };
// const showEmojiBox = function () {
//     data.emoji.visible = !data.emoji.visible;
// };
// const chooseEmoji = function (emojiKey) {
//     data.text += emojiKey;
//     data.emoji.visible = false;
// };
// const sendImageMessage = function (e) {
//     let fileList = [...e.target.files];
//     fileList.forEach((file) => {
//         //发送图片api
//     })
// };
// const sendVideoMessage = function (e) {
//     const file = e.target.files[0];
//     // 发送音频api
// };
// const sendFileMessage = function (e) {
//     const file = e.target.files[0];
//     // 发送文件api
// };
// const showOrderMessageList = function () {
//     data.orderList.orders = null;
//     data.orderList.visible = true;
// };
// const sendOrderMessage = function (order) {
//     data.orderList.visible = false;
//     //
// };
// const sendMessage = function (message) {
//     data.history.messages.push(message);
//     data.scrollToBottom();
//     //
// };
// const showActionPopup = function (message) {
//     const MAX_RECALLABLE_TIME = 3 * 60 * 1000; //3分钟以内的消息才可以撤回
//     data.messageSelector.ids = [message.messageId];
//     if ((Date.now() - message.timestamp) < MAX_RECALLABLE_TIME && message.senderId === data.currentUser.id && message.status === 'success') {
//         data.actionPopup.recallable = true;
//     } else {
//         data.actionPopup.recallable = false;
//     }
//     data.actionPopup.visible = true;
// };
// const deleteSingleMessage = function () {
//     data.actionPopup.visible = false;
//     data.deleteMessage();
// };
// const deleteMultipleMessages = function () {
//     if (data.messageSelector.ids.length > 0) {
//         data.messageSelector.visible = false;
//         data.deleteMessage();
//     }
// };
// const deleteMessage = function () {
//     let conf = confirm("确认删除？");
//     if (conf === true) {
//         let selectedMessages = [];
//         data.history.messages.forEach((message) => {
//             if (data.messageSelector.ids.includes(message.messageId)) {
//                 selectedMessages.push(message);
//             }
//         });
//         // api
//     } else {
//         data.messageSelector.ids = [];
//     }
// };


// const recallMessage = function () {
//     let selectedMessages = [];
//     data.history.messages.forEach((message) => {
//         if (data.messageSelector.ids.includes(message.messageId)) {
//             selectedMessages.push(message);
//         }
//     });
//     data.actionPopup.visible = false;
//     //
// };

// const editRecalledMessage = function (text) {
//     data.text = text;
// };
// const showImagePreviewPopup = function (url) {
//     data.imagePreview.visible = true;
//     data.imagePreview.url = url;
// };
// const hideImagePreviewPopup = function () {
//     data.imagePreview.visible = false;
// };
// const showCheckBox = function () {
//     data.messageSelector.ids = [];
//     data.messageSelector.visible = true;
//     data.actionPopup.visible = false;
// };
// const selectMessages = function (e) {
//     if (e.target.checked) {
//         data.messageSelector.ids.push(e.target.value)
//     } else {
//         let index = data.messageSelector.ids.indexOf(e.target.value);
//         if (index > -1) {
//             data.messageSelector.ids.splice(index, 1);
//         }
//     }
// };
const loadHistoryMessage = function (scrollToBottom) {
    data.history.loading = true;
    //历史消息
    let lastMessageTimeStamp = null;
    let lastMessage = data.history.messages[0];
    if (lastMessage) {
        lastMessageTimeStamp = lastMessage.timestamp;
    }
    //
};
// //标记群聊已读成功
// const markGroupMessageAsRead = function () {
//     // data.goEasy.im.markMessageAsRead({
//     //     id: data.to.id,
//     //     type: data.to.type,
//     //     onSuccess: function () {
//     //         console.log('标记群聊已读成功');
//     //     },
//     //     onFailed: function (error) {
//     //         console.log('标记群聊已读失败', error);
//     //     },
//     // });
// };
const scrollToBottom = function () {
    scrollView.proxy.$nextTick(() => {
        scrollView.value.scrollTop = scrollView.value.messageList.scrollHeight;
    });
};

</script>
<style lang="less" scoped>
@import url("./index.less");
</style>