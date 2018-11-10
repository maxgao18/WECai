import java.io.*;

public class JSBoard implements Printerface{
//    enum States {EMPTY, PLAYER1, PLAYER2, WALL}
//    States[][] gameState;

//    public static void main(String [] args) {
//        States[][] gs = new States[17][17];
//        JSBoard jb = new JSBoard();
//
//        gs[3][5] = States.PLAYER2;
//        gs[1][2] = States.EMPTY;
//        gs[5][5] = States.PLAYER1;
//
//
//        jb.writeToHTML(gs);
//    }

    public void writeToHTML(States[][] gs) {
        // The name of the file to open.
        String fileName = "baseJS.txt";

        String writeTo = "ui.html";

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fileReader);


            FileWriter fileWriter = new FileWriter(writeTo);
            BufferedWriter bw = new BufferedWriter(fileWriter);

            while((line = br.readLine()) != null) {
                System.out.println(line);
                bw.write(line);
                bw.newLine();
            }

            int blockCount = 0;
            for (int x = 1; x < gs.length-1; x++) {
                for (int y = 1; y < gs.length-1; y++) {
                    bw.write("        var block" + blockCount + " = {x:");
                    blockCount++;
                    bw.write(Integer.toString(x));
                    bw.write(", y: ");
                    bw.write(Integer.toString(y));
                    bw.write(", type: \"" );
                    if (gs[x][y] != null) {
                        switch (gs[x][y]) {
                            case WALL:
                                if (x == 0 || y == 0 || x == gs.length - 1 || y == gs[x].length - 1) {
                                    bw.write("tail");
                                } else {
                                    bw.write("wall");
                                }
                                break;
                            case PLAYER1:
                                bw.write("p1");
                                break;
                            case PLAYER2:
                                bw.write("p2");
                                break;
                            case EMPTY:
                                bw.write("empty");
                                break;
                        }
                    } else {
                        bw.write("empty");
                    }
                    bw.write("\" };");
                    bw.newLine();
                }
            }
//            bw.write("        var block1 = {x: 0, y: 0, type: \"wall\"};\n" +
//                    "        var block2 = {x: 16, y: 16, type: \"wall\"};\n" +
//                    "        var block3 = {x: 1, y: 1, type: \"p1\"};\n");
//

            bw.write("        var block_array = new Array(");
            for (int i = 0; i < 15*15-1; i++) {
                bw.write("block" + i + ", ");
            }
            bw.write("block" + (15*15-1) + " ");
            bw.write(");\n");
            bw.write("" +
                    "        container.appendChild(grid(17, 17, 750, block_array));\n" +
                    "</script>");

            // Always close files.
            br.close();
            bw.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '"+ fileName + "'");
        }
    }
}
