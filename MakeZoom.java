package mandelbrotSet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class MakeZoom {
	private int width, height, max;
	private BufferedImage image;
	private int[] colors;
	private String name;
	private BigDecimal[][] pointsReal;
	private BigDecimal[][] pointsImagine;
	private int[][] pixelValues;
	private double time;

	public MakeZoom(int w, int h, int m, double t) throws IOException, InterruptedException, ExecutionException {
		width = w;
		height = h;
		max = m;
		time = t;
		name = "Mandelbrot " + height + "X" + width + "at" + max + "for" + time + "s" + ".mp4";
		pointsReal = new BigDecimal[width][height];
		pointsImagine = new BigDecimal[width][height];
		colors = new int[max];
		pixelValues = new int[width][height];
		setPalete();
		makeVideo();
	}

	public void start() throws InterruptedException, ExecutionException, IOException {
		getSet();
	}

	private void setPalete() {
		for (int i = 0; i < max; i++) {
			colors[i] = Color.HSBtoRGB((i / 256f) + .73f, 1, i / (i + 4f));
		}
	}

	private void makePoints(double xMult, double yMult) {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				BigDecimal real = new BigDecimal(((col - width / 2) * xMult / width) + .1477197897737227);
				BigDecimal imagine = new BigDecimal(((row - height / 2) * yMult / height) - .6463248991533478);
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
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				if (pixelValues[k][i] != 0) {
					image.setRGB(k, i, colors[pixelValues[k][i]]);
				} else {
					image.setRGB(k, i, 0);
				}
			}
		}
	}

	private void makeVideo() throws IOException, InterruptedException, ExecutionException {
		File vid = new File(name);
		float percent, oldPercent = 0f;
		int totalFrames = (int) time * 60;
		AWTSequenceEncoder8Bit encoder = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(vid, 60);
		encoder.getEncoder().setKeyInterval(60);
		for (int i = 0; i < (int) totalFrames; i++) {
			makePoints(5.0 * Math.pow(.996, i), 2.84 * Math.pow(.996, i));
			getSet();
			encoder.encodeImage(image);
			percent = (float) i / totalFrames;
			percent *= 1000;
			percent = Math.round(percent);
			percent /= 10;
			if (percent != oldPercent) {
				oldPercent = percent;
				System.out.println(percent + "%");
			}
			System.out.println((i + 1) + "/" + totalFrames + " frames done");
		}
		encoder.finish();
	}
}
