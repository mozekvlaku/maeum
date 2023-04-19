from adafruit_servokit import ServoKit
from flask import Flask, request, jsonify
import json

app = Flask(__name__)
if __name__ == '__main__':
    app.run(host='127.0.0.1', port=3001)
kit_left = ServoKit(channels=16,address=64)
kit_right = ServoKit(channels=16,address=65)

visemes = {
2: [26,19,0,85,110,98,113],
4: [35,22,62,36,110,157,90],
6: [33,180,0,36,166,180,180],
8: [32,56,122,121,0,38,180],
7: [38,52,122,142,0,180,180],
21: [38,162,49,180,66,180,180],
19: [38,162,49,180,66,67,180],
20: [38,162,49,180,66,180,97],
15: [38,85,0,161,109,180,99],
13: [31,162,49,0,66,180,145],
14: [31,162,49,0,66,84,180],
18: [33,142,69,19,68,164,180],
16: [38,168,116,0,0,180,180],
12: [38,134,47,21,77,180,180],
0: [38,134,47,21,77,180,180]
}

@app.route('/servo_batch', methods = ['POST'])
def servo_batch():
    y = json.loads(request.data)
    for x in y:
        setServoDegrees(x["degrees"], x["servo"], x["side"])
    return "OK"

@app.route('/servo_set_get/<id>/<angle>/<board>')
def servo_set_get(id,angle,board):
    setServoDegrees(angle,id,board)
    return json.dumps(catchPositions(), indent=4)

@app.route('/servo_set/<id>/<angle>/<board>')
def servo_set(id,angle,board):
    setServoDegrees(angle,id,board)
    return "OK"

@app.route('/servo_inc/<id>/<position>/<board>')
def servo_inc(id,position,board):
    incrementServoDegrees(position, id, board)
    return "OK"

@app.route('/servo_get')
def servo_get():
    return json.dumps(catchPositions(), indent=4)

@app.route('/speak_viseme/<id>')
def speak_viseme(id):
    tosend = int(id)
    speak(tosend)
    return "OK"

def speak(id):
    setServoDegrees(visemes[id][0],"0","virtual") # Celist
    setServoDegrees(visemes[id][1],"4","left") # LU
    setServoDegrees(visemes[id][2],"6","left") # LL
    setServoDegrees(visemes[id][3],"5","right") # RU
    setServoDegrees(visemes[id][4],"6","right") # RL
    setServoDegrees(visemes[id][5],"0","right") # UM
    setServoDegrees(visemes[id][6],"15","left") # LM

def catchPositions():
    left_servos = {
        0: "NotScanned",
        1: "NotScanned",
        2: "NotScanned",
        3: "NotScanned",
        4: "NotScanned",
        5: "NotScanned",
        6: "NotScanned",
        7: "NotScanned",
        8: "NotScanned",
        9: "NotScanned",
        10: "NotScanned",
        11: "NotScanned",
        12: "NotScanned",
        13: "NotScanned",
        14: "NotScanned",
        15: "NotScanned"
    }
    right_servos = {
        0: "NotScanned",
        1: "NotScanned",
        2: "NotScanned",
        3: "NotScanned",
        4: "NotScanned",
        5: "NotScanned",
        6: "NotScanned",
        7: "NotScanned",
        8: "NotScanned",
        9: "NotScanned",
        10: "NotScanned",
        11: "NotScanned",
        12: "NotScanned",
        13: "NotScanned",
        14: "NotScanned",
        15: "NotScanned"
    }
    for i in range(15):
        deg = kit_left.servo[i].angle
        if deg <= 180:
            left_servos[i] = deg
       
    for i in range(15):
        deg = kit_right.servo[i].angle
        if deg <= 180:
            right_servos[i] = deg
       
    return {
        "left": left_servos,
        "right": right_servos
    }    

def setServoDegrees(angl,serv,side):
    #print("Moving servo no. " + serv + ", " + side + " board to " + angl)
    if side == 'left':
        kit_left.servo[int(serv)].angle = int(angl)
    if side == 'right':
        kit_right.servo[int(serv)].angle = int(angl)
    if side == 'virtual':
        if serv == '0':
            left_servo_angle = int(angl)
            right_servo_angle = int(map_range(int(angl),0,180,180,0))
            #print("Moving servos 3 on both boards to " + str(left_servo_angle) + ":" + str(right_servo_angle))
            kit_left.servo[3].angle = left_servo_angle
            kit_right.servo[3].angle = right_servo_angle
        if serv == '1':
            left_top_servo_angle = int(map_range(int(angl),0,180,65,8))
            left_bottom_servo_angle = int(map_range(int(angl),0,180,5,64))
            right_top_servo_angle = int(map_range(int(angl),0,180,0,80))
            right_bottom_servo_angle = int(map_range(int(angl),0,180,56,20))
            kit_left.servo[1].angle = left_top_servo_angle
            kit_left.servo[2].angle = left_bottom_servo_angle
            kit_right.servo[1].angle = right_top_servo_angle
            kit_right.servo[2].angle = right_bottom_servo_angle

def map_range(x, in_min, in_max, out_min, out_max):
    return (x - in_min) * (out_max - out_min) // (in_max - in_min) + out_min

def incrementServoDegrees(position,serv,side):
    if side == 'left':
        if position == '+':
            if kit_left.servo[int(serv)].angle <= 165:
                kit_left.servo[int(serv)].angle = kit_left.servo[int(serv)].angle + 5
        if position == '-':
            if kit_left.servo[int(serv)].angle >= 15:
                kit_left.servo[int(serv)].angle = kit_left.servo[int(serv)].angle - 5

    if side == 'right':
        if position == '+':
            if kit_right.servo[int(serv)].angle <= 165:
                kit_right.servo[int(serv)].angle = kit_right.servo[int(serv)].angle + 5
        if position == '-':
            if kit_right.servo[int(serv)].angle >= 15:
                kit_right.servo[int(serv)].angle = kit_right.servo[int(serv)].angle - 5

