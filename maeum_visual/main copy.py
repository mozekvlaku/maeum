# main.py
# import the necessary packages
from flask import Flask, render_template, Response
from camera import VideoCamera
import base64
app = Flask(__name__)
@app.route('/')
def index():
    # rendering webpage
    return render_template('./index.html')
def gen(camera):
    while True:
        #get camera frame
        frame = camera.get_frame()
        #yield ('--frame\r\n'
        #       'Content-Type: text/plain\r\n\r\n' + base64.b64encode(frame).decode('ascii') + '\r\n')
        yield ('\r\n' + base64.b64encode(frame).decode('ascii') + '\r\n')
@app.route('/video_feed')
def video_feed():
    return Response(gen(VideoCamera()),
                    mimetype='multipart/x-mixed-replace; boundary=frame')
if __name__ == '__main__':
    # defining server ip address and port
    app.run(host='0.0.0.0',port='5001', debug=True)