package thesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.emory.mathcs.jtransforms.fft.*;

public class Neuron
{
	private static final Logger logger = Logger.getLogger(Neuron.class);

    public Neuron(String dirname)
    {
    	BasicConfigurator.configure();

         eps = 0.005;
         a = 0.5;
         b = 0.15;
         d = 1.0;

         //skalowanie szumu
         D = 10E-5;
         sqrt2D = Math.sqrt(2.0*D);

         //skalowanie (mnoznik) pobudzenia periodycznego
         r = 0.4;
         //stala (odwrotnosc okresu) pobudzenia periodycznego
         beta = 15;

         //wrazliwosc na pobudzenie
         sens = 0.20;
         //przesuniecie fazowe
         phase = 0.0;

         //poziom odciecia od dolu
         low_cut_off = 0.4;

         //poziom splaszczenia od gory
         high_cut_off = 0.8;


         //szum, korelacja
         tc = 0.01;
         lam = 1/tc;
         eta = 0;
         deta = 0;

         //czas, krok czasowy, zmienna losowa do szumu
         t = 0.0;
         dt = 0.0025;
         xi = 0;

         //potencjal, relaksacja i ich przyrosty
         v=0;
         dv=0;
         w=0;
         dw=0;

         v_history = new LinkedList<Double>();
         long seed = new Date().getTime();
         logger.info("initializing Random with seed " + seed);
         generator = new Random(seed);
         //generator = new Random(1000);

         //System.out.println(generator.nextDouble());
         generator.nextDouble();

         this.dirname = dirname;
    }


    public void iterate()
    {
        //xi = Math.random();
        noiseGauss();
        //noiseRand();
        noiseOU();
        countdv();
        countdw();
        //v += connected.getv() * sens;
        if(connected != null)
        	v += connected.getv(nh-1) * sens;

        v += dv;

        //TODO: porzadna implementacja polaczonych neuronow (lista/kolejka)
        //v += connected.getv() * sens;

        w += dw;


        //v_history.add(v);
        memorize_v();
        t += dt;
        T += dt;
        if (t>q)
        	t -= q;
        //System.out.println(t);
        /* Runge-Kutta:
         * dy/dt = f(t,y), y(t0) = y0
         * k1 = f(tn,yn)
         * k2 = f(tn+0.5dt, yn+0.5dt*k1)
         * k3 = f(tn+0.5dt, yn+0.5dt*k2)
         * k4 = f(tn+dt, yn+dt*k3)
         * yn+1 = yn + (dt/6)*(k1+2k2+2k3+k4)
         *
         *
         */
        //System.out.println("V_history size:" + v_history.size());
    }

    public int round(int i)
    {
        int power = 2;
        while(i > power)
        {
            power *= 2;
        }

        return power;
    }

    public LinkedList<Double> flatten()
    {
        LinkedList<Double> v_history_flattened =  new LinkedList<Double>();
        double v;
        for(int j=0; j<v_history.size(); ++j)
        {
            v = v_history.get(j);
            if(v < low_cut_off)    //arbitralne 0.4!
                v_history_flattened.add(0.0);
            else if(v > high_cut_off)
                v_history_flattened.add(1.0);
            else
                v_history_flattened.add(v);
        }

        return v_history_flattened;
    }

    public void showFlattened()
    {
        LinkedList<Double> v_hist =  this.flatten();
        for(int j=0; j<v_hist.size(); ++j)
        {
            System.out.println(v_hist.get(j));
        }
    }


