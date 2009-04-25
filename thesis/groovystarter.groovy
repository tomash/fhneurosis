package thesis

import java.io.IOException
import java.util.GregorianCalendar

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger

Logger logger = Logger.getLogger(NeuralNetwork.class)
BasicConfigurator.configure()

def run_simulation_multi(n)
{
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
	
	
}