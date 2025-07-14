<!-- src/components/Navbar.vue -->
<template>
  <nav class="navbar bg-base-100 px-6 py-4 shadow-sm">
    <div class="flex-1 flex items-center">
      <BookOpenIcon class="w-8 h-8 text-primary" />
      <span class="text-2xl font-bold ml-2">Study Assistant</span>
    </div>
    <div class="hidden md:flex gap-6 pr-6">
      <RouterLink to="/" class="btn btn-ghost"> Back to Landing Page</RouterLink>
    </div>
    <div class="flex-none">
      <RouterLink to="/login" class="btn btn-outline btn-neutral">Sign In</RouterLink>
      <RouterLink to="/register" class="btn btn-neutral ml-2">Sign Up</RouterLink>
    </div>
  </nav>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import { BookOpen as BookOpenIcon } from 'lucide-vue-next'
</script>
