<template>
  <el-row class="enter-view-row" type="flex" justify="center">
    <el-card class="enter-view-card">
      <template #header>
        <span class="enter-view-card-title">
          Olá! Antes de jogar defina um identificador com exatos 8 caracteres!
        </span>
      </template>

      <el-form
        ref="ruleForm"
        class="enter-view-form"
        :model="model"
        :rules="rules"
      >
        <el-form-item
          class="enter-view-form-identifier-block"
          prop="identifier"
        >
          <el-input
            v-model="model.identifier"
            class="enter-view-form-identifier-block-input"
            placeholder="Identificador..."
            :prefix-icon="lock"
          ></el-input>
        </el-form-item>

        <el-row
          class="enter-view-form-identifier-block-button-row"
          type="flex"
          justify="center"
        >
          <el-col class="enter-view-form-identifier-block-button-col">
            <el-button
              class="enter-view-form-identifier-block-button-col-button"
              type="primary"
              :loading="loading"
              @click="register"
            >
              CONFIRMAR
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </el-row>
</template>

<script setup lang="ts">
  import { reactive, ref, shallowRef } from "vue";
  import type { FormInstance, FormRules } from "element-plus";

  import router from "../router";
  import { Lock } from "@element-plus/icons-vue";
  import { ElMessage } from "element-plus";
  import PlayerService from "../services/playerService";

  const loading = ref<boolean>(false);
  const lock = shallowRef(Lock);
  const ruleForm = ref<FormInstance>()
  const model = reactive<PlayerDto>({
    identifier: "",
    uuid: "",
    gameDifficulty: "",
  });
  const rules = reactive<FormRules>({
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
    if (!ruleForm.value) {
      return;
    }

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

        const identifier = model.identifier;
        PlayerService.register(identifier)
          .then((response) => {
            response.json().then((data) => {
              if (response.ok) {
                localStorage.setItem("playerDto", JSON.stringify(data));
                router.push({ path: "/inicio" });
              } else {
                alertRegisterFail();
                loading.value = false;
              }
            });
          })
          .catch(() => {
            alertRegisterFail();
            loading.value = false;
          });
      });
  }

  function alertRegisterFail() {
    ElMessage.error("Credenciais inválidas.");
  }
</script>
