package Tools;

import Screens.PlayScreen;
import Sprites.Brick;
import Sprites.Coin;
import Sprites.Goomba;

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
import com.badlogic.gdx.utils.Array;
import com.paskomartin.mariobros.MarioBros;

// some kind of farbic - every body will be created, initialized and then will putted to the world.
public class B2WorldCreator {
	private Array<Goomba> goombas;
	
	public B2WorldCreator(PlayScreen screen) {
		World world = screen.getWorld();
		TiledMap map = screen.getMap();
		
		// z czego jest zbudowane cia³o
		BodyDef bdef = new BodyDef();
		// shape for fixture
		PolygonShape shape = new PolygonShape();
		// fixture
		FixtureDef fdef = new FixtureDef();
		Body body;
		
		// tworzenie cia³a i jego osprzêtu
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
			// set filters
			fdef.filter.categoryBits = MarioBros.OBJECT_BIT;
			
			body.createFixture(fdef);
		}
		
		// bricks
		for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class) ) { 
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			new Brick(screen, rect);
		}
		
		// coins
		for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class) ) { 
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			new Coin(screen, rect);
		}
		
		// goombas
		goombas = new Array<Goomba>();
		for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class) ) {
			Rectangle rect = ( (RectangleMapObject) object).getRectangle();
			goombas.add(new Goomba(screen, rect.getX() / MarioBros.PPM, rect.getY() / MarioBros.PPM ));
		}
	}
	
	
	public Array<Goomba> getGoombas() {
		return goombas;
	}

	public void setGoombas(Array<Goomba> goombas) {
		this.goombas = goombas;
	}
}
