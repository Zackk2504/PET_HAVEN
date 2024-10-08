import { resolve } from "path";
export default {
    head: {
        title: 'My Nuxt App',
        meta: [
            {charset: 'utf-8'},
            {name: 'viewport', content: 'width=device-width, initial-scale=1'},
        ],
    },

    css: [
        './assets/css/bootstrap.min.css',
        './assets/cat.css',
    ],

    js: [
        './assets/js/bootstrap.bundle.min.js'
    ],

    dev: process.env.NODE_ENV !== 'production',
    devtools: {
        enabled: process.env.NODE_ENV === 'development',
        timeline: {
            enabled: true
        }
    },

    server: {
        port: 3000,
    },

    compatibilityDate: '2024-10-03',

    modules: [
        '@nuxtjs/i18n',
        '@pinia/nuxt',
        '@nuxt/image'
    ],

    buildModules: [
        '@nuxt/typescript-build'
    ],
    i18n: {
        locales: [
            { code: 'en', name: 'English', file: 'en.ts' },
            { code: 'vi', name: 'Tiếng Việt', file: 'vi.ts' }
        ],
        defaultLocale: 'en',
        lazy: true,
        langDir: 'i18n/',
        vueI18nLoader: true,
        detectBrowserLanguage: {
            useCookie: true,
            cookieKey: 'i18n_redirected',
            fallbackLocale: 'en'
        },
        strategy: 'no_prefix'
    },
    alias: {
        "@": resolve(__dirname, 'assets'),
        pinia: '/node_modules/@pinia/nuxt/node_modules/pinia/dist/pinia.mjs',
    }
};