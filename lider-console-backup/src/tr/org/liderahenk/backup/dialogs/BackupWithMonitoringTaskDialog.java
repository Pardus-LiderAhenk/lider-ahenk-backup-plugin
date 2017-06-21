package tr.org.liderahenk.backup.dialogs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

import tr.org.liderahenk.backup.comparators.ResultComparator;
import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.backup.labelProviders.ProgressLabelProvider;
import tr.org.liderahenk.backup.model.MonitoringTableItem;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Command;
import tr.org.liderahenk.liderconsole.core.model.CommandExecution;
import tr.org.liderahenk.liderconsole.core.model.CommandExecutionResult;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.enums.StatusCode;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskNotification;

public class BackupWithMonitoringTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(BackupWithMonitoringTaskDialog.class);

	private Text txtUsername;
	private Text txtPassword;
	private Text txtSourcePath;
	private Text txtDestHost;
	private Text txtDestPort;
	private Text txtDestPath;
	private Composite inputComposite;
	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private Text txtSearch;

	private Label lblAgentCount;
	private Label lblOngoingAgentCount;
	private Label lblSuccessfulAgentCount;
	private Label lblUnavailableAgentCount;
	private Label lblMaxEstimation;

	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	Integer SUCCESSFUL_PERCENTAGE = new Integer(100);
	Timer timer = null;

	public BackupWithMonitoringTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
		subscribeEventHandler(getPluginName().toUpperCase(Locale.ENGLISH) + "_TASK_NOTIFICATION", eventHandler);
	}

	@Override
	public String createTitle() {
		return Messages.getString("BACKUP_WITH_MONITORING");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createBackupInputArea(parent);
		createMonitoringTableArea(parent);
		return null;
	}

	private void createBackupInputArea(final Composite parent) {

		Label lblInput = new Label(parent, SWT.NONE);
		lblInput.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblInput.setText(Messages.getString("BACKUP_INPUTS"));

		inputComposite = new Composite(parent, SWT.BORDER);
		inputComposite.setLayout(new GridLayout(4, true));
		inputComposite.setData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label labelUN = new Label(inputComposite, SWT.NONE);
		labelUN.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelUN.setText(Messages.getString("USERNAME"));
		txtUsername = new Text(inputComposite, SWT.BORDER);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelPassword = new Label(inputComposite, SWT.NONE);
		labelPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelPassword.setText(Messages.getString("PASSWORD"));
		txtPassword = new Text(inputComposite, SWT.PASSWORD | SWT.BORDER);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		Label labelHost = new Label(inputComposite, SWT.NONE);
		labelHost.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelHost.setText(Messages.getString("DEST_HOST"));
		txtDestHost = new Text(inputComposite, SWT.BORDER);
		txtDestHost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelPort = new Label(inputComposite, SWT.NONE);
		labelPort.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelPort.setText(Messages.getString("DEST_PORT"));
		txtDestPort = new Text(inputComposite, SWT.BORDER);
		txtDestPort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtDestPort.setText(BackupConstants.DEFAULT_PORT);

		Label labelSrcDir = new Label(inputComposite, SWT.NONE);
		labelSrcDir.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelSrcDir.setText(Messages.getString("SOURCE_DIR"));
		txtSourcePath = new Text(inputComposite, SWT.BORDER);
		txtSourcePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label labelDir = new Label(inputComposite, SWT.NONE);
		labelDir.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelDir.setText(Messages.getString("DEST_DIR"));
		txtDestPath = new Text(inputComposite, SWT.BORDER);
		txtDestPath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	private void createMonitoringTableArea(Composite parent) {
		Label lblMonitoring = new Label(parent, SWT.NONE);
		lblMonitoring.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblMonitoring.setText(Messages.getString("BACKUP_MONITORING"));
		createTableInfoArea(parent);
		createTableFilterArea(parent);
		createTable(parent);
	}

	private void createTableInfoArea(Composite parent) {
		lblAgentCount = new Label(parent, SWT.NONE);
		lblAgentCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblAgentCount.setText(Messages.getString("AGENT_COUNT", getDnSet().size()));

		lblOngoingAgentCount = new Label(parent, SWT.NONE);
		lblOngoingAgentCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblOngoingAgentCount.setText(Messages.getString("ONGOING_AGENT_COUNT", "0"));

		lblSuccessfulAgentCount = new Label(parent, SWT.NONE);
		lblSuccessfulAgentCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblSuccessfulAgentCount.setText(Messages.getString("SUCCESSFUL_AGENT_COUNT", "0"));

		lblUnavailableAgentCount = new Label(parent, SWT.NONE);
		lblUnavailableAgentCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblUnavailableAgentCount.setText(Messages.getString("UNAVAILABLE_AGENT_COUNT", getDnSet().size()));

		lblMaxEstimation = new Label(parent, SWT.NONE);
		lblMaxEstimation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblMaxEstimation.setText(Messages.getString("MAX_ESTIMATION", "-"));
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
			MonitoringTableItem item = (MonitoringTableItem) element;
			return item.getDn().matches(searchString) || item.getPercentage().matches(searchString);
		}
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
		TableViewerColumn agentColumn = createTableViewerColumn(Messages.getString("AGENT"), 300);
		agentColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MonitoringTableItem) {
					return ((MonitoringTableItem) element).getDn();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn progressBarColumn = createTableViewerColumn(Messages.getString("PROGRESS_BAR"), 200);
		progressBarColumn.setLabelProvider(new ProgressLabelProvider(tableViewer));

		TableViewerColumn estimationColumn = createTableViewerColumn(Messages.getString("ESTIMATION"), 100);
		estimationColumn.setLabelProvider(new ColumnLabelProvider() {
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
					monitor.beginTask("BACKUP_LIST", 100);
					try {
						final TaskNotification task = (TaskNotification) event
								.getProperty("org.eclipse.e4.data");
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								Long taskId = task.getCommand().getTask().getId();
								timer = new Timer();
								timer.schedule(new CheckResults(taskId), 0, 500);
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR"));
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

	protected class CheckResults extends TimerTask {
		
		Long taskId = null;
		
		public CheckResults(Long taskId) {
			this.taskId = taskId;
		}

		@Override
		public void run() {
			try {
				final Command command = TaskRestUtils.getCommand(this.taskId);
				if (command != null && command.getCommandExecutions() != null) {
					final List<MonitoringTableItem> items = new ArrayList<MonitoringTableItem>();
					int successful = 0, ongoing = 0;
					String maxEstimation = null;
					// Iterate over each agent
					for (CommandExecution exec : command.getCommandExecutions()) {
						List<CommandExecutionResult> results = exec.getCommandExecutionResults();
						String estimation = Messages.getString("NO_RESULT");
						String percentage = "0";
						if (results != null && !results.isEmpty()) {
							// Find latest result
							results.sort(new ResultComparator());
							CommandExecutionResult result = results.get(0);
							
							if (result.getResponseCode() == StatusCode.TASK_PROCESSED) {
								percentage = "100";
								estimation = "00:00:00";
							} else { // TASK_PROCESSING
								// Read estimation & percentage
								byte[] data = result.getResponseData();
								Map<String, Object> responseData = null;
								try {
									responseData = new ObjectMapper().readValue(data, 0,
											data.length, new TypeReference<HashMap<String, Object>>() {
											});
								} catch (JsonParseException e) {
									e.printStackTrace();
								} catch (JsonMappingException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								percentage = responseData.get(BackupConstants.PARAMETERS.PERCENTAGE).toString();
								estimation = responseData.get(BackupConstants.PARAMETERS.ESTIMATION).toString();	
							}
							
							if (result.getResponseCode() == StatusCode.TASK_PROCESSED || SUCCESSFUL_PERCENTAGE.toString().equals(percentage)) {
								successful++;
							}
							else if (result.getResponseCode() == StatusCode.TASK_PROCESSING) {
								ongoing++;
							}
							
							// Update max estimation if necessary
							maxEstimation = (maxEstimation != null && sdf.parse(maxEstimation).getTime() > sdf.parse(estimation).getTime()) ? maxEstimation : estimation;
						}

						// Create table item
						MonitoringTableItem item = new MonitoringTableItem(exec.getDn(), percentage, estimation);
						items.add(item);
					}
					final int fSuccessful = successful;
					final int fOngoing = ongoing;
					final String fMaxEstimation = maxEstimation;

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							// Update table
							tableViewer.setInput(items);
							tableViewer.refresh();
							// Update labels
							int unavailable = getDnSet().size() - (fSuccessful + fOngoing);
							lblOngoingAgentCount.setText(Messages.getString("ONGOING_AGENT_COUNT", fOngoing));
							lblSuccessfulAgentCount.setText(Messages.getString("SUCCESSFUL_AGENT_COUNT", fSuccessful));
							lblUnavailableAgentCount.setText(Messages.getString("UNAVAILABLE_AGENT_COUNT", unavailable));
							lblMaxEstimation.setText(Messages.getString("MAX_ESTIMATION", fMaxEstimation));							
						}
					});
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	};

	@Override
	protected Point getInitialSize() {
		return new Point(800, 800);
	}

	@Override
	protected void onClose() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
	}

}
