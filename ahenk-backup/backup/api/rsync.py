#!/usr/bin/python3
# -*- coding: utf-8 -*-

import subprocess,sys
from requests.api import options

class BackupRsync():
    def __init__(self, backup_data):
        super(BackupRsync, self).__init__()
        self.backup_data = backup_data
    
    def prepareCommand(self):
        destinationPath = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data['destPath']
        for backup_directory in self.backup_data['directories']:
            path = backup_directory['sourcePath'] + ' ' + destinationPath
            options = ' --info=progress '
            if source['recursive']:
                options = options + ' -r '
            if source['preserveGroup']:
                options = options + ' -g '
            if source['preserveOwner']:
                options = options + ' -o '
            if source['preservePermissions']:
                options = options + ' -p '
            if source['archive']:
                options = options + ' -a '
            if source['compress']:
                options = options + ' -z '
            if source['existingOnly']:
                options = options + ' --existing '
            if source['excludePattern']:
                options = options + ' --exclude "' + source['excludePattern'] + '" '
        backup_command = 'rsync ' + options + ' ' + path
        return backup_command