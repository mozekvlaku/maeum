# main.py
import base64
import threading
from collections import deque
from flask import Flask, render_template, Response
from camera import VideoCamera
import time

app = Flask(__name__)

# Create a deque to store the frames
frame_queue = deque(maxlen=3)

camera = VideoCamera()

# Define a function to read frames from the camera and add them to the queue


def read_frames():
    while True:
        frame = camera.get_frame().tobytes()
        
        if len(frame_queue) == frame_queue.maxlen:
            frame_queue.popleft()
        frame_queue.append(frame)


# Start a new thread to read frames from the camera and add them to the queue
read_thread = threading.Thread(target=read_frames)
read_thread.daemon = True
read_thread.start()

# Define a generator function to stream frames to clients


def gen():
    while True:
        # If there are no frames in the queue, wait for one to arrive
        if len(frame_queue) == 0:
            continue
        # Otherwise, yield the most recent frame
        frame = frame_queue[-1]
        # Encode the frame as JPEG and yield it
        time.sleep(0.1)
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')
        

# Define a route to stream video to clients


@app.route('/video_feed')
def video_feed():
    return Response(gen(),
                    mimetype='multipart/x-mixed-replace; boundary=frame')

# Define a route to serve the index.html template


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/synapse')
def synapse():
    return render_template('mobile.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port='5001', debug=True)
