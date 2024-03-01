<script setup></script>

<template>
    <header class="p-3 mb-3 border-bottom">
        <div class="container">
            <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
                <router-link :to="{ name: 'Home' }"
                    class="d-flex align-items-center mb-2 mb-lg-0 link-body-emphasis text-decoration-none">
                    <img alt="Logo" src="../images/logo.png" style="width: 50px" />
                </router-link>

                <ul class="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
                    <li class="nav-item">
                        <router-link :to="{ name: 'Home' }" class="nav-link px-2 link-body-emphasis">
                            Home
                        </router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :to="{ name: 'About' }" class="nav-link px-2 link-body-emphasis">
                            About
                        </router-link>
                    </li>
                </ul>
            </div>
        </div>
    </header>
</template>

<style scoped></style>
