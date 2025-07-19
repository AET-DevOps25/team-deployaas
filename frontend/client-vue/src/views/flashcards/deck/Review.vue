<template>
  <div data-theme="lofi" class="min-h-screen bg-base-200 flex flex-col">
    <nav class="navbar bg-base-100 px-6 py-4 shadow-sm">
      <div class="navbar-start">
        <button @click="goBack" class="btn btn-ghost">
          <ArrowLeftIcon class="w-5 h-5 mr-2" />
          Back to Decks
        </button>
      </div>
      <div class="navbar-center">
        <h1 class="text-xl font-bold">{{ deck?.name || 'Loading...' }}</h1>
      </div>
      <div class="navbar-end">
        <div class="text-sm text-base-content/70">
          {{ currentCardIndex + 1 }} / {{ flashcards.length }}
        </div>
      </div>
    </nav>

    <div class="flex-1 flex items-center justify-center px-6 py-8">
      <div v-if="loading" class="text-center">
        <span class="loading loading-spinner loading-lg mb-4"></span>
        <p class="text-base-content/70">Loading flashcards...</p>
      </div>

      <div v-else-if="flashcards.length === 0" class="text-center">
        <BookOpenIcon class="w-16 h-16 mx-auto text-base-content/30 mb-4" />
        <h2 class="text-2xl font-bold mb-2">No Flashcards in This Deck</h2>
        <p class="text-base-content/70 mb-6">
          Add some flashcards to start studying
        </p>
        <button @click="goBack" class="btn btn-primary">
          Go Back
        </button>
      </div>

      <div v-else class="w-full max-w-2xl">
        <div class="mb-6">
          <div class="flex justify-between text-sm text-base-content/70 mb-2">
            <span>Progress</span>
            <span>{{ Math.round((currentCardIndex / flashcards.length) * 100) }}%</span>
          </div>
          <progress 
            class="progress progress-primary w-full" 
            :value="currentCardIndex" 
            :max="flashcards.length"
          ></progress>
        </div>

        <div class="card bg-base-100 shadow-xl min-h-96">
          <div class="card-body flex items-center justify-center text-center">
            <div v-if="!showAnswer" class="w-full">
              <h2 class="text-2xl font-bold mb-4">Question</h2>
              <p class="text-lg leading-relaxed whitespace-pre-wrap">
                {{ currentCard.front }}
              </p>
              <div class="card-actions justify-center mt-8">
                <button @click="showAnswer = true" class="btn btn-primary">
                  <EyeIcon class="w-5 h-5 mr-2" />
                  Show Answer
                </button>
              </div>
            </div>
            
            <div v-else class="w-full">
              <h2 class="text-2xl font-bold mb-4">Answer</h2>
              <p class="text-lg leading-relaxed whitespace-pre-wrap mb-8">
                {{ currentCard.back }}
              </p>
              <div class="card-actions justify-center space-x-4">
                <button @click="markDifficult" class="btn btn-error">
                  <XIcon class="w-5 h-5 mr-2" />
                  Difficult
                </button>
                <button @click="markEasy" class="btn btn-success">
                  <CheckIcon class="w-5 h-5 mr-2" />
                  Easy
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-between mt-6">
          <button 
            @click="previousCard" 
            :disabled="currentCardIndex === 0"
            class="btn btn-outline"
          >
            <ChevronLeftIcon class="w-5 h-5 mr-2" />
            Previous
          </button>
          
          <div class="flex space-x-2">
            <button @click="shuffleDeck" class="btn btn-ghost">
              <ShuffleIcon class="w-5 h-5 mr-2" />
              Shuffle
            </button>
            <button @click="resetProgress" class="btn btn-ghost">
              <RotateCcwIcon class="w-5 h-5 mr-2" />
              Reset
            </button>
          </div>

          <button 
            @click="nextCard" 
            :disabled="currentCardIndex === flashcards.length - 1"
            class="btn btn-outline"
          >
            Next
            <ChevronRightIcon class="w-5 h-5 ml-2" />
          </button>
        </div>
      </div>
    </div>

    <dialog ref="completeModal" class="modal" :class="{ 'modal-open': showCompleteModal }">
      <div class="modal-box text-center">
        <h3 class="font-bold text-lg mb-4">🎉 Study Session Complete!</h3>
        <p class="mb-4">
          You've reviewed all {{ flashcards.length }} flashcards in this deck.
        </p>
        <div class="stats shadow mb-6">
          <div class="stat">
            <div class="stat-title">Easy Cards</div>
            <div class="stat-value text-success">{{ easyCards }}</div>
          </div>
          <div class="stat">
            <div class="stat-title">Difficult Cards</div>
            <div class="stat-value text-error">{{ difficultCards }}</div>
          </div>
        </div>
        <div class="modal-action justify-center">
          <button @click="restartSession" class="btn btn-primary">
            Study Again
          </button>
          <button @click="goBack" class="btn btn-ghost">
            Back to Decks
          </button>
        </div>
      </div>
    </dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
