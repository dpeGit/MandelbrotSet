package mandelbrotSet;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MandelbrotMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		boolean computeType = vidOrPic();

		if (computeType) {
			picture();
		} else {
			video();
		}
	}

	private static boolean vidOrPic() {
		int input = -1;
		while (input == -1) {
			System.out.println("Press 1 for picture and 2 for a video.");
			input = scanInt();
		}

		if (input != 1 && input != 2) {
			return vidOrPic();
		} else if (input == 1) {
			return true;
		} else {
			return false;
		}

	}

	private static void picture() throws InterruptedException, ExecutionException, IOException {
		int height = 0;
		int max = 0;

		while (height <= 0) {
			System.out.println(
					"Enter the height of the image. Must be positive integer. Image will be 16x9 aspect ratio.");
			height = scanInt();
		}

		while (max <= 0) {
			System.out.println("Enter max iterations to run to. Must be positive integer.");
			max = scanInt();
		}

		long start = System.nanoTime();
		MakePicture set = new MakePicture(height, max);
		set.start();
		System.out.println((System.nanoTime() - start) / 1000000000 + " seconds");
	}

	private static void video() throws IOException, InterruptedException, ExecutionException {
		int height = 0;
		int max = 0;
		int time = 0;

		while (height <= 0) {
			System.out.println(
					"Enter the height of the image. Must be positive integer. Image will be 16x9 aspect ratio.");
			height = scanInt();
		}

		while (max <= 0) {
			System.out.println("Enter max iterations to run to. Must be positive integer.");
			max = scanInt();
		}

		while (time <= 0) {
			System.out.println("Enter length of the video in seconds. Must be positive integer.");
			time = scanInt();
		}

		long start = System.nanoTime();
		MakeVideo set = new MakeVideo(height, max, time);
		set.start();
		System.out.println((System.nanoTime() - start) / 1000000000 + " seconds");
	}

	private static int scanInt() {
		Scanner scan1 = new Scanner(System.in);
		try {
			return scan1.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("Input must be an integer.");
			return -1;
		}
	}
}
