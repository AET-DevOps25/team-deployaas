import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import { createRouter, createWebHistory } from "vue-router";
import RegisterPage from "../../views/RegisterPage.vue";

// Mock axios
vi.mock("../../utils/api.js", () => ({
  default: {
    post: vi.fn(),
  },
}));

// Mock components that RegisterPage imports
vi.mock("../../views/components/Navbar.vue", () => ({
  default: { template: '<div data-testid="navbar">Navbar</div>' },
}));

vi.mock("../../views/components/Footer.vue", () => ({
  default: { template: '<div data-testid="footer">Footer</div>' },
}));

const createTestRouter = () => {
  return createRouter({
    history: createWebHistory(),
    routes: [
      { path: "/register", component: RegisterPage },
      { path: "/login", component: { template: "<div>Login</div>" } },
    ],
  });
};

describe("RegisterPage Component", () => {
  let wrapper;
  let router;

  beforeEach(async () => {
    router = createTestRouter();
    await router.push("/register");
    vi.clearAllMocks();
    localStorage.clear();
  });

  afterEach(() => {
    wrapper?.unmount();
  });

  it("renders registration form with required elements", () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.find("h2").text()).toBe("Create an Account");
    expect(wrapper.find('input[type="text"]').exists()).toBe(true);
    expect(wrapper.find('input[type="email"]').exists()).toBe(true);
    expect(wrapper.find('input[type="password"]').exists()).toBe(true);
    expect(wrapper.find("button").exists()).toBe(true);
  });

  it("has proper form validation attributes", () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
      },
    });

    const nameInput = wrapper.find('input[type="text"]');
    const emailInput = wrapper.find('input[type="email"]');
    const passwordInput = wrapper.find('input[type="password"]');

    expect(nameInput.attributes("required")).toBeDefined();
    expect(emailInput.attributes("required")).toBeDefined();
    expect(passwordInput.attributes("required")).toBeDefined();
  });

  it("contains navigation to login page", () => {
    wrapper = mount(RegisterPage, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.find('a[href="/login"]').exists()).toBe(true);
  });
});
