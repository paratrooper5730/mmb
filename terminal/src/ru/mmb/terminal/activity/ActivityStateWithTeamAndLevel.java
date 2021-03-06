package ru.mmb.terminal.activity;

import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_DISTANCE;
import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_LEVEL;
import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_LEVEL_POINT_TYPE;
import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_TEAM;
import ru.mmb.terminal.R;
import ru.mmb.terminal.model.Distance;
import ru.mmb.terminal.model.Level;
import ru.mmb.terminal.model.LevelPoint;
import ru.mmb.terminal.model.Team;
import ru.mmb.terminal.model.registry.DistancesRegistry;
import ru.mmb.terminal.model.registry.TeamsRegistry;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ActivityStateWithTeamAndLevel extends CurrentState
{
	private Distance currentDistance = null;
	private Level currentLevel = null;
	private LevelPointType currentLevelPointType = null;
	private Team currentTeam = null;

	public ActivityStateWithTeamAndLevel(String prefix)
	{
		super(prefix);
	}

	public Distance getCurrentDistance()
	{
		return currentDistance;
	}

	public void setCurrentDistance(Distance currentDistance)
	{
		this.currentDistance = currentDistance;
		fireStateChanged();
	}

	public Level getCurrentLevel()
	{
		return currentLevel;
	}

	public void setCurrentLevel(Level currentLevel)
	{
		this.currentLevel = currentLevel;
		fireStateChanged();
	}

	public LevelPointType getCurrentLevelPointType()
	{
		return currentLevelPointType;
	}

	public void setCurrentLevelPointType(LevelPointType currentLevelPointType)
	{
		this.currentLevelPointType = currentLevelPointType;
		fireStateChanged();
	}

	@Override
	public void save(Bundle savedInstanceState)
	{
		if (currentDistance != null)
		    savedInstanceState.putSerializable(KEY_CURRENT_DISTANCE, currentDistance);
		if (currentLevel != null)
		    savedInstanceState.putSerializable(KEY_CURRENT_LEVEL, currentLevel);
		if (currentLevelPointType != null)
		    savedInstanceState.putSerializable(KEY_CURRENT_LEVEL_POINT_TYPE, currentLevelPointType);
		if (currentTeam != null) savedInstanceState.putSerializable(KEY_CURRENT_TEAM, currentTeam);
	}

	@Override
	public void load(Bundle savedInstanceState)
	{
		if (savedInstanceState == null) return;

		if (savedInstanceState.containsKey(KEY_CURRENT_DISTANCE))
		    currentDistance = (Distance) savedInstanceState.getSerializable(KEY_CURRENT_DISTANCE);
		if (savedInstanceState.containsKey(KEY_CURRENT_LEVEL))
		    currentLevel = (Level) savedInstanceState.getSerializable(KEY_CURRENT_LEVEL);
		if (savedInstanceState.containsKey(KEY_CURRENT_LEVEL_POINT_TYPE))
		    currentLevelPointType =
		        (LevelPointType) savedInstanceState.getSerializable(KEY_CURRENT_LEVEL_POINT_TYPE);
		if (savedInstanceState.containsKey(KEY_CURRENT_TEAM))
		    currentTeam = (Team) savedInstanceState.getSerializable(KEY_CURRENT_TEAM);
	}

	@Override
	protected void update(boolean fromSavedBundle)
	{
		DistancesRegistry distances = DistancesRegistry.getInstance();

		if (currentDistance != null)
		{
			Distance updatedDistance = distances.getDistanceById(currentDistance.getDistanceId());
			if (updatedDistance == null)
			{
				currentDistance = null;
				currentLevel = null;
				currentLevelPointType = null;
			}
			else
			{
				currentDistance = updatedDistance;
			}
		}

		if (currentDistance != null && currentLevel != null)
		{
			Level updatedLevel = currentDistance.getLevelById(currentLevel.getLevelId());
			if (updatedLevel == null)
			{
				currentLevel = null;
				currentLevelPointType = null;
			}
			else
			{
				currentLevel = updatedLevel;
			}
		}

		if (currentTeam != null)
		{
			TeamsRegistry teams = TeamsRegistry.getInstance();
			Team updatedTeam = teams.getTeamById(currentTeam.getTeamId());
			currentTeam = updatedTeam;
		}
	}

	public boolean isLevelSelected()
	{
		return currentDistance != null && currentLevel != null && currentLevelPointType != null;
	}

	private String getSelectedLevelString(Activity activity)
	{
		if (!isLevelSelected())
			return activity.getResources().getString(R.string.input_global_no_selected_level);
		else
			return "\"" + currentDistance.getDistanceName() + "\" -> \""
			        + currentLevel.getLevelName() + "\" -> ["
			        + activity.getResources().getString(currentLevelPointType.getDisplayNameId())
			        + "]";
	}

	public String getLevelPointText(Activity activity)
	{
		return getSelectedLevelString(activity);
	}

	@Override
	protected void loadFromExtrasBundle(Bundle extras)
	{
		if (extras.containsKey(KEY_CURRENT_DISTANCE))
		    setCurrentDistance((Distance) extras.getSerializable(KEY_CURRENT_DISTANCE));
		if (extras.containsKey(KEY_CURRENT_LEVEL))
		    setCurrentLevel((Level) extras.getSerializable(KEY_CURRENT_LEVEL));
		if (extras.containsKey(KEY_CURRENT_LEVEL_POINT_TYPE))
		    setCurrentLevelPointType((LevelPointType) extras.getSerializable(KEY_CURRENT_LEVEL_POINT_TYPE));
		if (extras.containsKey(KEY_CURRENT_TEAM))
		    setCurrentTeam((Team) extras.getSerializable(KEY_CURRENT_TEAM));
	}

	@Override
	public void saveToSharedPreferences(SharedPreferences preferences)
	{
		SharedPreferences.Editor editor = preferences.edit();
		if (getCurrentDistance() != null)
		    editor.putInt(getPrefix() + "." + KEY_CURRENT_DISTANCE, getCurrentDistance().getDistanceId());
		if (getCurrentLevel() != null)
		    editor.putInt(getPrefix() + "." + KEY_CURRENT_LEVEL, getCurrentLevel().getLevelId());
		if (getCurrentLevelPointType() != null)
		    editor.putInt(getPrefix() + "." + KEY_CURRENT_LEVEL_POINT_TYPE, getCurrentLevelPointType().getId());
		editor.commit();
	}

	@Override
	public void loadFromSharedPreferences(SharedPreferences preferences)
	{
		DistancesRegistry distances = DistancesRegistry.getInstance();
		int distanceId = preferences.getInt(getPrefix() + "." + KEY_CURRENT_DISTANCE, -1);
		currentDistance = distances.getDistanceById(distanceId);
		if (currentDistance != null)
		{
			int levelId = preferences.getInt(getPrefix() + "." + KEY_CURRENT_LEVEL, -1);
			currentLevel = currentDistance.getLevelById(levelId);
			if (currentLevel != null)
			{
				int inputModeId =
				    preferences.getInt(getPrefix() + "." + KEY_CURRENT_LEVEL_POINT_TYPE, -1);
				if (inputModeId == -1)
					currentLevelPointType = null;
				else
					currentLevelPointType = LevelPointType.getById(inputModeId);
			}
			else
				currentLevelPointType = null;
		}
		else
		{
			currentLevel = null;
			currentLevelPointType = null;
		}
	}

	@Override
	public void prepareStartActivityIntent(Intent intent, int activityRequestId)
	{
		switch (activityRequestId)
		{
			case Constants.REQUEST_CODE_DEFAULT_ACTIVITY:
			case Constants.REQUEST_CODE_LEVEL_ACTIVITY:
			case Constants.REQUEST_CODE_INPUT_HISTORY_ACTIVITY:
			case Constants.REQUEST_CODE_INPUT_BARCODE_ACTIVITY:
			case Constants.REQUEST_CODE_INPUT_DATA_ACTIVITY:
			case Constants.REQUEST_CODE_WITHDRAW_MEMBER_ACTIVITY:
				if (getCurrentDistance() != null)
				    intent.putExtra(KEY_CURRENT_DISTANCE, getCurrentDistance());
				if (getCurrentLevel() != null)
				    intent.putExtra(KEY_CURRENT_LEVEL, getCurrentLevel());
				if (getCurrentLevelPointType() != null)
				    intent.putExtra(KEY_CURRENT_LEVEL_POINT_TYPE, getCurrentLevelPointType());
				if (getCurrentTeam() != null) intent.putExtra(KEY_CURRENT_TEAM, getCurrentTeam());
		}
	}

	public Team getCurrentTeam()
	{
		return currentTeam;
	}

	public void setCurrentTeam(Team currentTeam)
	{
		this.currentTeam = currentTeam;
	}

	public void setCurrentTeam(int teamId)
	{
		this.currentTeam = TeamsRegistry.getInstance().getTeamById(teamId);
	}

	public boolean isTeamSelected()
	{
		return currentTeam != null;
	}

	public LevelPoint getCurrentLevelPoint()
	{
		Level level = getCurrentLevel();
		LevelPoint result = level.getStartPoint();
		if (getCurrentLevelPointType() == LevelPointType.FINISH) result = level.getFinishPoint();
		return result;
	}

	@Override
	public String toString()
	{
		return "ActivityStateWithTeamAndLevel [currentDistance=" + currentDistance
		        + ", currentLevel=" + currentLevel + ", currentLevelPointType="
		        + currentLevelPointType + ", currentTeam=" + currentTeam + ", toString()="
		        + super.toString() + "]";
	}
}
