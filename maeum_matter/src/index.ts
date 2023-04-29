// System imports
import Branding                   from './branding';
import CentralDispatcher          from './centralDispatcher';
import BiomimeticCortexletActions from './cortexletBiomimetic/biomimeticCortexletActions';
import BlinkingEngine             from './cortexletBiomimetic/blinkingEngine';
import MimicryEngine              from './cortexletBiomimetic/mimicryEngine';
import { EmotionManager }        from './cortexletEmotional/emotionManager';
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
import LifeEngine from './cortexletBiomimetic/lifeEngine';

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
const mimicryEngine     = new MimicryEngine(motoricManager, longTermMemory);
const emotionManager    = new EmotionManager(mimicryEngine);
const centralDispatcher = new CentralDispatcher();
const verbalEmotion     = new VerbalEmotionParser(emotionManager);
const visualMemory      = new VisualMemory(centralDispatcher, emotionManager);
const verbalManager     = new VerbalManager(verbalEmotion, emotionManager, motoricManager, longTermMemory, visualMemory);
const blinkingEngine    = new BlinkingEngine(motoricManager, emotionManager, longTermMemory);
const lifeEngine        = new LifeEngine(emotionManager,visualMemory, longTermMemory, motoricManager);

// Cortexlet state observation subscribing
stateCollector.observe_state(emotionManager);
stateCollector.observe_state(motoricManager);
stateCollector.observe_state(visualMemory);
stateCollector.observe_state(verbalManager);
stateCollector.observe_state(longTermMemory);
stateCollector.observe_state(blinkingEngine);
stateCollector.observe_state(centralDispatcher);
stateCollector.observe_state(visualProxy);
stateCollector.observe_state(lifeEngine);
stateCollector.observe_state(mimicryEngine);

// Cortexlet actions
const stateActions      = new StateCortexletActions(app, stateCollector);
const motoricActions    = new MotoricCortexletActions(app, motoricManager);
const memoricActions    = new MemoricCortexletActions(app, visualMemory);
const emotionalActions  = new EmotionalCortexletActions(app, emotionManager);
const verbalActions     = new VerbalCortexletActions(app, verbalManager, verbalEmotion);
const biomimeticActions = new BiomimeticCortexletActions(app, mimicryEngine, blinkingEngine, lifeEngine);

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

io.on('connection', (socket: {
    id: string; on: (arg0: string, arg1: { (data: any): void; (): void; }) => void; broadcast: { emit: (arg0: string, arg1: any) => void; }; 
}) => {
    console.log('Maeum Synapse Client connected ' + socket.id);
    process.stdout.write('\x07');
    var sl = io;
    setTimeout(()=>sl.emit("state", stateCollector.get_states()), 1600);
    
    // Odpojit klienta
    socket.on('disconnect', () => {
        console.log('Client disconnected');
        process.stdout.write('\x07'); process.stdout.write('\x07');
    });
});

const matterSettings = longTermMemory.get_from_memory("matterSettings");
app.listen(matterSettings.restPort, () => console.log('Matter REST Server listening on port ' + matterSettings.restPort +'...'));
httpServer.listen(matterSettings.wsPort, () => console.log('Matter WebSocket Server listening on port ' + matterSettings.wsPort +'...'))

