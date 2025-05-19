// Crie uma classe ImagemFundo no pacote Modelo
package Modelo;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import Auxiliar.Consts;

public class ImagemFundo {
    private ImageIcon imagem;
    private int largura;
    private int altura;
    
    public ImagemFundo(String nomeArquivo) {
        try {
            // Carrega a imagem do arquivo
            String caminho = new File(".").getCanonicalPath() + Consts.PATH + nomeArquivo;
            System.out.println("Tentando carregar imagem: " + caminho);
            
            File file = new File(caminho);
            if (!file.exists()) {
                System.out.println("ERRO: Arquivo não encontrado: " + caminho);
                return;
            }
            
            Image img = Toolkit.getDefaultToolkit().getImage(caminho);
            this.imagem = new ImageIcon(img);
            this.largura = imagem.getIconWidth();
            this.altura = imagem.getIconHeight();
            
            System.out.println("Imagem carregada com sucesso. Dimensões: " + largura + "x" + altura);
        } catch (IOException ex) {
            System.out.println("Erro ao carregar a imagem: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public int getLargura() {
        return this.largura;
    }
    
    public void desenhar(Graphics g) {
        // Desenha a imagem em tamanho completo
        if (imagem != null) {
            imagem.paintIcon(null, g, 0, 0);
        } else {
            System.out.println("ERRO: Tentativa de desenhar imagem nula");
        }
    }
    
    public void desenharRedimensionado(Graphics g, int larguraJanela, int alturaJanela) {
        // Desenha a imagem redimensionada para caber na janela
        if (imagem != null && imagem.getImage() != null) {
            g.drawImage(imagem.getImage(), 0, 0, larguraJanela, alturaJanela, null);
        } else {
            System.out.println("ERRO: Tentativa de desenhar imagem nula");
        }
    }
}