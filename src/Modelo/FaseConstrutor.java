/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Auxiliar.Consts;
import Auxiliar.Posicao;
import Controler.Fase;
import java.util.ArrayList;

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
        
        this.estradaSprite = "Grama.png";
        this.paredeSprite = "Spike.png";
        
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
}
