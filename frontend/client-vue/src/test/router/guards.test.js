import { describe, it, expect, beforeEach, vi } from "vitest";
import { createRouter, createWebHistory } from "vue-router";

const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
};
Object.defineProperty(window, "localStorage", { value: localStorageMock });

describe("Router Navigation Guards", () => {
  let router;

  beforeEach(() => {
    vi.clearAllMocks();
    localStorageMock.getItem.mockReturnValue(null);

    // Create router with the same configuration as the real app
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: "/", component: { template: "<div>Landing</div>" } },
        { path: "/login", component: { template: "<div>Login</div>" } },
        { path: "/register", component: { template: "<div>Register</div>" } },
        {
          path: "/home",
          component: { template: "<div>Home</div>" },
          meta: { requiresAuth: true },
        },
        {
          path: "/courses",
          component: { template: "<div>Courses</div>" },
          meta: { requiresAuth: true },
        },
      ],
    });

    router.beforeEach((to, from, next) => {
      const isAuthenticated = !!localStorage.getItem("token");

      if (to.meta.requiresAuth && !isAuthenticated) {
        next("/login");
      } else if (
        (to.path === "/login" || to.path === "/register") &&
        isAuthenticated
      ) {
        next("/home");
      } else {
        next();
      }
    });
  });

  it("redirects unauthenticated users from protected routes to login", async () => {
    localStorageMock.getItem.mockReturnValue(null);

    await router.push("/home");
    expect(router.currentRoute.value.path).toBe("/login");

    await router.push("/courses");
    expect(router.currentRoute.value.path).toBe("/login");
  });

  it("allows authenticated users to access protected routes", async () => {
    localStorageMock.getItem.mockReturnValue("fake-token");

    await router.push("/home");
    expect(router.currentRoute.value.path).toBe("/home");

    await router.push("/courses");
    expect(router.currentRoute.value.path).toBe("/courses");
  });

  it("redirects authenticated users from auth pages to home", async () => {
    localStorageMock.getItem.mockReturnValue("fake-token");

    await router.push("/login");
    expect(router.currentRoute.value.path).toBe("/home");

    await router.push("/register");
    expect(router.currentRoute.value.path).toBe("/home");
  });

  it("allows unauthenticated users to access auth and public pages", async () => {
    localStorageMock.getItem.mockReturnValue(null);

    await router.push("/");
    expect(router.currentRoute.value.path).toBe("/");

    await router.push("/login");
    expect(router.currentRoute.value.path).toBe("/login");

    await router.push("/register");
    expect(router.currentRoute.value.path).toBe("/register");
  });
});