// Import your configured Axios instance. Adjust the path if needed.
import api from "../utils/api.js"; 

import {
  ArrowLeft as ArrowLeftIcon,
  BookOpen as BookOpenIcon,
  Eye as EyeIcon,
  X as XIcon,
  Check as CheckIcon,
  ChevronLeft as ChevronLeftIcon,
  ChevronRight as ChevronRightIcon,
  Shuffle as ShuffleIcon,
  RotateCcw as RotateCcwIcon,
} from "lucide-vue-next";

// You no longer need apiBaseUrl if your axios instance is configured with a base URL
// import { apiBaseUrl } from "@/config/api.js"; 

const route = useRoute();
const router = useRouter();

// State
const deck = ref(null);
const flashcards = ref([]);
const loading = ref(true);
const currentCardIndex = ref(0);
const showAnswer = ref(false);
const showCompleteModal = ref(false);
const easyCards = ref(0);
const difficultCards = ref(0);

// Computed
const currentCard = computed(() => flashcards.value[currentCardIndex.value]);

// Methods
const goBack = () => {
  router.push("/flashcards");
};

const loadDeck = async () => {
  try {
    loading.value = true;
    const deckId = route.params.deckId;
    
    // Load deck info using Axios
    const deckResponse = await api.get(`/decks/${deckId}`);
    deck.value = deckResponse.data;

    // Load flashcards using Axios
    const flashcardsResponse = await api.get(`/decks/${deckId}/flashcards`);
    flashcards.value = flashcardsResponse.data;

  } catch (error) {
    console.error("Error loading deck or flashcards for review:", error);
    // Handle specific error types, e.g., 404 for not found, 403 for forbidden
    if (error.response && error.response.status === 404) {
      console.warn("Deck for review not found.");
      // Optionally, redirect to the decks list if the deck doesn't exist
      // router.push('/flashcards'); 
    }
  } finally {
    loading.value = false;
  }
};

const nextCard = () => {
  if (currentCardIndex.value < flashcards.value.length - 1) {
    currentCardIndex.value++;
    showAnswer.value = false;
  } else {
    showCompleteModal.value = true;
  }
};

const previousCard = () => {
  if (currentCardIndex.value > 0) {
    currentCardIndex.value--;
    showAnswer.value = false;
  }
};

const markEasy = () => {
  easyCards.value++;
  nextCard();
};

const markDifficult = () => {
  difficultCards.value++;
  nextCard();
};

const shuffleDeck = () => {
  const shuffled = [...flashcards.value];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  flashcards.value = shuffled;
  currentCardIndex.value = 0;
  showAnswer.value = false;
};

const resetProgress = () => {
  currentCardIndex.value = 0;
  showAnswer.value = false;
  easyCards.value = 0;
  difficultCards.value = 0;
  showCompleteModal.value = false;
};

const restartSession = () => {
  resetProgress();
  showCompleteModal.value = false;
  shuffleDeck(); // Optionally shuffle again for a fresh session
};

// Load data on mount
onMounted(() => {
  loadDeck();
});
</script>

<style scoped>
/* Any specific styles for this component */
</style>