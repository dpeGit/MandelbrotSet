package mandelbrotSet;

import java.util.concurrent.Callable;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

public class MandelbrotMath implements Callable<int[]> {

	private int m, col, row;
	private Apfloat r, i;

	public MandelbrotMath(int m, Apfloat r, Apfloat i, int col, int row) {
		this.m = m;
		this.r = r;
		this.i = i;
		this.col = col;
		this.row = row;
	}

	@Override
	public int[] call() throws Exception {
		int[] pixel = new int[3];
		pixel[0] = calcPixel();
		pixel[1] = col;
		pixel[2] = row;
		return pixel;
	}

	private int calcPixel() {
		Apfloat xNew;
		Apfloat x = new Apfloat(0, 100), y = new Apfloat(0, 100);
		int iteration = 0;
		Apfloat xSqr = new Apfloat(0, 100);
		Apfloat ySqr = new Apfloat(0, 100);
		while ((xSqr.add(ySqr)).compareTo(new Apfloat(4)) < 0 && iteration < m) {
			xNew = xSqr.subtract(ySqr).add(r);
			y = x.multiply(new Apfloat(2)).multiply(y).add(i);
			x = xNew;
			xSqr = ApfloatMath.pow(x, 2);
			ySqr = ApfloatMath.pow(y, 2);
			iteration++;
		}
		if (iteration < m) {
			return iteration;
		} else {
			return 0;
		}
	}

}
