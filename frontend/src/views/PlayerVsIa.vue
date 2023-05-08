<template>
  <el-row type="flex" justify="center">
    <div class="chess-board">
      <div
        v-for="(line, lineIndex) in gameMatrix"
        :key="line"
        class="chess-row"
      >
        <div
          v-for="(piece, pieceIndex) in line"
          :key="piece"
          class="chess-square"
          :class="{
            'black-square': (lineIndex + pieceIndex) % 2 !== 0,
            'white-square': (lineIndex + pieceIndex) % 2 === 0,
          }"
        >
          <img
            v-if="piece.pieceType"
            :src="getPieceImage(piece)"
            alt="{{getPieceName(piece)}}"
            class="chess-piece pointer"
            :class="{
              'selected-square': piece.isSelected,
              'possible-move-square': piece.isPossibleMove,
            }"
            @click="getPossibleMoves(piece)"
          />
          <div
            v-else
            class="chess-piece"
            :class="{
              'selected-square': piece.isSelected,
              'possible-move-square': piece.isPossibleMove,
            }"
            @click="getPossibleMoves(piece)"
          ></div>
        </div>
      </div>
    </div>
  </el-row>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import { ElLoading } from "element-plus";

import router from "../router";
import GameService from "../services/gameService";
import PlayerService from "../services/playerService";

import BISHOP_BLACK from "../assets/pieces/BISHOP_BLACK.webp";
import BISHOP_WHITE from "../assets/pieces/BISHOP_WHITE.webp";
import HORSE_BLACK from "../assets/pieces/HORSE_BLACK.webp";
import HORSE_WHITE from "../assets/pieces/HORSE_WHITE.webp";
import KING_BLACK from "../assets/pieces/KING_BLACK.webp";
import KING_WHITE from "../assets/pieces/KING_WHITE.webp";
import PAWN_BLACK from "../assets/pieces/PAWN_BLACK.webp";
import PAWN_WHITE from "../assets/pieces/PAWN_WHITE.webp";
import QUEEN_BLACK from "../assets/pieces/QUEEN_BLACK.webp";
import QUEEN_WHITE from "../assets/pieces/QUEEN_WHITE.webp";
import TOWER_BLACK from "../assets/pieces/TOWER_BLACK.webp";
import TOWER_WHITE from "../assets/pieces/TOWER_WHITE.webp";

const timer = ref<number>();
const selectedPiece = ref<GameMatrixPiece>();
const playerDto = ref<PlayerDto>(PlayerService.getPlayerDtoFromStorage());
const iaGameDto = ref<GameDto>(GameService.getIaGameDtoFromStorage());
const gameMatrix = ref<GameMatrixPiece[][]>(buildMatrix());
const pieceImages = ref<{[key: string]: string;}>({
  BISHOP_BLACK: BISHOP_BLACK,
  BISHOP_WHITE: BISHOP_WHITE,
  HORSE_BLACK: HORSE_BLACK,
  HORSE_WHITE: HORSE_WHITE,
  KING_BLACK: KING_BLACK,
  KING_WHITE: KING_WHITE,
  PAWN_BLACK: PAWN_BLACK,
  PAWN_WHITE: PAWN_WHITE,
  QUEEN_BLACK: QUEEN_BLACK,
  QUEEN_WHITE: QUEEN_WHITE,
  TOWER_BLACK: TOWER_BLACK,
  TOWER_WHITE: TOWER_WHITE,
});

onMounted(() => {
  timer.value = window.setInterval(async () => {
    iaGameDto.value = GameService.getIaGameDtoFromStorage();
    if (iaGameDto.value && iaGameDto.value.id) {
      GameService.get(iaGameDto.value.gameType, iaGameDto.value.id)
        .then((response) => {
          response.json().then((result) => {
            if (result) {
              iaGameDto.value = result;
              localStorage.setItem("iaGameDto", JSON.stringify(result));
              fillGameMatrix(iaGameDto.value.board.whitePieces);
              fillGameMatrix(iaGameDto.value.board.blackPieces);
            }
            else {
              removeIaGameAndRedirect();
            }
          });
        });
    }
  }, 1000);
});

