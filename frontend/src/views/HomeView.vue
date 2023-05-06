<template>
  <el-row>
    <el-col :xs="{ span: 24 }">
      <el-button
        color="#626aef"
        size="large"
        :loading="vsIaLoading"
        @click="playVsIa"
      >
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
      isValid: true,
      vsIaLoading: false,
    };
  },

  mounted() {
    this.timer = setInterval(async () => {}, 500);
  },

  beforeUnmount() {
    clearInterval(this.timer);
  },

  async created() {
    const self = this;

    const url = import.meta.env.PROD ? "https://xadrez.eterniaserver.com.br" : "http://localhost:8000";
    const iaGameDto = JSON.parse(localStorage.getItem("iaGameDto"));
    const headers = { "Content-Type": "application/json" };
    const gameType = iaGameDto.gameType;
    const gameId = iaGameDto.id;

    await fetch(`${url}/api/game/check/${gameType}/${gameId}/`, {
      method: "GET",
      headers: headers,
    })
      .then(async (response) => {
        const result = await response.json();
        if (!result) {
          self.isValid = false;
        }
      })
      .catch(() => {
        self.isValid = false;
      });

    if (this.isValid && gameType === "PLAYER_IA_CLASSIC") {
      this.$router.push({ path: "/playervsia" });
    }
  },

  methods: {
    async playVsIa() {
      const self = this;

      self.vsIaLoading = true;
      const url = import.meta.env.PROD ? "https://xadrez.eterniaserver.com.br" : "http://localhost:8000";
      const playerDto = JSON.parse(localStorage.getItem("playerDto"));
      const headers = { "Content-Type": "application/json" };
      const payload = JSON.stringify({
        uuid: playerDto.uuid,
        identifier: playerDto.identifier,
      });

      await fetch(`${url}/api/game/create/PLAYER_IA_CLASSIC/`, {
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
            self.isValid = false;
            self.vsIaLoading = false;
          }
        })
        .catch(() => {
          self.alertCreateFail();
          self.isValid = false;
          self.vsIaLoading = false;
        });

      if (self.isValid) {
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
