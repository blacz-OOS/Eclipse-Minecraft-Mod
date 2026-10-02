package com.soldierskull.eclipse.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("eclipse", "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals);

        int id = 0;

        INSTANCE.registerMessage(id++, PacketAddStat.class,
                PacketAddStat::encode, PacketAddStat::decode, PacketAddStat::handle);

        INSTANCE.registerMessage(id++, PacketSyncStats.class,
                PacketSyncStats::encode, PacketSyncStats::decode, PacketSyncStats::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(id++, PacketConvertEnergy.class,
                PacketConvertEnergy::encode, PacketConvertEnergy::decode, PacketConvertEnergy::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // New: client -> server request for an immediate, fresh sync.
        // Used by StatsScreen.init() so the screen never shows stale data.
        INSTANCE.registerMessage(id++, PacketRequestStats.class,
                PacketRequestStats::encode, PacketRequestStats::decode, PacketRequestStats::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(id++, PacketActivateSkill.class,
                PacketActivateSkill::encode, PacketActivateSkill::decode, PacketActivateSkill::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // Irmao gemeo do PacketActivateSkill, usado pela SkillTreeScreen
        // pra gastar ponto e desbloquear um no da arvore.
        INSTANCE.registerMessage(id++, PacketUnlockSkill.class,
                PacketUnlockSkill::encode, PacketUnlockSkill::decode, PacketUnlockSkill::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
