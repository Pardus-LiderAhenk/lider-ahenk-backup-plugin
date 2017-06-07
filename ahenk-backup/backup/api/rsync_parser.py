#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: ismail BASARAN <ismail.basaran@tubitak.gov.tr> <basaran.ismaill@gmail.com>


import re, os
import subprocess,sys


class BackupParser():
    def __init__(self, sourcePath):
        super(BackupParser, self).__init__()
        self.sourcePath = sourcePath
        self.percentage = None
        self.number_of_files = None
        self.number_of_transfered_files = None
        self.resuming = True
    
    def parseLine(self,line):
        if output == '' or process.poll() is not None:
            self.resuming = False
            break
        if(b'Number of files' in line):
            total_file = re.findall(b'Number of files: (\d+)', line)
            if total_file:
                self.number_of_files = str(total_file[0].decode('utf-8'))
        elif(b'Number' in line and b'transferred' in line):
            transfferd_file_size = re.findall(b'Number of regular files transferred: (\d+)', line)
            if transfferd_file_size:
                self.number_of_transfered_files = str(transfferd_file_size[0].decode('utf-8'))
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