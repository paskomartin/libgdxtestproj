package Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.paskomartin.mariobros.MarioBros;

public class Hud implements Disposable {
	public Stage stage;
	private Viewport viewport;
	
	private Integer worldTimer;
	private float timeCount;
	private static Integer score;
	
	Label countdownLabel;
	static Label scoreLabel;
	Label timeLabel;
	Label levelLabel;
	Label worldLabel;
	Label marioLabel;
	
	public Hud(SpriteBatch sb) {
		worldTimer = 300;
		timeCount = 0;
		score = 0;
		OrthographicCamera camera = new OrthographicCamera();
		
		viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, camera);
		stage = new Stage(viewport, sb);
		
		Table table = new Table();
		table.top();
		table.setFillParent(true);
		
		BitmapFont font = new BitmapFont();
		LabelStyle style = new LabelStyle(font, Color.WHITE);
		countdownLabel = new Label(String.format("%03d", worldTimer), style);
		scoreLabel = new Label(String.format("%06d", score), style);
		timeLabel = new Label("TIME", style);
		levelLabel = new Label("1-1", style);
		worldLabel = new Label("WORLD", style);
		marioLabel = new Label("MARIO", style);
		
		table.add(marioLabel).expandX().padTop(10);
		table.add(worldLabel).expandX().padTop(10);
		table.add(timeLabel).expandX().padTop(10);
		table.row();
		table.add(scoreLabel).expandX();
		table.add(levelLabel).expandX();
		table.add(countdownLabel).expandX();
		
		stage.addActor(table);
		
	}
	
	public void update(float dt) {
		timeCount += dt;
		if (timeCount >= 1) {
			--worldTimer;
			countdownLabel.setText(String.format("%03d", worldTimer));
			timeCount = 0;
		}
	}
	
	public static void addScore(int value) {
		score += value;
		scoreLabel.setText(String.format("%06d", score));
		
	}
	

	@Override
	public void dispose() {
		stage.dispose();
	}
	
}
