<template>
    <div id="recommend-manage">
        <div class="left">
            <el-table :data="recommend" style="width: 100%" :row-class-name="tableRowClassName">
                <el-table-column sortable label="top">
                    <template #default="scope">
                        <span>{{ scope.$index + 1 }}</span>
                    </template>
                </el-table-column>
                <el-table-column sortable label="评分">
                    <template #default="scope">
                        <el-text type="warning">{{ Math.sqrt(scope.row.article.articleLike *
                scope.row.article.collectCount).toFixed(3) }}</el-text>
                    </template>
                </el-table-column>
                <el-table-column prop="posterName" label="发帖人" width="120">
                    <template #default="scope">
                        <span>{{ scope.row.article.posterName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="type" label="类型">
                    <template #default="{ row }">
                        <el-tag>{{ row.article.type }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="内容">
                    <template #default="{ row }">
                        <el-button type="warning" round @click="showPreview(row.article)">预览</el-button>
                    </template>
                </el-table-column>
                <el-table-column fixed="right" width="180">
                    <template #default="scope">
                        <el-tooltip effect="dark" content="推荐" placement="top" :disabled="scope.row.recommend">
                            <el-button type="primary" :icon="StarFilled" :enterable="false"
                                :disabled="scope.row.recommend"
                                @click="showRecommend(scope.row.article.id)"></el-button>
                        </el-tooltip>
                        <el-tooltip effect="dark" content="取消推荐" placement="right">
                            <el-button type="danger" :icon="Close" :enterable="false" :disabled="!scope.row.recommend"
                                @click="cancelRecommend(scope.row.article.id)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="right">

            <el-carousel height="500px" arrow="always" direction="vertical" type="card" :autoplay="false">
                <el-carousel-item v-for="item in recommendCarousel" :key="item.id">
                    <el-image :src="item.recommend.cover" lazy />
                </el-carousel-item>
            </el-carousel>

        </div>

        <el-dialog class="preview" v-model="previewVisible" title="预览" width="50%" draggable overflow center>
            <div class="preview-title">标题：{{ previewArticle.title }}</div>
            <div class="preview-info">
                <el-tag peffect="light" type="success" size="large">{{ previewArticle.type }}</el-tag>
                <span>发布时间--.--:&gt;&gt;{{ previewArticle.postTime.slice(0, 10) }}</span>
            </div>
            <div class="preview-content" v-for="preItem, index in previewArticle.content.contentList" :key="index">
                <div class="preview-text" v-html="preItem.text"></div>
                <div class="preview-image" v-for="image, iItem in preItem.imageList" :key="iItem">
                    <el-image :src="image.url" lazy />
                </div>
                <el-divider>
                    <el-icon><star-filled /></el-icon>
                </el-divider>
                <div class="preview-video" v-for="video, vItem in preItem.videoList" :key="vItem">
                    <video controls :src="video.url" width="500"></video>
                </div>
                <el-divider content-position="left">Game community</el-divider>
                <div class="preview-desc">
                    <h3>内容简介：{{ previewArticle.contentDes }}</h3>
                </div>
            </div>

            <template #footer>
                <div class="preview-footer">
                    <div class="view"></div>
                    <div class="like">
                        <img :src="require('@/assets/static/icons/zan.png')" alt="like">
                        <span>{{ previewArticle.articleLike }}</span>
                    </div>
                    <div class="dislike">
                        <img :src="require('@/assets/static/icons/zancopy.png')" alt="dislike">
                        <span>{{ previewArticle.articleDislike }}</span>
                    </div>
                    <div class="collect">
                        <img :src="require('@/assets/static/icons/collection.png')" alt="collect">
                        <span>{{ previewArticle.collectCount }}</span>
                    </div>
                </div>
            </template>
        </el-dialog>

        <el-dialog class="recommend-dialog" v-model="recommendVisible" title="推荐" width="50%" draggable overflow>
            <el-row>
                <el-col :span="4"><span class="label">内容描述:</span></el-col>
                <el-col :span="16">
                    <el-upload :action="imageBed" :before-upload="beforeUpload" :on-success="uploadSuccess"
                        :on-preview="handlePreview" :on-remove="handleRemove" :file-list="data.files"
                        list-type="picture" drag accept="image/*" multiple>
                        <template #="tip">
                            <el-button size="small" type="primary">点击上传</el-button>
                            <div>支持png、jpg、jpeg、gif图片格式，但图片大小不超过5MB</div>
                        </template>
                    </el-upload>
                </el-col>
            </el-row>
            <el-row style="margin-top: 24px;">
                <el-col :span="4"><span class="label">是否热门:</span></el-col>
                <el-col :span="16">
                    <el-switch v-model="isHot" active-text="是" inactive-text="否" />
                </el-col>
            </el-row>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="recommendVisible = false">取消</el-button>
                    <el-button type="primary" @click="recommendArticle()">确认推荐</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup>
import { onMounted, reactive, ref } from 'vue';
import {
    ElText, ElRow, ElUpload, ElMessageBox, ElNotification,
    ElTable, ElTableColumn, ElCarousel, ElTooltip, ElButton, ElMessage
} from 'element-plus';
import { StarFilled, Close } from '@element-plus/icons-vue';
import { GetUpRateArticle } from '@/utils/request/admin';
import { GetRecommends, SetRecommend, CancelRecommend } from '@/utils/request/recommend';
const imageBed = 'http://localhost:2766/api/private/v1/community/file/upload';
const recommend = ref([])
const recommendCarousel = ref([])
const previewVisible = ref(false)
const previewArticle = ref({})
const recommendVisible = ref(false)
const isHot = ref(false);
const activeId = ref(0);
const data = reactive({
    files: []
})

const tableRowClassName = ({ row, index }) => {
    if (row.recommend)
        return 'success-row';
    return '';
}

const asyncRecommendArticle = async () => {
    const res = await GetUpRateArticle()
    if (res.meta.code === 200) {
        recommend.value = res.data
    }
    console.log(recommend.value);
}

const asyncRecommendBar = async () => {
    const res = await GetRecommends();
    if (res.meta.code === 200) {
        recommendCarousel.value = res.data
    }
}
onMounted(() => {
    asyncRecommendArticle();
    asyncRecommendBar();
})

const showPreview = (row) => {
    previewVisible.value = true;
    previewArticle.value = row;
    if (row.content instanceof Object) previewArticle.value.content = row.content;
    else previewArticle.value.content = JSON.parse(row.content);
}

const showRecommend = (id) => {
    recommendVisible.value = true;
    activeId.value = id;
}

const beforeUpload = (file) => {
    const type = file.type.match('image/');
    const size = file.size / 1024 / 1024 < 5;
    if (!type) ElMessage.error('上传文件仅支持图片格式');
    if (!size) ElMessage.error('上传图片大于5MB')
    return type && size;
}
const uploadSuccess = (res, file, fileList) => {
    if (res.meta.code === 239) {
        data.files.push(res.data.content.download);
        ElMessage.success('添加列表成功！')
    }
    else ElMessage.error('上传失败');
}

const handlePreview = (file) => {
}

const handleRemove = (file) => {

}
const recommendArticle = () => {
    if (0 < data.files.length) {
        ElMessageBox.confirm(
            '确认推荐该文章吗？',
            'Warning',
            {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
            }).then(async () => {
                const res = await SetRecommend({
                    aid: activeId.value,
                    isHot: isHot.value ? 1 : 0,
                    cover: data.files[0]
                });
                if (res.meta.code === 200) {
                    recommendVisible.value = false;
                    asyncRecommendArticle();
                    asyncRecommendBar();
                    data.files = [];
                    isHot.value = false;
                    ElNotification
                        ({
                            title: '系统消息',
                            message: '推荐成功',
                            type: 'success',
                        })
                } else {
                    ElNotification
                        ({
                            title: '系统消息',
                            message: '推荐失败',
                            type: 'error',
                        })
                }

            }).catch()
    } else ElMessage.error('请先上传封面')
}
const cancelRecommend = (id) => {
    console.log(id);
    ElMessageBox.confirm(
        '确认取消推荐该文章吗？',
        'Warning',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
        }).then(async () => {
            const res = await CancelRecommend(id)
            if (res.meta.code === 223) {
                asyncRecommendArticle();
                asyncRecommendBar();
                ElNotification
                    ({
                        title: '系统消息',
                        message: '取消推荐成功',
                        type: 'success',
                    })
            } else ElNotification
                ({
                    title: '系统消息',
                    message: '取消推荐失败',
                    type: 'error',
                })
        }
        ).catch()
}
</script>
<style lang='less' scoped>
@import url('./index.less');
</style>
