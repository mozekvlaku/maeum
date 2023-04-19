const axios = require('axios').default;

module.exports = {
    move_servo_batch: function (posted_data) {
            axios.post("http://127.0.0.1:5000/servo_batch", posted_data).catch(function(err) {
                console.log(err)
            });
    },
  move_servo_direct: function (degrees, id, servocontroller) {
    if(degrees < 0 || degrees > 180) return "Out of range";
    if(id < 0 || id > 15) return "Servo not available";
    console.log("Moving: " + degrees + " degrees to servo " + id);
    switch (servocontroller) {
        case "left":
            axios("http://127.0.0.1:5000/servo_set/"+id+"/"+degrees+"/"+servocontroller).catch(function (err) {
                console.log(err)
            });
            return "Moving.";
        case "right": 
            axios("http://127.0.0.1:5000/servo_set/"+id+"/"+degrees+"/"+servocontroller).catch(function (err) {
                console.log(err)
            });
            return "Moving.";

        case "virtual":
            axios("http://127.0.0.1:5000/servo_set/"+id+"/"+degrees+"/"+servocontroller).catch(function (err) {
                console.log(err)
            });
            return "Moving.";

        default: 
            return "No such servocontroller.";
    }
  },
    inc_servo_direct: function (position, id, servocontroller) {
    
    if(id < 0 || id > 15) return "Servo not available";
    console.log("Moving: " + position + " degrees to servo " + id);
    switch (servocontroller) {
        case "left":
            axios("http://127.0.0.1:5000/servo_inc/"+id+"/"+position+"/"+servocontroller).catch(function (err) {
                console.log(err)
            });
            return "Moving.";
        case "right": 
            axios("http://127.0.0.1:5000/servo_inc/"+id+"/"+position+"/"+servocontroller).catch(function (err) {
                console.log(err)
            });
            return "Moving.";

        case "virtual":
            axios("http://127.0.0.1:5000/servo_inc/"+id+"/"+position+"/"+servocontroller).catch(function (err) {
                console.log(err)
            });
            return "Moving.";

        default: 
            return "No such servocontroller.";
    }
  },
  viseme: function (viseme) {
    
    axios("http://127.0.0.1:5000/speak_viseme/"+viseme).catch(function (err) {
        console.log(err)
    });
    return "Moving.";
        
    
  },
  speak: function (what) {
    
    axios("http://192.168.102.147:5000/speak/"+what).catch(function (err) {
        console.log(err)
    });
    return "Moving.";
        
    
  }
};
function map_range(value, low1, high1, low2, high2) {
    return low2 + (high2 - low2) * (value - low1) / (high1 - low1);
}
