package me.basiqueevangelist.multicam.client.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class MsTimeArgumentType implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0ms", "0");
    private static final SimpleCommandExceptionType INVALID_UNIT_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.time.invalid_unit"));
    private static final Dynamic2CommandExceptionType TICK_COUNT_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType(
        (value, minimum) -> Text.stringifiedTranslatable("argument.time.tick_count_too_low", minimum, value)
    );
    private static final Object2IntMap<String> UNITS = new Object2IntOpenHashMap<>();
    final int minimum;

    private MsTimeArgumentType(int minimum) {
        this.minimum = minimum;
    }

    public static MsTimeArgumentType time() {
        return new MsTimeArgumentType(0);
    }

    public static MsTimeArgumentType time(int minimum) {
        return new MsTimeArgumentType(minimum);
    }

    public Integer parse(StringReader stringReader) throws CommandSyntaxException {
        float f = stringReader.readFloat();
        String string = stringReader.readUnquotedString();
        int i = UNITS.getOrDefault(string, 0);
        if (i == 0) {
            throw INVALID_UNIT_EXCEPTION.createWithContext(stringReader);
        } else {
            int j = Math.round(f * i);
            if (j < this.minimum) {
                throw TICK_COUNT_TOO_LOW_EXCEPTION.createWithContext(stringReader, j, this.minimum);
            } else {
                return j;
            }
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getRemaining());

        try {
            stringReader.readFloat();
        } catch (CommandSyntaxException var5) {
            return builder.buildFuture();
        }

        return CommandSource.suggestMatching(UNITS.keySet(), builder.createOffset(builder.getStart() + stringReader.getCursor()));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    static {
        UNITS.put("d", 24000 * 50);
        UNITS.put("s", 1000);
        UNITS.put("t", 50);
        UNITS.put("ms", 1);
        UNITS.put("", 1);
    }
}
