<script setup>
import BaseHeader from "./layouts/BaseHeader.vue";
import BaseFooter from "./layouts/BaseFooter.vue";
</script>

<template>
  <el-header><BaseHeader /></el-header>
  <el-main>
    <router-view />
  </el-main>
  <el-footer><BaseFooter /></el-footer>
</template>

<style>
#app {
  text-align: center;
}
body {
  margin: 0;
  background-color: #e2e2e2;
}
.router-view {
  margin: 0;
}
.el-header {
  margin: 0;
  padding: 0;
  height: 100%;
}
.el-footer {
  margin: 0;
  padding: 0;
}
.el-main {
  margin: 0;
  padding: 0;
}
</style>
