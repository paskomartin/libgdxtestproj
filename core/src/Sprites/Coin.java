package Sprites;

import Scenes.Hud;
import Screens.PlayScreen;
import Sprites.Items.ItemDef;
import Sprites.Items.Mushroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.paskomartin.mariobros.MarioBros;

public class Coin extends InteractiveTileObject {
	private static TiledMapTileSet tileset;
	private final int BLANK_COIN = 28; // because GDX Tiles set starts counting from 1
	
	public Coin(PlayScreen screen, MapObject object) {
		super(screen, object);
		tileset = map.getTileSets().getTileSet("tileset_gutter");
		fixture.setUserData(this);
		setCategoryFIlter(MarioBros.COIN_BIT);
	}

	@Override
	public void onHeadHit(Mario mario) {
		Gdx.app.log("Coin", "Collision");
		if (getCell().getTile().getId() == BLANK_COIN) {
			MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
		}
		else {
			if (object.getProperties().containsKey("mushroom")) {
				screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
				MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
			}
			else {
				MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
			}
		}
		getCell().setTile(tileset.getTile(BLANK_COIN));
		Hud.addScore(100);
	}
}
