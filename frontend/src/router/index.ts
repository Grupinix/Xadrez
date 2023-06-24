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
        requiresPlayerGameDto: false,
      },
    },
    {
      path: "/inicio",
      name: "inicio",
      component: HomeView,
      meta: {
        requiresPlayerDto: true,
        requiresIaGameDto: false,
        requiresPlayerGameDto: false,
      },
    },
    {
      path: "/playervsia",
      name: "playervsia",
      component: () => import("../views/PlayerVsIaView.vue"),
      meta: {
        requiresPlayerDto: true,
        requiresIaGameDto: true,
        requiresPlayerGameDto: false,
      },
    },
    {
      path: "/playervsplayer",
      name: "playervsplayer",
      component: () => import("../views/PlayerVsPlayerView.vue"),
      meta: {
        requiresPlayerDto: true,
        requiresIaGameDto: false,
        requiresPlayerGameDto: true,
      }
    },
    {
      path: "/settings",
      name: "settings",
      component: () => import("../views/SettingsView.vue"),
      meta: {
        requiresPlayerDto: true,
        requiresIaGameDto: false,
        requiresPlayerGameDto: false,
      },
    },
    {
      path: "/rooms",
      name: "rooms",
      component: () => import("../views/RoomsView.vue"),
      meta: {
        requiresPlayerDto: true,
        requiresIaGameDto: false,
        requiresPlayerGameDto: false,
      }
    },
    {
      path: "/ranking",
      name: "ranking",
      component: () => import("../views/RanksView.vue"),
      meta: {
        requiresPlayerDto: true,
        requiresIaGameDto: false,
        requiresPlayerGameDto: false,
      }
    }
  ],
});

router.beforeEach((to, _, next) => {
  const requiresPlayerDto = to.meta.requiresPlayerDto;
  const requiresIaGameDto = to.meta.requiresIaGameDto;
  const requiresPlayerGameDto = to.meta.requiresPlayerGameDto;

  const playerDtoStringed = localStorage.getItem("playerDto");
  const iaGameDtoStringed = localStorage.getItem("iaGameDto");
  const playerGameDtoStringed = localStorage.getItem("playerGameDto");

  const notHasIaGameAndNeed = requiresIaGameDto && !iaGameDtoStringed;
  const notHasPlayerAndNeed = requiresPlayerDto && !playerDtoStringed;
  const notHasPlayerGameAndNeed = requiresPlayerGameDto && !playerGameDtoStringed;

  if (notHasIaGameAndNeed || notHasPlayerAndNeed || notHasPlayerGameAndNeed) {
    return next({ path: "/" });
  }
  if (!requiresPlayerDto && playerDtoStringed) {
    return next({ path: "/inicio" });
  }

  return next();
});

export default router;
