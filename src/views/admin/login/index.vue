<template>
    <div id="admin-login">
        <div class="main">
            <div class="container">
                <el-card>
                    <div class="header">账号登录</div>
                    <el-form :model="data" :rules="rules" ref="LoginForm">
                        <el-form-item prop="admin">
                            <el-input type="text" v-model="data.admin" placeholder="输入账号" :disabled="data.isLoading" />
                        </el-form-item>
                        <el-form-item prop="password">
                            <el-input type="password" v-model="data.password" placeholder="输入密码" :show-password="true"
                                @keyup.enter.native="login" :disabled="data.isLoading" />
                        </el-form-item>
                    </el-form>
                    <el-button :type="data.isSuccess ? 'success' : 'primary'" :loading="data.isLoading" @click="login">{{
                        data.isLoading ? 'loading' :
                        data.isSuccess ? '√ 登录成功' : '登录' }}</el-button>
                </el-card>
            </div>
        </div>
        <div class="footer"></div>
    </div>
</template>
<script setup>
import { reactive, getCurrentInstance, unref, ref } from 'vue';
import { ElCard, ElForm, ElFormItem, ElButton, ElMessage } from 'element-plus';

const LoginForm = ref(null);
const router = getCurrentInstance().proxy.$router;
const defaultAttributes = {
    admin: '',
    password: '',
    isLoading: false,
    isSuccess: false
}

const data = reactive({
    ...defaultAttributes
})
const rules = {
    admin: [
        { validator: validateAdmin, trigger: 'change' }

    ],
    password: [
        { validator: validatePwd, trigger: 'change' }
    ]
};
function validateAdmin(rule, value, callback) {
    if (value === '') {
        return callback(new Error('账号不能为空'))
    } else {
        if (value.length < 2 || value.length > 10) {
            return callback(new Error('账号长度在3到9个字符'))
        }
    }
    callback()
};
function validatePwd(rule, value, callback) {
    const requires = /^[a-zA-Z0-9]+$/
    if (value === '') {
        return callback(new Error('密码不能为空'))
    } else {
        if (!requires.test(value)) {
            return callback(new Error('包含非法字符，请重试'))
        }
        if (value.length < 3 || value.length > 10) {
            return callback(new Error('密码长度在4到9个字符'))
        }
    }
    callback()
};
const login = function () {
    data.isLoading = true;
    setTimeout(() => {
        unref(LoginForm).validate(async vaild => {
            if (vaild) {
                data.isSuccess = true;
                ElMessage.success('welcome!')
                setTimeout(() => router.push('/admin/index'), 500)
            } else {
                ElMessage({
                    type: "error",
                    message: '请检查你的账户和密码，稍后重试~',
                    iconClass: 'el-icon-error',
                    offset: 42
                })
            }

        })
        data.isLoading = false;
    }, 800)

}
</script>
<style lang='less' scoped>
@import url('./index.less');
</style>
