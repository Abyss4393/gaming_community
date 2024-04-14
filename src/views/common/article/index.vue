<template>
    <div id="abyss-article">
        <div class="container">
            <div class="container-main">
                <el-card>
                    <div class="title">
                        <h2>「{{ data.articleData.title }}」</h2>
                    </div>
                    <el-divider content-position="left">
                        <span>帖子内容）</span>
                    </el-divider>
                    <div class="page-info">
                        <span>类型：<el-tag>{{ data.articleData.type }}</el-tag></span>
                        <div class="passive">
                            <img :src="require('@/assets/static/icons/message.png')" alt="">
                            <span>{{ data.articleData.comments.length }}</span>
                        </div>
                        <div class="positive">
                            <img :src="data.upvoted ? require('@/assets/static/icons/zan-active.png') : require('@/assets/static/icons/zan.png')"
                                alt="like">
                            <span>{{ data.articleData.articleLike }}</span>
                        </div>
                        <div class="collect">
                            <img :src="data.collected ? require('@/assets/static/icons/collection-active.png') : require('@/assets/static/icons/collection.png')"
                                alt="collection">
                            <span>{{ data.articleData.collectCount }}</span>
                        </div>
                    </div>
                    <div class="post-timestamp">
                        <span>帖子发表时间</span>
                        <span>{{ time(data.articleData.postTime) }}</span>
                    </div>
                    <div class="content">
                        <div class="content-item" v-if="data.articleData.content">
                            <div class="content-des">
                                <span>简介：{{ data.articleData.contentDes }}</span>
                            </div>
                            <div v-for="item, index in data.articleData.content.contentList" :key="index" :src="index">
                                <div class="content-item-html" v-if="item.text">
                                    <div v-html="item.text"></div>
                                </div>
                                <div class="content-item-image" v-if="item.imageList">
                                    <img v-for="image, imageIndex in item.imageList" :key="imageIndex" :src="image.url"
                                        alt="image">
                                </div>
                                <div v-if="item.videoList">
                                    <video controls width="840" v-for="video, videoIndex in item.videoList"
                                        :key="videoIndex">
                                        <source :src="video.url" type="video/mp4">
                                    </video>
                                </div>
                            </div>
                        </div>
                        <el-divider>
                            <el-icon><star-filled /></el-icon>
                        </el-divider>
                        <div class="content-text" v-if="data.articleData.content.text != null"
                            v-html="data.articleData.content.text"></div>
                    </div>
                    <div class="footer">
                        <div class="left" @click="actionupvoted()">
                            <img :src="data.upvoted ? require('@/assets/static/icons/zan-active.png') : require('@/assets/static/icons/zan.png')"
                                alt="like">
                            <span>{{ data.articleData.articleLike }}</span>
                        </div>
                        <div class="right" @click="actionCollected()">
                            <img :src="data.collected ? require('@/assets/static/icons/collection-active.png') : require('@/assets/static/icons/collection.png')"
                                alt="">
                            <span>{{ data.articleData.collectCount }}</span>
                        </div>
                    </div>
                </el-card>
                <el-card class="reply">
                    <el-row style="height: auto;">
                        <el-col>
                            <span>上善若水，水善利万物而不争，处众人之所恶，故几于道</span>
                        </el-col>
                        <el-col style="margin-bottom: 8rem;"><quill-editor id="commentEditor" theme="bubble" ref="Quill"
                                v-model:value="data.quillEditor.content" :options="data.quillEditor.editorOption"
                                :disabled="data.quillEditor.disabled" />
                        </el-col>
                    </el-row>
                    <el-button type="primary" @click="submitComment()">评论</el-button>
                    <div class="emoji" v-show="data.isShowEmoji">
                        <emoji-slider @addEmoji="addEmoji" />
                    </div>
                </el-card>
                <div class="comments" v-if="data.articleData.comments.length !== 0">
                    <div class="card-header">
                        <div class="show-all">
                            <span>
                                全部评论
                            </span>
                        </div>
                        <div class="sort">
                            <el-dropdown trigger="click" @command="handleCommand">
                                <span>
                                    {{ sortType }}：
                                    <el-icon>
                                        <ArrowDown />
                                    </el-icon>
                                </span>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item v-for="sort, index in sortMenu" :command="index" :key="index">
                                            <span>{{ sort.text }}</span>
                                            <el-icon v-if="sort.focus">
                                                <Check />
                                            </el-icon>
                                        </el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>

                        </div>
                    </div>
                    <el-card v-for="item, itemIndex in data.articleData.comments" :key="itemIndex">

                        <div class="comments-myself">
                            <img :src="item.user.avatar" alt="">
                            <span>
                                <em>{{ item.user.nickname }}</em>
                            </span>
                        </div>
                        <el-divider border-style="double" />
                        <div class="comments-content">
                            <div v-html="item.comment.content"></div>
                        </div>
                        <div class="comment-interact">
                            <div class="intereact-reply-time">
                                <span>{{ time(item.comment.commentTime) }}</span>
                            </div>
                            <div class="intereaction-bar">
                                <span @click="triggerActive('comment', itemIndex)">回复</span>
                                <img :src="require('@/assets/static/icons/zan.png')" alt="reply_avatar">
                                <span>0</span>
                                <img :src="require('@/assets/static/icons/zancopy.png')" alt="">
                                <span>0</span>
                            </div>
                        </div>
                        <div class="delete" v-if="item.user.id == uid">
                            <el-tooltip effect="light" popper-class="popper" placement="bottom-end">
                                <img :src="require('@/assets/static/icons/menu.png')" alt="del">
                                <template #content>
                                    <div style="width: 3rem;cursor: pointer;text-align: center;"
                                        @click="delComment(item.user.id)">删除</div>
                                </template>
                            </el-tooltip>
                        </div>
                        <div class="comment-input" v-if="data.commentActiveIndex == itemIndex">
                            <el-row style="height: auto;">
                                <el-col><quill-editor id="replyEditor" theme="bubble" ref="ReplyQuill"
                                        v-model:value="data.quillEditor.replyContent"
                                        :options="data.quillEditor.replyEditorOption"
                                        :disabled="data.quillEditor.disabled" />
                                </el-col>
                            </el-row>
                            <el-button type="primary" @click="submitReply(item.comment.id)">评论</el-button>
                            <div class="emoji" v-show="data.isShowReplyEmoji">
                                <emoji-slider @addEmoji="addReplyContentEmoji" />
                            </div>
                        </div>
                        <div class="reply-card" v-if="item.replies">
                            <div class="reply-item" v-for="reply, rindex in item.replies" :key="rindex">
                                <div class="reply-user-info">
                                    <div class="reply-user-avatar">
                                        <img :src="reply.user.avatar" alt="">
                                    </div>
                                    <span>{{ reply.user.nickname }}</span>
                                    <span>@{{ item.user.nickname }}</span>
                                    <span>{{ time(reply.replyTime) }}</span>
                                </div>
                                <div class="reply-content" style="padding-left: 4rem; box-sizing: border-box;"
                                    v-html="reply.content" />
                                <div class="reply-intereaction-bar">
                                    <span @click="triggerActive('reply', reply.id)">回复</span>
                                    <span>
                                        <img :src="require('@/assets/static/icons/zan.png')" alt="reply_avatar">
                                    </span>
                                    <span>0</span>
                                    <span>
                                        <img :src="require('@/assets/static/icons/zancopy.png')" alt="">
                                    </span>
                                </div>
                                <div class="opreation" v-if="reply.userId == uid">
                                    <el-tooltip effect="light" popper-class="popper" placement="bottom-end">
                                        <img :src="require('@/assets/static/icons/menu.png')" alt="del">

                                        <template #content>
                                            <div style="width: 3rem;cursor: pointer;text-align: center;"
                                                @click="delReply(reply.userId)">删除</div>
                                        </template>
                                    </el-tooltip>
                                </div>
                                <div class="reply-input" v-if="data.replyActiveIndex == reply.id">
                                    <el-row style="height: auto;">
                                        <el-col><quill-editor theme="bubble" ref="ReplyQuill"
                                                v-model:value="data.quillEditor.replyContent"
                                                :options="data.quillEditor.replyEditorOption"
                                                :disabled="data.quillEditor.disabled" />
                                        </el-col>
                                    </el-row>
                                    <el-button type="primary" @click="submitReply(item.comment.id)">评论</el-button>
                                    <div class="emoji" v-show="data.isShowReplyEmoji">
                                        <emoji-slider @addEmoji="addReplyContentEmoji" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </el-card>
                </div>
            </div>
            <div class="container-sub">
                <el-card class="article-page-author">
                    <a :href="`/abyss/accountCenter/postList?id=${data.posterData.id}`" target="_blank">
                        <img :src="data.posterData.avatar" alt="">
                        <div class="article-author-info">
                            <h4>{{ data.posterData.nickname }}</h4>
                        </div>
                        <div class="follow" v-if="uid != data.posterData.id">
                            <el-button v-if="!data.isFollow" text type="primary" plain round size="large"
                                @click="follow(data.posterData.id)">关注</el-button>
                            <el-button v-else text type="info" round size="large">
                                <span>
                                    已关注
                                </span>
                                <el-icon>
                                    <Check />
                                </el-icon>
                            </el-button>
                        </div>
                    </a>
                </el-card>
            </div>
        </div>
        <div class="article-actions">

            <div class="article-ations-item">
                <img :src="require('@/assets/static/icons/message.png')" alt="">
                <span>{{ data.articleData.comments.length }}</span>
            </div>
            <div class="article-ations-item" @click="actionupvoted()">
                <img :src="data.upvoted ? require('@/assets/static/icons/zan-active.png') : require('@/assets/static/icons/zan.png')"
                    alt="">
                <span>{{ data.articleData.articleLike }}</span>
            </div>
            <div class="article-ations-item" @click="actionDislike()">
                <img :src="data.disliked ? require('@/assets/static/icons/zancopy-active.png') : require('@/assets/static/icons/zancopy.png')"
                    alt="">
                <span>{{ data.articleData.articleDislike }}</span>
            </div>
            <div class="article-ations-item" @click="actionCollected()">
                <img :src="data.collected ? require('@/assets/static/icons/collection-active.png') : require('@/assets/static/icons/collection.png')"
                    alt="">
                <span>{{ data.articleData.collectCount }}</span>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, onMounted, computed, ref, getCurrentInstance } from 'vue';
