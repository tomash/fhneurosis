package thesis

import java.io.IOException
import java.util.GregorianCalendar
import sun.security.x509.DeltaCRLIndicatorExtension
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger

Logger logger = Logger.getLogger(NeuralNetwork.class)
BasicConfigurator.configure()

public class GroovyStarter {
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
neuron_count = 2
phase_shift = 0.111
simulation_count = 40


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
	for(i in 1..(n-1))
	{
		randphas = phase_shift// * (0.9 + 0.2*generator.nextDouble())
		logger.info("randomised phase = ${randphas}")
		nn.getNeurons()[i-1].connected = nn.getNeurons()[i]; 
		nn.getNeurons()[i].setPhase(randphas*2*Math.PI*i)
	}
	return nn
}



def run_simulation_multi()
{
	sc = simulation_count
	
	Logger logger = Logger.getLogger(NeuralNetwork.class)
	def snrs = []
	for(i in 1..sc)
	{
		nn = build_line_network()
		
		nn.run(2048, 8192*32)
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
		snr_pack = run_simulation_multi()
		snr_packs.add(snr_pack)
		
		//we don't need such dense data after D=1E-5
		if(d >= 1E-5)
		{
			delta_d = 5E-6
		}
	}
	return ["d_array": d_array, "snr_packs_array": snr_packs]
}

def sweep_over_c(phas)
{
	Logger logger = Logger.getLogger(NeuralNetwork.class)
	
	c0 = 0.0	//start at
	c1 = 2*phas	//end at
	delta_c = 0.1

	props = new Properties()
	f = new FileInputStream("props.txt")
	props.load(f)
	f.close()
	
	for(c=c0; c<=c1; c += delta_c)
	{
		logger.info("changing c property to ${c}")
		//new D value saved
		props.setProperty("c", c.toString())
		f2 = new FileOutputStream("props.txt")
		props.store(f2, "wyjasnienia zmiennych w props_with_properties.txt")
		f2.close()
		logger.info("c value ${c} saved, running sweep_over_d")
		
		logger.info("SWEEP OVER D START with c=${c}")
		hash_with_results = sweep_over_d(phas)
		println format_results(hash_with_results)
		logger.info("SWEEP OVER D FINISH with c=${c}")
	}

	
	return 0
}

def format_results(results_hash)
{
	s="#D: \n"
	for(d in results_hash["d_array"])
	{
		s+=String.format("%.6f\t", d)
	}
	s+="\n"
	
	d_count = results_hash["d_array"].size();
	n_count = results_hash["snr_packs_array"][0][0].size();	//amount of neurons
	sim_count = results_hash["snr_packs_array"][0].size();	//amount of simulations for given params

	
	for(n=0; n<n_count; ++n)	// over neurons
	{
		s+=String.format("NEURON %d HERE\n", n);
		for(c=0; c<sim_count; ++c)
        {
			for(i=0; i<d_count; i++)	//over D values
			{
				snr = results_hash["snr_packs_array"][i][c][n];
				s+=String.format("%.6f\t", snr )
			}
			s+="\n"
        }

		
	}
	
	return s
	
}
//the meat!
println("STARTING SWEEP with phas = ${phase_shift}")
hash_with_results = sweep_over_d()
println("FINISHED SWEEP with phas = ${phase_shift}")
println format_results(hash_with_results)

//println "2010-07-12 2 neurons, 20 simulations, sweeping over c (delay)"
//println("STARTING SWEEP with phas = +0.5")
//hash_with_results = sweep_over_d(0.5)
//sweep_over_c(0.5)
//println("FINISHED SWEEP with phas = +0.5")
//println format_results(hash_with_results)
