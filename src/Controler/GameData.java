package Controler;

import java.io.Serializable;

public class GameData implements Serializable {
    public int pontuacao;
    public int vidas;
    public int faseAtual;

    public GameData(int pontuacao, int vidas, int faseAtual) {
        this.pontuacao = pontuacao;
        this.vidas = vidas;
        this.faseAtual = faseAtual;
    }
}
