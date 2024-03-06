<template>
  <div id="app">
    <shield-cover :show="mask" />
    <router-view />
  </div>
</template>
<script setup>
import ShieldCover from '@/components/shielding-cover/index'
import { computed } from 'vue';
import { useStore } from 'vuex';

const mask = computed(() => { return useStore().state.hasMask })


document.addEventListener('mousewheel', function (e) {
  e = e || window.event;
  if ((e.wheelDelta && event.ctrlKey) || e.detail) {
    event.preventDefault();
  }
}, { capture: false, passive: false });
document.addEventListener('keydown', function (event) {
  if ((event.ctrlKey === true || event.metaKey === true)
    && (event.keyCode === 61 || event.keyCode === 107
      || event.keyCode === 173 || event.keyCode === 109
      || event.keyCode === 187 || event.keyCode === 189)) {
    event.preventDefault();
  }
}, false);
</script>
<style>
#app {
  width: 100%;
  height: 100%;
}
</style>
