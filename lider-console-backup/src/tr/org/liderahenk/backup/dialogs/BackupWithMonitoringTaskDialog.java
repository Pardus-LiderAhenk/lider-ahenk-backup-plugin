package tr.org.liderahenk.backup.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.backup.labelProviders.ProgressLabelProvider;
import tr.org.liderahenk.backup.model.MonitoringTableItem;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;

public class BackupWithMonitoringTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(BackupWithMonitoringTaskDialog.class);

	private Button btnCheckSSH;
	private Text txtUsername;
	private Text txtPassword;
	private Text txtSourcePath;
	private Text txtDestHost;
	private Text txtDestPort;
	private Text txtDestPath;
	private Composite tableComposite;
	private TableViewer tableViewer;

	public BackupWithMonitoringTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
		subscribeEventHandler(eventHandler);
	}

	@Override
	public String createTitle() {
		return Messages.getString("BACKUP_WITH_MONITORING");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createBackupInputArea(composite);
		createMonitoringTableArea(composite);
		return composite;
	}

	private void createBackupInputArea(Composite composite) {
		btnCheckSSH = new Button(composite, SWT.CHECK);
		btnCheckSSH.setText(Messages.getString("SSH"));
		btnCheckSSH.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtPassword.setEnabled(!btnCheckSSH.getSelection());
				txtPassword.setText("");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		new Label(composite, SWT.NONE);

		Label labelUN = new Label(composite, SWT.NONE);
		labelUN.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelUN.setText(Messages.getString("USERNAME"));
		txtUsername = new Text(composite, SWT.BORDER);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelPassword = new Label(composite, SWT.NONE);
		labelPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelPassword.setText(Messages.getString("PASSWORD"));
		txtPassword = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelHost = new Label(composite, SWT.NONE);
		labelHost.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelHost.setText(Messages.getString("DEST_HOST"));
		txtDestHost = new Text(composite, SWT.BORDER);
		txtDestHost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelPort = new Label(composite, SWT.NONE);
		labelPort.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelPort.setText(Messages.getString("DEST_PORT"));
		txtDestPort = new Text(composite, SWT.BORDER);
		txtDestPort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtDestPort.setText(BackupConstants.DEFAULT_PORT);

		Label labelSrcDir = new Label(composite, SWT.NONE);
		labelSrcDir.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelSrcDir.setText(Messages.getString("SOURCE_DIR"));
		txtSourcePath = new Text(composite, SWT.BORDER);
		txtSourcePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelDir = new Label(composite, SWT.NONE);
		labelDir.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelDir.setText(Messages.getString("DEST_DIR"));
		txtDestPath = new Text(composite, SWT.BORDER);
		txtDestPath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	private void createMonitoringTableArea(Composite parent) {
		tableComposite = new Composite(parent, SWT.BORDER);
		tableComposite.setLayout(new GridLayout(1, false));
		new Label(parent, SWT.NONE);
		createTable(tableComposite);
		// Hide initially
		tableComposite.setVisible(false);
	}

	private void createTable(Composite parent) {
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

		List<MonitoringTableItem> items = createDefaultTableItems();
		tableViewer.setInput(items);
		tableViewer.refresh();

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 140;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
	}

	private List<MonitoringTableItem> createDefaultTableItems() {
		List<MonitoringTableItem> items = new ArrayList<MonitoringTableItem>();
		for (String dn : getDnSet()) {
			items.add(new MonitoringTableItem(dn, "0", Messages.getString("NO_RESULT")));
		}
		return items;
	}

	private void createTableColumns() {
		TableViewerColumn agentColumn = createTableViewerColumn(Messages.getString("AGENT"), 60);
		agentColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MonitoringTableItem) {
					return ((MonitoringTableItem) element).getDn();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn excFileTypesColumn = createTableViewerColumn(Messages.getString("PROGRESS_BAR"), 160);
		excFileTypesColumn.setLabelProvider(new ProgressLabelProvider(tableViewer));

		TableViewerColumn recursiveColumn = createTableViewerColumn(Messages.getString("PROGRESS_PERCENTAGE"), 40);
		recursiveColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MonitoringTableItem) {
					return ((MonitoringTableItem) element).getPercentage();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn groupColumn = createTableViewerColumn(Messages.getString("ESTIMATION"), 140);
		groupColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MonitoringTableItem) {
					return ((MonitoringTableItem) element).getEstimation();
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> profileData = new HashMap<String, Object>();
		profileData.put(BackupConstants.PARAMETERS.USERNAME, txtUsername.getText());
		profileData.put(BackupConstants.PARAMETERS.PASSWORD, txtPassword.getText());
		profileData.put(BackupConstants.PARAMETERS.USE_SSH_KEY, btnCheckSSH.getSelection());
		profileData.put(BackupConstants.PARAMETERS.DEST_HOST, txtDestHost.getText());
		profileData.put(BackupConstants.PARAMETERS.DEST_PORT, txtDestPort.getText());
		profileData.put(BackupConstants.PARAMETERS.SOURCE_PATH, txtSourcePath.getText());
		profileData.put(BackupConstants.PARAMETERS.DEST_PATH, txtDestPath.getText());
		return profileData;
	}

	@Override
	public String getCommandId() {
		return "BACKUP_WITH_MONITORING_TASK";
	}

	@Override
	public String getPluginName() {
		return BackupConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return BackupConstants.PLUGIN_VERSION;
	}

	private EventHandler eventHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("BACKUP_MONITORING", 100);
					try {
						final TaskStatusNotification taskStatus = (TaskStatusNotification) event
								.getProperty("org.eclipse.e4.data");
						byte[] data = taskStatus.getResult().getResponseData();
						final Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
								new TypeReference<HashMap<String, Object>>() {
								});
						Display.getDefault().asyncExec(new Runnable() {
							@SuppressWarnings("unchecked")
							@Override
							public void run() {
								if (responseData != null) {
									if (!tableComposite.isVisible()) {
										tableComposite.setVisible(true);
									}
									List<MonitoringTableItem> items = (List<MonitoringTableItem>) tableViewer.getInput();
									String dn = taskStatus.getCommandExecution().getDn();
									for (MonitoringTableItem item : items) {
										if (dn.equalsIgnoreCase(item.getDn())) {
											item.setEstimation(responseData.get("estimation").toString());
											item.setPercentage(responseData.get("percentage").toString());
											break;
										}
									}
									tableViewer.setInput(items);
									tableViewer.refresh();
								}
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_ACCESSING_SERVICES"));
					}
					monitor.worked(100);
					monitor.done();

					return Status.OK_STATUS;
				}
			};

			job.setUser(true);
			job.schedule();
		}
	};

}
