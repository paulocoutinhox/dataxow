<script setup>
import axios from "axios";
import { computed, onMounted, ref, watch } from 'vue';
import PageFooter from '../components/PageFooter.vue';
import PageHeader from '../components/PageHeader.vue';
import useSharedState from '../states/app-state';

const { systemReady, apiUrl } = useSharedState();

const imageFolder = ref([]);
const rawImageList = ref([])

const imageList = computed(() => {
    return rawImageList.value.filter(image => !image.isDirectory);
});

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

function getImageList(success, error) {
    axios.post(apiUrl.value + '/module/call', {
        func: 'modules.image.list',
        params: {
            path: imageFolder.value.join("/")
        },
    })
        .then((r) => {
            console.log('OK');

            if (r && r.data && r.data.data && r.data.data.list) {
                rawImageList.value = r.data.data.list;
            }

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

function refresh() {
    getImageList();
}

function playerChangeImage(image) {
    playerUpdateData({
        type: "image",
        path: imageFolder.value.join("/") + image
    });
}

onMounted(() => {
    getImageList();
})
</script>

<template>
    <div v-if="systemReady">
        <div class="container">
            <PageHeader />

            <p class="text-center">
                <button class="btn btn-primary" @click="refresh">Refresh</button>
            </p>

            <div v-if="imageList && imageList.length > 0" class="row">
                <div class="col-6 col-md-4" v-for="image in imageList" :key="image.path">
                    <div class="image-box">
                        <img :src="`${apiUrl}/image?path=${imageFolder.join('/')}${image.path}`"
                            class="img-thumbnail rounded img-item" alt="Responsive image"
                            @click="playerChangeImage(image.path)">
                    </div>
                </div>
            </div>
            <div v-else class="row text-center">
                <p>Empty List</p>
            </div>

            <PageFooter />
        </div>
    </div>
</template>

<style scoped>
.image-box {
    padding: 10px;
    margin-bottom: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.img-item {
    height: 200px;
    width: auto;
    object-fit: cover;
    cursor: pointer;
}
</style>