    public void dumpFFT(boolean cut_off)
    {
        String filename = String.format("neuron%1$02dfft.txt", this.getNnumber());
        System.out.println("calculating and dumping FFT into file " + dirname + "/" + filename);
        int n = round(v_history.size());

        double fn = (1/dt)*0.5;    //maksymalna czestotliwosc - Nyqist!
        double df = fn/n;    //interwal czestotliwosci
        double f=0;

        double[] arr = new double[n];
        int i=0;
        for(Iterator<Double> itr = v_history.iterator(); itr.hasNext();)
        {
            arr[i] = itr.next();
            if(cut_off)
            {
                if(arr[i] <= low_cut_off)
                {
                    //System.out.print("_");
                    arr[i] = 0.0;
                }
                else if (arr[i] > high_cut_off)
                {
                    //System.out.print("-");
                    arr[i] = 1.0;
                }
                else
                {
                    //System.out.print("^");
                }
            }
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

    public void saveProperties(String filename)
    {
        Properties props = new Properties();
        props.setProperty("eps", Double.toString(eps));
        props.setProperty("a", Double.toString(a));
        props.setProperty("b", Double.toString(b));
        props.setProperty("d", Double.toString(d));
        props.setProperty("D", Double.toString(D));
        props.setProperty("r", Double.toString(r));
        props.setProperty("beta", Double.toString(beta));
        props.setProperty("tc", Double.toString(tc));
        props.setProperty("dt", Double.toString(dt));


        //FileWriter fw;
        //PrintWriter outfile;
        FileOutputStream fos;
        try
        {
            fos = new FileOutputStream(filename);
            //fw = new FileWriter("props.txt");
            //outfile = new PrintWriter(fw);
            props.store(fos, "wartosci parametrow eksperymentu");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void loadProperties(String filename)
    {
        Properties props = new Properties();
        FileInputStream fis;
        try
        {
            fis = new FileInputStream(filename);
            props.load(fis);
            eps = new Double(props.getProperty("eps"));
            a = new Double(props.getProperty("a"));
            b = new Double(props.getProperty("b"));
            d = new Double(props.getProperty("d"));
            D = new Double(props.getProperty("D"));
            sqrt2D = Math.sqrt(2.0*D);
            //System.out.printf("D=%1f, sqrt2D=%1f" , D, sqrt2D);
            r = new Double(props.getProperty("r"));
            //beta = new Double(props.getProperty("beta"));
            q = new Double(props.getProperty("q"));
            beta = (2.0*Math.PI)/q;
            tc = new Double(props.getProperty("tc"));
            dt = new Double(props.getProperty("dt"));
            sqrtdt = Math.sqrt(dt);
            sens = new Double(props.getProperty("sens"));

            low_cut_off = new Double(props.getProperty("low_cut_off"));
            high_cut_off = new Double(props.getProperty("high_cut_off"));

            double c = new Double(props.getProperty("c"));
            double nh_double = (2*Math.PI*c) / (beta * dt);
            nh = (int)nh_double;
            if(nh_double > nh)
            {
                nh++;
            }
            //System.out.printf("[nrn] sens=%1f, c=%1f, nh_double=%1f, nh=%1d, dt=%1f, sqrt(dt)=%1f \n", sens, c, nh_double, nh, dt, sqrtdt);
            logger.info(String.format("sens=%1f, c=%1f, nh_double=%1f, nh=%1d, dt=%1f, D=%e", sens, c, nh_double, nh, dt, D));
//            nh = new Integer(props.getProperty("nh"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    public void copyProperties(String filename)
    {

    }


    private double countdv()
    {
        dv = (1/eps) * dt * (v * (v-a) * (1-v) - w - (sqrt2D*eta));
        return dv;
    }

    private double countdw()
    {
        dw = dt * (v - (d*w) - (b + r* Math.sin(beta * t + phase)));
        return dw;
    }

    public void fill_history()
    {
        logger.info("filling history with 0.0 for nh="+nh);
        for(int i=0; i< nh; i++)
        {
            v_history.add(0.0);
            if(v_history.size() > nh)
            {
                v_history.remove();
                System.out.println("WTF?");
            }
        }

    }

    private void memorize_v()
    {
        double removed=0.0;

        v_history.add(v);
        if(v_history.size() > nh)
        {
            removed = v_history.remove();
        }
        //System.out.printf("%1d: added %1f, removed %1f \n", nnumber, v, removed);
    }



    /*
    private double noiseRand()
    {
        //xi = Math.random();
        xi = (generator.nextDouble() * 2.0) - 1;
        return xi;
    }*/


    private double noiseGauss()
    {
        //xi = generator.nextGaussian();
        xi = generator.nextGaussian();// * Math.sqrt(2.0*D);
        return xi;
    }

    private double noiseOU()
    {
        //double rnd = generator.nextDouble();
    	//poprzednie - tylko skladnik 1-go rzedu
        //deta =  - (eta * lam * dt) + (lam * xi * dt);

    	deta = (lam * xi * dt) - (eta * lam * dt);

    	//mannella - palleschi
    	//poprawka rzedu 1/2
    	double y1 = generator.nextGaussian();
    	deta += (lam * y1 *  sqrtdt ) ;

        eta += deta;
        //xi = eta;
        return eta;
    }

    public double getdt()
    {
        return dt;
    }

    public double getv()
    {
        return v;
    }

    /**
     * @param n ilosc iteracji wstecz, jaka ma zwrocic
     * @return wartosc v (pot. czynnosciowego) n iteracji temu
     */
    public double getv(int n)
    {
    	double returned_v;
    	if(v_history.size() > 0)
    		returned_v = v_history.getFirst();
    	else
    		returned_v = v;


    	//obcinamy dla minimalizacji wzajemnego wzbudzenia
    	if(returned_v < low_cut_off)
    		returned_v = 0.0;


    	return returned_v;
    }

    public double get_flat_v()
    {
        if(this.v < low_cut_off)
            return 0.0;
        else if(this.v > high_cut_off)
            return 1.0;
        else
            return v;
    }

    public int getNnumber()
    {
        return nnumber;
    }


    public void setNnumber(int nnumber)
    {
        this.nnumber = nnumber;
    }

    //public PrintWriter outfile;
    private PrintWriter outfile;

    private String dirname;

    public void openOutFile()
    {
        FileWriter fw;
        try
        {
            //System.out.println(dirname);
            new File(dirname).mkdirs();
            File currfile = new File(dirname, String.format("neuron%1$02d.txt", this.getNnumber()));
            currfile.createNewFile();
            fw = new FileWriter(currfile);
            this.outfile = new PrintWriter(fw);
        }
        catch (IOException e) {    e.printStackTrace();
        }
    }

    public void closeOutFile()
    {
        this.outfile.close();
    }

    private Random generator;

    private double v;    //potencjal czynnosciowy
    private double dv;    //przyrost v
    private double w;    //relaksacja
    private double dw;    //przyrost v

    //wspolczynniki skalujace:
    private double eps;        //0.005
    private double a;    //0.5
    private double b;    //0.15
    private double d;    //1.0
    private double D;    //10^-5
    private double sqrt2D;    //sqrt(2D)
    private double r;
    private double beta; //15
    private double q; //15

    private int nnumber;

    private double deta;
    private double eta;
    private double tc;    //0.01 - czas korelacji
    private double lam;    //odwrotnosc czasu korelacji

    private double t;    //aktualna chwila, mod T
    private double T;    //aktualna chwila
    private double dt;    //skok czasowy... 0.0025?
    private double sqrtdt;    //pierwiastek z dt
    private double xi;    //szum

    private double low_cut_off;    //poziom odciecia v od dolu przy zapisie
    private double high_cut_off;    //poziom splaszczenia v od gory przy zapisie

    private int nh;    //ilosc iteracji trzymanych w historii

    private double phase;

    private LinkedList<Double> v_history;

    public double getT()
    {
        return T;
    }

    public double gett()
    {
        return t;
    }

    public Neuron connected;
    private double sens;    //sensitivity - wrazliwosc na bodzce

    public double getPeriodic()
    {
        return r * Math.sin(beta * t + phase);
    }


    public double getPhase() {
        return phase;
    }


    public void setPhase(double phase) {
        this.phase = phase;
    }






}
