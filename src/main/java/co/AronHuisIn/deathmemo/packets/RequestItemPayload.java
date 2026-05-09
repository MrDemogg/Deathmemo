package co.AronHuisIn.deathmemo.packets;

import co.AronHuisIn.deathmemo.Deathmemo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record RequestItemPayload(ItemStack item) implements CustomPacketPayload {
    public static final Type<RequestItemPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, "request_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestItemPayload> STREAM_CODEC =
            StreamCodec.composite(ItemStack.STREAM_CODEC, RequestItemPayload::item, RequestItemPayload::new);

    @SuppressWarnings("NullableProblems")
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
