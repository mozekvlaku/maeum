import face_recognition

from fer import FER
import cv2
import os
import imutils
import requests
import numpy as np
from imageai.Detection import ObjectDetection

video_capture = cv2.VideoCapture(0)
video_capture.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)
video_capture.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)
obj_detect = ObjectDetection()
emotion_detector = FER(mtcnn=False)

known_images = []
known_face_encodings = []
known_face_names = []

left_trigger = 300
right_trigger = 1000
top_trigger = 60
bottom_trigger = 670

def overlay_transparent(background, overlay, x, y):

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
                np.ones((overlay.shape[0], overlay.shape[1], 1), dtype = overlay.dtype) * 255
            ],
            axis = 2,
        )

    overlay_image = overlay[..., :3]
    mask = overlay[..., 3:] / 255.0

    background[y:y+h, x:x+w] = (1.0 - mask) * background[y:y+h, x:x+w] + mask * overlay_image

    return background

def bounding(x, y, left, right, top, bottom):
    x_l = int((1280 - left + x - 300) * 0.5) + 580
    y_l = int((720 - top + y - 700) * 0.5) + 40
    return (x_l, y_l)

def headmove(where):
    print(where)

dir_path = './faces/'
dir_path_bin = './faces_learnt/'
dir_path_tmp = './faces_tmp/'

for path in os.listdir(dir_path):
    if os.path.isfile(os.path.join(dir_path, path)):
        if path.lower().endswith(('.png', '.jpg', '.jpeg', '.tiff', '.bmp', '.gif')):
            img = face_recognition.load_image_file(dir_path+path)
            known_images.append(img)
            known_face_encodings.append(face_recognition.face_encodings(img)[0])
            known_face_names.append(os.path.splitext(path)[0])

for path in os.listdir(dir_path_bin):
    if os.path.isfile(os.path.join(dir_path_bin, path)):
        if path.lower().endswith(('.maeum_face')):
            file = open(dir_path_bin+path, "rb")
            encoding = np.frombuffer(file.read())
            file.close()
            known_face_encodings.append(encoding)
            known_face_names.append(os.path.splitext(path)[0])

# Initialize some variables
face_locations = []
face_encodings = []
face_names = []
emotions = {}
process_this_frame = True
process_objects = 6
tmp_frame = 0

faces_in_image = []
face_in_foreground = ""
last_emotion = ""

print("Facial model loaded")

unknown_delay = 0
unknown_delay_max = 10

obj_detect.setModelTypeAsYOLOv3()
obj_detect.setModelPath('./yolov3.pt')
obj_detect.loadModel()

print("Object model loaded")
print("Maeum Visual Cortex Loaded")

