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
            class="chess-piece"
          />
        </div>
      </div>
    </div>
  </el-row>
</template>

<script>
export default {
  name: "PlayerVsIa",

  data() {
    return {
      isValid: true,
      playerDto: {},
      iaGameDto: {},
      gameMatrix: [
        [{}, {}, {}, {}, {}, {}, {}, {}],
        [{}, {}, {}, {}, {}, {}, {}, {}],
        [{}, {}, {}, {}, {}, {}, {}, {}],
        [{}, {}, {}, {}, {}, {}, {}, {}],
        [{}, {}, {}, {}, {}, {}, {}, {}],
        [{}, {}, {}, {}, {}, {}, {}, {}],
        [{}, {}, {}, {}, {}, {}, {}, {}],
        [{}, {}, {}, {}, {}, {}, {}, {}],
      ],
    };
  },

  async created() {
    this.iaGameDto = JSON.parse(localStorage.getItem("iaGameDto"));
    if (!this.iaGameDto) {
      this.$router.push({ path: "/inicio" });
    }

    let url = import.meta.env.VUE_APP_ROOT_API;
    if (!!url) {
      url = "http://localhost:8000/";
    }
    const self = this;
    const headers = { "Content-Type": "application/json" };
    const gameType = this.iaGameDto.gameType;
    const gameId = this.iaGameDto.id;

    await fetch(`${url}api/game/check/${gameType}/${gameId}/`, {
      method: "GET",
      headers: headers,
    })
      .then(async (response) => {
        const result = await response.json();
        if (!result) {
          self.isValid = false;
        }
      })
      .catch(() => {
        self.isValid = false;
      });

    if (!this.isValid) {
      localStorage.removeItem("iaGameDto");
      this.$router.push({ path: "/inicio" });
    }

    this.fillGameMatrix(this.iaGameDto.board.whitePieces);
    this.fillGameMatrix(this.iaGameDto.board.blackPieces);

    this.playerDto = JSON.parse(localStorage.getItem("playerDto"));
  },

  methods: {
    fillGameMatrix(pieceArray) {
      for (let i = 0; i < pieceArray.length; i++) {
        let piece = pieceArray[i];

        const matrixPiece = this.gameMatrix[piece.positionY][piece.positionX];

        matrixPiece.positionX = piece.positionX;
        matrixPiece.positionY = piece.positionY;
        matrixPiece.id = piece.id;
        matrixPiece.pieceType = piece.pieceType;
        matrixPiece.whitePiece = piece.whitePiece;
      }
    },

    getPieceName(piece) {
      const pieceColor = piece.whitePiece ? "_WHITE" : "_BLACK";
      return `${piece.pieceType}${pieceColor}`;
    },

    getPieceImage(piece) {
      const pieceName = this.getPieceName(piece);
      const url = `/src/assets/pieces/${pieceName}.webp`;
      return new URL(url, import.meta.url).href;
    },
  },
};
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
