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

        System.out.print("\n[FFT] loading results from file " + input.getPath() +" ... ");

        FileInputStream fstream = new FileInputStream(input);
        DataInputStream din = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(din));
        String strLine;
        int n = 0;
        //System.out.println(br.readLine());
        br.readLine();
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
        System.out.print(" transforming... ");
        System.out.println(n);
        DoubleFFT_1D transform = new DoubleFFT_1D(n);
        transform.realForward(arr);
        //falszujemy:
        arr[0] = 0;
        arr[1] = 0;
        arr[2] = 0;
        arr[3] = 0;
        arr[4] = 0;
        arr[5] = 0;
        System.out.print(" saving... ");
        //transform.realForward((double[])(Double[])v_history.toArray());

        PrintWriter outfile;
        FileWriter fw;
        String dirname = input.getParent();
        String filename = String.format("neuron%1$02dfft.txt", neuron.getNnumber());
        System.out.print("\n[FFT] calculating and dumping FFT into file " + dirname + "/" + filename + " ... ");
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
            System.out.print(" completed & closing...\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void countSNR(File input)
    	throws IOException
    {
    	LinkedList<Double> v_fft = new LinkedList<Double>();
    	LinkedList<Double> v_f = new LinkedList<Double>();

        System.out.print("\n[SNR] loading FFT from file " + input.getPath());

        FileInputStream fstream = new FileInputStream(input);
        DataInputStream din = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(din));
        String strLine;
        int n = 0;
        while((strLine = br.readLine()) != null)
        {
            ++n;
            v_f.add(Double.valueOf(strLine.split("\\s")[0]));
            v_fft.add(Double.valueOf(strLine.split("\\s")[1]));
        }
        System.out.print(" ... processing");

        int index_of_max = 0;
        double max = 0.0;
        double curr = 0.0;

        //szukamy maksimum FFT - czyli "sygnalu" do SNR
        for(Iterator<Double> itr = v_fft.iterator(); itr.hasNext();)
        {
        	curr = itr.next();
        	if(curr > max)
        	{
        		max=curr;
        		index_of_max = v_fft.indexOf(curr);
        	}
        }
        System.out.print(" ... done \n");

        System.out.printf("[SNR] index of max: %1d ; value at max: %1f (for f=%1f)\n", index_of_max, max, v_f.get(index_of_max));

        double around = 0.0;
        int count = 0;
        for(int i=index_of_max-5-1; i<index_of_max-1; ++i)
        {
        	around += v_fft.get(i);
        	++count;
        }
        for(int i=index_of_max+5+1; i>index_of_max+1; --i)
        {
        	around += v_fft.get(i);
        	++count;
        }
        double mean = (around/count);
        System.out.printf("[SNR] taken %1d values around; mean= %1f; SNR= %f \n", count, mean, (v_fft.get(index_of_max)/mean));

        //System.out.println("[SNR] indexof max: " + index_of_max);
        //System.out.println("[SNR] value at max: " + v_fft.get(index_of_max));



    }

}
