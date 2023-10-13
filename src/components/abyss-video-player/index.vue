<template>
    <div id="abyss-player">
        <div class="content-video" @click="play">
            <img :src="props.thumbnail.url"
                :style="{ height: method.getImageHeight(thumbnail.width, thumbnail.height) + 'px' }" />
            <div class="icon"></div>
        </div>
        <div v-if="playing" class="video-player">
            <video :src="props.src" autoplay controls></video>
            <span class="close-player" @click="end">x</span>
        </div>
    </div>
</template>
  
<script setup>
import { reactive, computed, defineProps } from 'vue';
const IMAGE_MAX_WIDTH = 200;
const IMAGE_MAX_HEIGHT = 150;
const props = defineProps({
    thumbnail: {
        type: Object
    },
    src: {
        type: Object
    }
})
const data = reactive({
    playing: false
})

const method = computed({
    getImageHeight: function (width, height) {
        if (width < IMAGE_MAX_WIDTH && height < IMAGE_MAX_HEIGHT) {
            return height;
        } else if (width > height) {
            return (IMAGE_MAX_WIDTH / width * height);
        } else if (width === height || width < height) {
            return IMAGE_MAX_HEIGHT;
        }
    }
})
const play = function () {
    data.playing = true;
}
const end = function () {
    data.playing = false;
}

</script>
<style lang="less" scoped>
@import url("./index.less");
</style>