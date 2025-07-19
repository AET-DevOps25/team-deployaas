// Test setup file for Vitest
import { vi } from "vitest";

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
};
global.localStorage = localStorageMock;

// Mock router
export const mockRouter = {
  push: vi.fn(),
  replace: vi.fn(),
  go: vi.fn(),
  back: vi.fn(),
  forward: vi.fn(),
  currentRoute: {
    value: {
      path: "/",
      params: {},
      query: {},
      meta: {},
    },
  },
};

// Mock route
export const mockRoute = {
  params: {},
  query: {},
  path: "/",
  meta: {},
};

// Add global atob and btoa for JWT parsing tests
global.atob = (str) => Buffer.from(str, "base64").toString("binary");
global.btoa = (str) => Buffer.from(str, "binary").toString("base64");

// Mock window.alert for tests
global.alert = vi.fn();

// Mock console methods to avoid noise in tests
global.console = {
  ...console,
  log: vi.fn(),
  warn: vi.fn(),
  error: vi.fn(),
};

// Reset mocks before each test
beforeEach(() => {
  vi.clearAllMocks();
  localStorageMock.getItem.mockReturnValue(null);
  localStorageMock.setItem.mockClear();
  localStorageMock.removeItem.mockClear();
  localStorageMock.clear.mockClear();

  // Reset router mock
  mockRouter.push.mockClear();
  mockRouter.replace.mockClear();
  mockRouter.go.mockClear();
  mockRouter.currentRoute.value = {
    path: "/",
    params: {},
    query: {},
    meta: {},
  };
});

// Vue 3 reactivity mocks for components that need them
global.ref = (initialValue) => ({
  value: initialValue,
});

global.computed = (fn) => ({
  get value() {
    return fn();
  },
});

global.reactive = (obj) => obj;

global.watch = vi.fn();
global.watchEffect = vi.fn();
global.nextTick = () => Promise.resolve();
