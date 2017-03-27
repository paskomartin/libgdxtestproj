package Tools;

import Sprites.InteractiveTileObject;
import Sprites.Mario;
import Sprites.Enemies.Enemy;
import Sprites.Items.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.paskomartin.mariobros.MarioBros;

public class WorldContactListener implements ContactListener{

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
		
		if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
			Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
			Fixture object = head == fixA ? fixB : fixA;
	
			//if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
			if (object.getUserData() instanceof InteractiveTileObject) {
				((InteractiveTileObject)object.getUserData()).onHeadHit();
			}
		}
		
		switch (cDef) {
			case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT: {
				// find out which fixture is enemy fixture
				if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT) {
					// FixA ia enemy
					((Enemy)fixA.getUserData()).hitOnHead();
				}
				else { // if (fixB.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT) {
					// FixB ia enemy
					((Enemy)fixB.getUserData()).hitOnHead();
				}
				break;
			}
			case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT: {
				if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT) {
					( (Enemy)fixA.getUserData() ).reverseVelocity(true, false);
				}
				else {
					( (Enemy)fixB.getUserData() ).reverseVelocity(true, false);
				}
				break;
			}
			case MarioBros.ENEMY_BIT | MarioBros.MARIO_BIT: {
				Gdx.app.log("Mario", "DIED");
				break;
			}
			case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT: {
				( (Enemy)fixA.getUserData() ).reverseVelocity(true, false);
				( (Enemy)fixB.getUserData() ).reverseVelocity(true, false);
				break;
			}
			
			case MarioBros.ENEMY_BIT | MarioBros.GROUND_BIT: {
				if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT) {
					( (Enemy)fixA.getUserData() ).reverseVelocity(true, false);
				}
				else {
					( (Enemy)fixB.getUserData() ).reverseVelocity(true, false);
				}
				break;
			}
			case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT: {
				if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT) {
					( (Item)fixA.getUserData() ).reverseVelocity(true, false);
				}
				else {
					( (Item)fixB.getUserData() ).reverseVelocity(true, false);
				}
				break;
			}
			case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT: {
				if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT) {
					( (Item)fixA.getUserData() ).use( (Mario)fixB.getUserData() );
				}
				else {
					( (Item)fixB.getUserData() ).use( (Mario)fixA.getUserData() );
				}
				break;
			}
			
		}
	}

	@Override
	public void endContact(Contact contact) {
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
