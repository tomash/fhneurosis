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
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            neurons[i].loadProperties("props.txt");
            neurons[i].setNnumber(i);
            neurons[i].fill_history();
            //System.out.println(neurons[i].toString());
        }
        neurons[1].setPhase(Math.PI/1.0);

        out = new PrintWriter(System.out);

        //dirrty hack!
        neurons[0].connected = neurons[1];
        neurons[1].connected = neurons[0];


    }

    public int run(int iterations)
    {
        for (int i = 0; i < iterations; ++i)
        {
            out.printf("%f", neurons[0].getT());
            for (int j = 0; j < neurons.length; ++j)
            {
                neurons[j].iterate();
                out.printf("\t%f", neurons[j].getv());
                out.printf("\t%f", neurons[j].get_flat_v());

            }
            out.printf("\t%f", neurons[0].getPeriodic());
            out.printf("\t%f", neurons[1].getPeriodic());
            out.print("\n");
            if(i%1000==0)
            {
                System.out.print(".");
            }
        }
        System.out.println();
        out.close();
        //this.dumpFFTs();
        try
        {
            ResultProcessor.countFFT(new File(dirname, "neurons.txt"), 1, neurons[0]);
            ResultProcessor.countFFT(new File(dirname, "neurons.txt"), 2, neurons[1]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public int dumpFFTs()
    {

        neurons[0].dumpFFT(true);
        neurons[1].dumpFFT(true);

        return 0;
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
