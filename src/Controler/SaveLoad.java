package Controler;

import java.io.*;
import Modelo.Hero;

public class SaveLoad {
    private static final String FILE_NAME = "savegame.dat";

    public static void salvarJogo(Hero heroi, int faseAtual) {
        GameData data = new GameData(heroi.getPontuacao(), heroi.getVidas(), faseAtual);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(data);
            System.out.println("Jogo salvo com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao salvar jogo: " + e.getMessage());
        }
    }

    public static GameData carregarJogo() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            GameData data = (GameData) ois.readObject();
            System.out.println("Jogo carregado com sucesso.");
            return data;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar jogo: " + e.getMessage());
            return null;
        }
    }
}
