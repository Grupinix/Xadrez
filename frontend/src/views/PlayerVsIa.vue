<template>
  <el-row>
    <p>era para ter um tabuleiro aqui</p>
  </el-row>
</template>

<script>
export default {
  name: "PlayerVsIa",

  data() {
    return {
      playerDto: {},
      iaGameDto: {},
    };
  },

  async created() {
    this.iaGameDto = JSON.parse(localStorage.getItem("iaGameDto"));
    if (!this.iaGameDto) {
      this.$router.push({ path: "/inicio" });
    }

    this.playerDto = JSON.parse(localStorage.getItem("playerDto"));
  },
};
</script>

<style scoped></style>
