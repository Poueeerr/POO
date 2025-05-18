package Controler;

import Modelo.Chaser;
import Modelo.Personagem;
import Modelo.Hero;
import Auxiliar.Posicao;
import java.util.ArrayList;

public class ControleDeJogo {
    Tela tela;
    
    public ControleDeJogo(Tela tela) {
        this.tela = tela;
    }
    public void desenhaTudo(ArrayList<Personagem> e) {
        // Primeiro desenha todos os elementos que não são o herói
        for (int i = 0; i < e.size(); i++) {
            if (!(e.get(i) instanceof Hero)) {
                e.get(i).autoDesenho();
            }
        }
        
        // Depois desenha o herói por último (assumindo que o herói é o primeiro elemento)
        if (e.size() > 0 && e.get(0) instanceof Hero) {
            e.get(0).autoDesenho();
        }
    }
    
    public void processaTudo(ArrayList<Personagem> umaFase) {
        Posicao faseUmPosicaoVitoria = new Posicao(29, 23);
        Hero hero = (Hero) umaFase.get(0);
        Personagem pIesimoPersonagem;
        for (int i = 1; i < umaFase.size(); i++) {
            pIesimoPersonagem = umaFase.get(i);
            if (hero.getPosicao().igual(pIesimoPersonagem.getPosicao())) {
                if (pIesimoPersonagem.isbTransponivel()) /*TO-DO: verificar se o personagem eh mortal antes de retirar*/ {
                    if (pIesimoPersonagem.isbMortal()) {
                        hero.setPosicao(0,0);
                        this.tela.resetaTela();
                        desenhaTudo(umaFase);;
                        return;
                    }
                    
                }
            }
            if(hero.getPosicao().igual(faseUmPosicaoVitoria)) {
                    hero.setPosicao(0,0);
                    this.tela.resetaTela();
                    desenhaTudo(umaFase);
            }
        }
        for (int i = 1; i < umaFase.size(); i++) {
            pIesimoPersonagem = umaFase.get(i);
            if (pIesimoPersonagem instanceof Chaser) {
                ((Chaser) pIesimoPersonagem).computeDirection(hero.getPosicao());
            }
        }
    }

    /*Retorna true se a posicao p é válida para Hero com relacao a todos os personagens no array*/
    public boolean ehPosicaoValida(ArrayList<Personagem> umaFase, Posicao p) {
        Personagem pIesimoPersonagem;
        for (int i = 1; i < umaFase.size(); i++) {
            pIesimoPersonagem = umaFase.get(i);
            if (!pIesimoPersonagem.isbTransponivel()) {
                if (pIesimoPersonagem.getPosicao().igual(p)) {
                    return false;
                }
            }
        }
        return true;
    }
}
