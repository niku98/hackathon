import pymongo
from bson.objectid import ObjectId
import time
import datetime
import requests
from init import socketio

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
db = myclient["Hackathon"]


def Check_User(User_Id):
    try:
        query = db.User.find({'User_Id': User_Id})[0]
        return True
    except:
        return False


def Create_User(User_Id, Name, Address, Phone, Name_family, Phone_family):
    id = db.User.insert({
        'User_Id': User_Id,
        'Name': Name,
        'Address': Address,
        'Phone': Phone,
        'Name_family': Name_family,
        'Phone_family': Phone_family
    })


def Get_User(User_Id):
    try:
        user = db.User.find({'User_Id': User_Id})[0]
        x = {'Name': user['Name'],
             'Address': user['Address'], 'Phone': user['Phone']}
        return x
    except Exception as e:
        print(e)
        return False


def totimestamp(dt, epoch=datetime.datetime(1970, 1, 1)):
    td = dt - epoch
    # return td.total_seconds()
    return (td.microseconds + (td.seconds + td.days * 86400) * 10**6) / 10**6
def Check_Create_Queue(user_id):
    tomorrow = datetime.datetime(datetime.date.today(
    ).year, datetime.date.today().month, datetime.date.today().day + 1, 0, 0)
    tomorrow = totimestamp(tomorrow)
    now = datetime.datetime(datetime.date.today(
    ).year, datetime.date.today().month, datetime.date.today().day, 0, 0)
    now = totimestamp(now)
    find = db.Queu_Room.find({'id_User': user_id, 'status': 1,'time_create': {'$lt': tomorrow, '$gt': now}})
    if find.count() > 0:
        return False
    else:
        return True

def Detail_queue(queue_id):
    print queue_id
    queue = db.Queu_Room.find({'_id':ObjectId(queue_id)})[0]
    user_id = queue['id_User']
    room_id = queue['id_Room']
    user = db.User.find({'User_Id':user_id})[0]
    room = db.Room.find({'_id':ObjectId(room_id)})[0]
    faculty_id = room['id_Faculty']
    faculty = db.Faculty.find({'_id':ObjectId(faculty_id)})[0]
    doctor_id = room['id_Doctor']
    doctor = db.Doctor.find({'Doctor_id':doctor_id})[0]
    arr = {
        'Faculty': {
            'Name': faculty['Name']
        },
        'Room': {
            'Number': room['Number'],
            'Id': room_id
        },
        'Doctor': {
            'Name': doctor['Name'],
            'Id': doctor_id
        },
        'number_queue': {
            'count': queue['count']
        },
        'User': {
            'user_id': user_id,
            'user_name': user['Name']
        },
    }
    return arr
def Create_queue(sick_id, user_id):
    try:
        sick = db.Sick.find({'_id': ObjectId(sick_id)})[0]
        Faculty_id = sick['id_Faculty']
        try:
            Faculty = db.Faculty.find({'_id': ObjectId(Faculty_id)})[0]
            try:
                n=0
                rooms = db.Room.find({'id_Faculty': Faculty_id})
                min = -1
                room_id = ''
                doctor_id = ''
                room_number = ''
                count = 0
                tomorrow = datetime.datetime(datetime.date.today(
                ).year, datetime.date.today().month, datetime.date.today().day + 1, 0, 0)
                tomorrow = totimestamp(tomorrow)
                now = datetime.datetime(datetime.date.today(
                ).year, datetime.date.today().month, datetime.date.today().day, 0, 0)
                now = totimestamp(now)
                for room in rooms:
                    count = int(db.Queu_Room.find(
                        {'id_Room': str(room['_id']), 'status': 1, 'time_create': {'$lt': tomorrow, '$gt': now}}).count())
                    if min < count:
                        min = count
                        room_id = str(room['_id'])
                        doctor_id = room['id_Doctor']
                        room_number = room['Number']
                list_user = db.Queu_Room.find({'id_Room':room_id,'time_create': {'$lt': tomorrow, '$gt': now}}).sort([('time_create', -1)])
                if list_user.count() == 0:
                    n = 1
                else:
                    n = list_user[0]['count'] + 1
                doctor = db.Doctor.find({'Doctor_id': doctor_id})[0]
                user_name = db.User.find({'User_Id': user_id})[0]['Name']
                queue_id = db.Queu_Room.insert({
                    'id_Room': room_id,
                    'id_User': user_id,
                    'id_Sick': sick_id,
                    'count': n,
                    'status': 1,
                    'time_create': int(time.time())
                })
                arr = {
                    'Sick': {
                        'Name': sick['Name']
                    },
                    'Faculty': {
                        'Name': Faculty['Name']
                    },
                    'Room': {
                        'Number': room_number,
                        'Id': room_id
                    },
                    'Doctor': {
                        'Name': doctor['Name'],
                        'Id': doctor_id
                    },
                    'number_queue': {
                        'count': n
                    },
                    'User': {
                        'user_id': user_id,
                        'user_name': user_name
                    },
                    'id': str(queue_id)
                }
                return arr
            except Exception as e:
                print e
                print '1'
                return False
        except Exception as e:
            print '2'
            print e
            return False
    except Exception as e:
        print '3'
        return False


