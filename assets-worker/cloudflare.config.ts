import { bindings, defineConfig } from "cf/config";

export default defineConfig({
  worker: {
    name: "claw-zipline-assets-worker",
    compatibilityDate: "2026-08-30",
    compatibilityFlags: ["nodejs_compat"],
    entrypoint: "src/index.ts",
    workersDev: false,
    observability: {
      enabled: true,
      logs: {
        headSamplingRate: 1,
      },
      traces: {
        enabled: true,
        headSamplingRate: 0.01,
      },
    },
    domains: ["claw.msfjarvis.dev"],
    env: {
      ADMIN_PASSWORD: bindings.secret(),
      RELEASES: bindings.kv({
        id: "283210e7cba04597b8aa8cdd5a4e6291",
      }),
      ASSETS: bindings.r2({
        name: "claw-zipline-assets",
      }),
    },
  },
});
