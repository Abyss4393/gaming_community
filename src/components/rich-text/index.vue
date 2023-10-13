<template>
    <div id="rich-text">
        <quill-editor ref="myQuillEditor" theme="snow" v-model:content="content"
            contentType="html" @update:content="setValue()" />
        <!-- 使用自定义图片上传 -->
        <input type="file" hidden accept=".jpg,.png" ref="fileBtn" @change="handleUpload" />
    </div>
</template>
  
<script setup>
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { reactive, onMounted, ref, toRaw, watch } from 'vue'

const props = defineProps(['value'])
const emit = defineEmits(['updateValue'])
const content = ref('')
const myQuillEditor = ref()

const fileBtn = ref()
const data = reactive({
    content: '',
    editorOption: {
        placeholder: ''
    }
})
const imgHandler = (state) => {
    if (state) {
        fileBtn.value.click()
    }
}
const setValue = () => {
    const text = toRaw(myQuillEditor.value).getHTML()
    emit('updateValue', text)
}
const handleUpload = (e) => {
    const files = Array.prototype.slice.call(0,0)
    console.log(files, "files")
    if (!files) {
        return
    }
    const formdata = new FormData()
    formdata.append('file', files[0])
}
watch(() => props.value, (val) => {
    console.log(toRaw(myQuillEditor.value))
    toRaw(myQuillEditor.value).setHTML(val)
}, { deep: true })
onMounted(() => {
    const quill = toRaw(myQuillEditor.value).getQuill()
    if (myQuillEditor.value) {
        quill.getModule('toolbar').addHandler('image', imgHandler)
    }
    toRaw(myQuillEditor.value).setHTML(props.value)
})
</script>
<style  lang="less" scoped>
@import url('./index.less');
</style>
  
  