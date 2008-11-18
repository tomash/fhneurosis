package thesis;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class ResultProcessor
{
    public static void countFFT(File input, int column, Neuron neuron)
        throws IOException
    {
        //String filename = String.format("neuron%1$02dfft.txt", this.getNnumber());
        //System.out.println("calculating and dumping FFT into file " + dirname + "/" + filename);

        LinkedList<Double> v_history = new LinkedList<Double>();

        System.out.println("loading results from file " + input.getPath());

        FileInputStream fstream = new FileInputStream(input);
        DataInputStream din = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(din));
        String strLine;
        int n = 0;
        System.out.println(br.readLine());
        while((strLine = br.readLine()) != null)
        {
            ++n;
            v_history.add(Double.valueOf(strLine.split("\\s")[column]));
        }

        double dt = neuron.getdt();
        double fn = (1/dt)*0.5;    //maksymalna czestotliwosc - Nyqist!
        double df = fn/n;    //interwal czestotliwosci
        double f=0;

        double[] arr = new double[n];
        int i=0;
        for(Iterator<Double> itr = v_history.iterator(); itr.hasNext();)
        {
            arr[i] = itr.next();
            ++i;
        }
        System.out.println("array rewritten, transforming...");
        DoubleFFT_1D transform = new DoubleFFT_1D(n);
        transform.realForward(arr);
        //falszujemy:
        arr[0] = 0;
        arr[1] = 0;
        arr[2] = 0;
        arr[3] = 0;
        arr[4] = 0;
        arr[5] = 0;
        System.out.println("transformed, saving...");
        //transform.realForward((double[])(Double[])v_history.toArray());

        PrintWriter outfile;
        FileWriter fw;
        String dirname = input.getParent();
        String filename = String.format("neuron%1$02dfft.txt", neuron.getNnumber());
        System.out.println("calculating and dumping FFT into file " + dirname + "/" + filename);
        try
        {
            new File(dirname).mkdirs();
            File currfile = new File(dirname, filename);
            currfile.createNewFile();

            fw = new FileWriter(currfile);
            outfile = new PrintWriter(fw);

            for (i = 0; i < n; ++i)
            {
                outfile.printf("%f\t%f \n", f, arr[i]*arr[i]);
                f += df;
            }
            outfile.close();
            System.out.println("everything successful, thank you :)");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
