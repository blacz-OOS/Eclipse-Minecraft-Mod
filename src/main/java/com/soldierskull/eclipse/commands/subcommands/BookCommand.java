package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * /eclipse book - entrega um Livro Escrito com tutorial de todas as
 * mecânicas FUNCIONAIS do mod (nada aspiracional/planejado - só o que
 * já está implementado e rodando). Requer OP porque todo o comando
 * /eclipse já exige permissão 2 (ver EclipseCommand.register).
 */
public class BookCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("book")

                .executes(context -> {

                    ServerPlayerEntity player;
                    try {
                        player = context.getSource().getPlayerOrException();
                    } catch (CommandSyntaxException e) {
                        context.getSource().sendFailure(new StringTextComponent("Este comando só pode ser usado por um jogador."));
                        return 0;
                    }

                    ItemStack book = createBook();
                    if (!player.addItem(book)) {
                        player.drop(book, false);
                    }
                    context.getSource().sendSuccess(new StringTextComponent("Grimório do Eclipse entregue."), false);

                    return 1;

                });
    }

    /** Público - reaproveitado por {@code ModItemGroup} pra o livro aparecer na aba criativa também. */
    public static ItemStack createBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundNBT nbt = book.getOrCreateTag();
        nbt.putString("title", "Grimorio do Eclipse");
        nbt.putString("author", "Eclipse");
        nbt.putInt("generation", 0);

        ListNBT pages = new ListNBT();
        for (String page : PAGES) {
            pages.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(page))));
        }
        nbt.put("pages", pages);
        return book;
    }

    private static final String[] PAGES = {

        "GRIMORIO DO ECLIPSE\n\nGuia de todas as mecanicas do mod, geradas a partir do que existe e funciona hoje.\n\nPagina seguinte: Racas.",

        "RACAS\n\nTodo jogador comeca Humano. Existem duas racas sobrenaturais: Vampiro e Lobisomem.\n\nA raca define parte do seu poder, suas skills e sua progressao propria (nivel racial, XP racial).",

        "TRANSFORMACAO\n\nAcontece sozinha, sem ritual: quando sua Afinidade (Vampirica ou Lupina) chega a 50 E voce esta na dimensao do Abismo, a transformacao comeca automaticamente.\n\nDura 5 dias de jogo. Se voce sair do Abismo antes de terminar, o processo e cancelado.",

        "AFINIDADES\n\nDuas: Vampirica e Lupina. Sobem conforme voce age de acordo com aquele caminho (o que exatamente aumenta cada uma depende das suas acoes no mundo).\n\nAfinidade 50+ e o gatilho pra Transformacao (ver pagina anterior).",

        "FACCOES\n\nDuas: Cacadores e Cultistas. Nenhuma e o padrao inicial.\n\nRegra fixa: Cacadores so aceita jogador Humano. Cultistas nao tem essa restricao - da pra ser Vampiro+Cultista ou Lobisomem+Cultista ao mesmo tempo.",

        "PROGRESSAO DE FACCAO\n\nCada faccao tem nivel (0 a 12) e XP proprio, junto com pontos de habilidade de faccao. Sobe fazendo XP de faccao (missoes, rituais especificos).\n\nNivel 12 = nivel maximo, exigido por alguns rituais avancados.",

        "REPUTACAO\n\nSeparada de Afinidade e Faccao. Existe reputacao (0-100) com 4 grupos: Cacadores, Vampiros, Lobisomens, Cultistas.\n\nGanhar reputacao com um grupo pode reduzir a de grupos rivais - e um sistema interligado, nao independente.",

        "HABILIDADES (SKILLS)\n\nCada raca e cada faccao tem sua propria arvore de habilidades, desbloqueadas gastando pontos (ganhos subindo de nivel racial/de faccao).\n\nExistem arvores de Vampiro, Lobisomem, Cacador e Cultista.",

        "ENERGIA\n\nDois tipos no PlayerStats: Common Energy e Corruption, cada uma com seu maximo.\n\nCorruption pode ser convertida em Common Energy. E tambem a energia que voce gasta no ritual Channel Power (ver pagina de Rituais).",

        "QUESTS E LIVROS\n\nExistem 4 livros de missao: Vampire Tome, Werewolf Tome, Hunter Journal, Forbidden Tome (Cultistas).\n\nClique direito no livro certo pra aceitar ou entregar uma missao da sua raca/faccao. Sem missao pendente, o livro lista os RITUAIS conhecidos daquela categoria.",

        "RITUAIS - VISAO GERAL\n\nUm ritual precisa de: um Altar, um circulo desenhado no chao (ou a propria estrutura do altar), o ingrediente certo, e Ritual Power suficiente no altar.\n\nRitual Power e do ALTAR, separado da sua energia pessoal.",

        "OS 4 ALTARES\n\nAltar (comum): rituais gerais, 100 de Ritual Power.\nBlood Altar: sangue/vampirismo, 250 de power.\nMoon Altar: lua/lobisomem, 250 de power - o circulo dele e um anel de pedra natural, sem giz.\nAbyss Altar: multibloco 9x9, 1000 de power, so funciona 100% completo.",

        "CIRCULOS E GIZ\n\nRitual Chalk desenha circulo pra rituais gerais. Blood Chalk, pra rituais de sangue. Ha 3 tamanhos: 7x7, 11x11, 15x15, dependendo do ritual.\n\nSem circulo valido, o altar nunca fica pronto pra iniciar.",

        "RITUAL: CHANNEL POWER\n\nO ritual mais basico - converte 50 de Corruption sua em 25 de Ritual Power pro altar (2 pra 1, com perda). Precisa ficar dentro do circulo ate terminar (5 segundos).\n\nExiste em qualquer altar - e o unico jeito de carregar um altar.",

        "RITUAL: ABYSS CRYSTAL CHARGING\n\nNo Altar comum, de noite: 1 Abyss Crystal (no altar) + 2 Abyss Dust (jogados no circulo) + 10 de Ritual Power -> 1 Charged Abyss Crystal (nasce com 3 cargas).",

        "RITUAL: CULTIST INITIATION\n\nNo Abyss Altar, de noite, so pra quem ja e Cultista: 1 Abyss Shard + 2 Abyss Dust + 100 de Ritual Power -> ganha XP de faccao Cultista.\n\nNao transforma raca - so avanca o nivel de Cultista.",

        "RITUAL: ECLIPSE RITUAL\n\nO mais poderoso, so no Abyss Altar: exige Cultista de nivel MAXIMO (12), de noite, 3 Abyss Shard + 4 Abyss Dust + 500 de Ritual Power, 30 segundos parado perto do altar.\n\nDispara o Evento Eclipse (proxima pagina).",

        "EVENTO ECLIPSE\n\nTrava o relogio do mundo em ciclo noturno por um tempo (hoje sem escurecer visualmente um dia ja em curso - e uma aproximacao funcional, nao um eclipse visual de verdade).\n\nPermite mecanicas 'so de noite' continuarem ativas o tempo todo.",

        "RITUAIS QUE FALHAM\n\nSe o circulo quebrar, faltar ingrediente, ou uma condicao deixar de bater, o ritual falha. Ingredientes nao voltam a ser gastos numa falha.\n\nSe ja havia bastante progresso feito, pode vir um Backlash: dano e um efeito ruim, calibrado pela categoria do ritual (mais leve nos gerais, mais pesado nos abissais/proibidos).",

        "ARMADILHA DE CACADOR\n\nItem/habilidade que planta uma armadilha de verdade no chao. Atrai criaturas sobrenaturais num raio de 10 blocos; se uma chegar perto o bastante, leva Lentidao extrema e a armadilha e consumida (some do mundo).",

        "HUNTER BADGE\n\nFunciona como uma mochila pequena: 7 slots, mas so aceita item de cacador (prata, agua benta, arco especial de cacador, armadilha, o proprio Hunter Journal).\n\nQualquer outro item e recusado pelos slots.",

        "ESTRUTURAS DO MUNDO\n\nAs Abyssal Ruins geram naturalmente no mundo - uma estrutura jigsaw ligada ao tema do Abismo.\n\nFim do grimorio. Novas mecanicas adicionadas ao mod exigem uma nova copia deste livro pra aparecerem aqui."

    };
}
