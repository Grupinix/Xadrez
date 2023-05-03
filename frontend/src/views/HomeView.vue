<template>
  <el-row>
    <el-col :span="4" :xs="{ span: 8 }">
    </el-col>
    <el-col :span="20" :xs="{ span: 16 }">
    </el-col>
  </el-row>
</template>

<script>
import { ElMessage } from "element-plus";

export default {
  name: "HomeView",

  components: {},

  data() {
    return {};
  },

  mounted() {
    this.timer = setInterval(async () => {}, 500);
  },

  beforeUnmount() {
    clearInterval(this.timer);
  },

  computed: {
  },

  async created() {
    const self = this;
    const headers = {
      "Content-Type": "application/json"
    };
    await fetch(`http://localhost:8000/api/client/`, headers)
      .then(async (response) => {
        if (response.ok) {
          return;
        }
        self.alertExpired();
      })
      .catch(() => {
        self.alertExpired();
      });
  },

  methods: {
    alertExpired() {
      localStorage.removeItem("userDTO");
      ElMessage.error("Sua sessão expirou, entre novamente por favor.");
    },
  },
};
</script>

<style scoped>
.cart-number {
  width: 8px;
  margin-right: 4px;
  height: 16px;
}
.el-menu {
  height: 100%;
}
</style>
