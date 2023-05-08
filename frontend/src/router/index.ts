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
      meta: {
        requiresPlayerDto: false,
        requiresIaGameDto: false,
      },
    },
    {
      path: "/inicio",
      name: "inicio",
      component: HomeView,
      meta: {
        requiresPlayerDto: true,
        requiresIaGameDto: false,
      },
    },
    {
      path: "/playervsia",
      name: "playervsia",
      component: () => import("../views/PlayerVsIa.vue"),
      meta: {
        requiresPlayerDto: true,
        requiresIaGameDto: true,
      },
    },
  ],
});

router.beforeEach((to, _, next) => {
  const requiresPlayerDto = to.meta.requiresPlayerDto;
  const requiresIaGameDto = to.meta.requiresIaGameDto;

  const playerDtoStringed = localStorage.getItem("playerDto");
  const iaGameDtoStringed = localStorage.getItem("iaGameDto");

  const notHasIaGameAndNeed = requiresIaGameDto && !iaGameDtoStringed;
  const notHasPlayerAndNeed = requiresPlayerDto && !playerDtoStringed;

  if (notHasIaGameAndNeed || notHasPlayerAndNeed) {
    return next({ path: "/" });
  }
  if (!requiresPlayerDto && playerDtoStringed) {
    return next({ path: "/inicio" });
  }

  return next();
});

export default router;