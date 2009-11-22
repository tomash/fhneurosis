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
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class ResultProcessor
{
	private static final Logger logger = Logger.getLogger(ResultProcessor.class);

	public static void main(String[] args) throws IOException, InterruptedException
	{
		if(args.length < 3)
		{
			System.out.println("syntax: ResulProcessor dirname column neuron_number");
			return;
		}
		BasicConfigurator.configure();
		logger.info("Loading properties and config");
		String dirname = args[0];
		int column = new Integer(args[1]);
		int neuron_number = new Integer(args[2]);
		File results = new File(dirname, "neurons.txt");
		File props_file = new File(dirname, "props.txt");

		Properties props = new Properties();
		props.load(new FileInputStream(props_file));
		double dt = new Double(props.getProperty("dt"));
		double D = new Double(props.getProperty("D"));

		logger.info(String.format("loaded properties: dirname %s, column %d, neuron_number %d, dt= %f, D= %g", dirname, column, neuron_number, dt, D));
		logger.info("counting FFT");
		String fft_filename = countFFT(results, column, dt, neuron_number);
		logger.info("counting SNR");
		ResultProcessor.countSNR(new File(dirname, fft_filename));
		System.exit(0);
	}

	public static String countFFT(File input, int column, Neuron neuron)
		throws IOException
	{
		return countFFT(input, column, neuron.getdt(), neuron.getNnumber());
	}

    public static String countFFT(File input, int column, double dt, int neuron_number)
        throws IOException
    {
        LinkedList<Double> v_history = new LinkedList<Double>();

        logger.info(String.format("[FFT] loading results from file %s",input.getPath()));

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
        logger.info("[FFT] transforming... " + n);
        DoubleFFT_1D transform = new DoubleFFT_1D(n);
        transform.realForward(arr);
        //falszujemy:
        arr[0] = 0;
        arr[1] = 0;
        arr[2] = 0;
        arr[3] = 0;
        arr[4] = 0;
        arr[5] = 0;
        logger.info("[FFT] saving... ");
        //transform.realForward((double[])(Double[])v_history.toArray());

        PrintWriter outfile;
        FileWriter fw;
        String dirname = input.getParent();
        String filename = String.format("neuron_%1$02d_%2$02d_fft.txt", neuron_number, column);
        logger.info(String.format("[FFT] calculating and dumping FFT into file %s/%s", dirname, filename));
        try
        {
            new File(dirname).mkdirs();
            File currfile = new File(dirname, filename);
            currfile.createNewFile();

            fw = new FileWriter(currfile);
            outfile = new PrintWriter(fw);

            for (i = 0; i < n; ++i)
            {
                outfile.printf(Locale.US, "%f\t%f \n", f, arr[i]*arr[i]);
                f += df;
            }
            outfile.close();
            logger.info("[FFT] completed & closing");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //return String.format("%s/%s", dirname, filename);
        return filename;

    }

    public static double countSNR(File input)
    	throws IOException
    {
    	LinkedList<Double> v_fft = new LinkedList<Double>();
    	LinkedList<Double> v_f = new LinkedList<Double>();

        logger.info(String.format("[SNR] loading FFT from file %s", input.getPath()));

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
        logger.info("[SNR] loaded, processing...");

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

        logger.info(String.format("[SNR] index of max: %1d ; value at max: %1f (for f=%1f)", index_of_max, max, v_f.get(index_of_max)));

        //wychodzimy jesli index_of_max=0 (czyli nie zostal znaleziony) -- znaczy mamy pusty przebieg
        if(index_of_max == 0)
        	return 0.0;
        
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
        double snr = (v_fft.get(index_of_max)/mean);
        logger.info(String.format("[SNR] taken %1d values around; mean= %1f; SNR= %f", count, mean, snr));

        //System.out.println("[SNR] indexof max: " + index_of_max);
        //System.out.println("[SNR] value at max: " + v_fft.get(index_of_max));
        return snr;


    }

}
