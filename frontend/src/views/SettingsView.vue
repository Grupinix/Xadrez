<template>
  <el-row>
    <el-col :xs="{ span: 24 }">
      <el-select
        v-model="value"
        :loading="loadingDifficulties"
        class="m-2"
        placeholder="Select"
        size="large"
        @change="changeDifficulty"
      >
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-col>
    <el-col :xs="{ span: 24 }">
      <el-button
        color="#626aef"
        size="large"
        @click="goToHome"
      >
        VOLTAR
      </el-button>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElLoading } from "element-plus";
import router from "../router";
import PlayerService from "../services/playerService";

const playerDto = ref<PlayerDto>(PlayerService.getPlayerDtoFromStorage());
const loadingDifficulties = ref<boolean>(false);
const value = ref<string>(playerDto.value.gameDifficulty);
const options = ref<{[key: string]: string;}[]>([
  {
    "value": "EASY",
    "label": "Fácil",
  },
  {
    "value": "NORMAL",
    "label": "Normal",
  },
  {
    "value": "HARD",
    "label": "Difícil",
  },
]);

function changeDifficulty(value: string) {
  loadingDifficulties.value = true;

  const loading = ElLoading.service({
    lock: true,
    text: "Carregando",
    background: "rgba(0,0,0,0.8)",
  });

  PlayerService.setGameDifficulty(value, playerDto.value)
    .then((response) => {
      response.json().then((result) => {
        localStorage.setItem("playerDto", JSON.stringify(result));
        playerDto.value = result;
        loading.close();
        loadingDifficulties.value = false;
      });
    })
    .catch(() => {
      loading.close();
      loadingDifficulties.value = false;
    });
}

function goToHome() {
  router.push({ path: "/inicio" });
}
</script>

<style scoped>
  .el-col {
    margin: 16px 0;
  }
  .el-button {
    font-size: 24px;
  }
</style>