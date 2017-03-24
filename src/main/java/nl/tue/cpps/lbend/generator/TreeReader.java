package nl.tue.cpps.lbend.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import lombok.SneakyThrows;
import nl.tue.cpps.lbend.geometry.Tree;

public class TreeReader implements Iterator<Tree> {
    private final int nbNodes;
    private final Scanner scanner;

    @SneakyThrows(IOException.class)
    public TreeReader(File pathToFolder, int nbNodes) {
        this.nbNodes = nbNodes;
        File file = new File(pathToFolder, nbNodes + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        scanner = new Scanner(br);
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNextLine();

    }

    @Override
    public Tree next() {
        scanner.nextLine();
        scanner.nextLine();

        Tree tree = new Tree(nbNodes);
        for (int j = 0; j < nbNodes; j++) {
            char[] line = scanner.nextLine().toCharArray();
            for (int i = 0; i < line.length; i++) {
                if (!tree.areConnected(i, j) && line[i] == '1') {
                    tree.connect(i, j);
                }
            }
        }

        return tree;
    }
}
