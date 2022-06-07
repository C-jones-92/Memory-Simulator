import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;
/**
 * Quad Tree image class.
 * @author Corey
 *
 * @param <Pixel> Generic number type that represent pixel value.
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class QuadTreeImage<Pixel extends Number> implements Comparable, Iterable<ImageBlob<Pixel>>
{

	/**
	 * Height and width value of tree.
	 */
	private int hxw;

	/**
	 * Maximum depth of tree.
	 */
	private int maxDepth;

	/**
	 * Root of quad tree.
	 */
	private ImageBlob<Pixel> root;

	/** 
	 * Number of roots in the tree.
	 */
	private int numberOfNodes = 1;

	/**
	 * Sum used in compareTo.
	 */
	private int sum;

	/**
	 * Level of node.
	 */
	private static int level;

	/**
	 * Answer used in toString.
	 */
	private ImageBlob<Pixel> answer;

	/**
	 * String used in toString.
	 */
	private static String string = "";


	/**
	 * Constructor for QuadTreeImage.
	 * @param <Pixel> Generic type that extends Number.
	 * @param array An square array filled with pixel values.
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	public <Pixel extends Number> QuadTreeImage(Pixel[][] array){

		this.hxw = array[0].length;
		this.maxDepth = (int) (Math.log(this.hxw)/Math.log(2));
		this.root = new ImageBlob(construct(0, 0, array, this.hxw));
	}


	/**
	 * Constructs ImageBlobs in this quad tree.
	 * @param <Pixel> Generic type that extends Number.
	 * @param x Row index in array.
	 * @param y Column index in Array.
	 * @param array A square array filled with pixel values.
	 * @param size Size of the array.
	 * @return Returns a new ImageBlob.
	 */
	@SuppressWarnings({ "hiding" })
	private <Pixel extends Number> ImageBlob<Pixel> construct(int x, int y, Pixel[][] array, int size) {

		if(size < 0) {
			return null;
		}

		for(int i = x; i < x+size; i++) {
			for (int j = y; j < y+size; j++) {
				if((int)(Short)array[x][y] != (int)(Short)array[i][j]) {
					numberOfNodes += 4;
					return new ImageBlob<Pixel>(construct(x,y, array, size/2), 
							construct(x, y + size/2, array, size/2),
							construct(x + size/2, y + size/2, array, size/2),
							construct(x+ size/2, y, array, size/2));
				}

			}
		}

		return new ImageBlob<Pixel>(array[x][y], null, null, null,null);
	}

	/**
	 * Gets color of ImageBlob using coordinates from the array.
	 * @param w Column index.
	 * @param h Row index.
	 * @return Returns the pixel value of the node located at index provided.
	 */
	public Pixel getColor(int w, int h) {

		return getNode(w, h, this.root, this.hxw).value;
	}

	/**
	 * Sets the color of the node located at column w row h.
	 * @param w Column index.
	 * @param h Row index.
	 * @param value Value of the Pixel you wish to set.
	 */
	@SuppressWarnings("unchecked")
	public void setColor(int w, int h, Pixel value) {

		ImageBlob<Pixel> node = getNode(w, h, this.root, this.hxw);

		if(node.value == null) {
			node.NW = null;
			node.NE = null;
			node.SE = null;
			node.SW = null;
			node.value = value;
			return;
		}


		ImageBlob parent = findParent(this.root, node);
		node.value = value;
		boolean okay = true;
		if(equalSector(parent)) {
			while(okay) {
				parent.NW = null;
				parent.NE = null;
				parent.SE = null;
				parent.SW = null;
				parent.value = value;
				parent = findParent(this.root,parent);
				okay = equalSector(parent);
			}
		}


		return;

	}

	/**
	 * Checks to see if a sector of the tree is equal. Used in setColor to restructure tree.
	 * @param parent Parents node of the node changed in setColor.
	 * @return Returns true if all children of parent are equal.
	 */
	private boolean equalSector(ImageBlob<Pixel> parent) {

		Short value = (Short)parent.NW.value;

		if((Short)parent.NW.value == value && (Short)parent.NE.value == value && (Short)parent.SE.value == value && (Short)parent.SW.value == value) {
			return true;
		}
		else
			return false;

	}

	/**
	 * Sums values of all pixels, and all nodes and takes the ratio between them.
	 * @param bytesPerPixel Amount of bytes used in each pixel.
	 * @param bytesPerPointer Amount of bytes used in each pointer.
	 * @return Returns the ratio between node bytes and pixel bytes.
	 */
	public float sizeRatio (int bytesPerPixel, int bytesPerPointer) {

		float pixelBytes = bytesPerPixel * this.hxw * this.hxw;
		float nodeBytes = this.numberOfNodes *( 10 + (bytesPerPointer * 4));

		return nodeBytes/pixelBytes;
	}

	/**
	 * Translates the quad tree into a string where each node is represented by {a b c d},
	 * where a and b are the two digits of the top left coordinate of the node,
	 * c is the size of the node, and d is the value of the node.
	 * @return String of nodes.
	 */
	public String toString() {

		string = "";
		return traverse2(this.root, 0, 0, 0, this.hxw);

	}

	/**
	 * Compares two quadtrees and finds the difference between the sums of their pixel values.
	 * @param other is the other quad tree you wish to compare it to. 
	 * @return returns sum.
	 */
	@SuppressWarnings({ "unchecked" })
	public  int compareTo(Object other) {

		sum = 0;
		int sum1 = 0; int sum2 = 0;


		if(this.numberOfNodes == 1) {
			sum1 = this.hxw * this.hxw * (Integer)this.root.value;
		}
		else
		{

			sum1 = traverse(this.root, this.maxDepth);

		}
		sum = 0;
		if(other instanceof QuadTreeImage && ((QuadTreeImage) other).numberOfNodes == 1) {
			sum2 = ((QuadTreeImage) other).hxw * ((QuadTreeImage) other).hxw * (Integer)((QuadTreeImage) other).root.value;
		}
		else
		{
			sum2 = traverse(((QuadTreeImage) other).root,((QuadTreeImage)other).maxDepth);
		}

		return sum1 - sum2;
	}

	/**
	 * Finds the intersection between two quad trees.
	 * @param <Pixel> Generic type that extends number.
	 * @param other The other quad tree you wish to compare to.
	 * @return returns another quad tree that shows where the two quad trees overlapped. 
	 */
	@SuppressWarnings({ "hiding" })
	public <Pixel extends Number> QuadTreeImage<Pixel> intersectionMask(QuadTreeImage<Pixel> other) {


		String one = "";
		String two = "";
		int count = 1;
		int pcount = 1;
		int level = 1;
		int number = 0;
		for(ImageBlob p : this) {
			if(p.value != null) {
				for(int i = 0; i < (this.hxw*this.hxw)/Math.pow(4, level); i++) {
					one += p.toString() + " ";
					number++;
				}
			}
			count++;
			if(count > Math.pow(4, pcount) + Math.pow(4, pcount - 1)) {
				level++;
				pcount++;
			}

		}

		count = 1;
		number = 0;
		level = 1;
		pcount = 1;
		for(ImageBlob p : other) {
			if(p.value != null) {
				for(int i = 0; i < (other.hxw/2); i++) {
					two += p.toString() + " ";
					number++;
				}
			}
			count++;
			if(count > Math.pow(4, pcount) + Math.pow(4, pcount - 1)) {
				level++;
				pcount++;
			}

		}




		return null;
	}



	/**
	 * Traverses through the quad tree.
	 * @param <Pixel> Generic type that extends Number.
	 * @param root Starts with root of quad tree.
	 * @param level Starts with level of root/
	 * @return Returns pixel sum. 
	 */
	@SuppressWarnings("hiding")
	private  <Pixel extends Number> int traverse(ImageBlob<Pixel> root, int level) {

		if(root == null) {
			return 0;
		}

		if(root.value != null) {
			sum += (Short)root.value * Math.pow(4, level);
		}
		level--;
		traverse(root.NW, level);
		traverse(root.NE, level);
		traverse(root.SE, level);
		traverse(root.SW, level);

		return sum;

	}

	/**
	 * Traverse tree in breadth first.
	 * @param root Root of the tree.
	 * @param level Level of the node.
	 * @param row Row index where node can be found.
	 * @param col Column index where node can be found.
	 * @param size Size of node.
	 * @return Returns a string used in toString.
	 */
	private String traverse2(ImageBlob<Pixel> root, int level, int row, int col, int size){

		if(root == null) 
			return "";

		if(root.NW != null && root.NW.value != null) {
			string += "{" + row + " " + col + " " + size/2 + " "+ root.NW.value + "},";
		}

		if(root.NE != null && root.NE.value != null) {
			string += "{" + row + " " + (col+size/2) + " " + size/2 + " "+ root.NE.value + "},";
		}

		if(root.SE != null && root.SE.value != null) {
			string += "{" + (row+size/2) + " " + (col+size/2) + " " + size/2 + " "+ root.SE.value + "},";
		}

		if(root.SW != null && root.SW.value != null) {
			string += "{" + (row+size/2) + " " + col + " " + size/2 + " "+ root.SW.value + "},";
		}

		traverse2(root.NW, level++, row, col, (int) Math.ceil(size/2));
		traverse2(root.NE, level++, row, col + (int) Math.ceil(size/2), (int) Math.ceil(size/2));
		traverse2(root.SE, level++, row + (int) Math.ceil(size/2), col + (int) Math.ceil(size/2), (int) Math.ceil(size/2));
		traverse2(root.SW, level++, row + (int) Math.ceil(size/2), col, (int) Math.ceil(size/2));

		return string;
	}

	/**
	 * Finds parent of root.
	 * @param root Root of quad tree.
	 * @param goal Goal node to find.
	 * @return Node that is parent. 
	 */
	private   ImageBlob<Pixel> findParent(ImageBlob<Pixel> root, ImageBlob<Pixel> goal) {


		if(root == null) {
			return null;
		}

		if(root.NW == goal) {
			answer = root;
		}
		findParent(root.NW, goal);

		if(root.NE == goal) {
			answer = root;
		}
		findParent(root.NE, goal);

		if(root.SE == goal) {
			answer = root;
		}
		findParent(root.SE, goal);

		if(root.SW == goal) {
			answer = root;
		}
		findParent(root.SW, goal);

		return answer;

	}

	/**
	 * Iterator.
	 * @return returns an imageblob iterator.
	 */
	public Iterator<ImageBlob<Pixel>> iterator(){

		return new QuadTreeImageIterator<Pixel>(this);

	}

	/**
	 * Iterator for QuadTreeImage.
	 * @author Corey
	 *
	 * @param <Pixel> Generic type that extends Number.
	 */
	@SuppressWarnings("hiding")
	public class QuadTreeImageIterator<Pixel extends Number> implements Iterator<ImageBlob<Pixel>> {
		
		/**
		 * Queue.
		 */
		Queue queue = new Queue();
		
		/**
		 * Counter.
		 */
		int count;
		
		/**
		 * Goal.
		 */
		int goal;

		/**
		 * Constructor.
		 * @param <Pixel> Generic type that extends Number.
		 * @param tree Quad Tree you want to iterate over.
		 */
		@SuppressWarnings("unchecked")
		public <Pixel extends Number> QuadTreeImageIterator(QuadTreeImage<Pixel> tree) {
			
			this.queue.enqueue(tree.root);
			this.count = 0;
			this.goal = tree.numberOfNodes;

		}

		/**
		 * Checks if next value exists.
		 * @return returns true if next value exists.
		 */
		@Override
		public boolean hasNext()
		{
			if(this.queue.isEmpty())
				return false;
			else
				return true;
		}

		/**
		 * Finds next ImageBlob and returns it.
		 * @return returns the ImageBlob
		 */
		@SuppressWarnings("unchecked")
		@Override
		public ImageBlob<Pixel> next()
		{

			ImageBlob<Pixel> answer = (ImageBlob<Pixel>) this.queue.dequeue();
			if(answer.NW != null) {
				this.queue.enqueue(answer.NW);
				this.queue.enqueue(answer.NE);
				this.queue.enqueue(answer.SE);
				this.queue.enqueue(answer.SW);
				count++;
			}
			return answer;

		}



	}

	/**
	 * Gets the node at a specified location.
	 * @param column Column index where node is located.
	 * @param row Row index where node is located.
	 * @param root Root of quad tree.
	 * @param size Size of image tree. 
	 * @return Returns ImageBlob at desired location.
	 */
	@SuppressWarnings("unchecked")
	private ImageBlob<Pixel> getNode(int column, int row, ImageBlob root, int size){


		if(column >= this.hxw || row > this.hxw) {
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
				return getNode(column, row, root.NW, newSize);
		}
		if(column >= newSize && row < newSize ) {
			if(root.NE.value != null)
				return root.NE;
			else
				return getNode(column-newSize, row, root.NE, newSize);
		}
		if(column >= newSize && row >= newSize ) {
			if(root.SE.value != null)
				return root.SE;
			else
				return getNode(column-newSize, row-newSize, root.SE, newSize);
		}
		if(column < newSize && row >= newSize ) {
			if(root.SW.value != null)
				return root.SW;
			else
				return getNode(column, row-newSize, root.SW, newSize);
		}

		return null;
	}

	/**
	 * Grabs height of tree.
	 * @return returns height of tree.
	 */
	public int getHeight() {
		return this.hxw;
	}

	/**
	 * Grabs root of tree.
	 * @return returns root of tree.
	 */
	public ImageBlob<Pixel> getRoot() {
		return this.root;
	}
	
	/**
	 * Grabs height of tree.
	 * @return returns width of tree.
	 */
	public int getImageWidth() {
		return this.hxw;
	}

}
