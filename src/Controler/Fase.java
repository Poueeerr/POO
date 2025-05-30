/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controler;
import Auxiliar.FasesDesign;
import java.util.ArrayList;
import Modelo.Personagem;
import Modelo.Hero;
import Auxiliar.Posicao;
import Modelo.BlocoMortal;
import Modelo.Chave;
import Modelo.Estrada;
import Modelo.FaseConstrutor;
import Modelo.Porta;
/**
 *
 * @author cleverson
 */
public class Fase {
    public static int numero;
    private int tempoLimite;
    private Posicao posicaoInicialHeroi;
    private Posicao posicaoVitoria;
    private ArrayList<Personagem> personagens;
    
    public Fase(int numero, Hero hero) {
        this.numero = numero;
        this.personagens = new ArrayList<>();
        this.personagens.add(hero);
    }
    
    public void configurarFase(int numero, Hero hero) {
        ArrayList<FaseConstrutor> fases = new ArrayList<FaseConstrutor>();
        fases.add(new FaseConstrutor(this, hero, FasesDesign.paredes[0], 150, new Posicao(0, 7), new Posicao(29, 23)));
        fases.add(new FaseConstrutor(this, hero, FasesDesign.paredes[0], 60, new Posicao(0, 7), new Posicao(29, 23)));
        fases.add(new FaseConstrutor(this, hero, FasesDesign.paredes[0], 60, new Posicao(0, 7), new Posicao(29, 23)));
        fases.add(new FaseConstrutor(this, hero, FasesDesign.paredes[0], 60, new Posicao(0, 7), new Posicao(29, 23)));
        fases.add(new FaseConstrutor(this, hero, FasesDesign.paredes[0], 60, new Posicao(0, 7), new Posicao(29, 23)));
        fases.get(numero).iniciarFase();
    }
    
    public void setTempoLimite(int tempoLimite) {
        this.tempoLimite = tempoLimite;
    }
    
    public void setPosicaoInicialHeroi(Posicao posicaoInicialHeroi) {
        this.posicaoInicialHeroi = posicaoInicialHeroi;
    }
    
    public void setPosicaoVitoria(Posicao posicaoVitoria) {
        this.posicaoVitoria = posicaoVitoria;
    }
    
    public void clear() {
        this.personagens.clear();
    }
    
    public int getTempoLimite() {
        return this.tempoLimite;
    }
    
    public void addPersonagem(Personagem personagem) {
        this.personagens.add(personagem);
    }
    
    public boolean existePersonagens() {
        return this.personagens.isEmpty();
    }
    
    public void removePersonagem(Personagem personagem) {
        this.personagens.remove(personagem);
    }
    
    public ArrayList<Personagem> getPersonagens() {
        return this.personagens;
    }
    public Posicao getPosicaoVitoria() {
        return this.posicaoVitoria;
    }
}
