package co.AronHuisIn.deathmemo.packets;

import co.AronHuisIn.deathmemo.Deathmemo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CommandRequestPayload(String command) implements CustomPacketPayload {
    public static final Type<CommandRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, "command_request"));

    public static final StreamCodec<ByteBuf, CommandRequestPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    CommandRequestPayload::command,
                    CommandRequestPayload::new
            );


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
