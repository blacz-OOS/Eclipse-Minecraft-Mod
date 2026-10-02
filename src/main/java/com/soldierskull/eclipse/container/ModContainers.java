package com.soldierskull.eclipse.container;

import com.soldierskull.eclipse.Eclipse;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.Hand;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, Eclipse.MOD_ID);

    public static final RegistryObject<ContainerType<HunterBadgeContainer>> HUNTER_BADGE = CONTAINERS.register("hunter_badge",
            () -> IForgeContainerType.create((windowId, inv, data) -> new HunterBadgeContainer(windowId, inv, data.readEnum(Hand.class))));

    private ModContainers() {
    }

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