import { ElCard, ElRow, ElCol, ElMessage, ElTag, ElDropdown, ElDropdownMenu, ElDropdownItem, ElIcon } from 'element-plus';
import { ArrowDown, Check, StarFilled } from '@element-plus/icons-vue'
import { quillEditor } from 'vue3-quill';
import EmojiSlider from '@/components/emoji-slider/index.vue';
import {
    AsyncArticleById, AsyncArticleUpvoteStatus, AsyncArticleDislikeStatus,
    AsyncArticleCollectStatus, PostComment, PostReply, AsyncArticleUpvote,
    AsyncArticleDislike, AsyncArticleCollect, AsyncAticleReplyTrees, AsyncUserFriendStatus,
    CancelUpvote, CancelDislike, CancelCollect, DelCommentByIds, AddFriend, SortComment
} from '@/utils/request/common.js';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import { getPassTime } from '@/utils/timestamp';

const toolBarIcon = '<svg t="1699341670577" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5270" width="32" height="32"><path d="M512 992C247.3 992 32 776.7 32 512S247.3 32 512 32s480 215.3 480 480-215.3 480-480 480z m0-896C282.6 96 96 282.6 96 512s186.6 416 416 416 416-186.6 416-416S741.4 96 512 96z" fill="#2c2c2c" p-id="5271"></path><path d="M512 800c-78 0-151.1-30.7-205.7-86.5C253.2 659.4 224 587.8 224 512c0-17.7 14.3-32 32-32h512c17.7 0 32 14.3 32 32 0 75.8-29.2 147.4-82.3 201.5C663.1 769.3 590 800 512 800zM352 668.8c42.5 43.4 99.3 67.2 160 67.2s117.5-23.9 160-67.2c33.7-34.4 55-77.9 61.7-124.8H290.3c6.6 46.9 28 90.3 61.7 124.8zM368 416c-26.5 0-48-21.5-48-48s21.5-48 48-48 48 21.5 48 48-21.5 48-48 48zM656 416c-26.5 0-48-21.5-48-48s21.5-48 48-48 48 21.5 48 48-21.5 48-48 48z" fill="#2c2c2c" p-id="5272"></path></svg>';
const instance = getCurrentInstance();
const store = useStore();
const loginState = useStore().getters['user/getLoginState'];
const current_id = parseInt(useRoute().query['poster_id']);
const uid = useStore().getters['user/getUserInfo'].data.id;
const data = reactive({
    articleId: null,
    articleData: {
        title: '',
        contentDes: '',
        content: {},
        type: '',
        comments: []
    },
    posterData: {
        id: '',
        avatar: '',
        nickname: '',
    },
    isShowEmoji: false,
    isShowReplyEmoji: false,
    quillEditor: {
        disabled: false,
        content: '',
        replyContent: '',
        editorOption: {
            placeholder: '~~输入你的评论!',
            modules: {
                toolbar: [
                    ['emoji'],
                    ['image'],
                ]
            }
        },
        replyEditorOption: {
            placeholder: '请开始你的表演~',
            modules: {
                toolbar: [
                    ['reply'],
                ]
            }
        },
    },
    isFollow: false,
    upvoted: false,
    disliked: false,
    collected: false,
    commentActiveIndex: -1,
    replyActiveIndex: -1
})


