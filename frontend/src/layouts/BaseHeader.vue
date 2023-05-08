<template>
  <section>
    <router-link to="/">
      <el-image :src="LOGO"></el-image>
    </router-link>
  </section>
</template>
<script setup lang="ts">
import LOGO from "../assets/logo.webp";
</script>
<style scoped>
section {
  background-color: #0d5469;
  text-align: start;
  height: 100%;
}
.el-image {
  height: 100%;
}
</style>
