<template>
    <div id="abyss">
        <abyssHeader />
        <router-view />
        <abyssFooter />
        <div class="abyss-top" v-if="data.scroll > 300">
            <img @click="back" :src="require('@/assets/static/icons/top.png')" alt="">
        </div>
    </div>
</template>
<script setup>
import { reactive, onMounted, onUnmounted } from 'vue';
import abyssHeader from '@/components/layout/abyss-navigation-header/index'
import abyssFooter from '@/components/layout/abyss-navigation-footer/index'


const data = reactive({
    scroll: 0,
})
const doScroll = function (e) {
    data.scroll = document.documentElement.scrollTop;
}
const back = () => document.documentElement.scrollTop = 0;
onMounted(async () => window.addEventListener("scroll", doScroll));
onUnmounted(() => window.removeEventListener("scroll", doScroll));
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>