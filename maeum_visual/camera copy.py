#camera.py
# import the necessary packages
import face_recognition

from fer import FER
import cv2
import os
import json
import imutils
import requests
import base64
import numpy as np
from imageai.Detection import ObjectDetection
ds_factor=1
obj_detect = ObjectDetection()


dir_path = './faces/'
dir_path_bin = './faces_learnt/'
dir_path_tmp = './faces_tmp/'
obj_detect.setModelTypeAsYOLOv3()
obj_detect.setModelPath('./yolov3.pt')
obj_detect.loadModel()
known_images = []



overlay = cv2.imread('./branding.png', cv2.IMREAD_UNCHANGED)
class VideoCamera(object):
    global left_trigger,right_trigger,top_trigger,bottom_trigger,face_locations,face_encodings,face_names,emotions,process_this_frame,process_objects,tmp_frame,faces_in_image,face_in_foreground,last_emotion,unknown_delay,unknown_delay_max 
    def __init__(self):
        self.left_trigger = 350
        self.right_trigger = 950
        self.top_trigger = 60
        self.bottom_trigger = 670
        self.face_locations = []
        self.face_encodings = []
        self.face_names = []
        self.emotions = {}
        self.process_this_frame = True
        self.process_objects = 6
        self.tmp_frame = 0
        self.faces_in_image = []
        self.face_in_foreground = ""
        self.last_emotion = ""
        self.emotion_detector = FER(mtcnn=False)
        self.unknown_delay = 0
        self.known_face_names = []
        self.known_face_encodings = []
        self.unknown_delay_max = 100
        self.video = cv2.VideoCapture('http://vebot:868686@192.168.102.140:8888/?action=stream')
        self.video.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)
        self.video.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)
        for path in os.listdir(dir_path):
            if os.path.isfile(os.path.join(dir_path, path)):
                if path.lower().endswith(('.png', '.jpg', '.jpeg', '.tiff', '.bmp', '.gif')):
                    img = face_recognition.load_image_file(dir_path+path)
                    known_images.append(img)
                    self.known_face_encodings.append(
                        face_recognition.face_encodings(img)[0])
                    self.known_face_names.append(os.path.splitext(path)[0])

        for path in os.listdir(dir_path_bin):
            if os.path.isfile(os.path.join(dir_path_bin, path)):
                if path.lower().endswith(('.maeum_face')):
                    file = open(dir_path_bin+path, "rb")
                    encoding = np.frombuffer(file.read())
                    file.close()
                    self.known_face_encodings.append(encoding)
                    self.known_face_names.append(os.path.splitext(path)[0])
    
    def __del__(self):
        #releasing camera
        self.video.release()

    def overlay_transparent(self,background, overlay, x, y):

        background_width = background.shape[1]
        background_height = background.shape[0]

        if x >= background_width or y >= background_height:
            return background

        h, w = overlay.shape[0], overlay.shape[1]

        if x + w > background_width:
            w = background_width - x
            overlay = overlay[:, :w]

        if y + h > background_height:
            h = background_height - y
            overlay = overlay[:h]

        if overlay.shape[2] < 4:
            overlay = np.concatenate(
                [
                    overlay,
                    np.ones((overlay.shape[0], overlay.shape[1],
                            1), dtype=overlay.dtype) * 255
                ],
                axis=2,
            )

        overlay_image = overlay[..., :3]
        mask = overlay[..., 3:] / 255.0

        background[y:y+h, x:x+w] = (1.0 - mask) * \
            background[y:y+h, x:x+w] + mask * overlay_image

        return background


    def bounding(self,x, y, left, right, top, bottom):
        x_l = int((1280 - left + x - 300) * 0.5) + 580
        y_l = int((720 - top + y - 700) * 0.5) + 40
        return (x_l, y_l)


    def headmove(self,where):
        url = "http://localhost:3000/matter/motoric/headmove"
        params = {
            "where": where
        }
        try:
            r = requests.post(url, params=params)
        except requests.exceptions.RequestException as e:
            print(e)
    
    def get_frame(self):
       #extracting frames
        ret, frame = self.video.read()
        frame = cv2.resize(frame, None, fx=ds_factor, fy=ds_factor,
                           interpolation=cv2.INTER_AREA)
        frame = imutils.rotate(frame, -20)
        if self.process_objects == 6:
            frame, self.preds = obj_detect.detectObjectsFromImage(input_image=frame,
                                                             output_type="array",
                                                             minimum_percentage_probability=10,
                                                             display_percentage_probability=True,
                                                             display_object_name=True)
            # frame = tmp_frame
            batch = []

            for pred in self.preds:
                batch.append({
                    "name": pred["name"],
                    "prob": pred["percentage_probability"]
                })
            url = "http://localhost:3000/matter/visual/maeumobj_batch"
            params = {
                "type": "object",
                "batch": json.dumps(batch)
            }
            try:
                r = requests.post(url, params=params)
                print(r.json())
            except requests.exceptions.RequestException as e:
                print(e)

            self.process_objects = 0
        else:
            # frame = tmp_frame
            self.process_objects = self.process_objects + 1

            # print(preds)
        i = 1
        for pred in self.preds:
            cv2.putText(frame, pred['name'] + " (" + str(pred['percentage_probability']) + "%)",
                        (20, 40+(40 * i)), cv2.   FONT_HERSHEY_SIMPLEX, 0.8, (125, 255, 255), 1)
            i = i + 1
            

        # Only process every other frame of video to save time
        if self.process_this_frame:
            # Resize frame of video to 1/4 size for faster face recognition processing
            small_frame = cv2.resize(frame, (0, 0), fx=0.250, fy=0.250)

            # Convert the image from BGR color (which OpenCV uses) to RGB color (which face_recognition uses)
            rgb_small_frame = small_frame[:, :, ::-1]

            # Find all the faces and face encodings in the current frame of video
            self.face_locations = face_recognition.face_locations(rgb_small_frame)
            self.face_encodings = face_recognition.face_encodings(
                rgb_small_frame, self.face_locations)
            self.face_landmarks_list = face_recognition.face_landmarks(small_frame)
            self.face_names = []
            for face_encoding in self.face_encodings:
                # See if the face is a match for the known face(s)
                matches = face_recognition.compare_faces(
                    self.known_face_encodings, face_encoding)
                name = "Unknown"

                # # If a match was found in known_face_encodings, just use the first one.
                # if True in matches:
                #     first_match_index = matches.index(True)
                #     name = known_face_names[first_match_index]

                # Or instead, use the known face with the smallest distance to the new face
                face_distances = face_recognition.face_distance(
                    self.known_face_encodings, face_encoding)
                best_match_index = np.argmin(face_distances)
                if matches[best_match_index]:
                    name = self.known_face_names[best_match_index]
                    if name not in self.faces_in_image:
                        self.faces_in_image.append(name)
                        url = "http://localhost:3000/matter/visual/maeumobj"
                        params = {
                            "type": "person",
                            "name": name,
                            "id": "best_match_index",
                            "prob": 1
                        }
                        try:
                            r = requests.post(url, params=params)
                            print(r.json())
                        except requests.exceptions.RequestException as e:
                            print(e)

                #if name == "Unknown":
                #    if self.unknown_delay <= self.unknown_delay_max:
