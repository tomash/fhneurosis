package thesis;

//import java.awt.Component;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

public class NeuronSimRunnable implements Runnable {

	public NeuronSimRunnable(Neuron n) {
		neuron = n;
		//drawing = aComponent;
		//speed = doubleNet.getSpeed();
	}

	public void run() {
		try {
			while (true) {
				try {
					EventQueue.invokeAndWait(new Runnable() {
						public void run() {
							for (int i = 0; i < 1000; i++) {
								neuron.iterate();
							}
						}
					});
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Thread.sleep(doubleNet.getDelay(), 1);
				Thread.sleep(0, 1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	private Neuron neuron;
	//private Component drawing;
	public static final int STEPS = 1000;

}
