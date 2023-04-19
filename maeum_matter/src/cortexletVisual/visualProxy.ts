import express from 'express';
import http from 'http';
import path from 'path';
import MjpegConsumer from 'mjpeg-consumer';
import axios from 'axios';

const app = express();
const httpServer = require('http').createServer(app);
const io = require('socket.io')(httpServer,
    {
   cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});
// Připojení k serveru pomocí Socket.io

class VisualProxy {
    private isStreaming: boolean;


    private latestFrame: any;
    private socket: any;
    private socket_loaded: Boolean = false;

    constructor() {
        const streamUrl = 'http://127.0.0.1:5001/video_feed';
        this.isStreaming = false;

/*


        // Naslouchat příchozím spojením
        io.on('connection', (socket: { on: (arg0: string, arg1: { (data: any): void; (): void; }) => void; broadcast: { emit: (arg0: string, arg1: any) => void; }; }) => {
            console.log('Maeum Synapse Client connected');
            this.socket = socket;
            // Odeslat snímky klientům
            this.socket_loaded = true;
            socket.on('image', () => {
                socket.broadcast.emit('image', this.latestFrame);
                console.log('Emiting');

            });

            // Odpojit klienta
            socket.on('disconnect', () => {
                console.log('Client disconnected');
            });
        });

        // Spustit server
        const PORT = 3001;
        httpServer.listen(PORT, () => {
            console.log(`Server running on port ${PORT}`);
        });
*/


        // Odběr streamu pomocí Axios
        axios({
            method: 'get',
            url: streamUrl,
            responseType: 'stream'
        })
            .then((response) => {
                response.data.on('data', async (chunk: any) => {
                    // Uložit poslední snímek
                    this.latestFrame = chunk;
             
                    this.isStreaming = true;
                    // Odeslat snímek pomocí WebSockets
                    if(this.socket_loaded)
                        this.socket.emit('image', this.latestFrame);

                    

                    await new Promise(resolve => setTimeout(resolve, 500));
                });
            })
            .catch((error) => {
                console.error(error);
                this.isStreaming = false;
            });

/*
/*

        // Create Express app and HTTP server
        this.app = express();
        this.server = http.createServer(this.app);

        // Serve static files from public directory
        this.app.use(express.static(path.join(__dirname, 'public')));

   
        // Set up MJPEG consumer
        this.consumer = new MjpegConsumer();

        this.consumer.on('data', (frame: string | any[]) => {
            console.log('Received frame:', frame.length);
            this.isStreaming = true;
        });

        this.consumer.on('error', (error: any) => {
            console.error('An error occurred:', error);
            this.isStreaming = false;
        });

        // Handle requests for the stream
        this.app.get('/matter/stream', (req, res) => {
            if (this.isStreaming) {
                // If we are currently receiving frames, pipe them to the response
                res.writeHead(200, {
                    "Content-Type": "multipart/x-mixed-replace; boundary=--myboundary",
                    Connection: "keep-alive",
                    Expires: "-1",
                    "Cache-Control": "no-cache, no-store, must-revalidate",
                    Pragma: "no-cache",
                });

                this.consumer.on("frame", (frame: string | any[]) => {
                    res.write(`--myboundary\r\nContent-Type: image/jpeg\r\nContent-Length: ${frame.length}\r\n\r\n`);
                    res.write(frame);
                });

            } else {
                // Otherwise, send a static image
                const staticImage = path.join(__dirname, 'waiting.png');
                res.sendFile(staticImage);
            }
        });


        const startStream = async () => {
            const response = await axios.get(streamUrl, {
                responseType: 'stream',
                headers: {
                    Accept: 'multipart/x-mixed-replace',
                },
            });
            console.log(response.data);
            response.data.pipe(this.consumer);
        };

        console.log('[VISUALPROXY] Loaded stream');
        
        this.server.listen(3001, () => {
            console.log('[VISUALPROXY] Server is listening on port 3001');
            startStream();
        });*/
    }
}

export default VisualProxy;
