<template>
    <div id="abyss-research">
        <el-input @keydown.enter.native="search" v-model="data.value" />
        <img @click="search" :src="require('@/assets/static/icons/search.png')">
    </div>
</template>
<script setup>
import { defineEmits, reactive } from 'vue';
import { ElInput } from 'element-plus';
const emit = defineEmits(["val"])
const data = reactive({
    value: ''
})
const search = () => {
    emit("val", data.value)
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>