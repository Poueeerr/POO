package Controler;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import Auxiliar.Posicao;
import Modelo.Chave;
import Modelo.Hero;
import Modelo.ImagemFundo;
import Modelo.Mochila;
import Modelo.Personagem;
import Modelo.PersonagemFactory;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Tela extends javax.swing.JFrame implements MouseListener, KeyListener, DropTargetListener {
    private boolean mostrarTelaComeco = true;
    private boolean mostrarTelaVitoria = false;
    public ImagemFundo imagemFundo;
    private ImagemFundo imagemCoracao;
    private ImagemFundo imagemTelaComeco;
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
    
    // Componentes de Drag and Drop
    private DropTarget dropTarget;
    private boolean dragDropHabilitado = true;

    public Tela() {
        Desenho.setCenario(this);
        initComponents();
        setAutoRequestFocus(true);
        
        this.addMouseListener(this);
        this.addKeyListener(this);

        this.setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);

        this.imagemCoracao = new ImagemFundo("coracao.png");
        this.imagemTelaComeco = new ImagemFundo("TelaComeco.png");
        hero = new Hero("Mario.png", new Mochila<Chave>());
        faseAtual = new Fase(0, hero);
        
        hero.mochila.adicionarItem(new Chave("Key.png"));
        this.faseAtual.addPersonagem(hero);
        cj = new ControleDeJogo(this, faseAtual);
        
        // Configura o drag and drop
        configurarDragAndDrop();

        // Cria personagens padrão se não existirem
        PersonagemFactory.criarPersonagensPadrao();
        mostrarInstrucoes();
        
        // CORREÇÃO: Carrega a tela sem iniciar o timer
        carregarTelaSemTimer(0);
    }
    
    /**
     * NOVO: Métodos para acessar informações do timer
     */
    public boolean isTimerAtivo() {
        return timerAtivo;
    }
    
    public long getTempoInicio() {
        return tempoInicio;
    }
    
    /**
     * Configura o sistema de drag and drop
     */
    private void configurarDragAndDrop() {
        try {
            dropTarget = new DropTarget(this, this);
            this.setDropTarget(dropTarget);
            
            System.out.println("Drag and Drop configurado com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar Drag and Drop: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Mostra instruções para o usuário
     */
    private void mostrarInstrucoes() {
        System.out.println("\n=== INSTRUÇÕES DE DRAG AND DROP ===");
        System.out.println("1. Navegue até a pasta 'personagens' no seu sistema de arquivos");
        System.out.println("2. Arraste qualquer arquivo .zip para a janela do jogo");
        System.out.println("3. O personagem será adicionado na posição onde você soltar o arquivo");
        System.out.println("\nPersonagens disponíveis:");

        String[] personagens = PersonagemFactory.listarPersonagensDisponiveis();
        for (String personagem : personagens) {
            System.out.println("- " + personagem);
        }
        
        System.out.println("\nTeclas especiais:");
        System.out.println("- S: Salvar jogo");
        System.out.println("- L: Carregar jogo");
        System.out.println("- ENTER: Começar jogo / Interagir");
        System.out.println("=====================================\n");
    }
    
    // ========== MÉTODOS DE DRAG AND DROP ==========
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (!dragDropHabilitado || mostrarTelaComeco || mostrarTelaVitoria) {
            dtde.rejectDrag();
            return;
        }
        
        // Aceita o drag se for um arquivo
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
            dtde.rejectDrag();
        }
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        if (!dragDropHabilitado || mostrarTelaComeco || mostrarTelaVitoria) {
            dtde.rejectDrag();
            return;
        }
        
        // Continua aceitando o drag
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
            dtde.rejectDrag();
        }
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // Não faz nada
    }
    
    public void dragExit(DropTargetEvent dte) {
        // Não faz nada
    }
    
    public void drop(DropTargetDropEvent dtde) {
        if (!dragDropHabilitado || mostrarTelaComeco || mostrarTelaVitoria) {
            dtde.rejectDrop();
            return;
        }
        
        try {
            // Aceita o drop
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            
            // Obtém a lista de arquivos
            Transferable transferable = dtde.getTransferable();
            @SuppressWarnings("unchecked")
            List<File> arquivos = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
            
            // Calcula a posição onde o arquivo foi solto
            int x = dtde.getLocation().x;
            int y = dtde.getLocation().y;
            
            // Converte coordenadas da tela para coordenadas do jogo
            int colunaJogo = (x / Consts.CELL_SIDE) + getCameraColuna();
            int linhaJogo = (y / Consts.CELL_SIDE) + getCameraLinha();
            
            // Processa cada arquivo
            for (File arquivo : arquivos) {
                if (arquivo.getName().toLowerCase().endsWith(".zip")) {
                    processarArquivoPersonagem(arquivo, linhaJogo, colunaJogo);
                } else {
                    System.out.println("Arquivo ignorado (não é .zip): " + arquivo.getName());
                }
            }
            
            // Marca o drop como completo
            dtde.dropComplete(true);
            
            // Atualiza a tela
            repaint();
            
        } catch (Exception e) {
            System.err.println("Erro durante o drop: " + e.getMessage());
            e.printStackTrace();
            dtde.dropComplete(false);
        }
    }
    
    /**
     * Processa um arquivo de personagem e o adiciona ao jogo
     */
    private void processarArquivoPersonagem(File arquivo, int linha, int coluna) {
        try {
            System.out.println("Processando arquivo: " + arquivo.getName() + 
                             " na posição (" + linha + ", " + coluna + ")");
            
            // Carrega o personagem do arquivo ZIP
            Personagem personagem = PersonagemFactory.carregarPersonagem(arquivo.getAbsolutePath());
            
            if (personagem != null) {
                // Verifica se a posição é válida
                if (linha >= 0 && linha < Consts.MUNDO_ALTURA && 
                    coluna >= 0 && coluna < Consts.MUNDO_LARGURA) {
                    
                    // Define a posição do personagem
                    personagem.setPosicao(linha, coluna);
                    
                    // Adiciona o personagem ao jogo
                    addPersonagem(personagem);
                    
                    System.out.println("Personagem " + personagem.getClass().getSimpleName() + 
                                     " adicionado na posição (" + linha + ", " + coluna + ")");
                } else {
                    System.out.println("Posição inválida: (" + linha + ", " + coluna + ")");
                }
            } else {
                System.out.println("Falha ao carregar personagem do arquivo: " + arquivo.getName());
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao processar arquivo " + arquivo.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ========== MÉTODOS ORIGINAIS DA TELA ==========
    
    public Fase getFaseAtual() {
        return this.faseAtual;
    }
    
    public void iniciarTimer() {
        tempoInicio = System.currentTimeMillis();
        timerAtivo = true;
        System.out.println("Timer iniciado!");
    }
    
    /**
     * CORREÇÃO: Para o timer
     */
    public void pararTimer() {
        timerAtivo = false;
        System.out.println("Timer parado!");
    }

    // Método para verificar se o tempo acabou
    private void verificarTimer() {
        if (!timerAtivo || mostrarTelaComeco || mostrarTelaVitoria) return;

        long tempoAtual = System.currentTimeMillis();
        int tempoPassado = (int)((tempoAtual - tempoInicio) / 1000);
        int tempoRestante = faseAtual.getTempoLimite() - tempoPassado;

        if (tempoRestante <= 0) {
            // Tempo esgotado
            this.pararTimer();
            
            // Vai para a tela de morte (tela 5)
            this.setTelaAtualNumero(5);
            
            // Reposiciona herói e reseta câmera
            this.resetaTela();
            hero.setPosicao(0, 0);
            
            System.out.println("Tempo esgotado! Pressione 'A' para jogar novamente");
        }
    }

    // Método para desenhar o tempo na tela
    private void desenharInstrucoesDragDrop() {
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(java.awt.Color.CYAN);
        String instrucao = "Arraste arquivos .zip para adicionar personagens (D para desabilitar)";
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(instrucao);
        int x = (getWidth() - larguraTexto) / 2;
        int y = getHeight() - 30;
        
        g2.drawString(instrucao, x, y);
    }
    
    /**
     * NOVO: Desenha a tela de vitória
     */
    private void desenhaTelaVitoria() {
        // Fundo dourado/amarelo para vitória
        g2.setColor(new java.awt.Color(255, 215, 0, 200)); // Dourado com transparência
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Título "PARABÉNS!"
        g2.setFont(new Font("Arial", Font.BOLD, 56));
        g2.setColor(java.awt.Color.WHITE);
        String parabens = "PARABÉNS!";
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(parabens);
        int x = (getWidth() - larguraTexto) / 2;
        int y = getHeight() / 2 - 120;
        
        // Adiciona contorno ao texto para melhor legibilidade
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(parabens, x + 3, y + 3);
        g2.setColor(java.awt.Color.WHITE);
        g2.drawString(parabens, x, y);
        
        // Subtítulo "VOCÊ VENCEU!"
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(java.awt.Color.YELLOW);
        String voceVenceu = "VOCÊ VENCEU!";
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(voceVenceu);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 - 60;
        
        // Adiciona contorno ao texto
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(voceVenceu, x + 2, y + 2);
        g2.setColor(java.awt.Color.YELLOW);
        g2.drawString(voceVenceu, x, y);
        
        // Pontuação final
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.setColor(java.awt.Color.WHITE);
        String pontuacaoFinal = "Pontuação Final: " + hero.getPontuacao();
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(pontuacaoFinal);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 + 10;
        
        // Adiciona contorno ao texto
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(pontuacaoFinal, x + 2, y + 2);
        g2.setColor(java.awt.Color.WHITE);
        g2.drawString(pontuacaoFinal, x, y);
        
        // Vidas restantes (bônus)
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(java.awt.Color.CYAN);
        String vidasRestantes = "Vidas Restantes: " + hero.getVidas();
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(vidasRestantes);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 + 50;
        
        // Adiciona contorno ao texto
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(vidasRestantes, x + 2, y + 2);
        g2.setColor(java.awt.Color.CYAN);
        g2.drawString(vidasRestantes, x, y);
        
        // Instrução para jogar novamente
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(java.awt.Color.WHITE);
        String instrucao = "Pressione 'A' para jogar novamente";
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(instrucao);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 + 100;
        
        // Adiciona contorno ao texto
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(instrucao, x + 2, y + 2);
        g2.setColor(java.awt.Color.WHITE);
        g2.drawString(instrucao, x, y);
        
        // Instrução adicional
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(java.awt.Color.LIGHT_GRAY);
        String instrucao2 = "ou pressione ESC para sair";
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(instrucao2);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 + 140;
        
        g2.drawString(instrucao2, x, y);
    }
    
    private void desenhaTelaMorte() {
        // Fundo escuro
        g2.setColor(new java.awt.Color(0, 0, 0, 220));
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Título "VOCÊ MORREU"
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(java.awt.Color.RED);
        String voceMorreu = "VOCÊ MORREU";
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(voceMorreu);
        int x = (getWidth() - larguraTexto) / 2;
        int y = getHeight() / 2 - 80;
        
        // Adiciona contorno ao texto para melhor legibilidade
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(voceMorreu, x + 3, y + 3);
        g2.setColor(java.awt.Color.RED);
        g2.drawString(voceMorreu, x, y);
        
        // Pontuação final
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(java.awt.Color.YELLOW);
        String pontuacaoFinal = "Pontuação Final: " + hero.getPontuacao();
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(pontuacaoFinal);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 - 20;
        
        // Adiciona contorno ao texto
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(pontuacaoFinal, x + 2, y + 2);
        g2.setColor(java.awt.Color.YELLOW);
        g2.drawString(pontuacaoFinal, x, y);
        
        // Instrução para reiniciar
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.setColor(java.awt.Color.WHITE);
        String instrucao = "Pressione 'A' para jogar novamente";
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(instrucao);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 + 40;
        
        // Adiciona contorno ao texto
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(instrucao, x + 2, y + 2);
        g2.setColor(java.awt.Color.WHITE);
        g2.drawString(instrucao, x, y);
        
        // Instrução adicional
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(java.awt.Color.LIGHT_GRAY);
        String instrucao2 = "ou pressione ESC para sair";
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(instrucao2);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 + 80;
        
        g2.drawString(instrucao2, x, y);
    }

    private void desenharTimer(Graphics g) {
        if (!timerAtivo || mostrarTelaComeco || mostrarTelaVitoria) return;

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
    
    /**
     * CORREÇÃO: Método para carregar tela sem iniciar timer (usado na inicialização)
     */
    public void carregarTelaSemTimer(int numeroDaTela) {
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
        
        // NÃO inicia o timer aqui
        System.out.println("Fase " + numeroDaTela + " carregada (sem timer)");
    }

    /**
     * NOVO: Método público para forçar a exibição da tela de vitória
     */
    public void mostrarVitoria() {
        mostrarTelaVitoria = true;
        pararTimer();
        System.out.println("Forçando exibição da tela de vitória!");
        repaint();
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

        // REMOVIDO: A verificação automática de vitória aqui
        // A vitória agora é controlada explicitamente pelo ControleDeJogo

        // Só inicia o timer se não estiver na tela de começo e não for tela final
        if (this.numeroDaTelaAtual < 5 && !mostrarTelaComeco && !mostrarTelaVitoria) {
            iniciarTimer();
        }
    }
    
    /**
     * CORREÇÃO: Método para iniciar o jogo propriamente
     */
    private void iniciarJogo() {
        mostrarTelaComeco = false;
        mostrarTelaVitoria = false;
        
        // Agora sim inicia o timer para a fase atual
        if (this.numeroDaTelaAtual < 5) {
            iniciarTimer();
        }
        
        System.out.println("Jogo iniciado! Timer ativo: " + timerAtivo);
        repaint();
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
        
        if (mostrarTelaComeco) {
            desenhaTelaComeco();
            g.dispose();
            g2.dispose();
            if (!getBufferStrategy().contentsLost()) {
                getBufferStrategy().show();
            }
            return;
        }
        
        // NOVO: Se estiver na tela de vitória, mostra a tela de vitória
        if (mostrarTelaVitoria) {
            desenhaTelaVitoria();
            g.dispose();
            g2.dispose();
            if (!getBufferStrategy().contentsLost()) {
                getBufferStrategy().show();
            }
            return;
        }
        
        // Se estiver na tela de morte (tela 6 e herói morto), mostra a mensagem de morte
        // MODIFICAR ESTA PARTE:
        // if (this.numeroDaTelaAtual == 5 && hero.estaMorto()) {
        // PARA:
        if (this.numeroDaTelaAtual == 5) {
            desenhaTelaMorte();
            g.dispose();
            g2.dispose();
            if (!getBufferStrategy().contentsLost()) {
                getBufferStrategy().show();
            }
            return;
        }
        
        // CORREÇÃO: Só verifica o timer se não estiver na tela de começo ou vitória
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

        // Desenha elementos do jogo normal
        desenhaPontuacao();
        desenhaVidas();
        
        if(timerAtivo) {
            desenharTimer(g);
        }
        
        // Desenha instruções de drag and drop se habilitado
        if (dragDropHabilitado) {
            desenharInstrucoesDragDrop();
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
        String textoPontuacao = "Pontuacao: " + hero.getPontuacao();

        // Calcula tamanho do texto
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(textoPontuacao);

        // Centraliza horizontalmente e posiciona na parte inferior com margem de 50px
        int x = (getWidth() - larguraTexto) / 2;
        int y = getHeight() - 50;

        // Adiciona contorno para melhor legibilidade
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(textoPontuacao, x + 1, y + 1);
        g2.setColor(java.awt.Color.YELLOW);
        g2.drawString(textoPontuacao, x, y);
    }
    
/**
 * Desenha a tela de começo ajustada ao tamanho da janela
 */
private void desenhaTelaComeco() {
    if (imagemTelaComeco != null) {
        // Limpa a tela com fundo preto
        g2.setColor(java.awt.Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Obtém as dimensões da tela e da imagem
        int larguraTela = getWidth();
        int alturaTela = getHeight();
        int larguraImagem = 900;
        int alturaImagem = 900;
        
        // Calcula a escala para ajustar a imagem à tela mantendo proporção
        double escalaX = (double) larguraTela / larguraImagem;
        double escalaY = (double) alturaTela / alturaImagem;
        double escala = Math.min(escalaX, escalaY); // Usa a menor escala para manter proporção
        
        // Calcula as novas dimensões da imagem
        int novaLargura = (int) (larguraImagem * escala);
        int novaAltura = (int) (alturaImagem * escala);
        
        // Centraliza a imagem na tela
        int x = (larguraTela - novaLargura) / 2;
        int y = (alturaTela - novaAltura) / 2;
        
        // Desenha a imagem redimensionada
        Graphics2D g2Copy = (Graphics2D) g2.create();
        g2Copy.translate(x, y);
        g2Copy.scale(escala, escala);
        imagemTelaComeco.desenhar(g2Copy);
        g2Copy.dispose();
        
        // Adiciona texto de instrução
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(java.awt.Color.WHITE);
        String instrucao = "Pressione ENTER para começar";
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(instrucao);
        int xTexto = (larguraTela - larguraTexto) / 2;
        int yTexto = alturaTela - 50;
        
        // Adiciona contorno ao texto para melhor legibilidade
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(instrucao, xTexto + 2, yTexto + 2);
        g2.setColor(java.awt.Color.WHITE);
        g2.drawString(instrucao, xTexto, yTexto);
        
    } else {
        // Fallback caso a imagem não carregue
        g2.setColor(java.awt.Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(java.awt.Color.WHITE);
        String titulo = "JOGO INICIANDO";
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(titulo);
        int x = (getWidth() - larguraTexto) / 2;
        int y = getHeight() / 2;
        
        g2.drawString(titulo, x, y);
        
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String instrucao = "Pressione ENTER para começar";
        fm = g2.getFontMetrics();
        larguraTexto = fm.stringWidth(instrucao);
        x = (getWidth() - larguraTexto) / 2;
        y = getHeight() / 2 + 100;
        
        g2.drawString(instrucao, x, y);
    }
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

    // Adicione um log de debug no método reiniciarJogo para verificar se está sendo chamado
public void reiniciarJogo() {
    System.out.println("Reiniciando jogo...");
    
    // Para o timer se estiver ativo
    pararTimer();
    
    // Reseta o estado do jogo
    mostrarTelaComeco = true;
    mostrarTelaVitoria = false;
    numeroDaTelaAtual = 0;
    
    // Limpa a fase atual
    faseAtual.clear();
    
    // Recria o herói
    hero = new Hero("Mario.png", new Mochila<Chave>());
    hero.mochila.adicionarItem(new Chave("Key.png"));
    
    // Configura a fase inicial
    faseAtual = new Fase(0, hero);
    faseAtual.addPersonagem(hero);
    
    // Reseta a câmera
    resetaTela();
    
    // Atualiza o controle de jogo
    cj = new ControleDeJogo(this, faseAtual);
    
    // Carrega a tela inicial sem iniciar o timer
    carregarTelaSemTimer(0);
    
    System.out.println("Jogo reiniciado com sucesso!");
    repaint();
}

    // Modifique o método keyPressed para incluir a verificação da tecla 'A'
    public void keyPressed(KeyEvent e) {
        // NOVO: Se o jogador venceu ou morreu (tela 6) e pressionou 'A', reinicia o jogo
        if ((numeroDaTelaAtual == 5 || numeroDaTelaAtual == 6 || mostrarTelaVitoria) && e.getKeyCode() == KeyEvent.VK_A) {
            System.out.println("Tecla A pressionada na tela de game over/vitória - reiniciando jogo");
            reiniciarJogo();
            return;
        }
        
        // NOVO: Se o jogador venceu ou morreu (tela 6) e pressionou ESC, sai do jogo
        if ((numeroDaTelaAtual == 5 || numeroDaTelaAtual == 6 || mostrarTelaVitoria) && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println("Saindo do jogo...");
            System.exit(0);
            return;
        }
        
        // Se estiver na tela de começo, ENTER inicia o jogo
        if (mostrarTelaComeco && e.getKeyCode() == KeyEvent.VK_ENTER) {
            iniciarJogo();
            return;
        }
        
        // Se ainda estiver na tela de começo, vitória ou morte, ignora outras teclas
        if (mostrarTelaComeco || mostrarTelaVitoria || numeroDaTelaAtual == 5 || numeroDaTelaAtual == 6) {
            return;
        }
        
        // Controles normais do jogo (só funcionam durante o jogo)
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
        
        // Funcionalidades de save/load originais
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
        
        // Novas funcionalidades de drag and drop
        if(e.getKeyCode() == KeyEvent.VK_P) {
            PersonagemFactory.criarPersonagensPadrao();
            System.out.println("Novos personagens criados!");
        }
        if(e.getKeyCode() == KeyEvent.VK_O) {
            System.out.println("\nPersonagens disponíveis:");
            String[] personagens = PersonagemFactory.listarPersonagensDisponiveis();
            for (String personagem : personagens) {
                System.out.println("- " + personagem);
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_D) {
            dragDropHabilitado = !dragDropHabilitado;
            System.out.println("Drag & Drop " + (dragDropHabilitado ? "HABILITADO" : "DESABILITADO"));
        }

        this.atualizaCamera();
        this.setTitle("-> Cell: " + (hero.getPosicao().getColuna()) + ", "
                + (hero.getPosicao().getLinha()));

        repaint();
    }

    public void mousePressed(MouseEvent e) {
       
    }
    
    public void resetaTela() {
        cameraLinha = 0;
        cameraColuna = 0;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
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
    }// </editor-fold>                        
    // Variables declaration - do not modify                     
    // End of variables declaration                   

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
