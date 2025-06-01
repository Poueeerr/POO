/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Auxiliar.Posicao;

/**
 *
 * @author yurifaria
 */
public class FaseDesign {
    private int tempoLimite;
    
    private Posicao posicaoInicialHeroi;
    private Posicao posicaoVitoria;
    
    private int[][] paredes;
    
    private String estradaSprite;
    private String paredeSprite;
    
    public FaseDesign(int[][] paredes, int tempoLimite, Posicao posicaoInicialHeroi, Posicao posicaoVitoria, String estradaSprite, String paredeSprite) {
        this.paredes = paredes;
        this.tempoLimite = tempoLimite;
        this.posicaoInicialHeroi = posicaoInicialHeroi;
        this.posicaoVitoria = posicaoVitoria;
        this.estradaSprite = estradaSprite;
        this.paredeSprite = paredeSprite;
    }
    
    public int[][] getParedes() {
        return this.paredes;
    }
    
    public int getTempoLimite() {
        return this.tempoLimite;
    }
    
    public Posicao getPosicaoInicialHeroi() {
        return this.posicaoInicialHeroi;
    }
    
    public Posicao getPosicaoVitoria() {
        return this.posicaoVitoria;
    }
    
    public String getEstradaSprite() {
        return this.estradaSprite;
    }
    
    public String getParedeSprite() {
        return this.paredeSprite;
    }
}
