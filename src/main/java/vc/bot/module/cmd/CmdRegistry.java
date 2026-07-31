package vc.bot.module.cmd;

import vc.bot.module.cmd.impl.JoinDateCmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdRegistry {
    public static final CmdRegistry INSTANCE = new CmdRegistry();

    private final Map<String, Cmd> cmdMap;

    private CmdRegistry() {
        cmdMap = new HashMap<>();
        registerAllCmds();
    }

    public Map<String, Cmd> cmds() {
        return cmdMap;
    }

    public void registerCmd(Cmd cmd) {
        for (var a : cmd.cmdAliases()) {
            cmdMap.put(a, cmd);
        }
    }

    private void registerAllCmds() {
        var cmds = List.of(
            new JoinDateCmd()
        );
        for (var cmd : cmds) {
            registerCmd(cmd);
        }
    }
}
