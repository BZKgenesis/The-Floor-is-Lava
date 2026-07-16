package net.bzkgns.theFloorIsLavaManager.managers;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import java.util.logging.Level;

import static net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager.*;

public class WorldManager {

    private final TheFloorIsLavaManager plugin;

    public WorldManager(TheFloorIsLavaManager plugin){
        this.plugin = plugin;
    }

    private boolean resettingWorld = false;

    public boolean isGameWorldLoaded = false;

    private Location oldSpawn;

    public void resetRandomWorld(){
        resetRandomWorld(0);
    }

    public void resetRandomWorld(long seed) {
        isGameWorldLoaded = false;
        plugin.getGameManager().stopGame();

        resettingWorld = true;

        World oldWorld = getGameWorld();
        List<Player> players;

        if(oldWorld == null){
            players = new ArrayList<>(Bukkit.getOnlinePlayers());
        }else{
            players = new ArrayList<>(oldWorld.getPlayers());
            World lobby = getLobbyWorld();

            if(lobby == null){
                resettingWorld = false;
                return;
            }

            players.forEach(p -> p.teleport(lobby.getSpawnLocation()));

            // On capture le dossier avant de décharger le monde pour être sûr du chemin
            File worldFolder = oldWorld.getWorldFolder();

            Bukkit.unloadWorld(oldWorld,false);
            deleteRecursively(worldFolder);
        }

        WorldCreator creator = new WorldCreator(GAME_WORLD);
        creator.seed(seed==0?new Random().nextLong():seed);

        recreateWorld(creator,players);
        isGameWorldLoaded = true;
    }

    private void recreateWorld(WorldCreator creator, List<Player> players) {
        recreateWorld(creator, players, true);
    }

    @SuppressWarnings("SameParameterValue")
    private void recreateWorld(WorldCreator creator, List<Player> players, boolean placeDefaultSpawnStructure) {
        World newWorld = Bukkit.createWorld(creator);

        if (newWorld == null) {
            plugin.getLogger().severe("Impossible de creer le monde.");
            resettingWorld = false;
            return;
        }

        if (placeDefaultSpawnStructure) {
            initializeGameWorld(newWorld);
        } else {
            initializeLoadedMapWorld(newWorld);
        }

        for (Player player : players) {
            player.teleport(newWorld.getSpawnLocation());
        }

        resettingWorld = false;
    }

    private void initializeLoadedMapWorld(World world) {
        world.setGameRule(GameRules.RESPAWN_RADIUS, 0);
        world.setTime(0);
        world.setGameRule(GameRules.ADVANCE_TIME, false);
    }