const sortMenu = ref([{
    text: '热门',
    focus: false,
    order: 1,
}, {
    text: '最新',
    focus: false,
    order: 2,
},
{
    text: '最早',
    focus: true,
    order: 3,
}]);

const sortType = ref('最早');

function changeHasMask(states) {
    store.commit("setHasMask", states);
}

function asyncChangeHasMask(secondes) {
    store.dispatch("updateHasMask", secondes)
}
const time = computed(() => {
    return (time) => {
        return getPassTime(time);
    }
})

async function init() {
    const emojiBtn = document.querySelector('.ql-emoji');
    emojiBtn.innerHTML = toolBarIcon;
    emojiBtn.addEventListener('click', () => showEmoji());
    data.articleId = instance.proxy.$route.params.id;
    const res = await AsyncArticleById(data.articleId);
    if (res.meta.code === 200) {
        data.articleData = res.data;
        data.posterData.id = res.data.posterData.id;
        data.posterData.avatar = res.data.posterData.avatar;
        data.posterData.nickname = res.data.posterData.nickname;
        if (loginState) {
            const isFollow = await AsyncUserFriendStatus(uid, data.articleId);
            if (isFollow.meta.code === 200) {
                data.isFollow = isFollow.data.isFriend;
            }
            const upvoteStatus = await AsyncArticleUpvoteStatus(uid, data.articleId);
            if (upvoteStatus.meta.code === 200) {
                data.upvoted = upvoteStatus.data.isUpvote;
            }
            const collectStatus = await AsyncArticleCollectStatus(uid, data.articleId);
            if (collectStatus.meta.code === 200) {
                data.collected = collectStatus.data.isCollect;
            }

            const dislikeStatus = await AsyncArticleDislikeStatus(uid, data.articleId);
            if (dislikeStatus.meta.code === 200) {
                data.disliked = dislikeStatus.data.isDislike;
            }
        }
    }
    for (const item of data.articleData.comments) {
        const cid = item.comment.id;
        const tresponse = await AsyncAticleReplyTrees(data.articleId, cid, -1);
        if (tresponse.meta.code === 200) {
            item.replies = tresponse.data.primary;
        }
    }
    console.log(data.articleData.comments);
}
onMounted(() => {
    changeHasMask(true);
    init();
    asyncChangeHasMask(800);
})