def Delete_queue(queue_id):
    queue = db.Queu_Room.find({'_id': ObjectId(queue_id)})[0]
    user_id = queue['id_User']
    db.Queu_Room.find_one_and_update({
        '_id': ObjectId(queue_id)
    },
        {
        '$set': {'status': 0}
    })
    return user_id


def Get_Info_Queue(doctor_id):
    try:
        tomorrow = datetime.datetime(datetime.date.today(
        ).year, datetime.date.today().month, datetime.date.today().day + 1, 0, 0)
        tomorrow = totimestamp(tomorrow)
        print tomorrow
        now = datetime.datetime(datetime.date.today(
        ).year, datetime.date.today().month, datetime.date.today().day, 0, 0)
        now = totimestamp(now)
        print now
        id_Room = db.Room.find({'id_Doctor': doctor_id})[0]
        list_queue = db.Queu_Room.find({'id_Room': str(
            id_Room['_id']), 'status': 1, 'time_create': {'$lt': tomorrow, '$gt': now}})
        arr = []
        for queue in list_queue:
            room_number = db.Room.find({'_id': ObjectId(queue['id_Room'])})[
                0]['Number']
            doctor_name = db.Doctor.find({'Doctor_id': doctor_id})[0]['Name']
            faculty_id = id_Room['id_Faculty']
            faculty_name = db.Faculty.find(
                {'_id': ObjectId(faculty_id)})[0]['Name']
            user = db.User.find({'User_Id': queue['id_User']})[0]
            user_name = user['Name']
            user_id = str(user['User_Id'])
            arr.append({
                'Faculty': {
                    'Name': faculty_name
                },
                'Room': {
                    'Number': room_number,
                    'Id': queue['id_Room']
                },
                'Doctor': {
                    'Name': doctor_name,
                    'Id': doctor_id
                },
                'number_queue': {
                    'count': queue['count']
                },
                'User': {
                    'user_id': user_id,
                    'user_name': user_name
                },
                'id': str(queue['_id'])
            })
        return arr
    except Exception as e:
        print e
        return False


def Get_Detail_Doctor(doctor_id):
    try:
        doctor = db.Doctor.find({'Doctor_id': doctor_id})[0]
        doctor_name = doctor['Name']
        id_faculty = doctor['id_Faculty']
        faculty_name = db.Faculty.find(
            {'_id': ObjectId(id_faculty)})[0]['Name']
        room = db.Room.find({'id_Doctor': doctor_id})[0]
        room_number = room['Number']
        return {
            'name': doctor_name,
            'faculty': {
                'id': id_faculty,
                'name': faculty_name
            },
            'room': {
                'id': str(room['_id']),
                'number': room_number
            },
            'id': doctor_id
        }
    except Exception as e:
        print e
        return False


def Get_list_sick():
    try:
        list_sick = db.Sick.find()
        arr = []
        for sick in list_sick:
            arr.append({'id': str(sick['_id']), 'name': sick['Name']})
        return arr
    except:
        return False


def Create_medical_records(description, patient):
    try:
        id = db.Medical_records.insert({
            'Description': description,
            'patient': {
                'Id': patient['id'],
                'Name': patient['name']
            }
        })
        return id
    except:
        return False


def Gen_QR(socket_id):
    r = requests.get('https://api.happi.dev/v1/qrcode?width=150&dots=000000&bg=FFFFFF&apikey=2d0c8auSZHtk2l9ez2ximLmLw2uh30ykgUUn7Eg53stc39iv3J3MjkOE&data=https://qr.id.vin/hook?url=http://203.162.13.40/check_qr?socket_id=' + str(socket_id), verify=False)
    return r.json()['qrcode']


def Check_Doctor(doctor_id):
    try:
        doctor = db.Doctor.find({'Doctor_id': doctor_id})[0]
        return doctor
    except:
        return False


def notifyToPatient(room_id, stt):
    queue = db.Queu_Room.find({
        'id_Room': room_id,
        'status': 1
    }).skip(stt - 1).limit(1)
    try:
        print queue[0]['id_User']
        socketio.emit('notifying', {'stt': stt}, room=queue[0]['id_User'])
    except:
        return False
