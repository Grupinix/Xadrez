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
html {
  height: 100%;
}
#app {
  text-align: center;
  height: 100%;
}
body {
  margin: 0;
  background-color: #e2e2e2;
  height: 100%;
}
.pointer {
  cursor: pointer;
}
.router-view {
  margin: 0;
}
.el-header {
  margin: 0;
  padding: 0;
}
.el-footer {
  margin: 0;
  padding: 0;
  height: 256px;
}
.el-main {
  margin: 0;
  padding: 0;
}
</style>
