<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../../../utils/api.js"; // ✅ import your Axios instance
import {
  ArrowLeft as ArrowLeftIcon,
  ArrowRight as ArrowRightIcon,
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

const course = ref({});
const chapters = ref([]);
const loading = ref(true);

const cameFromHome = computed(() =>
  router.options.history.state?.back === "/home" || document.referrer.includes("/home")
);

const backButtonText = computed(() => (cameFromHome.value ? "Back to Home" : "Back to Courses"));

const goBack = () => {
  router.push(cameFromHome.value ? "/home" : "/courses");
};

const startChapterQuiz = (chapterId) => {
  router.push(`/quiz/${chapterId}`);
};

const difficultyBadge = (level) => {
  switch (level) {
    case "Beginner": return "badge badge-success";
    case "Intermediate": return "badge badge-warning";
    case "Advanced": return "badge badge-error";
    default: return "badge badge-outline";
  }
};

onMounted(async () => {
  const courseId = route.params.courseId;

  try {
    // ✅ fetch all courses
    const courseRes = await api.get("/quiz/courses");
    const allCourses = courseRes.data;

    const foundCourse = allCourses.find((c) => c.id === courseId);
    if (foundCourse) {
      course.value = foundCourse;
    } else {
      course.value = {
        id: courseId,
        title: "Course Not Found",
        description: "The requested course could not be found.",
        difficulty: "Unknown",
        estimatedTime: "N/A",
      };
    }

    // ✅ fetch chapters for selected course
    const chaptersRes = await api.get(`/quiz/courses/${courseId}/chapters`);
    chapters.value = chaptersRes.data;

    // ✅ enrich each chapter with question count
    for (let chapter of chapters.value) {
      try {
        const questionsRes = await api.get(`/quiz/chapters/${chapter.id}/questions`);
        chapter.questionCount = questionsRes.data.length;
      } catch (err) {
        console.warn(`Failed to fetch questions for chapter ${chapter.id}:`, err);
        chapter.questionCount = 0;
      }
    }
  } catch (err) {
    console.error("Error loading course or chapters:", err);
  } finally {
    loading.value = false;
  }
});
</script>
