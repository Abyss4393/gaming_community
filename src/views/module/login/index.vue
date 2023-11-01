<template>
    <div id="login">
        <div class="login-content">
            <div class="login-box">
                <h2>Login</h2>
                <el-form :model="loginInfo" :rules="rule" ref="loginForm">
                    <div class="user-box">
                        <el-form-item prop="username">
                            <el-input type="text" v-model="loginInfo.username" placeholder="Username"></el-input>
                        </el-form-item>
                    </div>
                    <div class="user-box">
                        <el-form-item prop="password">
                            <el-input type="password" :show-password="true" v-model="loginInfo.password"
                                placeholder="Password" @keyup.enter.native="login"></el-input>
                        </el-form-item>
                    </div>
                    <div @click="login">
                        <a>
                            <span></span>
                            <span></span>
                            <span></span>
                            <span></span>
                            Submit
                        </a>
                    </div>
                    <div class="register" @click="() => intance.proxy.$router.push('/register')">
                        <div>Register</div>
                    </div>

                </el-form>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, reactive, getCurrentInstance, unref } from 'vue'
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus';
import { LoginAPI } from '@/utils/request/common.js'


const loginForm = ref(null)
const loginInfo = reactive({
    username: '',
    password: ''

});
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
const intance = getCurrentInstance();
const store = useStore();

// hasMask

function changeHasMask(params) {
    store.commit("setHasMask", params);
}
function asyncChangeHasMask(secondes) {
    store.dispatch("updateHasMask", secondes)
}

// user

function setUserInfo(info) {
    store.commit("user/setUserInfo", info);
}

function asyncUpdateUserInfo(params) {
    store.dispatch("user/asyncUpdateUserInfo", params);
};

//
function setToken(token) {
    store.commit("setToken", token)
}









const login = function () {
    changeHasMask(true);
    setTimeout(() => {
        unref(loginForm).validate(async vaild => {
            if (vaild) {
                const res = await LoginAPI(loginInfo);
                if (res.meta.code === 222) {
                    setUserInfo(res.data);
                    setToken(res.data.token);
                    console.log(store.state.user.userInfo);
                    ElMessage.success(res.meta.msg);
                    asyncChangeHasMask(100);
                    intance.proxy.$router.push('/abyss/')
                } else {
                    ElMessage.error(res.meta.msg);
                    asyncChangeHasMask(100);
                }
            } else {
                asyncChangeHasMask(100);
                ElMessage({
                    type: "error",
                    message: '请检查你的账户和密码，稍后重试~',
                    iconClass: 'el-icon-error',
                    offset: 42
                })
            }

        })
    }, 800)
}
</script>
<style lang="less" scoped>
@import url("./index.less");
</style>
