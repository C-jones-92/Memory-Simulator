/**
 * Image Blob of pixels.
 * @author Corey
 *
 * @param <Pixel> Generic type that extends Number.
 */
public class ImageBlob<Pixel extends Number>
{
	/**
	 * Children in directions.
	 */
	ImageBlob<Pixel> NW, NE, SE, SW;    
	
	/**
	 * Data point of node that is a pixel.
	 */
	Pixel value;                         


	/**
	 * Creates null ImageBlob.
	 */
	public ImageBlob() {

	}

	/**
	 * Creates Image Blob with desired value.
	 * @param value Pixel value desired.
	 */
	@SuppressWarnings("unchecked")
	public ImageBlob(Short value) {
		this.value = (Pixel) value;
	}

	/**
	 * Creates new Image Blob with children in parameters.
	 * @param nw Northwest child.
	 * @param ne Northeast child.
	 * @param se Southeast child.
	 * @param sw Southwest child.
	 */
	public ImageBlob(ImageBlob<Pixel> nw, ImageBlob<Pixel> ne,
			ImageBlob<Pixel> se, ImageBlob<Pixel> sw) {

		this.NW = nw;
		this.NE = ne;
		this.SE = se;
		this.SW = sw;
	}

	/**
	 * Creates new Image blob with children and value.
	 * @param value Pixel value.
	 * @param nw Northwest child.
	 * @param ne Northeast child. 
	 * @param se Southeast child.
	 * @param sw Southwest child. 
	 */
	public ImageBlob(Pixel value, ImageBlob<Pixel> nw, ImageBlob<Pixel> ne,
			ImageBlob<Pixel> se, ImageBlob<Pixel> sw) {
		this.value = value;
		this.NW = nw;
		this.NE = ne;
		this.SE = se;
		this.SW = sw;
	}

	/**
	 * Creates new Image blob with a previous image blobs value.
	 * @param x Image blob to copy. 
	 */
	public ImageBlob(ImageBlob<Pixel> x) {
		this.NW = x.NW;
		this.NE = x.NE;
		this.SE = x.SE;
		this.SW = x.SW;
		this.value = x.value;
	}

	/**
	 * Used in iterator to turn image blob in string of values.
	 * @return String of blob value.
	 */
	public  String toString() {
		String string = "null";
		@SuppressWarnings("unused")
		Pixel okay;

		if(this.value != null) {
			Short answer = (Short)this.value;
			string = Short.toString(answer);
		}

		return string;
	}


}

