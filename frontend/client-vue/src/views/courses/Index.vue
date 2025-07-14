<script setup>
import { ref, onMounted } from "vue";
import { RouterLink } from "vue-router";
import api from "../../utils/api.js"; // ✅ axios instance with token

import {
  BookOpen as BookOpenIcon,
  ArrowLeft as ArrowLeftIcon,
  Code as CodeIcon,
  Database as DatabaseIcon,
  Shield as ShieldIcon,
  Cloud as CloudIcon,
  Smartphone as SmartphoneIcon,
  Brain as BrainIcon,
  Globe as GlobeIcon,
  Cpu as CpuIcon,
} from "lucide-vue-next";

const courses = ref([]);
const loading = ref(true);

// icon mapping
const iconMap = {
  code: CodeIcon,
  database: DatabaseIcon,
  shield: ShieldIcon,
  cloud: CloudIcon,
  smartphone: SmartphoneIcon,
  brain: BrainIcon,
  globe: GlobeIcon,
  cpu: CpuIcon,
};
const DefaultIcon = CodeIcon;

function difficultyBadge(level) {
  switch (level) {
    case "Beginner":
      return "badge badge-success badge-sm";
    case "Intermediate":
      return "badge badge-warning badge-sm";
    case "Advanced":
      return "badge badge-error badge-sm";
    default:
      return "badge badge-outline badge-sm";
  }
}

// ✅ fetch with token
onMounted(async () => {
  try {
    const res = await api.get("/courses"); // ✅ your Nginx routes /api/courses → /api/quiz/courses
    courses.value = res.data;
  } catch (e) {
    console.error("Failed to load courses:", e);
  } finally {
    loading.value = false;
  }
});
</script>
