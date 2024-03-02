<script setup>
import axios from "axios";
import { nextTick, ref, watch } from 'vue';
import PageHeader from '../components/PageHeader.vue';
import PageFooter from '../components/PageFooter.vue';
import useSharedState from '../states/app-state';

const { systemReady, apiUrl } = useSharedState();

const playerText = ref("DataXow Sample Text");
const btPlayerClearText = ref(null);

watch([systemReady], ([value]) => {
    if (value) {
        onSystemIsReady();
    }
}, {
    immediate: true, deep: true
});

function onSystemIsReady() {
    console.log('Remote Control Is Ready');
}

function playerUpdateData(data, success, error) {
    axios.post(apiUrl.value + '/module/call', {
        func: 'modules.player.update',
        params: data,
    })
        .then(() => {
            console.log('OK');

            if (success) {
                success();
            }
        }, (e) => {
            console.log(e);

            if (error) {
                error(e);
            }
        });
}

function playerShow() {
    axios.post(apiUrl.value + '/module/call', { 'func': 'modules.player.show' })
        .then(() => {
            console.log('OK');
        }, (e) => {
            console.log(e);
        });
}

function playerHide() {
    axios.post(apiUrl.value + '/module/call', { 'func': 'modules.player.hide' })
        .then(() => {
            console.log('OK');
        }, (e) => {
            console.log(e);
        });
}

function playerSetText() {
    playerUpdateData({
        'type': 'text',
        'text': playerText.value
    });
}

function playerClearText() {
    playerText.value = "";

    nextTick(() => {
        btPlayerClearText.value.focus();
    });
}

function playerSetImage() {
    playerUpdateData({ type: 'image', src: 'https://picsum.photos/1024/768/?blur=2&rnd=' + new Date().getTime() });
}

function playerSetVideo() {
    playerUpdateData({ type: 'video', src: 'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4' });
}

function playerClear() {
    playerUpdateData({ type: 'black' });
}
</script>

<template>
    <div v-if="systemReady">
        <div class="container">
            <PageHeader />

            <div class="row g-3">
                <h3>Player - Control</h3>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerShow">Show Player</button>
                </div>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerHide">Hide Player</button>
                </div>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerClear">Clear Player</button>
                </div>
            </div>

            <hr>

            <div class="row g-3">
                <h3>Player - Text</h3>
                <div class="mb-3">
                    <label for="inputNewText" class="form-label">New Text</label>
                    <input v-model="playerText" type="email" class="form-control" id="inputNewText" ref="btPlayerClearText">
                </div>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerSetText">Send</button>
                </div>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerClearText">Clear</button>
                </div>
            </div>

            <hr>

            <div class="row g-3">
                <h3>Player - Image</h3>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerSetImage">Send</button>
                </div>
            </div>

            <hr>

            <div class="row g-3">
                <h3>Player - Video</h3>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerSetVideo">Send</button>
                </div>
            </div>

            <PageFooter />
        </div>
    </div>
</template>

<style scoped></style>
