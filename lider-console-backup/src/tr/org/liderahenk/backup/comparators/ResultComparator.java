package tr.org.liderahenk.backup.comparators;

import java.util.Comparator;

import tr.org.liderahenk.liderconsole.core.model.CommandExecutionResult;
import tr.org.liderahenk.liderconsole.core.xmpp.enums.StatusCode;

public class ResultComparator implements Comparator<CommandExecutionResult> {

	@Override
	public int compare(CommandExecutionResult o1, CommandExecutionResult o2) {
		if (o1.getResponseCode() == StatusCode.TASK_PROCESSED) {
			return -1;
		} else if (o2.getResponseCode() == StatusCode.TASK_PROCESSED) {
			return 1;
		}
		return o2.getCreateDate().compareTo(o1.getCreateDate());
	}

}
