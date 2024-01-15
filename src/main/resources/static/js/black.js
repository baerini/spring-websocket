/**
 * 웹소켓 구역
 */
let socket = new WebSocket("ws://localhost:8080/ws/chat");

socket.onopen = function(e) {
    console.log("connection open");
    socket.send(JSON.stringify({
        "gameId" : 1,
        "sender" : "black",
        "type" : "ENTER",
        "message": "Black enter the game"
    }));
};

socket.onmessage = function(event) {
    let msg = JSON.parse(event.data);
    if(msg.type == "MOVE") {
        if(isWhiteTurn) {
            let [pieceId, startingSquareId, destinationSquareId] = msg.message.split("|");
            isWhiteTurn = !isWhiteTurn;
            updateBoardSquares(pieceId, startingSquareId, destinationSquareId, boardSquaresArray);
            checkForCheckMate();
            clearInterval(whiteTimer);
            startTimer();
        } else {
            isWhiteTurn = !isWhiteTurn;
            clearInterval(blackTimer);
            startTimer();
        }
    }
};

socket.onclose = function(event) {
    console.log("connection closed");
};

socket.onerror = function(error) {
};

let boardSquaresArray = [];
let isWhiteTurn = true;
let whiteKingSquare="e1";
let blackKingSquare="e8";
const boardSquares = document.getElementsByClassName("square");
const pieces = document.getElementsByClassName("piece");
const piecesImages = document.getElementsByTagName("img");

function fillBoardSquaresArray() {
    const boardSquares = document.getElementsByClassName("square");
    for (let i = 0; i < boardSquares.length; i++) {
        let row = 1 + Math.floor(i / 8);
        let column = String.fromCharCode(104 - (i % 8));
        let square = boardSquares[i];
        square.id = column + row;

        let color = "";
        let pieceType = "";
        let pieceId="";

        if (square.querySelector(".piece")) {
            color = square.querySelector(".piece").getAttribute("color");
            pieceType = square.querySelector(".piece").classList[1];
            pieceId = square.querySelector(".piece").id;
        } else {
            color = "blank";
            pieceType = "blank";
            pieceId ="blank";
        }
        let arrayElement = {
            squareId: square.id,
            pieceColor: color,
            pieceType: pieceType,
            pieceId:pieceId
        };
        boardSquaresArray.push(arrayElement);
    }
}

function updateBoardSquaresArray(currentSquareId, destinationSquareId, boardSquaresArray) {
    let currentSquare = boardSquaresArray.find((element) => element.squareId === currentSquareId);
    let destinationSquare = boardSquaresArray.find((element) => element.squareId === destinationSquareId);

    let pieceColor = currentSquare.pieceColor;
    let pieceType = currentSquare.pieceType;
    let pieceId= currentSquare.pieceId;

    destinationSquare.pieceColor = pieceColor;
    destinationSquare.pieceType = pieceType;
    destinationSquare.pieceId = pieceId;

    currentSquare.pieceColor = "blank";
    currentSquare.pieceType = "blank";
    currentSquare.pieceId = "blank";
}

function deepCopyArray(array){
    let arrayCopy = array.map(element => {
        return {...element}
    });
    return arrayCopy;
}

setupBoardSquares();
setupPieces();
fillBoardSquaresArray();

function setupBoardSquares() {
    for (let i = 0; i < boardSquares.length; i++) {
        boardSquares[i].addEventListener("dragover", allowDrop);
        boardSquares[i].addEventListener("drop", drop);
        let row = 1 + Math.floor(i / 8);
        let column = String.fromCharCode(104 - (i % 8));
        let square = boardSquares[i];
        square.id = column + row;
    }
}

function setupPieces() {
    for (let i = 0; i < pieces.length; i++) {
        pieces[i].addEventListener("dragstart", drag);
        pieces[i].setAttribute("draggable", true);
        pieces[i].id = pieces[i].className.split(" ")[1] + pieces[i].parentElement.id;
    }

    for (let i = 0; i < piecesImages.length; i++) {
        piecesImages[i].setAttribute("draggable", false);
    }
}

function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    const piece = ev.target;
    const pieceColor = piece.getAttribute("color");
    const pieceType = piece.classList[1];
    const pieceId=piece.id;
