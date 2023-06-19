package me.vetustus.server.simplechat.mixin;

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
import java.util.Optional;
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
    public abstract SignedMessage getSignedMessage(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException;

    @Shadow
    public abstract Optional<LastSeenMessageList> validateMessage(String message, Instant timestamp, LastSeenMessageList.Acknowledgment acknowledgment);

    @Shadow
    public abstract CompletableFuture<FilteredMessage> filterText(String text);

    @Shadow
    public abstract void handleDecoratedMessage(SignedMessage message);

    @Shadow
    public abstract void handleMessageChainException(MessageChain.MessageChainException exception);

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (hasIllegalCharacter(packet.chatMessage())) {
            disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
        } else {
            Optional<LastSeenMessageList> optional = validateMessage(packet.chatMessage(), packet.timestamp(), packet.acknowledgment());
            if (optional.isPresent()) {
                if (!packet.chatMessage().startsWith("/")) {
                    String string = StringUtils.normalizeSpace(packet.chatMessage());
                    PlayerChatCallback.ChatMessage message = PlayerChatCallback.EVENT.invoker().result(player, string);
                    if (!message.isCancelled()) {
                        SignedMessage signedMessage;
                        try {
                            signedMessage = getSignedMessage(packet, (LastSeenMessageList) optional.get());
                            server.getPlayerManager().broadcast(signedMessage.withUnsignedContent(
                                    Objects.requireNonNull(Text.Serializer.fromJson("[{\"text\":\"" + message.getMessage() + "\"}]"))
                            ), player, MessageType.params(MessageType.CHAT, player));
                        } catch (MessageChain.MessageChainException e) {
                            handleMessageChainException(e);
                        }
                    }
                } else {
                    server.submit(() -> {
                        SignedMessage signedMessage;
                        try {
                            signedMessage = getSignedMessage(packet, (LastSeenMessageList) optional.get());
                        } catch (MessageChain.MessageChainException e) {
                            handleMessageChainException(e);
                            return;
                        }

                        CompletableFuture<FilteredMessage> completableFuture = filterText(signedMessage.getSignedContent());
                        CompletableFuture<Text> completableFuture2 = server.getMessageDecorator().decorate(player, signedMessage.getContent());
                        messageChainTaskQueue.append((executor) -> CompletableFuture.allOf(completableFuture, completableFuture2).thenAcceptAsync((void_) -> {
                            SignedMessage signedMessage2 = signedMessage.withUnsignedContent((Text) completableFuture2.join()).withFilterMask(((FilteredMessage) completableFuture.join()).mask());
                            handleDecoratedMessage(signedMessage2);
                        }, executor));
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
