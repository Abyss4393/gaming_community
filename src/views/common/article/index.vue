<template>
    <div id="abyss-article">

    </div>
</template>
<script setup>
import { reactive, onMounted, onUnmounted, getCurrentInstance } from 'vue';
import { ElCard, ElButton } from 'element-plus';
import { AsyncArticleList } from '@/utils/request/common.js'
const instance = getCurrentInstance();

onMounted(()=>{
    const router = instance.proxy.$route
    console.log(router);
})

</script>
<style lang="less" scoped>
@import url('./index.less');
</style>