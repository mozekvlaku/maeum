const express = require('express');
const app = express();
var cors = require('cors');
const subProcess = require('child_process');
var ServoController = require('./ServoController');
const { Gpio } = require('onoff');
var gpio = require('onoff').Gpio;
var leftdriver = new Gpio(24, 'out');
var rightdriver = new Gpio(23, 'out');

const ServosLeft = {
    TopMouth: 0,
    LeftTopLid: 1, 
    LeftBottomLid: 2,
    LeftMouth: 3,
    LeftTopMouthActuator: 4,
    LeftBottomMouthActuator: 6,
    Neck: 5
}

const ServosRight = {
    BrowRight: 0,
    RightTopLid: 1, 
    RightBottomLid: 2,
    RightMouth: 3,
    EyesY: 4, 
    EyesX: 7,
    RightTopMouthActuator: 5,
    RightBottomMouthActuator: 6
}



say("Startuji ovládací API");
console.log("Nestor Firmware Server is starting...");
app.use(cors())
app.use(express.json());
app.use(express.urlencoded({extended: true}));




app.get('/api/servo_direct/:servocontroller/:servoid/:servodegrees', (req, res) => {
    const servoid = Number(req.params.servoid);
    const servodegrees = Number(req.params.servodegrees);
    const servocontroller = req.params.servocontroller;
    res.send(ServoController.move_servo_direct(servodegrees, servoid, servocontroller));
});
app.get('/api/servo_inc/:servocontroller/:servoid/:servoinc', (req, res) => {
    const servoid = Number(req.params.servoid);
    const servoinc = req.params.servoinc;
    const servocontroller = req.params.servocontroller;
    res.send(ServoController.inc_servo_direct(servoinc, servoid, servocontroller));
});
app.post('/api/servo_batch', async (req, res) => {
    
    res.send(ServoController.move_servo_batch(req.body.data));
    res.sendStatus(200);
});
app.get('/api/speech/:viseme', (req, res) => {
    res.send(ServoController.viseme(req.params.viseme));
});

app.get('/api/speak/:what', (req, res) => {
    res.send(ServoController.speak(req.params.what));
});

app.get('/api/ping', (req, res) => {
    console.log("Pong!");
    res.send("Pong");
});

app.get('/api/power/:side/:state', (req, res) => {
    var state = 0;
    if(req.params.state == "on")
    {
        state = 1;
    }
    if(req.params.side == "left")
    {
        leftdriver.writeSync(state);
    }
    if(req.params.side == "right")
    {
        rightdriver.writeSync(state);
    }
    res.send("OK");
});

app.listen(3000, () => console.log('Listening on port 3000...'));


function say(message) {
    subProcess.exec('espeak -vcs+m1 "'+message+'"', (err, stdout, stderr) => {
        if (err) 
        { 
            console.error(err);
            process.exit(1);
        } 
    });
}
