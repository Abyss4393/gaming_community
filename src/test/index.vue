<template>
    <div id="abyss-test">
        <h1>11111111111</h1>
        <audio controls :src="audioPath" />
        <video controls width="250">
            <source :src="videoPath" type="video/mp4" />
        </video>

    </div>
</template>
<script setup>
const audioPath = 'https://abyss4393.cn/ftp/20231118e85a60b85bcf4ef2b9303d5189847bc4.mp3';
const videoPath = 'https://abyss4393.cn/ftp/20231118e97569b4466540ee819ab25b44452326.mp4'
</script>