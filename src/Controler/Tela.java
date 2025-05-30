package Controler;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import Auxiliar.Posicao;
import Modelo.Chave;
import Modelo.Hero;
import Modelo.ImagemFundo;
import Modelo.Mochila;
import Modelo.Personagem;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tela extends javax.swing.JFrame implements MouseListener, KeyListener {
    public ImagemFundo imagemFundo;
    private ImagemFundo imagemCoracao;
    private long tempoInicio; // Quando a fase começou
    private boolean timerAtivo = false;
    private Timer gameTimer; // Timer para atualizar o jogo
    private Font fonteTempo = new Font("Arial", Font.BOLD, 20);
    private final int ESPACAMENTO_CORACAO = 10; // Espaço entre os corações
    private final int POSICAO_X_CORACAO_INICIAL = 475; // Posição X inicial para o primeiro coração
    private final int POSICAO_Y_CORACAO = 0; // Posição Y para os corações
    private int numeroDaTelaAtual = 1;
    private final int FASE_FINAL = 5;
    private Hero hero;
    private Fase faseAtual;
    private ControleDeJogo cj;
    private Graphics g2;
    private int cameraLinha = 0;
    private int cameraColuna = 0;

    
    public Tela() {
        Desenho.setCenario(this);
        initComponents();
        setAutoRequestFocus(true);
        
        this.addMouseListener(this);
        this.addKeyListener(this);

        this.setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);

        this.imagemCoracao = new ImagemFundo("coracao.png");
        
        hero = new Hero("Robbo.png", new Mochila<Chave>());
        faseAtual = new Fase(0, hero);
        
        hero.mochila.adicionarItem(new Chave("Key.png"));
        this.faseAtual.addPersonagem(hero);
        cj = new ControleDeJogo(this, faseAtual);
        
        carregarTela(0);   
    }
    
    public Fase getFaseAtual() {
        return this.faseAtual;
    }
    
    public void iniciarTimer() {
        tempoInicio = System.currentTimeMillis();
        timerAtivo = true;
    }

    // Método para verificar se o tempo acabou
    private void verificarTimer() {
        if (!timerAtivo) return;

        long tempoAtual = System.currentTimeMillis();
        int tempoPassado = (int)((tempoAtual - tempoInicio) / 1000);
        int tempoRestante = faseAtual.getTempoLimite() - tempoPassado;

        if (tempoRestante <= 0) {
            // Tempo esgotado
            timerAtivo = false;
            hero.setPosicao(0, 0);
            this.carregarTela(1);
            hero.setPontuacao(0);
            this.resetaTela();
            atualizaCamera();
        }
    }

    // Método para desenhar o tempo na tela
    private void desenharTimer(Graphics g) {
        if (!timerAtivo) return;

        long tempoAtual = System.currentTimeMillis();
        int tempoPassado = (int)((tempoAtual - tempoInicio) / 1000);
        int tempoRestante = this.faseAtual.getTempoLimite() - tempoPassado;

        g2.setFont(fonteTempo);
        g2.setColor(java.awt.Color.WHITE);
        String textoTempo = "Tempo: " + tempoRestante + "s";

        // Desenha o texto do tempo no canto superior esquerdo
        g2.drawString(textoTempo, 10, 25);
    }

    // Método para resetar o timer quando passar de fase
    public void resetarTimer() {
        iniciarTimer();
    }
    
    public int getTelaAtualNumero() {
        return numeroDaTelaAtual;
    }
    
    public void setTelaAtualNumero(int numeroDaTelaAtual) {
        this.numeroDaTelaAtual = numeroDaTelaAtual;
    }

    public void carregarTela(int numeroDaTela) {
        faseAtual.clear();

        for(int i = 0; i < hero.mochila.tamanho();i++) {
            if(hero.mochila.pegarItem(i) instanceof Chave) {
                Chave chave = (Chave) hero.mochila.pegarItem(i);
                chave.reset();
            }
        }

        hero.resetVidas();
        faseAtual.addPersonagem(hero);
        faseAtual.configurarFase(numeroDaTela, hero);
        this.numeroDaTelaAtual = numeroDaTela;
        atualizaCamera();

        // Inicia o timer para a nova fase
        if (this.numeroDaTelaAtual < 5) {
            iniciarTimer();
        }
    }

    public int getCameraLinha() {
        return cameraLinha;
    }

    public int getCameraColuna() {
        return cameraColuna;
    }

    public boolean ehPosicaoValida(Posicao p) {
        return cj.ehPosicaoValida(this.faseAtual.getPersonagens(), p);
    }

    public void addPersonagem(Personagem umPersonagem) {
        faseAtual.addPersonagem(umPersonagem);
    }

    public void removePersonagem(Personagem umPersonagem) {
        faseAtual.removePersonagem(umPersonagem);
    }

    public Graphics getGraphicsBuffer() {
        return g2;
    }

    public void paint(Graphics gOld) {
        // Verifica se o BufferStrategy existe
        if (getBufferStrategy() == null) {
            this.createBufferStrategy(2);
            return;
        }
        
        Graphics g = this.getBufferStrategy().getDrawGraphics();
        g2 = g.create(getInsets().left, getInsets().top, getWidth() - getInsets().right, getHeight() - getInsets().top);
        
        // Verifica o timer
        verificarTimer();
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
        if (!this.faseAtual.existePersonagens()) {
            this.cj.desenhaTudo(faseAtual.getPersonagens());
            this.cj.processaTudo(faseAtual.getPersonagens());
        }
        
        if(imagemFundo != null) {
            imagemFundo.desenhar(g2);
        }
        if(this.numeroDaTelaAtual != 6) {
            desenhaPontuacao();
            desenhaVidas();
            // Desenha o timer se estiver ativo
            if (timerAtivo) {
                desenharTimer(g2);
            }
        }


        g.dispose();
        g2.dispose();
        if (!getBufferStrategy().contentsLost()) {
            getBufferStrategy().show();
        }
    }
    private void desenhaPontuacao() {
        g2.setFont(fonteTempo);
        g2.setColor(java.awt.Color.YELLOW);
        String textoTempo = "Pontuacao: " + hero.getPontuacao();

        // Calcula dimensões totais da tela com base nas constantes
        int larguraTela = Consts.CELL_SIDE * Consts.RES;
        int alturaTela = Consts.CELL_SIDE * Consts.RES;

        // Calcula tamanho do texto
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(textoTempo);

        // Centraliza horizontalmente e posiciona no final da tela na vertical
        int x = (larguraTela - larguraTexto) / 2;
        int y = alturaTela - fm.getDescent();  // ou -10 se quiser um pequeno espaçamento acima da borda

        // Desenha o texto
        g2.drawString(textoTempo, x, y - 50);   
    }
    
    private void desenhaVidas() {
        if (imagemCoracao == null) {
            // Fallback para texto se a imagem não estiver disponível
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            String texto = "Vidas: " + hero.getVidas();
            g2.drawString(texto, POSICAO_X_CORACAO_INICIAL, POSICAO_Y_CORACAO + 20);
            return;
        }

        // Desenha um coração para cada vida
        int totalVidas = hero.getVidas();
        int larguraTotal = totalVidas * imagemCoracao.getLargura() + (totalVidas - 1) * ESPACAMENTO_CORACAO;
        int larguraTela = getWidth(); // ou use o valor fixo se preferir
        int posX = larguraTela - larguraTotal - 20;

        for (int i = 0; i < hero.getVidas(); i++) {
            // Salva o estado atual do Graphics
            Graphics2D g2Copy = (Graphics2D) g2.create();

            // Translada o contexto gráfico para a posição do coração atual
            g2Copy.translate(posX, POSICAO_Y_CORACAO);

            // Desenha o coração na posição atual
            imagemCoracao.desenhar(g2Copy);

            // Libera o contexto gráfico copiado
            g2Copy.dispose();

            // Avança para a próxima posição
            posX += imagemCoracao.getLargura() + ESPACAMENTO_CORACAO;
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
            int loaded = 0;

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
        } else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.cj.processaTudo(faseAtual.getPersonagens(), KeyEvent.VK_ENTER);
        }
        if(e.getKeyCode() == KeyEvent.VK_S){
            System.out.println("Fase" + getTelaAtualNumero());
            SaveLoad.salvarJogo(hero, getTelaAtualNumero());
            loaded = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_L){
            GameData data = SaveLoad.carregarJogo();
            if (data != null) {
                setTelaAtualNumero(data.faseAtual);
                carregarTela(numeroDaTelaAtual);    
                    System.out.println(loaded);
                    if(loaded == 0){
                        hero.setPontuacao(data.pontuacao);
                        loaded++;
                    }
                hero.resetVidas(); 
                System.out.println(data.vidas);
                for (int i = 5; i > data.vidas; i--) {
                    hero.perdeUmaVida(); 
                }
                
        }

        }

        this.atualizaCamera();
        this.setTitle("-> Cell: " + (hero.getPosicao().getColuna()) + ", "
                + (hero.getPosicao().getLinha()));

        repaint(); /*invoca o paint imediatamente, sem aguardar o refresh*/
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
