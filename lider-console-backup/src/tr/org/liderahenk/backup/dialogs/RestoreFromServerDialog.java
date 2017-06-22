package tr.org.liderahenk.backup.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.backup.model.BackupServerConf;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

public class RestoreFromServerDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(RestoreFromServerDialog.class);
	
	private Button btnBack;
	private Button btnRestore;
	private Button btnUpdateBackupServerConf;
	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private Text txtSearch;
	
	private BackupServerConf selectedConfig = null;
	
	public RestoreFromServerDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		return Messages.getString("RESTORE_FROM_SERVER");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createRestoreTableArea(parent);
		return null;
	}

	private void createRestoreTableArea(Composite parent) {
		
		try {
			selectedConfig = getBackupServerConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createTableButtonArea(parent);
		createTableFilterArea(parent);
		createTableArea(parent);
	}

	private void createTableArea(Composite parent) {
		GridData dataSearchGrid = new GridData();
		dataSearchGrid.grabExcessHorizontalSpace = true;
		dataSearchGrid.horizontalAlignment = GridData.FILL;

		tableViewer = new TableViewer(parent,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Create table columns
		createTableColumns();

		// Configure table layout
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.getVerticalBar().setEnabled(true);
		table.getVerticalBar().setVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());

		if (selectedConfig != null) {
			List<String> items;
			try {
				items = Arrays.asList(getBackupServerDirectories("/"));
				tableViewer.setInput(items);
				tableViewer.refresh();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 140;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);		
	}

	private void createTableColumns() {
		// TODO Auto-generated method stub
		
	}

	private void createTableFilterArea(Composite parent) {
		Composite filterContainer = new Composite(parent, SWT.NONE);
		filterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterContainer.setLayout(new GridLayout(2, false));

		// Search label
		Label lblSearch = new Label(filterContainer, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblSearch.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblSearch.setText(Messages.getString("SEARCH_FILTER"));

		// Filter table rows
		txtSearch = new Text(filterContainer, SWT.BORDER | SWT.SEARCH);
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtSearch.setToolTipText(Messages.getString("SEARCH_AGENT_TOOLTIP"));
		txtSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(txtSearch.getText());
				tableViewer.refresh();
			}
		});		
	}

	private void createTableButtonArea(Composite parent) {
		// TODO Auto-generated method stub
		
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
	
	private BackupServerConf getBackupServerConfig() throws Exception {
		IResponse response = null;
		try {
			response = TaskRestUtils.execute(BackupConstants.PLUGIN_NAME, BackupConstants.PLUGIN_VERSION,
					"GET_BACKUP_SERVER_CONFIG", false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
		return (BackupServerConf) ((response != null && response.getResultMap() != null
				&& response.getResultMap().get("BACKUP_SERVER_CONFIG") != null) ? new ObjectMapper().readValue(response.getResultMap().get("BACKUP_SERVER_CONFIG").toString(), BackupServerConf.class) : null);
	}
	
	private String[] getBackupServerDirectories(String path) throws Exception {
		IResponse response = null;
		try {
			response = TaskRestUtils.execute(BackupConstants.PLUGIN_NAME, BackupConstants.PLUGIN_VERSION,
					"LIST_BACKUP_SERVER_DIR", false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
		String result = response.getResultMap().get("CHILD_DIRS").toString();
		return result.split("\n");
	}
	
	public class TableFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s) {
			this.searchString = ".*" + s + ".*";
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			String item = (String) element;
			return item.matches(searchString);
		}
	}

}
