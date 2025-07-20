import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import { createRouter, createWebHistory } from "vue-router";
import Navbar from "../../views/components/Navbar.vue";

const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
};
Object.defineProperty(window, "localStorage", { value: localStorageMock });

const createTestRouter = () => {
  return createRouter({
    history: createWebHistory(),
    routes: [
      { path: "/", component: { template: "<div>Landing</div>" } },
      { path: "/login", component: { template: "<div>Login</div>" } },
      { path: "/register", component: { template: "<div>Register</div>" } },
    ],
  });
};

describe("Navbar Component", () => {
  let wrapper;
  let router;

  beforeEach(async () => {
    router = createTestRouter();
    await router.push("/");
    vi.clearAllMocks();
  });

  afterEach(() => {
    wrapper?.unmount();
  });

  it("renders navbar with brand elements", () => {
    localStorageMock.getItem.mockReturnValue(null);

    wrapper = mount(Navbar, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.find("span").text()).toBe("Study Assistant");
    expect(wrapper.find('a[href="/"]').exists()).toBe(true);
  });

  it("shows login and register buttons when not authenticated", () => {
    localStorageMock.getItem.mockReturnValue(null);

    wrapper = mount(Navbar, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.find('a[href="/login"]').exists()).toBe(true);
    expect(wrapper.find('a[href="/register"]').exists()).toBe(true);
    expect(wrapper.find("button").exists()).toBe(false);
  });

  it("shows logout button when authenticated", () => {
    localStorageMock.getItem.mockReturnValue("fake-token");

    wrapper = mount(Navbar, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.find("button").exists()).toBe(true); // Logout button
    expect(wrapper.find('a[href="/login"]').exists()).toBe(false);
    expect(wrapper.find('a[href="/register"]').exists()).toBe(false);
  });
});
