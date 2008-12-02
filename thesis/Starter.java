package thesis;

import java.io.IOException;
import java.util.GregorianCalendar;

/**
 * Klasa - matka calego programu. Sluzy tylko do stworzenia
 * StartWindow. W przyslosci zapewne bedzie odpowiedzialna
 * za kolejne ladowanie klas i ewentualny progressbar.
 */
public class Starter {

    public static void main(String[] args) throws IOException, InterruptedException {

        GregorianCalendar gc = new GregorianCalendar();
        String dirname = String.format("%1$tF_%1$tH%1$tM%1$tS", gc);

        NeuralNetwork nn = new NeuralNetwork(4, dirname);
        nn.openOutFile();
        nn.run(16384*2);

        //n.dumpFFT();
        //n.saveProperties("props.txt");
    }
}
