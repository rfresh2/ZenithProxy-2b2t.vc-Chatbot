package vc.bot.cmd;

import vc.bot.cmd.impl.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdRegistry {
    public static final CmdRegistry INSTANCE = new CmdRegistry();

    private final List<Cmd> cmds = List.of(
        new BotCmd(),
        new CoordsCmd(),
        new FirstDeathCmd(),
        new FirstKillCmd(),
        new FirstSeenCmd(),
        new FirstWordsCmd(),
        new HelpCmd(),
        new KillDeathCmd(),
        new LastSeenCmd(),
        new NoLifeCmd(),
        new PlaytimeCmd(),
        new PrioPlayersCmd(),
        new PrioSlotsCmd(),
        new QueueCmd(),
        new QuoteCmd(),
        new ReportCmd(),
        new TimeCmd(),
        new TpCmd(),
        new WordCountCmd()
    );
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
        for (var cmd : cmds) {
            registerCmd(cmd);
        }
    }
}
