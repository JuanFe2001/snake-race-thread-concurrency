package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.*;
import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];

    private AtomicInteger longestAtLifeSnake;
    private AtomicInteger worstDeathSnake;
    JLabel JLabellongestAtLifeSnake;
    JLabel JLabelworstDeathSnake;
    private JButton resume;
    private JButton stop;
    private JButton reset;

    public SnakeApp() {
        worstDeathSnake = new AtomicInteger();
        worstDeathSnake.set(-1);
        longestAtLifeSnake = new AtomicInteger();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 40);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        board = new Board();
        
        frame.add(board, BorderLayout.CENTER);
          frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout());

        JLabellongestAtLifeSnake = new JLabel("LONGEST:");
        JLabelworstDeathSnake = new JLabel("WORST:");
        resume = new JButton("resume");
        stop = new JButton("stop");
        reset = new JButton("reset");
        stop.setEnabled(true);
        resume.setEnabled(false);
        // Panel para la información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1)); // Dos filas, una para cada etiqueta

        // Etiquetas de información
        infoPanel.add(JLabelworstDeathSnake);
        infoPanel.add(JLabellongestAtLifeSnake);

        // Panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Otra disposición para los botones

        // Botones
        buttonPanel.add(resume);
        buttonPanel.add(stop);
        buttonPanel.add(reset);

        actionsPanel.add(infoPanel);
        actionsPanel.add(buttonPanel); 

   
        
        frame.add(actionsPanel, BorderLayout.SOUTH);
        
  
        // frame.add(JLabellongestAtLifeSnake, BorderLayout.NORTH);
        prepareButtons();
  


    }
    
   
    private void actualizeData() {
        String text = "Longest sizes snakes: ";
        for (int i = 0; i != MAX_THREADS; i++) {
            if (snakes[i].getBody().size() == longestAtLifeSnake.get()) {
                text += "SNAKE: " + snakes[i].getIdt() + " SIZE: = " + longestAtLifeSnake.get() + " ";
            }
        }
        JLabellongestAtLifeSnake.setText(text);
        if (worstDeathSnake.get() != -1) {
            JLabelworstDeathSnake.setText("WORST DEATH SNAKE: " + worstDeathSnake.get());
        }
    }
  
    private void prepareButtons() {
        resume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i != MAX_THREADS; i++) {
                    snakes[i].resume();
                }
                stop.setEnabled(true);
                resume.setEnabled(false);
            }
        });
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i != MAX_THREADS; i++) {
                    snakes[i].stop();
                }
                actualizeData();
                resume.setEnabled(true);
                stop.setEnabled(false);
            }
        });

         reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Detener todas las serpientes y restablecer los valores necesarios
                stopAllSnakes();
                resetGame();
                reset.setEnabled(true);
            }
        });

       
    }
    // NUEVO FINAL
    

    private void stopAllSnakes() {
        for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i].stop();
        }
    }

    private void resetGame() {
        // Reiniciar el juego, restablecer valores
        init();
        
        actualizeData(); // Actualizar información en la interfaz
        stop.setEnabled(true);
        resume.setEnabled(false);
        reset.setEnabled(false); // Deshabilitar el botón "reset" después de reiniciar
        
    }

     

    public static void main(String[] args) {
        app = new SnakeApp();
        app.init();
    }

    private void init() {
        for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i] = new Snake(i + 1, spawn[i], i + 1, longestAtLifeSnake, worstDeathSnake);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
            thread[i].start();
        }

        frame.setVisible(true);
    }
    

    public static SnakeApp getApp() {
        return app;
    }
    //
}
