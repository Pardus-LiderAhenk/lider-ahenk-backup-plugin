package tr.org.liderahenk.backup.constants;

public class BackupConstants {

	public static final String PLUGIN_NAME = "backup";
	public static final String PLUGIN_VERSION = "1.0.0";

	public static final String DEFAULT_PORT = "22";

	public static final class PARAMETERS {
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String DEST_HOST = "destHost";
		public static final String DEST_PORT = "destPort";
		public static final String DEST_PATH = "destPath";
		public static final String SOURCE_PATH = "sourcePath";
		public static final String USE_SSH_KEY = "useSsh";
		public static final String USE_LVM = "useLvmShadow";
		public static final String BACKUP_LIST_ITEMS = "directories";
		public static final String ESTIMATION = "estimation";
		public static final String PERCENTAGE = "percentage";
	}

	public static final class EDITORS {
		public static final String BACKUP_TASK_LIST_EDITOR = "tr.org.liderahenk.backup.editors.BackupTaskListEditor";
	}
}