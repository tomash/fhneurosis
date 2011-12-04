package thesis;

import java.io.IOException
import java.util.GregorianCalendar
import sun.security.x509.DeltaCRLIndicatorExtension
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger

Logger logger = Logger.getLogger(NeuralNetwork.class)
BasicConfigurator.configure()

public class ParameterAnalysis {
	public List rawResults
	public HashMap remappedResults
	public List d_array

	public HashMap reMap() {
		remappedResults = [:]

        int d_count = d_array.size()
		int n_count = rawResults[0][0].size()	//amount of neurons
		int sim_count = rawResults[0].size()	//amount of simulations for given params

		for(int n=0; n<n_count; ++n)	// over neurons
		{
			remappedResults[n] = []
			for(int c=0; c<sim_count; ++c)
	        {
				for(int i=0; i<d_count; i++)	//over D values
				{
					if(remappedResults[n][i] == null) { remappedResults[n][i] = [] }
					remappedResults[n][i].add(rawResults[i][c][n])
				}
	        }
		}
        

		return remappedResults
	}
}


//simulation set parameters
neuron_count = 1
phase_shift = 0.0
simulation_count = 2



def build_line_network()
{
	n = neuron_count
	phase = phase_shift
	Logger logger = Logger.getLogger(NeuralNetwork.class)
	def dirnames = []
    dirname = NeuralNetwork.generateDirName()
	dirnames.add(dirname)
	logger.info("Starting our simulation, results directory is:  " + dirname)
	logger.info("building line network with ${n} neurons and phase jump ${phase}")
	//logger.info("randomising phase by +/i 10%")
	//Random generator = new Random(new Date().getTime())
	
	nn = new NeuralNetwork(n, dirname)
	/*
	for(i in 1..(n-1))
	{
		randphas = phase_shift// * (0.9 + 0.2*generator.nextDouble())
		logger.info("randomised phase = ${randphas}")
		nn.getNeurons()[i-1].connected = nn.getNeurons()[i]; 
		nn.getNeurons()[i].setPhase(randphas*2*Math.PI*i)
	}
	*/
	return nn
}



def run_simulation()
{
	sc = simulation_count
	
	Logger logger = Logger.getLogger(NeuralNetwork.class)
	def snrs = []
	for(i in 1..sc)
	{
		nn = build_line_network()
		
		nn.run(2048, 8192*1)
		snr_hash = nn.getSNRhash()
		snrs.add(snr_hash)
	}
	
	return snrs
}

def sweep_over_d()
{
	Logger logger = Logger.getLogger(NeuralNetwork.class)
	
	D0 = 5E-6	//startowe
	D1 = 5E-5	//koncowe
	delta_d = 1E-6	//krok na poczatku
	
	n = simulation_count 	//ilosc symulacji

	def d_array = []
    def snr_packs = []
	
	props = new Properties()
	f = new FileInputStream("props.txt")
	props.load(f)
	f.close()
	
	for(d=D0; d<=D1; d += delta_d)
	{
		logger.info("changing D property to ${d}")
		//new D value saved
		props.setProperty("D", d.toString())
		f2 = new FileOutputStream("props.txt")
		props.store(f2, "wyjasnienia zmiennych w props_with_properties.txt")
		f2.close()
		logger.info("D value ${d} saved, running ${n} full simulations")
		
		d_array.add(d)
		// snr_pack: an array of SNRs
		run_simulation()
		
		//we don't need such dense data after D=1E-5
		if(d >= 1E-5)
		{
			delta_d = 5E-6
		}
	}
	return ["d_array": d_array]
}

def sweep_over_r()
{
	Logger logger = Logger.getLogger(NeuralNetwork.class)
	
	r0 = 0.01	//startowe
	r1 = 0.05	//koncowe
	delta_r = 0.01	//krok
	
	n = simulation_count 	//ilosc symulacji
	
	props = new Properties()
	f = new FileInputStream("props.txt")
	props.load(f)
	f.close()
	
	for(r=r0; r<=r1; r += delta_r)
	{
		logger.info("changing r property to ${r}")
		//new D value saved
		props.setProperty("r", r.toString())
		f2 = new FileOutputStream("props.txt")
		props.store(f2, "wyjasnienia zmiennych w props_with_properties.txt")
		f2.close()
		logger.info("r value ${r} saved, running ${n} full simulations")
		
		run_simulation()
	}
	return []
}


//the meat!
//println("SWEEPING OVER D, ${simulation_count} runs for every value")
//hash_with_results = sweep_over_d()

println("SWEEPING OVER r, ${simulation_count} runs for every value")
hash_with_results = sweep_over_r()
