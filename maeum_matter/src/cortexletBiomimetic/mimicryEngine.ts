import fs from 'fs';
import path from 'path';
import { EmotionalState } from '../cortexletEmotional/emotionPipeline';
import MotoricManager from '../cortexletMotoric/motoricManager';
class MimicryEngine {
    private expressions: string = __dirname + "/expressions"; 
    private motoricManager: MotoricManager;
    private expressionModel: ExpressionModel;

    constructor(motoricManager: MotoricManager) {
        this.motoricManager = motoricManager;
        this.expressionModel = new ExpressionModel();
        this.load_emotional_expressions();
    }

    do_emotional_expression(expression: EmotionalState): void {
        this.expressionModel.get_expression(expression, this.motoricManager);
    }

    load_emotional_expressions(): void {
        console.log("[MIMICRY] Loading facial expression models");
        fs.readdir(this.expressions, (err, files) => {
            if (err) throw err;
            const jsonFiles = files.filter(file => path.extname(file) === ".json");
            jsonFiles.forEach(file => {
                const filePath = path.join(this.expressions, file);
                const fileContent = fs.readFileSync(filePath, 'utf8');
                const jsonData = JSON.parse(fileContent);
                this.expressionModel.load_expression(path.basename(file), jsonData);
            });
        })
    }
}

class ExpressionModel {
    private angryModel: Array<Object> = [];
    private neutralModel: Array<Object> = [];
    private happyModel: Array<Object> = [];
    private sadModel: Array<Object> = [];
    private curiousModel: Array<Object> = [];
    private fearfulModel: Array<Object> = [];
    private suspiciousModel: Array<Object> = [];
    private disgustedModel: Array<Object> = [];
    private surprisedModel: Array<Object> = [];

    constructor() {}
    load_expression(expressionName: string, expressionData: Object): void {
        console.log("[MIMICRY] Loading " + expressionName)
        switch(true) {
            case /angry/.test(expressionName):
                this.angryModel.push(expressionData);
                break;
            case /creepedout/.test(expressionName):
                this.fearfulModel.push(expressionData);
                break;
            case /neutral/.test(expressionName):
                this.neutralModel.push(expressionData);
                break;
            case /happy/.test(expressionName):
                this.happyModel.push(expressionData);
                break;
            case /sad/.test(expressionName):
                this.sadModel.push(expressionData);
                break;
            case /shock/.test(expressionName):
                this.curiousModel.push(expressionData);
                break;
            case /surprised/.test(expressionName):
                this.surprisedModel.push(expressionData);
                break;
            case /grossout/.test(expressionName):
                this.disgustedModel.push(expressionData);
                break;
            default:
            break;
        }
    }
    get_expression(expression: EmotionalState, motoricManager: MotoricManager) {
        switch (expression) {
            case EmotionalState.Angry:
                motoricManager.send_set(this.get_random_expression(this.angryModel));
                break;
            case EmotionalState.Curious:
                motoricManager.send_set(this.get_random_expression(this.curiousModel));
                break;
            case EmotionalState.Disgusted:
                motoricManager.send_set(this.get_random_expression(this.disgustedModel));
                break;
            case EmotionalState.Fearful:
                motoricManager.send_set(this.get_random_expression(this.fearfulModel));
                break;
            case EmotionalState.Happy:
                motoricManager.send_set(this.get_random_expression(this.happyModel));
                break;
            case EmotionalState.Neutral:
                motoricManager.send_set(this.get_random_expression(this.neutralModel));
                break;
            case EmotionalState.Sad:
                motoricManager.send_set(this.get_random_expression(this.sadModel));
                break;
            case EmotionalState.Surprised:
                motoricManager.send_set(this.get_random_expression(this.surprisedModel));
                break;
            case EmotionalState.Suspicious:
                motoricManager.send_set(this.get_random_expression(this.suspiciousModel));
                break;
            default:
            break;
        }
    }
    get_random_expression(expressionModel: Array<Object>): Object
    {
        return expressionModel[Math.floor(Math.random() * expressionModel.length)];
    }
}
export default MimicryEngine;