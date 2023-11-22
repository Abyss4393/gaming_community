<template>
    <div id="abyss-new-article">
        <div class="container">
            <el-card class="content">
                <span class="crumbs">> 发布帖子</span>
                <div class="editor">
                    <el-form ref="ArticleForm" :model="data.article" :rules="rules">
                        <el-row>
                            <el-col :span="2"><span>标题:</span></el-col>
                            <el-col :span="22">
                                <el-form-item prop="title">
                                    <el-input type="text" placeholder="标题(必填)" v-model="data.article.title" maxlength="50"
                                        show-word-limit />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row style="height: auto;">
                            <el-col :span="2"><span>内容:</span></el-col>
                            <el-col :span="22">
                                <quill-editor ref="Quill" v-model:value="data.quillEditor.content"
                                    :options="data.quillEditor.editorOption" :disabled="data.quillEditor.disabled"
                                    @blur="onEditorBlur($event)" @focus="onEditorFocus($event)"
                                    @ready="onEditorReady($event)" @change="onEditorChange($event)" />
                            </el-col>
                        </el-row>
                        <el-row style="margin-top: 5rem;">
                            <el-col :span="2"><span>类型:</span></el-col>
                            <el-col :span="7">
                                <el-radio v-model="data.article.type" label="资讯">资讯</el-radio>
                            </el-col>
                            <el-col :span="7" :pull="4">
                                <el-radio v-model="data.article.type" label="攻略">攻略</el-radio>
                            </el-col>
                            <el-col :span="8" :pull="8">
                                <el-radio v-model="data.article.type" label="新奇">新奇</el-radio>
                            </el-col>
                        </el-row>
                    </el-form>
                    <div class="confirm">
                        <el-button type="primary" round :loading="data.confirm" @click="confirm">发布</el-button>
                    </div>
                </div>
            </el-card>
        </div>
        <div class="emoji" v-show="data.isShowEmoji">
            <emoji-slider @addEmoji="addEmoji" />
        </div>
    </div>
</template>
<script setup>
import { getCurrentInstance, onMounted, reactive, ref, unref } from 'vue';
import { useStore } from 'vuex';
import { ElCard, ElForm, ElRow, ElCol, ElRadio, ElButton, ElMessage } from 'element-plus';
import { quillEditor } from 'vue3-quill'
import EmojiSlider from '@/components/emoji-slider/index'
import { PostArticle } from '@/utils/request/common';

const instance = getCurrentInstance();
const ArticleForm = ref(null);
const Quill = ref(null);
const userInfo = useStore().getters['user/getUserInfo'].data;

const data = reactive({
    article: {
        title: '',
        type: ''
    },
    isShowEmoji: false,
    quillEditor: {
        disabled: false,
        content: '',
        editorOption: {
            placeholder: '输入帖子内容',
            modules: {
                toolbar: [
                    ['emoji'],
                    ['bold', 'italic', 'underline', 'strike'],
                    ['blockquote'],
                    [{ header: 1 }, { header: 2 }],
                    [{ indent: '-1' }, { indent: '+1' }],
                    [{ direction: 'rtl' }],
                    [{ size: ['small', false, 'large', 'huge'] }],
                    [{ header: [1, 2, 3, 4, 5, 6, false] }],
                    [{ color: [] }, { background: [] }],
                    [{ font: [] }],
                    [{ align: [] }],
                    ['link', 'image', 'video'],
                    ['clean'],
                ]

            }
        },

    },
    confirm: false
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


function validateTitle(rule, value, callback) {
    if (value === '')
        return callback(new Error('标题必填'))
    if (value.length > 50)
        return callback(new Error('标题长度不超过50个字符'))
    callback()
}


const rules = {
    title: [
        { validator: validateTitle, trigger: 'blur' }
    ],
};


const onEditorBlur = (quill) => {
    console.log(data.quillEditor.content);
}

const onEditorFocus = (quill) => {
}

function onEditorReady({ quill, html, text }) {

}

function onEditorChange(e) {
}


async function confirm() {
    data.confirm = !data.confirm;
    unref(ArticleForm).validate(async valid => {
        if (valid) {
            const res = await PostArticle({
                posterName: userInfo.nickname,
                posterId: userInfo.id,
                title: data.article.title,
                content: JSON.stringify({
                    contentList: [{
                        text: data.quillEditor.content
                    }]
                }),
                type: data.article.type
            });
            if (res.meta.code === 245) {
                ElMessage.success(res.meta.msg);
                Object.keys(data.article).forEach(key => data.article[key] = '');
                data.quillEditor.content = ''
            }
        } else ElMessage.error('未通过校检，请重试！')
    })
    setTimeout(() => data.confirm = !data.confirm, 500);
}

</script>
<style lang="less" scoped>
@import url('./index.less');
</style>