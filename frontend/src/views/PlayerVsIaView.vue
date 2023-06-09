<template>
  <el-button
    v-if="gameStatus"
    type="danger"
    @click="removeIaGameAndRedirect"
  >
    Sair
  </el-button>
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
  <div class="move-history">
    <div v-for="(move, index) in moveHistory" :key="index" class="move-item">
      {{ move }}
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, h } from "vue";
  import { ElLoading, ElMessage, ElMessageBox } from "element-plus";

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

  const isOpenMatte = ref<boolean>(false);
  const isOpenCheck = ref<boolean>(false);
  const gameStatus = ref<string>("");
  const timer = ref<number>();
  const whiteTurn = ref<boolean>(true);
  const selectedPiece = ref<GameMatrixPiece>();
  const moveHistory = ref<HistoryDto[]>([]);
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
      if (iaGameDto.value && iaGameDto.value.id && !whiteTurn.value) {
        GameService.get(iaGameDto.value.gameType, iaGameDto.value.id)
          .then((response) => {
            response.json().then((result) => {
              if (result) {
                iaGameDto.value = result;
                if (iaGameDto.value.whiteTurn) {
                  whiteTurn.value = true;
                }
                localStorage.setItem("iaGameDto", JSON.stringify(result));
                clearGameMatrix();
                fillGameMatrix(iaGameDto.value.board.whitePieces);
                fillGameMatrix(iaGameDto.value.board.blackPieces);
                verifyGameStatus(true);
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
    moveHistory.value = [];
    moveHistory.value.push(...iaGameDto.value.board.histories);
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

  function getPieceName(piece: GameMatrixPiece) {
    const pieceColor = piece.whitePiece ? "_WHITE" : "_BLACK";
    return `${piece.pieceType}${pieceColor}`;
  }

  function getPieceImage(piece: GameMatrixPiece) {
    const pieceName = getPieceName(piece);
    return pieceImages.value[pieceName];
  }

  function setPieceSelected(pieceId: number) {
    if (pieceId === 0) {
      selectedPiece.value = {
        id: null,
        move: null,
        whitePiece: null,
        pieceType: null,
        positionX: -1,
        positionY: -1,
        isPossibleMove: false,
        isSelected: false,
      };
    }
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

  function openEndBox() {
    if (isOpenMatte.value) {
      return;
    }

    isOpenMatte.value = true;
    const msg = gameStatus.value === "BLACK_WINS" ?
      "Você perdeu! " : "Você ganhou! ";
    const msgGame = "Continue jogando e se divirta!";

    ElMessageBox({
      title: "FIM DE JOGO!",
      message: h("p", null, [
        h("span", null, msg),
        h("i", { style: 'color: teal' }, msgGame),
      ]),
      confirmButtonText: "OK",
      beforeClose: (action, instance, done) => {
        instance.confirmButtonLoading = true
        instance.confirmButtonText = "Carregando..."
        isOpenMatte.value = false;
        setTimeout(() => {
          localStorage.removeItem("iaGameDto");
          router.push({ path: "/inicio" });
          done();
        }, 1000);
      },
    });
  }

  function showCheckMessage() {
    if (isOpenCheck.value) {
      return;
    }

    isOpenCheck.value = true;
    const msgType = gameStatus.value === "WHITE_CHECK" ? "success" : "warning";
    const msg = gameStatus.value === "WHITE_CHECK" ?
      "O Rei Preto está em cheque!" : "O Rei Branco está em cheque!";

    ElMessage({
      message: msg,
      type: msgType,
    });
    setTimeout(() => isOpenCheck.value = false, 3000);
  }

  function verifyGameStatus(silent: boolean) {
    const gameId = iaGameDto.value.id;
    const gameType = iaGameDto.value.gameType

    let loading: { close: () => void; };
    if (!silent) {
      loading = ElLoading.service({
        lock: true,
        text: "Carregando",
        background: "rgba(0,0,0,0.8)",
      });
    }

    GameService.getGameStatus(gameType, gameId)
      .then((response) => {
        response.json().then((result) => {
          gameStatus.value = result;
          if (result === "WHITE_WINS" || result === "BLACK_WINS") {
            openEndBox();
          }
          else if (result === "WHITE_CHECK" || result === "BLACK_CHECK") {
            showCheckMessage();
          }
          if (!silent) {
            loading.close();
          }
        });
      })
      .catch(() => {
        if (!silent) {
          loading.close();
        }
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
    const pieceType = selectedPiece.value.pieceType;


    const loading = ElLoading.service({
      lock: true,
      text: "Carregando",
      background: "rgba(0,0,0,0.8)",
    });

    setPieceSelected(0);
    if (move.second.first === 0 && pieceType === "PAWN") {
      ElMessageBox.prompt("Escolha a peça para promoção, as válidas são: 'rainha', 'torre', 'cavalo', 'bispo'", "Promoção", {
        showCancelButton: false,
        confirmButtonText: "Confirmar",
        inputPattern: /^(?:\s|^)(rainha|torre|cavalo|bispo)(?=\s|$)$/,
        inputErrorMessage: "Peça inválida",
      }).then(({ value }) => {
        const rightValue: string = {
          rainha: "QUEEN",
          torre: "TOWER",
          cavalo: "HORSE",
          bispo: "BISHOP",
        }[value] || "QUEEN";

        PlayerService.setPawnPromotion(playerDto.value, rightValue).then(() => {
          GameService.movePiece(gameType, gameId, uuid, pieceId, move)
            .then((response) => {
              response.json().then((result) => {
                if (result) {
                  iaGameDto.value = result;
                  localStorage.setItem("iaGameDto", JSON.stringify(result));
                  verifyGameStatus(false);
                  clearGameMatrix();
                  fillGameMatrix(result.board.whitePieces);
                  fillGameMatrix(result.board.blackPieces);
                  loading.close();
                  whiteTurn.value = false;
                }
              });
            })
            .catch(() => {
              loading.close();
            });
        });
      });
    }
    else {
      GameService.movePiece(gameType, gameId, uuid, pieceId, move)
        .then((response) => {
          response.json().then((result) => {
            if (result) {
              iaGameDto.value = result;
              localStorage.setItem("iaGameDto", JSON.stringify(result));
              verifyGameStatus(false);
              clearGameMatrix();
              fillGameMatrix(result.board.whitePieces);
              fillGameMatrix(result.board.blackPieces);
              loading.close();
              whiteTurn.value = false;
            }
          });
        })
        .catch(() => {
          loading.close();
        });
    }
  }

  function getPossibleMoves(piece: GameMatrixPiece) {
    if (!whiteTurn.value) {
      return;
    }

    const selected = selectedPiece.value;
    if (selected && selected.id === piece.id) {
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
            if (!result.length) {
              if (gameStatus.value === "BLACK_CHECK") {
                ElMessage({
                  message: "Você está em cheque, só pode mover peças para salvar o Rei!",
                  type: "error",
                });
                setPieceSelected(0);
              }
            }
            else {
              setPieceSelected(piece.id);
              setPossiblesMoves(result);
            }
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
            verifyGameStatus(false);
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
