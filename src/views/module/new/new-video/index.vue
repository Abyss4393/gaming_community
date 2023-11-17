<template>
    <div id="abyss-new-video">
        <div class="container">
            <el-card class="content">
                <span class="crumbs">> 发布视频</span>
                <div class="editor">
                    <el-form ref="VideoForm" :model="data.video" :rules="rules">
                        <el-row>
                            <el-col :span="4"><span class="label">标题:</span></el-col>
                            <el-col :span="16">
                                <el-form-item prop="title">
                                    <el-input type="text" placeholder="标题(必填)" v-model="data.video.title" maxlength="50"
                                        show-word-limit />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="4"><span class="label">上传视频:</span></el-col>
                            <el-col :span="16">
                                <div class="upload">
                                    <el-upload :action="_actionURL" :before-upload="beforeUpload"
                                        :on-success="uploadSuccess" :on-preview="handlePreview" :on-remove="handleRemove"
                                        :file-list="data.files" multiple drag>
                                        <template #="tip">
                                            <div style="margin-top: 1rem;">点击或者拖拽文件进行上传</div>
                                            <el-button style="margin-top: 1rem;" type="primary" round>上传视频</el-button>
                                            <div style="margin-top: 1rem;">上传视频的大小不超过1G，时长在10min-1hour之间</div>
                                        </template>
                                    </el-upload>
                                </div>
                            </el-col>
                        </el-row>
                        <el-row style="height: 18rem;">
                            <el-col :span="4"><span class="label">内容描述:</span></el-col>
                            <el-col :span="16">
                                <el-form-item prop="describe">
                                    <el-input :autosize="{ minRows: 12, maxRows: 36 }" type="textarea" placeholder="输入视频描述"
                                        v-model="data.video.describe" maxlength="500" show-word-limit />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="4"><span class="label">上传封面:</span></el-col>
                            <el-col :span="16">
                                <div class="upload">
                                    <el-upload :action="_actionURL" :before-upload="beforeCoverUpload"
                                        :on-success="uploadCoverSuccess" :on-preview="handlePreview"
                                        :on-remove="handleRemove" :file-list="data.covers" list-type="picture" multiple>
                                        <template #="tip">
                                            <el-button style="margin-top: 1rem;" type="primary" round>上传封面</el-button>
                                            <div style="margin-top: 1rem;">上传的封面大小不超过5MB，仅支持jpg、png、jpeg格式</div>
                                        </template>
                                    </el-upload>
                                </div>
                            </el-col>
                        </el-row>
                        <el-row style="margin-top: 5rem;">
                            <el-col :span="4"><span class="label">类型:</span></el-col>
                            <el-col :span="6">
                                <el-radio v-model="data.video.type" label="资讯">资讯</el-radio>
                            </el-col>
                            <el-col :span="6" :pull="4">
                                <el-radio v-model="data.video.type" label="攻略">攻略</el-radio>
                            </el-col>
                            <el-col :span="8" :pull="8">
                                <el-radio v-model="data.video.type" label="新奇">新奇</el-radio>
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
import { reactive, ref, unref } from 'vue';
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus';
import { PostArticle } from '@/utils/request/common'

const userInfo = useStore().getters['user/getUserInfo'].data;
const VideoForm = ref(null);
const _actionURL = 'http://localhost:2766/api/private/v1/community/file/upload';
const data = reactive({
    video: {
        title: '',
        describe: '',
        type: '',
        covers: [],
        list: []
    },
    files: [],
    covers: [],
    confirm: false
})

function validateTitle(rule, value, callback) {
    if (value === '')
        return callback(new Error('标题必填'))
    if (value.length > 50)
        return callback(new Error('标题长度不超过50个字符'))
    callback()
}

function validateDescribe(rule, value, callback) {
    if (value === '')
        return callback(new Error('描述不能为空'))
    if (value.length > 500)
        return callback(new Error('描述长度不超过500个字符'))
    callback()
}


const rules = {
    title: [
        { validator: validateTitle, trigger: 'blur' }
    ],
    describe: [
        { validator: validateDescribe, trigger: 'blur' }
    ]
}

const beforeUpload = (file) => {
    const type = file.type.match('video/');
    const size = file.size / 1024 / 1024 / 1024 < 1;
    if (!type) ElMessage.error('上传文件仅支持wav、mp4');
    if (!size) ElMessage.error('上传视频大于1GB')
    return type && size;
}

const uploadSuccess = (res, file, fileList) => {
    if (res.meta.code === 239) {
        data.video.list.push(res.data.content.url);
        console.log('res', res);
        ElMessage.success('上传成功！')
    }
    else ElMessage.error('上传失败');

}

const beforeCoverUpload = (file) => {
    const type = file.type.match('image/');
    const size = file.size / 1024 / 1024 < 5;
    if (!type) ElMessage.error('上传文件仅支持jpg、jpeg、png');
    if (!size) ElMessage.error('上传视频大于5MB')
    return type && size;
}

const uploadCoverSuccess = (res, file, fileList) => {
    if (res.meta.code === 239) {
        data.video.covers.push(res.data.content.downloadUrl);
        console.log('res', res);
        ElMessage.success('上传封面成功！')
    }
    else ElMessage.error('上传封面失败，请重试！');
}

const handlePreview = (file) => {
   
}

const handleRemove = (file) => {
    
}


const confirm = async () => {
    function encapsulate() {
        let array = [];
        for (let i = 0; i < data.video.covers.length; i++) {
            array.push({
                cover: data.covers[i],
                video: data.video.list[i]
            })
        }
        return array;
    }
    data.confirm = true;
    unref(VideoForm).validate(async valid => {
        if (valid) {
            console.log(data.video);
            const res = await PostArticle({
                posterName: userInfo.nickname,
                posterId: userInfo.id,
                title: data.video.title,
                contentDes: data.video.describe,
                content: JSON.stringify(encapsulate()),
                type: data.video.type
            });
            console.log(res);
            if (res.meta.code === 245) {
                Object.keys(data.video).forEach(key => data.video[key] = '')
                ElMessage.success(res.meta.msg);
            }
            setTimeout(() => data.confirm = false, 800);
        } else {
            setTimeout(() => data.confirm = false, 800);
            ElMessage.error('校检未通过，请重试!')
        }
    })
    setTimeout(() => data.confirm = false, 800);
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>