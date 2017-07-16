package Server;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FFT {
	
	private int length; // must be a power of 2
	private int levels;
	private double[] real, imag;
	private double[] cosTable, sinTable;
	double[] amplitudes;
	double samplingRate; // in Hz
	
	// Constructor
	public FFT(double[] real, double[] imag){
		this.real = real;
		this.imag = imag;
		this.length = real.length;
		if (real.length != imag.length)
			throw new IllegalArgumentException("Length of input arrays is not equal.");
		this.levels = 31 - Integer.numberOfLeadingZeros(length); // Equal to floor(log2(n))
		if (1 << levels != length)
			throw new IllegalArgumentException("Length is not a power of 2.");
		this.cosTable = new double[length / 2];
		this.sinTable = new double[length / 2];
		for (int i = 0; i < length / 2; i++) {
			cosTable[i] = Math.cos(2 * Math.PI * i / length);
			sinTable[i] = Math.sin(2 * Math.PI * i / length);
		}
		this.samplingRate = length; // default sampling cycle is 1 second
	}
	
	public void setSamplingRate(double rate){
		this.samplingRate = rate;
	}
	
	public void printComplex(){
		for (int i=0; i<length; i++){
			if (Math.abs(real[i]) < 0.000001)
				System.out.print(0.0 + "\t");
			else
				System.out.print(real[i] + "\t");
			if (Math.abs(imag[i]) < 0.000001)
				System.out.println(0.0);
			else
				System.out.println(imag[i]);
		}
	}
	
	public void printReal(){
		System.out.println("Real part of the samples:");
		Locale.setDefault(Locale.GERMAN);
		DecimalFormat df = new DecimalFormat("#0.00000");
		for (int i=1; i<length; i++)
			System.out.println(df.format(real[i]));
	}
	
	public void printAmplitude(){
		System.out.println("Frequency Domain:");
		System.out.println("freq\tamp");
		Locale.setDefault(Locale.GERMAN);
		DecimalFormat df = new DecimalFormat("#0.00000");
		for (int i=1; i<length*0.5; i++){
			double amp = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]) / (0.5 * length);
			System.out.println(df.format(this.samplingRate / this.length * i) + "\t" + df.format(amp));
		}
	}
	
	public double getMaxAmplitude(){
		amplitudes = new double[(int) (length * 0.5)];
		for (int i = 1; i < length * 0.5; i++){
			double amp = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]) / (0.5 * length);
			amplitudes[i-1] = amp;
		}
		double maxAmp = amplitudes[0];
		for (int i=1; i < length * 0.5; i++){
			if(amplitudes[i] > maxAmp){
				maxAmp = amplitudes[i];
			}
		}
		return maxAmp;
	}
	
	/* 
	 * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
	 * The vector's length must be a power of 2. Uses the Cooley-Tukey decimation-in-time radix-2 algorithm.
	 */
	public void transform() {
		
		// Bit-reversed addressing permutation
		for (int i = 0; i < length; i++) {
			int j = Integer.reverse(i) >>> (32 - levels);
			if (j > i) {
				double temp = real[i];
				real[i] = real[j];
				real[j] = temp;
				temp = imag[i];
				imag[i] = imag[j];
				imag[j] = temp;
			}
		}
		
		// Cooley-Tukey decimation-in-time radix-2 FFT
		for (int size = 2; size <= length; size *= 2) {
			int halfsize = size / 2;
			int tablestep = length / size;
			for (int i = 0; i < length; i += size) {
				for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
					double tpre =  real[j+halfsize] * cosTable[k] + imag[j+halfsize] * sinTable[k];
					double tpim = -real[j+halfsize] * sinTable[k] + imag[j+halfsize] * cosTable[k];
					real[j + halfsize] = real[j] - tpre;
					imag[j + halfsize] = imag[j] - tpim;
					real[j] += tpre;
					imag[j] += tpim;
				}
			}
			if (size == length)  // Prevent overflow in 'size *= 2'
				break;
		}
	}


}