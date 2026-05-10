package co.AronHuisIn.deathmemo.packets;

import co.AronHuisIn.deathmemo.Deathmemo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestResponsePayload(boolean approved, Component message) implements CustomPacketPayload {
    public static final Type<RequestResponsePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, "request_response"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestResponsePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    RequestResponsePayload::approved,
                    ComponentSerialization.STREAM_CODEC,
                    RequestResponsePayload::message,
                    RequestResponsePayload::new
            );


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
