<script setup>
import { useRouter } from 'vue-router';
import PageHeader from '../components/PageHeader.vue';
import useSharedState from '../states/app-state';

const { apiUrl } = useSharedState();
const router = useRouter();

function back() {
    router.push({ name: 'Home', query: { api: apiUrl.value } });
}
</script>

<template>
    <div>
        <div class="container">
            <PageHeader />

            <div class="px-4 py-5 my-5 text-center">
                <router-link :to="{ name: 'Home' }">
                    <img src="../images/logo.png" alt="Logo" style="max-width: 100px" />
                </router-link>

                <h1 class="display-5 fw-bold text-body-emphasis">
                    DataXow
                    <br>
                    Remote Control App
                </h1>

                <button class="btn btn-primary" @click="back">Back</button>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
