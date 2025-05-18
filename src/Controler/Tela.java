package Controler;

import Modelo.Personagem;
import Modelo.Caveira;
import Modelo.Estrada;
import Modelo.BlocoMortal;
import Modelo.Hero;
import Modelo.Chaser;
import Modelo.BichinhoVaiVemHorizontal;
import Auxiliar.Consts;
import Auxiliar.Desenho;
import Modelo.BichinhoVaiVemVertical;
import Modelo.ZigueZague;
import Auxiliar.Posicao;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JButton;

public class Tela extends javax.swing.JFrame implements MouseListener, KeyListener {

    private Hero hero;
    private ArrayList<Personagem> faseAtual;
    private ControleDeJogo cj = new ControleDeJogo(this);
    private Graphics g2;
    private int cameraLinha = 0;
    private int cameraColuna = 0;

    public Tela() {
        Desenho.setCenario(this);
        initComponents();
        this.addMouseListener(this);
        /*mouse*/

        this.addKeyListener(this);
        /*teclado*/
 /*Cria a janela do tamanho do tabuleiro + insets (bordas) da janela*/
        this.setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);
        faseAtual = new ArrayList<Personagem>();

        /*Cria faseAtual adiciona personagens*/
        hero = new Hero("Robbo.png");
        hero.setPosicao(0, 7);
        this.addPersonagem(hero);
        this.constroiEstradasDaFase1();
        this.constroiLabirintoDaFase1();
        this.atualizaCamera();
        

        
        
    }
