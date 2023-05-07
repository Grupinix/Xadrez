<!-- eslint-disable prettier/prettier -->
<script>
import { defineComponent } from "vue";
import * as logo from "../assets/logo.webp";

export default defineComponent({
  name: "BaseHeader",
  data() {
    return { logoImg: logo };
  },
});
</script>

<template>
  <section>
    <router-link to="/">
      <el-image :src="logoImg.default"></el-image>
    </router-link>
  </section>
</template>

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
