package thesis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class NeuralNetwork
{
	private static final Logger logger = Logger.getLogger(NeuralNetwork.class);

    public NeuralNetwork(int count, String dirname)
    {
    	logger.debug("initializing " + count + "neurons");
        neurons = new Neuron[count];

        this.dirname = dirname;
        new File(dirname).mkdirs();
        String outpath = this.dirname + "/" + "props.txt";
        logger.debug("Copying props.txt config file to destination directory");
        try
        {
            FileUtils.copyFile(new File("props.txt"), new File(outpath));
        }
        catch(Exception e)
        {
        	logger.error("cannot copy props.txt file!");
            e.printStackTrace();
        }

        logger.debug("initializing " + count + "neurons");
        for(int i=0; i<count; i++)
        {
            neurons[i] = new Neuron(dirname);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            neurons[i].loadProperties("props.txt");
            neurons[i].setNnumber(i);
            neurons[i].fill_history();
        }

        out = new PrintWriter(System.out);

        logger.info("preparing output file for results");
        this.openOutFile();

        logger.debug("connecting neurons");
        //dirrty hack!
        if(neurons.length > 1)
        {
        	neurons[0].connected = neurons[1];
        	//neurons[1].connected = neurons[0];
        	for(int i=1; i<neurons.length; i++)
            {
             	neurons[i].connected = neurons[0];
            }
        }



    }

    public int run(int dry_run, int iterations)
    {
    	int i = 0;
    	logger.info(dry_run + " runs without saving...");
    	for (i = 0; i < dry_run; ++i)
    	{
    		for (int j = 0; j < neurons.length; ++j)
            {
                neurons[j].iterate();
            }
    	}

    	logger.info(iterations + " real runs now! ");
        for (i=0; i < iterations; ++i)
        {
            out.printf("%f", neurons[0].getT());
            for (int j = 0; j < neurons.length; ++j)
            {
                neurons[j].iterate();

                out.printf("\t%f", neurons[j].getv());
                out.printf("\t%f", neurons[j].get_flat_v());
                out.printf("\t%f", neurons[j].getPeriodic());
            }
            for (int j = 0; j < neurons.length; ++j)
            {
           		//out.printf("\t%f", neurons[0].getPeriodic());
            }
            out.print("\n");
            if(i%1000==0)
            {
                System.out.print(".");
            }
            //System.out.printf("T: %f, t: %f, sin: %f \n", neurons[0].getT(), neurons[0].gett(), neurons[0].getPeriodic());
        }
        System.out.print("\n");
        //System.out.printf("END TIMES: t1: %f, t2: %f", neurons[0].getT(), neurons[1].getT());
        out.close();
        //this.dumpFFTs();
        logger.info("dumping FFT and calculating SNR");
        this.dump_fft_and_snr();

        return 0;
    }

    public void dump_fft_and_snr()
    {
    	int column_number;
    	try
        {
        	for (int j = 0; j < neurons.length; ++j)
            {
        		column_number = (3*j)+2;
        		logger.debug("calculating FFT for neuron "+j+ " by column number "+column_number);
        		ResultProcessor.countFFT(new File(dirname, "neurons.txt"), column_number, neurons[j]);
            }
        	for (int j = 0; j < neurons.length; ++j)
            {
        		logger.debug("calculating SNR for neuron "+j);
        		ResultProcessor.countSNR(new File(dirname, "neuron0"+j+"fft.txt"));
            }
            //dla 16384 iteracji: 0.507813
            //dla 4x16384 (65536) iteracji: 0.501953

        }
        catch(Exception e)
        {
        	logger.error("error while calculating FFT and SNR!");
            e.printStackTrace();
        }
    }

    public void openOutFile()
    {
        FileWriter fw;
        try
        {
            new File(dirname).mkdirs();
            File currfile = new File(dirname, "neurons.txt");
            currfile.createNewFile();
            fw = new FileWriter(currfile);
            this.out = new PrintWriter(fw);
            this.saveHeader();
        }
        catch (IOException e) {    e.printStackTrace();
        }
    }

    public void saveHeader()
    {
        out.printf("# T\t", neurons[0].getT());
        for (int j = 0; j < neurons.length; ++j)
        {
            out.printf("\tV(%d)", j);
            out.printf("\tV_flat(%d)", j);
            out.printf("\tperiodic(%d)", j);
        }
        out.printf("\n");

    }

    public void closeOutFile()
    {
        this.out.close();
    }

    private Neuron[] neurons;
    private PrintWriter out;

    private String dirname;

}
