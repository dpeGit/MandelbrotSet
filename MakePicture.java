package mandelbrotSet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class MakePicture {
	private int width, height, max, numPixels, numDone;
	private BufferedImage image;
	private int[] colors;
	private String name;
	private float percent, oldPercent;
	private BigDecimal[][] pointsReal;
	private BigDecimal[][] pointsImagine;
	int[][] pixelValues;

	public MakePicture(int w, int h, int m) {
		width = w;
		height = h;
		max = m;
		pointsReal = new BigDecimal[width][height];
		pointsImagine = new BigDecimal[width][height];
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		colors = new int[max];
		numPixels = height * width;
		numDone = 0;
		percent = 0F;
		oldPercent = 0F;
		pixelValues = new int[width][height];
		setPalete();
		makePoints();
	}

	public void start() throws InterruptedException, ExecutionException, IOException {
		getSet();
	}

	private void setPalete() {
		for (int i = 0; i < max; i++) {
			colors[i] = Color.HSBtoRGB((i / 256f) + .73f, 1, i / (i + 4f));
		}
	}

	private void makePoints() {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				BigDecimal real = new BigDecimal((col - width / 2) * 5.0 / width);
				BigDecimal imagine = new BigDecimal((row - height / 2) * 2.82 / height);
				pointsReal[col][row] = real;
				pointsImagine[col][row] = imagine;
			}

		}
	}

	private void getSet() throws InterruptedException, ExecutionException, IOException {
		ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				Callable<int[]> callable = new MandelbrotMath(max, pointsReal[col][row], pointsImagine[col][row], col,
						row);

				numDone++;
				percent = (float) numDone / numPixels;
				percent *= 1000;
				percent = Math.round(percent);
				percent /= 10;
				if (percent != oldPercent) {
					oldPercent = percent;
					System.out.println(percent + "%");
				}

				ListenableFuture<int[]> future = executor.submit(callable);
				Futures.addCallback(future, new FutureCallback<int[]>() {
					public void onSuccess(int[] pixel) {
						pixelValues[pixel[1]][pixel[2]] = pixel[0];
					}

					public void onFailure(Throwable thrown) {
					}
				});
			}
		}
		executor.shutdown();
		drawSet();
	}

	private void drawSet() throws IOException {
		name = "Mandelbrot " + height + "X" + width + "at" + max + ".png";
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				if (pixelValues[k][i] != 0) {
					image.setRGB(k, i, colors[pixelValues[k][i]]);
				} else {
					image.setRGB(k, i, 0);
				}
			}
		}
		ImageIO.write(image, "png", new File(name));
	}
}
