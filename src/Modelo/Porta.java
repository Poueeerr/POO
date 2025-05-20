/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import Auxiliar.Consts;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.ImageIcon;
import java.io.Serializable;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
/**
 *
 * @author cadshoes3
 */
public class Porta extends Personagem implements Serializable {
    private boolean aberta = false;
    public Porta(String sNomeImagePNG) {
        super(sNomeImagePNG);
        
    }
    
public void abrirPorta() {
    this.aberta = true;
    try {
        System.out.println("Abrindo porta...");
        iImage = new ImageIcon(new java.io.File(".").getCanonicalPath() + Consts.PATH + "PortaAberta.png");
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

    
    public boolean estaAberta() {
        return this.aberta;
    }
    

    @Override
    public void autoDesenho() {
        super.autoDesenho();
    }
}

