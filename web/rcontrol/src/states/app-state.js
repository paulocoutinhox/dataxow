import { reactive, toRefs } from 'vue';

const state = reactive({
    systemSettings: {},
    systemReady: false,
    apiUrl: ""
});

function setSystemSettings(value) {
    state.systemSettings = value;
}

function setSystemReady(value) {
    state.systemReady = value;
}

function setApiUrl(value) {
    state.apiUrl = value;
}

export default function useSharedState() {
    return {
        ...toRefs(state),
        setSystemReady,
        setSystemSettings,
        setApiUrl
    };
}
