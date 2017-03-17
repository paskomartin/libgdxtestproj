package Sprites;

import Screens.PlayScreen;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.paskomartin.mariobros.MarioBros;

public class Mario extends Sprite {
	public World world;
	public Body b2body;
	
	private TextureRegion marioStand;
	
	
	public Mario(World world, PlayScreen screen) {
		super(screen.getAtlas().findRegion("little_mario"));
		this.world = world;
		defineMario();
		// get subtexture form big texture
		marioStand = new TextureRegion(getTexture(), 0, 0, 16, 16);
		// set collision bounds
		setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
		// texture region which is now associated with the sprite
		setRegion(marioStand);
	}

	public void update(float dt) {
		setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
	}
	
	public void defineMario() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		
		b2body = world.createBody(bdef);
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / MarioBros.PPM);
		
		fdef.shape = shape;
		b2body.createFixture(fdef);
	}
}
