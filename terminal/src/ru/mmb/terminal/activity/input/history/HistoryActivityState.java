package ru.mmb.terminal.activity.input.history;

import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_TEAM_NUMBER;
import ru.mmb.terminal.activity.ActivityStateWithTeamAndLevel;
import android.os.Bundle;

public class HistoryActivityState extends ActivityStateWithTeamAndLevel
{
	private String teamNumber = null;

	public HistoryActivityState()
	{
		super("input.history");
	}

	@Override
	public void save(Bundle savedInstanceState)
	{
		super.save(savedInstanceState);
		if (teamNumber != null)
		    savedInstanceState.putString(KEY_CURRENT_TEAM_NUMBER, teamNumber);
	}

	@Override
	public void load(Bundle savedInstanceState)
	{
		super.load(savedInstanceState);
		if (savedInstanceState.containsKey(KEY_CURRENT_TEAM_NUMBER))
		    teamNumber = savedInstanceState.getString(KEY_CURRENT_TEAM_NUMBER);
	}

	public String getNumberFilter()
	{
		return teamNumber;
	}

	public int getNumberFilterAsInt()
	{
		return Integer.parseInt(teamNumber);
	}

	public void setNumberFilter(String numberFilter)
	{
		if (isEmptyString(numberFilter))
			this.teamNumber = null;
		else
			this.teamNumber = numberFilter;
	}

	private boolean isEmptyString(String numberFilter)
	{
		return numberFilter != null && numberFilter.trim().length() == 0;
	}
}
