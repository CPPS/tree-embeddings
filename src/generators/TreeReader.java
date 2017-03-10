package generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Scanner;

import geometry.Tree;

public class TreeReader implements Iterator<Tree> {

	int nbNodes;
	Scanner scanner;

	public TreeReader(String pathToFolder, int nbNodes) throws FileNotFoundException {
		this.nbNodes = nbNodes;
		File file = new File(pathToFolder + "/" + nbNodes + ".txt");
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
		System.out.println(scanner.nextLine());
		Tree tree = new Tree(nbNodes);
		for (int j = 0; j < nbNodes; j++) {
			char[] line = scanner.nextLine().toCharArray();
			for (int i = 0; i < line.length; i++) {
				if(!tree.areConnected(i, j) && line[i]=='1'){
					tree.connect(i, j);
				}
			}
		}
		return tree;
	}

}
