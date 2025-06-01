package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import Auxiliar.Posicao;
import java.io.Serializable;
import java.util.*;

public class Chaser extends Personagem implements Serializable {

    private int contadorMovimento = 0;
    private int intervaloMovimento = 8;
    private Posicao ultimaPosicaoHeroi;
    private Queue<Posicao> caminhoParaHeroi;
    private boolean[][] mapaParedes;
    private boolean mapaInicializado = false;
    
    public Chaser(String sNomeImagePNG) {
        super(sNomeImagePNG);
        
        // CORREÇÃO: Mantém transponível para o herói, mas mortal
        this.bTransponivel = true; 
        this.bMortal = true;
        
        this.ultimaPosicaoHeroi = new Posicao(0, 0);
        this.caminhoParaHeroi = new LinkedList<>();
        this.mapaParedes = new boolean[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
        
        System.out.println("Chaser criado na posição: " + this.getPosicao().getLinha() + ", " + this.getPosicao().getColuna());
    }
    
    /**
     * Define o mapa de paredes para o Chaser
     */
    public void setMapaParedes(boolean[][] mapaParedes) {
        this.mapaParedes = new boolean[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
        for (int i = 0; i < Consts.MUNDO_ALTURA; i++) {
            for (int j = 0; j < Consts.MUNDO_LARGURA; j++) {
                this.mapaParedes[i][j] = mapaParedes[i][j];
            }
        }
        this.mapaInicializado = true;
        System.out.println("Mapa de paredes definido para Chaser");
    }

    public void computeDirection(Posicao heroPos) {
        // Atualiza o mapa de paredes periodicamente
        if (!mapaInicializado || contadorMovimento % 20 == 0) {
            atualizarMapaParedes();
        }
        
        // Só recalcula o caminho se o herói mudou de posição ou não temos caminho
        if (!heroPos.igual(ultimaPosicaoHeroi) || caminhoParaHeroi.isEmpty()) {
            ultimaPosicaoHeroi.copia(heroPos);
            calcularCaminhoParaHeroi(heroPos);
        }
        
        moverChaser();
    }
    
    /**
     * CORREÇÃO: Atualiza o mapa de paredes baseado apenas em BlocoMortal e paredes reais
     */
    private void atualizarMapaParedes() {
        // Limpa o mapa
        for (int i = 0; i < Consts.MUNDO_ALTURA; i++) {
            for (int j = 0; j < Consts.MUNDO_LARGURA; j++) {
                mapaParedes[i][j] = false;
            }
        }
        
        // Marca apenas BlocoMortal e outros obstáculos reais como paredes
        if (Desenho.acessoATelaDoJogo() != null) {
            var personagens = Desenho.acessoATelaDoJogo().getFaseAtual().getPersonagens();
            
            for (Personagem p : personagens) {
                // CORREÇÃO: Só considera BlocoMortal como parede para pathfinding
                if (p instanceof BlocoMortal) {
                    int linha = p.getPosicao().getLinha();
                    int coluna = p.getPosicao().getColuna();
                    
                    if (linha >= 0 && linha < Consts.MUNDO_ALTURA && 
                        coluna >= 0 && coluna < Consts.MUNDO_LARGURA) {
                        mapaParedes[linha][coluna] = true;
                    }
                }
            }
            mapaInicializado = true;
        }
    }
    
    /**
     * Verifica se uma posição é uma parede
     */
    private boolean ehParede(int linha, int coluna) {
        if (linha < 0 || linha >= Consts.MUNDO_ALTURA || 
            coluna < 0 || coluna >= Consts.MUNDO_LARGURA) {
            return true; // Fora dos limites = parede
        }
        return mapaParedes[linha][coluna];
    }
    
    /**
     * CORREÇÃO: Validação simplificada - só verifica paredes reais
     */
    private boolean ehPosicaoValidaParaMovimento(int linha, int coluna) {
        // Verifica limites
        if (linha < 0 || linha >= Consts.MUNDO_ALTURA || 
            coluna < 0 || coluna >= Consts.MUNDO_LARGURA) {
            return false;
        }
        
        // Verifica se é parede (BlocoMortal)
        return !mapaParedes[linha][coluna];
    }
    
    /**
     * Calcula o caminho até o herói usando BFS, evitando apenas paredes reais
     */
    private void calcularCaminhoParaHeroi(Posicao heroPos) {
        caminhoParaHeroi.clear();
        
        Posicao posicaoAtual = this.getPosicao();
        Posicao destino = heroPos;
        
        // Se já estamos na posição do herói, não precisa calcular
        if (posicaoAtual.igual(destino)) {
            return;
        }
        
        // BFS para encontrar o caminho mais curto
        Queue<Posicao> fila = new LinkedList<>();
        boolean[][] visitado = new boolean[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
        Posicao[][] anterior = new Posicao[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
        
        fila.offer(new Posicao(posicaoAtual.getLinha(), posicaoAtual.getColuna()));
        visitado[posicaoAtual.getLinha()][posicaoAtual.getColuna()] = true;
        
        // Direções: cima, baixo, esquerda, direita
        int[] deltaLinha = {-1, 1, 0, 0};
        int[] deltaColuna = {0, 0, -1, 1};
        
        boolean encontrouCaminho = false;
        
        while (!fila.isEmpty() && !encontrouCaminho) {
            Posicao atual = fila.poll();
            
            // Verifica se chegou ao destino
            if (atual.igual(destino)) {
                encontrouCaminho = true;
                break;
            }
            
            // Explora vizinhos
            for (int i = 0; i < 4; i++) {
                int novaLinha = atual.getLinha() + deltaLinha[i];
                int novaColuna = atual.getColuna() + deltaColuna[i];
                
                // Verifica se é posição válida para movimento
                if (!ehPosicaoValidaParaMovimento(novaLinha, novaColuna)) {
                    continue;
                }
                
                // Verifica se já foi visitado
                if (visitado[novaLinha][novaColuna]) {
                    continue;
                }
                
                Posicao novaPosicao = new Posicao(novaLinha, novaColuna);
                fila.offer(novaPosicao);
                visitado[novaLinha][novaColuna] = true;
                anterior[novaLinha][novaColuna] = atual;
            }
        }
        
        // Reconstrói o caminho se encontrou
        if (encontrouCaminho) {
            Stack<Posicao> caminhoInvertido = new Stack<>();
            Posicao atual = destino;
            
            while (atual != null && !atual.igual(posicaoAtual)) {
                caminhoInvertido.push(new Posicao(atual.getLinha(), atual.getColuna()));
                atual = anterior[atual.getLinha()][atual.getColuna()];
            }
            
            // Inverte o caminho para ficar na ordem correta
            while (!caminhoInvertido.isEmpty()) {
                caminhoParaHeroi.offer(caminhoInvertido.pop());
            }
            
            System.out.println("Caminho calculado com " + caminhoParaHeroi.size() + " passos");
        } else {
            System.out.println("Nenhum caminho encontrado para o herói!");
        }
    }
    
    /**
     * CORREÇÃO: Move o Chaser usando os métodos originais com validação
     */
    private void moverChaser() {
        contadorMovimento++;
        
        // Só se move a cada 'intervaloMovimento' ciclos para controlar velocidade
        if (contadorMovimento < intervaloMovimento) {
            return;
        }
        
        contadorMovimento = 0;
        
        // Se não há caminho, não se move
        if (caminhoParaHeroi.isEmpty()) {
            return;
        }
        
        // Pega o próximo passo do caminho
        Posicao proximaPosicao = caminhoParaHeroi.poll();
        
        if (proximaPosicao != null) {
            // Verifica se a próxima posição não é parede
            if (ehParede(proximaPosicao.getLinha(), proximaPosicao.getColuna())) {
                System.out.println("ERRO: Tentativa de mover para parede! Recalculando caminho...");
                caminhoParaHeroi.clear(); // Limpa o caminho para recalcular
                return;
            }
            
            // Salva posição atual para rollback se necessário
            int linhaAnterior = this.getPosicao().getLinha();
            int colunaAnterior = this.getPosicao().getColuna();
            
            // Calcula a direção do movimento
            int deltaLinha = proximaPosicao.getLinha() - this.getPosicao().getLinha();
            int deltaColuna = proximaPosicao.getColuna() - this.getPosicao().getColuna();
            
            // Move na direção calculada usando os métodos originais
            boolean movimentoValido = false;
            
            if (deltaLinha == -1) { // Mover para cima
                movimentoValido = this.moveUp();
            } else if (deltaLinha == 1) { // Mover para baixo
                movimentoValido = this.moveDown();
            } else if (deltaColuna == -1) { // Mover para esquerda
                movimentoValido = this.moveLeft();
            } else if (deltaColuna == 1) { // Mover para direita
                movimentoValido = this.moveRight();
            }
            
            // CORREÇÃO: Verifica se acabou em uma parede após o movimento
            if (movimentoValido && ehParede(this.getPosicao().getLinha(), this.getPosicao().getColuna())) {
                // Se acabou em parede, volta para posição anterior
                this.setPosicao(linhaAnterior, colunaAnterior);
                caminhoParaHeroi.clear();
                System.out.println("Chaser estava em parede! Voltou para: " + 
                                 linhaAnterior + ", " + colunaAnterior);
            } else if (!movimentoValido) {
                // Se movimento não foi válido, limpa caminho para recalcular
                caminhoParaHeroi.clear();
            }
        }
    }
    
    /**
     * Define a velocidade do Chaser
     */
    public void setVelocidade(int intervalo) {
        this.intervaloMovimento = Math.max(1, intervalo);
    }
    
    /**
     * Obtém a velocidade atual do Chaser
     */
    public int getVelocidade() {
        return this.intervaloMovimento;
    }

    @Override
    public void autoDesenho() {
        super.autoDesenho();
    }
}
