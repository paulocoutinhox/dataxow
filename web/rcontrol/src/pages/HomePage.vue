<script setup>
import axios from "axios";
import { nextTick, ref, watch } from 'vue';
import PageFooter from '../components/PageFooter.vue';
import PageHeader from '../components/PageHeader.vue';
import useSharedState from '../states/app-state';

const { systemReady, apiUrl } = useSharedState();

const playerText = ref("DataXow\nSample\nText");
const playerImageUrl = ref("");
const playerVideoUrl = ref("");
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
    axios.post(apiUrl.value + '/module/call', {
        func: 'modules.player.show'
    })
        .then(() => {
            console.log('OK');
        }, (e) => {
            console.log(e);
        });
}

function playerHide() {
    axios.post(apiUrl.value + '/module/call', {
        func: 'modules.player.hide'
    })
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
    playerUpdateData({ type: 'image', url: playerImageUrl.value });
}

function playerSetVideo() {
    playerUpdateData({ type: 'video', url: playerVideoUrl.value });
}

function playerClear() {
    playerUpdateData({ type: 'clear' });
}
</script>

<template>
    <div v-if="systemReady">
        <div class="container">
            <PageHeader />

            <div class="row g-3">
                <h3>Player</h3>
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
                <h3>Text</h3>
                <div class="mb-3">
                    <label for="inputNewText" class="form-label">Type New Text:</label>
                    <textarea v-model="playerText" type="email" class="form-control" id="inputNewText"
                        ref="btPlayerClearText" rows="5"></textarea>
                </div>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerSetText">Update</button>
                </div>
                <div class="col-auto">
                    <button type="button" class="btn btn-primary" @click="playerClearText">Clear</button>
                </div>
            </div>

            <hr>

            <div class="row g-3">
                <h3>Image</h3>
                <div class="mb-3">
                    <label for="inputNewImage" class="form-label">Type New URL:</label>
                    <input v-model="playerImageUrl" type="text" class="form-control" id="inputNewImage">
                    <br>
                    <button type="button" class="btn btn-primary" @click="playerSetImage">Update</button>
                </div>
            </div>

            <hr>

            <div class="row g-3">
                <h3>Video</h3>
                <div class="mb-3">
                    <label for="inputNewVideo" class="form-label">Type New URL:</label>
                    <input v-model="playerVideoUrl" type="text" class="form-control" id="inputNewVideo">
                    <br>
                    <button type="button" class="btn btn-primary" @click="playerSetVideo">Update</button>
                </div>
            </div>

            <PageFooter />
        </div>
    </div>
</template>

<style scoped></style>
