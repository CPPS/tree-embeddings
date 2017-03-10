package generators;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.junit.Test;

import geometry.Tree;

public class TreeReaderTest {
	
	@Test
	public void test() throws FileNotFoundException{
		String pathToFolder="/home/stefan/Tree Embeddings";
		int nbNodes=4;
		TreeReader treereader=new TreeReader(pathToFolder,nbNodes);
		for(int i=0;i<2;i++){
			assertTrue("it failed after"+i,treereader.hasNext());	
			treereader.next();
		}
		assertFalse(treereader.hasNext());
	}

}
