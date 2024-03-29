package swexpertacademy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * https://www.swexpertacademy.com/main/code/problem/problemDetail.do?contestProbId=AWXRQm6qfL0DFAUo
 * <p>
 * 5656. [모의 SW 역량테스트] 벽돌 깨기
 */

public class P5656_BreakBrick {
    private static int T, N, W, H;

    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            T = Integer.parseInt(br.readLine());

            // Loop T times
            for (int i = 0; i < T; i++) {
                StringTokenizer st = new StringTokenizer(br.readLine());

                N = Integer.parseInt(st.nextToken());
                W = Integer.parseInt(st.nextToken());
                H = Integer.parseInt(st.nextToken());

                int[][] board = new int[H][W];

                for (int j = 0; j < H; j++) {
                    String s = br.readLine();
                    st = new StringTokenizer(s);
                    for (int k = 0; k < W; k++) {
                        board[j][k] = Integer.parseInt(st.nextToken());
                    }
                }

                // Print result
                System.out.printf("#%d %d\n", (i + 1), solution(board, N, -1));
            }
        }
    }

    static int beforeDropRemain;

    // 매 회 각 줄마다 공을 떨어뜨리는 DFS메서드
    private static int solution(int[][] board, int N, int x) {
        int remain = getRemainCount(board);

        if (N == 0 || remain == 0 || remain == beforeDropRemain) {
            return remain;
        } else {
            int min = Integer.MAX_VALUE;

            for (int i = 0; i < W; i++) {
                // 현재 board를 복사해두고 여러번 사용
                int[][] boardCopy = deepCopy(board);

                // 최적화를 위한 beforeDropRemain 변경
                beforeDropRemain = remain;

                // 공을 떨어뜨리고 난 다음 보드상황
                int[][] afterDropBall = dropBall(boardCopy, i);

                // 해당 보드에 다시 공을 떨어뜨림
                int tempRemain = solution(afterDropBall, N - 1, x);

                if (tempRemain < min) {
                    min = tempRemain;
                }
            }
            return min;
        }
    }

    // 공을 떨어뜨리는 메서드
    private static int[][] dropBall(int[][] board, int x) {
        // 터질 벽돌들을 추가하고 터뜨리기위해 Queue를 사용
        Queue<Bomb> bombQueue = new LinkedList<>();

        // 해당 라인 가장 위쪽 벽돌을 찾아 큐에 넣음
        for (int i = 0; i < H; i++) {
            int value = board[i][x];
            if (value != 0) {
                bombQueue.add(new Bomb(x, i, value));
                board[i][x] = 0;
                break;
            }
        }

        // 큐가 빌 때까지 반복하여 벽돌을 찾고 터뜨림
        while (!bombQueue.isEmpty()) {
            // 이번에 터뜨릴 벽돌을 찾음
            Bomb thisBomb = bombQueue.remove();
            // 찾은 벽돌을 터뜨려 새롭게 터질 벽돌을 큐에 넣음
            board = explodeBomb(board, thisBomb, bombQueue);
        }

        // 전부 터뜨리고난 후 벽돌들을 내린다.
        return down(board);
    }

    // 벽돌이 터질 때 작동하는 메서드
    private static int[][] explodeBomb(int[][] board, Bomb bomb, Queue<Bomb> bombQueue) {
        int bombX = bomb.x;
        int bombY = bomb.y;
        int bombValue = bomb.value;

        int bombLeftRange = Math.max(0, bombX - bombValue + 1);
        int bombRightRange = Math.min(W - 1, bombX + bombValue - 1);
        int bombUpRange = Math.max(0, bombY - bombValue + 1);
        int bombDownRange = Math.min(H - 1, bombY + bombValue - 1);

        // 가로 범위의 벽돌을 터뜨린다.
        for (int i = bombLeftRange; i <= bombRightRange; i++) {
            int thisValue = board[bombY][i];
            if (thisValue > 0) {
                bombQueue.add(new Bomb(i, bombY, thisValue));
                board[bombY][i] = 0;
            }
        }

        // 세로 범위의 벽돌을 터뜨린다.
        for (int i = bombUpRange; i <= bombDownRange; i++) {
            int thisValue = board[i][bombX];
            if (thisValue > 0) {
                bombQueue.add(new Bomb(bombX, i, thisValue));
                board[i][bombX] = 0;
            }
        }

        return board;
    }

    // 해당 보드에 남은 벽돌의 수를 세는 메서드
    private static int getRemainCount(int[][] board) {
        int cnt = 0;
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                if (board[i][j] != 0) cnt++;
            }
        }
        return cnt;
    }

    // 해당 보드에서 붕 떠있는 벽돌을 내리는 메서드
    private static int[][] down(int[][] board) {
        for (int i = 0; i < W; i++) {
            //이동할 위치
            int y = H;
            // 아래서부터 차례로 내림
            for (int j = H - 1; j >= 0; j--) {
                if (board[j][i] != 0) {
                    y--;
                    board[y][i] = board[j][i];
                    if (j != y) {
                        board[j][i] = 0;
                    }
                }
            }
        }
        return board;
    }

    // 이차원 배열 깊은 복사
    public static int[][] deepCopy(int[][] board) {
        final int[][] result = new int[board.length][board[0].length];
        for (int i = 0; i < H; i++) {
            System.arraycopy(board[i], 0, result[i], 0, W);
        }
        return result;
    }

    //벽돌을 나타낼 Bomb class
    static class Bomb {
        private int x;
        private int y;
        private int value;

        public Bomb(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }
}
