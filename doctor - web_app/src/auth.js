import Axios from 'axios';

export default async function auth({
	next,
	store
}) {
	var user = JSON.parse(localStorage.getItem('user'));
	store.getters.auth.user = user;
	store.getters.auth.expire_at = localStorage.getItem('expire_at');

	if (!user) {
		return next({
			name: 'login'
		});
	}
	var result = false;

	await Axios.post('http://203.162.13.40/check_user', {
		data: user
	}).then(response => {
		if (response.data == '1') {
			if (new Date().getTime() > parseInt(store.getters.auth.expire_at)) {
				return next({
					name: 'login'
				});
			}
			result = true;
		}
	}).catch(err => {
		console.log(err);
	});

	if (result) {
		return next();
	}

	return next({
		name: 'login'
	});
}