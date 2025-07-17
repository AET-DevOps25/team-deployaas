<template>
  <div data-theme="lofi" class="min-h-screen bg-base-200 flex flex-col">
    <!-- Navbar -->
    <Navbar />

    <!-- Login Form -->
    <main class="flex-1 flex items-center justify-center">
      <div class="p-6 max-w-md mx-auto w-full">
        <h2 class="text-2xl font-bold mb-4">Login</h2>
        <form @submit.prevent="login">
          <input v-model="email" type="email" placeholder="Email" class="input input-bordered w-full mb-3" required />
          <input v-model="password" type="password" placeholder="Password" class="input input-bordered w-full mb-3" required />
          <button class="btn btn-neutral w-full">Login</button>
        </form>
        <p class="mt-4 text-sm">
          Don't have an account?
          <RouterLink to="/register" class="link text-primary">Register</RouterLink>
        </p>
      </div>
    </main>

    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/api.js'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'

const email = ref('')
const password = ref('')
const router = useRouter()

async function login() {
  try {
    const res = await axios.post('/auth/login', {
      email: email.value,
      password: password.value,
    })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userId', res.data.userId)
    router.push('/home')
  } catch (err) {
    alert('Login failed')
  }
}
</script>
