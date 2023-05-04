package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.repositories.BoardRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("boardService")
@AllArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final PieceRepository pieceRepository;


    @Override
    public Board createBoard() {
        Board board = new Board();

        boardRepository.save(board);

        List<Piece> whitePieces = createPieces(board, true);
        List<Piece> blackPieces = createPieces(board, false);

        board.setWhitePieces(whitePieces);
        board.setBlackPieces(blackPieces);

        boardRepository.save(board);

        return board;
    }

    private List<Piece> createPieces(Board board, boolean isWhite) {
        PieceType[] pieceOrdering = new PieceType[]{
                PieceType.TOWER, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.KNIGHT, PieceType.BISHOP, PieceType.TOWER
        };

        List<Piece> pieces = new ArrayList<>();

        int pieceYNonPawn = isWhite ? 7 : 0;
        int pieceYPawn = isWhite ? 6 : 1;

        for (boolean isPawn : List.of(false, true)) {
            for (int i = 0; i < 8; i++) {
                PieceType pieceType = isPawn ? PieceType.PAWN : pieceOrdering[i];
                Piece piece = new Piece();
                piece.setPieceType(pieceType);
                piece.setPositionY(isPawn ? pieceYPawn : pieceYNonPawn);
                piece.setPositionX(i);

                if (isWhite) {
                    piece.setWhiteBoard(board);
                }
                else {
                    piece.setBlackBoard(board);
                }

                pieces.add(piece);
            }
        }

        pieceRepository.saveAll(pieces);

        return pieces;
    }

}
