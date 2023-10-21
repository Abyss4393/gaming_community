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
                        <el-row style="height: 20rem;">
                            <el-col :span="2"><span>内容:</span></el-col>
                            <el-col :span="22">
                                <quill-editor v-model:value="data.quillEditor.content"
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
    </div>
</template>
<script setup>
import { getCurrentInstance, h, reactive, ref } from 'vue';
import { ElCard, ElForm, ElRow, ElCol, ElRadio, ElButton } from 'element-plus';
import { quillEditor } from 'vue3-quill'


const instance = getCurrentInstance();
const ArticleForm = ref(null);

const uid = instance.proxy.$route.query['author_id'];

const data = reactive({
    article: {
        title: '',
        content: '',
        type: ''
    },
    quillEditor: {
        disabled: false,
        content: '',
        editorOption: {
            placeholder: '有时，jing',
            modules: {
                toolbar: [
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
                    ['clean'],
                    ['link', 'image', 'video']
                ]
            },
        },

    },
    confirm: false
})

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
    console.log('blur', quill);
}

const onEditorFocus = (quill) => {
    console.log('focus', quill);

}

function onEditorReady({ quill, html, text }) {
    console.log('ready');
    console.log(quill, html, text);

}

function onEditorChange(e) {
    console.log('change');
    console.log(data.quillEditor.content);
}


function confirm() {
    data.confirm = !data.confirm;
}

</script>
<style lang="less" scoped>
@import url('./index.less');
</style>