<template>
  <section class="app-header-image-block">
    <router-link to="/">
      <el-image class="app-header-image" :src="LOGO"></el-image>
    </router-link>
  </section>
</template>

<script setup lang="ts">
  import { ref } from "vue";
  import ThemeService from "../services/themeService";

  const theme = ref<string>(ThemeService.getThemeName());
  const LOGO = `./themes/${theme.value}/logo.webp`;
</script>
