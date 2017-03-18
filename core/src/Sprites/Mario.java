package Sprites;

import Screens.PlayScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.paskomartin.mariobros.MarioBros;

public class Mario extends Sprite {
	public enum State { FALLING, JUMPING, STANDING, RUNNING };
	public State currentState;
	public State previousState;
	
	// box2d
	public World world;
	public Body b2body;
	
	// textures
	private TextureRegion marioStand;
	private Animation marioRun;
	private Animation marioJump;
	
	private float stateTimer;
	private boolean runningRight;
	
	public Mario(World world, PlayScreen screen) {
		super(screen.getAtlas().findRegion("little_mario"));
		this.world = world;
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0f;
		runningRight = true;
		
		// initialize texture region and set animation array
		Array<TextureRegion> frames = new Array<TextureRegion>(); 
		for(int i = 1; i < 4; ++i) {
			frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
		}
		
		marioRun = new Animation(0.1f, frames);
		frames.clear();

		// jump
		for(int i = 4; i < 6; ++i) {
			frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
		}
		marioJump = new Animation(0.1f, frames);
		
		// get subtexture form big texture
		marioStand = new TextureRegion(getTexture(), 0, 0, 16, 16);
		
		defineMario();
		// set collision bounds
		setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
		// texture region which is now associated with the sprite
		setRegion(marioStand);
	}

	public void update(float dt) {
		setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
		setRegion(getFrame(dt));
	}
	
	public TextureRegion getFrame(float dt) {
		currentState = getState();
		
		TextureRegion region;
		switch(currentState) {
			case JUMPING: {
				region = (TextureRegion) marioJump.getKeyFrame(stateTimer);
				break;
			}
			case RUNNING: {
				region = (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
				break;
			}
			case FALLING:
			case STANDING:
			default: {
				region = marioStand;
				break;
			}
		}
		
		if ((b2body.getLinearVelocity().x < 0 || !runningRight ) && !region.isFlipX()) {
			region.flip(true, false);
			runningRight = false;
		}
		// TODO: https://www.youtube.com/watch?v=1fJrhgc0RRw&list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&index=11
		// 13:09
		else if ( (b2body.getLinearVelocity().x >0 || runningRight) && region.isFlipX() ) {
			region.flip(true, false);
			runningRight = true;
		}
		
		stateTimer = currentState == previousState ? stateTimer + dt : 0;
		previousState = currentState;
		return region;
	}

	private State getState() {
		/* Stop the mario
		if (b2body.getLinearVelocity().x > 0 && previousState == State.RUNNING && !Gdx.input.isKeyPressed(Keys.RIGHT)) {
			currentState = State.STANDING;
			b2body.applyLinearImpulse(new Vector2(-b2body.getLinearVelocity().x, 0), b2body.getWorldCenter(), true);
		}
		*/
		
		
		if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
			return State.JUMPING;
		}
		else if (b2body.getLinearVelocity().y < 0) {
			return State.FALLING;
		}
		else if (b2body.getLinearVelocity().x != 0 ) {
			return State.RUNNING;
		}
		return State.STANDING;
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
		
		// create head
		// just line
		EdgeShape head = new EdgeShape();
		// relative to the origin of the center of the body
		head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM)); 
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData("head");
	}
}
