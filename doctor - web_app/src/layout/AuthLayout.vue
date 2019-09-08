<template>
    <div class="main-content bg-gradient-success min-vh-100">
        <!-- Header -->
        <div class="header pt-7">
            <div class="container">
                <div class="header-body text-center pb-7">
                    <div class="row justify-content-center">
                        <div class="col-lg-5 col-md-6">
                            <h1 class="text-white">Welcome!</h1>
                            <p
                                class="text-lead text-white"
                            >Dùng ứng dụng VinID quét mã QR dưới đây để đăng nhập</p>
                        </div>
                        <!-- Page content -->
                        <div class="container pt-5">
                            <slide-y-up-transition mode="out-in" origin="center top">
                                <router-view></router-view>
                            </slide-y-up-transition>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { SlideYUpTransition } from "vue2-transitions";

export default {
    name: "auth-layout",
    components: {
        SlideYUpTransition
    },
    data() {
        return {
            year: new Date().getFullYear(),
            showMenu: false
        };
    }
};
</script>
<style>
</style>
