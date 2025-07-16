<template>
  <div data-theme="lofi" class="min-h-screen bg-base-200 flex flex-col">
    <nav class="navbar bg-base-100 px-6 py-4 shadow-sm">
      <div class="flex-1 flex items-center">
        <BookOpenIcon class="w-8 h-8 text-primary" />
        <span class="text-2xl font-bold ml-2">Study Assistant</span>
      </div>
      <div class="flex-none">
        <button @click="goBack" class="btn btn-ghost">
          <ArrowLeftIcon class="w-5 h-5 mr-2" />
          {{ backButtonText }}
        </button>
      </div>
    </nav>

    <div class="flex-1 container mx-auto py-8">
      <div v-if="loading" class="text-center py-16">
        <div class="loading loading-spinner loading-lg"></div>
        <p class="mt-4">Loading course details...</p>
      </div>

      <div v-else-if="!course.id" class="text-center py-16">
        <h1 class="text-3xl font-bold mb-4">Course Not Found</h1>
        <p class="text-lg text-base-content/70">The requested course could not be found or loaded.</p>
        <button @click="goBack" class="btn btn-primary mt-6">
          Go Back
        </button>
      </div>

      <div v-else class="card bg-base-100 shadow-xl p-8">
        <div class="flex items-center mb-4">
          <h1 class="text-4xl font-bold mr-4">{{ course.title }}</h1>
          <span :class="difficultyBadge(course.difficulty)">
            {{ course.difficulty }}
          </span>
        </div>
        <p class="text-lg text-base-content/70 mb-6">{{ course.description }}</p>

        <div class="stats stats-vertical lg:stats-horizontal shadow mb-8">
          <div class="stat">
            <div class="stat-title">Chapters</div>
            <div class="stat-value">{{ chapters.length }}</div>
          </div>
          <div class="stat">
            <div class="stat-title">Estimated Time</div>
            <div class="stat-value">{{ course.estimatedTime }}</div>
          </div>
        </div>

        <h2 class="text-2xl font-semibold mb-4">Chapters</h2>
        <ul v-if="chapters.length" class="space-y-4">
          <li v-for="chapter in chapters" :key="chapter.id" class="card bg-base-200 shadow-sm p-4">
            <div class="flex justify-between items-center">
              <div>
                <h3 class="text-xl font-medium">{{ chapter.name }}</h3>
                <p v-if="chapter.questionCount !== undefined" class="text-sm text-base-content/70">
                  {{ chapter.questionCount }} questions
                </p>
                <p v-else class="text-sm text-base-content/70">Loading question count...</p>
              </div>
              <button @click="startChapterQuiz(chapter.id)" class="btn btn-primary">
                Start Quiz
                <ArrowRightIcon class="w-4 h-4 ml-2" />
              </button>
            </div>
          </li>
        </ul>
        <div v-else class="text-center py-8 text-base-content/70">
          No chapters found for this course.
        </div>
      </div>
    </div>

    <footer class="footer bg-base-100 text-base-content py-6">
      <div class="w-full text-center">
        <p>© {{ new Date().getFullYear() }} Study Assistant</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../../../utils/api.js"; // Your configured axios instance
import {
  ArrowLeft as ArrowLeftIcon,
  ArrowRight as ArrowRightIcon,
  BookOpen as BookOpenIcon,
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

// State
const course = ref({});
const chapters = ref([]);
const loading = ref(true);

// Computed properties for navigation
const cameFromHome = computed(() => {
  const prevPath = router.options.history.state?.back;
  const referrer = document.referrer;

  // Corrected logical expression to accurately check paths.
  // This will make `cameFromHome` true ONLY if the previous path was /home or started with /quiz.
  return (
    prevPath === "/home" ||
    (prevPath && prevPath.startsWith("/quiz")) || // Check if previous path exists AND starts with /quiz
    referrer.includes("/home") ||
    referrer.includes("/quiz")
  );
});

const backButtonText = computed(() => {
  return cameFromHome.value ? "Back to Home" : "Back to Courses";
});

// Methods
const goBack = () => {
  if (cameFromHome.value) {
    // If we came from /home or /quiz, always go back to /home
    router.push("/home");
  } else {
    // Otherwise, go back one step in history if possible,
    // or default to /courses list page.
    if (window.history.length > 1 && router.options.history.state?.back) {
      router.back();
    } else {
      router.push("/courses");
    }
  }
};

const startChapterQuiz = (chapterId) => {
  router.push(`/quiz/${chapterId}`);
};

const difficultyBadge = (level) => {
  switch (level) {
    case "Beginner":
      return "badge badge-success";
    case "Intermediate":
      return "badge badge-warning";
    case "Advanced":
      return "badge badge-error";
    default:
      return "badge badge-outline";
  }
};

// Fetch data
onMounted(async () => {
  const courseId = route.params.courseId;
  loading.value = true;

  if (!courseId) {
    console.error("CourseDetailPage: No courseId found in route params.");
    course.value = {
      id: null,
      title: "Course Not Found",
      description: "No course ID was provided. Please navigate from the courses list.",
      difficulty: "N/A",
      estimatedTime: "N/A",
    };
    loading.value = false;
    return;
  }

  try {
    let fetchedCourse = null;
    // Keeping only the fallback method for course details as requested.
    // This will hit /courses and find the course by ID.
    try {
      const { data: allCourses } = await api.get("/courses");
      fetchedCourse = allCourses.find((c) => c.id === courseId);

      if (!fetchedCourse) {
        console.warn(`CourseDetailPage: Course with ID ${courseId} not found in the list of all courses.`);
        course.value = {
          id: null,
          title: "Course Not Found",
          description: "The requested course could not be found in the available list.",
          difficulty: "N/A",
          estimatedTime: "N/A",
        };
        chapters.value = [];
        loading.value = false;
        return;
      }
      course.value = fetchedCourse;

    } catch (courseFetchErr) {
      console.error("CourseDetailPage: Failed to fetch all courses (fallback method).", courseFetchErr);
      course.value = {
        id: null,
        title: "Error Loading Course Data",
        description: "Could not load course details due to an unexpected error. Please try again later.",
        difficulty: "N/A",
        estimatedTime: "N/A",
      };
      chapters.value = [];
      loading.value = false;
      return;
    }

    // Fetch chapters for the course using the /quiz/courses/:courseId/chapters endpoint.
    if (course.value.id) {
      try {
        const { data: chapterList } = await api.get(`/quiz/courses/${courseId}/chapters`);
        chapters.value = chapterList.map(chapter => ({
          ...chapter,
          questionCount: chapter.questions ? chapter.questions.length : 0
        }));
      } catch (chapterFetchErr) {
        console.error("CourseDetailPage: Failed to fetch chapters for course.", chapterFetchErr);
        chapters.value = [];
      }
    } else {
        chapters.value = [];
    }

  } catch (err) {
    console.error("CourseDetailPage: An unhandled error occurred during data loading.", err);
    course.value = {
      id: null,
      title: "Loading Error",
      description: "An unexpected error occurred. Please try again later.",
      difficulty: "N/A",
      estimatedTime: "N/A",
    };
    chapters.value = [];
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
/* Add any specific styles for this component here if needed */
</style>