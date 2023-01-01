package net.william278.husktowns.command;

import net.william278.husktowns.user.CommandUser;
import net.william278.husktowns.user.ConsoleUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TabProvider {

    @Nullable
    List<String> suggest(@NotNull CommandUser user, @NotNull String[] args);

    default List<String> getSuggestions(@NotNull CommandUser user, @NotNull String[] args) {
        List<String> suggestions = suggest(user, args);
        if (suggestions == null) {
            suggestions = List.of();
        }
        return suggestions.stream()
                .filter(suggestion -> args.length == 0 || suggestion.startsWith(args[args.length - 1]))
                .toList();
    }

    static List<String> getMatchingNames(@Nullable String argument, @NotNull CommandUser user,
                                         @NotNull List<? extends Node> providers) {
        return providers.stream()
                .filter(command -> !(user instanceof ConsoleUser) || command.isConsoleExecutable())
                .map(Node::getName)
                .filter(commandName -> argument == null || argument.isBlank() || commandName.startsWith(argument.trim()))
                .toList();
    }


}
