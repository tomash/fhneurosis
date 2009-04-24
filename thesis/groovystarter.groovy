package thesis

import java.io.IOException
import java.util.GregorianCalendar

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger

Logger logger = Logger.getLogger(NeuralNetwork.class)
BasicConfigurator.configure()

def snrs_from_neuron0 = []
for(i in 0..3)
{
	dirname = Starter.generateDirName();
	logger.info("Starting our simulation, results directory is:  " + dirname)
	nn = new NeuralNetwork(2, dirname)
	nn.run(2048, 8192*2)
	snr_hash = nn.getSNRhash()
	snrs_from_neuron0.add(snr_hash[0])
}

println "Completed!"
println snrs_from_neuron0