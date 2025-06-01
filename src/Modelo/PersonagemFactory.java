package Modelo;

import java.io.*;
import java.util.zip.*;
import Auxiliar.Posicao;

/**
 * Factory para criar e salvar personagens em arquivos ZIP
 */
public class PersonagemFactory {
    private static final String PERSONAGENS_DIR = "personagens" + File.separator;
    
    static {
        // Cria o diretório de personagens se não existir
        File dir = new File(PERSONAGENS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Cria personagens pré-definidos e os salva em arquivos ZIP
     */
    public static void criarPersonagensPadrao() {
        try {
            Chaser chaser = new Chaser("Chaser.png");
            chaser.setVelocidade(7);
            salvarPersonagem(chaser, "chaser.zip");
            
            Estrada estrada = new Estrada("Estrada.png");
            salvarPersonagem(estrada, "estrada.zip");
            Chave chave = new Chave("Key.png");
            salvarPersonagem(chave, "chave.zip");
            
            Porta porta = new Porta("PortaFechada.png");
            salvarPersonagem(porta, "porta.zip");
            
            System.out.println("Personagens padrão criados com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro ao criar personagens padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Salva um personagem em um arquivo ZIP
     */
    public static void salvarPersonagem(Personagem personagem, String nomeArquivo) {
        try {
            String caminhoCompleto = PERSONAGENS_DIR + nomeArquivo;
            
            // Cria o arquivo ZIP
            FileOutputStream fos = new FileOutputStream(caminhoCompleto);
            ZipOutputStream zos = new ZipOutputStream(fos);
            
            // Adiciona o objeto serializado ao ZIP
            ZipEntry entry = new ZipEntry("personagem.dat");
            zos.putNextEntry(entry);
            
            ObjectOutputStream oos = new ObjectOutputStream(zos);
            oos.writeObject(personagem);
            oos.flush();
            
            zos.closeEntry();
            zos.close();
            fos.close();
            
            System.out.println("Personagem salvo em: " + caminhoCompleto);
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar personagem: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carrega um personagem de um arquivo ZIP
     */
    public static Personagem carregarPersonagem(String caminhoArquivo) {
        try {
            FileInputStream fis = new FileInputStream(caminhoArquivo);
            ZipInputStream zis = new ZipInputStream(fis);
            
            // Encontra a entrada do personagem
            ZipEntry entry = zis.getNextEntry();
            if (entry != null && entry.getName().equals("personagem.dat")) {
                ObjectInputStream ois = new ObjectInputStream(zis);
                Personagem personagem = (Personagem) ois.readObject();
                
                zis.closeEntry();
                zis.close();
                fis.close();
                
                System.out.println("Personagem carregado de: " + caminhoArquivo);
                return personagem;
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar personagem: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lista todos os arquivos de personagens disponíveis
     */
    public static String[] listarPersonagensDisponiveis() {
        File dir = new File(PERSONAGENS_DIR);
        String[] arquivos = dir.list((dir1, name) -> name.toLowerCase().endsWith(".zip"));
        return arquivos != null ? arquivos : new String[0];
    }
}
