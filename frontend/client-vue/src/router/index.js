import { createRouter, createWebHistory } from 'vue-router'

// chapters
import Chapters       from '../views/chapters/Index.vue'
import ChapterPage    from '../views/chapters/_id/Index.vue'
import ChapterResults from '../views/chapters/_id/Results.vue'

// courses
import Courses           from '../views/courses/Index.vue'
import CoursePage        from '../views/courses/_courseId/Index.vue'
import CourseChapters    from '../views/courses/_courseId/chapters/Index.vue'
import CourseChapterPage from '../views/courses/_courseId/chapters/_chapterId.vue'

// flashcards
import Flashcards from '../views/flashcards/Index.vue'
import Generated  from '../views/flashcards/generated/Index.vue'
import Loading    from '../views/flashcards/generated/Loading.vue'

// progress & quizzes
import Progress from '../views/progress/Index.vue'
import Quizzes  from '../views/quizzes/Index.vue'

const routes = [
  // chapters
  { path: '/chapters', component: Chapters },
  {
    path: '/chapters/:id',
    component: ChapterPage,
    children: [
      { path: 'results', component: ChapterResults }
    ]
  },

  // courses
  { path: '/courses', component: Courses },
  {
    path: '/courses/:courseId',
    component: CoursePage,
    children: [
      {
        path: 'chapters',
        component: CourseChapters,
        children: [
          { path: ':chapterId', component: CourseChapterPage }
        ]
      }
    ]
  },

  // flashcards
  {
    path: '/flashcards',
    component: Flashcards,
    children: [
      { path: 'generated', component: Generated },
      { path: 'generated/loading', component: Loading }
    ]
  },

  // progress & quizzes
  { path: '/progress', component: Progress },
  { path: '/quizzes', component: Quizzes },

  // catch-all redirect
  { path: '/', redirect: '/courses' }
]

export default createRouter({
  history: createWebHistory(),
  routes
})

