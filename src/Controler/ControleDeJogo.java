package Controler;

import Auxiliar.Posicao;
import Modelo.Chaser;
import Modelo.Chave;
import Modelo.Hero;
import Modelo.Personagem;
import Modelo.Porta;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
public class ControleDeJogo {
    Tela tela;
    Fase fase;
    
    public ControleDeJogo(Tela tela, Fase fase) {
        this.tela = tela;
        this.fase = fase;
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
    
    public void verificaGameOver(ArrayList<Personagem> umaFase, Hero hero, Personagem pIesimoPersonagem) {
        if (hero.getPosicao().igual(pIesimoPersonagem.getPosicao())) {
                if (pIesimoPersonagem.isbTransponivel()) /*TO-DO: verificar se o personagem eh mortal antes de retirar*/ {
                    if (pIesimoPersonagem.isbMortal()) {
                        if(!hero.estaMorto()) {
                           hero.setPosicao(0, 0);
                           hero.perdeUmaVida();
                           this.tela.resetaTela();
                           desenhaTudo(umaFase);
                        } else {
                            System.out.println("Morreu");
                        }
                    }
                    
                }
        }
    }
    
    public void verificaPassouDeFase(ArrayList<Personagem> umaFase, Hero hero, Personagem pIesimoPersonagem) {
            if(!hero.getPosicao().igual(pIesimoPersonagem.getPosicao())) {
                return;
            }
            if(pIesimoPersonagem instanceof Porta) {
                    Porta porta = (Porta) pIesimoPersonagem;
                    if(porta.estaAberta()) {
                        this.tela.resetaTela();
                        hero.setPontuacao(hero.getPontuacao() + 1);
                        this.tela.setTelaAtualNumero(this.tela.getTelaAtualNumero() + 1);
                        this.tela.carregarTela(this.tela.getTelaAtualNumero());
                    }
           }
    }
   
    
    public void verificaAberturaDePorta(ArrayList<Personagem> umaFase, Hero hero, Personagem pIesimoPersonagem, int tecla) {
            
            if(!hero.getPosicao().igual(pIesimoPersonagem.getPosicao())) {
                return;
            }
            if (pIesimoPersonagem instanceof Porta) {
                Porta porta = (Porta) pIesimoPersonagem;
                if (porta.getPosicao().igual(hero.getPosicao())) {
                    if (!porta.estaAberta()) {
                        for(int j = 0; j < hero.mochila.tamanho(); j++) {
                            if(hero.mochila.pegarItem(j) instanceof Chave) {
                                Chave chave = (Chave) hero.mochila.pegarItem(j);
                                System.out.println("Chave foi coletada: " + chave.foiColetada());
                                System.out.println("Chave foi usada: " + chave.foiUsada());
                                if(chave.foiColetada() && !chave.foiUsada()) {
                                    porta.abrirPorta();
                                    chave.abrir();
                                }
                                
                            }
                        }
                        
                    }
                }
            }
    }
    
    public void verificaColetaDeChave(ArrayList<Personagem> umaFase, Hero hero, Personagem pIesimoPersonagem, int tecla) {
            
            if(!hero.getPosicao().igual(pIesimoPersonagem.getPosicao())) {
                return;
            }
            if(pIesimoPersonagem instanceof Chave && tecla == KeyEvent.VK_ENTER) {
                Chave chaveFase = (Chave) pIesimoPersonagem;
                if (chaveFase.getPosicao().igual(hero.getPosicao())) {
                    for(int j = 0; j < hero.mochila.tamanho(); j++) {
                        if(hero.mochila.pegarItem(j) instanceof Chave) {
                            Chave chaveHeroi = (Chave) hero.mochila.pegarItem(j);
                            if(!chaveHeroi.foiColetada()) {
                                chaveHeroi.coletar();
                                chaveFase.coletar();
                            }
                        }
                            
                    }

                }
            }
    }
    
    public void processaTudo(ArrayList<Personagem> umaFase, int tecla) {
        Posicao faseUmPosicaoVitoria = this.tela.getPosicaoVitoria();
        Hero hero = (Hero) umaFase.get(0);
        Personagem pIesimoPersonagem;
        for (int i = 1; i < umaFase.size(); i++) {
            pIesimoPersonagem = umaFase.get(i);
            verificaGameOver(umaFase, hero, pIesimoPersonagem);
            verificaColetaDeChave(umaFase, hero, pIesimoPersonagem, tecla);
            verificaAberturaDePorta(umaFase, hero, pIesimoPersonagem, tecla);
            verificaPassouDeFase(umaFase, hero, pIesimoPersonagem);
            
        }
 

        
        
        for (int i = 1; i < umaFase.size(); i++) {
            pIesimoPersonagem = umaFase.get(i);
            if (pIesimoPersonagem instanceof Chaser) {
                ((Chaser) pIesimoPersonagem).computeDirection(hero.getPosicao());
            }
        }
    }
    public void processaTudo(ArrayList<Personagem> umaFase) {
        Posicao faseUmPosicaoVitoria = this.fase.getPosicaoVitoria();
        Hero hero = (Hero) umaFase.get(0);
        Personagem pIesimoPersonagem;
        for (int i = 1; i < umaFase.size(); i++) {
            pIesimoPersonagem = umaFase.get(i);
            verificaGameOver(umaFase, hero, pIesimoPersonagem);
            verificaPassouDeFase(umaFase, hero, pIesimoPersonagem);
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
