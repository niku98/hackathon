import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)


export default new Vuex.Store({
	state: {
		$auth: {
			logged_in: false,
			expire_at: null,
			user: {

			}
		}
	},

	getters: {
		auth(state) {
			return state.$auth
		}
	}
})