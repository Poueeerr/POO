/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Auxiliar.Consts;
import Auxiliar.Posicao;
import Controler.Fase;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author yurifaria
 */
public class FaseConstrutor {
    private Fase fase;
    private Hero hero;
    
    private int tempoLimite;
    private Posicao posicaoInicialHeroi;
    private Posicao posicaoVitoria;
    
    private int[][] paredes;
    private int[][] estradas;
    
    private String estradaSprite;
    private String paredeSprite;
    
    public FaseConstrutor(Fase fase, Hero hero, FaseDesign faseDesign) {
        this.fase = fase;
        this.hero = hero;
        
        this.paredes = faseDesign.getParedes();
        
        this.estradaSprite = faseDesign.getEstradaSprite();
        this.paredeSprite = faseDesign.getParedeSprite();
        
        this.tempoLimite = faseDesign.getTempoLimite();
        this.posicaoInicialHeroi = faseDesign.getPosicaoInicialHeroi();
        this.posicaoVitoria = faseDesign.getPosicaoVitoria();
        
        this.construirListaDeEstradas();
    }
    
    private void construirListaDeEstradas() {
        boolean[][] ehParede = new boolean[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];

        for (int[] parede : paredes)
            ehParede[parede[0]][parede[1]] = true;

        ArrayList<int[]> estradas = new ArrayList<>();

        for (int i = 0; i < Consts.MUNDO_ALTURA; i++) {
            for (int j = 0; j < Consts.MUNDO_LARGURA; j++) {
                if (!ehParede[i][j])
                    estradas.add(new int[]{i, j});
            }
        }

        this.estradas = estradas.toArray(new int[0][]);
    }
    
    public void iniciarFase() {
       this.fase.setTempoLimite(this.tempoLimite);
       this.fase.setPosicaoVitoria(this.posicaoVitoria);
       this.construirFase();
       this.hero.setPosicao(this.posicaoInicialHeroi.getLinha(), this.posicaoInicialHeroi.getColuna());
    }
    
    private void construirFase() {
        this.construirEstradas();
        this.construirParedes();
        this.construirChaves();
        this.construirPortas();
        this.construirChasers();
    }
    
    private void construirEstradas() {
        for(int i = 0; i < 30; i++) {
            for(int j = 0; j < 30; j++) {
                Estrada estradaTemp = new Estrada(this.estradaSprite);
                estradaTemp.setPosicao(i, j);
                this.fase.addPersonagem(estradaTemp);
            }
        }
    }
    
    private void construirParedes() {
        for (int[] pos : this.paredes) {
            BlocoMortal parede = new BlocoMortal(this.paredeSprite);
            parede.setPosicao(pos[0], pos[1]);
            this.fase.addPersonagem(parede);
        }
    }
    
    private void construirChaves() {
        int indiceAleatorio = (int) (Math.random() * this.estradas.length);
        int[] posicaoAleatoria = this.estradas[indiceAleatorio];
        
        Chave chave = new Chave("Key.png");
        chave.setPosicao(posicaoAleatoria[0], posicaoAleatoria[1]);
        this.fase.addPersonagem(chave);
    }
    
