package mandelbrotSet;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MandelbrotSet {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		int input = vidOrPic();
		if (input == 1) {
			Inputs a1 = new Inputs();
			a1.scanHeight();
			a1.scanMax();
			long start = System.nanoTime();
			MakePicture set = new MakePicture(a1.getWidth(), a1.getHeight(), a1.getMax());
			set.start();
			System.out.println((System.nanoTime() - start) / 1000000000 + " seconds");
		} else if (input == 2) {
			Inputs a1 = new Inputs();
			a1.scanHeight();
			a1.scanMax();
			a1.scanTime();
			long start = System.nanoTime();
			MakeZoom set;
			set = new MakeZoom(a1.getWidth(), a1.getHeight(), a1.getMax(), a1.getTime());
			set.start();
			System.out.println((System.nanoTime() - start) / 1000000000 + " seconds");
		} else {
			System.out.println("Input must be a 1 or a 2");
		}
	}

	private static int vidOrPic() {
		int input = 0;
		System.out.println("Press 1 for picture and 2 for a video");
		Scanner scan1 = new Scanner(System.in);
		try {
			input = scan1.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("Input must be an integer.");
			return vidOrPic();
		}
		if (input != 1 && input != 2) {
			return vidOrPic();
		}
		return input;
	}
}