<template>
  <div class="s-canvas">
    <canvas ref="canvas" id="canvas" :width="contentWidth" :height="contentHeight" @refesh="handleRefesh"></canvas>
    <p @refesh="handleRefesh">看不清？点击图片刷新</p>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
const canvasRef = ref();
const props = defineProps({
  identifyCode: {
    type: String,
    default: '1234'
  },
  fontSizeMin: {
    type: Number,
    default: 25
  },
  fontSizeMax: {
    type: Number,
    default: 30
  },
  backgroundColorMin: {
    type: Number,
    default: 255
  },
  backgroundColorMax: {
    type: Number,
    default: 255
  },
  colorMin: {
    type: Number,
    default: 0
  },
  colorMax: {
    type: Number,
    default: 160
  },
  lineColorMin: {
    type: Number,
    default: 100
  }, lineColorMax: {
    type: Number,
    default: 255
  },
  dotColorMin: {
    type: Number,
    default: 0
  },
  dotColorMax: {
    type: Number,
    default: 255
  },
  contentWidth: {
    type: Number,
    default: 112
  },
  contentHeight: {
    type: Number,
    default: 31
  }
})

onMounted(() => drawPic())
// watch(props.identifyCode, (newValue, oldValue) => {
//   drawPic()
// }, { immediate: true })

// 生成一个随机数
const randomNum = (min, max) => {
  return Math.floor(Math.random() * (max - min) + min)
}
// 生成一个随机的颜色
const randomColor = (min, max) => {
  let r = randomNum(min, max)
  let g = randomNum(min, max)
  let b = randomNum(min, max)
  return 'rgb(' + r + ',' + g + ',' + b + ')'
}

const drawText = (ctx, txt, i) => {
  ctx.fillStyle = randomColor(props.colorMin, props.colorMax)
  ctx.font = randomNum(props.fontSizeMin, props.fontSizeMax) + 'px SimHei'
  let x = (i + 1) * (props.contentWidth / (props.identifyCode.length + 1))
  let y = randomNum(props.fontSizeMax, props.contentHeight - 5)
  var deg = randomNum(-45, 45)
  // 修改坐标原点和旋转角度
  ctx.translate(x, y)
  ctx.rotate(deg * Math.PI / 180)
  ctx.fillText(txt, 0, 0)
  // 恢复坐标原点和旋转角度
  ctx.rotate(-deg * Math.PI / 180)
  ctx.translate(-x, -y)
}
const drawLine = (ctx) => {
  // 绘制干扰线
  for (let i = 0; i < 5; i++) {
    ctx.strokeStyle = randomColor(props.lineColorMin, props.lineColorMax)
    ctx.beginPath()
    ctx.moveTo(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight))
    ctx.lineTo(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight))
    ctx.stroke()
  }
}
const drawDot = (ctx) => {
  // 绘制干扰点
  for (let i = 0; i < 80; i++) {
    ctx.fillStyle = randomColor(0, 255)
    ctx.beginPath()
    ctx.arc(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight), 1, 0, 2 * Math.PI)
    ctx.fill()
  }
}
const drawPic = () => {
  let ctx = canvasRef.getContext('2d');
  ctx.textBaseline = 'bottom';
  // 绘制背景
  ctx.fillStyle = randomColor(props.backgroundColorMin, props.backgroundColorMax)
  ctx.fillRect(0, 0, props.contentWidth, props.contentHeight)
  // 绘制文字
  for (let i = 0; i < props.identifyCode.length; i++) {
    drawText(ctx, props.identifyCode[i], i)
  }
  drawLine(ctx)
  drawDot(ctx)
}

const handleRefesh = () => {
  drawPic()
}


</script>
<style lang="less" scoped>
@import url('./index.less');
</style>