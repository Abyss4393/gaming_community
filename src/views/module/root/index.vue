<template>
    <div id="abyss">
        <abyssHeader />
        <router-view />
        <abyssFooter />
        <el-backtop>
            <div class="abyss-top" :bottom="100">
                <img @click="back" :src="require('@/assets/static/icons/top.png')" alt="">
            </div>
        </el-backtop>
    </div>
</template>
<script setup>
import { ElBacktop } from 'element-plus';
import abyssHeader from '@/components/layout/abyss-navigation-header/index'
import abyssFooter from '@/components/layout/abyss-navigation-footer/index'
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>