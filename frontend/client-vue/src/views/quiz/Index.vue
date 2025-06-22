<template>
  <div class="container mx-auto py-8 max-w-4xl">
    <!-- Loading state -->
    <div v-if="loading" class="text-center py-16">
      <div class="loading loading-spinner loading-lg"></div>
      <h2 class="text-xl mt-4">Loading quiz...</h2>
    </div>

    <!-- No questions state -->
    <div v-else-if="questions.length === 0" class="text-center py-16">
      <h2 class="text-2xl mb-4">No questions found for this chapter</h2>
      <button @click="goBackToCourse" class="btn btn-primary">
        Back to Course
      </button>
    </div>

    <!-- Quiz content -->
    <div v-else>
      <!-- Header -->
      <div class="mb-8">
        <button
          @click="goBackToCourse"
          class="btn btn-ghost flex items-center gap-2 mb-4"
        >
          <ArrowLeftIcon class="w-5 h-5" />
          Back to Course
        </button>
      </div>

      <!-- Quiz title and progress -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold mb-4">{{ chapter?.name || "Quiz" }}</h1>
        <div class="badge badge-primary badge-lg">
          Question {{ currentQuestionIndex + 1 }} of {{ questions.length }}
        </div>
      </div>

      <!-- Question card -->
      <div class="card bg-base-100 shadow-lg mb-8">
        <div class="card-body">
          <h3 class="text-xl font-medium mb-6">{{ currentQuestion.text }}</h3>

          <textarea
            v-model="currentAnswer"
            rows="6"
            class="textarea textarea-bordered w-full"
            placeholder="Type your answer here..."
          />
        </div>
      </div>

      <!-- Navigation controls -->
      <div class="flex justify-between items-center flex-wrap gap-4 mb-8">
        <button
          @click="handlePreviousQuestion"
          :disabled="currentQuestionIndex === 0"
          class="btn btn-outline"
        >
          <ArrowLeftIcon class="w-4 h-4 mr-2" />
          Previous
        </button>

        <div class="text-center">
          <div
            class="badge"
            :class="currentAnswer.trim() ? 'badge-success' : 'badge-ghost'"
          >
            {{ currentAnswer.trim() ? "✓ Answered" : "Not answered yet" }}
          </div>
        </div>

        <button
          v-if="currentQuestionIndex < questions.length - 1"
          @click="handleNextQuestion"
          class="btn btn-primary"
        >
          Next
          <ArrowRightIcon class="w-4 h-4 ml-2" />
        </button>

        <button v-else @click="handleFinishQuiz" class="btn btn-error">
          Finish Quiz
        </button>
      </div>

      <!-- Question navigator dots (if more than one question) -->
      <div v-if="questions.length > 1" class="text-center">
        <div class="flex justify-center gap-2 mb-4">
          <button
            v-for="(question, index) in questions"
            :key="`question-${question.id || index}`"
            @click="setCurrentQuestionIndex(index)"
            class="btn btn-sm btn-circle"
            :class="{
              'btn-primary': index === currentQuestionIndex,
              'btn-success':
                index !== currentQuestionIndex && answers[index]?.trim(),
              'btn-ghost':
                index !== currentQuestionIndex && !answers[index]?.trim(),
            }"
          >
            {{ index + 1 }}
          </button>
        </div>
        <p class="text-sm text-base-content/70">
          Click a number to jump to that question
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  ArrowLeft as ArrowLeftIcon,
  ArrowRight as ArrowRightIcon,
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

// State
const chapter = ref(null);
const questions = ref([]);
const currentQuestionIndex = ref(0);
const answers = ref({});
const loading = ref(true);

// Computed
const currentQuestion = computed(
  () => questions.value[currentQuestionIndex.value]
);
const currentAnswer = computed({
  get: () => answers.value[currentQuestionIndex.value] || "",
  set: (value) => {
    answers.value[currentQuestionIndex.value] = value;
  },
});

// Methods
const handleNextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++;
  }
};

const handlePreviousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--;
  }
};

const handleFinishQuiz = () => {
  // TODO: Implement quiz submission logic here
  alert("Quiz completed! Returning to course.");
  goBackToCourse();
};

const goBackToCourse = () => {
  // Get course ID from chapter data or default to devops
  const courseId = chapter.value?.courseId || "devops";
  router.push(`/courses/${courseId}`);
};

const setCurrentQuestionIndex = (index) => {
  currentQuestionIndex.value = index;
};

// Fetch data on component mount
onMounted(async () => {
  const chapterId = route.params.chapterId;

  try {
    // Fetch chapter details
    const chapterRes = await fetch(
      `http://localhost:8081/api/quiz/chapters/${chapterId}`
    );
    chapter.value = await chapterRes.json();

    // Fetch questions for the selected chapter
    const questionsRes = await fetch(
      `http://localhost:8081/api/quiz/chapters/${chapterId}/questions`
    );
    questions.value = await questionsRes.json();
  } catch (error) {
    console.error("Error fetching quiz data:", error);
  } finally {
    loading.value = false;
  }
});
</script>
