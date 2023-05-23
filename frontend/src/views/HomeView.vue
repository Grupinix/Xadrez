<template>
  <el-row class="home-view-row">
    <el-col class="home-view-col">
      <el-button
        class="home-view-button"
        size="large"
        :loading="vsIaLoading"
        @click="playVsIa"
      >
        JOGAR CONTRA IA
      </el-button>
    </el-col>
    <el-col class="home-view-col">
      <el-button
        class="home-view-button"
        size="large"
        @click="loadSettings"
      >
        CONFIGURAÇÕES
      </el-button>
    </el-col>
    <el-col class="home-view-col">
      <el-button
        class="home-view-button"
        size="large"
        disabled
      >CRIAR SALA
      </el-button>
    </el-col>
    <el-col class="home-view-col">
      <el-button
        class="home-view-button"
        size="large"
        disabled
      >
        SALAS
      </el-button>
    </el-col>
    <el-col class="home-view-col">
      <el-button
        class="home-view-button"
        size="large"
        disabled
      >
        RANK
      </el-button>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
  import { ref } from "vue";

  import router from "../router";
  import { ElMessage } from "element-plus";
  import GameService from "../services/gameService";
  import PlayerService from "../services/playerService";

  const vsIaLoading = ref<boolean>(false);
  const playerDto = ref<PlayerDto>(PlayerService.getPlayerDtoFromStorage());

  function playVsIa() {
    vsIaLoading.value = true;

    GameService.create("PLAYER_IA_CLASSIC", playerDto.value)
      .then((response) => {
        response.json().then((data) => {
          if (response.ok) {
            localStorage.setItem("iaGameDto", JSON.stringify(data));
            setTimeout(function() {
              router.push({ path: "/playervsia" });
            }, 250);
          }
          else {
            alertCreateFail();
            vsIaLoading.value = false;
          }
        });
      })
      .catch(() => {
        alertCreateFail();
        vsIaLoading.value = false;
      });
  }

  function loadSettings() {
    router.push({ path: "/settings" });
  }

  function alertCreateFail() {
    ElMessage.error("Credenciais inválidas.");
  }

  function hasIaGameInProgress() {
    if (!localStorage.getItem("iaGameDto")) {
      return true;
    }

    const iaGameDto: GameDto = GameService.getIaGameDtoFromStorage();
    const gameType = iaGameDto.gameType;
    const gameId = iaGameDto.id;

    if (!gameType || !gameId) {
      localStorage.removeItem("iaGameDto");
    }

    GameService.check(gameType, gameId)
      .then((response) => {
        response.json().then((result) => {
          if (result && gameType === "PLAYER_IA_CLASSIC") {
            setTimeout(function() {
              router.push({ path: "/playervsia" });
            }, 250);
          }
        });
      });
  }

  hasIaGameInProgress();
</script>
