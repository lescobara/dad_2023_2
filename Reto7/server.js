const express = require('express');
const socketIo = require('socket.io');

const app = express();
var port = 3000;
//seteando el servidor
//const server = http.createServer(app);
const server = app.listen(port)
app.use(express.static('public')); 
console.log(`Servidor escuchando en el puerto ${port}`);

const io = socketIo(server);

const games = {};

const jugadores = [];
/*server.listen(port, () => {
    console.log(`Servidor escuchando en el puerto ${port}`);
});*/

// Función para listar los juegos generados
app.get('/juegos', (req, res) => {
    const gameList = Object.keys(games);
    res.json(gameList);
});

// Función para listar los jugadores conectados a un juego específico
app.get('/juegos/:gameId/jugadores', (req, res) => {
    const gameId = req.params.gameId;
    const game = games[gameId];
    if (game) {
        res.json(game.players);
    } else {
        res.status(404).json({ message: 'Juego no encontrado' });
    }
});

app.get('/juegos/:gameId/tablero', (req, res) => {
    const gameId = req.params.gameId;
    const game = games[gameId];
    if (game) {
        res.json(game.board);
    } else {
        res.status(404).json({ message: 'Juego no encontrado' });
    }
});


// Maneja la lógica del juego y los movimientos de los jugadores
io.on('connection', (socket) => {
    console.log ("Nueva Conexión:"+socket.id);
    socket.on('nuevo-juego', () => {
        // Encuentra un juego disponible con menos de 2 jugadores
        let gameId;

        // Si no hay juegos disponibles, crea uno nuevo
        if (!gameId) {
            gameId = 'game-' + Date.now();
            games[gameId] = { players: [], board: Array(9).fill(null), currentPlayerIndex: 0 };
            socket.emit('juego-nuevo',gameId);
            console.log ("Creando un juego nuevo...");
        }
    });

    socket.on('unirse_juego',(gameId)=>{

        for (const key in games) {
            if (games[key].players.length < 2) {
                gameId = key;
                break;
            }
        }

        if (games[gameId].players.length >= 2) {
            // Ya hay 2 jugadores en el juego, no se permite unirse
            socket.emit('mensaje', 'El juego ya tiene 2 jugadores');
            console.log ("El juego ya tiene 2 jugadores, seleccione otro!")
            return;
        }

        
        if (games[gameId].players.length<2){
            // Únete al juego y notifica al cliente
            socket.join(gameId);
            games[gameId].players.push(socket.id);
            jugadores.push(socket.id);
            // Notifica al cliente que se ha unido al juego
            socket.emit('juego-unido', gameId);
            socket.emit('conexion-id',socket.id);
            console.log ("Jugador unido al juego:"+gameId);
        }

        // Si hay 2 jugadores, comienza el juego
        if (games[gameId].players.length === 2) {
            io.to(gameId).emit('juego-iniciado');
            io.to(gameId).emit('jugador-id',jugadores[0])
            io.to(gameId).emit('otro-jugador-id',jugadores[1])
            console.log("Juego iniciado")
            console.log ("jugador 1:"+jugadores[0])
            console.log("jugador 2:"+jugadores[1])
        }
    });

    socket.on('movimiento', (gameId, player, position) => {
        const game = games[gameId];
    
        if (!game) {
            return; // El juego no existe
        }
    
        // Verificar si es el turno del jugador que envió el movimiento
        const currentPlayer = game.players[game.currentPlayerIndex];
    
        if (currentPlayer !== socket.id) {
            // No es el turno del jugador que envió el movimiento
            socket.emit('mensaje', 'No es tu turno');
            return;
        }
    
        // Verificar si la posición está ocupada
        if (game.board[position] === null) {
            // Actualizar el tablero con el movimiento del jugador
            game.board[position] = player;
            // Enviar el nuevo estado del tablero a todos los jugadores en el juego
            io.to(gameId).emit('actualizar-tablero', game.board)
            console.log(game.board)
    
            // Verificar si el juego ha terminado
            const ganador = verificarGanador(game.board);
            if (ganador) {
                //io.to(gameId).emit('juego-terminado', ganador);
                io.to(gameId).emit('juego-terminado', { resultado: 'Ganador', jugador: ganador });
                console.log("juego terminado ganador:",ganador)
            } else if (tableroLleno(game.board)) {
                //io.to(gameId).emit('juego-terminado', 'Empate');
                io.to(gameId).emit('juego-terminado', { resultado: 'Empate' });
                console.log("Es un empate!!!")
            } else {
                // Cambiar el turno al otro jugador
                game.currentPlayerIndex = 1 - game.currentPlayerIndex;
            }
        } else {
            // La posición ya está ocupada
            socket.emit('mensaje', 'La posición ya está ocupada');
            console.log("posicion ocupada")
        }
        
    });

    socket.on('obtener-juegos-disponibles', () => {
        const juegosDisponibles = obtenerJuegosDisponibles();
        socket.emit('juegos-disponibles', juegosDisponibles);
    });
});

// Verifica si hay un ganador en el juego del triqui
function verificarGanador(tablero) {
    // Define todas las combinaciones de líneas ganadoras
    const lineasGanadoras = [
        [0, 1, 2], [3, 4, 5], [6, 7, 8], // Filas
        [0, 3, 6], [1, 4, 7], [2, 5, 8], // Columnas
        [0, 4, 8], [2, 4, 6]             // Diagonales
    ];

    for (const linea of lineasGanadoras) {
        const [a, b, c] = linea;
        if (tablero[a] && tablero[a] === tablero[b] && tablero[a] === tablero[c]) {
            return tablero[a]; // Retorna el jugador que ganó (X o O)
        }
    }

    return null; // No hay ganador
}

// Verifica si el tablero está lleno y hay un empate
function tableroLleno(tablero) {
    return tablero.every((casilla) => casilla !== null);
}

function obtenerJuegosDisponibles() {
    const juegosDisponibles = Object.keys(games).filter(gameId => games[gameId].players.length < 2);
    return juegosDisponibles;
}
