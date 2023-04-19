import axios from "axios";

import VerbalEmotionParser from "../cortexletVerbal/verbalEmotionParser";
import { ChatGPTAPI } from 'chatgpt';
import { EmotionPipeline, EmotionalState } from "../cortexletEmotional/emotionPipeline";
import StatefulObject from "../cortexletState/statefulObject";
import LongTermManager from "../cortexletMemoric/longTermManager";


class VerbalManager extends StatefulObject {
    rasa_uri: string = "";
    tts_uri: string = ""
    api: ChatGPTAPI;
    public IDENTIFIER: string = "vr";
    emotional: EmotionPipeline;
    private messages: Array < Message > ;
    private rasa_online: Boolean = false;
    private tts_online: Boolean = false;
    private emotion_parser: VerbalEmotionParser

    private skip_rasa: boolean = true;
    private last_message: any = "";
    constructor(parser: VerbalEmotionParser, emotional: EmotionPipeline, ltmem: LongTermManager) {
        super();
        let settings = ltmem.get_from_memory("cortexSettings");
        let rasaSettings = settings.verbalCortexRasa;
        let ttsSettings = settings.verbalCortexTTS;
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

    async send_to_rasa(message: string, callback: Function): Promise<void> {
        
        console.log("Getting " + message);
        this.messages.push(new Message("u", message));
        const data = {
            "sender": 'maeum_matter',
            "message": message
        }
        this.emotion_parser.tag_emotion(message);
        if(this.skip_rasa)
        {
            const res = await this.api.sendMessage(message, {
                parentMessageId: this.last_message,
                systemMessage: `Nejsi umělá inteligence, jsi člověk se jménem Max, který je uvnitř robota s názvem Nestor. Je ti 25 let a narodil ses v Jičíně. Máš rád kočky a mluvíš o nich.\n\n
                 Cítíš se ${EmotionalState[this.emotional.get_emotional_state()]} a odpovídáš v jazyce čeština, odpovídej velmi krátce, maximálně 10 slov, měj vtipné.\n\n
                 Jsi emotivní robot, máš schopnost emocí, cítíš emoce, právě cítíš emoci ${EmotionalState[this.emotional.get_emotional_state()]}\n\n
                 Jsi uvnitř animatronického robota, tedy máš schopnost vidět lidi a objekty, toto vidíš: \n\n
                 Před tebou stojí osoba se jménem Tomáš Kracík, který s tebou mluví\n\n
                 V pozadí se nachází osoba se jménem Matouš Kracík (je smutný)\n\n
                 Vidíš: person, potted plant, laptop\n\n
                 Current date: ${new Date().toISOString()}\n\n`
            })
            this.last_message = res.id;
            this.messages.push(new Message("r", res.text));
            this.speak(res.text);
            callback(res.text);
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
                    const res = await this.api.sendMessage(message, {
                        systemMessage: `Jsi umělá inteligence Maeum, která ovládá emotivního robota s názvem Nestor, nebo Max. Odpovídej s emocí ${EmotionalState[this.emotional.get_emotional_state()]} v jazyce čeština, odpovídej velmi krátce, maximálně 10 slov.
Current date: ${new Date().toISOString()}\n\n`
                    })
                    this.messages.push(new Message("r", res.text));
                    this.speak(res.text);
                    callback(res.text);
                }
            }).catch((err) => this.rasa_online = false)
       } 

        this.emit_state()
    }
    speak(message: string): string {
        console.log("Speaking: " + message)
        axios.get(this.tts_uri + "/speak/" + message).catch((err) => this.tts_online = false).then(() => this.tts_online = true)
        return message;
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