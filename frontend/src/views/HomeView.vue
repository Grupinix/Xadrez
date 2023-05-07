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

<script setup>
import { ref } from "vue";

import router from "../router";
import { ElMessage } from "element-plus";
import GameService from "@/services/gameService";
import PlayerService from "@/services/playerService";

const isValid = ref(true);
const vsIaLoading = ref(false);
const playerDto = ref({});

function playVsIa() {
  vsIaLoading.value = true;
  playerDto.value = JSON.parse(localStorage.getItem("playerDto"));

  GameService.create("PLAYER_IA_CLASSIC", playerDto.value)
    .then((response) => {
      response.json().then((data) => {
        if (response.ok) {
          localStorage.setItem("iaGameDto", JSON.stringify(data));
          if (isValid.value) {
            router.push({ path: "/playervsia" });
          }
        } else {
          alertCreateFail();
          isValid.value = false;
          vsIaLoading.value = false;
        }
      });
    })
    .catch(() => {
      alertCreateFail();
      isValid.value = false;
      vsIaLoading.value = false;
    });
}

function alertCreateFail() {
  ElMessage.error("Credenciais inválidas.");
}

playerDto.value = JSON.parse(localStorage.getItem("playerDto"));
PlayerService.verify(playerDto.value).then((response) => {
  response.json().then((result) => {
    if (!result) {
      localStorage.removeItem("playerDto");
      router.push({ path: "/" });
    }

    const iaGameDto = JSON.parse(localStorage.getItem("iaGameDto"));
    if (iaGameDto) {
      const gameType = iaGameDto.gameType;
      const gameId = iaGameDto.id;

      GameService.check(gameType, gameId)
        .then((response) => {
          response.json().then((result) => {
            if (!result) {
              isValid.value = false;
            }
            if (isValid.value && gameType === "PLAYER_IA_CLASSIC") {
              router.push({ path: "/playervsia" });
            }
          });
        })
        .catch(() => {
          isValid.value = false;
        });
    }
  });
});
</script>

<style scoped>
.el-col {
  margin: 16px 0;
}
.el-button {
  font-size: 24px;
}
</style>
