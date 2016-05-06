package tr.org.liderahenk.backup.constants;

public class BackupConstants {
	
	public static final String PLUGIN_NAME = "backup";
	
	public static final String PLUGIN_VERSION = "1.0.0";
	
	public static final class PARAMETERS 
	{
		public static final String USERNAME  	= "username";
		public static final String PASSWORD  	= "password";
		public static final String DEST_HOST 	= "destHost";
		public static final String DEST_PORT 	= "destPort";
		public static final String DEST_DIR  	= "destDir";
		public static final String USE_SSH_KEY  = "sshKey";
		public static final String USE_LVM 	  	= "lvm";
		public static final String BACKUP_LIST_ITEMS = "directories";
	}
}