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
        <button @click="showCreateFlashcardModal = true" class="btn btn-primary">
          <PlusIcon class="w-5 h-5 mr-2" />
          Add Card
        </button>
      </div>
    </nav>

    <div class="flex-1 container mx-auto py-8 px-6">
      <div v-if="loading" class="flex justify-center items-center h-64">
        <span class="loading loading-spinner loading-lg"></span>
      </div>

      <div v-else class="mb-6">
        <div class="flex flex-wrap gap-4 justify-between items-center">
          <div class="stats shadow">
            <div class="stat">
              <div class="stat-title">Total Cards</div>
              <div class="stat-value">{{ flashcards.length }}</div>
            </div>
          </div>
          
          <div class="flex gap-2">
            <button 
              @click="exportToAnki" 
              class="btn btn-outline btn-info"
              :disabled="flashcards.length === 0"
            >
              <DownloadIcon class="w-5 h-5 mr-2" />
              Export to Anki
            </button>
            <button 
              @click="reviewDeck" 
              class="btn btn-primary"
              :disabled="flashcards.length === 0"
            >
              <BrainIcon class="w-5 h-5 mr-2" />
              Review Cards
            </button>
          </div>
        </div>
      </div>

      <div v-if="!loading && flashcards.length === 0" class="text-center py-16">
        <BookOpenIcon class="w-16 h-16 mx-auto text-base-content/30 mb-4" />
        <h2 class="text-2xl font-bold mb-2">No Flashcards Yet</h2>
        <p class="text-base-content/70 mb-6">
          Add your first flashcard to start studying
        </p>
        <button @click="showCreateFlashcardModal = true" class="btn btn-primary">
          <PlusIcon class="w-5 h-5 mr-2" />
          Add First Card
        </button>
      </div>

      <div v-else-if="!loading" class="space-y-4">
        <div
          v-for="(flashcard, index) in flashcards"
          :key="flashcard.id"
          class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow"
        >
          <div class="card-body">
            <div class="flex justify-between items-start">
              <div class="flex-1">
                <h3 class="font-semibold text-lg mb-2">Card {{ index + 1 }}</h3>
                <div class="grid md:grid-cols-2 gap-4">
                  <div>
                    <h4 class="font-medium text-sm text-base-content/70 mb-1">Front</h4>
                    <p class="text-sm whitespace-pre-wrap">{{ flashcard.front }}</p>
                  </div>
                  <div>
                    <h4 class="font-medium text-sm text-base-content/70 mb-1">Back</h4>
                    <p class="text-sm whitespace-pre-wrap">{{ flashcard.back }}</p>
                  </div>
                </div>
              </div>
              <div class="dropdown dropdown-end">
                <div tabindex="0" role="button" class="btn btn-ghost btn-sm">
                  <MoreHorizontalIcon class="w-4 h-4" />
                </div>
                <ul tabindex="0" class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52">
                  <li>
                    <a @click="editFlashcard(flashcard)">
                      <EditIcon class="w-4 h-4" />
                      Edit
                    </a>
                  </li>
                  <li>
                    <a @click="deleteFlashcardConfirm(flashcard)" class="text-error">
                      <TrashIcon class="w-4 h-4" />
                      Delete
                    </a>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <dialog ref="createFlashcardModal" class="modal" :class="{ 'modal-open': showCreateFlashcardModal }">
      <div class="modal-box max-w-2xl">
        <h3 class="font-bold text-lg mb-4">
          {{ editingFlashcard ? 'Edit Flashcard' : 'Create New Flashcard' }}
        </h3>
        <form @submit.prevent="submitFlashcard">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">Front (Question)</span>
            </label>
            <textarea
              v-model="flashcardForm.front"
              class="textarea textarea-bordered h-24"
              placeholder="Enter the question or front of the card"
              required
            ></textarea>
          </div>
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">Back (Answer)</span>
            </label>
            <textarea
              v-model="flashcardForm.back"
              class="textarea textarea-bordered h-24"
              placeholder="Enter the answer or back of the card"
              required
            ></textarea>
          </div>
          <div class="modal-action">
            <button type="button" @click="cancelFlashcardForm" class="btn btn-ghost">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary">
              {{ editingFlashcard ? 'Update' : 'Create' }}
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="cancelFlashcardForm">close</button>
      </form>
    </dialog>

    <dialog ref="deleteModal" class="modal" :class="{ 'modal-open': showDeleteModal }">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">Delete Flashcard</h3>
        <p class="mb-4">
          Are you sure you want to delete this flashcard? This action cannot be undone.
        </p>
        <div class="modal-action">
          <button @click="showDeleteModal = false" class="btn btn-ghost">
            Cancel
          </button>
          <button @click="deleteFlashcard" class="btn btn-error">
            Delete
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="showDeleteModal = false">close</button>
      </form>
    </dialog>

    <dialog ref="exportSuccessModal" class="modal" :class="{ 'modal-open': showExportSuccessModal }">
      <div class="modal-box max-w-2xl">
        <h3 class="font-bold text-lg mb-4 flex items-center gap-2">
          <DownloadIcon class="w-5 h-5 text-success" />
          Export Successful!
        </h3>
        
        <div class="alert alert-success mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>Successfully exported "{{ exportedDeckName }}" flashcards!</span>
        </div>

        <div class="space-y-4">
          <h4 class="font-semibold">How to Import into Anki:</h4>
          <ol class="list-decimal list-inside space-y-2 text-sm">
            <li>Open Anki and create/select a deck</li>
            <li>Go to <code class="bg-base-200 px-1 rounded">File → Import</code></li>
            <li>Select your downloaded .txt file</li>
            <li>Set <strong>Field separator</strong> to "Tab"</li>
            <li>Map <strong>Field 1</strong> to "Front" and <strong>Field 2</strong> to "Back"</li>
            <li>Click <strong>Import</strong></li>
          </ol>
          
          <div class="alert alert-info">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="stroke-info shrink-0 w-6 h-6">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span>Need Anki? Download it free from <a href="https://apps.ankiweb.net/" target="_blank" class="link link-primary">ankiweb.net</a></span>
          </div>
        </div>

        <div class="modal-action">
          <button @click="showExportSuccessModal = false" class="btn btn-primary">
            Got it!
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="showExportSuccessModal = false">close</button>
      </form>
    </dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