public void constroiEstradasDaFase1() {
    Estrada estrada = new Estrada("Estrada1.png");
    
    int[][] estradas = {
       {0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}, {0, 8}, {0, 9}, {0, 10}, {0, 11}, {0, 12}, {0, 13}, {0, 14}, {0, 15}, {0, 16}, {0, 17}, {0, 18}, {0, 19}, {0, 21}, {0, 22}, {0, 23}, {0, 24}, {0, 25}, {0, 26}, {0, 27}, {0, 28},
    {1, 0}, {1, 3}, {1, 9}, {1, 11}, {1, 15}, {1, 18}, {1, 21}, {1, 24},
    {2, 0}, {2, 1}, {2, 3}, {2, 4}, {2, 5}, {2, 6}, {2, 7}, {2, 8}, {2, 9}, {2, 11}, {2, 15}, {2, 17}, {2, 18}, {2, 19}, {2, 20}, {2, 21}, {2, 22}, {2, 23}, {2, 24},
    {3, 3}, {3, 9}, {3, 11}, {3, 12}, {3, 13}, {3, 14}, {3, 15}, {3, 18}, {3, 21}, {3, 24}, {3, 26}, {3, 27}, {3, 28},
    {4, 0}, {4, 2}, {4, 3}, {4, 5}, {4, 6}, {4, 7}, {4, 9}, {4, 10}, {4, 11}, {4, 13}, {4, 15}, {4, 16}, {4, 17}, {4, 18}, {4, 20}, {4, 21}, {4, 22}, {4, 24}, {4, 25}, {4, 26}, {4, 28},
    {5, 0}, {5, 2}, {5, 5}, {5, 7}, {5, 13}, {5, 20}, {5, 22}, {5, 26}, {5, 28},
    {6, 0}, {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5}, {6, 7}, {6, 8}, {6, 9}, {6, 10}, {6, 11}, {6, 12}, {6, 13}, {6, 15}, {6, 16}, {6, 17}, {6, 18}, {6, 19}, {6, 20}, {6, 22}, {6, 23}, {6, 24}, {6, 25}, {6, 26}, {6, 27}, {6, 28},
    {7, 7}, {7, 10}, {7, 18}, {7, 22}, {7, 25},
    {8, 0}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5}, {8, 6}, {8, 7}, {8, 8}, {8, 9}, {8, 10}, {8, 11}, {8, 12}, {8, 13}, {8, 14}, {8, 16}, {8, 17}, {8, 18}, {8, 19}, {8, 20}, {8, 21}, {8, 22}, {8, 25}, {8, 26}, {8, 28},
    {9, 3}, {9, 7}, {9, 13}, {9, 18}, {9, 20}, {9, 22}, {9, 28},
    {10, 0}, {10, 1}, {10, 2}, {10, 3}, {10, 4}, {10, 5}, {10, 7}, {10, 10}, {10, 11}, {10, 12}, {10, 13}, {10, 14}, {10, 15}, {10, 18}, {10, 19}, {10, 20}, {10, 22}, {10, 25}, {10, 26}, {10, 27}, {10, 28},
    {11, 3}, {11, 7}, {11, 10}, {11, 15}, {11, 17}, {11, 18}, {11, 20}, {11, 22}, {11, 25},
    {12, 1}, {12, 3}, {12, 4}, {12, 5}, {12, 6}, {12, 7}, {12, 8}, {12, 10}, {12, 15}, {12, 18}, {12, 19}, {12, 20}, {12, 21}, {12, 22}, {12, 23}, {12, 25},
    {13, 1}, {13, 2}, {13, 3}, {13, 5}, {13, 8}, {13, 10}, {13, 11}, {13, 12}, {13, 14}, {13, 15}, {13, 16}, {13, 18}, {13, 20}, {13, 23}, {13, 25}, {13, 26}, {13, 27},
    {14, 3}, {14, 5}, {14, 8}, {14, 12}, {14, 16}, {14, 20}, {14, 27},
    {15, 0}, {15, 1}, {15, 2}, {15, 3}, {15, 4}, {15, 5}, {15, 6}, {15, 7}, {15, 8}, {15, 9}, {15, 10}, {15, 11}, {15, 12}, {15, 13}, {15, 14}, {15, 15}, {15, 16}, {15, 18}, {15, 20}, {15, 21}, {15, 22}, {15, 23}, {15, 24}, {15, 25}, {15, 26}, {15, 27}, {15, 28},
    {16, 0}, {16, 3}, {16, 9}, {16, 11}, {16, 13}, {16, 15}, {16, 18}, {16, 21}, {16, 24},
    {17, 0}, {17, 1}, {17, 3}, {17, 4}, {17, 5}, {17, 6}, {17, 7}, {17, 8}, {17, 9}, {17, 11}, {17, 13}, {17, 15}, {17, 16}, {17, 18}, {17, 20}, {17, 21}, {17, 22}, {17, 23}, {17, 24},
    {18, 3}, {18, 9}, {18, 11}, {18, 12}, {18, 13}, {18, 14}, {18, 15}, {18, 18}, {18, 22}, {18, 24}, {18, 26}, {18, 27}, {18, 28},
    {19, 0}, {19, 2}, {19, 3}, {19, 5}, {19, 6}, {19, 7}, {19, 9}, {19, 10}, {19, 11}, {19, 13}, {19, 15}, {19, 17}, {19, 18}, {19, 20}, {19, 21}, {19, 22}, {19, 24}, {19, 25}, {19, 26}, {19, 28},
    {20, 0}, {20, 2}, {20, 5}, {20, 7}, {20, 17}, {20, 20}, {20, 22},
    {21, 0}, {21, 1}, {21, 2}, {21, 4}, {21, 5}, {21, 7}, {21, 10}, {21, 11}, {21, 12}, {21, 13}, {21, 14}, {21, 15}, {21, 16}, {21, 17}, {21, 18}, {21, 19}, {21, 20}, {21, 22}, {21, 23}, {21, 24}, {21, 25}, {21, 26}, {21, 27}, {21, 28},
    {22, 7}, {22, 10}, {22, 15}, {22, 22}, {22, 25},
    {23, 0}, {23, 1}, {23, 2}, {23, 3}, {23, 4}, {23, 5}, {23, 6}, {23, 7}, {23, 8}, {23, 9}, {23, 10}, {23, 11}, {23, 12}, {23, 13}, {23, 15}, {23, 16}, {23, 17}, {23, 18}, {23, 19}, {23, 20}, {23, 21}, {23, 22}, {23, 25}, {23, 26}, {23, 27}, {23, 28},
    {24, 3}, {24, 18}, {24, 28}, {24, 29},
    {25, 0}, {25, 1}, {25, 2}, {25, 3}, {25, 4}, {25, 5}, {25, 7}, {25, 10}, {25, 11}, {25, 12}, {25, 13}, {25, 15}, {25, 16}, {25, 17}, {25, 18}, {25, 19}, {25, 20}, {25, 22}, {25, 25}, {25, 27}, {25, 28},
    {26, 3}, {26, 7}, {26, 10}, {26, 18}, {26, 22}, {26, 25}, {26, 28},
    {27, 1}, {27, 3}, {27, 4}, {27, 5}, {27, 6}, {27, 7}, {27, 8}, {27, 10}, {27, 16}, {27, 18}, {27, 19}, {27, 20}, {27, 22}, {27, 23}, {27, 25}, {27, 28},
    {28, 1}, {28, 2}, {28, 3}, {28, 8}, {28, 9}, {28, 10}, {28, 11}, {28, 12}, {28, 13}, {28, 14}, {28, 16}, {28, 23}, {28, 24}, {28, 25}, {28, 26}, {28, 27}, {28, 28},
    {29, 23}
    };
    
    for (int[] pos : estradas) {
        Estrada estradaTemp = new Estrada("Estrada1.png");
        estradaTemp.setPosicao(pos[0], pos[1]);
        this.addPersonagem(estradaTemp);
    }
    
    
}  
public void constroiLabirintoDaFase1() {
    BlocoMortal blocoMortal = new BlocoMortal("BlocoMortal.png");
    
    int[][] paredes = {
        {0, 20},{0, 29},
        {1, 1}, {1, 2}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, {1, 10}, {1, 12}, {1, 13}, {1, 14}, {1, 16}, {1, 17}, {1, 19}, {1, 20}, {1, 22}, {1, 23}, {1, 25}, {1, 26}, {1, 27}, {1, 28}, {1, 29},
        {2, 2}, {2, 10}, {2, 12}, {2, 13}, {2, 14}, {2, 16}, {2, 25}, {2, 26}, {2, 27}, {2, 28}, {2, 29},
        {3, 0}, {3, 1}, {3, 2}, {3, 4}, {3, 5}, {3, 6}, {3, 7}, {3,8}, {3, 10}, {3, 16}, {3, 17}, {3, 19}, {3, 20}, {3, 22}, {3, 23}, {3, 25}, {3, 29},
        {4, 1}, {4, 4}, {4, 8}, {4, 12}, {4, 14}, {4, 19}, {4, 23}, {4, 27}, {4, 29},
        {5, 1}, {5, 3}, {5, 4}, {5, 6}, {5,8}, {5, 9}, {5, 10}, {5, 11}, {5, 12}, {5, 14}, {5, 15}, {5, 16}, {5, 17}, {5, 18}, {5, 19}, {5, 21}, {5, 23}, {5, 24}, {5, 25}, {5, 27}, {5, 29},
        {6, 6}, {6, 14}, {6, 21}, {6, 29},
        {7, 0}, {7, 1}, {7, 2}, {7, 3}, {7, 4}, {7, 5}, {7, 6}, {7, 8}, {7, 9}, {7, 11}, {7, 12}, {7, 13}, {7, 14}, {7, 15}, {7, 16}, {7, 17}, {7, 19}, {7, 20}, {7, 21}, {7, 23}, {7, 24}, {7, 26}, {7, 27}, {7, 28}, {7, 29},
        {8, 15}, {8, 23}, {8, 24}, {8, 27}, {8, 29},
        {9, 0}, {9, 1}, {9, 2}, {9, 4}, {9, 5}, {9, 6}, {9, 8}, {9, 9}, {9, 10}, {9, 11}, {9, 12}, {9, 14}, {9, 15}, {9, 16}, {9, 17}, {9, 19}, {9, 21}, {9, 23}, {9, 24}, {9, 25}, {9, 26}, {9, 27}, {9, 29},
        {10, 6}, {10, 8}, {10, 9}, {10, 16}, {10, 17}, {10, 21}, {10, 23}, {10, 24}, {10, 29},
        {11, 0}, {11, 1}, {11, 2}, {11, 4}, {11, 5}, {11, 6}, {11, 8}, {11, 9}, {11, 11}, {11, 12}, {11, 13}, {11, 14}, {11, 16}, {11, 19}, {11, 21}, {11, 23}, {11, 24}, {11, 26}, {11, 27}, {11, 28}, {11, 29},
        {12, 0}, {12, 2}, {12, 9}, {12, 11}, {12, 12}, {12, 13}, {12,14}, {12, 16}, {12, 17}, {12, 24}, {12, 26}, {12, 27}, {12, 28}, {12, 29},
        {13, 0}, {13, 4}, {13, 6}, {13, 7}, {13, 9}, {13, 13}, {13, 17}, {13, 19}, {13, 21}, {13, 22}, {13, 24}, {13, 28}, {13, 29},
        {14, 0}, {14, 1}, {14 ,2}, {14, 4}, {14, 6}, {14, 7}, {14, 9}, {14, 10}, {14, 11}, {14, 13}, {14, 14}, {14, 15}, {14, 17}, {14, 18}, {14, 19}, {14, 21}, {14, 22}, {14, 23}, {14, 24}, {14, 25}, {14, 26}, {14, 28}, {14, 29},
        {15, 17}, {15, 19}, {15, 29},
        {16, 1}, {16, 2}, {16, 4}, {16, 5}, {16, 6}, {16, 7}, {16, 8}, {16, 10}, {16, 12}, {16, 14}, {16, 16}, {16, 17}, {16, 19}, {16, 20}, {16, 22}, {16, 23}, {16, 25}, {16, 26}, {16, 27}, {16, 28}, {16, 29},
        {17, 2}, {17, 10}, {17, 12}, {17, 14}, {17, 17}, {17, 19}, {17, 25}, {17, 26}, {17, 27}, {17, 28}, {17, 29},
        {18, 0}, {18, 1}, {18, 2}, {18, 4}, {18, 5}, {18, 6}, {18, 7}, {18, 8}, {18, 10}, {18, 16}, {18, 17}, {18, 19}, {18, 20}, {18, 21}, {18, 23}, {18, 25}, {18, 29},
        {19, 1}, {19, 4}, {19, 8}, {19, 12}, {19, 14}, {19, 16}, {19, 19}, {19, 23}, {19, 27}, {19, 29},
        {20, 1}, {20, 3}, {20, 4}, {20, 6}, {20, 8}, {20, 9}, {20, 10}, {20, 11}, {20, 12}, {20, 13}, {20, 14}, {20, 15}, {20, 16}, {20, 18}, {20, 19}, {20, 21}, {20, 23}, {20, 24}, {20, 25}, {20, 26}, {20, 27}, {20, 28}, {20, 29},
        {21, 3}, {21, 6}, {21, 8}, {21, 9}, {21, 21}, {21, 29},
        {22, 0}, {22, 1}, {22, 2}, {22, 3}, {22, 4}, {22, 5}, {22, 6}, {22, 8}, {22, 9}, {22, 11}, {22, 12}, {22, 13}, {22, 14}, {22, 16}, {22, 17}, {22, 18}, {22, 19}, {22, 20}, {22, 21}, {22, 23}, {22, 24}, {22, 26}, {22, 27}, {22, 28}, {22, 29},
        {23, 14}, {23, 23}, {23, 24}, {23, 29},
        {24, 0}, {24, 1}, {24, 2}, {24, 4}, {24, 5}, {24, 6}, {24, 7}, {24, 8}, {24, 9}, {24, 10}, {24, 11}, {24, 12}, {24, 13}, {24, 14}, {24, 15}, {24, 16}, {24, 17}, {24, 19}, {24, 20}, {24, 21}, {24, 22}, {24, 23}, {24, 24}, {24, 25}, {24, 26}, {24, 27},
        {25, 6}, {25, 8}, {25, 9}, {25, 14}, {25, 21}, {25, 23}, {25, 24}, {25, 26}, {25, 29},
        {26, 0}, {26, 1}, {26, 2}, {26, 4}, {26, 5}, {26, 6}, {26, 8}, {26, 9}, {26, 11}, {26, 12}, {26, 13}, {26, 14}, {26, 15}, {26, 16}, {26, 17}, {26, 19}, {26, 20}, {26, 21}, {26, 23}, {26, 24}, {26, 26}, {26, 27}, {26, 29},
        {27, 0}, {27, 2}, {27, 9}, {27, 11}, {27, 12}, {27, 13}, {27, 14}, {27, 15}, {27, 17}, {27, 21}, {27, 24}, {27, 26}, {27, 27}, {27, 29},
        {28, 0}, {28, 4}, {28, 5}, {28, 6}, {28, 7}, {28, 15}, {28, 17}, {28, 18}, {28, 19}, {28, 20}, {28, 21}, {28, 22}, {28, 29},
        {29, 0}, {29, 1}, {29, 2}, {29, 3}, {29, 4}, {29, 5}, {29, 6}, {29, 7}, {29, 8}, {29, 9}, {29, 10}, {29, 11}, {29, 12}, {29, 13}, {29, 14}, {29, 15}, {29, 16}, {29, 17}, {29, 18}, {29, 19}, {29, 20}, {29, 21}, {29, 22}, {29, 24}, {29, 25}, {29, 26}, {29, 27}, {29, 28}, {29, 29}
    };

    for (int[] pos : paredes) {
        BlocoMortal parede = new BlocoMortal("BlocoMortal.png");
        parede.setPosicao(pos[0], pos[1]);
        this.addPersonagem(parede);
    }
}


    public int getCameraLinha() {
        return cameraLinha;
    }

    public int getCameraColuna() {
        return cameraColuna;
    }

    public boolean ehPosicaoValida(Posicao p) {
        return cj.ehPosicaoValida(this.faseAtual, p);
    }

    public void addPersonagem(Personagem umPersonagem) {
        faseAtual.add(umPersonagem);
    }

    public void removePersonagem(Personagem umPersonagem) {
        faseAtual.remove(umPersonagem);
    }

    public Graphics getGraphicsBuffer() {
        return g2;
    }

    public void paint(Graphics gOld) {
        Graphics g = this.getBufferStrategy().getDrawGraphics();
        /*Criamos um contexto gráfico*/
        g2 = g.create(getInsets().left, getInsets().top, getWidth() - getInsets().right, getHeight() - getInsets().top);
        /**
         * ***********Desenha cenário de fundo*************
         */
        for (int i = 0; i < Consts.RES; i++) {
            for (int j = 0; j < Consts.RES; j++) {
                int mapaLinha = cameraLinha + i;
                int mapaColuna = cameraColuna + j;

                if (mapaLinha < Consts.MUNDO_ALTURA && mapaColuna < Consts.MUNDO_LARGURA) {
                    try {
                        Image newImage = Toolkit.getDefaultToolkit().getImage(
                                new java.io.File(".").getCanonicalPath() + Consts.PATH + "blackTile.png");
                        g2.drawImage(newImage,
                                j * Consts.CELL_SIDE, i * Consts.CELL_SIDE,
                                Consts.CELL_SIDE, Consts.CELL_SIDE, null);
                    } catch (IOException ex) {
                        Logger.getLogger(Tela.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        if (!this.faseAtual.isEmpty()) {
            this.cj.desenhaTudo(faseAtual);
            this.cj.processaTudo(faseAtual);
        }

        g.dispose();
        g2.dispose();
        if (!getBufferStrategy().contentsLost()) {
            getBufferStrategy().show();
        }
    }

    private void atualizaCamera() {
        int linha = hero.getPosicao().getLinha();
        int coluna = hero.getPosicao().getColuna();

        cameraLinha = Math.max(0, Math.min(linha - Consts.RES / 2, Consts.MUNDO_ALTURA - Consts.RES));
        cameraColuna = Math.max(0, Math.min(coluna - Consts.RES / 2, Consts.MUNDO_LARGURA - Consts.RES));
    }

    public void go() {
        TimerTask task = new TimerTask() {
            public void run() {
                repaint();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, Consts.PERIOD);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_C) {
            this.faseAtual.clear();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            hero.moveUp();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            hero.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            hero.moveLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            hero.moveRight();
        }
        this.atualizaCamera();
        this.setTitle("-> Cell: " + (hero.getPosicao().getColuna()) + ", "
                + (hero.getPosicao().getLinha()));

        //repaint(); /*invoca o paint imediatamente, sem aguardar o refresh*/
    }

    public void mousePressed(MouseEvent e) {
        /* Clique do mouse desligado*/
        int x = e.getX();
        int y = e.getY();

        this.setTitle("X: " + x + ", Y: " + y
                + " -> Cell: " + (y / Consts.CELL_SIDE) + ", " + (x / Consts.CELL_SIDE));

        this.hero.getPosicao().setPosicao(y / Consts.CELL_SIDE, x / Consts.CELL_SIDE);

        repaint();
    }
    
    public void resetaTela() {
        cameraLinha = 0;
        cameraColuna = 0;
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POO2023-1 - Skooter");
        setAlwaysOnTop(true);
        setAutoRequestFocus(false);
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
