package tr.org.liderahenk.backup.commands;

import java.util.Map;

import tr.org.liderahenk.backup.plugininfo.PluginInfoImpl;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class ListBackupServerDirCommand implements ICommand {

	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		Map<String, Object> parameterMap = context.getRequest().getParameterMap();
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	@Override
	public String getCommandId() {
		return "LIST_BACKUP_SERVER_DIR";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setPluginInfo(PluginInfoImpl pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

}
