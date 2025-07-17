<template>
  <div data-theme="lofi" class="min-h-screen bg-base-200 flex flex-col">
    <!-- Navbar -->
    <nav class="navbar bg-base-100 px-6 py-4 shadow-sm">
      <div class="navbar-start">
        <button @click="goBack" class="btn btn-ghost">
          <ArrowLeftIcon class="w-5 h-5 mr-2" />
          {{ backButtonText }}
        </button>
      </div>
      <div class="navbar-center">
        <h1 class="text-xl font-bold">Flashcard Decks</h1>
      </div>
      <div class="navbar-end">
        <button @click="showCreateDeckModal = true" class="btn btn-primary">
          <PlusIcon class="w-5 h-5 mr-2" />
          New Deck
        </button>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="flex-1 container mx-auto py-8 px-6">
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center h-64">
        <span class="loading loading-spinner loading-lg"></span>
      </div>

      <!-- Empty State -->
      <div v-else-if="decks.length === 0" class="text-center py-16">
        <BookOpenIcon class="w-16 h-16 mx-auto text-base-content/30 mb-4" />
        <h2 class="text-2xl font-bold mb-2">No Flashcard Decks Yet</h2>
        <p class="text-base-content/70 mb-6">
          Create your first deck or generate flashcards from quiz questions
        </p>
        <div class="space-x-4">
          <button @click="showCreateDeckModal = true" class="btn btn-primary">
            <PlusIcon class="w-5 h-5 mr-2" />
            Create New Deck
          </button>
          <RouterLink to="/courses" class="btn btn-outline">
            <BookOpenIcon class="w-5 h-5 mr-2" />
            Browse Courses
          </RouterLink>
        </div>
      </div>

      <!-- Decks Grid -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="deck in decks"
          :key="deck.id"
          class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow"
        >
          <div class="card-body">
            <h3 class="card-title text-lg">{{ deck.name }}</h3>
            <p class="text-base-content/70">
              {{ deck.flashcardCount }} {{ deck.flashcardCount === 1 ? 'card' : 'cards' }}
            </p>
            <div class="card-actions justify-end mt-4">
              <button 
                @click="manageDeck(deck.id)" 
                class="btn btn-outline btn-sm"
              >
                <BookOpenIcon class="w-4 h-4 mr-1" />
                Manage Cards
              </button>
              <button 
                @click="exportDeckToAnki(deck)" 
                class="btn btn-outline btn-info btn-sm"
                :disabled="deck.flashcardCount === 0"
              >
                <DownloadIcon class="w-4 h-4 mr-1" />
                Export
              </button>
              <button 
                @click="reviewDeck(deck.id)" 
                class="btn btn-primary btn-sm"
                :disabled="deck.flashcardCount === 0"
              >
                <BrainIcon class="w-4 h-4 mr-1" />
                Review
              </button>
              <button @click="editDeck(deck)" class="btn btn-ghost btn-sm">
                <EditIcon class="w-4 h-4 mr-1" />
                Edit
              </button>
              <button @click="deleteDeckConfirm(deck)" class="btn btn-error btn-sm">
                <TrashIcon class="w-4 h-4 mr-1" />
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Deck Modal -->
    <dialog ref="createDeckModal" class="modal" :class="{ 'modal-open': showCreateDeckModal }">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">
          {{ editingDeck ? 'Edit Deck' : 'Create New Deck' }}
        </h3>
        <form @submit.prevent="submitDeck">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">Deck Name</span>
            </label>
            <input
              v-model="deckForm.name"
              type="text"
              placeholder="Enter deck name"
              class="input input-bordered"
              required
            />
          </div>
          <div class="modal-action">
            <button type="button" @click="cancelDeckForm" class="btn btn-ghost">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary">
              {{ editingDeck ? 'Update' : 'Create' }}
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="cancelDeckForm">close</button>
      </form>
    </dialog>

    <!-- Delete Confirmation Modal -->
    <dialog ref="deleteModal" class="modal" :class="{ 'modal-open': showDeleteModal }">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">Delete Deck</h3>
        <p class="mb-4">
          Are you sure you want to delete "{{ deckToDelete?.name }}"? 
          This action cannot be undone.
        </p>
        <div class="modal-action">
          <button @click="showDeleteModal = false" class="btn btn-ghost">
            Cancel
          </button>
          <button @click="deleteDeck" class="btn btn-error">
            Delete
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="showDeleteModal = false">close</button>
      </form>
    </dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter, RouterLink } from "vue-router";
import api from "./utils/api.js"; // Ensure this path is correct

import {
  ArrowLeft as ArrowLeftIcon,
  Plus as PlusIcon,
  BookOpen as BookOpenIcon,
  Brain as BrainIcon,
  Edit as EditIcon,
  Trash as TrashIcon,
  Download as DownloadIcon,
} from "lucide-vue-next";

const router = useRouter();

// State
const decks = ref([]);
const loading = ref(true);
const showCreateDeckModal = ref(false);
const showDeleteModal = ref(false);
const editingDeck = ref(null);
const deckToDelete = ref(null);
const deckForm = ref({ name: "" });

// This will now hold the dynamically fetched user ID from localStorage
const authenticatedUserId = ref(null);

