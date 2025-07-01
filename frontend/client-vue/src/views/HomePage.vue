<template>
  <div data-theme="lofi" class="min-h-screen bg-base-200 flex flex-col">
    <!-- Navbar -->
    <nav class="navbar bg-base-100 px-6 py-4 shadow-sm">
      <div class="flex-1 flex items-center">
        <BookOpenIcon class="w-8 h-8 text-primary" />
        <span class="text-2xl font-bold ml-2">Study Assistant</span>
      </div>
      <div class="hidden md:flex gap-6">
        <RouterLink to="/home" class="btn btn-ghost">Home</RouterLink>
        <RouterLink to="/courses" class="btn btn-ghost">Courses</RouterLink>
        <RouterLink to="/progress" class="btn btn-ghost">Progress</RouterLink>
      </div>
      <div class="flex-none">
        <button class="btn btn-primary">Profile</button>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="flex-1 container mx-auto py-8">
      <!-- Welcome Section -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold mb-4">Welcome Back!</h1>
        <p class="text-lg text-base-content/70">
          Continue your learning journey with our AI-powered courses
        </p>
      </div>

      <!-- Available Courses -->
      <section class="mb-16">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-3xl font-semibold">Your Courses</h2>
          <RouterLink to="/courses" class="btn btn-outline">
            View All Courses
          </RouterLink>
        </div>

        <!-- Loading state -->
        <div v-if="loading" class="text-center py-16">
          <div class="loading loading-spinner loading-lg"></div>
          <p class="mt-4">Loading courses...</p>
        </div>

        <!-- Empty state -->
        <div v-else-if="!courses.length" class="text-center py-16">
          <p class="text-base-content/70 mb-4">No courses available yet.</p>
          <RouterLink to="/courses" class="btn btn-primary">
            Explore Courses
          </RouterLink>
        </div>

        <!-- Courses grid -->
        <div
          v-else
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6"
        >
          <div
            v-for="course in courses"
            :key="course.id"
            class="card bg-base-100 shadow hover:shadow-lg transition"
          >
            <div class="card-body">
              <!-- Icon + difficulty badge -->
              <div class="flex justify-between items-start">
                <component
                  :is="iconMap[course.iconKey] || DefaultIcon"
                  class="w-8 h-8 text-purple-600"
                />
                <span :class="difficultyBadge(course.difficulty)">
                  {{ course.difficulty }}
                </span>
              </div>

              <!-- Title + description -->
              <h2 class="card-title mt-2">{{ course.title }}</h2>
              <p class="text-base-content/70">{{ course.description }}</p>

              <!-- chapters / time -->
              <div
                class="mt-4 flex justify-between text-sm text-base-content/70"
              >
                <span>{{ course.chapters }} chapters</span>
                <span>{{ course.estimatedTime }}</span>
              </div>

              <!-- tags -->
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

              <!-- Continue/Start button -->
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
      </section>

      <!-- Quick Actions -->
      <section class="mb-16">
        <h2 class="text-2xl font-semibold mb-6">Quick Actions</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <RouterLink
            to="/progress"
            class="card bg-base-100 shadow hover:shadow-lg transition"
          >
            <div class="card-body text-center">
              <TargetIcon class="mx-auto w-12 h-12 text-primary mb-2" />
              <h3 class="text-lg font-semibold">View Progress</h3>
              <p class="text-sm text-base-content/70">
                Track your learning journey
              </p>
            </div>
          </RouterLink>

          <RouterLink
            to="/flashcards"
            class="card bg-base-100 shadow hover:shadow-lg transition"
          >
            <div class="card-body text-center">
              <BrainIcon class="mx-auto w-12 h-12 text-primary mb-2" />
              <h3 class="text-lg font-semibold">Flashcards</h3>
              <p class="text-sm text-base-content/70">
                Review with spaced repetition
              </p>
            </div>
          </RouterLink>

          <RouterLink
            to="/quizzes"
            class="card bg-base-100 shadow hover:shadow-lg transition"
          >
            <div class="card-body text-center">
              <BookOpenIcon class="mx-auto w-12 h-12 text-primary mb-2" />
              <h3 class="text-lg font-semibold">All Quizzes</h3>
              <p class="text-sm text-base-content/70">
                Browse all available quizzes
              </p>
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
import { ref, onMounted } from "vue";
import { RouterLink } from "vue-router";
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
} from "lucide-vue-next";

// State
const courses = ref([]);
const loading = ref(true);

// Icon mapping
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

// Difficulty badge styling
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

// Fetch courses on mount
onMounted(async () => {
  try {
    const res = await fetch("/api/courses");
    courses.value = await res.json();
  } catch (e) {
    console.error("Failed to load courses:", e);
  } finally {
    loading.value = false;
  }
});
</script>
