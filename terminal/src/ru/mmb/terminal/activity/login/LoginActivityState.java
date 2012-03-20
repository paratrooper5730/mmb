package ru.mmb.terminal.activity.login;

import static ru.mmb.terminal.activity.Constants.KEY_ACTIVE_USER;
import ru.mmb.terminal.activity.CurrentState;
import ru.mmb.terminal.model.User;
import ru.mmb.terminal.model.registry.UsersRegistry;
import android.os.Bundle;

public class LoginActivityState extends CurrentState
{
	public LoginActivityState()
	{
		super("login");
	}

	private User activeUser = null;

	public User getActiveUser()
	{
		return activeUser;
	}

	public void setActiveUser(User activeUser)
	{
		this.activeUser = activeUser;
	}

	@Override
	public void save(Bundle savedInstanceState)
	{
		if (activeUser != null) savedInstanceState.putSerializable(KEY_ACTIVE_USER, activeUser);
	}

	@Override
	public void load(Bundle savedInstanceState)
	{
		if (savedInstanceState == null) return;

		if (savedInstanceState.containsKey(KEY_ACTIVE_USER))
		    activeUser = (User) savedInstanceState.getSerializable(KEY_ACTIVE_USER);
	}

	@Override
	protected void update()
	{
		UsersRegistry users = UsersRegistry.getInstance();
		if (activeUser != null)
		{
			activeUser = users.getUserById(activeUser.getId());
		}
	}

	@Override
	protected void loadFromExtrasBundle(Bundle extras)
	{
		if (extras.containsKey(KEY_ACTIVE_USER))
		    setActiveUser((User) extras.getSerializable(KEY_ACTIVE_USER));
	}
}