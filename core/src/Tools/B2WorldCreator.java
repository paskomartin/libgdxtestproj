package Tools;

import Sprites.Brick;
import Sprites.Coin;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.paskomartin.mariobros.MarioBros;

// some kind of farbic - every body will be created, initialized and then will putted to the world.
public class B2WorldCreator {
	public B2WorldCreator(World world, TiledMap map) {
		// z czego jest zbudowane cia�o
		BodyDef bdef = new BodyDef();
		// shape for fixture
		PolygonShape shape = new PolygonShape();
		// fixture
		FixtureDef fdef = new FixtureDef();
		Body body;
		
		// tworzenie cia�a i jego osprz�tu
		// ground
		for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class) ) { 
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyType.StaticBody;
			bdef.position.set( (rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2 ) / MarioBros.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
			fdef.shape = shape;
			body.createFixture(fdef);
		}
		
		// pipe
		for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class) ) { 
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyType.StaticBody;
			bdef.position.set( (rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2 ) / MarioBros.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
			fdef.shape = shape;
			body.createFixture(fdef);
		}
		
		// bricks
		for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class) ) { 
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			new Brick(world, map, rect);
		}
		
		// coins
		for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class) ) { 
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			new Coin(world, map, rect);
		}
	}
}