<template>
    <div id="abyss-new-image">
        <div class="container">
            <el-card class="content">
                <span class="crumbs">> 发布有趣的图片</span>
                <div class="editor">
                    <el-form ref="form" :model="data.image" :rules="rules">
                        <el-row>
                            <el-col :span="4" :push="2"><span>标题:</span></el-col>
                            <el-col :span="16">
                                <el-form-item prop="title">
                                    <el-input type="text" placeholder="标题(必填)" v-model="data.image.title" maxlength="50"
                                        show-word-limit />
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>
                    <div class="confirm">
                       
                    </div>
                </div>
            </el-card>
        </div>
    </div>
</template>
<script setup>
import { reactive } from 'vue';
const data = reactive({
    image:{
        title:''
    }
})

const rules = {

}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>