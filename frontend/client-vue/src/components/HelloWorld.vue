<template>
  <div data-theme="silk" class="min-h-screen bg-base-100">
    <!-- Navbar -->
    <div class="navbar bg-base-200 px-4">
      <div class="flex-1">
        <BookOpenIcon class="w-8 h-8" />
        <span class="text-xl font-bold ml-2">Study Assistant</span>
      </div>
      <div class="hidden md:flex gap-4">
        <RouterLink to="/" class="btn btn-ghost">Home</RouterLink>
        <RouterLink to="/courses" class="btn btn-ghost">Courses</RouterLink>
        <RouterLink to="/progress" class="btn btn-ghost">Progress</RouterLink>
      </div>
      <div class="flex-none">
        <button class="btn">Sign In</button>
      </div>
    </div>

    <!-- Hero -->
    <div class="hero h-72 bg-primary text-primary-content">
      <div class="hero-content text-center">
        <div class="max-w-lg">
          <h1 class="text-4xl font-bold">Master Technology with AI</h1>
          <p class="py-2">Practice questions, get feedback, and export to Anki for spaced repetition.</p>
          <div class="mt-4 flex justify-center gap-2">
            <RouterLink to="/courses" class="btn btn-secondary">Explore Courses</RouterLink>
            <RouterLink to="/progress" class="btn btn-outline">View Progress</RouterLink>
          </div>
        </div>
      </div>
    </div>

    <!-- Features -->
    <div class="container mx-auto py-8">
      <h2 class="text-2xl font-semibold mb-4 text-center">Why Choose Us?</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="card shadow hover:shadow-lg transition">
          <div class="card-body text-center">
            <BrainIcon class="mx-auto w-10 h-10 text-secondary" />
            <h3 class="card-title">AI Feedback</h3>
            <p>Get intelligent personalized feedback on your answers.</p>
          </div>
        </div>
        <div class="card shadow hover:shadow-lg transition">
          <div class="card-body text-center">
            <TargetIcon class="mx-auto w-10 h-10 text-secondary" />
            <h3 class="card-title">Multiple Courses</h3>
            <p>DevOps, Data Science, Cybersecurity and more.</p>
          </div>
        </div>
        <div class="card shadow hover:shadow-lg transition">
          <div class="card-body text-center">
            <DownloadIcon class="mx-auto w-10 h-10 text-secondary" />
            <h3 class="card-title">Anki Export</h3>
            <p>Export quizzes as Anki flashcards for better retention.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <footer class="footer footer-center p-4 bg-base-200 text-base-content">
      <div>
        <p>© {{ new Date().getFullYear() }} Study Assistant</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { BookOpen as BookOpenIcon, Brain as BrainIcon, Target as TargetIcon, Download as DownloadIcon } from 'lucide-vue-next'
import { RouterLink } from 'vue-router'
</script>
