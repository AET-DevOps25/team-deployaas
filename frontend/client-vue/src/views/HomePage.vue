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
              <svg class="ml-2 w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </label>
            <ul tabindex="0" class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-40">
              <li><a @click.prevent="logout">Logout</a></li>
            </ul>
          </div>
        </template>

        <template v-else>
          <RouterLink to="/login" class="btn btn-primary">Login</RouterLink>
        </template>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="flex-1 container mx-auto py-8">
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold mb-4">Welcome Back!</h1>
        <p class="text-lg text-base-content/70">
          Continue your learning journey with our AI-powered courses
        </p>
      </div>

      <!-- Courses -->
      <section class="mb-16">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-3xl font-semibold">Your Courses</h2>
          <RouterLink to="/courses" class="btn btn-outline">View All Courses</RouterLink>
        </div>

        <div v-if="loading" class="text-center py-16">
          <div class="loading loading-spinner loading-lg"></div>
          <p class="mt-4">Loading courses...</p>
        </div>

        <div v-else-if="!courses.length" class="text-center py-16">
          <p class="text-base-content/70 mb-4">No courses available yet.</p>
          <RouterLink to="/courses" class="btn btn-primary">Explore Courses</RouterLink>
        </div>

        <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="course in courses" :key="course.id" class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body">
              <div class="flex justify-between items-start">
                <component :is="iconMap[course.iconKey] || DefaultIcon" class="w-8 h-8 text-purple-600" />
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
                <span v-for="tag in course.tags.slice(0, 3)" :key="tag" class="badge badge-outline badge-sm">{{ tag }}</span>
                <span v-if="course.tags.length > 3" class="badge badge-outline badge-sm">
                  +{{ course.tags.length - 3 }}
                </span>
              </div>
              <div class="mt-6">
                <RouterLink :to="`/courses/${course.id}`" class="btn bg-black text-white border-black hover:bg-gray-800 btn-block">
                  Continue Course
                </RouterLink>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Quick Actions -->
      <section class="mb-16">
        <h2 class="text-2xl font-semibold mb-6">Quick Actions</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <RouterLink to="/progress" class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body text-center">
              <TargetIcon class="mx-auto w-12 h-12 text-primary mb-2" />
              <h3 class="text-lg font-semibold">View Progress</h3>
              <p class="text-sm text-base-content/70">Track your learning journey</p>
            </div>
          </RouterLink>
          <div class="card bg-base-100 shadow hover:shadow-lg transition opacity-60">
            <div class="card-body text-center">
              <SettingsIcon class="mx-auto w-12 h-12 text-primary mb-2" />
              <h3 class="text-lg font-semibold">Placeholder Action</h3>
              <p class="text-sm text-base-content/70">Placeholder to be used for future features</p>
            </div>
          </div>
          <RouterLink to="/flashcards" class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body text-center">
              <BrainIcon class="mx-auto w-12 h-12 text-primary mb-2" />
              <h3 class="text-lg font-semibold">Flashcards</h3>
              <p class="text-sm text-base-content/70">Review with spaced repetition</p>
            </div>
          </RouterLink>
        </div>
      </section>
    </div>

    <!-- Footer -->
    <footer class="footer bg-base-100 text-base-content py-6">
      <div class="w-full text-center">
        <p>© {{ new Date().getFullYear() }} Study Assistant</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter, RouterLink } from "vue-router";
import {
  BookOpen as BookOpenIcon,
  Brain as BrainIcon,
  Target as TargetIcon,
  Code as CodeIcon,
  Database as DatabaseIcon,
  Shield as ShieldIcon,
  Cloud as CloudIcon,
  Smartphone as SmartphoneIcon,
  Globe as GlobeIcon,
  Cpu as CpuIcon,
  Settings as SettingsIcon,
} from "lucide-vue-next";
import api from '../utils/api.js';

const router = useRouter();
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

function logout() {
  localStorage.removeItem("token");
  router.push("/");
}

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

onMounted(async () => {
  try {
    const res = await api.get('/courses');
    courses.value = res.data;
  } catch (e) {
    console.error("Failed to load courses:", e);
  } finally {
    loading.value = false;
  }
});
</script>
