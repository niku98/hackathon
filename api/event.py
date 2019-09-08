from flask import request
from flask_socketio import *
from init import *


@socketio.on('detect_user')
def detect_user(user):
    print 'Joined user: ' + str(user)
    join_room(user)


@socketio.on('detect_room')
def detect_room(room):
    print 'Joined: ' + 'room_' + str(room)
    join_room('room_' + str(room))


@socketio.on('notify_patient')
def notify_patient(user_id):
    print 'Notify ' + str(user_id)
    socketio.emit('notifying', {'stt': 1}, room=user_id)


@socketio.on('disconnect')
def disconnect():
    print request.sid + ' disconnected!'


@socketio.on('connect')
def connect():
    print request.sid + ' connected!'


@socketio.on_error()        # Handles the default namespace
def error_handler(e):
    print e
