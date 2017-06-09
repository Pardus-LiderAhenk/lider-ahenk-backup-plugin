#!/usr/bin/python3
# -*- coding: utf-8 -*-

import subprocess,sys, os, threading, time
from requests.api import options
from pip.cmdoptions import process_dependency_links
from pexpect import expect
import cmd
from base.scope import Scope

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))
from sys import stderr
from rsync_parser import BackupParser

class BackupRsync():
    def __init__(self, backup_data):
        super(BackupRsync, self).__init__()
        self.backup_data = backup_data
        self.backup_result = {}
        self.parser = BackupParser()
    
    def prepare_command(self):
        destinationPath = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data['destPath']
        for backup_directory in self.backup_data['directories']:
            path = backup_directory['sourcePath'] + ' ' + destinationPath
            options = ' -a --no-i-r --info=progress --stats '
            if backup_directory['recursive']:
                options = options + ' -r '
            if backup_directory['preserveGroup']:
                options = options + ' -g '
            if backup_directory['preserveOwner']:
                options = options + ' -o '
            if backup_directory['preservePermissions']:
                options = options + ' -p '
            #if backup_directory['archive']:
            #    options = options + ' -a '
            if backup_directory['compress']:
                options = options + ' -z '
            if backup_directory['existingOnly']:
                options = options + ' --existing '
            if backup_directory['excludePattern']:
                options = options + ' --exclude "' + backup_directory['excludePattern'] + '" '
        backup_command = 'rsync ' + options + ' ' + path
        return backup_command
    
    def execute_command(self,cmd):
        try:
            command_process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE,stderr=subprocess.PIPE)
            self.parser.set_process = command_process
            Scope.get_instance().get_logger().info('Backup baslatiliyor.')
            while self.parser.resuming:
                output = command_process.stdout.readline()
                if output == '' or command_process.poll() is not None:
                    self.parser.resuming = False
                if output:
                    self.parser.parseLine(output)
            Scope.get_instance().get_logger().info('Backup bitti.')
        except Exception as e:
            Scope.get_instance().get_logger().info('hataya girdim')
            Scope.get_instance().get_logger().info(e)
            return str(e)
    
    def get_resource_total_size(self,sourcePath):
        return os.stat(sourcePath).st_size
    
    def is_file(self,source_path):
        return os.path.isfile(source_path)
    
    def append_command_execution_type(self,cmd):
        return 'sshpass -p ' + self.backup_data['password'] + ' ' + cmd + ' | stdbuf -oL tr "\\r" "\\n"'
        
    def backup(self):
        self.start_backup_watcher()
        self.start_backup()
        
    def prepare_backup(self):
        Scope.get_instance().get_logger().info('starting to backup')
        cmd = self.append_command_execution_type(self.prepare_command())
        Scope.get_instance().get_logger().info('command ok')
        
        Scope.get_instance().get_logger().info('watcher ok')
        Scope.get_instance().get_logger().info(cmd)
        self.execute_command(cmd)
        Scope.get_instance().get_logger().info('evet backup bittii')
        
        Scope.get_instance().get_logger().info(self.parser.percentage)
        Scope.get_instance().get_logger().info(self.parser.number_of_transfered_files)
        
    def start_backup(self):
        try:
            Scope.get_instance().get_logger().info('starting to watcher 4')
            t = threading.Thread(target=self.prepare_backup, args=())
            t.start()
            Scope.get_instance().get_logger().info('starting to watcher 5')
        except Exception as e:
            Scope.get_instance().get_logger().info(e)
            
    def backup_watcher(self):
        while self.parser.resuming:
            try:
                time.sleep(2)
                Scope.get_instance().get_logger().info(self.parser.percentage)
                Scope.get_instance().get_logger().info('Kalan zaman ' + self.parser.estimated_time) # Send this to lider server
            except Exception as e:
                print(e)
    
    def start_backup_watcher(self):
        try:
            Scope.get_instance().get_logger().info('starting to watcher')
            t = threading.Thread(target=self.backup_watcher, args=())
            t.start()
            Scope.get_instance().get_logger().info('starting to watcher 2')
        except Exception as e:
            print(e)
    
    
    