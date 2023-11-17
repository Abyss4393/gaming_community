<template>
    <div id="abyss-article">
        <div class="container">
            <div class="container-main">
                <el-card>
                    <div class="title">
                        <h2>「{{ data.articleData.title }}」</h2>
                    </div>
                    <div class="page-info">
                        <span>来源：{{ data.articleData.type }}</span>
                        <span class="passive">
                            <img :src="require('@/assets/static/icons/message.png')" alt="">
                            {{ data.articleData.passivenessCount }}</span>
                        <span class="positive">
                            <img :src="require('@/assets/static/icons/zan.png')" alt="">
                            {{ data.articleData.positivenessCount }}</span>
                        <span class="collect">
                            <img :src="require('@/assets/static/icons/collection.png')" alt="">
                            {{ data.articleData.collectCount }}</span>
                    </div>
                    <div class="post-timestamp">
                        <span>帖子发布时间</span>
                        <span>{{ data.articleData.postTime }}</span>
                    </div>
                    <div class="content">
                        <div class="content-title">
                            <span>{{ data.articleData.title }}</span>
                        </div>
                        <div class="content-des">
                            <span>{{ data.articleData.contentDes }}</span>
                        </div>
                        <div class="content-item">
                            <div>
                                <img v-for="item, index in data.articleData.content.imageList" :key="index" :src="item.url"
                                    alt="">
                            </div>
                        </div>
                        <div class="content-text" v-if="data.articleData.content.text != null"
                            v-html="data.articleData.content.text"></div>
                    </div>
                    <div class="footer">
                        <div class="left">
                            <img :src="require('@/assets/static/icons/zan.png')" alt="">
                            <span>{{ data.articleData.passivenessCount }}</span>
                        </div>
                        <div class="right">
                            <img :src="require('@/assets/static/icons/collection.png')" alt="">
                            <span>{{ data.articleData.positivenessCount }}</span>
                        </div>
                    </div>

                </el-card>
                <el-card class="reply">
                    <el-row style="height: auto;">
                        <el-col>
                            <span>上善若水，水善利万物而不争，处众人之所恶，故几于道</span>
                        </el-col>
                        <el-col style="margin-bottom: 8rem;" :span="24"><quill-editor theme="bubble" ref="Quill"
                                v-model:value="data.quillEditor.content" :options="data.quillEditor.editorOption"
                                :disabled="data.quillEditor.disabled" @blur="onEditorBlur($event)"
                                @focus="onEditorFocus($event)" @ready="onEditorReady($event)"
                                @change="onEditorChange($event)" />
                        </el-col>
                    </el-row>
                    <el-button type="primary" @click="submitReply">评论</el-button>
                </el-card>
            </div>
            <div class="container-sub">
                <el-card class="article-page-author">
                    <img :src="data.posterData.avatar" alt="">
                    <div class="article-author-info">
                        <h4>{{ data.posterData.nickname }}</h4>
                    </div>
                </el-card>
            </div>
        </div>
        <div class="article-actions">
            <div class="article-ations-item" v-for="item, index in data.ationIcons" @click="handler(index)">
                <img :src="item" alt="">
                <span :title="item">0</span>
            </div>
        </div>
        <div class="emoji" v-show="data.isShowEmoji">
            <emoji-slider @addEmoji="addEmoji" />
        </div>
    </div>
</template>
<script setup>
import { reactive, onMounted, getCurrentInstance } from 'vue';
import { ElCard, ElRow, ElCol } from 'element-plus';
import { quillEditor } from 'vue3-quill';
import EmojiSlider from '@/components/emoji-slider/index.vue';
import { AsyncArticleById } from '@/utils/request/common.js';

const instance = getCurrentInstance();

const data = reactive({
    articleId: null,
    articleData: {
        title: '',
        contentDes: '',
        content: {},
        type: '',
    },
    posterData: {
        avatar: '',
        nickname: '',
    },
    ationIcons: [
        require('@/assets/static/icons/message.png'),
        require('@/assets/static/icons/collection.png'),
        require('@/assets/static/icons/zan.png'),
    ],
    isShowEmoji: false,
    quillEditor: {
        disabled: false,
        content: '',
        editorOption: {
            placeholder: '~~输入你的评论!',
            modules: {
                toolbar: [
                    ['emoji'],
                    ['image'],
                ]
            }
        },
    },
})

onMounted(async function init() {
    data.articleId = instance.proxy.$route.params.id;
    const res = await AsyncArticleById(data.articleId);
    if (res.meta.code === 200) {
        data.articleData = res.data;
        data.posterData.avatar = res.data.posterData.avatar;
        data.posterData.nickname = res.data.posterData.nickname;
    }
    console.log(data.articleData);
})


onMounted(() => {
    const emojiBotton = document.querySelector('.ql-emoji');
    emojiBotton.innerHTML = '<svg t="1699341670577" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5270" width="32" height="32"><path d="M512 992C247.3 992 32 776.7 32 512S247.3 32 512 32s480 215.3 480 480-215.3 480-480 480z m0-896C282.6 96 96 282.6 96 512s186.6 416 416 416 416-186.6 416-416S741.4 96 512 96z" fill="#2c2c2c" p-id="5271"></path><path d="M512 800c-78 0-151.1-30.7-205.7-86.5C253.2 659.4 224 587.8 224 512c0-17.7 14.3-32 32-32h512c17.7 0 32 14.3 32 32 0 75.8-29.2 147.4-82.3 201.5C663.1 769.3 590 800 512 800zM352 668.8c42.5 43.4 99.3 67.2 160 67.2s117.5-23.9 160-67.2c33.7-34.4 55-77.9 61.7-124.8H290.3c6.6 46.9 28 90.3 61.7 124.8zM368 416c-26.5 0-48-21.5-48-48s21.5-48 48-48 48 21.5 48 48-21.5 48-48 48zM656 416c-26.5 0-48-21.5-48-48s21.5-48 48-48 48 21.5 48 48-21.5 48-48 48z" fill="#2c2c2c" p-id="5272"></path></svg>';
    emojiBotton.addEventListener('click', () => showEmoji())
})

const showEmoji = () => {
    data.isShowEmoji = !data.isShowEmoji;
}

const addEmoji = (emoji) => {
    instance.proxy.$nextTick(() => {
        let index = data.quillEditor.content.lastIndexOf('<');
        data.quillEditor.content = data.quillEditor.content.slice(0, index) + emoji + data.quillEditor.content.slice(index);
    })
    data.isShowEmoji = !data.isShowEmoji;
}

//评论
const submitReply = () => {

}

const onEditorBlur = (quill) => {
    console.log(data.quillEditor.content);
}

const onEditorFocus = (quill) => {
}

function onEditorReady({ quill, html, text }) {

}

function onEditorChange(e) {
}

const handler = (index) => {
    console.log(index);
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>