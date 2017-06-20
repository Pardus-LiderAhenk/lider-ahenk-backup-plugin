#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Seren Piri <seren.piri@agem.com.tr>

import sys
import os.path
# from pip.utils import backup_dir

# sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))

from backup_util import BackupUtil
from api.rsync import BackupRsync

def handle_task(task, context):
    backup = BackupRsync(task,context)
    backup.backup()
    #backup = BackupUtil(task, context, 'task')
    #backup.backup()