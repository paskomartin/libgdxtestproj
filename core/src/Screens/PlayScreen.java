package Screens;

import java.util.concurrent.LinkedBlockingQueue;

import Scenes.Hud;
import Sprites.Mario;
import Sprites.Enemies.Enemy;
import Sprites.Items.Item;
import Sprites.Items.ItemDef;
import Sprites.Items.Mushroom;
import Tools.B2WorldCreator;
import Tools.WorldContactListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.paskomartin.mariobros.MarioBros;

public class PlayScreen implements Screen {
	private MarioBros game;
	
	// 
	private TextureAtlas atlas;

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
	private B2WorldCreator creator;
	
	// sprites
	private Mario player;
	
	private Array<Item> items;
	private LinkedBlockingQueue<ItemDef> itemsToSpawn;
	
	// music
	private Music music;
	
	public PlayScreen(MarioBros game) {
		atlas = new TextureAtlas("Mario_and_enemies.pack");
		
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
		
		world = new World( new Vector2(0, -10), true); 	// bez grawitacji, obiekty œpi¹ i nie s¹ obliczane w³aœciwoœci fizyczne
		b2dr = new Box2DDebugRenderer();				// auxiliary renderer shows the collision box 
		
		// mario
		player = new Mario(this);
		
		creator = new B2WorldCreator(this);
		
		
		// set the collision list?
		world.setContactListener(new WorldContactListener() );
		
		// set music
		music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
		music.setLooping(true);
		music.setVolume(0.3f);
		music.play();
		
		items = new Array<Item>();
		itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
	}
	
	public void spawnItem(ItemDef idef) {
		itemsToSpawn.add(idef);
	}
	
	public void handleSpawningItems() {
		if (!itemsToSpawn.isEmpty()) {
			ItemDef idef = itemsToSpawn.poll();
			if (idef.type == Mushroom.class) {
				items.add(new Mushroom(this, idef.position.x, idef.position.y));
			}
		}
	}
		
	@Override
	public void show() {
		
	}

	public void update(float dt) {
		handleInput(dt);
		handleSpawningItems();
		
		world.step(1/60f, 6, 2);
		
		player.update(dt);
		for (Enemy enemy : creator.getGoombas()) {
			enemy.update(dt);
			// 14 bricks  * brick width (16)
			if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
				enemy.b2body.setActive(true);
			}
		}
		
		for (Item item : items) {
			item.update(dt);
		}
		
		hud.update(dt);
		
		// update camera
		if (player.currentState != Mario.State.DEAD) {
			gamecam.position.x = player.b2body.getPosition().x;
		}
		
		// always update camera
		gamecam.update();
		
		renderer.setView(gamecam);
	}
		
	private void handleInput(float dt) {
		if (player.currentState != Mario.State.DEAD) {
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
		
		game.batch.setProjectionMatrix(gamecam.combined);
		game.batch.begin();
		player.draw(game.batch);
		
		for (Enemy enemy : creator.getGoombas() ) {
			enemy.draw(game.batch);
		}
		
		for (Item item : items) {
			item.draw(game.batch);
		}
		
		game.batch.end();
		
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
		
		if (gameOver()) {
			game.setScreen(new GameOverScreen(game));
			dispose();
		}
	}

	public boolean gameOver() {
		if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3) {
			return true;
		}
		return false;
	}
	
	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);
	}

	public TiledMap getMap() {
		return map;
	}
	
	public World getWorld() {
		return world;
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
		map.dispose();
		renderer.dispose();
		world.dispose();
		b2dr.dispose();
		hud.dispose();
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
	}

}
