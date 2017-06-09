#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: ismail BASARAN <ismail.basaran@tubitak.gov.tr> <basaran.ismaill@gmail.com>


import re, os
import subprocess,sys


class BackupParser():
    def __init__(self):
        super(BackupParser, self).__init__()
        self.percentage = None
        self.number_of_files = None
        self.number_of_transfered_files = None
        self.resuming = True
        self.estimated_time = 0
        
    
    def parseLine(self,line):
        if(b'Number of files' in line):
            total_file = re.findall(b'Number of files: (\d+)', line)
            if total_file:
                self.number_of_files = str(total_file[0].decode('utf-8'))
                self.percentage = str(100)
                self.estimated_time = '0:00:00'
                self.resuming = False
        elif(b'Number' in line and b'transferred' in line):
            transfferd_file_size = re.findall(b'Number of regular files transferred: (\d+)', line)
            if transfferd_file_size:
                self.number_of_transfered_files = str(transfferd_file_size[0].decode('utf-8'))
                self.percentage = str(100)
                self.estimated_time = '0:00:00'
                self.resuming = False
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
                self.percentage = str(lineAsArr[1].decode('utf-8')).replace('%','')
                self.estimated_time = str(lineAsArr[3].decode('utf-8'))