    private void construirPortas() {
        Porta porta = new Porta("PortaFechada.png");
        porta.setPosicao(posicaoVitoria.getLinha(), posicaoVitoria.getColuna());
        this.fase.addPersonagem(porta);
    } 

private void construirChasers() {
    System.out.println("Construindo Chasers...");
    
    // Primeiro, vamos garantir que temos estradas válidas
    if (this.estradas == null || this.estradas.length == 0) {
        System.out.println("ERRO: Nenhuma estrada disponível para colocar Chasers!");
        return;
    }
    
    // Criar mapa de paredes para validação
    boolean[][] ehParede = new boolean[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
    for (int[] parede : this.paredes) {
        if (parede[0] >= 0 && parede[0] < Consts.MUNDO_ALTURA && 
            parede[1] >= 0 && parede[1] < Consts.MUNDO_LARGURA) {
            ehParede[parede[0]][parede[1]] = true;
        }
    }
    
    int numeroChasers = 1;
    
    for (int i = 0; i < numeroChasers; i++) {
        int[] posicaoChaser = escolherPosicaoValidaParaChaser(ehParede);
        
        if (posicaoChaser != null) {
            Chaser chaser = new Chaser("Chaser.png");
            
            // VALIDAÇÃO EXTRA: Verifica se a posição não é parede
            if (!ehParede[posicaoChaser[0]][posicaoChaser[1]]) {
                chaser.setPosicao(posicaoChaser[0], posicaoChaser[1]);
                
                // Define velocidade
                int velocidade = 7;
                chaser.setVelocidade(velocidade);
                
                // Passa o mapa de paredes para o Chaser
                chaser.setMapaParedes(ehParede);
                
                this.fase.addPersonagem(chaser);
                System.out.println("Chaser " + (i+1) + " criado na ESTRADA: " + 
                                 posicaoChaser[0] + ", " + posicaoChaser[1]);
            } else {
                System.out.println("ERRO: Tentativa de colocar Chaser em parede: " + 
                                 posicaoChaser[0] + ", " + posicaoChaser[1]);
            }
        } else {
            System.out.println("ERRO: Não foi possível encontrar posição válida para Chaser " + (i+1));
        }
    }
}

/**
 * Escolhe uma posição válida para o chaser (apenas em estradas)
 */
/**
 * Escolhe uma posição válida para o Chaser, garantindo distância mínima do herói
 */
private int[] escolherPosicaoValidaParaChaser(boolean[][] ehParede) {
    // Primeiro, precisamos da posição do herói
    Hero hero = null;
    for (Personagem p : this.fase.getPersonagens()) {
        if (p instanceof Hero) {
            hero = (Hero) p;
            break;
        }
    }
    
    if (hero == null) {
        System.out.println("ERRO: Herói não encontrado!");
        return null;
    }
    
    int heroiLinha = hero.getPosicao().getLinha();
    int heroiColuna = hero.getPosicao().getColuna();
    
    System.out.println("Posição do herói: " + heroiLinha + ", " + heroiColuna);
    
    // Lista de posições candidatas (estradas)
    ArrayList<int[]> posicoesValidas = new ArrayList<>();
    
    // Verifica todas as estradas disponíveis
    for (int[] estrada : this.estradas) {
        int linha = estrada[0];
        int coluna = estrada[1];
        
        // Verifica se é uma posição válida (não é parede)
        if (linha >= 0 && linha < Consts.MUNDO_ALTURA && 
            coluna >= 0 && coluna < Consts.MUNDO_LARGURA && 
            !ehParede[linha][coluna]) {
            
            // Calcula a distância de Manhattan entre esta posição e o herói
            int distancia = Math.abs(linha - heroiLinha) + Math.abs(coluna - heroiColuna);
            
            // Só aceita posições com distância mínima de 25
            if (distancia >= 25) {
                posicoesValidas.add(new int[]{linha, coluna});
                System.out.println("Posição candidata: " + linha + ", " + coluna + " (distância: " + distancia + ")");
            }
        }
    }
    
    // Se não encontrou nenhuma posição válida com a distância mínima
    if (posicoesValidas.isEmpty()) {
        System.out.println("AVISO: Não foi possível encontrar posição com distância mínima de 25. Tentando com distância menor...");
        
        // Tenta novamente com distância menor
        for (int[] estrada : this.estradas) {
            int linha = estrada[0];
            int coluna = estrada[1];
            
            if (linha >= 0 && linha < Consts.MUNDO_ALTURA && 
                coluna >= 0 && coluna < Consts.MUNDO_LARGURA && 
                !ehParede[linha][coluna]) {
                
                int distancia = Math.abs(linha - heroiLinha) + Math.abs(coluna - heroiColuna);
                
                // Aceita qualquer posição com distância mínima de 15
                if (distancia >= 15) {
                    posicoesValidas.add(new int[]{linha, coluna});
                }
            }
        }
        
        // Se ainda não encontrou, aceita qualquer posição válida
        if (posicoesValidas.isEmpty()) {
            System.out.println("AVISO: Usando qualquer posição válida disponível.");
            
            for (int[] estrada : this.estradas) {
                int linha = estrada[0];
                int coluna = estrada[1];
                
                if (linha >= 0 && linha < Consts.MUNDO_ALTURA && 
                    coluna >= 0 && coluna < Consts.MUNDO_LARGURA && 
                    !ehParede[linha][coluna]) {
                    
                    int distancia = Math.abs(linha - heroiLinha) + Math.abs(coluna - heroiColuna);
                    System.out.println("Adicionando posição de fallback: " + linha + ", " + coluna + " (distância: " + distancia + ")");
                    posicoesValidas.add(new int[]{linha, coluna});
                }
            }
        }
    }
    
    // Se ainda não encontrou nenhuma posição válida
    if (posicoesValidas.isEmpty()) {
        System.out.println("ERRO: Não foi possível encontrar nenhuma posição válida para o Chaser!");
        return null;
    }
    
    // Escolhe uma posição aleatória entre as válidas
    int indiceAleatorio = new Random().nextInt(posicoesValidas.size());
    int[] posicaoEscolhida = posicoesValidas.get(indiceAleatorio);
    
    int distanciaFinal = Math.abs(posicaoEscolhida[0] - heroiLinha) + Math.abs(posicaoEscolhida[1] - heroiColuna);
    System.out.println("Posição escolhida para Chaser: " + posicaoEscolhida[0] + ", " + posicaoEscolhida[1] + 
                     " (distância do herói: " + distanciaFinal + ")");
    
    return posicaoEscolhida;
}


}
