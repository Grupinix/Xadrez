<template>
  <el-row style="padding-top: 32px; padding-bottom: 32px">
    <el-table :data="filterGameList" style="width: 75%; margin: auto">
      <el-table-column label="Tipo" prop="gameType" />
      <el-table-column label="Jogador" prop="identifier" />
      <el-table-column label="Movimentos" prop="moves" />
      <el-table-column label="Data" prop="dateTime" />
    </el-table>
  </el-row>
</template>

<script setup lang="ts">
  import { computed, ref, onMounted } from "vue";
  import RankService from "../services/rankService";

  const timer = ref<number>();
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

  function typeLabel(gameType: string): string {
    if (gameType === "PLAYER_IA_CLASSIC") {
      return "Jogador vs IA";
    }
    return "Jogador vs Jogador";
  }

  function loadGameList() {
    RankService.list()
      .then((response) => {
        response.json().then((result) => {
          if (result) {
            gameList.value = result;
            gameList.value.forEach((game) => {
              game.gameType = typeLabel(game.gameType);
            });
          }
        });
      });
  }

  loadGameList();

</script>
