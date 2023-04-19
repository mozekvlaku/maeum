// System imports
import Branding                   from './branding';
import CentralDispatcher          from './centralDispatcher';
import BiomimeticCortexletActions from './cortexletBiomimetic/biomimeticCortexletActions';
import BlinkingEngine             from './cortexletBiomimetic/blinkingEngine';
import MimicryEngine              from './cortexletBiomimetic/mimicryEngine';
import { EmotionPipeline }        from './cortexletEmotional/emotionPipeline';
import EmotionalCortexletActions  from './cortexletEmotional/emotionalCortexletActions';
import MemoricCortexletActions    from './cortexletMemoric/memoricCortexletActions';
import { VisualMemory }           from './cortexletMemoric/visualMemory';
import MotoricCortexletActions    from './cortexletMotoric/motoricCortexletActions';
import MotoricManager             from './cortexletMotoric/motoricManager';
import StateCollector             from './cortexletState/stateCollector';
import StateCortexletActions      from './cortexletState/stateCortexletActions';
import VerbalCortexletActions     from './cortexletVerbal/verbalCortexletActions';
import VerbalEmotionParser        from './cortexletVerbal/verbalEmotionParser';
import VerbalManager              from './cortexletVerbal/verbalManager';
import VisualProxy                from './cortexletVisual/visualProxy';
// Thirdparty imports
import express                    from 'express';
import cors                       from 'cors';
import LongTermManager from './cortexletMemoric/longTermManager';

// Thirdparty definitions
const app        = express();
const httpServer = require('http').createServer(app);
const io         = require('socket.io')
(httpServer, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});

app.use(cors())

// Matter Branding definition
const br = new Branding();
console.log("MAEUM Matter is starting...");
console.log(br.get_branding());

// Cortexlet definitions
const stateCollector    = new StateCollector(io, true);
const longTermMemory    = new LongTermManager();
const visualProxy       = new VisualProxy();
const motoricManager    = new MotoricManager(longTermMemory);
const mimicryEngine     = new MimicryEngine(motoricManager);
const emotionPipeline   = new EmotionPipeline(mimicryEngine);
const centralDispatcher = new CentralDispatcher();
const verbalEmotion     = new VerbalEmotionParser(emotionPipeline);
const visualMemory      = new VisualMemory(centralDispatcher, emotionPipeline);
const verbalManager     = new VerbalManager(verbalEmotion, emotionPipeline, longTermMemory);
const blinkingEngine    = new BlinkingEngine(motoricManager, emotionPipeline);

// Cortexlet state observation subscribing
stateCollector.observe_state(emotionPipeline);
stateCollector.observe_state(motoricManager);
stateCollector.observe_state(visualMemory);
stateCollector.observe_state(verbalManager);
stateCollector.observe_state(longTermMemory);
stateCollector.observe_state(blinkingEngine);
stateCollector.observe_state(centralDispatcher);

// Cortexlet actions
const stateActions      = new StateCortexletActions(app, stateCollector);
const motoricActions    = new MotoricCortexletActions(app, motoricManager);
const memoricActions    = new MemoricCortexletActions(app, visualMemory);
const emotionalActions  = new EmotionalCortexletActions(app, emotionPipeline);
const verbalActions     = new VerbalCortexletActions(app, verbalManager, verbalEmotion);
const biomimeticActions = new BiomimeticCortexletActions(app, mimicryEngine, blinkingEngine);

/*longTermMemory.set_value((memoryObject)=>{
    memoryObject["biomimeticSettings"].blinking = false
});*/



app.get('/matter/cdcall', (req: {
    params: {
        ori: any;tgt: any;cmd: any;ret: any;int: any;rsp: any;
    };
}, res: {
    send: (arg0: any) => void;
}) => {
    res.send(centralDispatcher.add(req.params.ori, req.params.tgt, req.params.cmd, req.params.ret, req.params.int, req.params.rsp));
});

app.get('/matter/cdcall/:call', (req: {
    params: {
        call: any;
    };
}, res: {
    send: (arg0: any) => void;
}) => {
    res.send(centralDispatcher.add_from_cdcall(req.params.call));
});

app.get('/', (req: any, res: {
    send: (arg0: string) => void;
}) => {
    res.send("<pre>" + br.get_branding().replace("\n", "<br>") + "</pre>");
});

io.on('connection', (socket: { on: (arg0: string, arg1: { (data: any): void; (): void; }) => void; broadcast: { emit: (arg0: string, arg1: any) => void; }; }) => {
    console.log('Maeum Synapse Client connected');
    
    // Odpojit klienta
    socket.on('disconnect', () => {
        console.log('Client disconnected');
    });
});

const matterSettings = longTermMemory.get_from_memory("matterSettings");
app.listen(matterSettings.restPort, () => console.log('Matter REST Server listening on port ' + matterSettings.restPort +'...'));
httpServer.listen(matterSettings.wsPort, () => console.log('Matter WebSocket Server listening on port ' + matterSettings.wsPort +'...'))
