import { createWebHistory, createRouter } from "vue-router";

import HomePage from '@/pages/HomePage'
import ImageListPage from '@/pages/ImageListPage'
import AboutPage from '@/pages/AboutPage'
import NotFoundPage from '@/pages/NotFoundPage'

const routes = [
    {
        path: "/",
        name: "Home",
        component: HomePage,
    },
    {
        path: "/images",
        name: "ImageList",
        component: ImageListPage,
    },
    {
        path: "/about",
        name: "About",
        component: AboutPage,
    },
    {
        path: "/:catchAll(.*)",
        component: NotFoundPage,
    },
];

const base = process.env.NODE_ENV === 'production' ? '/rcontrol/' : '/';

const router = createRouter({
    history: createWebHistory(base),
    routes,
});

export default router;
