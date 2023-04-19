
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
            "Surprised": "Úplně jsem překvapený"
        }

        for case in case_dict:
            if case.lower() == name.lower():
                message = f"{case_dict[case]}."
                break

        if message == "":
            message = f"Omlouvám se, nebyl jsem schopen identifikovat vaši emoci."

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
