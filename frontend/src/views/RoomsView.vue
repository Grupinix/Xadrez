<template>
  <el-row style="padding-top: 32px; padding-bottom: 32px">
    <el-table :data="filterGameList" style="width: 75%; margin: auto;">
      <el-table-column label="Tipo" prop="gameType" />
      <el-table-column label="Jogador Branco" prop="whitePlayerIdentifier" />
      <el-table-column label="Jogador Preto" prop="blackPlayerIdentifier" />
      <el-table-column align="right">
        <template #header>
          <el-input
            v-model="search"
            size="default"
            placeholder="Tipo de jogo..." />
        </template>
        <template #default="scope">
          <el-button
            v-if="filterGameList[scope.$index].gameType !== 'Jogador vs IA'"
            size="default"
            class="home-view-button"
            @click="enterGame(scope.row)"
          >Entrar</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-row>
</template>

<script setup lang="ts">
  import { computed, ref, onMounted } from "vue";
  import GameService from "../services/gameService";
  import PlayerService from "../services/playerService";
  import { ElLoading } from 'element-plus'
  import router from "../router";

  const timer = ref<number>();
  const playerDto = ref<PlayerDto>(PlayerService.getPlayerDtoFromStorage());
  const gameList = ref<GameDto[]>([]);
  const search = ref<string>("")
  const filterGameList = computed(() =>
    gameList.value.filter(
      (game) => !search.value || game.gameType.toLowerCase().includes(
        search.value.toLowerCase()
      )
    )
  );

  onMounted(() => {
    timer.value = window.setInterval(async () => {
      loadGameList();
    }, 5000);
  });

  function labelType(gameType: string): string {
    if (gameType === "Jogador vs IA") {
      return "PLAYER_IA_CLASSIC";
    }
    return "PLAYER_PLAYER_CLASSIC";
  }

  function typeLabel(gameType: string): string {
    if (gameType === "PLAYER_IA_CLASSIC") {
      return "Jogador vs IA";
    }
    return "Jogador vs Jogador";
  }

  function enterGame(gameDto: GameDto) {
    const loading = ElLoading.service({
      lock: true,
      text: "Carregando",
      background: "rgba(0,0,0,0.8)",
    });

    PlayerService.verify(playerDto.value)
      .then((response) => {
        response.json().then((result) => {
          if (result) {
            GameService.enter(labelType(gameDto.gameType), gameDto.id, playerDto.value)
              .then((response) => {
                response.json().then((result) => {
                  if (result) {
                    localStorage.setItem("playerGameDto", JSON.stringify(result));
                    setTimeout(function() {
                      loading.close();
                      router.push({ path: "/playervsplayer" });
                    }, 1000);
                  }
                  else {
                    loading.close();
                  }
                });
              });
          }
          else {
            loading.close();
            router.push({ path: "/" });
          }
        });
      });
  }

  function loadGameList() {
    GameService.list()
      .then((response) => {
        response.json().then((result) => {
          if (result) {
            gameList.value = result;
            gameList.value.forEach((game) => {
              PlayerService.get(game.whitePlayerUUID).then((rp) => {
                rp.json().then((rs) => {
                  game.whitePlayerIdentifier = rs.identifier;
                });
              });
              if (game.gameType !== "PLAYER_IA_CLASSIC") {
                if (game.blackPlayerUUID) {
                  PlayerService.get(game.blackPlayerUUID).then((rp) => {
                    rp.json().then((rs) => {
                      game.blackPlayerIdentifier = rs.identifier;
                    });
                  });
                }
              }
              else {
                game.blackPlayerIdentifier = "EU ROBÔ!";
              }
              game.gameType = typeLabel(game.gameType);
            });
          }
        });
      });
  }

  loadGameList();

</script>
