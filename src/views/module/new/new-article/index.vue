<template>
    <div id="abyss-new-article">
        <rich-text></rich-text>
    </div>
</template>
<script setup>
import { getCurrentInstance, onMounted } from 'vue';
import richText from '@/components/rich-text/index'


const instance = getCurrentInstance();

const uid = instance.proxy.$route.query['author_id'];


</script>
<style lang="less" scoped>
@import url('./index.less');
</style>