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
const store = useStore();
const mask = computed(() => { return store.state.hasMask })
</script>
<style>
#app {
  width: 100%;
  height: 100%;

}
</style>
