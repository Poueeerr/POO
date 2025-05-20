/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.util.ArrayList;

/**
 *
 * @author cadshoes3
 */
public class Mochila<T> {
    private ArrayList<T> items = new ArrayList<>();
    
    public void adicionarItem(T t) {
        items.add(t);
    }
    public T pegarItem(int posicao) {
        return this.items.get(posicao);
    }
    
    public void clear() {
        this.items.clear();
    }
    
    public int tamanho() {
        return this.items.size();
    }
}