#
                #        if self.unknown_delay == self.unknown_delay_max:
                #            cv2.putText(
                #                frame, "Kdo jste?", (20, 670), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
                #        else:
                #            cv2.putText(
                #                frame, "?", (20, 670), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
#
                #        self.unknown_delay = self.unknown_delay+1
                #    else:
                #        filename = input("Kdo to je? ")
                #        cv2.putText(frame, "Kdo jste?", (20, 670),
                #                    cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
                #        face = frame
                #        cv2.imwrite(dir_path_tmp+filename+".jpg", face)
                #        img = face_recognition.load_image_file(
                #            dir_path_tmp+filename+".jpg")
                #        known_images.append(img)
                #        self.known_face_encodings.append(
                #            face_recognition.face_encodings(img)[0])
                #        f = open(dir_path_bin+filename+".maeum_face", "wb")
                #        f.write(face_recognition.face_encodings(
                #            img)[0].tobytes())
                #        f.close()
                #        self.known_face_names.append(filename)
                #else:
                #    unknown_delay = 0

                self.face_names.append(name)

        self.process_this_frame = not self.process_this_frame

        # Print the location of each facial feature in this image

        position = " - (Nevidim lidi)"
        process_emotions = False
        # Display the results
        font = cv2.FONT_HERSHEY_SIMPLEX
        for (top, right, bottom, left), name in zip(self.face_locations, self.face_names):
            # Scale back up face locations since the frame we detected in was scaled to 1/4 size

            top *= 4
            right *= 4
            bottom *= 4
            left *= 4
            process_emotions = True
            for face_landmarks in self.face_landmarks_list:
                for facial_feature in face_landmarks.keys():
                    length = len(face_landmarks[facial_feature])
                    i = 0
                    y = 1
                    for feature in face_landmarks[facial_feature]:
                        if len(face_landmarks[facial_feature]) <= y:
                            y = 0
                        else:
                            start = face_landmarks[facial_feature][i]
                            end = face_landmarks[facial_feature][y]
                            i += 1
                            y += 1
                            result_s = self.bounding(
                                start[0]*4, start[1]*4, left, right, top, bottom)
                            result_e = self.bounding(
                                end[0]*4, end[1]*4, left, right, top, bottom)
                            # print(facial_feature,result)
                            # cv2.circle(frame, result, 3, (0, 0, 255), -1)
                            cv2.line(frame, result_s, result_e,
                                     (255, 255, 255), 1)

            bottom += 80
            top -= 40
            # Draw a box around the face
            cv2.rectangle(frame, (left, top), (right, bottom), (0, 0, 124), 2)

            position = " - " + name + " - (O)"

            if left <= self.left_trigger:
                position = " - " + name + " - (<) posun doprava"
                self.headmove("right")

            if right >= self.right_trigger:
                position = " - " + name + " - (>) posun doleva"
                self.headmove("left")

            if top <= self.top_trigger:
                position = " - " + name + " - (A) posun nahoru"
                self.headmove("up")

            if bottom >= self.bottom_trigger:
                position = " - " + name + " - (V) posun dolu"
                self.headmove("down")

            cv2.rectangle(frame, (self.left_trigger, self.bottom_trigger),
                          (self.right_trigger, self.top_trigger), (128, 128, 128), 1)

            # Draw a label with a name below the face
            cv2.rectangle(frame, (left, bottom - 35),
                          (right, bottom), (0, 0, 124), cv2.FILLED)
            cv2.putText(frame, name, (left + 6, bottom - 6),
                        font, 1.0, (255, 255, 255), 1)

        if process_emotions == True and self.process_objects == 5:
            analysis = self.emotion_detector.detect_emotions(frame)
            if len(analysis) > 0:
                self.emotions = analysis[0]['emotions']

        if position == " - (Nevidim lidi)":
            self.emotions = False

        if self.emotions:
            highest = max(self.emotions, key=self.emotions.get)
            cv2.putText(frame, highest, (1020, 200+(40 * 1)),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 2)
            cv2.putText(frame, "angry: " + str(self.emotions['angry']), (1020, 200+(
                40 * 2)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 1)
            cv2.putText(frame, "disgust: " + str(self.emotions['disgust']), (1020, 200+(
                40 * 3)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125,    255), 1)
            cv2.putText(frame, "fear: " + str(self.emotions['fear']), (1020, 200+(
                40 * 4)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 1)
            cv2.putText(frame, "happy: " + str(self.emotions['happy']), (1020, 200+(
                40 * 5)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 1)
            cv2.putText(frame, "sad: " + str(self.emotions['sad']), (1020, 200+(
                40 * 6)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 1)
            cv2.putText(frame, "surprise: " + str(self.emotions['surprise']), (1020, 200+(
                40 * 7)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125,  255), 1)
            cv2.putText(frame, "neutral: " + str(self.emotions['neutral']), (1020, 200+(
                40 * 8)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125,    255), 1)
            if True: #self.last_emotion != highest:
                self.last_emotion = highest
                url = "http://localhost:3000/matter/visual/maeumemo"
                params = {
                    "id": name,
                    "emotion": highest
                }
                try:
                    r = requests.post(url, params=params)
                    print(r.json())
                except requests.exceptions.RequestException as e:
                    print(e)

        cv2.putText(frame, "Maeum Visual Cortex v1.0.0" + position,
                    (20, 40), font, 1.0, (255, 255, 255), 2)
        # Display the resulting image
        frame = self.overlay_transparent(frame, overlay, 0, 0)
        # encode OpenCV raw frame to jpg and displaying it
        ret, jpeg = cv2.imencode('.jpg', frame)
        data_encode = np.array(jpeg)
        byte_encode = data_encode.tobytes()
        
        # odeslání Base64 řetězce klientovi
        return byte_encode
