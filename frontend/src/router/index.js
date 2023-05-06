import { createRouter, createWebHistory } from "vue-router";

import EnterView from "../views/EnterView.vue";
import HomeView from "../views/HomeView.vue";

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
      component: HomeView,
      meta: { requiresAuth: true },
    },
    {
      path: "/playervsia",
      name: "playervsia",
      component: () => import("../views/PlayerVsIa.vue"),
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to, _, next) => {
  const requiresAuth = to.meta.requiresAuth;
  const playerUUID = JSON.parse(localStorage.getItem("playerDto"))?.uuid;
  if (requiresAuth && !playerUUID) {
    return next({ path: "/" });
  }

  return next();
});

export default router;
