import { EmotionManager, EmotionalState, EmotionOrigin } from "../cortexletEmotional/emotionManager";
import { LangCs, StemmerCs } from '@nlpjs/lang-cs';
import { Container } from '@nlpjs/core';
import { SentimentAnalyzer } from '@nlpjs/sentiment';
import fs from 'fs';
import path from 'path';
class VerbalEmotionParser {
    private pipeline: EmotionManager;
    private likes: Array<string> = [];
    private dislikes: Array<string> = [];
    private saddens: Array<string> = [];
    private makescurious: Array<string> = [];
    private disgusts: Array<string> = [];
    private makessuspicious: Array<string> = [];
    private fears: Array<string> = [];

    constructor(pipeline: EmotionManager) {
        this.pipeline = pipeline;
        this.load_data();
    }

    async tag_emotion(message: string) : Promise<void> {
        let sentence: any = await this.parse_sentence_nlp(message);

        let sentence_array: Array<string> = sentence.tokens;
        
        switch (sentence.vote)
        {
            case "neutral":
            break;
            case "negative":
                this.pipeline.push_emotional_state(EmotionalState.Angry, 10, EmotionOrigin.VerbalTone);
                break;
                case "positive":
                this.pipeline.push_emotional_state(EmotionalState.Happy, 10, EmotionOrigin.VerbalTone);
                break;
                default: 
                break;
        }

        sentence_array.forEach((word) => {
            if (this.likes.includes(word))
                this.pipeline.push_emotional_state(EmotionalState.Happy, 50, EmotionOrigin.VerbalWord);

            if (this.dislikes.includes(word))
                this.pipeline.push_emotional_state(EmotionalState.Angry, 50, EmotionOrigin.VerbalWord);

            if (this.saddens.includes(word))
                this.pipeline.push_emotional_state(EmotionalState.Sad, 50, EmotionOrigin.VerbalWord);

            if (this.makescurious.includes(word))
                this.pipeline.push_emotional_state(EmotionalState.Curious, 50, EmotionOrigin.VerbalWord);

            if (this.disgusts.includes(word))
                this.pipeline.push_emotional_state(EmotionalState.Disgusted, 50, EmotionOrigin.VerbalWord);
            
            if (this.makessuspicious.includes(word))
                this.pipeline.push_emotional_state(EmotionalState.Suspicious, 50, EmotionOrigin.VerbalWord);

            if (this.fears.includes(word))
                this.pipeline.push_emotional_state(EmotionalState.Fearful, 50, EmotionOrigin.VerbalWord);
        });
    }

    private async load_data(): Promise<void> {
        let file_path = path.join(__dirname, "../cortexletMemoric/verbalEmotionalRelations.json");
        try {
            const file_content = await fs.promises.readFile(file_path, 'utf-8');
            let data = JSON.parse(file_content);
            this.likes = data.likes;
            this.dislikes = data.dislikes;
            this.saddens = data.saddens;
            this.makescurious = data.makescurious;
            this.disgusts = data.disgusts;
            this.makessuspicious = data.makessuspicious;
            this.fears = data.fears;
        } catch (error) {
            console.error(`Chyba při načítání JSON souboru: ${error}`);
        }
    }

    async parse_sentence_nlp(message:string) : Promise<any> {
       
        const container = new Container();
        container.use(LangCs);
        const sentiment = new SentimentAnalyzer({ container });
        const result = await sentiment.process({ locale: 'cs', text: message });
        const stemmer = new StemmerCs();
        const vote = result.sentiment.vote;
        const tokens = stemmer.tokenizeAndStem(message);

        const res = {
            vote: vote,
            tokens: tokens
        }

        console.log("[VERBAL] Vote of " + vote)

        return res
    }
}
export default VerbalEmotionParser