overlay = cv2.imread('./branding.png', cv2.IMREAD_UNCHANGED)
if video_capture.isOpened():
    while (1):
        # Grab a single frame of video
        ret, frame = video_capture.read()
        if process_objects == 6:
            frame, preds = obj_detect.detectObjectsFromImage(input_image=frame,
                          output_type="array",
                          minimum_percentage_probability=10,
                          display_percentage_probability=True,
                          display_object_name=True)
            #frame = tmp_frame
           
            process_objects = 0
        else:
            #frame = tmp_frame
            process_objects = process_objects + 1
    
            #print(preds)
        i = 1
        for pred in preds:
            cv2.putText(frame, pred['name'] + " ("+ str(pred['percentage_probability']) +"%)", (20,40+(40 * i)), cv2.   FONT_HERSHEY_SIMPLEX, 0.8, (125, 255, 255), 1)
            i = i + 1
    
    
        # Only process every other frame of video to save time
        if process_this_frame:
            # Resize frame of video to 1/4 size for faster face recognition processing
            small_frame = cv2.resize(frame, (0, 0), fx=0.250, fy=0.250)
    
    
            # Convert the image from BGR color (which OpenCV uses) to RGB color (which face_recognition uses)
            rgb_small_frame = small_frame[:, :, ::-1]
            
            # Find all the faces and face encodings in the current frame of video
            face_locations = face_recognition.face_locations(rgb_small_frame)
            face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)
            face_landmarks_list = face_recognition.face_landmarks(small_frame)
            face_names = []
            for face_encoding in face_encodings:
                # See if the face is a match for the known face(s)
                matches = face_recognition.compare_faces(known_face_encodings, face_encoding)
                name = "Unknown"
    
                # # If a match was found in known_face_encodings, just use the first one.
                # if True in matches:
                #     first_match_index = matches.index(True)
                #     name = known_face_names[first_match_index]
    
                # Or instead, use the known face with the smallest distance to the new face
                face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)
                best_match_index = np.argmin(face_distances)
                if matches[best_match_index]:
                    name = known_face_names[best_match_index]
                   
    
                if name == "Unknown":
                    if unknown_delay <= unknown_delay_max:
                        
                        if unknown_delay == unknown_delay_max:
                            cv2.putText(frame, "Kdo jste?", (20,670), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
                        else:
                            cv2.putText(frame, "?", (20,670), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
                        
                        unknown_delay = unknown_delay+1
                    else:
                        filename = input("Kdo to je? ")
                        cv2.putText(frame, "Kdo jste?", (20,670), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
                        face = frame
                        cv2.imwrite(dir_path_tmp+filename+".jpg", face)
                        img = face_recognition.load_image_file(dir_path_tmp+filename+".jpg")
                        known_images.append(img)
                        known_face_encodings.append(face_recognition.face_encodings(img)[0])
                        f = open(dir_path_bin+filename+".maeum_face", "wb")
                        f.write(face_recognition.face_encodings(img)[0].tobytes())
                        f.close()
                        known_face_names.append(filename)
                else:
                    unknown_delay = 0
    
                face_names.append(name)
    
            
    
    
        process_this_frame = not process_this_frame
    
        
        
        
            # Print the location of each facial feature in this image
            
        position = " - (Nevidim lidi)"
        process_emotions = False
        # Display the results
        font = cv2.FONT_HERSHEY_SIMPLEX
        for (top, right, bottom, left), name in zip(face_locations, face_names):
            # Scale back up face locations since the frame we detected in was scaled to 1/4 size
            
            
            top *= 4
            right *= 4
            bottom *= 4
            left *= 4
            process_emotions = True
            for face_landmarks in face_landmarks_list:
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
                            result_s = bounding(start[0]*4, start[1]*4, left, right, top, bottom)
                            result_e = bounding(end[0]*4, end[1]*4, left, right, top, bottom)
                            #print(facial_feature,result)
                            #cv2.circle(frame, result, 3, (0, 0, 255), -1)
                            cv2.line(frame, result_s, result_e, (255,255,255), 1)
    
    
            bottom += 80
            top -= 40
            # Draw a box around the face
            cv2.rectangle(frame, (left, top), (right, bottom), (0, 0, 124), 2)
            
            position = " - " + name + " - (O)"
    
            if left <= left_trigger:
                position = " - " + name + " - (<) posun doprava"
                headmove("right")
            
            if right >= right_trigger:
                position = " - " + name + " - (>) posun doleva"
                headmove("left")
            
            if top <= top_trigger:
                position = " - " + name + " - (A) posun nahoru"
                headmove("up")
            
            if bottom >= bottom_trigger:
                position = " - " + name + " - (V) posun dolu"
                headmove("down")
    
            cv2.rectangle(frame, (left_trigger, bottom_trigger), (right_trigger, top_trigger), (128,128,128), 1)
    
            # Draw a label with a name below the face
            cv2.rectangle(frame, (left, bottom - 35), (right, bottom), (0, 0, 124), cv2.FILLED)
            cv2.putText(frame, name, (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)
            
        if process_emotions == True and process_objects == 5:
            analysis = emotion_detector.detect_emotions(frame)
            if len(analysis) > 0:
                emotions = analysis[0]['emotions']
    
        if position == " - (Nevidim lidi)":
            emotions = False
    
        if emotions:
            highest = max(emotions, key=emotions.get)
            cv2.putText(frame, highest, (1020,200+(40 * 1)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 2)
            cv2.putText(frame, "angry: "+ str(emotions['angry']), (1020,200+(40 * 2)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 1)
            cv2.putText(frame, "disgust: "+ str(emotions['disgust']), (1020,200+(40 * 3)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125,    255), 1)
            cv2.putText(frame, "fear: "+ str(emotions['fear']), (1020,200+(40 * 4)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 1)
            cv2.putText(frame, "happy: "+ str(emotions['happy']), (1020,200+(40 * 5)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 1)
            cv2.putText(frame, "sad: "+ str(emotions['sad']), (1020,200+(40 * 6)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125, 255), 1)
            cv2.putText(frame, "surprise: "+ str(emotions['surprise']), (1020,200+(40 * 7)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125,  255), 1)
            cv2.putText(frame, "neutral: "+ str(emotions['neutral']), (1020,200+(40 * 8)), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 125,    255), 1)
           
            
    
        cv2.putText(frame, "Maeum Visual Cortex v1.0.0" + position, (20,40), font, 1.0, (255, 255, 255), 2)
        # Display the resulting image
        frame = overlay_transparent(frame, overlay, 0, 0)
        cv2.imshow('Maeum VisualCortex', frame)
        # Hit 'q' on the keyboard to quit!
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        
    # Release handle to the webcam
    video_capture.release()
    cv2.destroyAllWindows()
    