    private void initializeGameWorld(World world) {
        StructureManager manager = Bukkit.getStructureManager();
        Structure structure = null;

        try (InputStream structFile = plugin.getResource("tfl_spawn.nbt")) {
            if (structFile != null) {
                structure = manager.loadStructure(structFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        world.getNearbyEntities(new BoundingBox(-15,250,-15,15,310,15))
                .forEach(entity -> {
                    if(entity.getType() != EntityType.PLAYER){
                        entity.remove();
                    }
                });

        if(structure != null){
            Location pos = new Location(world,-14,279,-14);

            List<Entity> displays = world.getEntities().stream()
                    .filter(e ->
                            e.getType()==EntityType.TEXT_DISPLAY && e.getScoreboardTags().contains("tfl_spawn_texts") ||
                            e.getType()==EntityType.MANNEQUIN && e.getScoreboardTags().contains("tfl_spawn_mannequins") ||
                            e.getType()==EntityType.ARMOR_STAND && e.getScoreboardTags().contains("tfl_spawn_armor_deco"))
                    .toList();

            displays.forEach(Entity::remove);

            structure.place(
                    pos,
                    true,
                    StructureRotation.NONE,
                    Mirror.NONE,
                    0,
                    1f,
                    new Random()
            );
        }

        Location spawn = new Location(world,0.5,281,0.5);

        oldSpawn = world.getSpawnLocation();

        world.setSpawnLocation(spawn);
        world.setGameRule(GameRules.RESPAWN_RADIUS,0);
        world.setTime(0);
        world.setGameRule(GameRules.ADVANCE_TIME,false);
    }

    private static void copyDirectory(Path source, Path destination) throws IOException {
        TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();
        try (Stream<Path> paths = Files.walk(source)){
            paths.forEach(path -> {
                try {
                    Path relative = source.relativize(path);
                    String relativeStr = relative.toString().replace("\\", "/");

                    // FILTRES CRUCIAUX POUR PAPER 26.1+
                    if (
                             relativeStr.startsWith("DIM1") || relativeStr.startsWith("DIM-1") // Bloque les sous-dimensions problématiques
                            || relativeStr.equals("uid.dat") || relativeStr.equals("session.lock")) {
                        return;
                    }
                    System.out.println("Copying: " + source.resolve(relative) + " to: " + destination.resolve(relative));

                    Path target = destination.resolve(relative);
                    if (Files.isDirectory(path)) {
                        if (!Files.exists(target)) {
                            Files.createDirectories(target);
                        }
                    } else {
                        Files.createDirectories(target.getParent());
                        Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    }
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, e.getMessage(), e);
        }
    }

    private void extractZip(File zipFile, File destination) throws IOException {
        if (!destination.exists()) {
            if (!destination.mkdirs()){
                throw new IOException("Impossible de créer le dossier de destination : " + destination.getAbsolutePath());
            }
        }

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(destination, entry.getName());

                String destPath = destination.getCanonicalPath();
                String outPath = outFile.getCanonicalPath();

                if (!outPath.startsWith(destPath + File.separator)) {
                    throw new IOException("Entrée ZIP invalide : " + entry.getName());
                }

                if (entry.isDirectory()) {
                    if (!outFile.mkdirs()){
                        throw new IOException("Impossible de créer le dossier : " + outFile.getAbsolutePath());
                    }
                } else {
                    if(!outFile.getParentFile().mkdirs()){
                        throw new IOException("Impossible de créer le dossier parent : " + outFile.getParentFile().getAbsolutePath());
                    }
                    Files.copy(zis, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    private Path resolveSourceDimension(Path mapRoot) {

        Path tflGameDim = mapRoot.resolve(GAME_WORLD);

        if (Files.isDirectory(tflGameDim)) {
            plugin.getLogger().info("Dimension \"" + GAME_WORLD + "\" trouvee dans la map, utilisation de celle-ci.");
            return tflGameDim;
        }

        Path overworldDim = mapRoot.resolve("overworld");

        if (Files.isDirectory(overworldDim)) {
            plugin.getLogger().info("Dimension \"" + GAME_WORLD + "\" introuvable, utilisation du dossier \"overworld\" trouve dans la map.");
            return overworldDim;
        }

        // Recherche approfondie du fichier "level.dat" pour ignorer le dossier parent inutile du zip
        try (Stream<Path> stream = Files.walk(mapRoot)) {
            Optional<Path> levelDatPath = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equals("level.dat"))
                    .findFirst();

            if (levelDatPath.isPresent()) {
                Path trueRoot = levelDatPath.get().getParent().resolve("dimensions").resolve("minecraft").resolve("overworld");
                plugin.getLogger().info("Fichier level.dat trouve. Utilisation du dossier : " + trueRoot.getFileName());
                return trueRoot;
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Erreur lors de la recherche du fichier level.dat : " + e.getMessage());
        }

        plugin.getLogger().info("Dimension introuvable et aucun level.dat detecte, utilisation de la racine de la map par defaut.");
        return mapRoot;
    }

    public void loadMap(String mapName) {

        plugin.getGameManager().stopGame();

        isGameWorldLoaded = false;
        resettingWorld = true;

        World oldWorld = getGameWorld();
        File destination;

        // Si le monde n'existe pas, on le crée temporairement pour récupérer l'architecture de dossier moderne de Paper
        if (oldWorld == null) {
            oldWorld = Bukkit.createWorld(new WorldCreator(GAME_WORLD));
            if (oldWorld == null) {
                plugin.getLogger().severe("Impossible de resoudre le dossier de dimension !");
                resettingWorld = false;
                return;
            }
        }

        // C'est la clé du problème : on récupère le vrai dossier utilisé par Paper (ex: world/dimensions/minecraft/tfl_game)
        destination = oldWorld.getWorldFolder();
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        World lobby = getLobbyWorld();

        if(lobby == null){
            resettingWorld = false;
            return;
        }

        for(Player player : players){
            player.teleport(lobby.getSpawnLocation());
        }

        // On décharge le monde et on vide le VRAI dossier de dimension
        Bukkit.unloadWorld(oldWorld, false);
        deleteRecursively(destination);

        File mapsFolder = new File(
                Bukkit.getWorldContainer(),
                "TheFloorIsLava-maps"
        );

        File mapFolder = new File(mapsFolder,mapName);
        plugin.getLogger().info(mapFolder.getAbsolutePath());

        if(!mapFolder.exists()){
            plugin.getLogger().warning("Map introuvable : " + mapName);
            resettingWorld = false;
            return;
        }

        Path tempExtractDir = null;

        try {
            if (mapFolder.isDirectory()) {
                Path sourceDimension = resolveSourceDimension(mapFolder.toPath());
                // Copie directe dans le dossier de dimension (bypass le système de migration legacy buggé)
                copyDirectory(sourceDimension, destination.toPath());
                copyDirectory(sourceDimension.getParent().getParent().getParent().resolve("data"), destination.toPath().resolve("data"));

            } else if (mapFolder.isFile() && mapFolder.getName().endsWith(".zip")) {
                tempExtractDir = Files.createTempDirectory("tfl_map_extract_");
                extractZip(mapFolder, tempExtractDir.toFile());
                Path sourceDimension = resolveSourceDimension(tempExtractDir);
                copyDirectory(sourceDimension, destination.toPath());
                copyDirectory(sourceDimension.getParent().getParent().getParent().resolve("data"), destination.toPath().resolve("data"));

            } else {
                throw new IOException("Le fichier n'est ni un dossier ni un zip.");
            }

        } catch(IOException e) {
            resettingWorld = false;
            throw new RuntimeException(e);
        } finally {
            if (tempExtractDir != null) {
                if (!deleteRecursively(tempExtractDir.toFile())){
                    plugin.getLogger().warning("Impossible de supprimer le dossier temporaire : " + tempExtractDir.toAbsolutePath());
                }
            }
        }

        isGameWorldLoaded = true;
        recreateWorld(
                new WorldCreator(GAME_WORLD),
                players,
                true
        );
    }

    private static boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) deleteRecursively(f);
            }
        }
        return file.delete();
    }

    @SuppressWarnings("unused")
    public boolean isResettingWorld(){
        return resettingWorld;
    }

    public World getLobbyWorld(){
        return Bukkit.getWorld(TheFloorIsLavaManager.LOBBY_WORLD);
    }

    public World getGameWorld(){
        return Bukkit.getWorld(TheFloorIsLavaManager.GAME_WORLD);
    }

    public Set<String> getMapsNames() {
        return Stream.of(Objects.requireNonNull(new File(MAPS_FOLDER).listFiles()))
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public Location getLobbySpawnLocation(){
        World lobby = getLobbyWorld();
        if(lobby == null){
            return null;
        }
        return lobby.getSpawnLocation();
    }

    public Location getPreGameSpawnLocation(){
        World game = getGameWorld();
        if(game == null){
            return null;
        }
        return game.getSpawnLocation();
    }

    public Location getDefaultSpawnLocation(){
        plugin.getLogger().warning("getDefaultSpawnLocation() ne devrait pas être appeler, quelque chose ne va pas.");
        return oldSpawn;
    }


    @SuppressWarnings("unused")
    public void mergeWorldGenSettings(File sourceFile, File targetFile) {
        plugin.getLogger().info("sourceFile: " + sourceFile.getAbsolutePath());
        plugin.getLogger().info("targetFile: " + targetFile.getAbsolutePath());
        if (!sourceFile.exists()) {
            plugin.getLogger().warning("Le fichier source world_gen_settings.dat est introuvable !");
            return;
        }

        try {
            // 1. Lire le fichier NBT de la map importée (Source)
            CompoundTag sourceRoot = NbtIo.readCompressed(sourceFile.toPath(), NbtAccounter.unlimitedHeap());

            // Validation de la structure source : data -> dimensions -> minecraft:tfl_game
            Optional<CompoundTag> dataTagOpt = sourceRoot.getCompound("data");
            if (dataTagOpt.isEmpty()) { // 10 = ID du type CompoundTag
                plugin.getLogger().warning("Le world_gen_settings de la map ne contient pas de tag 'data'.");
                return;
            }
            CompoundTag sourceData = dataTagOpt.get();

            Optional<CompoundTag> dimensionsTagOpt = sourceData.getCompound("dimensions");
            if (dimensionsTagOpt.isEmpty()) {
                plugin.getLogger().warning("Le world_gen_settings de la map ne contient pas de tag 'dimensions'.");
                return;
            }
            CompoundTag sourceDimensions = dimensionsTagOpt.get();

            Optional<CompoundTag> tflGameDimensionTagOpt = sourceDimensions.getCompound("minecraft:tfl_game");
            if (tflGameDimensionTagOpt.isEmpty()) {
                plugin.getLogger().warning("La dimension 'minecraft:tfl_game' est introuvable dans le world_gen_settings de la map. Test avec overworld...");
                tflGameDimensionTagOpt = sourceDimensions.getCompound("minecraft:overworld");
                if (tflGameDimensionTagOpt.isEmpty()) {
                    plugin.getLogger().warning("La dimension 'minecraft:overworld' est egalement introuvable dans le world_gen_settings de la map. Aucune dimension valide à fusionner.");
                    return;
                }
            }

            // Extraction de la configuration de votre dimension
            CompoundTag tflGameDimensionTag = tflGameDimensionTagOpt.get();

            // 2. Lire le fichier NBT du serveur (Cible) ou en créer un nouveau s'il n'existe pas
            CompoundTag targetRoot;
            if (targetFile.exists()) {
                targetRoot = NbtIo.readCompressed(targetFile.toPath(), NbtAccounter.unlimitedHeap());
            } else {
                targetRoot = new CompoundTag();
            }

            // Assurer l'existence de "data" dans le fichier du serveur
            CompoundTag targetData;
            if (targetRoot.getCompound("data").isPresent()) {
                targetData = targetRoot.getCompound("data").get();
            } else {
                targetData = new CompoundTag();
                targetRoot.put("data", targetData);
            }

            // Assurer l'existence de "dimensions" dans le fichier du serveur
            CompoundTag targetDimensions;
            if (targetData.getCompound("dimensions").isPresent()) {
                targetDimensions = targetData.getCompound("dimensions").get();
            } else {
                targetDimensions = new CompoundTag();
                targetData.put("dimensions", targetDimensions);
            }

            // 3. Fusionner (écraser) la dimension avec une copie propre pour éviter les références partagées
            targetDimensions.put("minecraft:tfl_game", tflGameDimensionTag.copy());

            // 4. Sauvegarder le fichier fusionné sur le serveur
            File parentDir = targetFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()){
                    plugin.getLogger().severe("Impossible de créer le dossier parent pour world_gen_settings.dat : " + parentDir.getAbsolutePath());
                    return;
                }
            }

            NbtIo.writeCompressed(targetRoot, targetFile.toPath());
            plugin.getLogger().info("La configuration de la dimension 'minecraft:tfl_game' a ete fusionnee avec succes !");

        } catch (IOException e) {
            plugin.getLogger().log( Level.SEVERE, e.getMessage(),e);
        }
    }

    public void initLobbyWorld() {
        TheFloorIsLavaManager.getInstance().getLogger().info("Initialisation du monde lobby...");
        plugin.getLogger().info("Le monde lobby existe deja, suppression de l'ancien monde...");
        Path lobbyPath = Bukkit.getWorldContainer().toPath().resolve("world/dimensions/minecraft").resolve(LOBBY_WORLD);
        plugin.getLogger().info(lobbyPath.toFile().getAbsolutePath());
        Bukkit.unloadWorld(getLobbyWorld(), false);
        deleteRecursively(lobbyPath.toFile());
        WorldCreator creator = new WorldCreator(LOBBY_WORLD);
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        creator.generatorSettings("{\"layers\":[{\"block\":\"minecraft:bedrock\",\"height\":1},{\"block\":\"minecraft:light_gray_wool\",\"height\":63}],\"biome\":\"minecraft:plains\"}");

        World lobby = creator.createWorld();

        if (lobby == null) {
            plugin.getLogger().severe("Impossible de charger le monde \"" + LOBBY_WORLD + "\" !");
            return;
        }

        // Configuration du lobby
        lobby.setAutoSave(false);
        lobby.setTime(6000);
        lobby.setGameRule(GameRules.ADVANCE_TIME, false);
        lobby.setGameRule(GameRules.ADVANCE_TIME, false);
        lobby.setGameRule(GameRules.ADVANCE_WEATHER, false);
        lobby.setGameRule(GameRules.SPAWN_MOBS, false);
        lobby.setStorm(false);
        lobby.setThundering(false);

        Location spawn = new Location(lobby, 0.5, 0, 0.5);
        lobby.setSpawnLocation(spawn);

        plugin.getLogger().info("Monde lobby charge !");
    }
}