const handleCommand = async (index) => {
    for (let i = 0; i < sortMenu.value.length; i++) {
        sortMenu.value[i].focus = false;
        if (i === index) {
            sortMenu.value[i].focus = true;
            let type = sortMenu.value[i].order;
            const order = await SortComment(data.articleId, type)
            if (order.meta.code === 200) {
                data.articleData.comments = order.data;
                for (const item of data.articleData.comments) {
                    const cid = item.comment.id;
                    const tresponse = await AsyncAticleReplyTrees(data.articleId, cid, -1);
                    if (tresponse.meta.code === 200) {
                        item.replies = tresponse.data.primary;
                    }
                }
            }
        }


    }
    let type = sortMenu.value[index].text
    sortType.value = type;
}


const showEmoji = () => data.isShowEmoji = !data.isShowEmoji;


const addEmoji = (emoji) => {
    let index = data.quillEditor.content.lastIndexOf('<');
    data.quillEditor.content = data.quillEditor.content.slice(0, index) + emoji + data.quillEditor.content.slice(index);
    instance.proxy.$nextTick(() => {
        const range = document.createRange();
        const selection = window.getSelection();
        let element = document.getElementById('commentEditor')
        let editor = element.querySelector('.ql-editor');
        range.selectNodeContents(editor);
        range.collapse(false);
        selection.removeAllRanges();
        selection.addRange(range);
    })
    data.isShowEmoji = !data.isShowEmoji;
}
const showReplyEmoji = () => data.isShowReplyEmoji = !data.isShowReplyEmoji;

