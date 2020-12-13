package driver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import index.Index;
import index.InvertedIndex;

public class MakePrior {
	
	public static void main(String[] args) {
		try {
			Index index = new InvertedIndex();
	        index.load(false);
	        int n = index.getDocCount();
            RandomAccessFile binaryFile = new RandomAccessFile("uniform.prior", "rw");
            // write the same uniform probability for all documents
            double uniform = Math.log(1.0 / n);
            for (int i = 0; i <= n; i++) {
                binaryFile.writeDouble(uniform);
            }
            binaryFile.close();

            // now the ransom-distribution ones
            Random random = new Random(1000);
            double[] rand = new double[n+1];
            double randSum = 0.0;
            rand[0]=0.0;
            for (int i = 1; i <=n; i++) {
                rand[i] = random.nextDouble();
                randSum += rand[i];
            }

            // normalize the random-priors so that they sum to 1
            for (int i = 1; i <=n; i++) {
                rand[i] /= randSum;

                // move from probability-space to log-space
                rand[i] = Math.log(rand[i]);
            }

            binaryFile = new RandomAccessFile("random.prior", "rw");
            for (int i = 0; i <=n; i++) {
                binaryFile.writeDouble(rand[i]);
            }

            binaryFile.close();
        } 
		catch (FileNotFoundException e) 
		{
            e.printStackTrace();
        } 
		catch (IOException e) 
		{
            e.printStackTrace();
        }
    }

}


