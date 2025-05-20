package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import Controler.ControleDeJogo;
import Controler.Tela;
import Auxiliar.Posicao;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Hero extends Personagem implements Serializable{
    private int pontuacao = 0;
    private int vidas = 5;
    private final int PONTUACAO_MAXIMA = 100;
    public Mochila mochila;
    
    public Hero(String sNomeImagePNG, Mochila mochila) {
        super(sNomeImagePNG);
        this.mochila = mochila;
    }
    
    public boolean estaMorto() {
        return this.vidas == 0;
    }
    
    public void perdeUmaVida() {
        this.vidas--;
    }
    
    public int getVidas() {
        return this.vidas;
    }
    
    public void resetVidas() {
        this.vidas = 5;
    }
    
    public int getPontuacao() {
        return this.pontuacao;
    }
    
    public void setPontuacao(int pontuacao) {
        if(this.pontuacao + pontuacao > PONTUACAO_MAXIMA) {
            this.pontuacao = pontuacao;
            return;
        }
        this.pontuacao += pontuacao;
    }

    public void voltaAUltimaPosicao(){
        this.pPosicao.volta();
    }
    
    // Método para posicionar sem validação (usado durante inicialização)
    public boolean setPosicaoSemValidacao(int linha, int coluna) {
        return this.pPosicao.setPosicao(linha, coluna);
    }
    
    public boolean setPosicao(int linha, int coluna){
        if(this.pPosicao.setPosicao(linha, coluna)){
            if (Desenho.acessoATelaDoJogo() != null && !Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
                this.voltaAUltimaPosicao();
            }
            return true;
        }
        return false;       
    }

    /*TO-DO: este metodo pode ser interessante a todos os personagens que se movem*/
    private boolean validaPosicao(){
        if (Desenho.acessoATelaDoJogo() != null && !Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
            this.voltaAUltimaPosicao();
            return false;
        }
        return true;       
    }
    
    // Resto dos métodos permanecem iguais...
}