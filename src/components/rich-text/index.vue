<!-- 
    <template>
    <div id="rich-text">
        <quill-editor ref="quillEditor" theme="snow" v-model:content="props.content" contentType="html"
            @update:content="setValue()" />
       
        <input ref="fileBtn" type="file" hidden accept="image/*" @change="handleUpload" />
    </div>
</template>
-->
  
<!-- <script setup>
import { onMounted, ref, toRaw, watch, defineProps } from 'vue';
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css';


const emit = defineEmits(['updateValue'])
const myQuillEditor = ref()
const fileBtn = ref()

const props = defineProps({
    content: {
        type: String, default: ''
    },
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
    const files = Array.prototype.slice.call(0, 0)
    console.log(files, "files")
    if (!files) {
        return
    }
    const formdata = new FormData()
    formdata.append('file', files[0])
}

onMounted(() => {
    const quill = toRaw(myQuillEditor.value).getQuill()
    if (myQuillEditor.value) {
        quill.getModule('toolbar').addHandler('image', imgHandler)
    }
    toRaw(myQuillEditor.value).setHTML(props.value)
})
watch(() => props.value, (val) => {
    console.log(toRaw(myQuillEditor.value))
    toRaw(myQuillEditor.value).setHTML(val)
}, { deep: true })
</script> -->
<!-- <style  lang="less" scoped>
@import url('./index.less');
</style> -->
  
  