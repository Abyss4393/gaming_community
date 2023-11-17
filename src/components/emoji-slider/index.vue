<template>
    <div id="emoji-slider">
        <div class="inner-container">
            <ul>
                <li v-for="item, index in pureEmojiList" :key="index" @click="handler(item)">{{ item }}</li>
            </ul>
        </div>
    </div>
</template>
<script setup>
const emit = defineEmits(["addEmoji"])
const emojiList = require("@/assets/static/emoji/emoji.json")
const pureEmojiList = []
for (let i in emojiList) {
    pureEmojiList.push(emojiList[i].char);
}

const handler = (emoji) => {
    emit("addEmoji", emoji)
}
</script>
<style lang="less" scoped>
@import url('./index.less');
</style>