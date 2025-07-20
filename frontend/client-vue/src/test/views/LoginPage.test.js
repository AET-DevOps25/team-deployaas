import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import { createRouter, createWebHistory } from "vue-router";
import LoginPage from "../../views/LoginPage.vue";

// Mock axios
vi.mock("../../utils/api.js", () => ({
  default: {
    post: vi.fn(),
  },
}));

// Mock components that LoginPage imports
vi.mock("../../views/components/Navbar.vue", () => ({
  default: { template: '<div data-testid="navbar">Navbar</div>' },
}));

vi.mock("../../views/components/Footer.vue", () => ({
  default: { template: '<div data-testid="footer">Footer</div>' },
}));

// Create a test router
const createTestRouter = () => {
  return createRouter({
    history: createWebHistory(),
    routes: [
      { path: "/login", component: LoginPage },
      { path: "/register", component: { template: "<div>Register</div>" } },
      { path: "/home", component: { template: "<div>Home</div>" } },
    ],
  });
};

describe("LoginPage Component", () => {
  let wrapper;
  let router;

  beforeEach(async () => {
    router = createTestRouter();
    await router.push("/login");
    vi.clearAllMocks();
    // Clear localStorage
    localStorage.clear();
  });

  afterEach(() => {
    wrapper?.unmount();
  });

  it("renders login form with required elements", async () => {
    wrapper = mount(LoginPage, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.find("h2").text()).toBe("Login");
    expect(wrapper.find('input[type="email"]').exists()).toBe(true);
    expect(wrapper.find('input[type="password"]').exists()).toBe(true);
    expect(wrapper.find("button").exists()).toBe(true);
    expect(wrapper.find('[data-testid="navbar"]').exists()).toBe(true);
    expect(wrapper.find('[data-testid="footer"]').exists()).toBe(true);
  });

  it("has proper form validation attributes", () => {
    wrapper = mount(LoginPage, {
      global: {
        plugins: [router],
      },
    });

    const emailInput = wrapper.find('input[type="email"]');
    const passwordInput = wrapper.find('input[type="password"]');

    expect(emailInput.attributes("required")).toBeDefined();
    expect(passwordInput.attributes("required")).toBeDefined();
  });

  it("contains navigation to register page", () => {
    wrapper = mount(LoginPage, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.find('a[href="/register"]').exists()).toBe(true);
  });
});
