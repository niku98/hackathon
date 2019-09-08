<template>
    <div class="row justify-content-center">
        <div class="card d-inline-block bg-secondary shadow border-0">
            <div class="card-body px-lg-5 py-lg-5">
                <img :src="qr_code" style="width: 150px" />
                <div v-if="!qr_code" class="lds-css ng-scope">
                    <div style="width:100%;height:100%" class="lds-ripple">
                        <div></div>
                        <div></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    data() {
        return {
            qr_code: null
        };
    },
    mounted() {
        if (
            this.$store.getters.auth.user &&
            new Date().getTime() <= parseInt(this.$store.getters.auth.expire_at)
        ) {
            return (window.location.href = "/");
        }
        this.$http
            .post(this.apiUrl + "/gen_qrcode", {
                data: {
                    socket_id: this.socketio.id
                }
            })
            .then(response => {
                this.qr_code = response.data["qr_generate"];
            })
            .catch(error => {
                console.log(error);
            });
    }
};
</script>
<style lang="css">
@keyframes lds-ripple {
    0% {
        top: 96px;
        left: 96px;
        width: 0;
        height: 0;
        opacity: 1;
    }
    100% {
        top: -2px;
        left: -2px;
        width: 196px;
        height: 196px;
        opacity: 0;
    }
}
@-webkit-keyframes lds-ripple {
    0% {
        top: 96px;
        left: 96px;
        width: 0;
        height: 0;
        opacity: 1;
    }
    100% {
        top: -2px;
        left: -2px;
        width: 196px;
        height: 196px;
        opacity: 0;
    }
}
.lds-ripple {
    position: relative;
}
.lds-ripple div {
    box-sizing: content-box;
    position: absolute;
    border-width: 4px;
    border-style: solid;
    opacity: 1;
    border-radius: 50%;
    -webkit-animation: lds-ripple 1.5s cubic-bezier(0, 0.2, 0.8, 1) infinite;
    animation: lds-ripple 1.5s cubic-bezier(0, 0.2, 0.8, 1) infinite;
}
.lds-ripple div:nth-child(1) {
    border-color: #29b6f6;
}
.lds-ripple div:nth-child(2) {
    border-color: #29b6f6;
    -webkit-animation-delay: -0.75s;
    animation-delay: -0.75s;
}
.lds-ripple {
    width: 150px !important;
    height: 150px !important;
    -webkit-transform: translate(-75px, -75px) scale(0.75) translate(75px, 75px);
    transform: translate(-75px, -75px) scale(0.75) translate(75px, 75px);
}
</style>