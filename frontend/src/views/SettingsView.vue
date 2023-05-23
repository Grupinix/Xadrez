<template>
  <el-row class="settings-view-row">
    <el-col class="settings-view-col">
      <section class="settings-title-box">
        <label class="settings-title-text">Dificuldade da I.A.</label>
      </section>
    </el-col>
    <el-col class="settings-view-col">
      <el-select
        v-model="value"
        :loading="loadingDifficulties"
        class="settings-select-box"
        placeholder="Select"
        size="large"
        @change="changeDifficulty"
      >
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
          class="settings-select-option"
        />
      </el-select>
    </el-col>
    <el-col class="settings-view-col">
      <section class="settings-title-box">
        <label class="settings-title-text">Tema</label>
      </section>
    </el-col>
    <el-col class="settings-view-col">
      <el-select
        v-model="theme"
        :loading="loadingThemes"
        class="settings-select-box"
        placeholder="Select"
        size="large"
        @change="changeTheme"
      >
        <el-option
          v-for="item in themes"
          :key="item.value"
          :label="item.label"
          :value="item.value"
          class="settings-select-option"
        />
      </el-select>
    </el-col>
    <el-col class="settings-view-col">
      <el-button
        class="settings-back-button"
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
  import ThemeService from "../services/themeService";

  const playerDto = ref<PlayerDto>(PlayerService.getPlayerDtoFromStorage());
  const themes = ref<{[key: string]: string;}[]>(ThemeService.getThemeList());
  const theme = ref<string>(ThemeService.getThemeName());
  const loadingDifficulties = ref<boolean>(false);
  const loadingThemes = ref<boolean>(false);
  const value = ref<string>(playerDto.value.gameDifficulty);
  const options = ref<{[key: string]: string;}[]>([
    {
      value: "EASY",
      label: "Fácil",
    },
    {
      value: "NORMAL",
      label: "Normal",
    },
    {
      value: "HARD",
      label: "Difícil",
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

  function changeTheme(value: string) {
    loadingThemes.value = true;
    ThemeService.setThemeName(value);
    location.reload();
  }

  function goToHome() {
    router.push({ path: "/inicio" });
  }
</script>
