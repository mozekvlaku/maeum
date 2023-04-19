# Python program to translate
# speech to text and text to speech


import speech_recognition as sr
import pyttsx3
import requests

# Initialize the recognizer
r = sr.Recognizer()



# Loop infinitely for user to
# speak

while (1):

	# Exception handling to handle
	# exceptions at the runtime
	try:

		# use the microphone as source for input.
		with sr.Microphone() as source2:
			print("Adjusting...")
			# wait for a second to let the recognizer
			# adjust the energy threshold based on
			# the surrounding noise level
			r.adjust_for_ambient_noise(source2, duration=0.1)
			print("Listening...")
			# listens for the user's input
			audio2 = r.listen(source2)

			# Using google to recognize audio
			MyText = r.recognize_google(audio2, language="cs-CZ")
			MyText = MyText.lower()

			print(MyText)

			url = "http://localhost:3000/matter/verbal/input"
			params = {
                "text": MyText,
                "origin": "ears"
            }
			requests.post(url, params = params)

	except sr.RequestError as e:
		print("Could not request results; {0}".format(e))

	except sr.UnknownValueError:
		print("unknown error occurred")
