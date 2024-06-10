import http from 'http';
import express from 'express';
import {Server} from "socket.io";

const SERVER_PORT = 4040;
const app = express();
const server = http.createServer(app);

const io = new Server(server);
app.use((req, res, next) => {
    res.setHeader('Access-Control-Allow-Origin', ['http://localhost:3000', 'http://localhost:5173']);
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');
    res.setHeader('Access-Control-Allow-Credentials', 'true');
    next();
});


io.on('connection', (socket) => {
    console.log('a user connected');
    socket.on('disconnect', () => {
        console.log('user disconnected');
    });

    socket.on('report-emergency', (data) => {
        console.log('Emergency reported:', data);
        socket.broadcast.emit('emergency-reported', data);
    });

    socket.on('respond-to-emergency', (data) => {
        console.log('Emergency responded:', data);
        socket.broadcast.emit('emergency-responded', data);
    });
});

server.listen(SERVER_PORT, () => {
    console.log(`Socket server listening on *:${SERVER_PORT}`);
});