// Computed
const backButtonText = computed(() => "Back to Home");

// Methods
const goBack = () => {
  router.push("/home");
};

const loadDecks = async () => {
  try {
    loading.value = true;
    
    // Crucial check: Ensure authenticatedUserId is available
    if (!authenticatedUserId.value) {
      console.warn("User ID not found, cannot load decks. Redirecting to login...");
      router.push('/login'); // Redirect to login if user ID is missing
      return; // Stop execution
    }

    // Use the dynamically obtained authenticatedUserId
    // CORRECTED: Removed '/api/flashcard' prefix because it's in api.js baseURL
    const response = await api.get(`/decks/user/${authenticatedUserId.value}`);
    decks.value = response.data;
  } catch (error) {
    console.error("Error loading decks:", error);
    if (error.response && error.response.data && error.response.data.message) {
        alert(`Error loading decks: ${error.response.data.message}`);
    } else {
        alert("Failed to load decks. Please try again.");
    }
  } finally {
    loading.value = false;
  }
};

const submitDeck = async () => {
  try {
    const isEditing = editingDeck.value !== null;
    
    // Crucial check: Ensure authenticatedUserId is available for deck creation/update
    if (!authenticatedUserId.value) {
      console.warn("User ID not found, cannot save deck. Please log in.");
      alert("Please log in to create or update decks.");
      return;
    }

    // For new decks, pass the userId in the request body
    const body = isEditing 
      ? { name: deckForm.value.name }
      : { userId: authenticatedUserId.value, name: deckForm.value.name };

    if (isEditing) {
      // CORRECTED: Removed '/api/flashcard' prefix
      await api.put(`/decks/${editingDeck.value.id}`, body);
    } else {
      // CORRECTED: Removed '/api/flashcard' prefix
      await api.post(`/decks`, body); 
    }

    await loadDecks(); 
    cancelDeckForm(); 
  } catch (error) {
    console.error("Error saving deck:", error);
    if (error.response && error.response.data && error.response.data.message) {
      alert(`Error: ${error.response.data.message}`);
    } else {
      alert("Failed to save deck. Please try again.");
    }
  }
};

const editDeck = (deck) => {
  editingDeck.value = deck;
  deckForm.value.name = deck.name;
  showCreateDeckModal.value = true;
};

const deleteDeckConfirm = (deck) => {
  deckToDelete.value = deck;
  showDeleteModal.value = true;
};

const deleteDeck = async () => {
  try {
    // Crucial check: Ensure authenticatedUserId is available for deletion (optional, but good practice if backend checks ownership)
    if (!authenticatedUserId.value) {
      console.warn("User ID not found, cannot delete deck. Please log in.");
      alert("Please log in to delete decks.");
      return;
    }
    // CORRECTED: Removed '/api/flashcard' prefix
    await api.delete(`/decks/${deckToDelete.value.id}`);

    await loadDecks(); 
    showDeleteModal.value = false;
    deckToDelete.value = null;
  } catch (error) {
    console.error("Error deleting deck:", error);
    alert("Failed to delete deck. Please try again.");
  }
};

const reviewDeck = (deckId) => {
  router.push(`/flashcards/deck/${deckId}/review`);
};

const manageDeck = (deckId) => {
  router.push(`/flashcards/deck/${deckId}`);
};

const exportDeckToAnki = async (deck) => {
  try {
    // CORRECTED: Removed '/api/flashcard' prefix
    const response = await api.get(`/decks/${deck.id}/flashcards`);
    const flashcards = response.data; 
    
    if (!flashcards || flashcards.length === 0) {
      alert('No flashcards found in this deck to export.');
      return;
    }

    let ankiText = "";
    
    flashcards.forEach(card => {
      const front = card.front.replace(/\t/g, " ").replace(/\n/g, "<br>");
      const back = card.back.replace(/\t/g, " ").replace(/\n/g, "<br>");
      ankiText += `${front}\t${back}\n`;
    });

    const blob = new Blob([ankiText], { type: 'text/plain;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    
    const sanitizedDeckName = deck.name
      .replace(/[^a-z0-9]/gi, '_')
      .toLowerCase();
    link.download = `${sanitizedDeckName}_anki_export.txt`;
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    alert(`Successfully exported ${flashcards.length} flashcards from "${deck.name}" to Anki format!\n\nTo import into Anki:\n1. Open Anki\n2. File > Import\n3. Select the downloaded file\n4. Choose "Tab" as field separator\n5. Map fields to Front/Back`);
    
  } catch (error) {
    console.error("Error exporting deck:", error);
    alert('Failed to export deck. Please try again.');
  }
};

const cancelDeckForm = () => {
  showCreateDeckModal.value = false;
  editingDeck.value = null;
  deckForm.value.name = "";
};

// Load data on mount
onMounted(() => {
  // Retrieve userId directly from localStorage
  const storedUserId = localStorage.getItem('userId');
  if (storedUserId) {
    authenticatedUserId.value = storedUserId;
    loadDecks(); // Call loadDecks now that userId is available
  } else {
    // If no user ID is found, redirect to the login page
    console.log("No user ID found in localStorage. Redirecting to login.");
    router.push('/login');
  }
});
</script>

<style scoped>
/* Any specific styles for this component */
</style>