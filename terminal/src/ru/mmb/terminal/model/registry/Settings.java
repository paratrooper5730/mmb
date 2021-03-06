package ru.mmb.terminal.model.registry;

import static android.content.Context.MODE_PRIVATE;

import java.io.File;
import java.util.Properties;

import ru.mmb.terminal.db.TerminalDB;
import ru.mmb.terminal.model.BarCodeLastExportDates;
import android.content.Context;
import android.content.SharedPreferences;

public class Settings
{
	private static final String SETTINGS_FILE = "settings";

	private static final String PATH_TO_TERMINAL_DB = "path_to_terminal_db";
	private static final String IMPORT_DIR = "import_dir";
	private static final String DEVICE_ID = "device_id";
	private static final String USER_ID = "user_id";
	private static final String CURRENT_RAID_ID = "current_raid_id";
	private static final String LAST_EXPORT_DATE = "last_export_date";
	private static final String TRANSP_USER_ID = "transp_user_id";
	private static final String TRANSP_USER_PASSWORD = "transp_user_password";
	private static final String BARCODE_LAST_EXPORT_DATES = "barcode_last_export_dates";

	private static Settings instance = null;

	private Properties settings = null;
	private final BarCodeLastExportDates barCodeLastExportDates = new BarCodeLastExportDates();

	private Context currentContext = null;
	private boolean settingsLoaded = false;

	private SharedPreferences preferences;

	public static Settings getInstance()
	{
		if (instance == null)
		{
			instance = new Settings();
		}
		return instance;
	}

	private Settings()
	{
	}

	public void setCurrentContext(Context currentContext)
	{
		this.currentContext = currentContext;
		if (!settingsLoaded)
		{
			refresh();
		}
	}

	public void refresh()
	{
		if (currentContext == null) return;

		try
		{
			settings = new Properties();
			loadSettings();
			settingsLoaded = true;
		}
		catch (Exception e)
		{
			throw new RuntimeException("Couldn't load settings.", e);
		}
	}

	private void loadSettings()
	{
		preferences = currentContext.getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE);
		loadProperty(PATH_TO_TERMINAL_DB);
		loadProperty(IMPORT_DIR);
		loadProperty(USER_ID);
		loadProperty(DEVICE_ID);
		loadProperty(CURRENT_RAID_ID);
		loadProperty(LAST_EXPORT_DATE);
		loadProperty(TRANSP_USER_ID);
		loadProperty(TRANSP_USER_PASSWORD);
		loadBarCodeLastExportDates();
	}

	private void loadProperty(String propertyName)
	{
		String value = preferences.getString(propertyName, null);
		if (value != null)
		{
			settings.put(propertyName, value);
		}
	}

	private void loadBarCodeLastExportDates()
	{
		String value = preferences.getString(BARCODE_LAST_EXPORT_DATES, null);
		if (value != null) barCodeLastExportDates.loadFromString(value);
	}

	public String getPathToTerminalDB()
	{
		return settings.getProperty(PATH_TO_TERMINAL_DB, "");
	}

	public void setPathToTerminalDB(String pathToTerminalDB)
	{
		boolean changed = setValue(PATH_TO_TERMINAL_DB, pathToTerminalDB);
		if (changed)
		{
			TerminalDB.getRawInstance().closeConnection();
			// reconnect in getInstance
			TerminalDB.getRawInstance().tryConnectToDB();
			if (TerminalDB.getRawInstance().isConnected())
			{
				DistancesRegistry.getInstance().refresh();
				TeamsRegistry.getInstance().refresh();
				UsersRegistry.getInstance().refresh();
			}
		}
	}

	public String getImportDir()
	{
		String result = settings.getProperty(IMPORT_DIR, "");
		if ("".equals(result))
		{
			result = getMMBPathFromTerminalDBFile();
		}
		// Log.d("Settings", "get import directory: " + result);
		return result;
	}

	private String getMMBPathFromTerminalDBFile()
	{
		String result = settings.getProperty(PATH_TO_TERMINAL_DB, "");
		if (!"".equals(result))
		{
			File dbFile = new File(result);
			result = dbFile.getParent();
		}
		// Log.d("Settings", "get path to mmb directory: " + result);
		return result;
	}

	public void onImportFileSelected(String fileName)
	{
		if (fileName == null)
		{
			setImportDir("");
			return;
		}

		File importFile = new File(fileName);
		setImportDir(importFile.getParent());
	}

	private void setImportDir(String importDir)
	{
		setValue(IMPORT_DIR, importDir);
	}

	public String getExportDir()
	{
		return getMMBPathFromTerminalDBFile();
	}

	public int getDeviceId()
	{
		return getIntSetting(DEVICE_ID);
	}

	public void setDeviceId(String deviceId)
	{
		setValue(DEVICE_ID, deviceId);
	}

	public int getUserId()
	{
		return getIntSetting(USER_ID);
	}

	public void setUserId(String userId)
	{
		setValue(USER_ID, userId);
	}

	public int getCurrentRaidId()
	{
		return getIntSetting(CURRENT_RAID_ID);
	}

	public void setCurrentRaidId(String currentRaidId)
	{
		boolean changed = setValue(CURRENT_RAID_ID, currentRaidId);
		if (changed)
		{
			if (TerminalDB.getConnectedInstance() != null)
			{
				DistancesRegistry.getInstance().refresh();
				TeamsRegistry.getInstance().refresh();
				UsersRegistry.getInstance().refresh();
			}
		}
	}

	public String getLastExportDate()
	{
		return settings.getProperty(LAST_EXPORT_DATE, "");
	}

	public void setLastExportDate(String lastExportDate)
	{
		setValue(LAST_EXPORT_DATE, lastExportDate);
	}

	public int getTranspUserId()
	{
		return getIntSetting(TRANSP_USER_ID);
	}

	public void setTranspUserId(String transpUserId)
	{
		setValue(TRANSP_USER_ID, transpUserId);
	}

	public String getTranspUserPassword()
	{
		return settings.getProperty(TRANSP_USER_PASSWORD, "");
	}

	public void setTranspUserPassword(String transpUserPassword)
	{
		setValue(TRANSP_USER_PASSWORD, transpUserPassword);
	}

	public String getBarCodeLastExportDate(Integer levelPointId)
	{
		String result = barCodeLastExportDates.get(levelPointId);
		return result == null ? "" : result;
	}

	public void setBarCodeLastExportDate(Integer levelPointId, String lastExportDate)
	{
		barCodeLastExportDates.put(levelPointId, lastExportDate);
		setValue(BARCODE_LAST_EXPORT_DATES, barCodeLastExportDates.saveToString());
	}

	private boolean setValue(String settingName, String newValue)
	{
		boolean changed = false;
		String oldValue = (String) settings.get(settingName);
		if (oldValue == null || !oldValue.equals(newValue))
		{
			settings.put(settingName, newValue);
			saveSetting(settingName, newValue);
			changed = true;
		}
		return changed;
	}

	private void saveSetting(String settingName, String value)
	{
		if (currentContext == null)
		{
			throw new RuntimeException("Error. Settings saveSetting while current context is NULL.");
		}

		preferences = currentContext.getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(settingName, value);
		editor.commit();
	}

	private int getIntSetting(String settingName)
	{
		String valueString = settings.getProperty(settingName);
		if (valueString == null || valueString.trim().length() == 0)
		{
			return -1;
		}
		return Integer.parseInt(valueString);
	}
}
