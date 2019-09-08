#!/usr/bin/env python
# -*- coding: utf-8 -*-
from flask import *
import json
from flask_socketio import *
from model import *
from init import *
from event import *


@app.route('/')
def index():
    User_Id = request.headers['User_Id']
    check = Check_User(User_Id)
    if check == False:
        data = json.dumps(
            {
                "data": {
                    "metadata": {
                        "app_name": "Thông tin bệnh nhân",
                        "app_id": 123456,
                        "title": "Thông tin bệnh nhân",
                        "submit_button": {
                            "label": "Gửi thông tin",
                            "background_color": "#6666ff",
                            "cta": "request",
                            "url": "http://203.162.13.40/api_submit"
                        },
                        "reset_button": {
                            "label": "Xóa toàn bộ",
                            "background_color": "#669999"
                        },
                        "elements": [
                            {
                                "label": "Họ và tên*",
                                "type": "input",
                                "input_type": "text",
                                "required": True,
                                "error": "Thông tin này là bắt buộc",
                                "name": "Name",
                                "placeholder": "Họ và tên",
                            },
                            {
                                "label": "Địa chỉ*",
                                "type": "input",
                                "input_type": "text",
                                "required": True,
                                "error": "Thông tin này là bắt buộc",
                                "name": "Address",
                                "placeholder": "Địa Chỉ",
                            },
                            {
                                "label": "Số Điện Thoại*",
                                "type": "input",
                                "input_type": "text",
                                "required": True,
                                "error": "Thông tin này là bắt buộc",
                                "name": "Phone",
                                "placeholder": "Điện Thoại",
                            },
                            {
                                "label": "Họ và tên người thân*",
                                "type": "input",
                                "input_type": "text",
                                "required": True,
                                "error": "Thông tin này là bắt buộc",
                                "name": "Name_family",
                                "placeholder": "Họ và tên",
                            },
                            {
                                "label": "Số Điện Thoại Người Thân*",
                                "type": "input",
                                "input_type": "text",
                                "required": True,
                                "error": "Thông tin này là bắt buộc",
                                "name": "Phone_family",
                                "placeholder": "Điện Thoại",
                            },
                        ]
                    }
                }
            }
        )
    else:
        user = Get_User(User_Id)
        data = json.dumps(
            {
                "data": {
                    "metadata": {
                        "app_name": "Thông tin bệnh nhân",
                        "app_id": 123456,
                        "title": "Thông tin bệnh nhân",
                        "submit_button": {
                            "label": "Gửi thông tin",
                            "background_color": "#6666ff",
                            "cta": "deeplink",
                            "url": "http://www.test.com/?user_id=" + User_Id + "&name=" + user['Name'] + "&address=" + user['Address'] + "&phone=" + user['Phone']
                        },
                        "reset_button": {
                            "label": "Xóa toàn bộ",
                            "background_color": "#669999"
                        },
                        "elements": [
                            {
                                "label": "Họ và tên*",
                                "type": "input",
                                "input_type": "text",
                                "name": "Name",
                                "placeholder": user['Name'],
                            },
                            {
                                "label": "Địa chỉ*",
                                "type": "input",
                                "input_type": "text",
                                "name": "Address",
                                "placeholder": user['Address'],
                            },
                            {
                                "label": "Số Điện Thoại*",
                                "type": "input",
                                "input_type": 'text',
                                "name": "Phone",
                                "placeholder": user['Phone'],
                            }
                        ]
                    }
                }
            }
        )
    r = Response(response=data, status=200, mimetype="application/json")
    r.headers["Content-Type"] = "application/json"
    return r


@app.route('/get_user')
def get_user():
    user_id = request.args.get('user_id')
    user = Get_User(user_id)
    return json.dumps(user)


