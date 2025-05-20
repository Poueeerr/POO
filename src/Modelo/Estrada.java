/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import Auxiliar.Desenho;
import java.io.Serializable;

/**
 *
 * @author cadshoes3
 */
public class Estrada extends Personagem implements Serializable {
    public Estrada(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.bMortal = false;
        this.bTransponivel = true;
    }

    @Override
    public void autoDesenho() {
        super.autoDesenho();
    }
}
