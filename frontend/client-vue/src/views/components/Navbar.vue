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
      <RouterLink v-if="!isLoggedIn" to="/login" class="btn">Login</RouterLink>
      <RouterLink v-if="!isLoggedIn" to="/register" class="btn btn-primary">Sign Up</RouterLink>
      <button v-if="isLoggedIn" @click="logout" class="btn btn-outline">Logout</button>
    </div>
  </nav>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import { BookOpen as BookOpenIcon } from 'lucide-vue-next'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLoggedIn = !!localStorage.getItem("token");

function logout() {
  localStorage.removeItem("token");
  router.push("/login");
}
</script>
