<template>
    <div>
        <base-header type="gradient-success" class="pb-6 pb-8 pt-5 pt-md-8">
            <h1
                v-if="$store.getters.auth.user.faculty"
                class="text-white"
            >Khoa {{ $store.getters.auth.user.faculty.name }}</h1>
            <h4
                v-if="$store.getters.auth.user.room"
                class="text-white"
            >Phòng {{ $store.getters.auth.user.room.number }}</h4>
        </base-header>

        <!--Charts-->
        <div class="container-fluid mt--7">
            <div class="row">
                <div class="col-md-8">
                    <card header-classes="bg-transparent">
                        <div slot="header" class="row align-items-center">
                            <div class="col">
                                <h5 class="h3 mb-0">Khám bệnh</h5>
                            </div>
                            <div
                                v-if="currentQueue != null"
                                class="col text-right font-weight-bold"
                            >
                                <span>{{ currentQueue.number_queue.count }}</span>
                            </div>
                        </div>
                        <form v-if="queues.length > 0" @submit="examDone">
                            <div class="row align-items-center">
                                <div class="col-md-12">
                                    <base-input
                                        class="input-group-alternative"
                                        placeholder="Họ và tên"
                                        v-model="medicalRecord.patient.name"
                                        disabled
                                    ></base-input>
                                </div>
                                <div class="col-md-12">
                                    <textarea
                                        class="form-control form-control-alternative"
                                        rows="3"
                                        nativeType="button"
                                        placeholder="Mô tả bệnh..."
                                        v-model="medicalRecord.description"
                                        required
                                    ></textarea>
                                </div>
                            </div>
                        </form>
                        <div v-else>
                            <h1 class="font-italic text-muted">Không có bệnh nhân</h1>
                        </div>
                        <div v-if="queues.length > 0" slot="footer" class="text-right">
                            <base-button
                                nativeType="button"
                                outline
                                @click="notifyPatient"
                                type="info"
                            >Gọi bệnh nhân</base-button>
                            <base-button
                                nativeType="button"
                                outline
                                @click="deleteQueue"
                                type="danger"
                            >Bệnh nhân không khám</base-button>
                            <base-button
                                outline
                                nativeType="submit"
                                @click="examDone"
                                type="success"
                            >Khám xong</base-button>
                        </div>
                    </card>
                </div>
                <div class="col-md-4">
                    <h1 class="text-white">Danh sách chờ</h1>
                    <div class="scrollable">
                        <stats-card
                            v-bind:title="'STT: ' + (index + 1)"
                            v-bind:type="'gradient-red'"
                            v-bind:sub-title="queue.User.user_name.toUpperCase()"
                            class="mb-4"
                            v-for="(queue, index) in queues"
                            v-if="index > 0"
                        >
                            <div
                                slot="icon"
                                class="icon icon-shape text-white font-weight-bold rounded-circle shadow bg-gradient-success"
                            >{{ queue.number_queue.count }}</div>
                            <!-- <template slot="	footer">
                                <span class="text-nowrap">{{  }}</span>
                            </template>-->
                        </stats-card>
                    </div>
                </div>
            </div>
            <!-- End charts-->
        </div>
    </div>
</template>
<script>
export default {
    data() {
        return {
            medicalRecord: {
                patient: {},
                description: ""
            },
            currentQueue: null,
            queues: []
        };
    },
    watch: {
        queues(oldVal, newVal) {
            if (this.queues.length) {
                this.medicalRecord.patient.name = this.queues[0]["User"][
                    "user_name"
                ];
                this.medicalRecord.patient.id = this.queues[0]["User"][
                    "user_id"
                ];
                this.currentQueue = this.queues[0];
            } else {
                this.medicalRecord = {
                    patient: {},
                    description: ""
                };
                this.currentQueue = null;
            }
        }
    },
    methods: {
        notifyPatient() {
            this.socketio.emit("notify_patient", this.medicalRecord.patient.id);
        },
        deleteQueue(needConfirm = true) {
            if (needConfirm && !confirm("Bạn chắc chứ?")) {
                return;
            }
            this.$http
                .post(this.apiUrl + "/delete_queue", {
                    data: {
                        queue_id: this.currentQueue.id,
                        room_id: this.$store.getters.auth.user.room.id
                    }
                })
                .then(response => {
                    this.queues.shift();
                });
        },
        examDone() {
            if (
                this.medicalRecord.description == null ||
                this.medicalRecord.description == ""
            ) {
                alert("Không được để trống!");
                return;
            } else if (!confirm("Bạn chắc chứ?")) {
                return;
            }
            this.$http
                .post(this.apiUrl + "/create_record", {
                    data: this.medicalRecord
                })
                .then(response => {
                    this.deleteQueue(false);
                });
        },
        registerSocketListener() {
            this.socketio.on("connect", () => {
                this.socketio.emit(
                    "detect_room",
                    this.$store.getters.auth.user.room.id
                );
            });
            this.socketio.on("created_queue", queue => {
                this.queues.push(queue);
            });
        },

        processQueuesData(queues) {
            this.queues = queues;
        }
    },
    mounted() {
        this.registerSocketListener();
        this.socketio.emit(
            "detect_room",
            this.$store.getters.auth.user.room.id
        );

        this.$http
            .get(
                this.apiUrl +
                    "/get_info_queue?doctor_id=" +
                    this.$store.getters.auth.user.id
            )
            .then(response => {
                if (response.data.length == 0 || !response.data) {
                    return;
                }

                this.processQueuesData(response.data);
            });
    }
};
</script>
<style lang="scss">
.scrollable {
    max-height: 300px;
    overflow: auto;

    &::-webkit-scrollbar {
        width: 7px;
        background-color: transparent;
    }

    &::-webkit-scrollbar-thumb {
        background-color: rgba(0, 0, 0, 0.5);
        border-radius: 5px;
    }

    &::-webkit-scrollbar-thumb:hover {
        background-color: rgba(0, 0, 0, 0.7);
    }
}
</style>