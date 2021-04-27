package com.mygdx.runnjump.util;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

import java.util.HashMap;
import java.util.Random;

public class TerrainGenerator {
    TiledMapTileLayer visualLayer, collisionLayer;
    TiledMapTileSet tileSet;
    int mapLoadXpos;
    float width, playerPosX;
    Random random = new Random();
    HashMap<String, TiledMapTile> tiles;

    public TerrainGenerator(TiledMapTileLayer visualLayer, TiledMapTileLayer collisionLayer, TiledMapTileSet tileSet) {
        this.collisionLayer = collisionLayer;
        this.visualLayer = visualLayer;
        this.tileSet = tileSet;
        mapLoadXpos = 0;
        TiledMapTile dirtTile = tileSet.getTile(170);
        TiledMapTile grassTile = tileSet.getTile(144);
        tiles = new HashMap<>();
        tiles.put("grass", grassTile);
        tiles.put("dirt", dirtTile);
    }

    private void generatePlatform(int length, int depth) {
        int y =  17 + random.nextInt(23);
        int x = Math.round(width / 32 + playerPosX / 32) + random.nextInt(3);

        TiledMapTileLayer.Cell cell = visualLayer.getCell(x, y);
        cell = new TiledMapTileLayer.Cell();
        cell.setTile(tiles.get("grass"));
        visualLayer.setCell(x, y, cell);
        collisionLayer.setCell(x, y, cell);
        visualLayer.setCell(x + 1, y, cell);
        collisionLayer.setCell(x + 1, y, cell);
        visualLayer.setCell(x + 2, y, cell);
        collisionLayer.setCell(x + 2, y, cell);
        visualLayer.setCell(x + 3, y, cell);
        collisionLayer.setCell(x + 3, y, cell);
        visualLayer.setCell(x + 4, y, cell);
        collisionLayer.setCell(x + 4, y, cell);
    }


    public void generateTerrain(float playerLocX, float width) {

        while(mapLoadXpos < playerLocX+width){
            mapLoadXpos += 32;
        }

        playerPosX = playerLocX;
        this.width = width;

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
        }
        if (random.nextFloat() < 0.3) {
            //generate small block obstacle
            int y = 17+ random.nextInt(23);
            int x = Math.round(width / 32 +playerPosX / 32) + random.nextInt(3);

            TiledMapTileLayer.Cell cell = visualLayer.getCell(x, y);
            cell = new TiledMapTileLayer.Cell();
            cell.setTile(tiles.get("dirt"));
            visualLayer.setCell(x, y, cell);
            collisionLayer.setCell(x, y, cell);
            visualLayer.setCell(x + 1, y, cell);
            collisionLayer.setCell(x + 1, y, cell);
        }

        if (random.nextFloat() < 0.41) {
            //generate platform obstacle
            generatePlatform(2+random.nextInt(8), 1+random.nextInt(3));
        }

        //


        mapLoadXpos = mapLoadXpos + 96;




    }

    public void setLoadPos(int mapLoadXpos) {
        this.mapLoadXpos = mapLoadXpos;
    }
}
