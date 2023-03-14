package me.vetustus.server.simplechat.mixin;

import me.vetustus.server.simplechat.SimpleChat;
import me.vetustus.server.simplechat.api.event.PlayerChatCallback;
import net.minecraft.SharedConstants;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.network.message.MessageChainTaskQueue;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler implements ServerPlayPacketListener {

    @Shadow
    private ServerPlayerEntity player;
    @Final
    @Shadow
    private MinecraftServer server;

    @Final
    @Shadow
    private MessageChainTaskQueue messageChainTaskQueue;

    @Shadow
    public abstract void disconnect(Text reason);

    @Shadow
    public abstract SignedMessage getSignedMessage(ChatMessageC2SPacket packet);

    @Shadow
    public abstract CompletableFuture<FilteredMessage> filterText(String text);

    @Shadow
    public abstract void handleDecoratedMessage(SignedMessage message);

    @Shadow
    protected abstract boolean canAcceptMessage(SignedMessage signedMessage);

    @Shadow
    protected abstract boolean canAcceptMessage(String message, Instant timestamp, LastSeenMessageList.Acknowledgment acknowledgment);

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (hasIllegalCharacter(packet.chatMessage())) {
            this.disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
        } else {
            if (this.canAcceptMessage(packet.chatMessage(), packet.timestamp(), packet.acknowledgment())) {

                if (!packet.chatMessage().startsWith("/")) {
                    String string = StringUtils.normalizeSpace(packet.chatMessage());
                    PlayerChatCallback.ChatMessage message = PlayerChatCallback.EVENT.invoker().result(player, string);
                    if (!message.isCancelled()) {
                        SignedMessage signedMessage = getSignedMessage(packet);
                        server.getPlayerManager().broadcast(signedMessage.withUnsignedContent(
                                Objects.requireNonNull(Text.Serializer.fromJson("[{\"text\":\"" + message.getMessage() + "\"}]"))
                        ), player, MessageType.params(MessageType.CHAT, player));
                    }
                } else {
                    server.submit(() -> {
                        SignedMessage signedMessage = this.getSignedMessage(packet);
                        if (this.canAcceptMessage(signedMessage)) {
                            messageChainTaskQueue.append(() -> {
                                CompletableFuture<FilteredMessage> completableFuture = this.filterText(signedMessage.getSignedContent().plain());
                                CompletableFuture<SignedMessage> completableFuture2 = server.getMessageDecorator().decorate(player, signedMessage);
                                return CompletableFuture.allOf(completableFuture, completableFuture2).thenAcceptAsync((void_) -> {
                                    FilterMask filterMask = ((FilteredMessage)completableFuture.join()).mask();
                                    SignedMessage signedMessageMask = ((SignedMessage)completableFuture2.join()).withFilterMask(filterMask);
                                    this.handleDecoratedMessage(signedMessageMask);
                                }, this.server);
                            });
                        }
                    });
                }
            }

        }

        ci.cancel();
    }

    private static boolean hasIllegalCharacter(String message) {
        for (int i = 0; i < message.length(); ++i) {
            if (!SharedConstants.isValidChar(message.charAt(i))) {
                return true;
            }
        }

        return false;
    }
}
