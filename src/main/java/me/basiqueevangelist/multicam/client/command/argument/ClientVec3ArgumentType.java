package me.basiqueevangelist.multicam.client.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class ClientVec3ArgumentType implements ArgumentType<ClientPosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
    private final boolean centerIntegers;

    public ClientVec3ArgumentType(boolean centerIntegers) {
        this.centerIntegers = centerIntegers;
    }

    public static ClientVec3ArgumentType vec3() {
        return new ClientVec3ArgumentType(true);
    }

    public static ClientVec3ArgumentType vec3(boolean centerIntegers) {
        return new ClientVec3ArgumentType(centerIntegers);
    }

    public static Vec3d getVec3(CommandContext<FabricClientCommandSource> context, String name) {
        return context.getArgument(name, ClientPosArgument.class).toAbsolutePos(context.getSource());
    }

    public static ClientPosArgument getPosArgument(CommandContext<FabricClientCommandSource> context, String name) {
        return context.getArgument(name, ClientPosArgument.class);
    }

    public ClientPosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        return stringReader.canRead() && stringReader.peek() == '^'
            ? ClientLookingPosArgument.parse(stringReader)
            : ClientDefaultPosArgument.parse(stringReader, this.centerIntegers);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof CommandSource)) {
            return Suggestions.empty();
        } else {
            String string = builder.getRemaining();
            Collection<CommandSource.RelativePosition> collection;
            if (!string.isEmpty() && string.charAt(0) == '^') {
                collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
            } else {
                collection = ((CommandSource)context.getSource()).getPositionSuggestions();
            }

            return CommandSource.suggestPositions(string, collection, builder, CommandManager.getCommandValidator(this::parse));
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

