// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

// Landing page
import LandingPage from "../views/LandingPage.vue";

// Auth pages
import RegisterPage from "../views/RegisterPage.vue";
import LoginPage from "../views/LoginPage.vue";

// Home page (authenticated)
import HomePage from "../views/HomePage.vue";

// Courses
import Courses from "../views/courses/Index.vue";
import CoursePage from "../views/courses/_courseId/Index.vue";
import CourseChapters from "../views/courses/_courseId/chapters/Index.vue";
import CourseChapterPage from "../views/courses/_courseId/chapters/_chapterId.vue";

// Chapters
import Chapters from "../views/chapters/Index.vue";
import ChapterPage from "../views/chapters/_id/Index.vue";
import ChapterResults from "../views/chapters/_id/Results.vue";

// Flashcards
import Flashcards from "../views/flashcards/Index.vue";
import Generated from "../views/flashcards/generated/Index.vue";
import Loading from "../views/flashcards/generated/Loading.vue";

// Progress & Quizzes
import Progress from "../views/progress/Index.vue";
import Quizzes from "../views/quizzes/Index.vue";
import Quiz from "../views/quiz/Index.vue";

const routes = [
  // Public routes
  { path: "/", component: LandingPage },
  { path: "/register", component: RegisterPage },
  { path: "/login", component: LoginPage },

  // Authenticated routes
  { path: "/home", component: HomePage, meta: { requiresAuth: true } },
  { path: "/courses", component: Courses, meta: { requiresAuth: true } },
  {
    path: "/courses/:courseId",
    component: CoursePage,
    meta: { requiresAuth: true },
    children: [
      {
        path: "chapters",
        component: CourseChapters,
        children: [
          {
            path: ":chapterId",
            component: CourseChapterPage,
          },
        ],
      },
    ],
  },
  { path: "/quiz/:chapterId", component: Quiz, meta: { requiresAuth: true } },
  { path: "/chapters", component: Chapters, meta: { requiresAuth: true } },
  {
    path: "/chapters/:id",
    component: ChapterPage,
    meta: { requiresAuth: true },
    children: [
      {
        path: "results",
        component: ChapterResults,
      },
    ],
  },
  {
    path: "/flashcards",
    component: Flashcards,
    meta: { requiresAuth: true },
    children: [
      { path: "generated", component: Generated },
      { path: "generated/loading", component: Loading },
    ],
  },
  { path: "/progress", component: Progress, meta: { requiresAuth: true } },
  { path: "/quizzes", component: Quizzes, meta: { requiresAuth: true } },

  // Catch-all
  { path: "/:pathMatch(.*)*", redirect: "/" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Add global navigation guard
router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem("token");

  if (to.meta.requiresAuth && !isAuthenticated) {
    next("/login");
  } else if ((to.path === "/login" || to.path === "/register") && isAuthenticated) {
    next("/home");
  } else {
    next();
  }
});

export default router;
