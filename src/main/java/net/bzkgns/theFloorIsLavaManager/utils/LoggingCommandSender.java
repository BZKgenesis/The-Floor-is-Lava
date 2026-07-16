package net.bzkgns.theFloorIsLavaManager.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"deprecation","removal"})
public class LoggingCommandSender implements ConsoleCommandSender {

    private final ConsoleCommandSender delegate;
    private final List<Component> messages = new ArrayList<>();

    public LoggingCommandSender(ConsoleCommandSender delegate) {
        this.delegate = delegate;
    }

    public List<Component> getMessages() {
        return List.copyOf(messages);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        messages.add(message);
        delegate.sendMessage(message); // optionnel
    }

    @Override
    public void sendMessage(@NotNull String message) {
        messages.add(Component.text(message));
        delegate.sendMessage(message);
    }


    @Override
    public void sendMessage(@NotNull String... messages) {
        for (String message : messages) {
            this.messages.add(Component.text(message));
        }
        delegate.sendMessage(messages);
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
        messages.add(Component.text(message));
        delegate.sendMessage(sender, message);
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
        for (String message : messages) {
            this.messages.add(Component.text(message));
        }
        delegate.sendMessage(sender, messages);
    }

    @Override
    public @NotNull Server getServer() {
        return delegate.getServer();
    }

    @Override
    public @NotNull String getName() {
        return delegate.getName();
    }

    @Override
    public @NotNull Spigot spigot() {
        return delegate.spigot();
    }

    @Override
    public @NotNull Component name() {
        return delegate.name();
    }

    @Override
    public boolean isConversing() {
        return delegate.isConversing();
    }

    @Override
    public void acceptConversationInput(@NotNull String input) {
        delegate.acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return delegate.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        delegate.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
        delegate.abandonConversation(conversation, details);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        messages.add(Component.text(message));
        delegate.sendRawMessage(message);
    }

    @Override
    public void sendRawMessage(@Nullable UUID sender, @NotNull String message) {
        messages.add(Component.text(message));
        delegate.sendRawMessage(sender, message);
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return delegate.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return delegate.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return delegate.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return delegate.hasPermission(perm);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return delegate.addAttachment(plugin, name, value);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return delegate.addAttachment(plugin);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return delegate.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return delegate.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        delegate.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        delegate.recalculatePermissions();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return delegate.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return delegate.isOp();
    }

    @Override
    public void setOp(boolean value) {
        delegate.setOp(value);
    }
}
