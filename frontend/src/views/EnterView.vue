<template>
  <el-row type="flex" justify="center">
    <el-card>
      <template #header>
        <span class="card-title">Já possui um cadastro? Faça login</span>
      </template>

      <el-form :model="model" :rules="rules" ref="ruleForm">
        <el-form-item prop="email">
          <el-input
            placeholder="Email..."
            :prefix-icon="lock"
            v-model="model.email"
          ></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            placeholder="Senha..."
            type="password"
            :prefix-icon="lock"
            v-model="model.password"
            show-password
          ></el-input>
        </el-form-item>

        <el-row type="flex" justify="center">
          <el-col>
            <el-button type="primary" :loading="loading" @click="login">
              ENTRAR
            </el-button>
          </el-col>
          <el-col>
            <el-link type="primary" @click="register">
              Não possui cadastro?
            </el-link>
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

export default {
  name: "EnterView",

  data() {
    return {
      loading: false,
      lock: shallowRef(Lock),
      message: shallowRef(Message),
      model: {
        email: "",
        password: "",
      },
      rules: {
        email: [
          {
            required: true,
            message: "Insira um endereço de email",
            trigger: "blur",
          },
          {
            type: "email",
            message: "Insira um endereço de email válido",
            trigger: "blur",
          },
        ],
        password: [
          { required: true, message: "Insira uma senha", trigger: "blur" },
          {
            min: 5,
            max: 20,
            message: "Escolha uma senha entre 5 à 20 caracteres",
            trigger: "blur",
          },
        ],
      },
    };
  },

  async created() {
    const self = this;
    const userDTO = JSON.parse(localStorage.getItem("userDTO"));
    if (!userDTO) {
      return;
    }

    const userToken = `Bearer ${userDTO.token}`;
    const headers = { "Content-Type": "application/json" };
    const payload = JSON.stringify({ token: userToken });
    let isValid = true;

    await fetch("http://localhost:8000/api/user/check/", {
      method: "POST",
      headers: headers,
      body: payload,
    })
      .then((response) => {
        if (!response.ok) {
          self.alertExpired();
          isValid = false;
          return;
        }
      })
      .catch(() => {
        self.alertExpired();
        isValid = false;
        return;
      });

    if (isValid) {
      this.$router.push({ path: "/inicio" });
    }
  },

  methods: {
    async login() {
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

      const headers = { "Content-Type": "application/json" };
      const payload = JSON.stringify({
        login: this.model.email,
        password: this.model.password,
      });
      await fetch("http://localhost:8000/api/user/auth/", {
        method: "POST",
        headers: headers,
        body: payload,
      })
        .then(async (response) => {
          const data = await response.json();
          if (!response.ok) {
            self.alertLoginFaill();
            isValid = false;
            self.loading = false;
            return;
          }
          localStorage.setItem("userDTO", JSON.stringify(data));
        })
        .catch(() => {
          self.alertLoginFaill();
          isValid = false;
          self.loading = false;
          return;
        });

      if (isValid) {
        this.$router.push({ path: "/inicio" });
      }
    },

    register() {
      this.$router.push({ path: "/cadastro" });
    },

    alertExpired() {
      localStorage.removeItem("userDTO");
      ElMessage.error("Sua sessão expirou, entre novamente por favor.");
    },

    alertLoginFaill() {
      ElMessage.error("Credenciais inválidas.");
    },
  },
};
</script>

<style scoped>
.el-link {
  margin-top: 16px;
}
.el-row {
  padding-top: 32px;
  padding-bottom: 32px;
}
.el-card {
  width: 512px;
}
.card-title {
  color: #4d5758;
  font-weight: bolder;
  font-size: large;
}
.el-form-item {
  text-align: center;
}
.el-button {
  width: 128px;
  height: 48px;
  font-size: large;
}
</style>
