<template>
  <div data-theme="lofi" class="min-h-screen bg-white">
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
          <div
            class="badge bg-purple-700 text-white border-purple-700 badge-lg"
          >
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
              :disabled="submittingAnswer"
            />

            <!-- Submit Answer Buttons -->
            <div class="flex gap-2 mt-4 flex-wrap">
              <div
                class="tooltip tooltip-bottom"
                data-tip="Get quick AI feedback (2-3 sentences) on your answer quality"
              >
                <button
                  @click="submitAnswer"
                  :disabled="!currentAnswer.trim() || submittingAnswer"
                  class="btn bg-purple-700 text-white hover:bg-purple-800"
                >
                  <span
                    v-if="submittingAnswer"
                    class="loading loading-spinner loading-sm"
                  ></span>
                  {{
                    submittingAnswer
                      ? "Getting AI Feedback..."
                      : "Submit Answer"
                  }}
                </button>
              </div>

              <div
                class="tooltip tooltip-bottom"
                data-tip="Receive detailed AI feedback with strengths, areas for improvement, and specific suggestions"
              >
                <button
                  @click="submitAdvancedAnswer"
                  :disabled="!currentAnswer.trim() || submittingAnswer"
                  class="btn btn-outline btn-secondary"
                >
                  <span
                    v-if="submittingAnswer"
                    class="loading loading-spinner loading-sm"
                  ></span>
                  {{
                    submittingAnswer ? "Analyzing..." : "Get Advanced Feedback"
                  }}
                </button>
              </div>

              <div
                class="tooltip tooltip-bottom"
                data-tip="Non-AI Semantic analysis comparing key concepts with sample solution (Warning: High similarity doesn't guarantee correctness)"
              >
                <button
                  @click="submitSemanticAnswer"
                  :disabled="!currentAnswer.trim() || submittingAnswer"
                  class="btn btn-outline btn-info"
                >
                  <span
                    v-if="submittingAnswer"
                    class="loading loading-spinner loading-sm"
                  ></span>
                  {{ submittingAnswer ? "Analyzing..." : "Check Similarity" }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Feedback Card -->
        <div v-if="currentFeedback" class="card bg-base-100 shadow-lg mb-8">
          <div class="card-body">
            <h3 class="text-xl font-medium mb-4 flex items-center gap-2">
              <span
                v-if="currentFeedback.model_used?.includes('semantic')"
                class="text-blue-600"
                >📊</span
              >
              <span v-else class="text-purple-700">🤖</span>
              <span v-if="currentFeedback.model_used?.includes('semantic')"
                >Semantic Analysis</span
              >
              <span v-else>AI Feedback</span>
            </h3>

            <!-- Semantic Analysis Score (only for semantic feedback) -->
            <div
              v-if="currentFeedback.model_used?.includes('semantic')"
              class="mb-4"
            >
              <h4 class="font-medium mb-2">Similarity Score:</h4>
              <div class="flex items-center gap-2">
                <progress
                  class="progress progress-info w-32"
                  :value="currentFeedback.score || 0"
                  max="100"
                ></progress>
                <span class="text-sm font-mono"
                  >{{ (currentFeedback.score || 0).toFixed(1) }}%</span
                >
              </div>
            </div>

            <!-- Main Feedback -->
            <div class="mb-4">
              <h4 class="font-medium mb-2">Overall Feedback:</h4>
              <p class="text-base-content/80">{{ currentFeedback.feedback }}</p>
            </div>

            <!-- Strengths -->
            <div v-if="currentFeedback.strengths?.length" class="mb-4">
              <h4 class="font-medium mb-2 text-green-600">✅ Strengths:</h4>
              <ul class="list-disc list-inside space-y-1">
                <li
                  v-for="strength in currentFeedback.strengths"
                  :key="strength"
                  class="text-base-content/80"
                >
                  {{ strength }}
                </li>
              </ul>
            </div>

            <!-- Areas for Improvement -->
            <div v-if="currentFeedback.weaknesses?.length" class="mb-4">
              <h4 class="font-medium mb-2 text-orange-600">
                ⚠️ Areas for Improvement:
              </h4>
              <ul class="list-disc list-inside space-y-1">
                <li
                  v-for="weakness in currentFeedback.weaknesses"
                  :key="weakness"
                  class="text-base-content/80"
                >
                  {{ weakness }}
                </li>
              </ul>
            </div>

            <!-- Suggestions -->
            <div v-if="currentFeedback.suggestions?.length" class="mb-4">
              <h4 class="font-medium mb-2 text-blue-600">💡 Suggestions:</h4>
              <ul class="list-disc list-inside space-y-1">
                <li
                  v-for="suggestion in currentFeedback.suggestions"
                  :key="suggestion"
                  class="text-base-content/80"
                >
                  {{ suggestion }}
                </li>
              </ul>
            </div>

            <!-- Model Used -->
            <div class="text-xs text-base-content/60 mt-4">
              Analysis by: {{ currentFeedback.model_used }} |
              {{ formatTimestamp(currentFeedback.timestamp) }}
            </div>
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
            class="btn bg-purple-700 text-white hover:bg-purple-800"
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
                'bg-purple-700 text-white border-purple-700':
                  index === currentQuestionIndex,
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
const feedbacks = ref({});
const loading = ref(true);
const submittingAnswer = ref(false);

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

const currentFeedback = computed(
  () => feedbacks.value[currentQuestionIndex.value] || null
);

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

  // Check if we should go back to home instead of courses
  const cameFromHome =
    router.options.history.state?.back === "/home" ||
    document.referrer.includes("/home");

  if (cameFromHome) {
    // If they came from home, go back to home
    router.push("/home");
  } else {
    // Otherwise go to the course detail page
    router.push(`/courses/${courseId}`);
  }
};

