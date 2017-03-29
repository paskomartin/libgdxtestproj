package Sprites;

import Screens.PlayScreen;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.paskomartin.mariobros.MarioBros;

public class Mario extends Sprite {
	public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD };
	public State currentState;
	public State previousState;
	
	// box2d
	public World world;
	public Body b2body;
	
	// textures
	private TextureRegion marioStand;
	private Animation<TextureRegion> marioRun;
	private TextureRegion marioJump; // 1 frame
	private TextureRegion marioDead;
	private TextureRegion bigMarioStand;
	private TextureRegion bigMarioJump;
	private Animation<TextureRegion> bigMarioRun;
	private Animation<TextureRegion> growMario;
	
	private float stateTimer;
	private boolean runningRight;
	private boolean marioIsBig;
	private boolean runGrowAnimation;
	private boolean timeToDefineBigMario;
	private boolean timeToRedefineMario;
	private boolean marioIsDead;
	
	public Mario(PlayScreen screen) {
		//super(screen.getAtlas().findRegion("little_mario"));
		this.world = screen.getWorld();
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0f;
		runningRight = true;
		
		// initialize texture region and set animation array
		Array<TextureRegion> frames = new Array<TextureRegion>(); 
		for(int i = 1; i < 4; ++i) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
		}
		
		marioRun = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		
		// big mario run animation
		for(int i = 1; i < 4; ++i) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
		}
		bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();

		// get set animation frames from growing mario
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 15 * 16, 0, 16, 32 ) );
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32 ) );
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 15 * 16, 0, 16, 32 ) );
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32 ) );
		growMario = new Animation<TextureRegion>(0.2f, frames);
		frames.clear();
		
		// jump
		marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 5 * 16, 0, 16, 16);
		bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 5 * 16, 0, 16, 32);
		
		// get subtexture form big texture
		marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
		bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);  
		
		// create dead mario texture region
		marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 6 * 16, 0, 16, 16 );
		
		
		defineMario();
		// set collision bounds
		setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
		// texture region which is now associated with the sprite
		setRegion(marioStand);
	}

	public void update(float dt) {
		if (marioIsBig) {
			setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
		}
		else {
			setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
		}
		setRegion(getFrame(dt));
		if(timeToDefineBigMario)
			defineBigMario();
		if (timeToRedefineMario)
			redefineMario();
	}
	
	public void redefineMario() {
		Vector2 position = b2body.getPosition();
		world.destroyBody(b2body);
		
		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);   // MarioBros.PPM/ MarioBros.PPM/ MarioBros.PPM
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / MarioBros.PPM);
		// define filters
		fdef.filter.categoryBits = MarioBros.MARIO_BIT;
		// define mask bit - mario can collide with?
		fdef.filter.maskBits = MarioBros.GROUND_BIT | 
				MarioBros.COIN_BIT | MarioBros.BRICK_BIT |
				MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT |
				MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		// create head
		// just line
		EdgeShape head = new EdgeShape();
		// relative to the origin of the center of the body
		head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM)); 
		fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData(this);
		
		timeToRedefineMario = false;
	}

	public void defineBigMario() {
		// save current position
		Vector2 currentPosition = b2body.getPosition();
		world.destroyBody(b2body);
		
		
		
		BodyDef bdef = new BodyDef();
		bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);   // MarioBros.PPM/ MarioBros.PPM/ MarioBros.PPM
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / MarioBros.PPM);
		// define filters
		fdef.filter.categoryBits = MarioBros.MARIO_BIT;
		// define mask bit - mario can collide with?
		fdef.filter.maskBits = MarioBros.GROUND_BIT | 
				MarioBros.COIN_BIT | MarioBros.BRICK_BIT |
				MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT |
				MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
		// create new fixture
		b2body.createFixture(fdef).setUserData(this);
		
		
		// create head
		// just line
		EdgeShape head = new EdgeShape();
		// relative to the origin of the center of the body
		head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM)); 
		fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		//b2body.createFixture(fdef).setUserData(this);
		b2body.createFixture(fdef).setUserData(this);//("head");	
		timeToDefineBigMario = false;
	}

	public TextureRegion getFrame(float dt) {
		currentState = getState();
		
		TextureRegion region;
		switch(currentState) {
			case DEAD: {
				region = marioDead;
				break;
			}
			case GROWING: {
				region = growMario.getKeyFrame(stateTimer); // not loopable
				if (growMario.isAnimationFinished(stateTimer)) {
					runGrowAnimation = false;
				}
				break;
			}
			case JUMPING: {
				region = marioIsBig ? bigMarioJump : marioJump;//(TextureRegion) marioJump.getKeyFrame(stateTimer);
				break;
			}
			case RUNNING: {
				//region = (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
				region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
				break;
			}
			case FALLING:
			case STANDING:
			default: {
				region = marioIsBig ? bigMarioStand : marioStand;
				break;
			}
		}
		
		if ((b2body.getLinearVelocity().x < 0 || !runningRight ) && !region.isFlipX()) {
			region.flip(true, false);
			runningRight = false;
		}
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
		if (marioIsDead) {
			return State.DEAD;
		}
		else if (runGrowAnimation) {
			return State.GROWING;
		}
		else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
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

	public void grow() {
		/* mine */
		if (marioIsBig)
			return;
		
		runGrowAnimation = true;
		marioIsBig = true;
		timeToDefineBigMario = true;
		setBounds(getX(), getY(), getWidth(), getHeight() * 2);
		MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
	}
	
	public boolean isDead() {
		return marioIsDead;
	}
	
	public float getStateTimer() {
		return stateTimer;
	}
	
	public void hit() {
		if (marioIsBig) {
			marioIsBig = false;
			timeToRedefineMario = true;
			setBounds(getX(), getY(), getWidth(), getHeight() / 2);
			MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
		}
		else {
			MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
			MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();

			marioIsDead = true;
			// no collision!
			Filter filter = new Filter();
			filter.maskBits = MarioBros.NOTHING_BIT;
			for(Fixture fixture : b2body.getFixtureList() ) {
				fixture.setFilterData(filter);
			}
			b2body.applyLinearImpulse(new Vector2(0, 4), b2body.getWorldCenter(), true);
		}
	}
	
	public void defineMario() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(16 / MarioBros.PPM, 16 / MarioBros.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);   // MarioBros.PPM/ MarioBros.PPM/ MarioBros.PPM
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / MarioBros.PPM);
		// define filters
		fdef.filter.categoryBits = MarioBros.MARIO_BIT;
		// define mask bit - mario can collide with?
		fdef.filter.maskBits = MarioBros.GROUND_BIT | 
				MarioBros.COIN_BIT | MarioBros.BRICK_BIT |
				MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT |
				MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT;
		
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		// create head
		// just line
		EdgeShape head = new EdgeShape();
		// relative to the origin of the center of the body
		head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM)); 
		fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		//b2body.createFixture(fdef).setUserData(this);
		b2body.createFixture(fdef).setUserData(this);//("head");
	}

	public boolean isBig() {
		return marioIsBig;
	}
}
