// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

// Landing page
import LandingPage from "../views/LandingPage.vue";

// Home page (for authenticated users)
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

// Auth pages 
import RegisterPage from "../views/RegisterPage.vue";
import LoginPage from "../views/LoginPage.vue";

const routes = [
  // landing
  { path: "/", component: LandingPage },

  // auth (new)
  { path: "/register", component: RegisterPage },
  { path: "/login", component: LoginPage },

  // home (for authenticated users)
  { path: "/home", component: HomePage },

  // courses
  { path: "/courses", component: Courses },
  {
    path: "/courses/:courseId",
    component: CoursePage,
    children: [
      {
        path: "chapters",
        component: CourseChapters,
        children: [{ path: ":chapterId", component: CourseChapterPage }],
      },
    ],
  },

  // quiz
  { path: "/quiz/:chapterId", component: Quiz },

  // chapters
  { path: "/chapters", component: Chapters },
  {
    path: "/chapters/:id",
    component: ChapterPage,
    children: [{ path: "results", component: ChapterResults }],
  },

  // flashcards
  {
    path: "/flashcards",
    component: Flashcards,
    children: [
      { path: "generated", component: Generated },
      { path: "generated/loading", component: Loading },
    ],
  },

  // progress & quizzes
  { path: "/progress", component: Progress },
  { path: "/quizzes", component: Quizzes },

  // fallback
  { path: "/:pathMatch(.*)*", redirect: "/" },
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
