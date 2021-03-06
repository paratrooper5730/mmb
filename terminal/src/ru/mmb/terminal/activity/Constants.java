package ru.mmb.terminal.activity;

public class Constants
{
	public static final boolean UPDATE_FROM_SAVED_BUNDLE = true;
	public static final boolean UPDATE_FOR_FIRST_LAUNCH = false;

	public static final String KEY_CURRENT_DISTANCE = "current.distance";
	public static final String KEY_CURRENT_LEVEL = "current.level";
	public static final String KEY_CURRENT_LEVEL_POINT_TYPE = "current.level_point_type";
	public static final String KEY_CURRENT_TEAM_NUMBER = "current.team.number";
	public static final String KEY_CURRENT_TEAM = "current.team";
	public static final String KEY_CURRENT_INPUT_CHECKPOINTS_STATE =
	    "current.input.checkpoint.state";
	public static final String KEY_CURRENT_INPUT_CHECKED_DATE = "current.input.checked.date";
	public static final String KEY_CURRENT_INPUT_EXISTING_RECORD = "current.input.existing.record";
	public static final String KEY_CURRENT_INPUT_WITHDRAWN_CHECKED =
	    "current.input.withdrawn.checked";
	public static final String KEY_EXPORT_RESULT_MESSAGE = "export.result.message";
	public static final String KEY_LEVEL_SELECT_MODE = "level.select_mode";

	public static final int REQUEST_CODE_DEFAULT_ACTIVITY = -1;
	public static final int REQUEST_CODE_MAIN_ACTIVITY = 1;
	public static final int REQUEST_CODE_SETTINGS_ACTIVITY = 2;
	public static final int REQUEST_CODE_LEVEL_ACTIVITY = 3;
	public static final int REQUEST_CODE_INPUT_HISTORY_ACTIVITY = 4;
	public static final int REQUEST_CODE_INPUT_DATA_ACTIVITY = 5;
	public static final int REQUEST_CODE_WITHDRAW_MEMBER_ACTIVITY = 6;
	public static final int REQUEST_CODE_FILE_DIALOG = 7;
	public static final int REQUEST_CODE_INPUT_BARCODE_ACTIVITY = 8;
	public static final int REQUEST_CODE_SETTINGS_DB_FILE_DIALOG = 9;
	public static final int REQUEST_CODE_SETTINGS_DEVICE_JSON_DIALOG = 10;
}
