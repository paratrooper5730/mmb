package ru.mmb.terminal.activity.level;

import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_DISTANCE;
import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_LEVEL;
import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_LEVEL_POINT_TYPE;
import static ru.mmb.terminal.activity.Constants.KEY_LEVEL_SELECT_MODE;
import ru.mmb.terminal.R;
import ru.mmb.terminal.activity.ActivityStateWithTeamAndLevel;
import ru.mmb.terminal.activity.LevelPointType;
import ru.mmb.terminal.activity.StateChangeListener;
import ru.mmb.terminal.model.Distance;
import ru.mmb.terminal.model.Level;
import ru.mmb.terminal.model.StartType;
import ru.mmb.terminal.model.registry.DistancesRegistry;
import ru.mmb.terminal.model.registry.Settings;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Spinner;

public class SelectLevelActivity extends Activity implements StateChangeListener
{
	private ActivityStateWithTeamAndLevel currentState;

	private Spinner inputDistance;
	private Spinner inputLevel;
	private RadioButton radioStart;
	private RadioButton radioFinish;
	private Button btnOk;

	private LevelSelectMode levelSelectMode = LevelSelectMode.NORMAL;

	private DistancesRegistry distances;

	private int prevSelectedDistancePos = -1;
	private int currSelectedDistancePos = -1;

	private int prevSelectedLevelPos = -1;
	private int currSelectedLevelPos = -1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Settings.getInstance().setCurrentContext(this);

		distances = DistancesRegistry.getInstance();

		currentState = new ActivityStateWithTeamAndLevel("input.level");
		currentState.initialize(this, savedInstanceState);

		updateLevelSelectMode(savedInstanceState);

		setContentView(R.layout.input_level);

		inputDistance = (Spinner) findViewById(R.id.inputLevel_distanceInput);
		inputLevel = (Spinner) findViewById(R.id.inputLevel_levelInput);
		radioStart = (RadioButton) findViewById(R.id.inputLevel_startRadio);
		radioFinish = (RadioButton) findViewById(R.id.inputLevel_finishRadio);
		btnOk = (Button) findViewById(R.id.inputLevel_okBtn);

		setInputDistanceAdapter();

		inputDistance.setOnItemSelectedListener(new InputDistanceOnItemSelectedListener());
		inputLevel.setOnItemSelectedListener(new InputLevelOnItemSelectedListener());
		radioStart.setOnCheckedChangeListener(new RadioOnCheckedChangeListener());
		radioFinish.setOnCheckedChangeListener(new RadioOnCheckedChangeListener());
		btnOk.setOnClickListener(new OkBtnClickListener());

		initializeControls();

