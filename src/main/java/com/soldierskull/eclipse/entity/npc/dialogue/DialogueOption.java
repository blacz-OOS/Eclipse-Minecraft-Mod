package com.soldierskull.eclipse.entity.npc.dialogue;

/** Uma opção de diálogo: o texto mostrado + o que acontece se o jogador clicar (Etapa 3). */
public class DialogueOption {

    private final String label;
    private final DialogueAction action;

    public DialogueOption(String label, DialogueAction action) {
        this.label = label;
        this.action = action;
    }

    public String getLabel() {
        return this.label;
    }

    public DialogueAction getAction() {
        return this.action;
    }
}
