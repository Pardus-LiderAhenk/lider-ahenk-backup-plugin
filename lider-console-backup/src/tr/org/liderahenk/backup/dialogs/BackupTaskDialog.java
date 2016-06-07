package tr.org.liderahenk.backup.dialogs;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;

/**
 * Task execution dialog for backup plugin.
 * 
 *  @author Seren Piri <seren.piri@gmail.com>
 */
public class BackupTaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(BackupTaskDialog.class);
	private BackupDialogBase backupDialog = new BackupDialogBase();
	
	public BackupTaskDialog(Shell parentShell, Set<String> dnSet)
	{
		super(parentShell, dnSet);
		logger.debug("Backup Task Editor Initialization");
	}

	@Override
	public String createTitle() {
		return "Backup";
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		backupDialog.createBackupDialog(parent, null);
		return null;
	}

	@Override
	public boolean validateBeforeExecution() {
		logger.debug("Backup Task - Validation is in progress.");
		return backupDialog.validateProfile();
	}

	@Override
	public Map<String, Object> getParameterMap() {
		return backupDialog.getParameterData();
	}

	@Override
	public String getCommandId() {
		// command id which is used to match tasks with ICommand class in the corresponding Lider plugin
		return "BACKUP_TASK";
	}

	@Override
	public String getPluginName() {
		return BackupConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return BackupConstants.PLUGIN_VERSION;
	}
	
}
