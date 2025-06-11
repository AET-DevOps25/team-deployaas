<template>
  <div data-theme="lofi" class="min-h-screen bg-base-200 flex flex-col">
    <!-- Navbar -->
    <nav class="navbar bg-base-100 px-6 py-4 shadow-sm">
      <div class="flex-1 flex items-center">
        <BookOpenIcon class="w-8 h-8 text-primary" />
        <span class="text-2xl font-bold ml-2">Study Assistant</span>
      </div>
      <div class="hidden md:flex gap-6">
        <RouterLink to="/" class="btn btn-ghost">Home</RouterLink>
        <RouterLink to="/courses" class="btn btn-ghost">Courses</RouterLink>
        <RouterLink to="/progress" class="btn btn-ghost">Progress</RouterLink>
      </div>
      <div class="flex-none">
        <button class="btn btn-primary">Sign In</button>
      </div>
    </nav>

    <!-- Hero -->
    <section class="hero flex-1 bg-base-200">
      + <div class="hero-content flex-col text-center mx-auto max-w-3xl py-24">
      <h1 class="text-5xl font-extrabold leading-tight">
        Master Technology with
        <span class="text-primary">AI-Powered Learning</span>
      </h1>
      <p class="mt-4 text-lg text-base-content/70">
        Practice open-ended questions across multiple technology courses, receive intelligent feedback, and export to Anki for spaced repetition learning.
      </p>
      <div class="mt-8 flex justify-center gap-4">
        <RouterLink to="/courses" class="btn btn-primary btn-lg">
          Explore Courses
        </RouterLink>
        <RouterLink to="/progress" class="btn btn-outline btn-lg">
          View Progress
        </RouterLink>
      </div>
    </div>
    </section>

    <!-- Why Choose Our Platform? -->
    <section class="bg-base-200 py-16">
      <div class="container mx-auto text-center">
        <h2 class="text-3xl font-semibold mb-4">Why Choose Our Platform?</h2>
        <p class="text-base-content/70 mb-10">
          Everything you need to level up your tech skills with AI-enhanced practice and memory tools.
        </p>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body text-center">
              <BrainIcon class="mx-auto w-12 h-12 text-primary" />
              <h3 class="card-title mt-2">AI-Powered Feedback</h3>
              <p class="text-base-content/70">
                Get intelligent, personalized feedback on your answers using advanced semantic analysis.
              </p>
            </div>
          </div>
          <div class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body text-center">
              <TargetIcon class="mx-auto w-12 h-12 text-primary" />
              <h3 class="card-title mt-2">Multiple Courses</h3>
              <p class="text-base-content/70">
                Choose from DevOps, Software Engineering, Data Science, Cybersecurity, and more.
              </p>
            </div>
          </div>
          <div class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body text-center">
              <DownloadIcon class="mx-auto w-12 h-12 text-primary" />
              <h3 class="card-title mt-2">Anki Integration</h3>
              <p class="text-base-content/70">
                Export your quizzes as Anki flashcards for spaced repetition and long-term retention.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Available Courses -->
    <section class="bg-base-200 py-16">
      <div class="container mx-auto">
        <h2 class="text-3xl font-semibold mb-8 text-center">Available Courses</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <!-- DevOps -->
          <div class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="text-2xl">&lt;/&gt;</span>
                  <h3 class="text-xl font-semibold">DevOps Engineering</h3>
                </div>
                <span class="badge badge-success">Popular</span>
              </div>
              <p class="text-base-content/70 mt-2">CI/CD, Infrastructure as Code, Monitoring</p>
              <p class="mt-4 text-sm font-medium">6 chapters</p>
              <RouterLink to="/courses/devops" class="btn btn-outline btn-block mt-4">
                Explore Course
              </RouterLink>
            </div>
          </div>
          <!-- Data Science -->
          <div class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="text-2xl">🗄️</span>
                  <h3 class="text-xl font-semibold">Data Science</h3>
                </div>
                <span class="badge badge-secondary">New</span>
              </div>
              <p class="text-base-content/70 mt-2">Machine Learning, Statistics, Data Analysis</p>
              <p class="mt-4 text-sm font-medium">8 chapters</p>
              <RouterLink to="/courses/data-science" class="btn btn-outline btn-block mt-4">
                Explore Course
              </RouterLink>
            </div>
          </div>
          <!-- Cybersecurity -->
          <div class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="text-2xl">🛡️</span>
                  <h3 class="text-xl font-semibold">Cybersecurity</h3>
                </div>
                <span class="badge badge-error">Advanced</span>
              </div>
              <p class="text-base-content/70 mt-2">Network Security, Ethical Hacking, Compliance</p>
              <p class="mt-4 text-sm font-medium">7 chapters</p>
              <RouterLink to="/courses/cybersecurity" class="btn btn-outline btn-block mt-4">
                Explore Course
              </RouterLink>
            </div>
          </div>
          <!-- Cloud Computing -->
          <div class="card bg-base-100 shadow hover:shadow-lg transition">
            <div class="card-body">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="text-2xl">☁️</span>
                  <h3 class="text-xl font-semibold">Cloud Computing</h3>
                </div>
                <span class="badge badge-warning">Intermediate</span>
              </div>
              <p class="text-base-content/70 mt-2">AWS, Azure, Google Cloud, Architecture</p>
              <p class="mt-4 text-sm font-medium">9 chapters</p>
              <RouterLink to="/courses/cloud" class="btn btn-outline btn-block mt-4">
                Explore Course
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer bg-base-100 text-base-content py-6">
      <div class="w-full text-center">
        <p>© {{ new Date().getFullYear() }} Study Assistant</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import {
  BookOpen as BookOpenIcon,
  Brain as BrainIcon,
  Target as TargetIcon,
  Download as DownloadIcon
} from 'lucide-vue-next'
import { RouterLink } from 'vue-router'
</script>