// Import your configured Axios instance. Adjust the path if needed.
import api from "../utils/api.js"; 

import {
  ArrowLeft as ArrowLeftIcon,
  Plus as PlusIcon,
  BookOpen as BookOpenIcon,
  Brain as BrainIcon,
  MoreHorizontal as MoreHorizontalIcon,
  Edit as EditIcon,
  Trash as TrashIcon,
  Download as DownloadIcon,
} from "lucide-vue-next";

// You no longer need apiBaseUrl if your axios instance is configured with a base URL
// import { apiBaseUrl } from "@/config/api.js"; 

const route = useRoute();
const router = useRouter();

// State
const deck = ref(null);
const flashcards = ref([]);
const loading = ref(true);
const showCreateFlashcardModal = ref(false);
const showDeleteModal = ref(false);
const showExportSuccessModal = ref(false);
const editingFlashcard = ref(null);
const flashcardToDelete = ref(null);
const flashcardForm = ref({ front: "", back: "" });
const exportedDeckName = ref("");

// Methods
const goBack = () => {
  router.push("/flashcards");
};

const reviewDeck = () => {
  router.push(`/flashcards/deck/${route.params.deckId}/review`);
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
    console.error("Error loading deck or flashcards:", error);
    // Handle specific error types, e.g., 404 for not found, 403 for forbidden
    // If the deck is not found, you might want to redirect or show a specific message
    if (error.response && error.response.status === 404) {
      console.warn("Deck not found.");
      // Optionally, redirect to the decks list or show a "Deck Not Found" message
      // router.push('/flashcards'); 
    }
  } finally {
    loading.value = false;
  }
};

const submitFlashcard = async () => {
  try {
    const isEditing = editingFlashcard.value !== null;
    const deckId = route.params.deckId;
    let response;

    if (isEditing) {
      // Edit existing flashcard using Axios PUT
      response = await api.put(`/flashcards/${editingFlashcard.value.id}`, flashcardForm.value);
    } else {
      // Create new flashcard using Axios POST
      response = await api.post(`/decks/${deckId}/flashcards`, flashcardForm.value);
    }

    // Axios throws an error for non-2xx status codes, so if we reach here, it's ok
    await loadDeck(); // Reload data after successful creation/update
    cancelFlashcardForm(); // Reset form and close modal
  } catch (error) {
    console.error("Error saving flashcard:", error);
    // You can check error.response.data for backend validation messages
  }
};

const editFlashcard = (flashcard) => {
  editingFlashcard.value = flashcard;
  flashcardForm.value.front = flashcard.front;
  flashcardForm.value.back = flashcard.back;
  showCreateFlashcardModal.value = true;
};

const deleteFlashcardConfirm = (flashcard) => {
  flashcardToDelete.value = flashcard;
  showDeleteModal.value = true;
};

const deleteFlashcard = async () => {
  try {
    // Delete flashcard using Axios DELETE
    await api.delete(`/flashcards/${flashcardToDelete.value.id}`);
    
    // Axios throws an error for non-2xx status codes, so if we reach here, it's ok
    await loadDeck(); // Reload data after successful deletion
    showDeleteModal.value = false;
    flashcardToDelete.value = null;
  } catch (error) {
    console.error("Error deleting flashcard:", error);
  }
};

const cancelFlashcardForm = () => {
  showCreateFlashcardModal.value = false;
  editingFlashcard.value = null;
  flashcardForm.value = { front: "", back: "" }; // Reset form fields
};

const exportToAnki = () => {
  if (!flashcards.value || flashcards.value.length === 0) {
    return;
  }

  // Create Anki-compatible text format
  // Format: "Front\tBack\n" (tab-separated values)
  let ankiText = "";
  
  flashcards.value.forEach(card => {
    // Escape tabs and newlines in the content
    const front = card.front.replace(/\t/g, " ").replace(/\n/g, "<br>");
    const back = card.back.replace(/\t/g, " ").replace(/\n/g, "<br>");
    
    ankiText += `${front}\t${back}\n`;
  });

  // Create and download the file
  const blob = new Blob([ankiText], { type: 'text/plain;charset=utf-8' });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  
  // Use deck name for filename, sanitized for file system
  const sanitizedDeckName = (deck.value?.name || 'flashcards')
    .replace(/[^a-z0-9]/gi, '_')
    .toLowerCase();
  link.download = `${sanitizedDeckName}_anki_export.txt`;
  
  // Trigger download
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);

  // Store deck name for success modal
  exportedDeckName.value = deck.value?.name || 'flashcards';
  
  // Show success message
  showExportSuccessModal.value = true;
};

// Load data on mount
onMounted(() => {
  loadDeck();
});
</script>

<style scoped>
/* Any specific styles for this component */
</style>