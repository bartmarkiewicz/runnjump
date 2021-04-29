package com.mygdx.runnjump.util;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.mygdx.runnjump.entity.Bandit;
import com.mygdx.runnjump.entity.Enemy;
import com.mygdx.runnjump.entity.GameObject;
import com.mygdx.runnjump.entity.Hedgehog;
import com.mygdx.runnjump.entity.TurtleMan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * This class is used to generate the procedural terrain for the survival mode.
 */
public class TerrainGenerator {
    TiledMapTileLayer visualLayer, collisionLayer;
    TiledMapTileSet tileSet, collectibleTileSet;
    int mapLoadXpos;
    float width, playerPosX;
    int spikeLoc;
    Random random = new Random();
    HashMap<String, TiledMapTile> tiles;
    final int CHUNK_SIZE;
    ArrayList<TiledMapTile> box4x4Construct, spikes;
    float time = 0;
    public int genCount = 0;


    public TerrainGenerator(TiledMapTileLayer visualLayer, TiledMapTileLayer collisionLayer, TiledMapTileSet tileSet, TiledMapTileSet collectibleSet) {
        this.collisionLayer = collisionLayer;
        this.visualLayer = visualLayer;
        this.tileSet = tileSet;
        this.collectibleTileSet = collectibleSet;
        mapLoadXpos = 0;
        TiledMapTile dirtTile = tileSet.getTile(201);
        TiledMapTile grassTile = tileSet.getTile(130);
        TiledMapTile coin1 = collectibleTileSet.getTile(60);
        TiledMapTile coin2 = collectibleTileSet.getTile(61);
        TiledMapTile coin3 = collectibleTileSet.getTile(72);
        TiledMapTile coin4 = collectibleTileSet.getTile(73);
        assert coin1 != null;
        tiles = new HashMap<>();
        tiles.put("grass", grassTile);
        tiles.put("dirt", dirtTile);
        tiles.put("coin1", coin1);
        tiles.put("coin2",coin2);
        tiles.put("coin3",coin3);
        tiles.put("coin4",coin4);

        this.CHUNK_SIZE = 32*9;//9 block sized chunks
        box4x4Construct = new ArrayList<>();
        spikes = new ArrayList<>();
        int blockCount = 0;
        int id = 13;
        while(blockCount < 16){
            box4x4Construct.add(tileSet.getTile(id));

            blockCount++;

            if(blockCount == 4 || blockCount== 8 || blockCount == 12){
                id+=29;
            } else {
                id += 1;
            }
        }
        blockCount = 0;
        id = 5;
        while(blockCount < 8){
            spikes.add(tileSet.getTile(id));

            blockCount++;

            if(blockCount == 4){
                id+=29;
            } else {
                id += 1;
            }
        }


        this.spikeLoc = 0;
    }

    /**
     * Spawns an enemy NPC of a random type.
     */
    public GameObject spawnEnemy() {
        if(random.nextFloat() < 0.1){
            int x = Math.round((width/2)/32 + mapLoadXpos/32);
            int y = findFloorY(x)+1;
            Enemy enemy;
            if(random.nextFloat() < 0.2){
                enemy = new TurtleMan(collisionLayer,visualLayer);
            } else if (random.nextFloat() < 0.4){
                enemy = new Bandit(collisionLayer,visualLayer, 150);
            } else {
                enemy = new Hedgehog(collisionLayer,visualLayer,2+random.nextInt(15), 150);
            }
            enemy.getSprite().setPosition(x*32,y*32);
            mapLoadXpos+=96;
            return enemy;
        } else {
            return null;
        }
    }

    /**
     * Places a collectible coin on the level.
     */
    private void generateCoin(){
        int x = Math.round((width/2)/32 + mapLoadXpos/32);
        int y = findFloorY(x);

        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

        int id = 60;
        TiledMapTile tile = collectibleTileSet.getTile(id);
        cell = new TiledMapTileLayer.Cell();
        cell.setTile(tiles.get("coin1"));
        visualLayer.setCell(x, y+1, cell);
        collisionLayer.setCell(x, y+1, cell);

        id = 61;
        TiledMapTile tile2 = collectibleTileSet.getTile(id);
        TiledMapTileLayer.Cell cell2 = new TiledMapTileLayer.Cell();
        cell2.setTile(tiles.get("coin2"));
        visualLayer.setCell(x+1, y+1, cell2);
        collisionLayer.setCell(x+1, y+1, cell2);

        id = 72;
        TiledMapTile tile3 = collectibleTileSet.getTile(id);
        TiledMapTileLayer.Cell cell3 = new TiledMapTileLayer.Cell();
        cell3.setTile(tiles.get("coin3"));
        visualLayer.setCell(x, y, cell3);
        collisionLayer.setCell(x, y, cell3);

        id = 73;
        TiledMapTile tile4 = collectibleTileSet.getTile(id);
        TiledMapTileLayer.Cell cell4 = new TiledMapTileLayer.Cell();
        cell4.setTile(tiles.get("coin4"));
        visualLayer.setCell(x+1, y, cell4);
        collisionLayer.setCell(x+1, y, cell4);

    }