onUnmounted(() => {
  clearInterval(timer.value);
});

function buildMatrix(): GameMatrixPiece[][] {
  const matrix = new Array(8);
  for (let i = 0; i < 8; i++) {
    matrix[i] = new Array(8);
    for (let j = 0; j < 8; j++) {
      matrix[i][j] = {
        id: null,
        move: null,
        whitePiece: null,
        pieceType: null,
        positionX: i,
        positionY: j,
        isPossibleMove: false,
        isSelected: false,
      };
    }
  }
  return matrix;
}

function clearGameMatrix() {
  for (let i = 0; i < 8; i++) {
    for (let j = 0; j < 8; j++) {
      gameMatrix.value[i][j].id = null;
      gameMatrix.value[i][j].move = null;
      gameMatrix.value[i][j].whitePiece = null;
      gameMatrix.value[i][j].pieceType = null;
      gameMatrix.value[i][j].positionX = i;
      gameMatrix.value[i][j].positionY = j;
      gameMatrix.value[i][j].isPossibleMove = false;
      gameMatrix.value[i][j].isSelected = false;
    }
  }
}

function fillGameMatrix(pieceArray: PieceDto[]) {
  for (let i = 0; i < pieceArray.length; i++) {
    const piece = pieceArray[i];
    const matrixPiece = gameMatrix.value[piece.positionX][piece.positionY];

    matrixPiece.positionX = piece.positionX;
    matrixPiece.positionY = piece.positionY;
    matrixPiece.id = piece.id;
    matrixPiece.pieceType = piece.pieceType;
    matrixPiece.whitePiece = piece.whitePiece;
  }
}

function getPieceName(piece: PieceDto) {
  const pieceColor = piece.whitePiece ? "_WHITE" : "_BLACK";
  return `${piece.pieceType}${pieceColor}`;
}

function getPieceImage(piece: PieceDto) {
  const pieceName = getPieceName(piece);
  return pieceImages.value[pieceName];
}

function setPieceSelected(pieceId: number) {
  for (let i = 0; i < gameMatrix.value.length; i++) {
    for (let j = 0; j < gameMatrix.value[i].length; j++) {
      const matrixPiece = gameMatrix.value[i][j];
      matrixPiece.isPossibleMove = false;
      matrixPiece.isSelected = matrixPiece.id === pieceId;
    }
  }
}

function setPossiblesMoves(listOfMoves: MoveDto[]) {
  for (let i = 0; i < listOfMoves.length; i++) {
    const pos = listOfMoves[i]["second"];
    const matrixPiece = gameMatrix.value[pos["first"]][pos["second"]];
    matrixPiece.move = listOfMoves[i];
    matrixPiece.isPossibleMove = true;
  }
}

function movePiece(piece: GameMatrixPiece) {
  const uuid = playerDto.value.uuid;
  const isWhite = uuid === iaGameDto.value.whitePlayerUUID;

  if (isWhite === piece.whitePiece || !piece.move || !selectedPiece.value || !selectedPiece.value.id) {
    return;
  }

  const move = piece.move;
  const gameType = iaGameDto.value.gameType;
  const gameId = iaGameDto.value.id;
  const pieceId = selectedPiece.value.id;

  GameService.movePiece(gameType, gameId, uuid, pieceId, move)
    .then((response) => {
      response.json().then((result) => {
        if (result) {
          iaGameDto.value = result;
          localStorage.setItem("iaGameDto", JSON.stringify(result));
          clearGameMatrix();
          fillGameMatrix(result.board.whitePieces);
          fillGameMatrix(result.board.blackPieces);
          setPieceSelected(0);
        }
      });
    })
    .catch(() => {
      setPieceSelected(0);
    });
}

