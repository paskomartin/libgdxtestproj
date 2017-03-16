package Screens;

import Scenes.Hud;
import Sprites.Mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.paskomartin.mariobros.MarioBros;

public class PlayScreen implements Screen {
	private MarioBros game;
	private OrthographicCamera gamecam;
	private Viewport gamePort;
	private Hud hud;
	
	// tiled map
	private TmxMapLoader maploader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	// box2d
	private World world;			 // box2d œwiat
	private Box2DDebugRenderer b2dr; // fizyczna reprezetnacja body w box2d
	
	// mario
	private Mario player;
	
	public PlayScreen(MarioBros game) {
		this.game = game;
		gamecam = new OrthographicCamera();
		gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);
		hud = new Hud(game.batch);
		
		// load map and setup map renderer
		maploader = new TmxMapLoader();
		map = maploader.load("level1.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
		
		// init gamecam position to the centered correctly
		gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
		
		world = new World( new Vector2(0, -10), true); // bez grawitacji, obiekty œpi¹ i nie s¹ obliczane w³aœciwoœci fizyczne
		b2dr = new Box2DDebugRenderer();
		
		// mario
		player = new Mario(world);
		
		
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
			body.createFixture(fdef);
		}
		
		// bricks
		for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class) ) { 
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyType.StaticBody;
			bdef.position.set( (rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2 ) / MarioBros.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
			fdef.shape = shape;
			body.createFixture(fdef);
		}
		
		// coins
		for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class) ) { 
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyType.StaticBody;
			bdef.position.set( (rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2 ) / MarioBros.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
			fdef.shape = shape;
			body.createFixture(fdef);
		}
	}
	
	@Override
	public void show() {
		
	}

	
	public void update(float dt) {
		handleInput(dt);
		
		world.step(1/60f, 6, 2);
		
		gamecam.position.x = player.b2body.getPosition().x;
		
		// always update camera
		gamecam.update();
		
		renderer.setView(gamecam);
	}
	
	
	private void handleInput(float dt) {
		if (Gdx.input.isKeyJustPressed(Keys.UP)) {
			player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
			player.b2body.applyLinearImpulse(new Vector2(0.1f, 0f), player.b2body.getWorldCenter(), true);
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
			player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0f), player.b2body.getWorldCenter(), true);
		}
	}

	
	@Override
	public void render(float delta) {
		update(delta);
		
		// czyszczenie ekranu
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// render our game map
		renderer.render();
		
		// render our Box2DebugLines
		b2dr.render(world, gamecam.combined);
		
		
		// ustawienie macierzy rzutowania, po to ¿eby by³o widoczne
		// tylko to co kamera widzi
		//game.batch.setProjectionMatrix(gamecam.combined); // combined : projection * view
		
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