@app.route('/api_submit', methods=['POST'])
def submit():
    user = json.loads(request.data)
    User_Id = request.headers
    Create_User(User_Id['User_Id'], user['Name'],
                user['Address'], user['Phone'], user['Name_family'], user['Phone_family'])
    data = json.dumps(
        {
            "data": {
                "metadata": {
                    "app_name": "Thông tin bệnh nhân",
                    "app_id": 123456,
                    "title": "Thông tin bệnh nhân",
                    "submit_button": {
                        "label": "Gửi thông tin",
                        "background_color": "#6666ff",
                        "cta": "request",
                        "url": "http://203.162.13.40/api_submit"
                    },
                    "elements": [
                        {
                            "label": "Trạng Thái",
                            "type": "text",
                            "content": 'Success',
                        }
                    ]
                }
            }
        }
    )
    r = Response(response=data, status=200, mimetype="application/json")
    r.headers["Content-Type"] = "application/json"
    return r


@app.route('/detail_queue',methods=['POST'])
def Detail():
    sick_id = request.args.get('sick_id')
    User_Id = request.args.get('user_id')
    return json.dumps(Detail_queue(sick_id,user_id))
@app.route('/sick_submit', methods=['POST'])
def Sick_submit():
    sick_id = request.args.get('sick_id')
    User_Id = request.args.get('user_id')
    if Check_Create_Queue(User_Id):
        queue = Create_queue(sick_id, User_Id)
        print queue
        print 'room_' + queue['Room']['Id']
        socketio.emit('created_queue', queue, room=User_Id)
        socketio.emit('created_queue', queue, room='room_' + queue['Room']['Id'])
        return json.dumps(queue)
    else:
        queue_id = request.args.get('queue_id')
        queue = Detail_queue(queue_id)
        return json.dumps(queue)


@app.route('/get_sick', methods=['POST', 'GET'])
def get_sick():
    if request.method == 'GET':
        list_sick = Get_list_sick()
        return json.dumps(list_sick)


@app.route('/get_detail_doctor')
def get_detail():
    doctor_id = request.args.get('doctor_id')
    detail = Get_Detail_Doctor(doctor_id)
    return json.dumps(detail)


@app.route('/get_info_queue')
def Get_Info():
    doctor_id = request.args.get('doctor_id')
    info = Get_Info_Queue(doctor_id)
    return json.dumps(info)


@app.route('/delete_queue', methods=['POST'])
def delete_queue():
    queue_id = json.loads(request.data)
    user_id = Delete_queue(queue_id['data']['queue_id'])
    socketio.emit('deleted_queue', room=user_id)

    notifyToPatient(queue_id['data']['room_id'], 1)
    notifyToPatient(queue_id['data']['room_id'], 2)

    return str(user_id)


@app.route('/create_record', methods=['POST'])
def create_record():
    data = json.loads(request.data)
    create = Create_medical_records(
        data['data']['description'], data['data']['patient'])
    return str(create)


@app.route('/gen_qrcode', methods=['POST'])
def Gen_Qrcode():
    data = json.loads(request.data)
    qr = Gen_QR(data['data']['socket_id'])
    return json.dumps({'qr_generate': qr})


@app.route('/check_user', methods=['POST'])
def Check():
    data = json.loads(request.data)
    check = Check_Doctor(data['data']['id'])
    return '1' if check else '0'


@app.route('/check_qr', methods=['POST'])
def Check_QR():
    socket_id = request.args.get('socket_id')
    User_Id = request.headers
    check = Get_Detail_Doctor(User_Id['User_Id'])
    if check:
        if socket_id:
            socketio.emit('receive_user', check, room=socket_id)
            data = json.dumps(
                {
                	"data": {
                	    "metadata": {
                	        "app_name": "Kiểm tra đăng nhập",
                	        "app_id": 123456,
                	        "title": "Kiểm tra",
                	        "elements": [
                	            {
                	                "type": "text",
                	                "content": 'Success',
                	            }
                	        ]
                	    }
                	}
           		}
            )
        r = Response(response=data, status=200,
                     mimetype="application/json")
        r.headers["Content-Type"] = "application/json"
        return r
    else:
        return {'status': True}


if __name__ == '__main__':
    socketio.run(app, '0.0.0.0', 80)
