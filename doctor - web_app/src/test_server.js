var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

io.on('connection', function (socket) {
	console.log('connection made');

	socket.on('CH01', function (from, msg) {
		console.log('MSG', from, ' saying ', msg);
	});

	socket.on('test', function (from, msg) {
		console.log("we got test");
	});

});

http.listen(3000, function () {
	console.log('listening on *:3000');
});