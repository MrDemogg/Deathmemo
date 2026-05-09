package co.AronHuisIn.deathmemo.packets;

import co.AronHuisIn.deathmemo.Deathmemo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestItemResponsePayload(boolean approved, String message) implements CustomPacketPayload {
    public static final Type<RequestItemResponsePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, "request_item_response"));

    public static final StreamCodec<ByteBuf, RequestItemResponsePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    RequestItemResponsePayload::approved,
                    ByteBufCodecs.STRING_UTF8,
                    RequestItemResponsePayload::message,
                    RequestItemResponsePayload::new
            );


    @SuppressWarnings("NullableProblems")
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
