package tr.org.liderahenk.backup.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;
import tr.org.liderahenk.backup.dialogs.BackupTaskDialog;

//TODO use MultipleSelectionHandler if this task support multiple LDAP entries/DNs otherwise use SingleSelectionHandler.
public class BackupTaskHandler extends MultipleSelectionHandler {
	
	private Logger logger = LoggerFactory.getLogger(BackupTaskHandler.class);
	
	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		// TODO dnSet contains distinguished names (DN) of the selected LDAP entries.
		BackupTaskDialog dialog = new BackupTaskDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.open();
	}
	
}
