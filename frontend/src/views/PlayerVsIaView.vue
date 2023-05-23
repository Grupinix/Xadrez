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
  import { ElLoading, ElMessage } from 'element-plus'

  import router from "../router";
  import GameService from "../services/gameService";
  import PlayerService from "../services/playerService";
  import ThemeService from "../services/themeService";

  const theme = ref<string>(ThemeService.getThemeName());
  const path = `./themes/${theme.value}/pieces/`;

  const BISHOP_BLACK = `${path}BISHOP_BLACK.webp`;
  const BISHOP_WHITE = `${path}BISHOP_WHITE.webp`;
  const HORSE_BLACK = `${path}HORSE_BLACK.webp`;
  const HORSE_WHITE = `${path}HORSE_WHITE.webp`;
  const KING_BLACK = `${path}KING_BLACK.webp`;
  const KING_WHITE = `${path}KING_WHITE.webp`;
  const PAWN_BLACK = `${path}PAWN_BLACK.webp`;
  const PAWN_WHITE = `${path}PAWN_WHITE.webp`;
  const QUEEN_BLACK = `${path}QUEEN_BLACK.webp`;
  const QUEEN_WHITE = `${path}QUEEN_WHITE.webp`;
  const TOWER_BLACK = `${path}TOWER_BLACK.webp`;
  const TOWER_WHITE = `${path}TOWER_WHITE.webp`;

  const gameStatus = ref<string>("");
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

  function verifyGameStatus() {
    const gameId = iaGameDto.value.id;
    const gameType = iaGameDto.value.gameType;

    const loading = ElLoading.service({
      lock: true,
      text: "Carregando",
      background: "rgba(0,0,0,0.8)",
    });

    GameService.getGameStatus(gameType, gameId)
      .then((response) => {
        response.json().then((result) => {
          gameStatus.value = result;
          ElMessage({
            message: "STATUS DO JOGO " + gameStatus.value,
            type: "warning",
          });
          loading.close();
        });
      })
      .catch(() => {
        loading.close();
        removeIaGameAndRedirect();
      });
  }

  function movePiece(piece: GameMatrixPiece) {
    const uuid = playerDto.value.uuid;
    const isWhite = uuid === iaGameDto.value.whitePlayerUUID;

    if (isWhite === piece.whitePiece || !piece.move
      || !selectedPiece.value || !selectedPiece.value.id) {
      return;
    }

    const move = piece.move;
    const gameType = iaGameDto.value.gameType;
    const gameId = iaGameDto.value.id;
    const pieceId = selectedPiece.value.id;

    const loading = ElLoading.service({
      lock: true,
      text: "Carregando",
      background: "rgba(0,0,0,0.8)",
    });

    setPieceSelected(0);

    GameService.movePiece(gameType, gameId, uuid, pieceId, move)
      .then((response) => {
        response.json().then((result) => {
          if (result) {
            iaGameDto.value = result;
            localStorage.setItem("iaGameDto", JSON.stringify(result));
            verifyGameStatus();
            clearGameMatrix();
            fillGameMatrix(result.board.whitePieces);
            fillGameMatrix(result.board.blackPieces);
            loading.close();
          }
        });
      })
      .catch(() => {
        loading.close();
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
      movePiece(piece);
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
      }).catch(() => {
        setPieceSelected(0);
        loading.close();
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
            verifyGameStatus();
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
