package mandelbrotSet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.apfloat.Apfloat;

public class MakePicture {

	private int width, height, max;
	private double xMult, yMult;
	private double xShift, yShift;
	private double[] colors;
	private double[][] pixelValues;
	private Apfloat[][] realPoints;
	private Apfloat[][] imaginePoints;
	private String name;

	// sets all initial variables
	public MakePicture(int height, int max) {
		this.height = height;
		width = (height * 16) / 9;
		this.max = max;
		realPoints = new Apfloat[height][width];
		imaginePoints = new Apfloat[height][width];
		pixelValues = new double[height][width];
		colors = new double[max + 1];
		name = "Mandelbrot " + height + "X" + width + "at" + max + ".png";
		xMult = 5;
		yMult = 2.82;
		xShift = 0;
		yShift = 0;

		makePoints();
	}

	// sets all the initial values for makeing a video
	public MakePicture(int height, int max, double yMult, double xShift, double yShift, String name) {
		this.height = height;
		width = (height * 16) / 9;
		this.max = max;
		realPoints = new Apfloat[height][width];
		imaginePoints = new Apfloat[height][width];
		pixelValues = new double[height][width];
		colors = new double[max + 1];
		this.name = name;
		this.yMult = yMult;
		this.xMult = (yMult * 16) / 9;
		this.xShift = xShift;
		this.yShift = yShift;

		makePoints();
	}

	// just starts the calculations
	public void start() throws InterruptedException, ExecutionException, IOException {
		getSet();
	}

	// histogram coloring makes it ranbow but no too big on it
	private void setPalette() {
		int total = 0;
		double h = 0;
		// makes the historgam
		int[] histogram = new int[max];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if ((int) (pixelValues[row][col]) != max) {
					histogram[(int) Math.floor(pixelValues[row][col])]++;
				}
			}
		}
		for (int i : histogram) {
			total += i;
		}

		// evenly spreads the interations accross the color specturm
		for (int i = 0; i < max; i++) {
			h += (double) histogram[i] / total;
			colors[i] = h;
		}
		colors[max] = h;
		System.out.println("Colors done.");
	}

	// normal iterative coloing gives it a nice purple look
	private void setPalette2() {
		for (int i = 0; i < max; i++) {
			colors[i] = (double) ((i / 256f) + 0.73f);
		}
		System.out.println("Colors done.");
	}

	// maps the coordnate plane to the image
	private void makePoints() {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				Apfloat real = new Apfloat(((col - width / 2) * xMult / width) + xShift, 100);
				Apfloat imagine = new Apfloat(((row - height / 2) * yMult / height) + yShift, 100);
				realPoints[row][col] = real;
				imaginePoints[row][col] = imagine;
			}
		}
		System.out.println("Points mapped.");
	}

	// multithreaded set up for calculting the set
	private void getSet() throws InterruptedException, ExecutionException, IOException {
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		ArrayList<Callable<double[]>> taskList = new ArrayList<Callable<double[]>>();

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				taskList.add(new MandelbrotMath(max, realPoints[row][col], imaginePoints[row][col], row, col));
			}
		}
		System.out.println("Tasks set.");

		List<Future<double[]>> futures = executor.invokeAll(taskList);
		System.out.println("Futures invoked.");

		for (Future<double[]> future : futures) {
			double[] pixel = future.get();
			pixelValues[(int) pixel[1]][(int) pixel[2]] = pixel[0];
		}

		executor.shutdown();
		drawSet();
	}

	// draws the set using linear interpolation might switch to bilinear or bicubic
	// if i like those better
	private void drawSet() throws IOException {
		setPalette2();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		System.out.println("Setting image.");
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (pixelValues[row][col] == max) {
					image.setRGB(col, row, 0);
				} else {
					// linear interpolation
					double color1 = colors[(int) Math.floor(pixelValues[row][col])];
					double color2 = colors[(int) Math.ceil(pixelValues[row][col])];
					double percent = pixelValues[row][col] % 1;
					double color = ((1.0 - percent) * color1) + (percent * color2);

					image.setRGB(col, row, (int) Color.HSBtoRGB((float) color, 1,
							(float) (pixelValues[row][col] / (pixelValues[row][col] + 4f))));
				}
			}
		}

		ImageIO.write(image, "png", new File(name));
	}
}
