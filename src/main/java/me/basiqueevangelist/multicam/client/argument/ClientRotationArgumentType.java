package me.basiqueevangelist.multicam.client.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;

public class ClientRotationArgumentType implements ArgumentType<ClientPosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
    public static final SimpleCommandExceptionType INCOMPLETE_ROTATION_EXCEPTION = new SimpleCommandExceptionType(
        Text.translatable("argument.rotation.incomplete")
    );

    public static ClientRotationArgumentType rotation() {
        return new ClientRotationArgumentType();
    }

    public static ClientPosArgument getRotation(CommandContext<FabricClientCommandSource> context, String name) {
        return context.getArgument(name, ClientPosArgument.class);
    }

    public ClientPosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
        } else {
            CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader, false);
            if (stringReader.canRead() && stringReader.peek() == ' ') {
                stringReader.skip();
                CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader, false);
                return new ClientDefaultPosArgument(coordinateArgument2, coordinateArgument, new CoordinateArgument(true, 0.0));
            } else {
                stringReader.setCursor(i);
                throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
            }
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