const addReplyContentEmoji = (emoji) => {
    let index = data.quillEditor.replyContent.lastIndexOf('<');
    data.quillEditor.replyContent = data.quillEditor.replyContent.slice(0, index) + emoji + data.quillEditor.replyContent.slice(index);
    instance.proxy.$nextTick(() => {
        const range = document.createRange();
        const selection = window.getSelection();
        let element = document.getElementById('replyEditor')
        console.log(element);
        // let editor = element.querySelector('.ql-editor');
        // range.selectNodeContents(editor);
        // range.collapse(false);
        // selection.removeAllRanges();
        // selection.addRange(range);
    })
    data.isShowReplyEmoji = !data.isShowReplyEmoji
}
// 点赞
const actionupvoted = async function () {
    if (loginState) {
        if (data.upvoted) {
            if (data.articleData.articleLike !== 0) {
                data.articleData.articleLike -= 1;
            }

        } else data.articleData.articleLike += 1;
        if (data.upvoted) {
            const cancel = await CancelUpvote(uid, data.articleId)
            if (cancel.meta.code === 200)
                ElMessage.success('取消点赞成功')
            else ElMessage.info('请勿重复操作')
        } else {
            const upvoted = await AsyncArticleUpvote(uid, data.articleId)
            if (upvoted.meta.code === 241)
                ElMessage.success(upvoted.meta.msg)
            else ElMessage.info('已经点赞过，请勿重复操作')
        }
        data.upvoted = !data.upvoted;
    } else ElMessage.info('请先登录')

}

// 拉踩
const actionDislike = async function () {
    if (loginState) {
        if (data.disliked) {
            if (data.articleData.articleDislike !== 0) {
                data.articleData.articleDislike -= 1;
            }

        } else data.articleData.articleDislike += 1;
        if (data.disliked) {
            const cancel = await CancelDislike(uid, data.articleId)
            if (cancel.meta.code === 200)
                ElMessage.success('取消拉踩成功')
            else ElMessage.info('请勿重复操作')
        } else {
            const disliked = await AsyncArticleDislike(uid, data.articleId)
            if (disliked.meta.code === 243)
                ElMessage.success(disliked.meta.msg)
            else ElMessage.info('已经拉踩过，请勿重复操作')
        }
        data.disliked = !data.disliked;
    } else ElMessage.info('请先登录')

}

