<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "../../../utils/api.js"; // axios instance
import {
  ArrowLeft as ArrowLeftIcon,
  ArrowRight as ArrowRightIcon,
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

// State
const course = ref({});
const chapters = ref([]);
const loading = ref(true);

// Computed properties for navigation
const cameFromHome = computed(() => {
  return (
    router.options.history.state?.back === "/home" ||
    document.referrer.includes("/home")
  );
});

const backButtonText = computed(() => {
  return cameFromHome.value ? "Back to Home" : "Back to Courses";
});

// Methods
const goBack = () => {
  router.push(cameFromHome.value ? "/home" : "/courses");
};

const startChapterQuiz = (chapterId) => {
  router.push(`/quiz/${chapterId}`);
};

const difficultyBadge = (level) => {
  switch (level) {
    case "Beginner":
      return "badge badge-success";
    case "Intermediate":
      return "badge badge-warning";
    case "Advanced":
      return "badge badge-error";
    default:
      return "badge badge-outline";
  }
};

// Fetch data
onMounted(async () => {
  const courseId = route.params.courseId;
  loading.value = true;

  try {
    const { data: allCourses } = await axios.get("/courses"); // uses token automatically

    const foundCourse = allCourses.find((c) => c.id === courseId);
    if (foundCourse) {
      course.value = foundCourse;
    } else {
      console.warn(`Course with ID ${courseId} not found`);
      course.value = {
        id: courseId,
        title: "Course Not Found",
        description: "The requested course could not be found.",
        difficulty: "Unknown",
        estimatedTime: "N/A",
      };
    }

    const { data: chapterList } = await axios.get(`/quiz/courses/${courseId}/chapters`);
    chapters.value = chapterList;

    // Fetch question counts
    await Promise.all(
      chapters.value.map(async (chapter) => {
        try {
          const { data: questions } = await axios.get(
            `/quiz/chapters/${chapter.id}/questions`
          );
          chapter.questionCount = questions.length;
        } catch (err) {
          console.error(`Error fetching questions for chapter ${chapter.id}:`, err);
          chapter.questionCount = 0;
        }
      })
    );
  } catch (err) {
    console.error("Error loading course or chapters:", err);
  } finally {
    loading.value = false;
  }
});
</script>
