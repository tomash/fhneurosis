package thesis

import java.io.IOException
import java.util.GregorianCalendar
import sun.security.x509.DeltaCRLIndicatorExtension
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger

Logger logger = Logger.getLogger(NeuralNetwork.class)
BasicConfigurator.configure()


def run_simulation_multi(n)
{
	Logger logger = Logger.getLogger(NeuralNetwork.class)
	def snrs_from_neuron0 = []
	def dirnames = []
	for(i in 1..n)
	{
		dirname = NeuralNetwork.generateDirName()
		dirnames.add(dirname)
		logger.info("Starting our simulation, results directory is:  " + dirname)
		nn = new NeuralNetwork(2, dirname)
		nn.run(2048, 8192*2)
		snr_hash = nn.getSNRhash()
		snrs_from_neuron0.add(snr_hash[0])
	}
	
	return snrs_from_neuron0
}

def sweep_over_d()
{
	Logger logger = Logger.getLogger(NeuralNetwork.class)
	
	D0 = 1E-6	//startowe
	D1 = 5E-5	//koncowe
	delta_d = 1E-6	//krok na poczatku
	n = 30	//ilosc symulacji

	def d_array = []
	def snrs_neuron0_array = []
	
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
		snrs = run_simulation_multi(n)
		snrs_neuron0_array.add(snrs)
		
		//we don't need such dense data after D=1E-5
		if(d >= 1E-5)
		{
			delta_d = 5E-6
		}
	}
	return ["d_array": d_array, "snrs_array": snrs_neuron0_array]
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
	n_count = results_hash["snrs_array"][0].size();
	for(j=0; j<n_count; j++)
	{
		for(i=0; i<d_count; i++)
		{
			s+=String.format("%.6f\t", results_hash['snrs_array'][i][j])
		}
		s+="\n"
	}
	return s
	
}
//the meat!
hash_with_results = sweep_over_d()


println format_results(hash_with_results)
