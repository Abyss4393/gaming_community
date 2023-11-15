<template>
    <div id="abyss-new-image">
        <div class="container">
            <el-card class="content">
                <span class="crumbs">> 发布有趣的图片</span>
                <div class="editor">
                    <el-form ref="form" :model="data.image" :rules="rules">
                        <el-row>
                            <el-col :span="2" :push="2"><span>标题:</span></el-col>
                            <el-col :span="18" :push="2">
                                <el-form-item prop="title">
                                    <el-input type="text" placeholder="标题(必填)" v-model="data.image.title" maxlength="50"
                                        show-word-limit />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="2" :push="2"><span>描述:</span>:</el-col>
                            <el-col :span="18" :push="2">
                                <div class="describe">
                                    <div class="recommand">
                                        <p>@</p>
                                    </div>
                                    <el-input :autosize="{ minRows: 5, maxRows: 25 }" type="textarea" placeholder="输入图片描述"
                                        v-model="data.image.describe" maxlength="500" show-word-limit />
                                </div>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="4" :push="2"><span>上传图片:</span>:</el-col>
                            <el-col :span="16" :push="2">
                                <div class="upload">
                                    <el-upload :action="_actionURL" :before-upload="beforeUpload"
                                        :on-success="uploadSuccess" :on-preview="handlePreview" :on-remove="handleRemove"
                                        :file-list="data.files" list-type="picture" multiple>
                                        <template #="tip">
                                            <el-button size="small" type="primary">点击上传</el-button>
                                            <div>支持png、jpg、jpeg、gif图片格式，但图片大小不超过5MB</div>
                                        </template>
                                    </el-upload>
                                </div>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="2" :push="2"><span>类型:</span></el-col>
                            <el-col :span="7" :push="2">
                                <el-radio v-model="data.image.type" label="资讯">资讯</el-radio>
                            </el-col>
                            <el-col :span="7" :pull="2">
                                <el-radio v-model="data.image.type" label="攻略">攻略</el-radio>
                            </el-col>
                            <el-col :span="8" :pull="6">
                                <el-radio v-model="data.image.type" label="新奇">新奇</el-radio>
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
import { reactive, getCurrentInstance } from 'vue';
import { useStore } from 'vuex';
import { ElCard, ElForm, ElRow, ElCol, ElRadio, ElButton, ElMessage } from 'element-plus';
import { PostArticle } from "@/utils/request/common.js";




const _actionURL = 'http://localhost:2766/api/private/v1/community/file/upload';
const userInfo = useStore().getters['user/getUserInfo'].data;
const data = reactive({
    image: {
        title: '',
        describe: '',
        type: '',
        list: [],
    },
    files: [],
    confirm: false
})

const rules = {

}

const beforeUpload = (file) => {
    const type = file.type.match('image/');
    const size = file.size / 1024 / 1024 < 5;
    if (!type) ElMessage.error('上传文件仅支持图片格式');
    if (!size) ElMessage.error('上传图片大于5MB')
    return type && size;
}

const uploadSuccess = (res, file, fileList) => {
    if (res.meta.code === 239) {
        data.image.list.push({
            name: file.name,
            url: res.data.content['downloadUrl']
        });
        ElMessage.success('添加列表成功！')
    }
    else ElMessage.error('上传失败');
}

const handlePreview = (file) => {
    console.log("preview", file);
}

const handleRemove = (file) => {
    console.log("remove", file);
}

const confirm = async () => {
    data.confirm = !data.confirm;
    setTimeout(() => data.confirm = !data.confirm, 500);
    console.log(data.image.list);
    const res = await PostArticle({
        posterName: userInfo.nickname,
        posterId: userInfo.id,
        title: data.image.title,
        contentDes: data.image.describe,
        content: JSON.stringify({
            imageList: data.image.list
        }),
        type: data.image.type
    });
    if (res.meta.code == 245) {
        ElMessage.success(res.meta.msg);
        Object.keys(data.image).forEach(item => {
            item = null;
        })
        data.files = [];
    }

}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>