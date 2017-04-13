package mandelbrotSet;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Inputs {
	private int width, height, max;
	private double time;

	public Inputs() {
	}

	public void scanHeight() {
		Scanner scan1 = new Scanner(System.in);
		System.out.println("Please input the media height. Must Be greater than 0. Aspect Ratio is 16x9");
		try {
			height = scan1.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("Input must be an integer.");
			scanHeight();
		}
		if (height > 0) {
			width = (height / 9) * 16;
			return;
		} else {
			scanHeight();
		}
	}

	public void scanMax() {
		Scanner scan1 = new Scanner(System.in);
		System.out.println("Please input the max number of iterations. Must Be greater than 0.");
		try {
			max = scan1.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("Input must be an integer.");
			scanMax();
		}
		if (max > 0) {
			return;
		} else {
			scanMax();
		}
	}

	public void scanTime() {
		Scanner scan1 = new Scanner(System.in);
		System.out
				.println("Please input the length of the video in seconds. Must Be greater than 0. Can be a decimal.");
		try {
			time = scan1.nextDouble();
		} catch (InputMismatchException e) {
			System.err.println("Input must be a number.");
			scanTime();
		}
		if (time > 0) {
			return;
		} else {
			scanTime();
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMax() {
		return max;
	}

	public double getTime() {
		return time;
	}
}
