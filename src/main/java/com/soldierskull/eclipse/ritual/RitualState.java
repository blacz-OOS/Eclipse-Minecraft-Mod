package com.soldierskull.eclipse.ritual;

/**
 * Estado de execução de um ritual num altar específico. Vive no
 * TileEntity do altar (nunca em {@code PlayerStats} nem no
 * {@code RitualManager} - decisão confirmada, Bloco B/#16).
 *
 * <pre>
 * INACTIVE  - nada preparado (sem círculo válido, sem ingrediente etc.)
 * READY     - círculo + ingrediente + power + condições OK, aguardando
 *             o jogador confirmar o início (clique de mão vazia)
 * RUNNING   - ritual em andamento, progressTicks avançando
 * COMPLETED - terminou com sucesso (transitório, ~1-2s de efeito visual,
 *             depois volta sozinho pra INACTIVE)
 * FAILED    - foi interrompido/invalidado (mesmo transitório de
 *             COMPLETED antes de voltar a INACTIVE)
 * </pre>
 */
public enum RitualState {

    INACTIVE,
    READY,
    RUNNING,
    COMPLETED,
    FAILED;

    /** COMPLETED e FAILED são estados transitórios (ver Bloco B/#16). */
    public boolean isTransient() {
        return this == COMPLETED || this == FAILED;
    }
}
