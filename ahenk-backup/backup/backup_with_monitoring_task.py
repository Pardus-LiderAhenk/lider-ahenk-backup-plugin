#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Seren Piri <seren.piri@agem.com.tr>

import os.path
import sys
from base.system.system import System

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))

from api.rsync import BackupRsync


def handle_task(task, context):
    if '{MAC}' in task['destPath']:
        task['destPath'] = task['destPath'].replace('{MAC}', str(System.Hardware.Network.mac_addresses()[0]))
    if '{IP_ADDRESS}' in task['destPath']:
        task['destPath'] = task['destPath'].replace('{IP_ADDRESS}', str(System.Hardware.ip_addresses()[0]))

    task['destPath'] = task['destPath']+'/'+task['sourcePath']

    backup = BackupRsync(task, context)
    backup.backup()
