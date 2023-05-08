interface PlayerDto {
  uuid: string;
  gameDifficulty: string;
  identifier: string;
}

interface PieceDto {
  id: number;
  whitePiece: boolean;
  pieceType: string;
  positionX: number;
  positionY: number;
}

interface HistoryDto {
  id: number;
  pieceType: string;
  isWhite: boolean;
  oldPositionX: number;
  oldPositionY: number;
  newPositionX: number;
  newPositionY: number;
  killedPiece: string;
}

interface BoardDto {
  id: number;
  whitePieces: PieceDto[];
  blackPieces: PieceDto[];
  histories: HistoryDto[];
  pieceMatrix: [][][];
}

interface GameDto {
  id: number;
  gameType: string;
  gameDifficulty: string;
  board: BoardDto;
  whiteTurn: boolean;
  whitePlayerUUID: string;
  whiteMoves: number;
  blackPlayerUUID: string;
  blackMoves: number;
  timer: number;
}

interface PositionDto {
  first: number;
  second: number;
}

interface MoveDto {
  first: string;
  second: PositionDto;
}

interface GameMatrixPiece {
  id: number | null;
  move: MoveDto | null;
  whitePiece: boolean | null;
  pieceType: string | null;
  positionX: number;
  positionY: number;
  isPossibleMove: boolean;
  isSelected: boolean;
}

