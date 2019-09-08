<template>
    <footer class="footer px-4">
        <div class="row align-items-center justify-content-end">
            <div class="col-lg-6">
                <ul class="nav nav-footer justify-content-center justify-content-lg-end">
                    <li class="nav-item">
                        <a
                            href="#"
                            class="nav-link"
                            target="_blank"
                        >© {{year}} Hoàng Đức Minh và những người bạn</a>
                    </li>
                </ul>
            </div>
        </div>
    </footer>
</template>
<script>
export default {
    data() {
        return {
            year: new Date().getFullYear()
        };
    }
};
</script>
<style></style>
