#!/usr/bin/python3
# -*- coding: utf-8 -*-

import subprocess,sys, os, thread
from requests.api import options
from pip.cmdoptions import process_dependency_links
from pexpect import expect
import cmd

class BackupRsync():
    def __init__(self, backup_data):
        super(BackupRsync, self).__init__()
        self.backup_data = backup_data
        self.backup_result = []
    
    def prepare_command(self):
        destinationPath = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data['destPath']
        for backup_directory in self.backup_data['directories']:
            path = backup_directory['sourcePath'] + ' ' + destinationPath
            options = ' -v --progress --stats '
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
    
    def execute_command(self,cmd):
        try:
            command_process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE)
            while True:
                output = process.stdout.readline()
                if output == '' or process.poll() is not None:
                    return 0
                if output:
                    print('in progress')
        except Exception as e:
            return str(e)
    
    def get_resource_total_size(self,sourcePath):
        return os.stat(sourcePath).st_size
    
    def is_file(self,source_path):
        return os.path.isfile(source_path)
    
    def append_command_execution_type(self,cmd):
        return 'sshpass -p ' + self.backup_data['password'] + cmd
    
    def backup(self):
        cmd = self.append_command_execution_type(self.prepare_command())
        self.execute_command(cmd)
    
    def backup_watcher(self):
        print(self.backup_result)
    
    def start_backup_watcher(self):
        try:
            thread.start_new_thread (backup_watcher, None)
        except Exception as e:
            print(e)
    
    
    