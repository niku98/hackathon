import Vue from 'vue'
import Router from 'vue-router'
import auth from './auth'
import DashboardLayout from '@/layout/DashboardLayout'
import AuthLayout from '@/layout/AuthLayout'
import middlewarePipeline from './middlewarePipeline'
import store from './store'
Vue.use(Router)

var router = new Router({
	linkExactActiveClass: 'active',
	routes: [{
		path: '/',
		redirect: 'dashboard',
		component: DashboardLayout,
		children: [{
			path: '/dashboard',
			name: 'dashboard',
			meta: {
				middleware: [auth],
			},
			// route level code-splitting
			// this generates a separate chunk (about.[hash].js) for this route
			// which is lazy-loaded when the route is visited.
			component: () => import( /* webpackChunkName: "demo" */ './views/Dashboard.vue')
		}, ]
	}, {
		path: '/',
		redirect: 'login',
		component: AuthLayout,
		children: [{
			path: '/login',
			name: 'login',
			component: () => import( /* webpackChunkName: "demo" */ './views/Login.vue')
		}]
	}]
})

router.beforeEach((to, from, next) => {
	if (!to.meta.middleware) {
		return next()
	}

	const middleware = to.meta.middleware

	const context = {
		to,
		from,
		next,
		store
	}
	return middleware[0]({
		...context,
		next: middlewarePipeline(context, middleware, 1)
	})
})

export default router