function getPossibleMoves(piece: GameMatrixPiece) {
  const selected = selectedPiece.value;
  if (selected && selected.id === piece.id) {
    selectedPiece.value = {
      id: null,
      move: null,
      whitePiece: null,
      pieceType: null,
      positionX: -1,
      positionY: -1,
      isPossibleMove: false,
      isSelected: false,
    }
    setPieceSelected(0);
    return;
  }
  else if (selected && selected.id && selected.id !== 0) {
    const loading = ElLoading.service({
      lock: true,
      text: "Carregando",
      background: "rgba(0,0,0,0.8)",
    });
    movePiece(piece);
    loading.close();
    return;
  }

  if (!playerDto.value || !iaGameDto.value) {
    router.push({ path: "/" });
    return;
  }

  const uuid = playerDto.value.uuid;
  const isWhitePlayer = uuid === iaGameDto.value.whitePlayerUUID;
  const isPlayerPiece = isWhitePlayer === piece.whitePiece;
  const isPlayerTurn = isWhitePlayer === iaGameDto.value.whiteTurn;

  if (!isPlayerPiece || !isPlayerTurn) {
    return;
  }

  const loading = ElLoading.service({
    lock: true,
    text: "Carregando",
    background: "rgba(0,0,0,0.8)",
  });

  if (piece.id && piece.pieceType && piece.whitePiece !== null) {
    const pieceDto: PieceDto = {
      id: piece.id,
      pieceType: piece.pieceType,
      whitePiece: piece.whitePiece,
      positionX: piece.positionX,
      positionY: piece.positionY,
    }

    GameService.getPiecePossibleMoves(
      iaGameDto.value.gameType,
      iaGameDto.value.id,
      uuid,
      pieceDto
    ).then((response) => {
      response.json().then((result) => {
        if (piece.id !== null) {
          selectedPiece.value = piece;
          setPieceSelected(piece.id);
          setPossiblesMoves(result);
          loading.close();
        }
      });
    });
  }
}

function removeIaGameAndRedirect() {
  localStorage.removeItem("iaGameDto");
  router.push({ path: "/inicio" });
}

const loading = ElLoading.service({
  lock: true,
  text: "Carregando",
  background: "rgba(0, 0, 0, 0.8)",
});

if (!iaGameDto.value || !iaGameDto.value.id || !iaGameDto.value.gameType) {
  loading.close();
  removeIaGameAndRedirect();
}
else {
  const gameType = iaGameDto.value.gameType;
  const gameId = iaGameDto.value.id;
  GameService.check(gameType, gameId)
    .then((response) => {
      response.json().then((result) => {
        if (!result || !iaGameDto.value) {
          loading.close();
          removeIaGameAndRedirect();
        }
        else {
          fillGameMatrix(iaGameDto.value.board.whitePieces);
          fillGameMatrix(iaGameDto.value.board.blackPieces);
          loading.close();
        }
      });
    })
    .catch(() => {
      loading.close();
      removeIaGameAndRedirect();
    });
}
</script>
<style scoped>
.chess-board {
  margin: 32px 0;
  display: flex;
  flex-direction: column;
}
.chess-row {
  display: flex;
}
.chess-square {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
}
.selected-square {
  background-color: rgba(174, 38, 38, 0.75);
}
.possible-move-square {
  background-color: rgba(99, 164, 33, 0.75);
}
.white-square {
  background-color: #ffffff;
}
.black-square {
  background-color: #b58863;
}
.chess-piece {
  color: #1c1c1c;
}
@media screen and (max-width: 767px) {
  .chess-square {
    width: 40px;
    height: 40px;
  }
  .chess-piece {
    width: 25px;
    height: 25px;
  }
}
@media screen and (max-width: 991px) and (min-width: 768px) {
  .chess-square {
    width: 45px;
    height: 45px;
  }
  .chess-piece {
    width: 30px;
    height: 30px;
  }
}
@media screen and (max-width: 1199px) and (min-width: 992px) {
  .chess-square {
    width: 65px;
    height: 50px;
  }
  .chess-piece {
    width: 50px;
    height: 50px;
  }
}
@media screen and (max-width: 1919px) and (min-width: 1200px) {
  .chess-square {
    width: 85px;
    height: 85px;
  }
  .chess-piece {
    width: 60px;
    height: 60px;
  }
}
@media screen and (min-width: 1920px) {
  .chess-square {
    width: 90px;
    height: 90px;
  }
  .chess-piece {
    width: 65px;
    height: 65px;
  }
}
</style>
