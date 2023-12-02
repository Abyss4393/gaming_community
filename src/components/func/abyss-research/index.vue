<template>
    <div id="abyss-research">
        <el-autocomplete
        v-bind="instance.proxy.$attrs" 
        popper-class="my-autocomplete"
        v-model="data.value"
        :fetch-suggestions="querySearchAsync"
         @select.enter.native="search">

        </el-autocomplete>
        <img @click="search" :src="require('@/assets/static/icons/search.png')">
    </div>
</template>
<script setup>
import { defineEmits, getCurrentInstance, reactive } from 'vue';
import { ElAutocomplete } from 'element-plus';

const instance = getCurrentInstance();
const emit = defineEmits(['querySearchAsync','val'])
const data = reactive({
    value: ''
})
const querySearchAsync = () => {
    emit('querySearchAsync')
}
const search = () => {
    emit('val', data.value)
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>