const setCurrentQuestionIndex = (index) => {
  currentQuestionIndex.value = index;
};

// Submit answer for AI feedback
const submitAnswer = async () => {
  if (!currentAnswer.value.trim()) return;

  submittingAnswer.value = true;
  try {
    const response = await fetch(
      `/api/quiz/questions/${currentQuestion.value.id}/submit`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          answer: currentAnswer.value,
          model_type: "local", // or 'openai' based on preference
        }),
      }
    );

    if (response.ok) {
      const feedback = await response.json();
      feedbacks.value[currentQuestionIndex.value] = feedback;
    } else {
      console.error("Failed to submit answer");
      alert("Failed to get AI feedback. Please try again.");
    }
  } catch (error) {
    console.error("Error submitting answer:", error);
    alert("Error connecting to the feedback service. Please try again.");
  } finally {
    submittingAnswer.value = false;
  }
};

// Submit answer for advanced AI analysis
const submitAdvancedAnswer = async () => {
  if (!currentAnswer.value.trim()) return;

  submittingAnswer.value = true;
  try {
    const response = await fetch(
      `/api/quiz/questions/${currentQuestion.value.id}/submit/advanced`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          answer: currentAnswer.value,
        }),
      }
    );

    if (response.ok) {
      const feedback = await response.json();
      feedbacks.value[currentQuestionIndex.value] = feedback;
    } else {
      console.error("Failed to submit answer for advanced analysis");
      alert("Failed to get advanced AI feedback. Please try again.");
    }
  } catch (error) {
    console.error("Error submitting answer for advanced analysis:", error);
    alert("Error connecting to the feedback service. Please try again.");
  } finally {
    submittingAnswer.value = false;
  }
};

// Submit answer for semantic similarity analysis
const submitSemanticAnswer = async () => {
  if (!currentAnswer.value.trim()) return;

  submittingAnswer.value = true;
  try {
    const response = await fetch(
      `/api/quiz/questions/${currentQuestion.value.id}/submit/semantic`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          answer: currentAnswer.value,
        }),
      }
    );

    if (response.ok) {
      const feedback = await response.json();
      feedbacks.value[currentQuestionIndex.value] = feedback;
    } else {
      console.error("Failed to submit answer for semantic analysis");
      alert("Failed to get semantic similarity feedback. Please try again.");
    }
  } catch (error) {
    console.error("Error submitting answer for semantic analysis:", error);
    alert(
      "Error connecting to the semantic analysis service. Please try again."
    );
  } finally {
    submittingAnswer.value = false;
  }
};

const formatTimestamp = (timestamp) => {
  try {
    return new Date(timestamp).toLocaleString();
  } catch {
    return timestamp;
  }
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
