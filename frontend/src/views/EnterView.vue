<template>
  <el-row id="main-row" type="flex" justify="center">
    <el-card>
      <template #header>
        <span class="card-title">
          Olá! Antes de jogar defina um identificador com exatos 8 caracteres!
        </span>
      </template>

      <el-form :model="model" :rules="rules" ref="ruleForm">
        <el-form-item prop="identifier">
          <el-input
            placeholder="Identificador..."
            :prefix-icon="lock"
            v-model="model.identifier"
          ></el-input>
        </el-form-item>

        <el-row type="flex" justify="center">
          <el-col>
            <el-button type="primary" :loading="loading" @click="register">
              CONFIRMAR
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </el-row>
</template>

<script setup>
import { ref, shallowRef } from "vue";

import router from "../router";
import { Lock } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import PlayerService from "@/services/playerService";

const playerDto = ref({});
const loading = ref(false);
const lock = shallowRef(Lock);
const ruleForm = ref({});
const model = ref({ identifier: "" });
const rules = ref({
  identifier: [
    {
      required: true,
      message: "Insira um identificador",
      trigger: "blur",
    },
    {
      min: 8,
      max: 8,
      message: "Precisa possuir exatos 8 caracteres",
      trigger: "blur",
    },
  ],
});

function register() {
  loading.value = true;
  ruleForm.value.validate((valid, fields) => {
      console.log(fields);
      if (!valid) {
        loading.value = false;
        ElMessage({
          message: "Preencha todos os campos corretamente.",
          type: "warning",
        });
      }
      return valid;
    })
    .then((isValid) => {
      if (!isValid) {
        return;
      }

      const identifier = model.value.identifier;
      PlayerService.register(identifier)
        .then((response) => {
          response.json().then((data) => {
            if (response.ok) {
              isValid = true;
              localStorage.setItem("playerDto", JSON.stringify(data));
              router.push({ path: "/inicio" });
            } else {
              alertRegisterFail();
              isValid = false;
              loading.value = false;
            }
          });
        })
        .catch(() => {
          alertRegisterFail();
          isValid = false;
          loading.value = false;
        });
    });
}

function alertRegisterFail() {
  ElMessage.error("Credenciais inválidas.");
}

playerDto.value = JSON.parse(localStorage.getItem("playerDto"));
if (playerDto.value) {
  PlayerService.verify(playerDto)
    .then((response) => {
      response.json().then((result) => {
        if (result) {
          router.push({ path: "/inicio" });
        } else {
          localStorage.removeItem("playerDto");
        }
      });
    })
    .catch(() => {
      localStorage.removeItem("playerDto");
    });
}
</script>

<style scoped>
#main-row {
  padding-top: 128px;
  padding-bottom: 128px;
}

.card-title {
  color: #4d5758;
  font-weight: bolder;
  font-size: large;
}
</style>
