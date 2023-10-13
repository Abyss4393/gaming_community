<template>
    <div id="chat">
        <el-container>
            <el-aside>
                <el-row>
                    <el-col :xs="24" :sm="24" :md="11" :lg="10" :xl="9" :span="12">
                        <div class="chitchat" @click="instance.proxy.$router.push('/friends')">
                            <p>Chating</p>
                        </div>
                    </el-col>
                    <el-col :xs="24" :sm="24" :md="11" :lg="10" :xl="9" :span="12">
                        <div class="other" @click="instance.proxy.$router.push('/other')">
                            <p>other</p>
                        </div>
                    </el-col>
                </el-row>

                <abyss-research class="search" @submit="submit" />
                <el-row :xs="24" :sm="24" :md="11" :lg="10" :xl="9" class="speparation" />
                <div class="display">
                    <router-view />
                </div>
            </el-aside>
            
        </el-container>

    </div>
</template>
<script setup>
import { getCurrentInstance } from 'vue';
import { ElAside, ElCol, ElContainer, ElMain, ElRow } from 'element-plus';
import abyssResearch from '@/components/func/abyss-research/index';
const instance = getCurrentInstance();
const submit = (val) => {
    console.log(val);
    socket.send(JSON.stringify({
        msg: val
    }));
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>