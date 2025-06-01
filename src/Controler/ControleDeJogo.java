package Controler;

import Auxiliar.Posicao;
import Modelo.BlocoMortal;
import Modelo.Chaser;
import Modelo.Chave;
import Modelo.Hero;
import Modelo.Personagem;
import Modelo.Porta;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import Auxiliar.Consts;
import java.util.Random;

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
            if (pIesimoPersonagem.isbTransponivel()) {
                if (pIesimoPersonagem.isbMortal()) {
                    System.out.println("Herói foi atingido por inimigo!");
                    
                    if (!hero.estaMorto()) {
                        hero.perdeUmaVida();
                        System.out.println("Herói perdeu uma vida. Vidas restantes: " + hero.getVidas());
                        
                        // Reposiciona o herói na posição inicial
                        this.tela.resetaTela();
                        hero.setPosicao(0, 0);
                        
                        // NOVO: Reposiciona todos os Chasers quando o herói perde uma vida
                        reposicionarChasers(umaFase, hero);
                        
                        // Se ainda tem vidas, continua na mesma fase
                        if (!hero.estaMorto()) {
                            desenhaTudo(umaFase);
                        }
                    }
                    
                    // CORREÇÃO: Se o herói morreu completamente, vai para tela de morte
                    if (hero.estaMorto()) {
                        System.out.println("GAME OVER - Herói morreu!");
                        
                        // Para o timer
                        tela.pararTimer();
                        
                        // Vai para a tela de morte (tela 6)
                        tela.setTelaAtualNumero(5);
                        
                        // Reposiciona herói e reseta câmera
                        this.tela.resetaTela();
                        hero.setPosicao(0, 0);
                        
                        System.out.println("Pressione 'A' para jogar novamente");
                    }
                }
            }
        }
    }
    
    /**
     * NOVO: Calcula a pontuação com bônus por vidas e tempo restante
     */
    private int calcularPontuacaoComBonus(Hero hero) {
        int pontuacaoBase = 1; // 1 ponto por fase completada
        int bonusVidas = 0;
        int bonusTempo = 0;
        
        // Bônus por vidas restantes: 5 pontos por vida
        bonusVidas = hero.getVidas() * 5;
        
        // Calcula bônus por tempo restante
        if (tela.isTimerAtivo()) {
            long tempoAtual = System.currentTimeMillis();
            long tempoInicio = tela.getTempoInicio();
            int tempoLimite = tela.getFaseAtual().getTempoLimite();
            
            int tempoPassado = (int)((tempoAtual - tempoInicio) / 1000);
            int tempoRestante = tempoLimite - tempoPassado;
            
            // Calcula a porcentagem do tempo restante
            double porcentagemTempoRestante = (double) tempoRestante / tempoLimite * 100;
            
            System.out.println("DEBUG: Tempo limite: " + tempoLimite + "s");
            System.out.println("DEBUG: Tempo passado: " + tempoPassado + "s");
            System.out.println("DEBUG: Tempo restante: " + tempoRestante + "s");
            System.out.println("DEBUG: Porcentagem tempo restante: " + String.format("%.1f", porcentagemTempoRestante) + "%");
            
            // Aplica bônus por faixas de tempo
            if (porcentagemTempoRestante >= 80) {
                bonusTempo += 10;
                System.out.println("DEBUG: Bônus 80%+ tempo: +10 pontos");
            }
            if (porcentagemTempoRestante >= 60) {
                bonusTempo += 10;
                System.out.println("DEBUG: Bônus 60%+ tempo: +10 pontos");
            }
            if (porcentagemTempoRestante >= 40) {
                bonusTempo += 10;
                System.out.println("DEBUG: Bônus 40%+ tempo: +10 pontos");
            }
            if (porcentagemTempoRestante >= 20) {
                bonusTempo += 10;
                System.out.println("DEBUG: Bônus 20%+ tempo: +10 pontos");
            }
        }
        
        int pontuacaoTotal = pontuacaoBase + bonusVidas + bonusTempo;
        
        System.out.println("=== CÁLCULO DE PONTUAÇÃO ===");
        System.out.println("Pontuação base (fase): " + pontuacaoBase);
        System.out.println("Bônus vidas (" + hero.getVidas() + " x 5): " + bonusVidas);
        System.out.println("Bônus tempo: " + bonusTempo);
        System.out.println("TOTAL GANHO NESTA FASE: " + pontuacaoTotal);
        System.out.println("============================");
        
        return pontuacaoTotal;
    }
    
    public void verificaPassouDeFase(ArrayList<Personagem> umaFase, Hero hero, Personagem pIesimoPersonagem) {
        if(!hero.getPosicao().igual(pIesimoPersonagem.getPosicao())) {
            return;
        }
        if(pIesimoPersonagem instanceof Porta) {
            Porta porta = (Porta) pIesimoPersonagem;
            if(porta.estaAberta()) {
                this.tela.resetaTela();
                
                // NOVO: Calcula pontuação com bônus
                int pontuacaoAtual = hero.getPontuacao();
                int pontosGanhos = calcularPontuacaoComBonus(hero);
                int novaPontuacao = pontuacaoAtual + pontosGanhos;

                System.out.println("DEBUG: Fase atual: " + this.tela.getTelaAtualNumero());
                System.out.println("DEBUG: Pontuação antes: " + pontuacaoAtual);
                System.out.println("DEBUG: Pontos ganhos: " + pontosGanhos);
                System.out.println("DEBUG: Nova pontuação total: " + novaPontuacao);
                
                // CORREÇÃO: Verifica se completou a última fase (fase 4 = índice 4)
                int proximaFase = this.tela.getTelaAtualNumero() + 1;
                
                if (proximaFase >= 5) {
                    // Completou todas as fases - mostra tela de vitória
                    hero.setPontuacao(novaPontuacao);
                    System.out.println("VITÓRIA! Todas as fases foram completadas!");
                    System.out.println("PONTUAÇÃO FINAL: " + hero.getPontuacao());
                    this.tela.pararTimer();
                    this.tela.setTelaAtualNumero(5); // Vai para tela de vitória
                    this.tela.mostrarVitoria(); // Novo método para forçar vitória
                } else {
                    // Ainda há fases para jogar
                    // PRIMEIRO define a nova pontuação
                    hero.setPontuacao(novaPontuacao);
                    System.out.println("DEBUG: Pontuação definida como: " + hero.getPontuacao());
                    
                    // DEPOIS carrega a próxima fase
                    this.tela.carregarTela(proximaFase);
                    
                    // VERIFICA se a pontuação foi mantida após carregar
                    System.out.println("DEBUG: Pontuação após carregar fase: " + hero.getPontuacao());
                    
                    // Se foi resetada, define novamente
                    if (hero.getPontuacao() != novaPontuacao) {
                        System.out.println("DEBUG: Pontuação foi resetada! Definindo novamente...");
                        hero.setPontuacao(novaPontuacao);
                    }
                }
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
        Posicao faseUmPosicaoVitoria = this.tela.getFaseAtual().getPosicaoVitoria();
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
    
    /**
     * NOVO: Reposiciona todos os Chasers para novas posições aleatórias
     * mantendo distância mínima do herói
     */
    private void reposicionarChasers(ArrayList<Personagem> umaFase, Hero hero) {
        System.out.println("=== REPOSICIONANDO CHASERS ===");
        
        // Encontra todos os Chasers na fase
        ArrayList<Chaser> chasers = new ArrayList<>();
        for (Personagem p : umaFase) {
            if (p instanceof Chaser) {
                chasers.add((Chaser) p);
            }
        }
        
        if (chasers.isEmpty()) {
            System.out.println("Nenhum Chaser encontrado para reposicionar.");
            return;
        }
        
        // Constrói lista de estradas válidas
        ArrayList<int[]> estradas = construirListaEstradas(umaFase);
        
        if (estradas.isEmpty()) {
            System.out.println("ERRO: Nenhuma estrada disponível para reposicionar Chasers!");
            return;
        }
        
        // Reposiciona cada Chaser
        for (int i = 0; i < chasers.size(); i++) {
            Chaser chaser = chasers.get(i);
            int[] novaPosicao = escolherPosicaoValidaParaChaser(estradas, hero, chasers, i);
            
            if (novaPosicao != null) {
                chaser.setPosicao(novaPosicao[0], novaPosicao[1]);
                System.out.println("Chaser " + (i+1) + " reposicionado para: " + 
                                 novaPosicao[0] + ", " + novaPosicao[1]);
            } else {
                System.out.println("AVISO: Não foi possível reposicionar Chaser " + (i+1));
            }
        }
        
        System.out.println("=== REPOSICIONAMENTO CONCLUÍDO ===");
    }

    /**
     * Constrói lista de todas as estradas (posições não ocupadas por paredes)
     */
    private ArrayList<int[]> construirListaEstradas(ArrayList<Personagem> umaFase) {
        // Cria mapa de paredes
        boolean[][] ehParede = new boolean[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
        
        for (Personagem p : umaFase) {
            if (p instanceof BlocoMortal) {
                int linha = p.getPosicao().getLinha();
                int coluna = p.getPosicao().getColuna();
                
                if (linha >= 0 && linha < Consts.MUNDO_ALTURA && 
                    coluna >= 0 && coluna < Consts.MUNDO_LARGURA) {
                    ehParede[linha][coluna] = true;
                }
            }
        }
        
        // Constrói lista de estradas
        ArrayList<int[]> estradas = new ArrayList<>();
        for (int i = 0; i < Consts.MUNDO_ALTURA; i++) {
            for (int j = 0; j < Consts.MUNDO_LARGURA; j++) {
                if (!ehParede[i][j]) {
                    estradas.add(new int[]{i, j});
                }
            }
        }
        
        return estradas;
    }

    /**
     * Escolhe posição válida para Chaser mantendo distância do herói e outros Chasers
     */
    private int[] escolherPosicaoValidaParaChaser(ArrayList<int[]> estradas, Hero hero, 
                                                ArrayList<Chaser> chasers, int chaserAtual) {
        int heroiLinha = hero.getPosicao().getLinha();
        int heroiColuna = hero.getPosicao().getColuna();
        
        ArrayList<int[]> posicoesValidas = new ArrayList<>();
        
        // Verifica todas as estradas disponíveis
        for (int[] estrada : estradas) {
            int linha = estrada[0];
            int coluna = estrada[1];
            
            // Calcula distância do herói
            int distanciaHeroi = Math.abs(linha - heroiLinha) + Math.abs(coluna - heroiColuna);
            
            // Verifica se está longe o suficiente do herói (mínimo 25)
            if (distanciaHeroi >= 25) {
                // Verifica distância de outros Chasers já posicionados
                boolean muitoProximoDeOutroChaser = false;
                
                for (int i = 0; i < chaserAtual; i++) {
                    Chaser outroChaser = chasers.get(i);
                    int outraLinha = outroChaser.getPosicao().getLinha();
                    int outraColuna = outroChaser.getPosicao().getColuna();
                    
                    int distanciaOutroChaser = Math.abs(linha - outraLinha) + Math.abs(coluna - outraColuna);
                    
                    // Mantém distância mínima de 10 entre Chasers
                    if (distanciaOutroChaser < 10) {
                        muitoProximoDeOutroChaser = true;
                        break;
                    }
                }
                
                if (!muitoProximoDeOutroChaser) {
                    posicoesValidas.add(new int[]{linha, coluna});
                }
            }
        }
        
        // Se não encontrou com distância 25, tenta com 15
        if (posicoesValidas.isEmpty()) {
            System.out.println("Tentando reposicionar com distância menor...");
            
            for (int[] estrada : estradas) {
                int linha = estrada[0];
                int coluna = estrada[1];
                
                int distanciaHeroi = Math.abs(linha - heroiLinha) + Math.abs(coluna - heroiColuna);
                
                if (distanciaHeroi >= 15) {
                    posicoesValidas.add(new int[]{linha, coluna});
                }
            }
        }
        
        // Se ainda não encontrou, aceita qualquer posição válida
        if (posicoesValidas.isEmpty()) {
            System.out.println("Usando qualquer posição válida para reposicionamento...");
            posicoesValidas.addAll(estradas);
        }
        
        if (posicoesValidas.isEmpty()) {
            return null;
        }
        
        // Escolhe posição aleatória
        Random random = new Random();
        int indiceAleatorio = random.nextInt(posicoesValidas.size());
        int[] posicaoEscolhida = posicoesValidas.get(indiceAleatorio);
        
        int distanciaFinal = Math.abs(posicaoEscolhida[0] - heroiLinha) + Math.abs(posicaoEscolhida[1] - heroiColuna);
        System.out.println("Nova posição escolhida: " + posicaoEscolhida[0] + ", " + posicaoEscolhida[1] + 
                         " (distância do herói: " + distanciaFinal + ")");
        
        return posicaoEscolhida;
    }
}
