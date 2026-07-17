package net.bzkgns.theFloorIsLavaManager.managers;

import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class SpreadEntityManager {

    private final Random random = new Random();

    public <T extends Entity> boolean spread(
            Collection<T> entities,
            Location center,
            double maxRadius,
            double minDistance,
            boolean respectTeams,
            int maxY
    ) {
        List<List<T>> groups = createGroups(entities, respectTeams);

        List<Location> positions = generatePositions(
                groups.size(),
                center,
                maxRadius,
                minDistance,
                maxY
        );

        if (positions.size() != groups.size()) {
            return false;
        }

        for (int i = 0; i < groups.size(); i++) {
            Location pos = positions.get(i);

            for (Entity player : groups.get(i)) {
                player.teleport(pos);
            }
        }

        return true;
    }
    private <T extends Entity> List<List<T>> createGroups(
            Collection<T> entities,
            boolean teams
    ) {

        if (!teams) {
            return entities.stream()
                    .map(List::of)
                    .collect(Collectors.toList());
        }

        Map<Team, List<T>> map = new HashMap<>();

        for (T e : entities) {

            Team team = Bukkit.getScoreboardManager()
                    .getMainScoreboard()
                    .getEntryTeam(e.getName());

            if (team == null) {
                map.computeIfAbsent(null, _ -> new ArrayList<>())
                        .add(e);
            } else {
                map.computeIfAbsent(team, _ -> new ArrayList<>())
                        .add(e);
            }
        }

        return new ArrayList<>(map.values());
    }
    private List<Location> generatePositions(
            int amount,
            Location center,
            double radius,
            double minDistance,
            int maxY
    ) {

        List<Location> result = new ArrayList<>();

        int attempts = 10000;


        while(result.size() < amount && attempts-- > 0) {

            double angle = random.nextDouble() * Math.PI * 2;
            double distance = random.nextDouble() * radius;


            double x =
                    center.getX()
                            + Math.cos(angle) * distance;

            double z =
                    center.getZ()
                            + Math.sin(angle) * distance;


            Location loc = findSurface(
                    center.getWorld(),
                    x,
                    z,
                    maxY
            );


            if(loc == null)
                continue;


            boolean valid = true;

            for(Location other : result) {
                if(other.distance(loc) < minDistance) {
                    valid = false;
                    break;
                }
            }


            if(valid)
                result.add(loc);
        }


        return result;
    }
    private Location findSurface(World world, double x, double z, int maxY) {
        int bx = (int) Math.floor(x);
        int bz = (int) Math.floor(z);

        int y = world.getHighestBlockYAt(
                bx,
                bz,
                HeightMap.WORLD_SURFACE
        );

        for (int currentY = y; currentY > world.getMinHeight(); currentY--) {

            Block ground = world.getBlockAt(bx, currentY, bz);
            Block above = world.getBlockAt(bx, currentY + 1, bz);
            Block above2 = world.getBlockAt(bx, currentY + 2, bz);

            if (currentY > maxY) {
                break;
            }

            // Bloc sur lequel on peut marcher
            if (ground.getType().isSolid()
                    && !ground.isLiquid()
                    && above.isPassable() && !above.isLiquid()
                    && above2.isPassable() && !above2.isLiquid()) {

                return new Location(
                        world,
                        bx + 0.5,
                        currentY + 1,
                        bz + 0.5
                );
            }
        }

        return null;
    }
}
