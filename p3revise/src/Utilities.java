import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

/**
 * Utilites class for opening and printing to file.
 * @author Corey
 *
 */
public class Utilities
{	
	/**
	 * Loads file of image and puts in into array form for Quad tree parameter.
	 * @param pgmFile .pgm File you wish to load.
	 * @return returns a 2d array of the file's pixel values.
	 * @throws FileNotFoundException Throws exception if file cannot be found.
	 */
	@SuppressWarnings("unused")
	static Short[][] loadFile(String pgmFile) throws FileNotFoundException{

		File file = new File(pgmFile);
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(file);
		StringBuilder sb = new StringBuilder();

		while(sc.hasNext()) {
			sb.append(sc.next());
			sb.append("\n");
		}
		String str = sb.toString();
		String[] array = str.split("\n");

		String format = array[0];
		int height = Integer.parseInt(array[1]);
		int width = Integer.parseInt(array[2]);
		String maxColor = array[3];

		Short[][] image = new Short[height][width];
		int start = 4;

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				image[i][j] = Short.parseShort(array[start++]);
			}
		}

		return image;


	}

	/**
	 * Exports image to a file.
	 * @param image Quad tree image you wish to export as file.
	 * @param filename Name of file.
	 * @throws FileNotFoundException Thrown exception if file cannot be made or found.
	 */
	@SuppressWarnings("resource")
	static void exportImage(QuadTreeImage<?> image, String filename) throws FileNotFoundException {

		File file = new File(filename);
		PrintWriter write = new PrintWriter(file);
		if(image == null) {
			return;
		}
		write.print("P2\n" + image.getHeight() + " " + image.getHeight() + "\n");
		for(int i = 0; i < image.getHeight(); i++) {
			for(int j = 0; j < image.getHeight(); j++) {
				write.print(getNode(image, j, i, image.getRoot(), image.getHeight()) + " ");
			}write.println();
		}
		write.close();


	}

	/**
	 * Gets node from image tree used in export.
	 * @param tree Quad tree.
	 * @param column column index.
	 * @param row row index.
	 * @param root Root of tree.
	 * @param size Size of tree.
	 * @return Returns the node at location/
	 */
	private static ImageBlob<?> getNode(QuadTreeImage<?> tree, int column, int row, ImageBlob<?> root, int size){


		if(column >= tree.getHeight() || row > tree.getHeight()) {
			throw new IndexOutOfBoundsException();
		}
		if(root.value != null || size == 1) {
			return root;
		}

		int newSize = size/2;
		if(column < newSize && row < newSize ) {
			if(root.NW.value != null)
				return root.NW;
			else
				return getNode(tree, column, row, root.NW, newSize);
		}
		if(column >= newSize && row < newSize ) {
			if(root.NE.value != null)
				return root.NE;
			else
				return getNode(tree, column-newSize, row, root.NE, newSize);
		}
		if(column >= newSize && row >= newSize ) {
			if(root.SE.value != null)
				return root.SE;
			else
				return getNode(tree, column-newSize, row-newSize, root.SE, newSize);
		}
		if(column < newSize && row >= newSize ) {
			if(root.SW.value != null)
				return root.SW;
			else
				return getNode(tree, column, row-newSize, root.SW, newSize);
		}

		return null;
	}


}
