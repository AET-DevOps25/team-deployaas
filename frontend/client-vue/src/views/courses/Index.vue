<template>
  <div data-theme="lofi" class="min-h-screen bg-base-200 flex flex-col">
    <!-- Navbar -->
    <nav class="navbar bg-base-100 px-6 py-4 shadow-sm">
      <div class="flex-1 flex items-center">
        <BookOpenIcon class="w-8 h-8 text-primary" />
        <span class="text-2xl font-bold ml-2">Study Assistant</span>
      </div>

      <div class="hidden md:flex gap-6 pr-6">
        <RouterLink to="/home" class="btn btn-ghost">Home</RouterLink>
        <RouterLink to="/courses" class="btn btn-ghost">Courses</RouterLink>
        <RouterLink to="/progress" class="btn btn-ghost">Progress</RouterLink>
      </div>

      <div class="flex-none">
        <template v-if="isAuthenticated">
          <div class="dropdown dropdown-end">
            <label tabindex="0" class="btn btn-neutral">
              {{ userEmail }}
              <svg
                class="ml-2 w-4 h-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M19 9l-7 7-7-7"
                />
              </svg>
            </label>
            <ul
              tabindex="0"
              class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-40"
            >
              <li><a @click.prevent="logout">Logout</a></li>
            </ul>
          </div>
        </template>

        <template v-else>
          <RouterLink to="/login" class="btn btn-primary">Login</RouterLink>
        </template>
      </div>
    </nav>

    <div class="flex-1 container mx-auto py-8">
      <div class="flex items-center justify-between mb-8">
        <RouterLink to="/home" class="btn btn-ghost flex items-center gap-2">
          <ArrowLeftIcon class="w-5 h-5" />
          Back to Home
        </RouterLink>
        <h1 class="text-3xl font-bold">Technology Courses</h1>
      </div>

      <div v-if="loading" class="text-center py-16">
        <div class="loading loading-spinner loading-lg"></div>
        <p class="mt-4">Loading courses…</p>
      </div>
      <div v-else-if="!courses.length" class="text-center py-16">
        <h1 class="text-3xl font-bold mb-4">No Courses Found</h1>
        <p class="text-lg text-base-content/70">
          It looks like there are no courses available yet.
        </p>
      </div>

      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="course in courses"
          :key="course.id"
          class="card bg-base-100 shadow hover:shadow-lg transition"
        >
          <div class="card-body">
            <div class="flex justify-between items-start">
              <component
                :is="iconMap[course.iconKey] || DefaultIcon"
                class="w-8 h-8 text-purple-600"
              />
              <span :class="difficultyBadge(course.difficulty)">
                {{ course.difficulty }}
              </span>
            </div>

            <h2 class="card-title mt-2">{{ course.title }}</h2>
            <p class="text-base-content/70">{{ course.description }}</p>

            <div class="mt-4 flex justify-between text-sm text-base-content/70">
              <span>{{ course.chapters }} chapters</span>
              <span>{{ course.estimatedTime }}</span>
            </div>

            <div class="mt-4 flex flex-wrap gap-2">
              <span
                v-for="tag in course.tags.slice(0, 3)"
                :key="tag"
                class="badge badge-outline badge-sm"
              >
                {{ tag }}
              </span>
              <span
                v-if="course.tags.length > 3"
                class="badge badge-outline badge-sm"
              >
                +{{ course.tags.length - 3 }}
              </span>
            </div>

            <div class="mt-6">
              <RouterLink
                :to="`/courses/${course.id}`"
                class="btn bg-black text-white border-black hover:bg-gray-800 btn-block"
              >
                Continue Course
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </div>

    <footer class="footer bg-base-100 text-base-content py-6">
      <div class="w-full pl-6">
        <p>© {{ new Date().getFullYear() }} Study Assistant</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { RouterLink } from "vue-router";
// IMPORTANT: Make sure this path correctly points to your axios instance.
// Using 'api' as the alias for consistency with previous responses.
import api from "../../utils/api.js"; // This is your axios instance

import {
  BookOpen as BookOpenIcon,
  ArrowLeft as ArrowLeftIcon,
  Code as CodeIcon,
  Database as DatabaseIcon,
  Shield as ShieldIcon,
  Cloud as CloudIcon,
  Smartphone as SmartphoneIcon,
  Brain as BrainIcon,
  Globe as GlobeIcon,
  Cpu as CpuIcon,
} from "lucide-vue-next";

// state
// state
const courses = ref([]);
const loading = ref(true);

const isAuthenticated = computed(() => !!localStorage.getItem("token"));

const userEmail = computed(() => {
  const token = localStorage.getItem("token");
  if (!token) return "User";
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.sub || "User";
  } catch {
    return "User";
  }
});

// map your backend's `iconKey` to the lucide component
const iconMap = {
  code: CodeIcon,
  database: DatabaseIcon,
  shield: ShieldIcon,
  cloud: CloudIcon,
  smartphone: SmartphoneIcon,
  brain: BrainIcon,
  globe: GlobeIcon,
  cpu: CpuIcon,
};
const DefaultIcon = CodeIcon;

// pick a Daisy badge class by difficulty
// pick a Daisy badge class by difficulty
function difficultyBadge(level) {
  switch (level) {
    case "Beginner":
      return "badge badge-success badge-sm";
    case "Intermediate":
      return "badge badge-warning badge-sm";
    case "Advanced":
      return "badge badge-error badge-sm";
    default:
      return "badge badge-outline badge-sm";
  }
}

// fetch on mount
// fetch on mount
onMounted(async () => {
  try {
    // Replaced fetch with your api (axios) instance
    const res = await api.get("/courses");
    courses.value = res.data; // Axios puts the response data in the `.data` property
  } catch (e) {
    console.error("Failed to load courses:", e);
    // You might want to display a user-friendly error message or clear courses
    // courses.value = [];
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
/* Any specific styles for this component, if needed */
/* Ensure elements like the description don't overflow */
.text-base-content\/70 {
  /* Example to limit description lines for consistent card height */
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3; /* Limit to 3 lines */
  -webkit-box-orient: vertical;
}
</style>
