/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** 后端 API 基地址；开发期走 Vite 代理，生产期走同源 /api */
  readonly VITE_API_BASE_URL: string
  readonly VITE_APP_TITLE: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
