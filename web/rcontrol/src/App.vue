<script setup>
import axios from "axios";
import { onMounted } from 'vue';
import useSharedState from './states/app-state';

onMounted(() => {
    const { setSystemReady, setSystemSettings, setApiUrl } = useSharedState();

    const urlParams = new URLSearchParams(window.location.search);
    const apiUrl = urlParams.get('api');

    if (apiUrl) {
        setApiUrl(apiUrl);

        axios.post(apiUrl + '/module/call', { 'func': 'modules.system.settings' })
            .then((response) => {
                setSystemSettings(response.data.result);
                setSystemReady(true);

                console.log("System settings is loaded!");
            }, (error) => {
                console.log(error);
            });
    }
});
</script>

<template>
    <router-view />
</template>

<style></style>
