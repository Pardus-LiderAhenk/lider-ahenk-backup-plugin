#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Seren Piri <seren.piri@agem.com.tr>

from plugins.backup.backup_util import BackupUtil

def handle_task(task, context):
    print('Backup Plugin Task')
    print('Task Data : {}'.format(str(task)))
    backup = BackupUtil(task, context, "task")
    backup.backup()