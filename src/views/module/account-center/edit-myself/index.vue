<template>
    <div id="abyss-edit-myself">
        <div class="header">
            编辑资料
        </div>
        <div class="edit-body">
            <div class="edit-avatar">
                <img :src="data.edit.avatar" alt="">
                <div class="upload">
                    <el-upload :action="_actionURL" :before-upload="beforeUpload" :on-success="uploadSuccess"
                        :on-preview="handlePreview" :on-remove="handleRemove" :file-list="data.upload"
                        :show-file-list="false" accept="image/*">
                        <template #="tip">
                            <el-button type="primary">修改头像</el-button>
                        </template>
                    </el-upload>
                </div>
            </div>

            <el-form ref="EditForm" :model="data.edit" :rules="rules">
                <el-row>
                    <el-col :span="2"><span>昵称</span></el-col>
                    <el-col :span="22">
                        <div class="edit-nickname">
                            <el-form-item prop="nickname">
                                <el-input type="text" v-model="data.edit.nickname" placeholder="昵称(必填)" maxlength="25"
                                    show-word-limit />
                            </el-form-item>
                        </div>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="2"><span>手机</span></el-col>
                    <el-col :span="22">
                        <div class="edit-mobilephone">
                            <el-form-item prop="mobile">
                                <el-input type="tel" v-model="data.edit.mobile" placeholder="输入手机号" maxlength="11"
                                    show-word-limit clearable />
                            </el-form-item>
                        </div>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="2"><span>性别</span></el-col>
                    <el-col :span="7">
                        <el-radio v-model="data.edit.gender" label="男">男</el-radio>
                    </el-col>
                    <el-col :span="7" :pull="4">
                        <el-radio v-model="data.edit.gender" label="女">女</el-radio>
                    </el-col>
                    <el-col :span="8" :pull="8">
                        <el-radio v-model="data.edit.gender" label="保密">保密</el-radio>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="2"><span>邮箱</span></el-col>
                    <el-col :span="22">
                        <div class="edit-email">
                            <el-form-item prop="email">
                                <el-input type="email" v-model="data.edit.email" placeholder="输入邮箱" clearable />
                            </el-form-item>
                        </div>
                    </el-col>
                </el-row>
            </el-form>
            <el-button class="confirm" type="primary" @click="submit">保存</el-button>
        </div>
    </div>
</template>
<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElUpload, ElForm, ElFormItem, ElRow, ElCol, ElInput, ElButton, ElMessage } from 'element-plus';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import { UpdateUser, UserInfo } from '@/utils/request/common.js';

const _actionURL = 'http://localhost:2766/api/private/v1/community/user/upload/avatar';
const store = useStore();
const uid = useRoute().query.id;
const EditForm = ref(null);

const userInfo = useStore().getters['user/getUserInfo'].data;
const data = reactive({
    edit: {
        id: '',
        avatar: '',
        nickname: '',
        mobile: '',
        gender: '',
        email: ''
    },
    upload: []
})

onMounted(() => {
    data.edit.id = uid;
    data.edit.avatar = userInfo.avatar;
    data.edit.nickname = userInfo.nickname;
    data.edit.mobile = userInfo.mobile; 
    data.edit.gender = userInfo.gender;
    data.edit.email = userInfo.email;
})


const beforeUpload = (file) => {
    const type = file.type.match('image/');
    const size = file.size / 1024 / 1024 < 5;
    if (!type) ElMessage.error('上传文件仅支持图片格式');
    if (!size) ElMessage.error('上传图片大于5MB')
    return type && size;
}

const uploadSuccess = (res, file, fileList) => {
    console.log(res);
    if (res.meta.code === 239)
        data.edit.avatar = res.data.content['download_url'];
}

const handlePreview = (file) => { }

const handleRemove = (file) => { }


function validateNickname(rule, value, callback) {
    if (value === '')
        return callback(new Error('昵称必填'))
    if (value.length > 25)
        return callback(new Error('标题长度不超过25个字符'))
    callback()
}

function validateMobile(rule, value, callback) {
    const regx = /^(1[3-9])\d{9}$/
    if (!regx.test(value))
        return callback(new Error('手机格式不正确！'))
    callback()
}

function validateEmail(rule, value, callback) {
    const regx = /[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/
    if (!regx.test(value))
        return callback(new Error('邮箱格式不正确！'))
    callback()
}



const rules = {
    nickname: [
        { validator: validateNickname, trigger: 'blur' }
    ],
    mobile: [
        { validator: validateMobile, trigger: 'blur' }
    ],
    email: [
        { validator: validateEmail, trigger: 'blur' }
    ]
}


const submit = async () => {
    function asyncUserInfo(info) {
        store.commit("user/setUserInfoData", info);
    }
    const res = await UpdateUser(data.edit);
    if (res.meta.code === 200) {
        const user = await UserInfo(uid);
        console.log(user);
        if (200 == user.meta.code) {
            asyncUserInfo(user.data);
            ElMessage.success('保存成功')
        } else ElMessage.error('保存失败')
    } else ElMessage.error('保存失败')
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>