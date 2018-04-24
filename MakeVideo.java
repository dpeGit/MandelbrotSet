package mandelbrotSet;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

public class MakeVideo {

	private int height, max, time;

	public MakeVideo(int height, int max, int time) {
		this.height = height;
		this.max = max;
		this.time = time;
	}

	public void start() throws IOException, InterruptedException, ExecutionException {
		makeVideo();
	}

	private void makeVideo() throws IOException, InterruptedException, ExecutionException {
		String name = "Mandelbrot" + height + "X" + (height * 16) / 9 + "at" + max;
		int preDone = 0;

		File path = new File(name);
		path.mkdirs();
		File vid = new File(name + ".mp4");

		while (new File(name + "/" + preDone + ".png").exists()) {
			preDone++;
		}

		float percent, oldPercent = 0f;
		int totalFrames = time * 60;
		AWTSequenceEncoder8Bit encoder = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(vid, 60);
		encoder.getEncoder().setKeyInterval(60);
		System.out.println("Starting at frame: " + preDone + "\n");

		for (int i = preDone; i < totalFrames; i++) {
			System.out.println("x-axis length: " + 5.0 * Math.pow(0.996, i));
			MakePicture frame = new MakePicture(height, max, 2.82 * Math.pow(0.996, i), 0.1477197897737227,
					-0.6463248991533478, name + "/" + i + ".png");
			frame.start();
			System.out.println((i + 1) + "/" + totalFrames + " frames done.\n\n");

			percent = (float) i / totalFrames;
			percent *= 1000;
			percent = Math.round(percent);
			percent /= 10;
			if (percent != oldPercent) {
				oldPercent = percent;
				System.out.println(percent + "%\n");
			}
		}

		System.out.println("Encoding");
		for (int i = 0; i < totalFrames; i++) {
			encoder.encodeImage(ImageIO.read(new File(name + "/" + i + ".png")));
		}
		encoder.finish();
	}
}
