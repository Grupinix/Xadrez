<template>
  <el-row>
    <el-col :xs="{ span: 24 }">
      <el-button
        color="#626aef"
        size="large"
        :loading="vsIaLoading"
        @click="playVsIa">
        JOGAR CONTRA IA
      </el-button>
    </el-col>
    <el-col :xs="{ span: 24 }">
      <el-button color="#626aef" size="large">CONFIGURAÇÕES</el-button>
    </el-col>
    <el-col :xs="{ span: 24 }">
      <el-button color="#626aef" size="large">CRIAR SALA</el-button>
    </el-col>
    <el-col :xs="{ span: 24 }">
      <el-button color="#626aef" size="large">SALAS</el-button>
    </el-col>
    <el-col :xs="{ span: 24 }">
      <el-button color="#626aef" size="large">RANK</el-button>
    </el-col>
  </el-row>
</template>

<script>
import { ElMessage } from "element-plus";

export default {
  name: "HomeView",

  components: {},

  data() {
    return {
      vsIaLoading: false,
    };
  },

  mounted() {
    this.timer = setInterval(async () => {}, 500);
  },

  beforeUnmount() {
    clearInterval(this.timer);
  },

  methods: {
    async playVsIa() {
      const self = this;
      let isValid = true;

      self.vsIaLoading = true;
      const playerDto = JSON.parse(localStorage.getItem("playerDto"));
      const headers = { "Content-Type": "application/json" };
      const payload = JSON.stringify({
        uuid: playerDto.uuid,
        identifier: playerDto.identifier,
      });

      await fetch(`http://localhost:8000/api/game/create/PLAYER_IA_CLASSIC`, {
        method: "POST",
        headers: headers,
        body: payload,
      })
        .then(async (response) => {
          const data = await response.json();
          if (response.ok) {
            localStorage.setItem("iaGameDto", JSON.stringify(data));
          } else {
            self.alertCreateFail();
            isValid = false;
            self.loading = false;
          }
        })
        .catch(() => {
          self.alertCreateFail();
          isValid = false;
          self.loading = false;
        });

      if (isValid) {
        this.$router.push({ path: "/playervsia" });
      }
    },

    alertCreateFail() {
      ElMessage.error("Credenciais inválidas.");
    },
  },
};
</script>

<style scoped>
.el-col {
  margin: 16px 0;
}
.el-button {
  font-size: 24px;
}
</style>
