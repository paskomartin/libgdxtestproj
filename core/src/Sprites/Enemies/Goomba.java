package Sprites.Enemies;

import Screens.PlayScreen;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.paskomartin.mariobros.MarioBros;

public class Goomba extends Enemy{
	private float stateTime;
	private Animation<TextureRegion> walkAnimation;
	private Array<TextureRegion> frames;
	private boolean setToDestroy;	// dirty flag - do we need to destroy this object after box2 simulation?
	private boolean destroyed;		// is object destroyed?
	
	// https://www.youtube.com/watch?v=23y0B-279JY&index=16&list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt
		// 4:48
	
	public Goomba(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		frames = new Array<TextureRegion>();
		for (int i = 0; i < 2; ++i) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i *16, 0, 16, 16));
		}
		walkAnimation = new Animation<TextureRegion>(0.4f, frames);
		stateTime = 0;
		setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
		setToDestroy = false;
		destroyed = false;
	}

	@Override
	protected void defineEnemy() {
		BodyDef bdef = new BodyDef();
		//bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
		bdef.position.set(getX(), getY() );
		bdef.type = BodyDef.BodyType.DynamicBody;
		
		b2body = world.createBody(bdef);
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / MarioBros.PPM);
		// define filters
		fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
		// define mask bit - mario can collide with?
		fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT |
				MarioBros.ENEMY_BIT |
				MarioBros.BRICK_BIT | MarioBros.OBJECT_BIT | MarioBros.MARIO_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		// create the head here
		PolygonShape head = new PolygonShape();
		Vector2[] vertices = new Vector2[4];
		// relative to the middle of the object
		vertices[0] = new Vector2(-5, 8).scl(1 / MarioBros.PPM);
		vertices[1] = new Vector2(5, 8).scl(1 / MarioBros.PPM);
		vertices[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
		vertices[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
		head.set(vertices);
		
		fdef.shape = head;
		fdef.restitution = 0.5f;
		fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;

		b2body.createFixture(fdef).setUserData(this);
	}
	
	public void update(float dt) {
		stateTime += dt;
		
		if (setToDestroy && !destroyed) {
			world.destroyBody(b2body);
			destroyed = true;
			// set smashed goomba
			setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
			stateTime = 0;
		}
		else if (!destroyed) {
			b2body.setLinearVelocity(velocity);
			
			//	setRegion( (TextureRegion)( walkAnimation.getKeyFrame(stateTime,true) ) );
			setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
			setRegion(  walkAnimation.getKeyFrame(stateTime,true) );
		}
	}
	
	@Override
	public void draw(Batch batch) {
		if (!destroyed || stateTime < 1) {
			super.draw(batch);
		}
	}
	

	@Override
	public void hitOnHead() {
		/* because we can't delete box2d body when is simulation calculated
		 * (hitOnHead is called from WorldContactListener) so we have to set 
		 * a dirty flag instead 
		 */
		setToDestroy = true;
	}

}
