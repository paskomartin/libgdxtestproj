package Sprites;

import Scenes.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.paskomartin.mariobros.MarioBros;

public class Coin extends InteractiveTileObject {
	private static TiledMapTileSet tileset;
	private final int BLANK_COIN = 28; // because GDX Tiles set starts counting from 1
	
	public Coin(World world, TiledMap map, Rectangle bounds) {
		super(world, map, bounds);
		tileset = map.getTileSets().getTileSet("tileset_gutter");
		fixture.setUserData(this);
		setCategoryFIlter(MarioBros.COIN_BIT);
	}

	@Override
	public void onHeadHit() {
		Gdx.app.log("Coin", "Collision");
		if (getCell().getTile().getId() == BLANK_COIN) {
			MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
		}
		else {
			MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
		}
		getCell().setTile(tileset.getTile(BLANK_COIN));
		Hud.addScore(100);
	}
}
