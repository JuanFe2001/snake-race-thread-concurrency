package edu.eci.arsw.primefinder;

import java.util.Scanner;

public class Control extends Thread {

	private final static int NTHREADS = 3;
	private final static int MAXVALUE = 30000000;
	private final static int TMILISECONDS = 5000;

	private final int NDATA = MAXVALUE / NTHREADS;

	private PrimeFinderThread pft[];
	private boolean paused;

	private Control() {
		super();
		this.pft = new PrimeFinderThread[NTHREADS];

		int i;
		for (i = 0; i < NTHREADS - 1; i++) {
			PrimeFinderThread elem = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA);
			pft[i] = elem;
		}
		pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1);
	}

	public static Control newControl() {
		return new Control();
	}

	public synchronized void pauseThreads() {
		paused = true;
		for (PrimeFinderThread thread : pft) {
			thread.pauseThread();
		}
	}

	public synchronized void resumeThreads() {
		paused = false;
		notifyAll();
		for (PrimeFinderThread thread : pft) {
			thread.resumeThread();
		}
	}

	@Override
	public void run() {
		for (int i = 0; i < NTHREADS; i++) {
			pft[i].start();
		}

		Scanner scanner = new Scanner(System.in);
		while (true) {
			try {
				Thread.sleep(TMILISECONDS);
				pauseThreads();
				for (PrimeFinderThread thread : pft) {
					System.out.println("Hilo " + thread.getName() + ": " + thread.getPrimes().size());
				}

				System.out.println("PRESIONA ENTER PARA CONTINUAR...");
				scanner.nextLine();

				resumeThreads();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
