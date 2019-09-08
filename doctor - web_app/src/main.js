/*!

=========================================================
* Vue Argon Dashboard - v1.0.0
=========================================================

* Product Page: https://www.creative-tim.com/product/argon-dashboard
* Copyright 2019 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://github.com/creativetimofficial/argon-dashboard/blob/master/LICENSE.md)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

*/
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import './registerServiceWorker'
import ArgonDashboard from './plugins/argon-dashboard'
import io from 'socket.io-client';
import Axios from 'axios'
import store from './store'

Vue.prototype.$http = Axios;
var api = 'http://203.162.13.40';
Vue.prototype.apiUrl = api;

Vue.config.productionTip = false
/**
 * Socket io
 */
const socket = io(api);
socket.on("connect", function () {
	// console.log("Connected");
});
socket.on("disconnect", function () {
	// console.log("Disconnected");
});

socket.on('receive_user', (user) => {
	let expire_at = new Date().getTime() + 3 * 60 * 60 * 1000;

	localStorage.setItem('user', JSON.stringify(user));
	localStorage.setItem('expire_at', expire_at);

	store.getters.auth.user = user;
	store.getters.auth.expire_at = expire_at;
	window.location.href = '/';
});

Vue.mixin({
	data() {
		return {
			socketio: socket,
			user: localStorage.getItem('user')
		}
	},

	mounted() {},
})

Vue.use(ArgonDashboard)
new Vue({
	router,
	store,
	render: h => h(App),
}).$mount('#app')