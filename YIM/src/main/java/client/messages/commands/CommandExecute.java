package client.messages.commands;

import client.MapleClient;
import constants.ServerConstants.CommandType;

public abstract class CommandExecute {

    public abstract int execute(MapleClient paramMapleClient, String[] paramArrayOfString);

    public CommandType getType() {
        return CommandType.NORMAL;
    }

    public static abstract class PokemonExecute extends CommandExecute {

        public CommandType getType() {
            return CommandType.POKEMON;
        }
    }

    public static abstract class TradeExecute extends CommandExecute {

        public CommandType getType() {
            return CommandType.TRADE;
        }
    }

    static enum ReturnValue {

        DONT_LOG,
        LOG;
    }
}
