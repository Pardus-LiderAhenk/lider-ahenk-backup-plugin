package tr.org.liderahenk.backup.model;

import java.io.Serializable;

/**
 * Model class for backup parameter items.
 * 
 * @author <a href="mailto:seren.unal@agem.com.tr">Seren Ünal</a>
 *
 */
public class BackupParametersListItem implements Serializable {

	private static final long serialVersionUID = -1215191189845829199L;

	private String  directory;
	private String  excludeFileTypes;
	private String  logicalVolume;
	private String  virtualGroup;
	private String  logicalVolumeSize;
	private boolean recursive;
	private boolean protectGroup;
	private boolean protectOwner;
	private boolean protectPermissions;
	private boolean archive;
	private boolean compress;
	private boolean updateOnlyExistings;

	public BackupParametersListItem() {
		super();
	}

	public BackupParametersListItem(String directory, String excludeFileTypes, String logicalVolume,
			String virtualGroup, String logicalVolumeSize, boolean recursive, boolean protectGroup, boolean protectOwner,
			boolean protectPermissions, boolean archive, boolean compress, boolean updateOnlyExistings) {
		super();
		this.directory = directory;
		this.excludeFileTypes = excludeFileTypes;
		this.logicalVolume = logicalVolume;
		this.virtualGroup = virtualGroup;
		this.logicalVolumeSize = logicalVolumeSize;
		this.recursive = recursive;
		this.protectGroup = protectGroup;
		this.protectOwner = protectOwner;
		this.protectPermissions = protectPermissions;
		this.archive = archive;
		this.compress = compress;
		this.updateOnlyExistings = updateOnlyExistings;
	}


	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public boolean isProtectGroup() {
		return protectGroup;
	}

	public void setProtectGroup(boolean protectGroup) {
		this.protectGroup = protectGroup;
	}

	public boolean isProtectOwner() {
		return protectOwner;
	}

	public void setProtectOwner(boolean protectOwner) {
		this.protectOwner = protectOwner;
	}

	public boolean isProtectPermissions() {
		return protectPermissions;
	}

	public void setProtectPermissions(boolean protectPermissions) {
		this.protectPermissions = protectPermissions;
	}

	public boolean isArchive() {
		return archive;
	}

	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public boolean isUpdateOnlyExistings() {
		return updateOnlyExistings;
	}

	public void setUpdateOnlyExistings(boolean updateOnlyExistings) {
		this.updateOnlyExistings = updateOnlyExistings;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getExcludeFileTypes() {
		return excludeFileTypes;
	}

	public void setExcludeFileTypes(String excludeFileTypes) {
		this.excludeFileTypes = excludeFileTypes;
	}

	public String getLogicalVolume() {
		return logicalVolume;
	}

	public void setLogicalVolume(String logicalVolume) {
		this.logicalVolume = logicalVolume;
	}

	public String getVirtualGroup() {
		return virtualGroup;
	}

	public void setVirtualGroup(String virtualGroup) {
		this.virtualGroup = virtualGroup;
	}

	public String getLogicalVolumeSize() {
		return logicalVolumeSize;
	}

	public void setLogicalVolumeSize(String logicalVolumeSize) {
		this.logicalVolumeSize = logicalVolumeSize;
	}

}