    /**
     * Generates a 4v4 box obstacle
     */
    private void generateBoxObstacle(){
        int x = Math.round((width/2)/32 + mapLoadXpos/32);
        int y = findFloorY(x);

        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

        int id = 0;


        for(int j = y+3; j >= y; j--) {
            for(int i = x; i < x+4; i++) {
                cell = new TiledMapTileLayer.Cell();
                cell.setTile(box4x4Construct.get(id));
                visualLayer.setCell(i, j, cell);
                collisionLayer.setCell(i, j, cell);
                id++;
            }
        }
    }

    /**
     * Finds the floor, either on a platform or the actual ground level.
     * @param x
     * @return
     */
    private int findFloorY(int x) {
        for (int j = collisionLayer.getHeight()-3; j > 0; j--){
            TiledMapTileLayer.Cell cell = collisionLayer.getCell(x,j);
            //finds a 3 by 3 empty spot with a floor
            if (cell == null && collisionLayer.getCell(x,j+1) == null && collisionLayer.getCell(x,j+2) == null){
                if (cell == null && collisionLayer.getCell(x+1,j+1) == null && collisionLayer.getCell(x+2,j+2) == null){
                    if(collisionLayer.getCell(x, j-1) != null && collisionLayer.getCell(x+1, j-1) != null && collisionLayer.getCell(x+2, j-1) != null){
                        return j;
                    }
                }
            }
        }
        return 17;
    }

    /**
     * Generates a platform of the size length * depth.
     * @param length
     * @param depth
     */
    private void generatePlatform(int length, int depth) {
        int y =  17 + random.nextInt(19);
        int x = Math.round((width/2)/32 + mapLoadXpos / 32);

        TiledMapTileLayer.Cell cell;// = visualLayer.getCell(x, y);
        cell = new TiledMapTileLayer.Cell();

        boolean changedTile = false;
        for(int i = x; i<(x+length); i++){
            for(int j = y; j < (y + depth); j++){
                cell = new TiledMapTileLayer.Cell();
                if(j == y+depth-1){
                    cell.setTile(tiles.get("grass"));
                } else {
                    cell.setTile(tiles.get("dirt"));
                }
                visualLayer.setCell(i,  j, cell);
                collisionLayer.setCell(i, j, cell);

            }

        }
    }


    /**
     * Generates the survival game mode level.
     * @param playerLocX
     * @param width
     * @param delta
     */
    public void generateTerrain(float playerLocX, float width, float delta) {

        while(mapLoadXpos < playerLocX){
            mapLoadXpos += 32;
        }

        playerPosX = playerLocX;
        this.width = width;


        generateWorld(delta);


        if (random.nextFloat() < 0.2) {
            //generate 4x4 block obstacle

            generateBoxObstacle();
        }


        if(random.nextFloat() < 0.6){
            generateCoin();
        }




        if (random.nextFloat() < 0.4) {
            //generate platform obstacle
            int platformLength = 3+random.nextInt(20);//3 to 20 length
            int platformDepth = 1+random.nextInt(5);//1 to 5 depth
            generatePlatform(platformLength, platformDepth);
        }

        mapLoadXpos = mapLoadXpos + CHUNK_SIZE;//moves to the next chunk

    }

    /**
     *
     * Generates the ceiling and boundaries of the world. Alongside the layer of spikes.
     */
    private void generateWorld(float delta) {
        time += delta;
        for (int x = mapLoadXpos/32; x < (mapLoadXpos) + 25; x = x + 1) {
            for (int y = 0; y < 17; y = y + 1) {

                TiledMapTileLayer.Cell cell = visualLayer.getCell(x, y);
                if (cell != null) {
                    //cell.setTile(tile);
                } else {
                    cell = new TiledMapTileLayer.Cell();
                    if(y == 16){
                        cell.setTile(tiles.get("grass"));
                    }else {
                        cell.setTile(tiles.get("dirt"));
                    }
                    visualLayer.setCell(x, y, cell);
                    collisionLayer.setCell(x, y, cell);

                }
            }
            TiledMapTileLayer.Cell ceilingCell = visualLayer.getCell(x, 39);
            if(ceilingCell == null){
                ceilingCell = new TiledMapTileLayer.Cell();
            }
            ceilingCell.setTile(tiles.get("dirt"));
            collisionLayer.setCell(x,39,ceilingCell);
        }

        if (time > 0.1){
            generateSpikes();
            genCount += 1;

        }



    }

    /**
     * Generates spikes which force the player to move right.
     */
    private void generateSpikes() {
        int x = spikeLoc;
        int y = 38;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

        int id = 0;

        for(int j = y; j >= y-21; j--) {
            for(int i = x; i < x+4; i++) {
                cell = new TiledMapTileLayer.Cell();
                cell.setTile(spikes.get(id));

                visualLayer.setCell(i, j, cell);
                collisionLayer.setCell(i, j, cell);
                id++;
            }
            if (id>7){
                id = 0;
            }
        }
        time = 0;




        spikeLoc+=4;
    }


    /**
     * Sets the terrain generation point (after this point new terrain is generated)
     * @param mapLoadXpos
     */
    public void setLoadPos(int mapLoadXpos) {
        this.mapLoadXpos = mapLoadXpos;
    }
}
