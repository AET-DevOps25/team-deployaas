<template>
  <div class="container mx-auto py-8 max-w-4xl">
    <!-- Loading state -->
    <div v-if="loading" class="text-center py-16">
      <div class="loading loading-spinner loading-lg"></div>
      <h2 class="text-xl mt-4">Loading course...</h2>
    </div>

    <!-- Course content -->
    <div v-else>
      <!-- Header -->
      <div class="mb-8">
        <RouterLink
          to="/courses"
          class="btn btn-ghost flex items-center gap-2 mb-4"
        >
          <ArrowLeftIcon class="w-5 h-5" />
          Back to Courses
        </RouterLink>

        <div class="text-center">
          <h1 class="text-4xl font-bold mb-4">{{ course.title }}</h1>
          <p class="text-lg text-base-content/70 mb-4">
            {{ course.description }}
          </p>
          <div class="flex justify-center gap-4 mb-6">
            <span class="badge badge-outline"
              >{{ chapters.length }} chapters</span
            >
            <span class="badge badge-outline">{{ course.estimatedTime }}</span>
            <span :class="difficultyBadge(course.difficulty)">{{
              course.difficulty
            }}</span>
          </div>
        </div>
      </div>

      <!-- Chapters list -->
      <div class="space-y-4">
        <h2 class="text-2xl font-semibold mb-6">Course Chapters</h2>

        <div v-if="chapters.length === 0" class="text-center py-8">
          <p class="text-base-content/70">
            No chapters available for this course yet.
          </p>
        </div>

        <div v-else class="grid gap-4">
          <div
            v-for="(chapter, index) in chapters"
            :key="chapter.id"
            class="card bg-base-100 shadow hover:shadow-lg transition cursor-pointer"
            @click="startChapterQuiz(chapter.id)"
          >
            <div class="card-body">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-4">
                  <div class="flex-shrink-0">
                    <div
                      class="w-12 h-12 bg-primary text-primary-content rounded-full flex items-center justify-center font-bold text-lg"
                    >
                      {{ index + 1 }}
                    </div>
                  </div>
                  <div>
                    <h3 class="text-xl font-semibold">{{ chapter.name }}</h3>
                    <p class="text-base-content/70 mt-1">
                      {{
                        chapter.description ||
                        "Practice questions for this chapter"
                      }}
                    </p>
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <span class="badge badge-ghost"
                    >{{ chapter.questionCount || 0 }} questions</span
                  >
                  <ArrowRightIcon class="w-5 h-5 text-base-content/50" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter, RouterLink } from "vue-router";
import {
  ArrowLeft as ArrowLeftIcon,
  ArrowRight as ArrowRightIcon,
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

// State
const course = ref({});
const chapters = ref([]);
const loading = ref(true);

// Methods
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

// Fetch data on component mount
onMounted(async () => {
  const courseId = route.params.courseId;

  try {
    // Fetch actual course data from the API
    const courseRes = await fetch(`/api/courses`);
    const allCourses = await courseRes.json();
    
    // Find the specific course by ID
    const foundCourse = allCourses.find(c => c.id === courseId);
    if (foundCourse) {
      course.value = foundCourse;
    } else {
      console.error(`Course with ID ${courseId} not found`);
      course.value = {
        id: courseId,
        title: "Course Not Found",
        description: "The requested course could not be found.",
        difficulty: "Unknown",
        estimatedTime: "N/A",
      };
    }

    // Fetch chapters for the specific course
    const chaptersRes = await fetch(`/api/quiz/courses/${courseId}/chapters`);
    if (chaptersRes.ok) {
      chapters.value = await chaptersRes.json();
      
      // Add question count to each chapter
      for (let chapter of chapters.value) {
        try {
          const questionsRes = await fetch(
            `/api/quiz/chapters/${chapter.id}/questions`
          );
          const questions = await questionsRes.json();
          chapter.questionCount = questions.length;
        } catch (error) {
          console.error(
            `Error fetching questions for chapter ${chapter.id}:`,
            error
          );
          chapter.questionCount = 0;
        }
      }
    } else {
      console.error('Failed to fetch chapters for course:', courseId);
      chapters.value = [];
    }
  } catch (error) {
    console.error("Error fetching course data:", error);
  } finally {
    loading.value = false;
  }
});
</script>
