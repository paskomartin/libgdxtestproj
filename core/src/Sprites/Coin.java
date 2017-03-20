package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.paskomartin.mariobros.MarioBros;

public class Coin extends InteractiveTileObject {
	public Coin(World world, TiledMap map, Rectangle bounds) {
		super(world, map, bounds);
		fixture.setUserData(this);
		setCategoryFIlter(MarioBros.COIN_BIT);
	}

	@Override
	public void onHeadHit() {
		Gdx.app.log("Coin", "Collision");
	}
}