// 收藏
const actionCollected = async function () {
    if (loginState) {
        if (data.collected) {
            if (data.articleData.articleCollect !== 0) {
                data.articleData.collectCount -= 1;
            }

        } else data.articleData.collectCount += 1;
        if (data.collected) {
            const cancel = await CancelCollect({
                aid: data.articleId,
                uid: uid
            })
            if (cancel.meta.code === 200)
                ElMessage.success('取消收藏成功')
            else ElMessage.info('请勿重复操作')
        } else {
            const collected = await AsyncArticleCollect({
                aid: data.articleId,
                uid: uid
            })
            if (collected.meta.code === 247)
                ElMessage.success(collected.meta.msg)
            else ElMessage.info('已经收藏过，请勿重复操作')
        }
        data.collected = !data.collected;
    } else ElMessage.info('请先登录')
}

// 评论
const submitComment = async () => {
    if (loginState) {
        if (data.quillEditor.content.length === 0) {
            ElMessage.warning('评论不能为空！')
            return
        }
        const res = await PostComment({
            aid: data.articleId,
            uid: uid,
            content: data.quillEditor.content
        })
        if (res.meta.code === 263) {
            data.quillEditor.content = '';
            ElMessage.success(res.meta.msg);
            init();
        } else ElMessage.info('你已经评论过了！稍安勿躁');
    } else ElMessage.info('请先登录')
}

// 删除
const delComment = async (uid) => {
    await instance.proxy.$confirm('确定删除词条评论，是否继续', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        const res = await DelCommentByIds(data.articleId, uid)
        if (res.meta.code === 223)
            init();
        else instance.proxy.$message.error(res.meta.msg)
    }).catch(err => err)
}

// 删除回复
const delReply = async (uid) => {

}
// 回复
const submitReply = async (commentId = -1, replyId = -1) => {
    if (loginState) {
        if (data.quillEditor.replyContent.length === 0) {
            ElMessage.warning('回复不能为空！')
            return
        }
        const res = await PostReply({
            articleId: data.articleId,
            userId: uid,
            parentCommentId: commentId,
            parentReplyId: replyId,
            content: data.quillEditor.replyContent
        })
        if (res.meta.code === 265) {
            init();
        } else instance.proxy.$message.error(res.meta.msg)
        data.quillEditor.replyContent = '';
    } else ElMessage.info('请先登录')

}

const triggerActive = (type, index) => {
    function trigger() {
        instance.proxy.$nextTick(() => {
            const replyEmojiBtn = document.querySelector('.ql-reply');
            replyEmojiBtn.innerHTML = toolBarIcon;
            replyEmojiBtn.addEventListener('click', () => showReplyEmoji());
        })
    }
    if (type === 'comment') {
        if (data.commentActiveIndex === index) data.commentActiveIndex = -1;
        else {
            data.commentActiveIndex = index;
            trigger();
        }
    }
    if (type === 'reply') {
        if (data.replyActiveIndex === index) data.replyActiveIndex = -1;
        else {
            data.replyActiveIndex = index;
            trigger();
        }
    }
}
// 关注
const follow = async () => {
    if (loginState) {
        const res = await AddFriend(uid, current_id)
        if (!data.isFriend) {
            if (res.meta.code === 271)
                ElMessage.success(res.meta.msg)
            else ElMessage.error(res.meta.msg)
        } else ElMessage.warning('你已经关注对方，无需重复操作')
    } else ElMessage.info('请先登录')

}
</script>

<style lang="less" scoped>
@import url('./index.less');
</style>