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
	public void onHeadHit(Mario mario) {
		//Gdx.app.log("Brick", "Collision");
		if (mario.isBig() ) {
			setCategoryFIlter(MarioBros.DESTROYED_BIT);
			getCell().setTile(null);
			Hud.addScore(200);
			MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
		}
		else {
			MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
		}
	}
}
