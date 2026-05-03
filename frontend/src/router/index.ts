import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "home",
      component: () => import("../views/HomeView.vue"),
    },
    {
      path: "/project/:id",
      name: "dashboard",
      component: () => import("../views/DashboardView.vue"),
    },
    {
      path: "/project/:id/browse",
      name: "browse",
      component: () => import("../views/CategoryView.vue"),
    },
    {
      path: "/project/:id/realms",
      name: "realms",
      component: () => import("../views/RealmView.vue"),
    },
    {
      path: "/project/:id/characters",
      name: "characters",
      component: () => import("../views/CharacterView.vue"),
    },
    {
      path: "/project/:id/factions",
      name: "factions",
      component: () => import("../views/FactionView.vue"),
    },
    {
      path: "/project/:id/techniques",
      name: "techniques",
      component: () => import("../views/TechniqueView.vue"),
    },
    {
      path: "/project/:id/graph",
      name: "graph",
      component: () => import("../views/GraphView.vue"),
    },
    {
      path: "/project/:id/search",
      name: "search",
      component: () => import("../views/SearchView.vue"),
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/LoginView.vue"),
    },
    {
      path: "/register",
      name: "register",
      component: () => import("../views/RegisterView.vue"),
    },
    {
      path: "/project/:id/admin",
      name: "admin",
      component: () => import("../views/AdminView.vue"),
    },
  ],
});

export default router;
