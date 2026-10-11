package ecr.ac.tec.lenguajes.pokerServer.table.mapper;

import ecr.ac.tec.lenguajes.pokerServer.game.GameType;
import ecr.ac.tec.lenguajes.pokerServer.table.TableConductor;
import java.util.Map;

public class MapTableConductorMapper implements TableConductorMapper {
    private final Map<GameType, TableConductor> tables;

    public MapTableConductorMapper(Map<GameType, TableConductor> tables) {
        this.tables = tables;
    }

    @Override
    public TableConductor get(GameType type) {
        return tables.get(type);
    }
}
