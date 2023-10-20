<template>
    <div id="abyss-new-article">
        <div class="container">
            <el-card class="content">
                <span class="crumbs">> 发布帖子</span>
                <div class="editor">
                    <el-form ref="form" :model="data.article" :rules="rules">
                        <el-row>
                            <el-col :span="4" :push="2"><span>标题:</span></el-col>
                            <el-col :span="16">
                                <el-form-item prop="title">
                                    <el-input type="text" placeholder="标题(必填)" v-model="data.article.title" maxlength="50"
                                        show-word-limit />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="4" :push="2"><span>内容:</span></el-col>
                            <el-col :span="16">
                                <!-- <rich-text></rich-text> -->
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="2" :push="2"><span>类型:</span></el-col>
                            <el-col :span="6" :push="4">
                                <el-radio v-model="data.article.type" label="资讯">资讯</el-radio>
                            </el-col>
                            <el-col :span="6">
                                <el-radio v-model="data.article.type" label="攻略">攻略</el-radio>
                            </el-col>
                            <el-col :span="6" :pull="4">
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
import { getCurrentInstance, onMounted, reactive, ref } from 'vue';
import { ElCard, ElForm, ElRow, ElCol, ElRadio, ElButton } from 'element-plus';
import richText from '@/components/rich-text/index'


const instance = getCurrentInstance();
const form = ref(null);

const uid = instance.proxy.$route.query['author_id'];

const data = reactive({
    article: {
        title: '',
        content: '',
        type: ''
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


function confirm() {
    data.confirm = !data.confirm;
}

</script>
<style lang="less" scoped>
@import url('./index.less');
</style>