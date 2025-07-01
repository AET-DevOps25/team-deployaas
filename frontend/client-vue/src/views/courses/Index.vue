<template>
  <div class="container mx-auto py-8">
    <!-- Header / Back link -->
    <div class="flex items-center justify-between mb-8">
      <RouterLink to="/home" class="btn btn-ghost flex items-center gap-2">
        <ArrowLeftIcon class="w-5 h-5" />
        Back to Home
      </RouterLink>
      <h1 class="text-3xl font-bold">Technology Courses</h1>
    </div>

    <!-- Loading / Empty states -->
    <div v-if="loading" class="text-center py-16">Loading courses…</div>
    <div v-else-if="!courses.length" class="text-center py-16">
      No courses found.
    </div>

    <!-- Courses grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
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
              class="w-8 h-8 text-primary"
            />
            <span :class="difficultyBadge(course.difficulty)">
              {{ course.difficulty }}
            </span>
          </div>

          <!-- Title + description -->
          <h2 class="card-title mt-2">{{ course.title }}</h2>
          <p class="text-base-content/70">{{ course.description }}</p>

          <!-- chapters / time -->
          <div class="mt-4 flex justify-between text-sm text-base-content/70">
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

          <!-- start button -->
          <div class="mt-6">
            <RouterLink
              :to="`/courses/${course.id}`"
              class="btn btn-outline btn-block"
            >
              Start Course
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { RouterLink } from "vue-router";
import {
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
const courses = ref([]);
const loading = ref(true);

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