//    if ((isWhiteTurn && pieceColor == "white") || (!isWhiteTurn && pieceColor == "black"))
    if (!isWhiteTurn && pieceColor == "black") {
        const startingSquareId = piece.parentNode.id;
        ev.dataTransfer.setData("text", piece.id + "|" + startingSquareId);
        const pieceObject = {
            pieceColor: pieceColor,
            pieceType: pieceType,
            pieceId: pieceId
        }
        let legalSquares = getPossibleMoves(startingSquareId, pieceObject, boardSquaresArray);
        let legalSquaresJson=JSON.stringify(legalSquares);
        ev.dataTransfer.setData("application/json", legalSquaresJson);
    }
}

function drop(ev) {
    ev.preventDefault();
    let data = ev.dataTransfer.getData("text");
    let [pieceId, startingSquareId] = data.split("|");
    let legalSquaresJson = ev.dataTransfer.getData("application/json");
    if (legalSquaresJson.length == 0) return; //움직일 곳 없음
    let legalSquares = JSON.parse(legalSquaresJson);

    const piece = document.getElementById(pieceId);
    const pieceColor = piece.getAttribute("color");
    const pieceType = piece.classList[1];

    const destinationSquare = ev.currentTarget;
    let destinationSquareId = destinationSquare.id;

    legalSquares = isMoveValidAgainstCheck(legalSquares,startingSquareId,pieceColor,pieceType);

    if (pieceType == "king") {
        let isCheck = isKingInCheck(destinationSquareId, pieceColor, boardSquaresArray);
        if (isCheck) {
            return; // 못 가니까 drop 무효화
        } else {
            if (isWhiteTurn) { //왕 위치 조정
                whiteKingSquare = destinationSquareId;
            } else {
                blackKingSquare = destinationSquareId;
            }
        }
    }

    let squareContent = getPieceAtSquare(destinationSquareId, boardSquaresArray);
    if (squareContent.pieceColor == "blank" && legalSquares.includes(destinationSquareId)) {
        //websocket 구역//
        let sender;
        if (isWhiteTurn) {
            sender = "white";
        } else {
            sender = "black"; //추후에 삭제
        }

        socket.send(JSON.stringify({
            "gameId" : 1,
            "sender" : sender,
            "type" : "MOVE",
            "message": pieceId + "|" + startingSquareId + "|" + destinationSquareId // startingSquareId, destinationSquareId
        }));
        //websocket 구역//
        destinationSquare.appendChild(piece);
//        isWhiteTurn = !isWhiteTurn;
        updateBoardSquaresArray(startingSquareId, destinationSquareId, boardSquaresArray);
        checkForCheckMate();
        return;
    }

    if (squareContent.pieceColor != "blank" && legalSquares.includes(destinationSquareId)) {
        let children = destinationSquare.children;
        for (let i = 0; i < children.length; i++) {
            if (!children[i].classList.contains('coordinate')) {
                destinationSquare.removeChild(children[i]);
            }
        }
        //websocket 구역//
        let sender;
        if (isWhiteTurn) {
            sender = "white";
        } else {
            sender = "black"; //추후에 삭제
        }

        socket.send(JSON.stringify({
            "gameId" : 1,
            "sender" : sender,
            "type" : "MOVE",
            "message": pieceId + "|" + startingSquareId + "|" + destinationSquareId // startingSquareId, destinationSquareId
        }));
        //websocket 구역//
        destinationSquare.appendChild(piece);
//        isWhiteTurn = !isWhiteTurn;
        updateBoardSquaresArray(startingSquareId, destinationSquareId, boardSquaresArray);
        checkForCheckMate();
        return;
    }
}

function updateBoardSquares(pieceId, startingSquareId, destinationSquareId, boardSquaresArray) {
    let destinationSquare = document.getElementById(destinationSquareId);
    let piece = document.getElementById(pieceId);
    let children = destinationSquare.children;
    for (let i = 0; i < children.length; i++) {
        if (!children[i].classList.contains('coordinate')) {
            destinationSquare.removeChild(children[i]);
        }
    }
    destinationSquare.appendChild(piece);
    updateBoardSquaresArray(startingSquareId, destinationSquareId, boardSquaresArray);
    checkForCheckMate();
}

