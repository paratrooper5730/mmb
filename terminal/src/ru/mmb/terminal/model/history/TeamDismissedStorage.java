package ru.mmb.terminal.model.history;

import java.util.HashMap;
import java.util.Map;

import ru.mmb.terminal.model.TeamLevelDismiss;
import ru.mmb.terminal.model.registry.TeamsRegistry;

public class TeamDismissedStorage
{
	private final Map<Integer, TeamDismissedState> teamDismissedStorage =
	    new HashMap<Integer, TeamDismissedState>();

	public void put(TeamLevelDismiss teamLevelDismiss)
	{
		Integer teamId = teamLevelDismiss.getTeamId();
		if (!teamDismissedStorage.containsKey(teamId))
		{
			teamDismissedStorage.put(teamId, new TeamDismissedState(teamLevelDismiss.getTeam()));
		}
		TeamDismissedState teamDismissedState = teamDismissedStorage.get(teamId);
		teamDismissedState.add(teamLevelDismiss);
	}

	public TeamDismissedState getTeamDismissedState(Integer teamId)
	{
		if (!teamDismissedStorage.containsKey(teamId))
		{
			teamDismissedStorage.put(teamId, new TeamDismissedState(TeamsRegistry.getInstance().getTeamById(teamId)));
		}
		return teamDismissedStorage.get(teamId);
	}

	public boolean containsTeamId(Integer teamId)
	{
		return teamDismissedStorage.containsKey(teamId);
	}
}
