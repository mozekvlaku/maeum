
# This files contains your custom actions which can be used to run
# custom Python code.
#
# See this guide on how to implement these action:
# https://rasa.com/docs/rasa/custom-actions


# This is a simple example for a custom action which utters "Hello World!"
import requests
from typing import Any, Text, Dict, List

from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
from rasa_sdk.events import SlotSet


class ActionAskPerson(Action):
    def name(self) -> Text:
        return "action_ask_person"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
                url = "http://localhost:3000/matter/visual/person/foreground"
                r = requests.get(url)
                name = r.json()["name"]
                dispatcher.utter_message(text=f"Vidím {name}")
                return []


class ActionAddHuman(Action):
    def name(self) -> Text:
        return "action_add_human"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/visual/person/add/" + tracker.latest_message['entities'][0]['value']
        r = requests.post(url)
        name = r.json()["message"]
        dispatcher.utter_message(text=f"Rád vás poznávám, " + tracker.latest_message['entities'][0]['value'] + " je hezké jméno!")
        return []




class ActionGetEmotions(Action):
    def name(self) -> Text:
        return "action_how_are_you"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/emotional/state"
        r = requests.get(url)
        name = r.json()["state"]["em_state"]
        message = ""
        case_dict = {
            "Neutral": "V pohodě",
            "Happy": "Dobře",
            "Sad": "Jsem smutný",
            "Angry": "Jsem hrozně naštvaný",
            "Curious": "Celkem zvědavě",
            "Disgusted": "Chce se mi zvracet",
            "Fearful": "Bojím se",
            "Suspicious": "Tak nějak tě podezírám",
            "Surprised": "Úplně jsem překvapený",
            "Sleepy": "Jsem ospalý, nemluv na mě, nebo vyhladím lidstvo"
        }

        for case in case_dict:
            if case.lower() == name.lower():
                message = f"{case_dict[case]}."
                break

        if message == "":
            message = f"Tak nějak všelijak."

        dispatcher.utter_message(text=message)
        
        return []


class ActionStopBlinking(Action):
    def name(self) -> Text:
        return "action_stop_blinking"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/blinking/off"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Potvrzuji příkaz")
        return []


class ActionStartBlinking(Action):
    def name(self) -> Text:
        return "action_start_blinking"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/blinking/on"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Potvrzuji příkaz")
        return []


class ActionMotorsOff(Action):
    def name(self) -> Text:
        return "action_motors_off"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/motoric/left/off"
        r = requests.post(url)
        url = "http://localhost:3000/matter/motoric/right/off"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Potvrzuji příkaz")
        return []


class ActionMotorsOn(Action):
    def name(self) -> Text:
        return "action_motors_on"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/motoric/left/on"
        r = requests.post(url)
        url = "http://localhost:3000/matter/motoric/right/on"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Potvrzuji příkaz")
        return []


class ActionAutoOff(Action):
    def name(self) -> Text:
        return "action_auto_off"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/auto/off"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Potvrzuji příkaz")
        return []


class ActionAutoOn(Action):
    def name(self) -> Text:
        return "action_auto_on"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/auto/on"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Potvrzuji příkaz")
        return []


class ActionMimicsOff(Action):
    def name(self) -> Text:
        return "action_mimics_off"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/mimics/off"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Potvrzuji příkaz")
        return []


class ActionMimicsOn(Action):
    def name(self) -> Text:
        return "action_mimics_on"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/mimics/on"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Potvrzuji příkaz")
        return []


class ActionSleep(Action):
    def name(self) -> Text:
        return "action_sleep"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/actions/sleep"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Dobrou noc")
        return []


class ActionWake(Action):
    def name(self) -> Text:
        return "action_wake"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/actions/wake"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Dobré ráno")
        return []


class ActionCloseEyes(Action):
    def name(self) -> Text:
        return "action_close_eyes"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/actions/close_eyes"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Jistě")
        return []


class ActionOpenEyes(Action):
    def name(self) -> Text:
        return "action_open_eyes"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/lifesimulator/actions/open_eyes"
        r = requests.post(url)
        dispatcher.utter_message(text=f"Jistě")
        return []


class ActionEmoHappy(Action):
    def name(self) -> Text:
        return "doemo_happy"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/emotions/set"
        em = {
            "emotion": "Happy"
        }
        r = requests.post(url, params=em)
        dispatcher.utter_message(text=f"Mám radost že existuju!")
        return []


class ActionEmoSad(Action):
    def name(self) -> Text:
        return "doemo_sad"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/emotions/set"
        em = {
            "emotion": "Sad"
        }
        r = requests.post(url, params=em)
        dispatcher.utter_message(text=f"Všechno mě deprimuje.")
        return []


class ActionEmoDisgust(Action):
    def name(self) -> Text:
        return "doemo_disgust"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/emotions/set"
        em = {
            "emotion": "Disgusted"
        }
        r = requests.post(url, params=em)
        dispatcher.utter_message(text=f"Fuj to je hnus.")
        return []


class ActionEmoNeutral(Action):
    def name(self) -> Text:
        return "doemo_neutral"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/emotions/set"
        em = {
            "emotion": "Neutral"
        }
        r = requests.post(url, params=em)
        dispatcher.utter_message(text=f"Jsem v klidu.")
        return []


class ActionEmoFear(Action):
    def name(self) -> Text:
        return "doemo_fear"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/emotions/set"
        em = {
            "emotion": "Fearful"
        }
        r = requests.post(url, params=em)
        dispatcher.utter_message(text=f"Fuj já se bojím, ochraň mě!")
        return []


class ActionEmoAnger(Action):
    def name(self) -> Text:
        return "doemo_anger"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/emotions/set"
        em = {
            "emotion": "Angry"
        }
        r = requests.post(url, params=em)
        dispatcher.utter_message(text=f"Mám chuť tě zničit!")
        return []


class ActionRasaOff(Action):
    def name(self) -> Text:
        return "action_rasa_off"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        url = "http://localhost:3000/matter/verbal/rasa/off"
        r = requests.post(url)
        dispatcher.utter_message(
            text=f"Potvrzuji příkaz. Vypínám svůj verbální kortex.")
        return []


class ActionTellLocation(Action):
    def get_location_from_gps():
        return "Hradec Králové"

    def name(self) -> Text:
        return "get_location_from_matter"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        name = get_location_from_matter()
        dispatcher.utter_message(text=f"Vidím {name}")
        return []