////////////////////////////////////////////////////////////////////////////////////////
function getPossibleMoves(startingSquareId, piece, boardSquaresArray) {
    const pieceColor = piece.pieceColor;
    const pieceType=piece.pieceType;
    let legalSquares = [];
    if (pieceType=="rook") {
        legalSquares = getRookMoves(startingSquareId, pieceColor, boardSquaresArray);
        return legalSquares;
    }
    if (pieceType=="bishop") {
        legalSquares = getBishopMoves(startingSquareId, pieceColor, boardSquaresArray);
        return legalSquares;
    }
    if (pieceType=="queen") {
        legalSquares = getQueenMoves(startingSquareId, pieceColor, boardSquaresArray);
        return legalSquares;
    }
    if (pieceType=="knight") {
        legalSquares = getKnightMoves(startingSquareId, pieceColor, boardSquaresArray);
        return legalSquares;
    }

    if (pieceType=="pawn") {
        legalSquares = getPawnMoves(startingSquareId, pieceColor, boardSquaresArray);
        return legalSquares;
    }
    if (pieceType=="king") {
        legalSquares = getKingMoves(startingSquareId, pieceColor, boardSquaresArray);
        return legalSquares;
    }
}

function getPawnMoves(startingSquareId, pieceColor, boardSquaresArray) {
    let diogonalSquares = checkPawnDiagonalCaptures(startingSquareId, pieceColor, boardSquaresArray);
    let forwardSquares = checkPawnForwardMoves(startingSquareId, pieceColor, boardSquaresArray);
    let legalSquares = [...diogonalSquares, ...forwardSquares];
    return legalSquares;
}

function checkPawnDiagonalCaptures(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let legalSquares = [];
    let currentFile = file;
    let currentRank = rankNumber;
    let currentSquareId = currentFile + currentRank;

    const direction = pieceColor == "white" ? 1 : -1;

    currentRank += direction;
    for (let i = -1; i <= 1; i += 2) {
        currentFile = String.fromCharCode(file.charCodeAt(0) + i);
        if (currentFile >= "a" && currentFile <= "h") {
        currentSquareId = currentFile + currentRank;
        let currentSquare = boardSquaresArray.find(
            (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if (squareContent != "blank" && squareContent != pieceColor)
            legalSquares.push(currentSquareId);
        }
    }
    return legalSquares;
}

function checkPawnForwardMoves(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let legalSquares = [];

    let currentFile = file;
    let currentRank = rankNumber;
    let currentSquareId = currentFile + currentRank;

    const direction = pieceColor == "white" ? 1 : -1;
    currentRank += direction;
    currentSquareId = currentFile + currentRank;
    let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
    );
    let squareContent = currentSquare.pieceColor;
    if (squareContent != "blank") return legalSquares;
    legalSquares.push(currentSquareId);
    if (!((rankNumber == 2 && pieceColor == "white" ) || (rankNumber == 7 && pieceColor=="black")) ) return legalSquares;
    currentRank += direction;
    currentSquareId = currentFile + currentRank;
    currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
    );
    squareContent = currentSquare.pieceColor;
    if (squareContent != "blank") return legalSquares;
    legalSquares.push(currentSquareId);
    return legalSquares;
}