		currentState.addStateChangeListener(this);
		onStateChange();
	}

	private void updateLevelSelectMode(Bundle savedInstanceState)
	{
		if (savedInstanceState == null)
		{
			Bundle extras = getIntent().getExtras();
			if (extras == null) return;
			if (extras.containsKey(KEY_LEVEL_SELECT_MODE))
			    levelSelectMode = (LevelSelectMode) extras.getSerializable(KEY_LEVEL_SELECT_MODE);
		}
		else
		{
			if (savedInstanceState.containsKey(KEY_LEVEL_SELECT_MODE))
			    levelSelectMode =
			        (LevelSelectMode) savedInstanceState.getSerializable(KEY_LEVEL_SELECT_MODE);
		}
	}

	private void setInputDistanceAdapter()
	{
		ArrayAdapter<String> adapter =
		    new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, distances.getDistanceNamesArray());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		inputDistance.setAdapter(adapter);
	}

	private void initializeControls()
	{
		refreshInputDistanceState();

		if (currentState.getCurrentDistance() == null)
		    currentState.setCurrentDistance(distances.getDistanceByIndex(0));
		Distance currentDistance = currentState.getCurrentDistance();
		setInitialDistancePos(currentDistance);

		refreshInputLevelState();

		if (currentState.getCurrentLevel() == null)
		    currentState.setCurrentLevel(currentState.getCurrentDistance().getLevelByIndex(0));
		Level currentLevel = currentState.getCurrentLevel();
		setInitialLevelPos(currentDistance, currentLevel);

		refreshInputModeState();
	}

	private void setInitialDistancePos(Distance currentDistance)
	{
		currSelectedDistancePos = distances.getDistanceIndex(currentDistance);
		prevSelectedDistancePos = currSelectedDistancePos;
	}

	private void setInitialLevelPos(Distance currentDistance, Level currentLevel)
	{
		currSelectedLevelPos = currentDistance.getLevelIndex(currentLevel);
		prevSelectedLevelPos = currSelectedLevelPos;
	}

	private void refreshInputDistanceState()
	{
		if (currentState.getCurrentDistance() == null)
		{
			inputDistance.setSelection(0);
		}
		else
		{
			int pos = distances.getDistanceIndex(currentState.getCurrentDistance());
			if (pos == -1) pos = 0;
			inputDistance.setSelection(pos);
		}
	}

	private void refreshInputLevelState()
	{
		setInputLevelAdapter();

		if (currentState.getCurrentLevel() == null || !isLevelFromCurrentDistance())
		{
			inputLevel.setSelection(0);
		}
		else
		{
			int pos =
			    currentState.getCurrentDistance().getLevelIndex(currentState.getCurrentLevel());
			if (pos == -1) pos = 0;
			inputLevel.setSelection(pos);
		}
	}

	private void setInputLevelAdapter()
	{
		ArrayAdapter<String> adapter =
		    new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currentState.getCurrentDistance().getLevelNamesArray());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		inputLevel.setAdapter(adapter);
	}

	private boolean isLevelFromCurrentDistance()
	{
		return currentState.getCurrentLevel().getDistance() == currentState.getCurrentDistance();
	}

	private void refreshInputModeState()
	{
		Level level = currentState.getCurrentLevel();

		if (level == null)
		{
			radioStart.setEnabled(false);
			radioFinish.setEnabled(false);
			currentState.setCurrentLevelPointType(null);
			return;
		}

		if (level.getLevelStartType() == StartType.USE_PREVIOUS_FINISH
		        || levelSelectMode == LevelSelectMode.BARCODE)
		{
			radioStart.setEnabled(false);
			radioFinish.setEnabled(true);
			radioFinish.setChecked(true);
			currentState.setCurrentLevelPointType(LevelPointType.FINISH);
		}
		else
		{
			radioStart.setEnabled(true);
			radioFinish.setEnabled(true);
			if (currentState.getCurrentLevelPointType() == null)
			{
				radioStart.setChecked(true);
				currentState.setCurrentLevelPointType(LevelPointType.START);
			}
			if (currentState.getCurrentLevelPointType() == LevelPointType.START)
			    radioStart.setChecked(true);
			if (currentState.getCurrentLevelPointType() == LevelPointType.FINISH)
			    radioFinish.setChecked(true);
		}
	}

	private class InputDistanceOnItemSelectedListener implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{
			currSelectedDistancePos = pos;
			if (currSelectedDistancePos != prevSelectedDistancePos)
			{
				prevSelectedDistancePos = currSelectedDistancePos;
				currentState.setCurrentDistance(distances.getDistanceByIndex(pos));
				refreshInputLevelState();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void onNothingSelected(AdapterView parent)
		{
			// Do nothing.
		}
	}

	private class InputLevelOnItemSelectedListener implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{
			currSelectedLevelPos = pos;
			if (currSelectedLevelPos != prevSelectedLevelPos)
			{
				prevSelectedLevelPos = currSelectedLevelPos;
				Distance distance = currentState.getCurrentDistance();
				currentState.setCurrentLevel(distance.getLevelByIndex(pos));
				currentState.setCurrentLevelPointType(null);
				refreshInputModeState();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void onNothingSelected(AdapterView parent)
		{
			// Do nothing.
		}
	}

	private class RadioOnCheckedChangeListener implements OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			if (isChecked)
			{
				if (buttonView == radioStart)
					currentState.setCurrentLevelPointType(LevelPointType.START);
				else
					currentState.setCurrentLevelPointType(LevelPointType.FINISH);
			}
		}
	}

	private class OkBtnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent resultData = new Intent();
			if (currentState.getCurrentDistance() != null)
			    resultData.putExtra(KEY_CURRENT_DISTANCE, currentState.getCurrentDistance());
			if (currentState.getCurrentLevel() != null)
			    resultData.putExtra(KEY_CURRENT_LEVEL, currentState.getCurrentLevel());
			if (currentState.getCurrentLevelPointType() != null)
			    resultData.putExtra(KEY_CURRENT_LEVEL_POINT_TYPE, currentState.getCurrentLevelPointType());
			setResult(RESULT_OK, resultData);
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		currentState.save(outState);
		outState.putSerializable(KEY_LEVEL_SELECT_MODE, levelSelectMode);
	}

	@Override
	public void onStateChange()
	{
		setTitle(currentState.getLevelPointText(this));

		btnOk.setEnabled(currentState.isLevelSelected());
	}
}
