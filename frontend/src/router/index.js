import { createRouter, createWebHistory } from "vue-router";
import EnterView from "../views/EnterView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: EnterView,
      meta: { requiresAuth: false },
    },
    {
      path: "/inicio",
      name: "inicio",
      component: () => import("../views/HomeView.vue"),
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to, _, next) => {
  const requiresAuth = to.meta.requiresAuth;
  const userDTO = JSON.parse(localStorage.getItem("userDTO"))?.token;
  if (requiresAuth && !userDTO) {
    return next({ path: "/" });
  }

  if (!requiresAuth && userDTO) {
    return next({ path: "/inicio" });
  }

  next();
});

export default router;
