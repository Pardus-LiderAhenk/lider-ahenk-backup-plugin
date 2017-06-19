#!/usr/bin/python3
# -*- coding: utf-8 -*-

import subprocess,sys, os, threading, time, re
from requests.api import options
from pip.cmdoptions import process_dependency_links
import cmd
from base.scope import Scope
from base.plugin.abstract_plugin import AbstractPlugin

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))
from sys import stderr

class BackupParser():
    def __init__(self,logger):
        super(BackupParser, self).__init__()
        self.dry_run_status = True
        self.percentage = None
        self.number_of_files = None
        self.number_of_transfered_files = None
        self.resuming = True
        self.estimated_time = 0
        self.total_file_size = None
        self.total_transferred_file_size = None
        self.transferred_file_size=0
        self.logger = logger
        
        
    def parseLine(self,line):
        self.logger.info('line parse ediliyor')
        if(b'Number of files' in line):
            total_file = re.findall(b'Number of files: (\d+)', line)
            if total_file:
                self.number_of_files = str(total_file[0].decode('utf-8'))
                self.update_last_status()
        elif(b'Number of regular files transferred' in line):
            transfferd_file_size = re.findall(b'Number of regular files transferred: (\d+)', line)
            if transfferd_file_size:
                self.number_of_transfered_files = str(transfferd_file_size[0].decode('utf-8'))
                self.update_last_status()
        elif(b'Total file size' in line):
            file_size = re.findall(b'Total file size: (\d+)', line)
            if file_size:
                self.total_file_size = str(file_size[0].decode('utf-8'))
                self.update_last_status()
        elif(b'Total transferred file size' in line):
            transferred_file_size = re.findall(b'Total transferred file size: (\d+)', line)
            if transferred_file_size:
                self.total_transferred_file_size = str(transferred_file_size[0].decode('utf-8'))
                self.update_last_status()            
        elif(b'' == line):
            pass
        else:
            lineAsArr = None
            try:
                lineAsArr = line.split()
            except:
                pass
            if len(lineAsArr) > 1 and b'%' in line:
                #print(str(lineAsArr[1].decode('utf-8')).replace('%',''))
                self.transferred_file_size = str(lineAsArr[0].decode('utf-8'))
                self.percentage = str(lineAsArr[1].decode('utf-8')).replace('%','')
                self.estimated_time = str(lineAsArr[3].decode('utf-8'))
                self.logger.info(str(self.percentage))
                # Send message to lider
                
    def update_last_status(self):
        if not self.dry_run_status:
            self.percentage = str(100)
            self.estimated_time = '0:00:00'
            self.resuming = False

class BackupRsync(AbstractPlugin):
    def __init__(self, backup_data):
        super(BackupRsync, self).__init__()
        self.backup_data = backup_data
        self.backup_result = {}
        self.parser = BackupParser(self.logger)
    
    def prepare_command(self):
        destinationPath = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data['destPath']
        for backup_directory in self.backup_data['directories']:
            path = backup_directory['sourcePath'] + ' ' + destinationPath
            options = ' -a --no-i-r --info=progress2 --stats --no-h '
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
        self.logger.info(str(backup_command))
        return backup_command
    
    def dry_run(self):
        destinationPath = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data['destPath']
        for backup_directory in self.backup_data['directories']:
            path = backup_directory['sourcePath'] + ' ' + destinationPath
        options = ' -azn --stats --no-h '
        dry_run_backup_command = 'rsync ' + options + ' ' + path
        return dry_run_backup_command
        
    
    def execute_command(self,cmd):
        self.logger.info('backup command execute ediliyor')
        try:
            command_process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE,stderr=subprocess.PIPE)
            self.parser.set_process = command_process
            while self.parser.resuming:
                output = command_process.stdout.readline()
                if output == '' or command_process.poll() is not None:
                    self.parser.resuming = False
                    self.logger.info('bittiiii backup')
                if output:
                    self.parser.parseLine(output)
        except Exception as e:
            Scope.get_instance().get_logger().info(e)
            return str(e)
        
    def execute_dry_run(self,cmd):
        try:
            command_process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE,stderr=subprocess.PIPE)
            self.parser.set_process = command_process
            while self.parser.dry_run_status:
                output = command_process.stdout.readline()
                if output == '' or command_process.poll() is not None:
                    self.parser.dry_run_status = False
                if output:
                    self.parser.parseLine(output)
        except Exception as e:
            Scope.get_instance().get_logger().info(e)
            return str(e)
    
    def get_resource_total_size(self,sourcePath):
        return os.stat(sourcePath).st_size
    
    def is_file(self,source_path):
        return os.path.isfile(source_path)
    
    def append_command_execution_type(self,cmd):
        return 'sshpass -p ' + self.backup_data['password'] + ' ' + cmd + ' | stdbuf -oL tr "\\r" "\\n"'
        
    def backup(self):
        # Change status of parser and run dry run command for backup informations
        #self.parser.dry_run_status = True
        dry_run_cmd = self.append_command_execution_type(self.dry_run())
        #self.execute_dry_run(dry_run_cmd)
        #self.parser.dry_run_status = False
        
        #self.logger.info('Dry run ok.')
        #self.logger.info(self.parser.total_file_size)
        #self.logger.info(self.parser.total_transferred_file_size)
        #self.logger.info(self.parser.number_of_transfered_files)
        self.prepare_backup()
        #self.start_backup_watcher()
        #self.start_backup()
        
        self.logger.info('backup bittiiiiiii')
        
    def prepare_backup(self):
        cmd = self.append_command_execution_type(self.prepare_command())
        self.logger.info(cmd)
        self.execute_command(cmd)
        
    def start_backup(self):
        try:
            t = threading.Thread(target=self.prepare_backup, args=())
            t.start()
        except Exception as e:
            Scope.get_instance().get_logger().info(e)
            
    def backup_watcher(self):
        while self.parser.resuming:
            try:
                time.sleep(2)
                Scope.get_instance().get_logger().info(self.parser.percentage)
                Scope.get_instance().get_logger().info('Kalan zaman ' + self.parser.estimated_time) # Send this to lider server
                resp_message = '{"percentage":"' + self.parser.percentage + '"}' 
                self.context.create_response(code=self.message_code.TASK_PROCESSING.value, message=resp_message, content_type=self.content_type.APPLICATION_JSON.value)
            except Exception as e:
                print(e)
    
    def start_backup_watcher(self):
        try:
            t = threading.Thread(target=self.backup_watcher, args=())
            t.start()
        except Exception as e:
            print(e)
    
    
    