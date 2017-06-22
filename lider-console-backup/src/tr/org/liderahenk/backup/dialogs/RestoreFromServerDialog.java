package tr.org.liderahenk.backup.dialogs;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

public class RestoreFromServerDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(RestoreFromServerDialog.class);
	
	public RestoreFromServerDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		return Messages.getString("RESTORE_FROM_SERVER");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
	}

	@Override
	public Map<String, Object> getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandId() {
		return "RESTORE";
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
