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

<script>
import { shallowRef } from "vue";
import { Lock, Message } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import PlayerService from "@/services/playerService";

export default {
  name: "EnterView",

  data() {
    return {
      loading: false,
      lock: shallowRef(Lock),
      message: shallowRef(Message),
      model: {
        identifier: "",
      },
      rules: {
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
      },
    };
  },

  async created() {
    const playerDto = JSON.parse(localStorage.getItem("playerDto"));
    if (!playerDto) {
      return;
    }

    await PlayerService.verify(playerDto)
      .then(async (response) => {
        const result = await response.json();
        if (result) {
          this.$router.push({ path: "/inicio" });
        } else {
          localStorage.removeItem("playerDto");
        }
      })
      .catch(() => {
        localStorage.removeItem("playerDto");
      });
  },

  methods: {
    async register() {
      const self = this;
      const form = this.$refs.ruleForm;
      self.loading = true;

      let isValid = await form.validate((valid, fields) => {
        console.log(fields);
        if (!valid) {
          self.loading = false;
          ElMessage({
            message: "Preencha todos os campos corretamente.",
            type: "warning",
          });
        }
        return valid;
      });

      if (!isValid) {
        return;
      }

      const identifier = this.model.identifier;
      await PlayerService.register(identifier)
        .then(async (response) => {
          const data = await response.json();
          if (response.ok) {
            isValid = true;
            localStorage.setItem("playerDto", JSON.stringify(data));
          } else {
            self.alertRegisterFail();
            isValid = false;
            self.loading = false;
          }
        })
        .catch(() => {
          self.alertRegisterFail();
          isValid = false;
          self.loading = false;
        });

      if (isValid) {
        this.$router.push({ path: "/inicio" });
      }
    },

    alertRegisterFail() {
      ElMessage.error("Credenciais inválidas.");
    },
  },
};
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
