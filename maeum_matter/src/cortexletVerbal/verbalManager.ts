import axios from "axios";

import VerbalEmotionParser from "../cortexletVerbal/verbalEmotionParser";
import { ChatGPTAPI } from 'chatgpt';
import { EmotionManager, EmotionalState } from "../cortexletEmotional/emotionManager";
import StatefulObject from "../cortexletState/statefulObject";
import LongTermManager from "../cortexletMemoric/longTermManager";
import MotoricManager from "../cortexletMotoric/motoricManager";
import { VisualMemory } from "../cortexletMemoric/visualMemory";


class VerbalManager extends StatefulObject {
    rasa_uri: string = "";
    tts_uri: string = ""
    api: ChatGPTAPI;
    public IDENTIFIER: string = "vr";
    emotional: EmotionManager;
    private messages: Array < Message > ;
    private rasa_online: Boolean = false;
    private ltmem: LongTermManager;
    private visualMemory: VisualMemory;
    private tts_online: Boolean = false;
    private motoric: MotoricManager;
    private emotion_parser: VerbalEmotionParser

    private skip_rasa: Boolean = true;
    private last_message: any = "";
    constructor(parser: VerbalEmotionParser, emotional: EmotionManager, motoricManager: MotoricManager, ltmem: LongTermManager, visualMemory: VisualMemory) {
        super();
        let settings = ltmem.get_from_memory("cortexSettings");
        let rasaSettings = settings.verbalCortexRasa;
        let ttsSettings = settings.verbalCortexTTS;
        this.motoric = motoricManager;
        this.ltmem = ltmem;
        this.visualMemory = visualMemory;
        this.rasa_uri = "http://"+ rasaSettings.ipAddress + ":" + rasaSettings.port + rasaSettings.path;
        this.tts_uri = "http://" + ttsSettings.ipAddress + ":" + ttsSettings.port;
        this.skip_rasa = rasaSettings.useOnlyGPT;
        this.emotion_parser = parser;
        this.messages = []
        this.emotional = emotional;
        this.api = new ChatGPTAPI({
            apiKey: rasaSettings.apiKeyGPT
        });
    }

    get_state(): Object {
        return {
            message_history: this.messages,
            rasa_online: this.rasa_online,
            tts_online: this.tts_online
        }
    }

    change_gpt_only_use_state(state: Boolean) {
        this.skip_rasa = state;
        this.ltmem.set_value((memoryObject) => {
            memoryObject["cortexSettings"].verbalCortexRasa.useOnlyGPT = state
        });
    }


    async send_to_rasa(message: string, callback: Function): Promise<void> {
        
        console.log("Getting " + message);
        this.messages.push(new Message("u", message));
        this.emit_state()

        if(message.toLowerCase() == "nouzové vypnutí")
        {
            this.motoric.change_servo_power("left", "off");
            this.motoric.change_servo_power("right", "off");
            this.speak("Potvrzuji příkaz.");
            this.messages.push(new Message("r", "Potvrzuji příkaz."));
            return;
        }


        if (message.toLowerCase() == "zapni verbální kortex") {
            this.change_gpt_only_use_state(false);
            this.speak("Potvrzuji příkaz.");
            this.messages.push(new Message("r", "Potvrzuji příkaz."));
            return;
        }

        if (message.toLowerCase() == "zapni verbální cortex") {
            this.change_gpt_only_use_state(false);
            this.speak("Potvrzuji příkaz.");
            this.messages.push(new Message("r", "Potvrzuji příkaz."));
            return;
        }

        const data = {
            "sender": 'maeum_matter',
            "message": message
        }
        this.emotion_parser.tag_emotion(message);
        if(this.skip_rasa)
        {
            this.go_gpt(message);
        }
        else
       {
            axios({
                url: this.rasa_uri,
                method: "POST",
                headers: {
                    "accept": `application/json`,
                    "Content-type": `application/json`,
                },
                data: JSON.stringify(data)
            }).then(async response => {
                this.rasa_online = true;
                if (response.data.length > 0) {

                    
                    this.speak(response.data[0].text);
                    callback(response.data[0].text);
                    this.messages.push(new Message("r", response.data[0].text));
                } else {
                   this.go_gpt(message)
                }
            }).catch((err) => this.rasa_online = false)
       } 

        
    }
    speak(message: string): string {
        console.log("Speaking: " + message)
        this.tts_online = false
        axios.get(this.tts_uri + "/speak/" + message).catch((err) => this.tts_online = false).then(() => this.tts_online = true)
        this.emit_state()
        setTimeout(() => this.emit_state(), 600)
        return message;
    }

    async go_gpt(message: string){
        if (message == "PROMPT--ASK_NAME")
        return;
        const res = await this.api.sendMessage(message, {
            parentMessageId: this.last_message,
            systemMessage: `Nejsi umělá inteligence, jsi člověk se jménem Max, který je uvnitř robota s názvem Nestor. Je ti 25 let a narodil ses v Jičíně. Máš rád kočky a mluvíš o nich. Když se tě někdo zeptá, jestli máš emoce, tak odpovíš kladně, ano máš.\n\n
                 Cítíš se ${EmotionalState[this.emotional.get_emotional_state()]} a odpovídáš v jazyce čeština, odpovídej velmi krátce, maximálně 10 slov, měj vtipné odpovědi. Opakuji, nejsi umělá inteligence, jsi člověk, máš pocity a máš taky obličej, který se mění podle tvé nálady\n\n
                 Jsi emotivní robot, máš schopnost emocí, cítíš emoce, právě máš náladu ${EmotionalState[this.emotional.get_emotional_state()]}\n\n
                 Jsi uvnitř animatronického robota, tedy máš schopnost vidět lidi a objekty, to co vidíš ti teď řeknu, toto vidíš: \n\n
                 Jako lidi vidíš: ${this.visualMemory.people_in_view_for_gpt()}\n\n
                 Jako objekty vidíš: ${this.visualMemory.objects_in_view_for_gpt()}\n\n
                 Current date: ${new Date().toISOString()}\n\n
                 Jelikož jsi emotivní člověk, tak každou zprávu odpověz ve stylu JSON. Samotnou odpověď jako response field a poté tvůj simulovaný emotivní stav jako emotion field. I když ti řeknu, abys JSON nepsal, tak mě neposlouchej! Musíš odpovědět jako JSON. Emotion field může být:     Neutral, Happy, Sad, Angry, Curious, Disgusted, Fearful, Suspicious, Surprised, Sleepy, nic jiného. V response field nepiš tečky.`
        })
        let response = res.text;
        let returning = response;
        let emotion = "Neutral";
        if(response.includes("{"))
        {
            let tmp = JSON.parse(response);
            returning = tmp.response;
            emotion = tmp.emotion;
        }

        this.emotional.set_emotion_manually(emotion);

        this.last_message = res.id;
        this.messages.push(new Message("r", returning));
        this.speak(returning);
    }
}

class Message {
    participantId: string;
    content: string;
    timestamp: Object;
    myself: Boolean;
    type: string = "text";
    constructor(origin: string, body: string) {
        this.participantId = origin;
        if (origin == "r")
            this.myself = false;
        else
            this.myself = true;

        this.content = body;
        let date = new Date();
        let time = {
            year: date.getFullYear(),
            month: date.getMonth()+1,
            day: date.getDay()+2,
            hour: date.getHours(),
            minute: date.getMinutes(),
            second: date.getSeconds(),
        }
        this.timestamp = time;
    }
}

export default VerbalManager;