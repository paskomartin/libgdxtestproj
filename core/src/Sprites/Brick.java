package Sprites;

import Scenes.Hud;
import Screens.PlayScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.paskomartin.mariobros.MarioBros;

public class Brick extends InteractiveTileObject {
	public Brick(PlayScreen screen, MapObject object) {
		super(screen, object);
		fixture.setUserData(this);
		setCategoryFIlter(MarioBros.BRICK_BIT);
	}

	@Override
	public void onHeadHit() {
		Gdx.app.log("Brick", "Collision");
		setCategoryFIlter(MarioBros.DESTROYED_BIT);
		getCell().setTile(null);
		Hud.addScore(200);
		MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
	}
}
