/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import Auxiliar.Consts;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.ImageIcon;
/**
 *
 * @author cadshoes3
 */
public class Chave extends Personagem implements Serializable {
    private boolean usada = false;
    private boolean coletada = false;
    
    public Chave(String sNomeImagePNG) {
        super(sNomeImagePNG);

    }

    
    public boolean foiUsada() {
        return this.usada;
    }
    
    public boolean foiColetada() {
        return this.coletada;
    }
    
    public void coletar() {
        this.coletada = true;

        System.out.println("Coletei");
            try {
            iImage = new ImageIcon(new java.io.File(".").getCanonicalPath() + Consts.PATH + "Key.png");
            Image img = iImage.getImage();

            BufferedImage bi = new BufferedImage(Consts.CELL_SIDE, Consts.CELL_SIDE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();

            // Define a opacidade desejada (0.0f transparente, 1.0f opaco)
            float opacity = 0.5f;  // exemplo 50% de opacidade

            // Configura o composite para usar essa opacidade
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

            // Desenha a imagem com opacidade
            g2d.drawImage(img, 0, 0, Consts.CELL_SIDE, Consts.CELL_SIDE, null);

            g2d.dispose();

            iImage = new ImageIcon(bi);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void largar() {
        this.coletada = false;
    }
    
    public void reset() {
        this.coletada = false;
        this.usada = false;
    }
    
    public void abrir() {
        this.usada = true;
    }
    
    @Override
    public void autoDesenho() {
        super.autoDesenho();
    }
    
    
}
