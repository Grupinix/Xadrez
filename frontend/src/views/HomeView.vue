<template>
  <el-row>
    <el-col :span="4" :xs="{ span: 8 }">
      <p>teste</p>
    </el-col>
    <el-col :span="20" :xs="{ span: 16 }">
      <p>teste</p>
    </el-col>
  </el-row>
</template>

<script>

export default {
  name: "HomeView",

  components: {},

  data() {
    return {};
  },

  mounted() {
    this.timer = setInterval(async () => {}, 500);
  },

  beforeUnmount() {
    clearInterval(this.timer);
  },
};
</script>

<style scoped>
</style>
