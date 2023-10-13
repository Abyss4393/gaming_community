<template>
    <div class="err404">
        <img :src="require(`@/assets/static/404.jpg`)" alt="">
        <h4>您访问的页面不见了！</h4>
    </div>
</template>
    
<script>
export default {
    data() {
        return {}

    }
}
</script>
    
<style>
.err404 {
    width: 420px;
    margin: 150px auto;
    height: 255px;
    padding: 20px 20px;
}

.err404 h4 {
    font-size: 24px;
    color: #191919;
    margin-top: 20px;
    margin-left: 120px;
    padding-bottom: 16px 31
}
</style>