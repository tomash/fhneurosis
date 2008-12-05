package thesis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class NeuralNetwork
{
    public NeuralNetwork(int count, String dirname)
    {
        neurons = new Neuron[count];

        this.dirname = dirname;
        new File(dirname).mkdirs();
        String outpath = this.dirname + "/" + "props.txt";
        try
        {
            FileUtils.copyFile(new File("props.txt"), new File(outpath));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        for(int i=0; i<count; i++)
        {
            neurons[i] = new Neuron(dirname);
            try {
                Thread.sleep(000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            neurons[i].loadProperties("props.txt");
            neurons[i].setNnumber(i);
            neurons[i].fill_history();
            //System.out.println(neurons[i].toString());
        }
        //neurons[1].setPhase(Math.PI/1.0);

        out = new PrintWriter(System.out);

        //dirrty hack!
        neurons[0].connected = neurons[1];
        for(int i=1; i<neurons.length; i++)
        {
        	neurons[i].connected = neurons[0];
        }


    }

    public int run(int iterations)
    {
    	System.out.print("[sim] ");
        for (int i = 0; i < iterations; ++i)
        {
            out.printf("%f", neurons[0].getT());
            for (int j = 0; j < neurons.length; ++j)
            {
                neurons[j].iterate();

                out.printf("\t%f", neurons[j].getv());
                out.printf("\t%f", neurons[j].get_flat_v());
            }
            for (int j = 0; j < neurons.length; ++j)
            {
            	out.printf("\t%f", neurons[0].getPeriodic());
            }
            out.print("\n");
            if(i%1000==0)
            {
                System.out.print(".");
            }
            //System.out.printf("T: %f, t: %f, sin: %f \n", neurons[0].getT(), neurons[0].gett(), neurons[0].getPeriodic());
        }
        System.out.println();
        //System.out.printf("END TIMES: t1: %f, t2: %f", neurons[0].getT(), neurons[1].getT());
        out.close();
        //this.dumpFFTs();
        this.dump_fft_and_snr();

        return 0;
    }

    public void dump_fft_and_snr()
    {
    	try
        {
        	for (int j = 0; j < neurons.length; ++j)
            {
        		ResultProcessor.countFFT(new File(dirname, "neurons.txt"), (2*j)+1, neurons[j]);
            }
        	for (int j = 0; j < neurons.length; ++j)
            {
        		ResultProcessor.countSNR(new File(dirname, "neuron0"+j+"fft.txt"));
            }
            //dla 16384 iteracji: 0.507813
            //dla 4x16384 (65536) iteracji: 0.501953

        }
        catch(Exception e)
        {
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
