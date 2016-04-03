package tetris.entities;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class Debugger {

    public static void printMatrix(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                sb.append(matrix[i][j]);
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }
}
