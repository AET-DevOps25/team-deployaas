<template>
  <div data-theme="lofi" class="min-h-screen bg-base-200 flex flex-col">
    <!-- Navbar -->
    <Navbar />

    <!-- Register Form -->
    <main class="flex-1 flex items-center justify-center">
      <div class="p-6 max-w-md mx-auto w-full">
        <h2 class="text-2xl font-bold mb-4">Create an Account</h2>
        <form @submit.prevent="register">
          <input v-model="name" type="text" placeholder="Name" class="input input-bordered w-full mb-3" required />
          <input v-model="email" type="email" placeholder="Email" class="input input-bordered w-full mb-3" required />
          <input v-model="password" type="password" placeholder="Password" class="input input-bordered w-full mb-3" required />
          <button class="btn btn-neutral w-full">Register</button>
        </form>
        <p class="mt-4 text-sm">
          Already have an account?
          <RouterLink to="/login" class="link text-primary">Login</RouterLink>
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

const name = ref('')
const email = ref('')
const password = ref('')
const router = useRouter()

async function register() {
  try {
    await axios.post('/auth/register', {
      name: name.value,
      email: email.value,
      password: password.value
    })
    alert('Registered successfully!')
    router.push('/login')
  } catch (err) {
    alert(err.response?.data || 'Registration failed.')
  }
}
</script>
