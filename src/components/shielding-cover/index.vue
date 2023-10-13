<template>
    <!-- 加载屏蔽罩 -->
    <div class="loading" v-if="show">
        <div class="load">
            <div class="dot" v-for="item in nums"></div>
        </div>
        <span class="title" @mouseenter="handlerForbid($event)">加载中......</span>
    </div>
</template>
<script setup>
const nums = 36;
const props = defineProps({
    show: {
        type: Boolean,
        defalut: false
    }
})
const handlerForbid = (event) => {  
    event.preventDefault()
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>