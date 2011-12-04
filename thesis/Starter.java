package thesis;

import java.io.IOException;
import java.util.GregorianCalendar;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Klasa - matka calego programu. Sluzy tylko do uruchomienia
 * i zaladowania oraz utworzenia podstawowej konfiguracji.
 */
public class Starter {

	private static final Logger logger = Logger.getLogger(NeuralNetwork.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        BasicConfigurator.configure();

        String dirname = NeuralNetwork.generateDirName();
        logger.info("Starting our simulation, results directory is:  " + dirname);

        NeuralNetwork nn = new NeuralNetwork(1, dirname);
        nn.run(2048, 8192*4);
    }
    
    
}
