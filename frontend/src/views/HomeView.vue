<template>
  <el-row class="home-view-row">
    <el-col class="home-view-col">
      <el-button
        class="home-view-button btn btn-success"
        size="large"
        :loading="vsIaLoading"
        @click="playVsIa"
      >
        JOGAR CONTRA IA
      </el-button>
    </el-col>
    <el-col class="home-view-col">
      <el-button
        class="home-view-button btn btn-success"
        size="large"
        @click="loadSettings"
      >
        CONFIGURAÇÕES
      </el-button>
    </el-col>
    <el-col class="home-view-col">
      <el-button
        class="home-view-button btn btn-success"
        size="large"
        @click="createRoom"
      >CRIAR SALA
      </el-button>
    </el-col>
    <el-col class="home-view-col">
      <el-button
        class="home-view-button btn btn-success"
        size="large"
        @click="loadRooms"
      >
        SALAS
      </el-button>
    </el-col>
    <el-col class="home-view-col">
      <el-button
        class="home-view-button btn btn-success"
        size="large"
        @click="loadRanking"
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
  const vsPlayerLoading = ref<boolean>(false);
  const playerDto = ref<PlayerDto>(PlayerService.getPlayerDtoFromStorage());

  function playVsIa() {
    vsIaLoading.value = true;
    PlayerService.verify(playerDto.value).then((response) => {
      response.json().then((data) => {
        if (data) {
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
        else {
          alertCreateFail();
          vsIaLoading.value = false;
        }
      });
    });
  }

  function loadSettings() {
    router.push({ path: "/settings" });
  }

  function loadRooms() {
    router.push({ path: "/rooms" });
  }

  function loadRanking() {
    router.push({ path: "/ranking" });
  }

  function alertCreateFail() {
    ElMessage.error("Credenciais inválidas.");
  }

  function createRoom() {
    vsPlayerLoading.value = true;

    PlayerService.verify(playerDto.value).then((response) => {
      response.json().then((data) => {
        if (data) {
          GameService.create("PLAYER_PLAYER_CLASSIC", playerDto.value)
            .then((response) => {
              response.json().then((data) => {
                if (response.ok) {
                  localStorage.setItem("playerGameDto", JSON.stringify(data));
                  setTimeout(function() {
                    router.push({ path: "/playervsplayer" });
                  }, 250);
                }
                else {
                  alertCreateFail();
                  vsPlayerLoading.value = false;
                }
              });
            })
            .catch(() => {
              alertCreateFail();
              vsPlayerLoading.value = false;
            });        }
        else {
          alertCreateFail();
          vsPlayerLoading.value = false;
        }
      });
    });
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

  function hasPlayerGameInProgress() {
    if (!localStorage.getItem("playerGameDto")) {
      return true;
    }

    const playerGameDto: GameDto = GameService.getPlayerGameDtoFromStorage();
    const gameType = playerGameDto.gameType;
    const gameId = playerGameDto.id;

    if (!gameType || !gameId) {
      localStorage.removeItem("playerGameDto");
    }

    GameService.check(gameType, gameId)
      .then((response) => {
        response.json().then((result) => {
          if (result && gameType === "PLAYER_PLAYER_CLASSIC") {
            setTimeout(function() {
              router.push({ path: "/playervsplayer" });
            }, 250);
          }
        });
      });
  }

  hasIaGameInProgress();
  hasPlayerGameInProgress();
</script>
