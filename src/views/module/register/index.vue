<template>
    <div id="register">
        <div class="register-content">
            <div class="register-box">
                <h2>Register</h2>
                <el-form :model="registerInfo" :rules="rule" ref="RegisterForm">
                    <div class="user-box">
                        <el-form-item prop="username">
                            <el-input v-model="registerInfo.username" placeholder="输入账号" />
                        </el-form-item>
                        <el-form-item prop="password">
                            <el-input v-model="registerInfo.password" placeholder="输入密码" />
                        </el-form-item>
                        <el-form-item>
                            <el-input v-model="registerInfo.nickname" placeholder="输入昵称" />
                        </el-form-item>
                        <el-form-item>
                            <el-input v-model="registerInfo.email" placeholder="输入邮箱" />
                        </el-form-item>
                        <!-- <s-identify class="verfiy" :identifyCode="verfiyCode" @refresh="refreshCode" /> -->
                    </div>
                    <div @click="register">
                        <a>
                            <span></span>
                            <span></span>
                            <span></span>
                            <span></span>
                            Submit
                        </a>
                    </div>
                </el-form>

            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, reactive, getCurrentInstance, unref } from 'vue'
import SIdentify from '@/components/sidentify/index';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useStore } from 'vuex';
import { RegisterAPI } from '@/utils/request/common';

const instance = getCurrentInstance();
const store = useStore();
const verfiyCode = ref('');
const RegisterForm = ref(null);
const registerInfo = reactive({
    username: '',
    password: '',
    nickname: '',
    email: '',

})

const rule = {
    username: [
        { validator: validateUser, trigger: 'change' }

    ],
    password: [
        { validator: validatePass, trigger: 'change' }
    ]
};
function validateUser(rule, value, callback) {
    if (value === '') {
        return callback(new Error('账号不能为空'))
    } else {
        if (value.length < 2 || value.length > 10) {
            return callback(new Error('账号长度在3到9个字符'))
        }
    }
    callback()
};
function validatePass(rule, value, callback) {
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


// hasMask
function changeHasMask(params) {
    store.commit("setHasMask", params);
}
function asyncChangeHasMask(secondes) {
    store.dispatch("updateHasMask", secondes)
}
const refreshCode = () => {

}

const register = () => {
    changeHasMask(true);
    setTimeout(() => {
        unref(RegisterForm).validate(async vaild => {
            if (vaild) {
                const res = await RegisterAPI(registerInfo);
                console.log(res);
                if (res.meta.code === 221) {
                    ElMessage.success(res.meta.msg);
                    ElMessageBox.confirm('注册成功，是否跳转到登录页面？', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'primary'
                    }).then(() => {
                        instance.proxy.$router.push('/login')
                    }).catch(() => {
                        instance.proxy.$router.push('/abyss')
                    })
                    asyncChangeHasMask(100);
                } else {
                    ElMessage.error(res.meta.msg);
                    asyncChangeHasMask(100);
                }
            } else {
                ElMessage({
                    type: "error",
                    message: '注册失败，请稍后重试~',
                    iconClass: 'el-icon-error',
                    offset: 42
                })
                asyncChangeHasMask(100);
            }

        })
    }, 800)

}
</script>
<style lang='less' scoped>
@import url('./index.less');
</style>
