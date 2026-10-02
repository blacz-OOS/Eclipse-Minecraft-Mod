package com.soldierskull.eclipse.item;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * BUGFIX (texto cortado): cada entrada desta lista vira uma pagina do
 * livro, e o Minecraft NAO quebra automaticamente pra pagina seguinte
 * o texto que nao cabe na area visivel - o excesso so some, sem erro.
 * As chaves de lang foram redivididas (28 topicos -> 57 paginas) pra
 * cada uma caber com folga: 190 caracteres OU 8 linhas visuais
 * estimadas, o que vier primeiro, o mesmo limite conferido nos dois
 * idiomas simultaneamente (en_us e pt_br) usando os MESMOS pontos de
 * corte - garante que a lista de paginas aqui vale pra qualquer idioma
 * que o jogador tenha selecionado. Nenhum conteudo foi resumido ou
 * removido, so redistribuido entre mais paginas.
 */
public class GrimoirePages {

    public static List<ITextComponent> getPages() {
        List<ITextComponent> pages = new ArrayList<>();

        // Capa
        pages.add(new TranslationTextComponent("grimoire.eclipse.cover.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.cover.2"));
        // Índice
        pages.add(new TranslationTextComponent("grimoire.eclipse.index.1"));

        // Seção I: Vampirismo
        pages.add(new TranslationTextComponent("grimoire.eclipse.vampire.1.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.vampire.1.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.vampire.2.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.vampire.2.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.vampire.3.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.vampire.3.2"));
        // Seção II: Lobisomem
        pages.add(new TranslationTextComponent("grimoire.eclipse.werewolf.1.1"));

        pages.add(new TranslationTextComponent("grimoire.eclipse.werewolf.2.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.werewolf.2.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.werewolf.3.1"));

        // Seção III: Caçadores
        pages.add(new TranslationTextComponent("grimoire.eclipse.hunters.1.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.hunters.1.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.hunters.2.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.hunters.2.2"));

        // Seção IV: Cultistas
        pages.add(new TranslationTextComponent("grimoire.eclipse.cultists.1.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.cultists.1.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.cultists.2.1"));

        // Seção V: Bestiário
        pages.add(new TranslationTextComponent("grimoire.eclipse.bestiary.1.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.bestiary.1.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.bestiary.2.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.bestiary.2.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.bestiary.3.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.bestiary.3.2"));


        // Seção VII: Rituais e Portal
        pages.add(new TranslationTextComponent("grimoire.eclipse.rituals.1.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.rituals.1.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.rituals.2.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.rituals.2.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.rituals.3.1"));


        // Seção X: A Forja de Prata
        pages.add(new TranslationTextComponent("grimoire.eclipse.silver_forge.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.silver_forge.2"));
        // Seção XI: Comandos
        pages.add(new TranslationTextComponent("grimoire.eclipse.commands.1"));
        // Seção XII: Fim
        pages.add(new TranslationTextComponent("grimoire.eclipse.final.1"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.final.2"));
        pages.add(new TranslationTextComponent("grimoire.eclipse.final.3"));



        return pages;
    }
}
