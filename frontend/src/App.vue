<template>
  <el-header class="app-header"><BaseHeader /></el-header>
  <el-main class="app-main">
    <router-view class="app-router-view"/>
  </el-main>
  <el-footer class="app-footer"><BaseFooter /></el-footer>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue'

  import BaseHeader from "./layouts/BaseHeader.vue";
  import BaseFooter from "./layouts/BaseFooter.vue";
  import ThemeService from "./services/themeService";

  const theme = ref<string>(ThemeService.getThemeName());

  onMounted(() => {
    const file = document.createElement("link");
    file.rel = "stylesheet";
    file.href = `./themes/${theme.value}.css`;
    document.head.appendChild(file);
  });
</script>
