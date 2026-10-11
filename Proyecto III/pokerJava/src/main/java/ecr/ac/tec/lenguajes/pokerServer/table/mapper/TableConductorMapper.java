package ecr.ac.tec.lenguajes.pokerServer.table.mapper;

import ecr.ac.tec.lenguajes.pokerServer.game.GameType;
import ecr.ac.tec.lenguajes.pokerServer.table.TableConductor;

public interface TableConductorMapper {
    TableConductor get(GameType date);
}
