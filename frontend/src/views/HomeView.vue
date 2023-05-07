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
      <el-button color="#626aef" size="large" disabled>CONFIGURAÇÕES</el-button>
    </el-col>
    <el-col :xs="{ span: 24 }">
      <el-button color="#626aef" size="large" disabled>CRIAR SALA</el-button>
    </el-col>
    <el-col :xs="{ span: 24 }">
      <el-button color="#626aef" size="large" disabled>SALAS</el-button>
    </el-col>
    <el-col :xs="{ span: 24 }">
      <el-button color="#626aef" size="large" disabled>RANK</el-button>
    </el-col>
  </el-row>
</template>

<script>
import { ElMessage } from "element-plus";
import GameService from "@/services/gameService";
import PlayerService from "@/services/playerService";

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

    const playerDto = JSON.parse(localStorage.getItem("playerDto"));
    await PlayerService.verify(playerDto).then(async (response) => {
      const result = await response.json();
      if (!result) {
        localStorage.removeItem("playerDto");
        this.$router.push({ path: "/" });
      }
    });

    const iaGameDto = JSON.parse(localStorage.getItem("iaGameDto"));
    if (iaGameDto) {
      const gameType = iaGameDto.gameType;
      const gameId = iaGameDto.id;

      await GameService.check(gameType, gameId)
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
    }
  },

  methods: {
    async playVsIa() {
      const self = this;

      self.vsIaLoading = true;
      const playerDto = JSON.parse(localStorage.getItem("playerDto"));
      const gameType = "PLAYER_IA_CLASSIC";

      await GameService.create(gameType, playerDto)
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