function getKnightMoves(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charCodeAt(0) - 97;
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let currentFile = file;
    let currentRank = rankNumber;
    let legalSquares = [];

    const moves = [[-2, 1],[-1, 2],[1, 2],[2, 1],[2, -1],[1, -2],[-1, -2],[-2, -1]];
    moves.forEach((move) => {
        currentFile = file + move[0];
        currentRank = rankNumber + move[1];
        if (currentFile >= 0 && currentFile <= 7 && currentRank > 0 && currentRank <= 8) {
        let currentSquareId = String.fromCharCode(currentFile + 97) + currentRank;
        let currentSquare = boardSquaresArray.find(
            (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if (squareContent != "blank" && squareContent == pieceColor)
            return legalSquares;
        legalSquares.push(String.fromCharCode(currentFile + 97) + currentRank);
        }
    });
    return legalSquares;
}

function getKingMoves(startingSquareId, pieceColor,boardSquaresArray) {
    const file = startingSquareId.charCodeAt(0) - 97; // get the second character of the string
    const rank = startingSquareId.charAt(1); // get the second character of the string
    const rankNumber = parseInt(rank); // convert the second character to a number
    let legalSquares = [];
    const moves = [[0, 1],[0, -1],[1, 1],[1, -1],[-1, 0],[-1, 1],[-1, -1],[1, 0]];

    moves.forEach((move) => {
        let currentFile = file + move[0];
        let currentRank = rankNumber + move[1];

        if (currentFile >= 0 && currentFile <= 7 && currentRank > 0 && currentRank <= 8) {
        let currentSquareId = String.fromCharCode(currentFile + 97) + currentRank;
        let currentSquare=boardSquaresArray.find((element) => element.squareId === currentSquareId);
        let squareContent=currentSquare.pieceColor;
        if (squareContent != "blank" && squareContent == pieceColor) {
            return legalSquares;
        }
        legalSquares.push(String.fromCharCode(currentFile + 97) + currentRank);
        }
    });
    return legalSquares;
}

function getRookMoves(startingSquareId, pieceColor, boardSquaresArray) {
    let moveToEighthRankSquares = moveToEighthRank(startingSquareId, pieceColor, boardSquaresArray);
    let moveToFirstRankSquares = moveToFirstRank(startingSquareId, pieceColor, boardSquaresArray);
    let moveToAFileSquares = moveToAFile(startingSquareId, pieceColor, boardSquaresArray);
    let moveToHFileSquares = moveToHFile(startingSquareId, pieceColor, boardSquaresArray);
    let legalSquares = [...moveToEighthRankSquares, ...moveToFirstRankSquares, ...moveToAFileSquares, ...moveToHFileSquares];
    return legalSquares;
}

function moveToEighthRank(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let currentRank = rankNumber;
    let legalSquares = [];
    while (currentRank != 8) {
        currentRank++;
        let currentSquareId = file + currentRank;
        let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if (squareContent != "blank" && squareContent == pieceColor)
        return legalSquares;
        legalSquares.push(currentSquareId);
        if (squareContent != "blank" && squareContent != pieceColor)
        return legalSquares;
    }
    return legalSquares;
}

function moveToFirstRank(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let currentRank = rankNumber;
    let legalSquares = [];
    while (currentRank != 1) {
        currentRank--;
        let currentSquareId = file + currentRank;
        let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if (squareContent != "blank" && squareContent == pieceColor)
        return legalSquares;
        legalSquares.push(currentSquareId);
        if (squareContent != "blank" && squareContent != pieceColor)
        return legalSquares;
    }
    return legalSquares;
}

function moveToAFile(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    let currentFile = file;
    let legalSquares = [];

    while (currentFile != "a") {
        currentFile = String.fromCharCode(
        currentFile.charCodeAt(currentFile.length - 1) - 1
        );
        let currentSquareId = currentFile + rank;
        let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if (squareContent != "blank" && squareContent == pieceColor)
        return legalSquares;
        legalSquares.push(currentSquareId);
        if (squareContent != "blank" && squareContent != pieceColor)
        return legalSquares;
    }
    return legalSquares;
}

function moveToHFile(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    let currentFile = file;
    let legalSquares = [];
    while (currentFile != "h") {
        currentFile = String.fromCharCode(
        currentFile.charCodeAt(currentFile.length - 1) + 1
        );
        let currentSquareId = currentFile + rank;
        let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if (squareContent != "blank" && squareContent == pieceColor)
        return legalSquares;
        legalSquares.push(currentSquareId);
        if (squareContent != "blank" && squareContent != pieceColor)
        return legalSquares;
    }
    return legalSquares;
}

function getBishopMoves(startingSquareId, pieceColor, boardSquaresArray) {
    let moveToEighthRankHFileSquares = moveToEighthRankHFile(startingSquareId, pieceColor, boardSquaresArray);
    let moveToEighthRankAFileSquares = moveToEighthRankAFile(startingSquareId, pieceColor, boardSquaresArray);
    let moveToFirstRankHFileSquares = moveToFirstRankHFile(startingSquareId, pieceColor, boardSquaresArray);
    let moveToFirstRankAFileSquares = moveToFirstRankAFile(startingSquareId, pieceColor, boardSquaresArray);
    let legalSquares = [...moveToEighthRankHFileSquares, ...moveToEighthRankAFileSquares, ...moveToFirstRankHFileSquares, ...moveToFirstRankAFileSquares];
    return legalSquares;
}

function moveToEighthRankAFile(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let currentFile = file;
    let currentRank = rankNumber;
    let legalSquares = [];
    while(!(currentFile == "a" || currentRank == 8)){
        currentFile = String.fromCharCode(
        currentFile.charCodeAt(currentFile.length-1)-1
        );
        currentRank++;
        let currentSquareId = currentFile + currentRank;
        let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if(squareContent != "blank" && squareContent == pieceColor)
        return legalSquares;
        legalSquares.push(currentSquareId);
        if(squareContent != "blank" && squareContent != pieceColor)
        return legalSquares;
    }
    return legalSquares;
}

function moveToEighthRankHFile(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let currentFile = file;
    let currentRank = rankNumber;
    let legalSquares = [];
    while(!(currentFile == "h" || currentRank == 8)){
        currentFile = String.fromCharCode(
        currentFile.charCodeAt(currentFile.length - 1) + 1
        );
        currentRank++;
        let currentSquareId = currentFile+currentRank;
        let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if(squareContent != "blank" && squareContent == pieceColor)
        return legalSquares;
        legalSquares.push(currentSquareId);
        if(squareContent != "blank" && squareContent != pieceColor)
        return legalSquares;
    }
    return legalSquares;
}
function moveToFirstRankAFile(startingSquareId, pieceColor, boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let currentFile = file;
    let currentRank = rankNumber;
    let legalSquares = [];
    while(!(currentFile == "a" || currentRank == 1)){
        currentFile = String.fromCharCode(
        currentFile.charCodeAt(currentFile.length - 1) - 1
        );
        currentRank--;
        let currentSquareId = currentFile+currentRank;
        let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if(squareContent != "blank" && squareContent == pieceColor)
        return legalSquares;
        legalSquares.push(currentSquareId);
        if(squareContent != "blank" && squareContent != pieceColor)
        return legalSquares;
    }
    return legalSquares;
}
function moveToFirstRankHFile(startingSquareId,pieceColor,boardSquaresArray) {
    const file = startingSquareId.charAt(0);
    const rank = startingSquareId.charAt(1);
    const rankNumber = parseInt(rank);
    let currentFile = file;
    let currentRank = rankNumber;
    let legalSquares = [];
    while(!(currentFile=="h" || currentRank == 1)){
        currentFile = String.fromCharCode(
        currentFile.charCodeAt(currentFile.length - 1) + 1
        );
        currentRank--;
        let currentSquareId = currentFile + currentRank;
        let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === currentSquareId
        );
        let squareContent = currentSquare.pieceColor;
        if(squareContent != "blank" && squareContent == pieceColor)
        return legalSquares;
        legalSquares.push(currentSquareId);
        if(squareContent != "blank" && squareContent != pieceColor)
        return legalSquares;
    }
    return legalSquares;
}

function getQueenMoves(startingSquareId, pieceColor, boardSquaresArray) {
    let bishopMoves = getBishopMoves(startingSquareId, pieceColor, boardSquaresArray);
    let rookMoves = getRookMoves(startingSquareId, pieceColor, boardSquaresArray);
    let legalSquares = [...bishopMoves, ...rookMoves];
    return legalSquares;
}
////////////////////////////////////////////////////////////////////////////////////////

function getPieceAtSquare(squareId, boardSquaresArray) {
    let currentSquare = boardSquaresArray.find(
        (element) => element.squareId === squareId
    );
    const color = currentSquare.pieceColor;
    const pieceType = currentSquare.pieceType;
    const pieceId = currentSquare.pieceId;
    return {
        pieceColor: color,
        pieceType: pieceType,
        pieceId: pieceId
    };
}

function isKingInCheck(squareId, pieceColor, boardSquaresArray) {
    let legalSquares = getRookMoves(squareId, pieceColor, boardSquaresArray);
    for (let squareId of legalSquares) {
        let pieceProperties = getPieceAtSquare(squareId,boardSquaresArray);
        if((pieceProperties.pieceType == "rook" || pieceProperties.pieceType=="queen") && pieceColor!=pieceProperties.pieceColor) return true;
    }

    legalSquares = getBishopMoves(squareId, pieceColor, boardSquaresArray);
    for (let squareId of legalSquares) {
        let pieceProperties = getPieceAtSquare(squareId, boardSquaresArray);
        if((pieceProperties.pieceType == "bishop" || pieceProperties.pieceType == "queen") && pieceColor != pieceProperties.pieceColor) return true;
    }

    legalSquares=checkPawnDiagonalCaptures(squareId, pieceColor, boardSquaresArray);
    for (let squareId of legalSquares) {
        let pieceProperties = getPieceAtSquare(squareId, boardSquaresArray);
        if((pieceProperties.pieceType == "pawn") && pieceColor != pieceProperties.pieceColor) return true;
    }

    legalSquares=getKnightMoves(squareId, pieceColor, boardSquaresArray);
    for (let squareId of legalSquares) {
        let pieceProperties = getPieceAtSquare(squareId, boardSquaresArray);
        if((pieceProperties.pieceType == "knight") && pieceColor != pieceProperties.pieceColor) return true;
    }

    legalSquares=getKingMoves(squareId, pieceColor, boardSquaresArray);
    for (let squareId of legalSquares) {
        let pieceProperties = getPieceAtSquare(squareId, boardSquaresArray);
        if((pieceProperties.pieceType == "king") && pieceColor != pieceProperties.pieceColor) return true;
    }
    return false;
}

function isMoveValidAgainstCheck(legalSquares, startingSquareId, pieceColor, pieceType){
    let kingSquare = isWhiteTurn ? whiteKingSquare : blackKingSquare;
    let boardSquaresArrayCopy = deepCopyArray(boardSquaresArray);
    let legalSquaresCopy = legalSquares.slice();
    legalSquaresCopy.forEach((element)=>{
        let destinationId = element;
        boardSquaresArrayCopy = deepCopyArray(boardSquaresArray);
        updateBoardSquaresArray(startingSquareId,destinationId,boardSquaresArrayCopy);

        if(pieceType!="king" && isKingInCheck(kingSquare, pieceColor, boardSquaresArrayCopy)){
            legalSquares = legalSquares.filter((item) => item!=destinationId);
        }

        if(pieceType=="king" && isKingInCheck(destinationId, pieceColor, boardSquaresArrayCopy)){
            legalSquares = legalSquares.filter((item) => item!=destinationId);
        }
    })
    return legalSquares;
}

function checkForCheckMate() {
    let kingSquare = isWhiteTurn ? whiteKingSquare : blackKingSquare;
    let pieceColor = isWhiteTurn ? "white" : "black";
    let boardSquaresArrayCopy = deepCopyArray(boardSquaresArray);
    let kingIsCheck = isKingInCheck(kingSquare,pieceColor,boardSquaresArrayCopy);

    if(!kingIsCheck) return;

    let possibleMoves = getAllPossibleMoves(boardSquaresArrayCopy,pieceColor);
    console.log(possibleMoves);
    if(possibleMoves.length > 0) return;

    let message="";
    isWhiteTurn ? (message="Black Wins!") : (message="White Wins!");
    showAlert(message);
    /*
    websocket
    */
}

function getAllPossibleMoves(squaresArray,color) {
    return squaresArray.filter((square) => square.pieceColor === color).flatMap((square)=>{
        const {pieceColor,pieceType,pieceId} = getPieceAtSquare(square.squareId,squaresArray);
        if(pieceId==="blank") return [];
        let squaresArrayCopy=deepCopyArray(squaresArray);
        const pieceObject={
            pieceColor: pieceColor,
            pieceType:pieceType,
            pieceId:pieceId
        }
        let legalSquares = getPossibleMoves(square.squareId, pieceObject, squaresArrayCopy);
        legalSquares = isMoveValidAgainstCheck(legalSquares, square.squareId, pieceColor,pieceType);
        return legalSquares;
    })
}
function showAlert(message) {
    const alert= document.getElementById("alert");
    alert.innerHTML=message;
    alert.style.display="block";

    setTimeout(function(){
        alert.style.display="none";
    },3000);
}

//timer//
let whiteTimer, blackTimer;
let whiteTime = 600; //추후에 10, 15분 입력받기
let blackTime = 600;

function updateTimers() {
    document.getElementById('whiteTimer').innerText = formatTime(whiteTime);
    document.getElementById('blackTimer').innerText = formatTime(blackTime);
}

function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
}

function startTimer() {
    if (isWhiteTurn) {
        whiteTimer = setInterval(() => {
            whiteTime--;
            updateTimers();
            if (whiteTime <= 0) {
                clearInterval(whiteTimer);
                /*
                websocket
                */
            }
        }, 1000);
    } else {
        blackTimer = setInterval(() => {
            blackTime--;
            updateTimers();
            if (blackTime <= 0) {
                clearInterval(blackTimer);
                /*
                websocket
                */
            }
        }, 1000);
    }
}

startTimer();
//timer//